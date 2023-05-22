package games.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import games.buildBattle.BuildBattle;
import games.tribes.Tribes;
import games.main.Main;
import games.main.Stats;
import games.skinMaker.SkinMaker;
import games.survivalGames.SurvivalGames;
import net.minecraft.server.v1_16_R3.ChatMessageType;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_16_R3.PacketPlayOutChat;

public class GameChooser implements Listener {
	
	static Plugin plugin = Main.getPlugin(Main.class);
	
	ArrayList<Player> reqPlayers = new ArrayList<Player>();
	ArrayList<Player> players = new ArrayList<Player>();
	static ArrayList<GameChooser> choosers = new ArrayList<GameChooser>();
	Player player;
	String game;
	
	HashMap<Player, Inventory> invs = new HashMap<Player, Inventory>();
	static Inventory i = plugin.getServer().createInventory(null, 27, ChatColor.GOLD + "Choose a game to play");
	
	static HashMap<String, List<String>> descriptions = new HashMap<String, List<String>>() {
		private static final long serialVersionUID = 1L;
	{
		put("Build Battle", new ArrayList<String>(Arrays.asList("§7Build the theme for 10 minutes", "§7and then vote for the best build.", "", "§eClick to Play")));
		put("Survival Games", new ArrayList<String>(Arrays.asList("§7Drop into a world and fight until you are last alive.", "§7A spaceship will spawn in to collect the dead players!.", "§7Look out!", "", "§eClick to play")));
		put("Skin Maker", new ArrayList<String>(Arrays.asList("§7Want a new skin?", "§7Play to create your own skin.", "", "§eClick to Play")));
		put("Tribes", new ArrayList<String>(Arrays.asList("§7A custom game which is consists of concepts of MMORPG and factions.", "", "§c§lCOMING SOON")));
	}};
	
