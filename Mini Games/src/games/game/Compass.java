package games.game;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Compass implements Listener{
	
	public static ItemStack compass = new ItemStack(Material.COMPASS);
	public static final String name = ChatColor.AQUA + "Click to play a game";
	
	static {
		ItemMeta meta = compass.getItemMeta();
		meta.setDisplayName(name);
		compass.setItemMeta(meta);
	}
	
	@EventHandler
	public void drop(PlayerDropItemEvent event) {
		ItemStack item = event.getItemDrop().getItemStack();
		if(item.getItemMeta().getDisplayName().equals(name)) {
			event.setCancelled(true);
		}
	} 
	
	@EventHandler
	public void click(PlayerInteractEvent event) {
		Action action = event.getAction();
		if(!action.equals(Action.PHYSICAL)) {
			if(event.getItem() != null) {
				String name = event.getItem().getItemMeta().getDisplayName();
				if(name.equals(Compass.name)) {
					Player player = event.getPlayer();
					if(!Game.playersPlaying.contains(player)) {
						new GameChooser(new String[0], player);
					} else {
						player.sendMessage(ChatColor.RED + "You are already playing a game");
					}
				}
			}
		}
	}
	
	@EventHandler
	public void click(InventoryClickEvent event) {
		ItemStack item = event.getCurrentItem();
		if(item != null) {
			if(item.getType().equals(Material.COMPASS)) {
				event.setCancelled(true);
			}
		}
	} 
}
