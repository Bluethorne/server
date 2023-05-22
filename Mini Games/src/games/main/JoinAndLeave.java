package games.main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import games.flappyChicken.FlappyChicken;
import games.flappyChicken.Pillar;
import games.game.Compass;
import games.skinMaker.SkinMaker;
import net.minecraft.server.v1_16_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_16_R3.PacketPlayOutTitle.EnumTitleAction;

public class JoinAndLeave implements Listener {
	
	@SuppressWarnings("serial")
	static HashMap<String, Integer> order = new HashMap<String, Integer>() {{
		put("§4§lOwner ", 1);
		put("§4§lLeader ", 2);
		put("§c§lAdmin ", 3);
		put("§9§lBuilder ", 4);
		put("§9§lDevoted ", 5);
	}};
	
	@EventHandler
	public void join(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		player.setPlayerListHeader(ChatColor.GREEN + "" + ChatColor.BOLD + "Thornecraft Network");
		player.setPlayerListFooter(ChatColor.GREEN + "" + ChatColor.BOLD + "IP - " + ChatColor.GRAY + "" + ChatColor.BOLD + "89.34.96.16:25592");
		ArrayList<ItemStack> items = new ArrayList<ItemStack>(Arrays.asList(player.getInventory().getContents()));
		boolean found = false;
		for(ItemStack item: items) {
			if(item != null) {
				if(item.getItemMeta().getDisplayName().equals(Compass.name)) {
					found = true;
				}
			}
		}
		if(!found) {
			player.getInventory().addItem(Compass.compass);
		}
		event.setJoinMessage(ChatColor.DARK_GRAY + "Join> " + ChatColor.GRAY + player.getName());
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{\"text\":\"§a§lWelcome, " + player.getName() + "!\"}")));
		if(!player.hasPlayedBefore()) {
			player.setGameMode(GameMode.ADVENTURE);
		}
		String rank = "";
		try {
			Statement statement = DataBase.connection().createStatement();
			ResultSet result = statement.executeQuery("SELECT Rank FROM Hub WHERE UUID ='" + player.getUniqueId() + "'");
			if(result.next()) {
				rank = result.getString("Rank") + " ";
			} else {
				statement.executeUpdate("INSERT INTO Hub (UUID, Rank, SetSkin) VALUES ('" + player.getUniqueId() + "', '', false)");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		player.setDisplayName(rank + "§e" + player.getName());
		if(!rank.equals("§4§lOwner ") && !rank.equals("§c§lAdmin ")) {
			player.teleport(new Location(Main.getPlugin(Main.class).getServer().getWorld("world"), 30, 122, -80));
			player.setGameMode(GameMode.ADVENTURE);
		}
		if(!rank.equals("")) {
			for(Player p: Main.getPlugin(Main.class).getServer().getOnlinePlayers()) {
				Scoreboard scoreboard = p.getScoreboard();
				int num = order.keySet().size() + 1;
				if(order.containsKey(rank)) {
					num = order.get(rank);
				}
				if (scoreboard.getTeam(num + rank) == null) {
					scoreboard.registerNewTeam(num + rank);
				}
				Team team = scoreboard.getTeam(num + rank);
				team.setPrefix(rank);
				team.addEntry(player.getName());
			}
		}
		Scoreboard scoreboard = player.getScoreboard();
		try {
			Statement statement = DataBase.connection().createStatement();
			ResultSet result = statement.executeQuery("SELECT UUID, Rank FROM Hub");
			while(result.next()) {
				String joined = result.getString("Rank");
				if(!joined.equals("")) {
					joined += " ";
					int num = order.keySet().size() + 1;
					if(order.containsKey(joined)) {
						num = order.get(joined);
					}
					if(scoreboard.getTeam(num + joined) == null) {
						scoreboard.registerNewTeam(num + joined);
					}
					Team team = scoreboard.getTeam(num + joined);
					team.setPrefix(joined);
					OfflinePlayer oplayer =	Main.getPlugin(Main.class).getServer().getOfflinePlayer(UUID.fromString(result.getString("UUID")));
					if(oplayer.hasPlayedBefore()) {
						team.addEntry(oplayer.getName());
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Questionairre.check(player);
		SkinMaker.hideNewJoin(player);
		changeSkin(player);
		for(FlappyChicken chicken: FlappyChicken.games) {
			chicken.chicken.sendKillPacket(player);
			chicken.cloud.sendKillPacket(player);
			for(Pillar pillar: chicken.pillars) {
				pillar.sendKillPackets(player);
			}
		}
	}
	
	@EventHandler
	public void leave(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		e.setQuitMessage(ChatColor.DARK_GRAY + "Quit> " + ChatColor.GRAY + player.getName());
	}
	
	public static void changeSkin(Player player) {
		try {
			ResultSet result = DataBase.connection().createStatement().executeQuery("SELECT texture, signature FROM Skins, Hub WHERE SetSkin = true AND Hub.UUID = '" + player.getUniqueId() + "' AND Hub.skinID = Skins.skinID");
			if(result.next()) {
				if(result.getString("texture") != null && result.getString("signature") != null) {
					EntityPlayer nmsp = ((CraftPlayer) player).getHandle();
					GameProfile profile = nmsp.getProfile();
					profile.getProperties().clear();
					profile.getProperties().put("textures", new Property("textures", result.getString("texture"), result.getString("signature")));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