	public GameChooser(String[] strings, Player player) {
		choosers.add(this);
		if(strings != null) {
			ArrayList<String> playersString = new ArrayList<>(Arrays.asList(strings));
			for(Player friend : plugin.getServer().getOnlinePlayers()) {
				if(playersString.contains(friend.getName())) {
					if(friend.equals(player)) {
						player.sendMessage(ChatColor.RED + "You cannot play with yourself");
					} else {
						this.reqPlayers.add(friend);	
					}
					playersString.remove(friend.getName());
				}
			}
			for(String notPlayer: playersString) {
				player.sendMessage(ChatColor.RED + notPlayer + " is not a player or is not online. They have been removed from your game");
			}
		}
		this.player = player;
		openGameChooser();
	}
	
	
	public static void loadInventory() {
		if(i.firstEmpty() == 0) {
			Set<String> games = plugin.getConfig().getConfigurationSection("Games.").getKeys(false);
			for(int n = 0; n < 27; n++) {
				Material material = null;
				if(n % 9 == 0 || n % 9 == 8) {
					material = getMaterial((n + 1) % 2);
				}
				if(n % 9 == 1 || n % 9 == 7 || ((n % 9 == 2 || n % 9 == 6) && (int) (n / 10) == 1)) {
					material = Material.WHITE_STAINED_GLASS_PANE;
				}
				if(n == 13) {
					material = Material.PLAYER_HEAD;
				}
				int num = (((int) ((n * 1.67) / 10)) + 1 - ((int) (n / 20))) * ((((((((((((((((((((((((n + 2) % 28) % 27) % 25) % 24) % 23) % 21) % 20) % 19) % 18) % 17) % 16) % 15) % 14) % 13) % 12) % 11) % 10) % 9) % 7) % 6) % 5) % 3) % 2);
				if(num != 0) {
					material = Material.getMaterial((String) games.toArray()[num - 1]);
				}
				if(material != null) {
					ItemStack item = new ItemStack(material);
					String name = plugin.getConfig().getString("Games." + material);
					List<String> lore;
					if(name == null) {
						name = "";
						lore = new ArrayList<String>();
					} else {
						lore = descriptions.get(name);
					}
					ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + name);
					meta.setLore(lore);
					meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
					item.setItemMeta(meta);
					i.setItem(n, item);
				}
			}
		}
	}
	
	static Material getMaterial(int num) {
		if(num == 0) {
			return Material.GREEN_STAINED_GLASS_PANE;
		} else if(num == 1) {
			return Material.LIME_STAINED_GLASS_PANE;
		}
		return null;
	}
	
	void openGameChooser() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		Inventory i = plugin.getServer().createInventory(null, 27, ChatColor.GOLD + "Choose a game to play");
		i.setContents(GameChooser.i.getContents());
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + this.player.getName());
		meta.setLore(new ArrayList<String>(Arrays.asList(ChatColor.GRAY + "Click to view stats")));
		meta.setOwningPlayer(this.player);
		item.setItemMeta(meta);
		i.setItem(13, item);
		invs.put(this.player, i);
		this.player.openInventory(i);
	}
	
	@EventHandler
	public void click(InventoryClickEvent event) {
		Player player = plugin.getServer().getPlayer(event.getWhoClicked().getName());
		if(this.player.equals(player)) {
			Inventory i = invs.get(this.player);
			if(event.getInventory().equals(i)) {
				event.setCancelled(true);
				if(event.getClickedInventory() != null) {
					if(event.getClickedInventory().equals(i)) {
						if(event.getCurrentItem() != null) {
							this.game = plugin.getConfig().getString("Games." + event.getCurrentItem().getType());
							if(this.game != null) {
								InventoryClickEvent.getHandlerList().unregister(this);
								InventoryCloseEvent.getHandlerList().unregister(this);
								PlayerQuitEvent.getHandlerList().unregister(this);
								this.player.closeInventory();
								invs.remove(this.player);
								this.player.sendMessage(ChatColor.GREEN + "You have chosen to play " + this.game);
								Game.playersPlaying.add(player);
								if(reqPlayers.size() != 0) {
									this.player.sendMessage(ChatColor.GOLD + "Waiting for players to accept");
									for(Player p: reqPlayers) {
										askToPlay(p);
									}
								} else {
									findGame();
								}
							} else {
								if(event.getCurrentItem().getType().equals(Material.PLAYER_HEAD)) {
									Stats.showStats(player, player);
								}
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void leave(InventoryCloseEvent event) {
		if(this.player.equals(event.getPlayer())) {
			InventoryClickEvent.getHandlerList().unregister(this);
			InventoryCloseEvent.getHandlerList().unregister(this);
			PlayerQuitEvent.getHandlerList().unregister(this);
			choosers.remove(this);
			invs.remove(this.player);
		}
	}
	
	@EventHandler
	public void quit(PlayerQuitEvent event) {
		if(this.player.equals(event.getPlayer())) {
			InventoryClickEvent.getHandlerList().unregister(this);
			InventoryCloseEvent.getHandlerList().unregister(this);
			PlayerQuitEvent.getHandlerList().unregister(this);
			choosers.remove(this);
			invs.remove(this.player);
		}
	}
	
	void askToPlay(Player player) {
		IChatBaseComponent comp = ChatSerializer.a("{\"text\":\"§6"+ this.player.getName() + " §6wants §6to §6play §6" + this.game + " §6with §6you. §6Do §6you §6want §6to §6play §6with §6them? \", \"extra\":[{\"text\":\" §a[Accept] \", \"clickEvent\":{\"action\":\"run_command\", \"value\":\"/playwith " + this.player.getName() + " accept\"}}, {\"text\":\" §c[Decline]\", \"clickEvent\":{\"action\":\"run_command\", \"value\":\"/playwith " + this.player.getName() + " decline\"}}]}");
		PacketPlayOutChat packet = new PacketPlayOutChat(comp, ChatMessageType.SYSTEM, player.getUniqueId());
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

	public static GameChooser getChooser(Player player) {
		for(GameChooser g: choosers) {
			if(g.player.equals(player)) {
				return g;
			}
		}
		return null;
	}

	public void reply(Player player, Boolean accept) {
		if(reqPlayers.contains(player)) {
			if(accept == true) {
				players.add(player);
				Game.playersPlaying.add(player);
				this.player.sendMessage(ChatColor.GOLD + player.getName() + " wants to play");
				player.sendMessage(ChatColor.GREEN + "You are playing with " + this.player.getName() + ", the game will start once all the players have replied");
			} else if(accept == false) {
				reqPlayers.remove(player);
				this.player.sendMessage(ChatColor.GOLD + player.getName() + " does not want to play");
				player.sendMessage(ChatColor.GREEN + "You dont want to play with " + this.player.getName());
			}
			checkAccept();
		} else {
			player.sendMessage(ChatColor.RED + this.player.getName() + " has not invited you to play anything");
		}
	}
	
	void checkAccept() {
		if(this.players.equals(this.reqPlayers)) {
			if(this.players.size() > 0) {
				this.players.add(this.player);
				findGame();
			} else {
				Game.playersPlaying.remove(this.player);
				this.player.sendMessage(ChatColor.GOLD + "Everyone that you invited declined your invitation. Type /play to play again");
				choosers.remove(this);
			}
		}
	}
	
	void findGame() {
		if(this.players.size() == 0) {
			if(this.game.replace(" ", "").equals(SurvivalGames.class.getSimpleName())) {
				SurvivalGames.addToQue(player);
			} else if(this.game.replace(" ", "").equals(BuildBattle.class.getSimpleName())) {
				BuildBattle.addToQue(player);
			} else if(this.game.replace(" ", "").equals("Tribes")) {
				Tribes.join(player);
			} else if(this.game.replace(" ", "").equals("SkinMaker")) {
				SkinMaker.join(player);
			}
		} else {
			if(this.game.replace(" ", "").equals(SurvivalGames.class.getSimpleName())) {
				new SurvivalGames(this.players);
			} else if(this.game.replace(" ", "").equals(BuildBattle.class.getSimpleName())) {
				new BuildBattle(this.players);
			} else if(this.game.replace(" ", "").equals("Tribes")) {
				for(Player player: this.players) {
					Tribes.join(player);
				}
			} else if(this.game.replace(" ", "").equals("SkinMaker")) {
				for(Player player: this.players) {
					SkinMaker.join(player);
				}
			}
		}
		choosers.remove(this);
	}
}
