package games.survivalGames;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Enchanter implements Listener{
	
	static ArrayList<Player> players = new ArrayList<Player>();
	
	public static void addPlayers(ArrayList<Player> players) {
		Enchanter.players.addAll(players);
	}
	
	public static void removePlayer(Player player) {
		Enchanter.players.remove(player);
	}
	
	public static void removePlayers(ArrayList<Player> players) {
		Enchanter.players.removeAll(players);
	}
	
	@EventHandler
	public void enchant(InventoryOpenEvent e) {
		Player player = (Player) e.getPlayer();
		if(players.contains(player)) {
			Inventory inv = e.getInventory();
			if(inv.getType().equals(InventoryType.ENCHANTING)) {
				EnchantingInventory einv = (EnchantingInventory) e.getInventory();
				ItemStack item = new ItemStack(Material.LAPIS_LAZULI);
				item.setAmount(64);
				einv.setItem(1, item);
			}
		}
	}
	
	@EventHandler
	public void click(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if(players.contains(player)) {
			if(event.getInventory().getType().equals(InventoryType.ENCHANTING)) {
				if(event.getCurrentItem().getType().equals(Material.LAPIS_LAZULI)) {
					event.setCancelled(true);
				} 
			}
		}
	}
	
	@EventHandler
	public void close(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		if(players.contains(player)) {
			if(event.getInventory().getType().equals(InventoryType.ENCHANTING)) {
				EnchantingInventory inv = (EnchantingInventory) event.getInventory();
				inv.setItem(1, null);
			}
		}
	}
	
}
