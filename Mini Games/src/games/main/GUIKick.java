package games.main;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;

public class GUIKick implements Listener {
	
	static HashMap<Player, Integer> clicks = new HashMap<Player, Integer>();
	
	@EventHandler
	public void click(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Integer click = clicks.get(player);
		if(click == null) {
			clicks.put(player, 1);
		} else {
			if(click >= 7) {
				if(player.getOpenInventory() != null) {
					player.closeInventory();
					player.sendMessage(ChatColor.RED + "Do not spam click inventorys.");
				}
				clicks.remove(player);
				return;
			}
			clicks.replace(player, click + 1);
		}
		new BukkitRunnable() {

			@Override
			public void run() {
				Integer click = clicks.get(player);
				if(click == null) {
					return;
				}
				if(click == 1) {
					clicks.remove(player);
					return;
				}
				clicks.replace(player, click - 1);
			}
			
		}.runTaskLater(Main.getPlugin(Main.class), 20);
	}
	
}
