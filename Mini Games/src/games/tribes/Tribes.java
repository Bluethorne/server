package games.tribes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import games.game.Game;
import games.main.DataBase;
import games.main.Main;
import games.main.Rank;
import games.tribes.CustomEntity.CustomEntitys;
import net.minecraft.server.v1_16_R3.ChatMessageType;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_16_R3.PacketPlayOutChat;

public class Tribes extends Game {
	
	public static final UUID gameUUID = UUID.randomUUID();
	public static ArrayList<Player> players = new ArrayList<Player>();
	public static final World world = Main.getPlugin(Main.class).getServer().getWorld("Tribes");
	public static int season = ((int) (world.getFullTime() / 219000)) % 4;
	public static Spawner spawner = new Spawner();
	public static ArrayList<Player> waiters = new ArrayList<Player>();
	
	public Tribes() {
		super(new ArrayList<Player>());	
	}
	
	public static void join(Player player) {
		players.add(player);
		save(player);
		String rank = Rank.getRank(player);
		if(rank.equals("§4§lOwner") || rank.equals("§c§lAdmin")  || rank.equals("§4§lLeader")) {
			new BukkitRunnable() {

				@Override
				public void run() {
					player.setGameMode(GameMode.ADVENTURE);
					player.getInventory().clear();
					try {
						ResultSet result = DataBase.connection().createStatement().executeQuery("SELECT * FROM Tribes WHERE UUID = '" + player.getUniqueId() + "'");
						if(!result.next()) {
							DataBase.connection().createStatement().executeUpdate("INSERT INTO Tribes (UUID) VALUES ('" + player.getUniqueId() + "')");
							chooseTribe(player);
						} else {
							String tribe = result.getString("tribe");
							if(tribe == null) {
								chooseTribe(player);
							}
						}
						player.teleport(new Location(world, 0, world.getHighestBlockYAt(0, 0), 0));	
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}	
			}.runTaskLater(Main.getPlugin(Main.class), 1);
		} else {
			player.sendMessage(ChatColor.RED + "This game is still in development");
			leave(player, false);
		}
	}
	
	public static void leaveGame(Player player) {
		Tribes.players.remove(player);
	}
	
	private static void chooseTribe(Player player) {
		waiters.add(player);
		player.sendMessage(ChatColor.GOLD + "Click to choose a tribe:");
		for(CustomEntitys e: CustomEntitys.values()) {
			String name = e.name().replace("_", " ");
			IChatBaseComponent comp = ChatSerializer.a("{\"text\":\"" + ChatColor.YELLOW + name.replaceAll("\\b(\\w)(\\w*)\\b", "$1") + name.replaceAll("\\b(\\w)(\\w*)\\b", "$2").toLowerCase() + " tribe\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/tribe " + e.name() + "\"}}");
			PacketPlayOutChat packet = new PacketPlayOutChat(comp, ChatMessageType.CHAT, player.getUniqueId());
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		}
	}
	
	public static void chooseTribe(Player player, String tribe) {
		if(waiters.contains(player)) {
			CustomEntitys entity = CustomEntitys.valueOf(tribe);
			if(entity == null) {
				player.sendMessage(ChatColor.RED + "This is not an entity");
			} else {
				
			}
		} else {
			player.sendMessage(ChatColor.RED + "You cannot do this");
		}
	}
}
