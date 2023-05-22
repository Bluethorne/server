package games.main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.WorldCreator;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import games.buildBattle.NoInvChange;
import games.buildBattle.TNTAndPistonStoping;
import games.buildBattle.menu.CopyAndPaste;
import games.buildBattle.menu.EntitySpawner;
import games.buildBattle.menu.GiveItem;
import games.buildBattle.menu.OpenMenu;
import games.game.LeaderBoard;
import games.game.Compass;
import games.game.Game;
import games.game.GameChooser;
import games.game.Quit;
import games.survivalGames.Enchanter;
import games.survivalGames.Stew;
import games.survivalGames.SurvivalGames;
import games.survivalGames.TNT;
import games.tribes.Tribes;

public class Main extends JavaPlugin implements Listener{
	
	private Commands commands = new Commands();
	public static ItemStack buildbattle_menu;
	
	public void onEnable() {LeaderBoard.updateLeaderBoard();
		Plugin plugin = Main.getPlugin(Main.class);
		System.out.println(ChatColor.GREEN + "Plugin enabled");
		
		getCommand("play").setExecutor(commands);
		getCommand("leave").setExecutor(commands);
		getCommand("playwith").setExecutor(commands);
		getCommand("stats").setExecutor(commands);
		getCommand("rank").setExecutor(commands);
		getCommand("respond").setExecutor(commands);
		getCommand("hub").setExecutor(commands);
		getCommand("world").setExecutor(commands);
		getCommand("skin").setExecutor(commands);
		getCommand("help").setExecutor(commands);
		
		getServer().getPluginManager().registerEvents(new Quit(), plugin);
		getServer().getPluginManager().registerEvents(new NoInvChange(), plugin);
		getServer().getPluginManager().registerEvents(new GiveItem(), plugin);
		getServer().getPluginManager().registerEvents(new OpenMenu(), plugin);
		getServer().getPluginManager().registerEvents(new TNTAndPistonStoping(), plugin);
		getServer().getPluginManager().registerEvents(new CopyAndPaste(), plugin);
		getServer().getPluginManager().registerEvents(new LeaderBoard(), plugin);
		getServer().getPluginManager().registerEvents(new EntitySpawner(), plugin);
		getServer().getPluginManager().registerEvents(new Stew(), plugin);
		getServer().getPluginManager().registerEvents(new Enchanter(), plugin);
		getServer().getPluginManager().registerEvents(new TNT(), plugin);
		getServer().getPluginManager().registerEvents(new Scoreboards(), plugin);
		getServer().getPluginManager().registerEvents(new HubNPC(), plugin);
		getServer().getPluginManager().registerEvents(new PacketListener(), plugin);
		getServer().getPluginManager().registerEvents(new JoinAndLeave(), plugin);
		getServer().getPluginManager().registerEvents(new Chat(), plugin);
		getServer().getPluginManager().registerEvents(new HubProtection(), plugin);
		getServer().getPluginManager().registerEvents(new Compass(), plugin);
		getServer().getPluginManager().registerEvents(new Stats(), plugin);
		getServer().getPluginManager().registerEvents(new GUIKick(), plugin);
		
		GameChooser.loadInventory();
		
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		buildbattle_menu = new ItemStack(Material.NETHER_STAR);
		ItemMeta meta = buildbattle_menu.getItemMeta();
		meta.setDisplayName(ChatColor.BLUE + "Menu");
		buildbattle_menu.setItemMeta(meta);
		
		OpenMenu.loadExtraInvs();
		
		for(Player player: getServer().getOnlinePlayers()) {
			Scoreboards.addScoreboardJoin(player);
		}
		
		try {
			Statement statement = DataBase.connection().createStatement();
			ResultSet result = statement.executeQuery("SELECT UUID, Rank FROM Hub");
			while(result.next()) {
				for(Player player: getServer().getOnlinePlayers()) {
					Scoreboard scoreboard = player.getScoreboard();
					String rank = result.getString("Rank");
					if(scoreboard.getTeam(rank) == null) {
						scoreboard.registerNewTeam(rank);
					}
					Team team = scoreboard.getTeam(rank);
					team.setPrefix(rank + " ");
					OfflinePlayer oplayer = Main.getPlugin(Main.class).getServer().getOfflinePlayer(UUID.fromString(result.getString("UUID")));
					if(oplayer.getName() != null) {
						team.addEntry(oplayer.getName());
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		WorldCreator creator = new WorldCreator("Tribes");
		creator.createWorld();
		
		new Tribes();
		
		SurvivalGames.loadChests();
	}
	
	public void onDisable() {
		System.out.println(ChatColor.RED + "Plugin disabled");
		for(Player player: Main.getPlugin(Main.class).getServer().getOnlinePlayers()) {
			if(Game.playersPlaying.contains(player)) {
				Game.leave(player, false);
			}
		}
		for(ArmorStand stand: LeaderBoard.stands) {
			stand.remove();
		}
		LeaderBoard.stands.clear();
	}
}
