package games.game;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import games.main.Main;
import net.md_5.bungee.api.ChatColor;

public class Protection implements Listener {
	
	public ArrayList<Block> blocks = new ArrayList<Block>();
	public static ArrayList<Protection> protections = new ArrayList<Protection>();
	
	public Protection(ArrayList<Block> blocks) {
		this.blocks = blocks;
		protections.add(this);
	}
	
	public void setProtection(boolean p) {
		HandlerList handlers = BlockBreakEvent.getHandlerList();
		Plugin plugin = Main.getPlugin(Main.class);
		PluginManager manager = plugin.getServer().getPluginManager();
		if(p == true) {
			manager.registerEvents(this, plugin);
		} else if(p == false) {
			handlers.unregister(this);
			protections.remove(this);
		}
	}
	
	@EventHandler
	public void destroy(BlockBreakEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();
		if(blocks.contains(block)) {
			if(!player.getName().equals("Bluethorne")) {
				Block fire = new Location(block.getWorld(), block.getX(), block.getY() + 1, block.getZ()).getBlock();
				if(fire.getType().equals(Material.FIRE)) {
					fire.setType(Material.AIR);
				}
				player.sendMessage(ChatColor.RED + "You cannot break this block!");
				event.setCancelled(true);
			}
		}
	}
	
	public static Protection getProtection(ArrayList<Block> blocks) {
		for(Protection protection: protections) {
			if(protection.blocks.equals(blocks)) {
				return protection;
			}
		}
		return null;
	}
}
