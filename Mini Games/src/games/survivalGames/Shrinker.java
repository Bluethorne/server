package games.survivalGames;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import games.main.Main;
import games.main.Pair;
import games.main.Scoreboards;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_16_R3.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_16_R3.WorldBorder;
import net.minecraft.server.v1_16_R3.PacketPlayOutWorldBorder.EnumWorldBorderAction;

public class Shrinker {
	
	public boolean cancel = false;
	SurvivalGames game;
	WorldBorder border;
	float size = 100;
	double midX;
	double midZ;
	boolean shrinking = false;
	
	public Shrinker(SurvivalGames game, WorldBorder border) {
		this.game = game;
		this.border = border;
		newSize(200);
		damage();
		midX = border.getCenterX();
		midZ = border.getCenterZ();
	}
	
	void newSize(int currentSize) {
		new BukkitRunnable() {

			@Override
			public void run() {
				if(cancel != true) {
					int newSize = currentSize / 4 * 3;
					if(newSize > 1) {
						shrinking = true;
						shrink(currentSize, newSize, ((float) (currentSize - newSize) / 600));
						ArrayList<Player> players = game.players;
						String supply;
						if(game.supply.x1 != null && game.supply.z1 != null) {
							supply = "X: " + game.supply.x1 + " Z: " + game.supply.z1;
						} else {
							supply = "No supply drop";
						}
						for(Player player: players) {
							player.sendMessage(ChatColor.YELLOW + "Border shrinking");
							int kills;
							if(Pair.getValue(((Player) player), game.kills.kills).size() == 0) {
								kills = 0;
							} else {
								kills = Pair.getValue(((Player) player), game.kills.kills).get(0);
							}
							Scoreboards.addScoreboardSurvivalGames(player, kills, supply, shrinking);
						}
						newSize(newSize);
					} else {
						cancel = true;
					}
				}
			}
			
		}.runTaskLater(Main.getPlugin(Main.class), 1200);
	}
	
	void shrink(float currentSize, int finalSize, float jump) {
		new BukkitRunnable() {

			@Override
			public void run() {
				ArrayList<Player> players = game.players;
				if(cancel != true) {
					float newSize = currentSize - jump;
					size = newSize / 2;
					border.setSize(newSize);
					PacketPlayOutWorldBorder packet = new PacketPlayOutWorldBorder(border, EnumWorldBorderAction.LERP_SIZE);
					for(Player player: players) {
						((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
					}
					if(finalSize <= newSize) {
						shrink(newSize, finalSize, jump);
					} else {
						shrinking = false;
						String supply;
						if(game.supply.x1 != null && game.supply.z1 != null) {
							supply = "X: " + game.supply.x1 + " Z: " + game.supply.z1;
						} else {
							supply = "No supply drop";
						}
						for(Player player: players) {
							int kills;
							if(Pair.getValue(((Player) player), game.kills.kills).size() == 0) {
								kills = 0;
							} else {
								kills = Pair.getValue(((Player) player), game.kills.kills).get(0);
							}
							Scoreboards.addScoreboardSurvivalGames(player, kills, supply, shrinking);
						}
					}
				}
			}
			
		}.runTaskLater(Main.getPlugin(Main.class), 1);
	}
	
	void damage() {
		new BukkitRunnable() {

			@Override
			public void run() {
				if(!cancel) {
					for(Player player: game.players) {
						Location location = player.getLocation();
						if(!game.kills.dead.contains(player)) {
							if((location.getX() > size + midX) || (location.getX() < midX - size) || (location.getZ() > midZ + size) || (location.getZ() < midZ - size)) {
								player.damage(1);
							}
						} else {
							if(location.getX() > size + midX) {
								location.setX(size + midX);
								player.teleport(location);
							}
							if(location.getX() < midX - size) {
								location.setX(midX - size);
								player.teleport(location);
							}
							if(location.getZ() > midZ + size) {
								location.setZ(midZ + size);
								player.teleport(location);
							}
							if(location.getZ() < midZ - size) {
								location.setZ(midZ - size);
								player.teleport(location);
							}
						}
					}
					damage();
				}
			}
			
		}.runTaskLater(Main.getPlugin(Main.class), 10);
	}
	
}
