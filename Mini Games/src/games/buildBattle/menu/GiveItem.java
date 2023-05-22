package games.buildBattle.menu;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import games.main.Main;

public class GiveItem implements Listener {
	
	@EventHandler
	public void drop(PlayerDropItemEvent event) {
		org.bukkit.entity.Item item = event.getItemDrop();
		ItemStack stack = item.getItemStack();
		if(Main.buildbattle_menu.equals(stack)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void move(InventoryClickEvent event) {
		ItemStack stack = event.getCurrentItem();
		ItemStack stack2 = event.getCursor();
		if(Main.buildbattle_menu.equals(stack) || Main.buildbattle_menu.equals(stack2)) {
			event.setCancelled(true);
		}
	}
}
