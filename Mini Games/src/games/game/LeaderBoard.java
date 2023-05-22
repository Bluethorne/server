package games.game;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import games.buildBattle.BuildBattle;
import games.game.PlayerWinEvent;
import games.main.DataBase;
import games.main.Main;
import games.main.Pair;
import games.survivalGames.SurvivalGames;
import net.minecraft.server.v1_16_R3.EntityArmorStand;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.WorldServer;
import net.minecraft.server.v1_16_R3.IChatBaseComponent.ChatSerializer;

public class LeaderBoard implements Listener {
	
	public static ArrayList<ArmorStand> stands = new ArrayList<ArmorStand>();
	static String[] games = {"SurvivalGames", "BuildBattle"};
	static Location[] locations = {new Location(Main.getPlugin(Main.class).getServer().getWorld("world"), -12.5, 127, -138.5, 0, 0), new Location(Main.getPlugin(Main.class).getServer().getWorld("world"), -15.5, 127, -137.5, 0, 0), new Location(Main.getPlugin(Main.class).getServer().getWorld("world"), -9.5, 127, -137.5), new Location(Main.getPlugin(Main.class).getServer().getWorld("world"), -12.5, 127, -119.5, 0, 0), new Location(Main.getPlugin(Main.class).getServer().getWorld("world"), -9.5, 127, -120.5, 0, 0), new Location(Main.getPlugin(Main.class).getServer().getWorld("world"), -15.5, 127, -120.5, 0, 0)};
	
	public static void tpLeaderBoard(Player player) {
		new BukkitRunnable() {

			@Override
			public void run() {
				LeaderBoard.updateLeaderBoard();
				player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false, false));
				player.teleport(new Location(Main.getPlugin(Main.class).getServer().getWorld("world"), -12.5, 125, -129.5, (float) 90, (float) 0));
			}
			
		}.runTask(Main.getPlugin(Main.class));
	}
	
	public static void updateLeaderBoard() {
		if(!stands.isEmpty()) {
			for(ArmorStand stand: stands) {
				stand.remove();
			}
			stands.clear();
		}
		int n = 0;
		for(String nameOfGame: games) {
			try {
				Statement statement1 = DataBase.connection().createStatement();
				ResultSet columns = statement1.executeQuery("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '" + nameOfGame + "'");
				while(columns.next()) {
					String column = columns.getNString(1);
					if(!column.equals("UUID") && !(column.equals("Played") && nameOfGame.equals("SurvivalGames"))) {
						Location gameLocation = locations[n];
						n++;
						if(column.equals("Wins")) {
							stands.add(getStand(ChatColor.DARK_RED + "" + ChatColor.BOLD + nameOfGame, new Location(gameLocation.getWorld(), gameLocation.getX(), gameLocation.getY() + 3, gameLocation.getZ())));
						}
						stands.add(getStand(ChatColor.RED + "" + ChatColor.BOLD + column, new Location(gameLocation.getWorld(), gameLocation.getX(), gameLocation.getY() + 2.6, gameLocation.getZ())));
						Statement statement2 = DataBase.connection().createStatement();
						ResultSet most = statement2.executeQuery("SELECT UUID, " + column + " FROM " + nameOfGame + " ORDER BY " + column + " DESC LIMIT 10");
						double n2 = -0.4;
						while(most.next()) {
							int score = most.getInt(column);
							int num = (int) Math.round(n2 * -10 / 4);
							String playerName = Main.getPlugin(Main.class).getServer().getOfflinePlayer(UUID.fromString(most.getString("UUID"))).getName();
							stands.add(getStand(ChatColor.GOLD + "" + num + ". " + ChatColor.YELLOW + playerName + " : " + score, new Location(gameLocation.getWorld(), gameLocation.getX(), gameLocation.getY() + 2.6 + n2, gameLocation.getZ())));
							n2-=0.4;	
						}
						most.close();
						statement2.close();
					}	
				}
				columns.close();
				statement1.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	static ArmorStand getStand(String name, Location location) {
		WorldServer world = ((CraftWorld) Main.getPlugin(Main.class).getServer().getWorld("world")).getHandle();
		EntityArmorStand stand = new EntityArmorStand(EntityTypes.ARMOR_STAND, world);
		stand.setLocation(location.getX(), location.getY(), location.getZ(), 0, 0);
		stand.setCustomName(ChatSerializer.a("{\"text\":\"" + name + "\"}"));
		stand.setInvisible(true);
		stand.setInvulnerable(true);
		stand.setNoGravity(true);
		stand.setCustomNameVisible(true);
		world.addEntity(stand);
		return (ArmorStand) stand.getBukkitEntity();
	} 
	
	@EventHandler
	public void win(PlayerWinEvent event) {
		Game game = event.getGame();
		for(Pair<Player, Integer> pair: event.getScores()) {
			Player player = pair.getKey();
			Integer score = pair.getValue();
			updateScore(player.getUniqueId(), score, game);
			if(!pair.getKey().equals(event.getWinner())) {
				addDeath(player.getUniqueId(), game);
			}
		}
		addWin(event.getWinner().getUniqueId(), game);
	}
	
	static void updateScore(UUID uuid, int score, Game game) {
		String orderName = "";
		if(game instanceof BuildBattle) {
			orderName = "TotalScore";
		} else if(game instanceof SurvivalGames) {
			orderName = "Kills";
		}
		try {
			Statement statement = DataBase.connection().createStatement();
			ResultSet result = statement.executeQuery("SELECT Played, " + orderName + " FROM " + game.getClass().getSimpleName() + " WHERE UUID = '" + uuid + "';");
			if(result.first()) {
				int played = result.getInt("Played");
				int totalScore = result.getInt(orderName);
				played++;
				totalScore = totalScore + score;
				statement.executeUpdate("UPDATE " + game.getClass().getSimpleName() + " SET Played = " + played + ", " + orderName + " = " + totalScore + " WHERE UUID = '" + uuid + "';");
			} else {
				if(game instanceof BuildBattle) {
					statement.executeUpdate("INSERT INTO " + game.getClass().getSimpleName() + " (UUID, Wins, Played, " + orderName + ") VALUES ('" + uuid + "', 0, 1, " + score + ");");
				} else {
					statement.executeUpdate("INSERT INTO " + game.getClass().getSimpleName() + " (UUID, Wins, Played, " + orderName + ", Deaths) VALUES ('" + uuid + "', 0, 1, " + score + ", 0);");
				}
			}
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	static void addWin(UUID uuid, Game game) {
		try {
			Statement statement = DataBase.connection().createStatement();
			ResultSet result = statement.executeQuery("SELECT Wins FROM " + game.getClass().getSimpleName() + " WHERE UUID = '" + uuid + "';");
			result.next();
			int wins = result.getInt("Wins");
			wins++;
			statement.executeUpdate("UPDATE " + game.getClass().getSimpleName() + " SET Wins = " + wins + " WHERE UUID = '" + uuid + "';");
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	static void addDeath(UUID uuid, Game game) {
		if(!(game instanceof BuildBattle)) {
			try {
				Statement statement = DataBase.connection().createStatement();
				ResultSet result = statement.executeQuery("SELECT Deaths FROM " + game.getClass().getSimpleName() + " WHERE UUID = '" + uuid + "';");
				result.next();
				int death = result.getInt("Deaths");
				death++;
				statement.executeUpdate("UPDATE " + game.getClass().getSimpleName() + " SET Deaths = " + death + " WHERE UUID = '" + uuid + "';");
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
