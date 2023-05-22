package games.game;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;

import games.buildBattle.BuildBattle;
import games.buildBattle.NoInvChange;
import games.buildBattle.menu.CopyAndPaste;
import games.buildBattle.menu.EntitySpawner;
import games.buildBattle.menu.OpenMenu;
import games.buildBattle.menu.Particles;
import games.buildBattle.menu.Sounds;
import games.flappyChicken.FlappyChicken;
import games.tribes.Tribes;
import games.main.DataBase;
import games.main.Main;
import games.main.HubNPC;
import games.main.Pair;
import games.main.Scoreboards;
import games.mind.Mind;
import games.skinMaker.SkinMaker;
import games.survivalGames.Enchanter;
import games.survivalGames.Stew;
import games.survivalGames.SurvivalGames;
import net.minecraft.server.v1_16_R3.ChatMessageType;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_16_R3.PacketPlayOutWorldBorder.EnumWorldBorderAction;
import net.minecraft.server.v1_16_R3.PacketPlayOutChat;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_16_R3.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_16_R3.WorldBorder;

public class Game {
	
	public static ArrayList<Player> playersPlaying = new ArrayList<Player>();
	public static ArrayList<Game> games = new ArrayList<Game>();
	public ArrayList<Player> players = new ArrayList<Player>();
	public UUID uuid = UUID.randomUUID();
	
