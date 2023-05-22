package games.survivalGames;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import games.buildBattle.menu.Heads;
import games.game.Game;
import games.game.PlayerWinEvent;
import games.main.Main;

public class Compass extends ItemStack implements Listener{
	
	private Player player;
	private SurvivalGames game;
	private ArrayList<Inventory> invs = new ArrayList<Inventory>();
	Compass c = this;
	private static ItemStack[] items = new ItemStack[54];
	
	static {
		for(int i = 0; i < 9; i++) {
			if(i % 2 == 0) {
				items[i] = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
			} else {
				items[i] = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
			}
		}
	}
	
	public Compass(Player player) {
		super(Material.COMPASS);
		this.player = player;
		game = (SurvivalGames) Game.getGame(player);
		Main.getPlugin(Main.class).getServer().getPluginManager().registerEvents(this, Main.getPlugin(Main.class));
		ItemMeta meta = this.getItemMeta();
		meta.setDisplayName(ChatColor.AQUA + "Compass");
		meta.setLore(new ArrayList<String>(Arrays.asList(ChatColor.YELLOW + "Click to teleport to a player")));
		this.setItemMeta(meta);
	}
	
	@EventHandler
	public void click(InventoryClickEvent event) {
		ItemStack item = event.getCurrentItem();
		if(item != null) {
			if(invs.contains(event.getInventory())) {
				event.setCancelled(true);
				Player player = (Player) event.getWhoClicked();
				if(item.getType().equals(Material.PLAYER_HEAD)) {
					OfflinePlayer teleporter = ((SkullMeta) item.getItemMeta()).getOwningPlayer();
					if(game.players.contains(player)) {
						player.teleport((Player) teleporter);
					} else {
						player.sendMessage(ChatColor.RED + "They are not playing or they have died");
					}
				} else {
					if(item.equals(Heads.forward)) {
						player.openInventory(invs.get(invs.indexOf(player.getOpenInventory().getTopInventory()) + 1));
					} else if(item.equals(Heads.back)) {
						player.openInventory(invs.get(invs.indexOf(player.getOpenInventory().getTopInventory()) - 1));
					}
				}
			}
		}
	}
	
	@EventHandler
	public void interact(PlayerInteractEvent event) {
		Action action = event.getAction();
		if(action.equals(Action.RIGHT_CLICK_AIR) ||  action.equals(Action.RIGHT_CLICK_BLOCK)) {
			ItemStack item = event.getItem();
			if(item != null) {
				if(event.getPlayer().equals(player)) {
					if(item.getType().equals(Material.COMPASS)) {
						invs = getNewInventory();
						player.openInventory(invs.get(0));
					}
				}
			}
		}
	}
	
	private ArrayList<Inventory> getNewInventory() {
		ArrayList<Inventory> invs = new ArrayList<Inventory>();
		Inventory i = Main.getPlugin(Main.class).getServer().createInventory(null, 54, ChatColor.BLUE + "Teleport Inventory");
		i.setContents(items);
		invs.add(i);
		ArrayList<Player> players = game.players;
		int n = 9;
		for(Player player: players) {
			if(!game.kills.dead.contains(player)) {
				ItemStack head = new ItemStack(Material.PLAYER_HEAD);
				SkullMeta meta = (SkullMeta) head.getItemMeta();
				meta.setOwningPlayer(player);
				meta.setDisplayName(ChatColor.YELLOW + player.getName());
				head.setItemMeta(meta);
				Inventory lastInv = invs.get(invs.size() - 1);
				if(lastInv.getItem(44) == null) {
					while(n % 9 % 8 == 0) {
						n++;
					}
					lastInv.setItem(n, head);
				} else {
					Inventory newInv = Main.getPlugin(Main.class).getServer().createInventory(null, 54, ChatColor.BLUE + "Teleport Inventoy");
					lastInv.setItem(53, Heads.forward);
					newInv.setItem(45, Heads.back);
					newInv.addItem(head);
					invs.add(newInv);
				}
			}
		}
		return invs;
	}
	
	@EventHandler
	public void drag(InventoryDragEvent event) {
		if(invs.contains(event.getInventory())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void win(PlayerWinEvent event) {
		if(event.getGame().equals(game)) {
			new BukkitRunnable() {

				@Override
				public void run() {
					InventoryClickEvent.getHandlerList().unregister(c);
					PlayerInteractEvent.getHandlerList().unregister(c);
					InventoryDragEvent.getHandlerList().unregister(c);
					PlayerDropItemEvent.getHandlerList().unregister(c);
				}
				
			}.runTaskLater(Main.getPlugin(Main.class), 200);
		}
	}
	
	@EventHandler
	public void drop(PlayerDropItemEvent event) {
		Item item = event.getItemDrop();
		if(player.equals(event.getPlayer())) {
			if(item.getItemStack().getType().equals(Material.COMPASS)) {
				event.setCancelled(true);
			}
		}
	}
}
