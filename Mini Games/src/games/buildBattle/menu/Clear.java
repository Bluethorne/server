package games.buildBattle.menu;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import games.main.Main;

public class Clear {
	
	public static Inventory confirm = Main.getPlugin(Main.class).getServer().createInventory(null, 27, ChatColor.RED + "Confirm");
	public static ItemStack ok = new ItemStack(Material.GREEN_TERRACOTTA);
	static ItemStack cancel = new ItemStack(Material.RED_TERRACOTTA);
	
	public static void confirm(Player player) {
		player.openInventory(confirm);
	}
	
	public static void loadConfirm() {
		ItemMeta okmeta = ok.getItemMeta();
		okmeta.setDisplayName(ChatColor.GREEN + "OK");
		ok.setItemMeta(okmeta);
		confirm.setItem(12, ok);
		ItemMeta cancelmeta = cancel.getItemMeta();
		cancelmeta.setDisplayName(ChatColor.RED + "CANCEL");
		cancel.setItemMeta(cancelmeta);
		confirm.setItem(14, cancel);
	}
	
	public static void clear(Integer plotx, Integer plotz) {
		for(int x = plotx + 1; x < plotx + 31; x++) {
			for(int y = 201; y < 231; y++) {
				for(int z = plotz + 1; z < plotz + 31; z++) {
					Block block = new Location(Main.getPlugin(Main.class).getServer().getWorld("world"), x, y, z).getBlock();
					block.setType(Material.AIR);
				}
			}
		}
	}
	
}
