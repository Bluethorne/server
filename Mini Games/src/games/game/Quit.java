package games.game;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import games.buildBattle.BuildBattle;
import games.buildBattle.NoInvChange;
import games.buildBattle.menu.CopyAndPaste;
import games.buildBattle.menu.EntitySpawner;
import games.buildBattle.menu.OpenMenu;
import games.buildBattle.menu.Particles;
import games.flappyChicken.FlappyChicken;
import games.tribes.Tribes;
import games.main.Main;
import games.main.Pair;
import games.mind.Mind;
import games.skinMaker.SkinMaker;
import games.survivalGames.Enchanter;
import games.survivalGames.Stew;
import games.survivalGames.SurvivalGames;
import net.minecraft.server.v1_16_R3.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_16_R3.WorldBorder;
import net.minecraft.server.v1_16_R3.PacketPlayOutWorldBorder.EnumWorldBorderAction;

public class Quit implements Listener {
	
	@EventHandler
	public void quit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if(Game.playersPlaying.contains(player)) {
			Game.playersPlaying.remove(player);
			if(SurvivalGames.que.contains(player)) {
				SurvivalGames.que.remove(player);
			} else if(BuildBattle.que.contains(player)) {
				BuildBattle.que.remove(player);
			} else {
				Game game = Game.getGame(player);
				if(game != null) {
					game.players.remove(player);
					FileConfiguration config = Main.getPlugin(Main.class).getConfig();
					config.set("Left." + player.getUniqueId().toString(), game.uuid.toString());
					Main.getPlugin(Main.class).saveConfig();
					if(game instanceof BuildBattle) {
						if(game.players.size() <= 1) {
							ArrayList<Player> players = new ArrayList<Player>();
							for(Player p: game.players) {
								p.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "All the players have left the game. The game has ended.");
								players.add(p);
							}
							for(Player p: players) {
								Game.leave(p, false);
							}
							game.endGame();
						}
						OpenMenu.dark.remove(Pair.getPairFromKey(player, OpenMenu.dark).get(0));
						if(new File("plugins/miniGames/BuildBattle/Structures/" + player.getUniqueId().toString() + "/").exists()) {
							for(File file: new File("plugins/miniGames/BuildBattle/Structures/" + player.getUniqueId().toString() + "/").listFiles()) {
								file.delete();
							}
						}
						Main.getPlugin(Main.class).getConfig().set("Structures." + player.getUniqueId().toString(), null);
						Main.getPlugin(Main.class).saveConfig();
						BuildBattle buildbattle = (BuildBattle) game;
						Pair<Player, Inventory> remove2 = null;
						for(Pair<Player, Inventory> pair: OpenMenu.invs) {
							Player p = pair.getKey();
							if(p.equals(player)) {
								remove2 = pair;
								Pair<Player, ArrayList<Inventory>> remove3 = null;
								for(Pair<Player, ArrayList<Inventory>> pair2: CopyAndPaste.structures) {
									if(pair2.getKey().equals(player)) {
										remove3 = pair2;
									}
								}
								CopyAndPaste.structures.remove(remove3);
							}
						}
						if(remove2 != null) {
							OpenMenu.invs.remove(remove2);
						}
						if(buildbattle.voter != null) {
							NoInvChange.remove(player);
							buildbattle.voter.players.remove(player);
						} else {
							for(Entity entity: Pair.getValue(player, EntitySpawner.entitys)) {
								entity.remove();
							}
							EntitySpawner.entitys.removeAll(Pair.getPairFromKey(player, EntitySpawner.entitys));
							Particles.removeP(player);
							ArrayList<Pair<Integer, Integer>> plot = Pair.getValue(player, buildbattle.plots).get(0);
							ArrayList<Block> blocks = new ArrayList<Block>();
							for(Pair<Integer, Integer> pair: plot) {
								for(int y = 200; y < 232; y++) {
									Integer x = pair.getKey();
									Integer z = pair.getValue();
									Location location = new Location(Main.getPlugin(Main.class).getServer().getWorld("world"), x, y, z);
									Block block = location.getBlock();
									block.setType(Material.AIR);
									if(((x == Collections.min(Pair.getKeys(plot))) || (x == Collections.max(Pair.getKeys(plot)))) || ((y == 200) || (y == 232)) || ((z == Collections.min(Pair.getValues(plot))) || (z == Collections.max(Pair.getValues(plot))))) {
										blocks.add(block);
									}
								}
							}
							for(Protection protection: Protection.protections) {
								if(protection.blocks.containsAll(blocks)) {
									protection.blocks.removeAll(blocks);
								}
							}
							Pair<Player, ArrayList<Pair<Integer, Integer>>> remove = null;
							for(Pair<Player, ArrayList<Pair<Integer, Integer>>> pair: buildbattle.plots) {
								if(pair.getKey().equals(player)) {
									remove = pair;
								}
							}
							buildbattle.plots.remove(remove);
							for(StopSpawning stopSpawning: StopSpawning.stopspawnings) {
								if(stopSpawning.coords.containsAll(plot)) {
									stopSpawning.coords.removeAll(plot);
								}
							}
							for(Land land: Land.lands) {
								if(land.land.contains(plot)) {
									land.land.remove(plot);
								}
							}
							Land.map.removeAll(Pair.getKeys(plot));
						}
					} else if(game instanceof SurvivalGames) {
						Stew.removePlayer(player);
						SurvivalGames sg = (SurvivalGames) game;
						if(sg.noInteract.players.contains(player)) {
							sg.noInteract.players.remove(player);
						}
						sg.noMove.players.remove(player);
						sg.players.remove(player);
						sg.kills.players.remove(player);
						sg.kills.dead.remove(player);
						if(!sg.ended) {
							if(sg.kills.check()) {
								for(Player p : sg.players) {
									p.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "All the players have left the game. The game has ended.");
								}
							}
						}
						Enchanter.removePlayer(player);
					} else if(game instanceof SkinMaker) {
						SkinMaker.leaveGame(player);
						
					} else if(game instanceof Tribes) {
						Tribes.leaveGame(player);
					} else if(game instanceof FlappyChicken) {
						((FlappyChicken) game).end();
					}  else if(game instanceof Mind) {
						((Mind) game).end();
					}
				} else {
					GameChooser chooser = GameChooser.getChooser(player);
					if(chooser != null) {
						for(Player friend: chooser.reqPlayers) {
							friend.sendMessage(ChatColor.GOLD + player.getName() + " has quit and you cant play with them");
						}
						GameChooser.choosers.remove(GameChooser.getChooser(player));
					}
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@EventHandler
	public void join(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		FileConfiguration config = Main.getPlugin(Main.class).getConfig();
		if(config.getConfigurationSection("Left.") != null) {
			if(config.getConfigurationSection("Left.").getKeys(false).contains(player.getUniqueId().toString())) {
				UUID uuid = UUID.fromString(config.getString("Left." + player.getUniqueId().toString()));
				Game game = Game.getGame(uuid);
				if(game instanceof BuildBattle) {
					player.setGameMode(GameMode.ADVENTURE);
					player.removePotionEffect(PotionEffectType.NIGHT_VISION);
				} else if(game instanceof SurvivalGames) {
					WorldBorder border = new WorldBorder();
					border.setCenter(0, 0);
					border.setSize(60000000);
					border.world = ((CraftWorld) Main.getPlugin(Main.class).getServer().getWorld("world")).getHandle();
					PacketPlayOutWorldBorder packet = new PacketPlayOutWorldBorder(border, EnumWorldBorderAction.INITIALIZE);
					((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
					player.setFlying(false);
					player.setAllowFlight(false);
					player.removePotionEffect(PotionEffectType.INVISIBILITY);
				} else if(game instanceof Tribes) {
					
				} else if(game instanceof SkinMaker) {
					player.removePotionEffect(PotionEffectType.NIGHT_VISION);
				}
				player.getInventory().clear();
				Location location = Location.deserialize((Map<String, Object>) config.get("Players." + player.getUniqueId() + ".Location"));
				player.teleport(location);
				ItemStack[] items = ((List<ItemStack>) config.getList("Players." + player.getUniqueId() + ".Inventory.contents")).toArray(new ItemStack[0]);
				player.getInventory().setContents(items);
				Integer slot = config.getInt("Players." + player.getUniqueId() + ".Inventory.slot");
				player.getInventory().setHeldItemSlot(slot);
				player.setLevel(config.getInt("Players." + player.getUniqueId() + ".XP.level"));
				player.setExp(config.getInt("Players." + player.getUniqueId() + ".XP.xp"));
				player.setHealth(config.getInt("Players." + player.getUniqueId() + ".Health"));
				player.setFoodLevel(config.getInt("Players." + player.getUniqueId() + ".Hunger"));
				config.set("Players." + player, null);
				config.set("Left." + player.getUniqueId().toString(), null);
				Main.getPlugin(Main.class).saveConfig();
			}
		}
	}
}
