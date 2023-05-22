package games.main;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import games.buildBattle.BuildBattle;
import games.game.Game;
import games.game.GameChooser;
import games.skinMaker.SkinMaker;
import games.survivalGames.SurvivalGames;
import net.minecraft.server.v1_16_R3.ChatMessageType;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.PacketPlayOutChat;
import net.minecraft.server.v1_16_R3.IChatBaseComponent.ChatSerializer;

public class Commands implements CommandExecutor{
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = ((Player) sender).getPlayer();
			if(command.getName().equalsIgnoreCase("play")) {
				if(!Game.playersPlaying.contains(player)) {
					new GameChooser(args, player);
				} else {
					if(BuildBattle.que.contains(player) || SurvivalGames.que.contains(player)) {
						IChatBaseComponent comp = ChatSerializer.a("{\"text\":\"§cYou §care §calready §cin §ca §cqueue.\",\"extra\":[{\"text\":\" §e§lCLICK HERE \",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/leave\"}}, {\"text\":\"§cto §cleave\"}]}");
						((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(comp, ChatMessageType.SYSTEM, player.getUniqueId()));
					} else {
						sender.sendMessage(ChatColor.RED + "You are already playing a game");
					}
				}
			} else if(command.getName().equalsIgnoreCase("leave")) {
				if(Game.playersPlaying.contains(player)) {
					Game.leave(player, true);
				} else {
					sender.sendMessage(ChatColor.RED + "You are not playing a game or in a queue");
				}
			} else if(command.getName().equalsIgnoreCase("playwith")) {
				if(!Game.playersPlaying.contains(player)) {
					if(args.length > 0) { 
						Player asked = Bukkit.getPlayerExact(args[0]);
						if(asked != null) {
							if(!asked.equals(player)) {
								GameChooser chooser = GameChooser.getChooser(asked);
								if(chooser != null) {
									if(args.length > 1) {
										if(args[1].equalsIgnoreCase("decline")) {
											chooser.reply(player, false);
										} else if(args[1].equalsIgnoreCase("accept")) {
											chooser.reply(player, true);
										}
									} else {
										chooser.reply(player, true);
									}	
								} else {
									player.sendMessage(ChatColor.RED + args[0] + " has not invited you to play anything");
								}
							} else {
								player.sendMessage(ChatColor.RED + "You cant play with yourself");
							}
						} else {
							player.sendMessage(ChatColor.RED + args[0] + " is not a player or is not online");
						}
					} else {
						player.sendMessage(ChatColor.RED + "You must enter a player");
					}
				} else {
					player.sendMessage(ChatColor.RED + "You are already playing a game");
				}
			} else if(command.getName().equalsIgnoreCase("stats")) {
				if(args.length != 0) {
					try {
						URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + args[0]);
						Scanner scanner = new Scanner(url.openStream());
						try {
							scanner.next();
							@SuppressWarnings("deprecation")
							OfflinePlayer statsplayer = Main.getPlugin(Main.class).getServer().getOfflinePlayer(args[0]);
							if(statsplayer.hasPlayedBefore()) {	
								Stats.showStats(statsplayer, player);
							} else {
								player.sendMessage(ChatColor.RED + args[0] + " has not played before");
							}
						} catch(NoSuchElementException e) {
							player.sendMessage(ChatColor.RED + args[0] + " is not a player");
						}
						scanner.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else {
					Stats.showStats(player, player);
				}
			} else if(command.getName().equalsIgnoreCase("rank")) {
				String senderRank = "";
				try {
					Statement statement = DataBase.connection().createStatement();
					ResultSet result = statement.executeQuery("SELECT Rank FROM Hub WHERE UUID ='" + player.getUniqueId() + "'");
					if(result.next()) {
						senderRank = result.getString("Rank");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if(senderRank.equals("§4§lOwner") || senderRank.equals("§c§lAdmin") || senderRank.equals("§4§lLeader")) {
					if(args.length > 1) {
						String rank = args[0].replace("&", "§");
						try {
							Statement statement = DataBase.connection().createStatement();
							for(int count = 1; count < args.length; count++) {
								String name = args[count];
								try {
									URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
									Scanner scanner = new Scanner(url.openStream());
									try {
										scanner.next();
										@SuppressWarnings("deprecation")
										OfflinePlayer p = Main.getPlugin(Main.class).getServer().getOfflinePlayer(name);
										ResultSet result = statement.executeQuery("SELECT Rank From Hub WHERE UUID ='" + p.getUniqueId() + "'");
										if(result.next()) {
											statement.executeUpdate("UPDATE Hub SET Rank = '" + rank + "' WHERE UUID = '" + p.getUniqueId() + "'");
										} else {
											statement.executeUpdate("INSERT INTO Hub (UUID, Rank) VALUES ('" + p.getUniqueId() + "', '" + rank + "');");
										}
										player.sendMessage(ChatColor.GREEN + "You changed " + (args.length - 1) + " players ranks to " + rank);
									} catch(NoSuchElementException e2) {
										player.sendMessage(ChatColor.RED + name + " is not a player");
									}
									scanner.close();
								} catch(IOException e) {
									player.sendMessage(ChatColor.RED + name + " is not a player");
								}								
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}	
					} else {
						sender.sendMessage(ChatColor.RED + "You need to put a rank and then the players");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "You do not have permisson to run this command");
				}
			} else if(command.getName().equalsIgnoreCase("respond")) {
				if(args.length == 2) {
					QuestionairreEvent event = new QuestionairreEvent(UUID.fromString(args[0]), args[1].replace("_", " "), player);
					Main.getPlugin(Main.class).getServer().getPluginManager().callEvent(event);
				} else {
					player.sendMessage(ChatColor.RED + "You cannot do this");
				}
			} else if(command.getName().equalsIgnoreCase("hub")) {
				if(Game.playersPlaying.contains(player)) {
					Game.leave(player, true);
				}
				player.teleport(new Location(Main.getPlugin(Main.class).getServer().getWorld("world"), 30, 122, -80));
				player.removePotionEffect(PotionEffectType.NIGHT_VISION);
			} else if(command.getName().equalsIgnoreCase("world")) {
				String rank = Rank.getRank(player);
				if(rank.equals("§4§lOwner") || rank.equals("§a§lAdmin") || rank.equals("§4§lLeader")) {
					if(args.length == 1) {
						String worldName = args[0];
						World world = Main.getPlugin(Main.class).getServer().getWorld(worldName);
						if(world == null) {
							player.sendMessage(ChatColor.RED + "This is not a world");
						} else {
							Location location = new Location(world, 0, world.getHighestBlockYAt(0, 0) + 1, 0);
							player.teleport(location);
						}
					} else {
						player.sendMessage(ChatColor.RED + "You need to write a world");
					}
				} else {
					player.sendMessage(ChatColor.RED + "You cannot use this command");
				}
			} else if(command.getName().equals("skin")) {
				if(args.length == 0) {
					player.sendMessage(ChatColor.RED + "You must put on, off, change, delete or list");
				} else {
					if(args[0].equalsIgnoreCase("on")) {
						try {
							DataBase.connection().createStatement().executeUpdate("UPDATE Hub SET SetSkin = true WHERE UUID = '" + player.getUniqueId() + "'");
							if(args.length >= 2) {
								String[] nameList = args.clone();
								nameList[0] = "";
								String name = String.join(" ", nameList).substring(1);
								ResultSet result = DataBase.connection().createStatement().executeQuery("SELECT texture, signature, skinID FROM Skins WHERE LOWER(name) = '" + name.toLowerCase() + "' AND UUID = '" + player.getUniqueId() + "'");
								if(result.next()) {
									SkinMaker.changeSkin(player, result.getString("texture"), result.getString("signature"));
									String skinID = result.getString("skinID");
									DataBase.connection().createStatement().executeUpdate("UPDATE Hub SET skinID = '" + skinID + "' WHERE UUID = '" + player.getUniqueId() + "'");
									player.sendMessage(ChatColor.GREEN + "You have changed your skin to " + name);
								} else {
									player.sendMessage(ChatColor.RED + "You do not have a skin called " + name);
								}
							} else {
								ResultSet result = DataBase.connection().createStatement().executeQuery("SELECT texture, signature FROM Skins, Hub WHERE SetSkin = true AND Hub.skinID = Skins.skinID AND Hub.UUID = '" + player.getUniqueId() + "'");
								if(result.next()) {
									if(result.getString("texture") != null && result.getString("signature") != null) {
										player.sendMessage(ChatColor.GREEN + "You have set your skin");
										SkinMaker.changeSkin(player, result.getString("texture"), result.getString("signature"));
									} else {
										player.sendMessage(ChatColor.RED + "You have not made a skin yet");
									}
								} else {
									player.sendMessage(ChatColor.RED + "You have not made a skin yet");
								}
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
					} else if(args[0].equalsIgnoreCase("off")) {
						player.sendMessage(ChatColor.GREEN + "You have reset your skin");
						String json = "";
						try {
							DataBase.connection().createStatement().executeUpdate("UPDATE Hub SET SetSkin = false WHERE UUID = '" + player.getUniqueId() + "'");
							URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + player.getUniqueId() + "?unsigned=false");
							Scanner scanner = new Scanner(url.openStream());
							while(scanner.hasNext()) {
								json += scanner.next();
							}
							scanner.close();
						} catch (IOException | SQLException e) {
							e.printStackTrace();
						}
						@SuppressWarnings("unchecked")
						HashMap<String, ArrayList<HashMap<String, String>>> jsonMap = (HashMap<String, ArrayList<HashMap<String, String>>>) JSON.getJSON(json);
						HashMap<String, String> textures = jsonMap.get("properties").get(0);
						String texture = textures.get("value");
						String signature = textures.get("signature");
						SkinMaker.changeSkin(player, texture, signature);
					} else if(args[0].equalsIgnoreCase("change")) {
						if(args.length >= 2) {
							try {
								String[] nameList = args.clone();
								nameList[0] = "";
								String name = String.join(" ", nameList).substring(1);
								ResultSet result = DataBase.connection().createStatement().executeQuery("SELECT texture, signature, skinID FROM Skins WHERE LOWER(name) = '" + name.toLowerCase() + "' AND UUID = '" + player.getUniqueId() + "'");
								if(result.next()) {
									SkinMaker.changeSkin(player, result.getString("texture"), result.getString("signature"));
									String skinID = result.getString("skinID");
									DataBase.connection().createStatement().executeUpdate("UPDATE Hub SET skinID = '" + skinID + "' WHERE UUID = '" + player.getUniqueId() + "'");
									player.sendMessage(ChatColor.GREEN + "You have changed your skin to " + name);
								} else {
									player.sendMessage(ChatColor.RED + "You do not have a skin called " + name);
								}
							} catch (SQLException e) {
								e.printStackTrace();
							}
						} else {
							player.sendMessage(ChatColor.RED + "You must put the name of the skin to change to. Do /skin list to view your skins");
						}
					} else if(args[0].equalsIgnoreCase("delete")) {
						if(args.length >= 2) {
							try {
								String[] nameList = args.clone();
								nameList[0] = "";
								String name = String.join(" ", nameList).substring(1);
								ResultSet result = DataBase.connection().createStatement().executeQuery("SELECT skinID FROM Skins WHERE LOWER(name) = '" + name.toLowerCase() + "' AND UUID = '" + player.getUniqueId() + "'");
								if(result.next()) {
									String skinID = result.getString("skinID");
									DataBase.connection().createStatement().executeUpdate("DELETE FROM Skins WHERE skinID = '" + skinID + "'");
									DataBase.connection().createStatement().executeUpdate("UPDATE Hub SET skinID = NULL WHERE skinID = '" + skinID + "'");
									player.sendMessage(ChatColor.GREEN + "You have deleted your skin called " + name);
								} else {
									player.sendMessage(ChatColor.RED + "You do not have a skin called " + name);
								}
							} catch (SQLException e) {
								e.printStackTrace();
							}
						} else {
							player.sendMessage(ChatColor.RED + "You must put the name of the skin to delete. Do /skin list to view your skins");
						}
					} else if(args[0].equalsIgnoreCase("list")) {
						try {
							ResultSet result = DataBase.connection().createStatement().executeQuery("SELECT name FROM Skins WHERE UUID = '" + player.getUniqueId() + "'");
							player.sendMessage(ChatColor.GOLD + "Your skins:");
							while(result.next()) {
								player.sendMessage(ChatColor.YELLOW + result.getString("name"));
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
					} else {
						player.sendMessage(ChatColor.RED + "You must put on, off, change, delete or list");
					}
				}
			} else if(command.getName().equalsIgnoreCase("help")) {
				player.sendMessage(ChatColor.YELLOW + "/play      " + ChatColor.GOLD + "Type to play a game and put players names after if you want to play with them");
				player.sendMessage(ChatColor.YELLOW + "/playwith  " + ChatColor.GOLD + "Type to accept someones request to play with them");
				player.sendMessage(ChatColor.YELLOW + "/leave     " + ChatColor.GOLD + "Type to leave your game or queue");
				player.sendMessage(ChatColor.YELLOW + "/hub       " + ChatColor.GOLD + "Type to leave your game or queue and teleport back to the hub");
				player.sendMessage(ChatColor.YELLOW + "/stats     " + ChatColor.GOLD + "Type someones name after to show their stats");
				player.sendMessage(ChatColor.YELLOW + "/skin      " + ChatColor.GOLD + "Type on or off to change whether to show your skin. Type change to change the skin your wearing, list to list all the skins you have and delete to delete one of your skins");
			}
		}
		return true;
	}
}