	public static void leave(Player player, boolean print) {
		Game.playersPlaying.remove(player);
		if(BuildBattle.que.contains(player)) {
			BuildBattle.que.remove(player);
			if(print) {
				player.sendMessage(ChatColor.GREEN + "You have left your queue");
			}
		} else if(SurvivalGames.que.contains(player)) {
			SurvivalGames.que.remove(player);
			if(print) {
				player.sendMessage(ChatColor.GREEN + "You have left your queue");
			}
		} else {
			Game game = getGame(player);
			if(game != null) {
				game.players.remove(player);
				if(print) {
					player.sendMessage(ChatColor.GREEN + "You have left your game");
				}
				if(game instanceof BuildBattle) {
					if(game.players.size() <= 1) {
						ArrayList<Player> players = new ArrayList<Player>();
						for(Player p: game.players) {
							p.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "All the players have left the game. The game has ended.");
							players.add(p);
						}
						for(Player p: players) {
							leave(p, false);
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
						Sounds.removeP(player);
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
					buildbattle.players.remove(player);
					player.setGameMode(GameMode.ADVENTURE);
					player.removePotionEffect(PotionEffectType.NIGHT_VISION);
				} else if(game instanceof SurvivalGames) {
					Stew.removePlayer(player);
					SurvivalGames sg = (SurvivalGames) game;
					sg.noInteract.players.remove(player);
					sg.players.remove(player);
					sg.kills.players.remove(player);
					if(sg.kills.dead.contains(player)) {
						sg.kills.dead.remove(player);
					}
					if(!sg.ended) {
						if(sg.kills.check()) {
							for(Player p : sg.players) {
								p.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "All the players have left the game. The game has ended.");
							}
						}
					}
					Enchanter.removePlayer(player);
					WorldBorder border = new WorldBorder();
					border.setCenter(0, 0);
					border.setSize(60000000);
					border.world = ((CraftWorld) Main.getPlugin(Main.class).getServer().getWorld("world")).getHandle();
					PacketPlayOutWorldBorder packet = new PacketPlayOutWorldBorder(border, EnumWorldBorderAction.INITIALIZE);
					((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
					player.setFlying(false);
					player.setAllowFlight(false);
					player.removePotionEffect(PotionEffectType.INVISIBILITY);
					EntityPlayer nmsplayer = ((CraftPlayer) player).getHandle();
					for(EntityPlayer nmsdead: sg.kills.fake) {
						PacketPlayOutEntityDestroy p1 = new PacketPlayOutEntityDestroy(nmsdead.getId());
						nmsplayer.playerConnection.sendPacket(p1);
						PacketPlayOutPlayerInfo p2 = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, nmsdead);
						nmsplayer.playerConnection.sendPacket(p2);
					}
				} else if(game instanceof SkinMaker) {
					SkinMaker.leaveGame(player);
				} else if(game instanceof Tribes) {
					Tribes.leaveGame(player);
				} else if(game instanceof FlappyChicken) {
					((FlappyChicken) game).end();
				} else if(game instanceof Mind) {
					((Mind) game).end();
				}
				player.getInventory().clear();
				FileConfiguration config = Main.getPlugin(Main.class).getConfig();
				@SuppressWarnings("unchecked")
				Location location = Location.deserialize((Map<String, Object>) config.get("Players." + player.getUniqueId() + ".Location"));
				player.teleport(location);
				@SuppressWarnings("unchecked")
				ItemStack[] items = ((List<ItemStack>) config.getList("Players." + player.getUniqueId() + ".Inventory.contents")).toArray(new ItemStack[0]);
				player.getInventory().setContents(items);
				Integer slot = config.getInt("Players." + player.getUniqueId() + ".Inventory.slot");
				player.getInventory().setHeldItemSlot(slot);
				player.setLevel(config.getInt("Players." + player.getUniqueId() + ".XP.level"));
				player.setExp(config.getInt("Players." + player.getUniqueId() + ".XP.xp"));
				player.setHealth(config.getInt("Players." + player.getUniqueId() + ".Health"));
				player.setFoodLevel(config.getInt("Players." + player.getUniqueId() + ".Hunger"));
				config.set("Players." + player, null);
				Main.getPlugin(Main.class).saveConfig();
				Scoreboards.addScoreboardJoin(player);
				if(!(game instanceof SkinMaker || game instanceof FlappyChicken)) {
					HubNPC.spawnNPCs(player);
				}
			} else {
				GameChooser chooser = GameChooser.getChooser(player);
				if(chooser != null) {
					for(Player friend: chooser.reqPlayers) {
						if(print) {
							friend.sendMessage(ChatColor.GOLD + player.getName() + " has left and you cant play with them any more");
						}
					}
					GameChooser.choosers.remove(GameChooser.getChooser(player));
					if(print) {
						player.sendMessage(ChatColor.GREEN + "You have left waiting for your friends");
					}
				}
			}		
		}
	}
	
	public Game(ArrayList<Player> players) {
		this.players = players;
		games.add(this);
		for(Player player: this.players) {
			player.sendMessage(ChatColor.GOLD + "Your game has started");
			save(player);
		}
	}
	
	public static Game getGame(Player player) {
		for(Game game: games) {
			if(game.players.contains(player)) {
				return game;
			}
		}
		return null;
	}
	
	public static void save(Player player) {
		PlayerInventory inventory = player.getInventory();
		Location location = player.getLocation();
		FileConfiguration config = Main.getPlugin(Main.class).getConfig();
		config.set("Players." + player.getUniqueId() + ".Location", location.serialize());
		config.set("Players." + player.getUniqueId() + ".Inventory.contents", new ArrayList<ItemStack>(Arrays.asList(inventory.getContents())));
		config.set("Players." + player.getUniqueId() + ".Inventory.slot", inventory.getHeldItemSlot());
		config.set("Players." + player.getUniqueId() + ".XP.xp", player.getExp());
		config.set("Players." + player.getUniqueId() + ".XP.level", player.getLevel());
		config.set("Players." + player.getUniqueId() + ".Health", player.getHealth());
		config.set("Players." + player.getUniqueId() + ".Hunger", player.getFoodLevel());
		Main.getPlugin(Main.class).saveConfig();
	}
	
	public void endGame() {
		String nameOfGame = this.getClass().getSimpleName();
		ArrayList<String> lines = new ArrayList<String>();
		String string = "";
		try {
			Statement statement1 = DataBase.connection().createStatement();
			ResultSet columns = statement1.executeQuery("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '" + nameOfGame + "'");
			while(columns.next()) {
				String column = columns.getNString(1);
				if(!column.equals("UUID")) {
					Statement statement2 = DataBase.connection().createStatement();
					ResultSet most = statement2.executeQuery("SELECT UUID, " + column + " FROM " + nameOfGame + " ORDER BY " + column + " DESC LIMIT 10");		
					lines.add("Leaderboard for the most " + column + ":");
					while(most.next()) {
						String playerName = Main.getPlugin(Main.class).getServer().getOfflinePlayer(UUID.fromString(most.getString("UUID"))).getName();		
						try {
							lines.add(most.getRow() + ". " + playerName + " : " + most.getInt(column));
						} catch(IndexOutOfBoundsException e) {}	
					}
					most.close();
					statement2.close();	
					lines.add("");
				}
			}
			columns.close();
			statement1.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for(String line: lines) {
			string = string + line;
			if(lines.indexOf(line) != lines.size() - 1) {
				string = string + "\n";
			}
		}
		IChatBaseComponent comp = ChatSerializer.a("{\"text\":\"" + ChatColor.GOLD + ChatColor.UNDERLINE + "Hover here to see the leaderboards for " + nameOfGame.replaceAll("(.)([A-Z])", "$1 $2") + "!\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"" + string + "\"}}");
		for(Player player: players) {
			PacketPlayOutChat packet = new PacketPlayOutChat(comp, ChatMessageType.SYSTEM, player.getUniqueId());
			player.setGameMode(GameMode.ADVENTURE);
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
			playersPlaying.remove(player);
			FileConfiguration config = Main.getPlugin(Main.class).getConfig();
			@SuppressWarnings("unchecked")
			Location location = Location.deserialize((Map<String, Object>) config.get("Players." + player.getUniqueId() + ".Location"));
			player.teleport(location);
			@SuppressWarnings("unchecked")
			ItemStack[] items = ((List<ItemStack>) config.getList("Players." + player.getUniqueId() + ".Inventory.contents")).toArray(new ItemStack[0]);
			player.getInventory().setContents(items);
			Integer slot = config.getInt("Players." + player.getUniqueId() + ".Inventory.slot");
			player.getInventory().setHeldItemSlot(slot);
			player.setLevel(config.getInt("Players." + player.getUniqueId() + ".XP.level"));
			player.setExp(config.getInt("Players." + player.getUniqueId() + ".XP.xp"));
			player.setHealth(config.getInt("Players." + player.getUniqueId() + ".Health"));
			player.setFoodLevel(config.getInt("Players." + player.getUniqueId() + ".Hunger"));
			config.set("Players." + player, null);
			Main.getPlugin(Main.class).saveConfig();
			if(!(this instanceof SkinMaker || this instanceof FlappyChicken)) {
				HubNPC.spawnNPCs(player);
			}
		}
		games.remove(this);
	}
	
	public static Game getGame(UUID uuid) {
		for(Game game: games) {
			if(game.uuid.equals(uuid)) {
				return game;
			}
		}
		return null;
	}
}
