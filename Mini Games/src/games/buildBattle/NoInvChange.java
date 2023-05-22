package games.buildBattle;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import games.main.Main;

public class NoInvChange implements Listener {
	
	static ArrayList<Player> players = new ArrayList<Player>();
	
	public static void remove(Player player) {
		if(players.contains(player)) {
			players.remove(player);
		}
	}
	
	public static void add(Player player) {
		if(!players.contains(player)) {
			players.add(player);
		}
	}
	
	@EventHandler
	public void change(InventoryClickEvent event) {
		Player player = Main.getPlugin(Main.class).getServer().getPlayer(event.getWhoClicked().getName());
		if(players.contains(player)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void change(InventoryDragEvent event) {
		Player player = Main.getPlugin(Main.class).getServer().getPlayer(event.getWhoClicked().getName());
		if(players.contains(player)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void drop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		if(players.contains(player)) {
			event.setCancelled(true);
		}
	}
}
