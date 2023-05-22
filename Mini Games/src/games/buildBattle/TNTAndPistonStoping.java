package games.buildBattle;

import java.util.Collections;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBurnEvent;

import games.game.Land;
import games.game.Protection;

public class TNTAndPistonStoping implements Listener {
	
	@EventHandler
	public void destroy(BlockPlaceEvent event) {
		Block block = event.getBlock();
		Material material = block.getType();
		if(material.equals(Material.PISTON) || material.equals(Material.STICKY_PISTON) || material.equals(Material.TNT)) {
			Location location = block.getLocation();
			Integer x = location.getBlockX();
			Integer z = location.getBlockZ();
			Integer y = location.getBlockY();
			if(Land.map.size() != 0) {
				Integer biggestX = Collections.max(Land.map);
				if(((x == -1) && (y < 232 && y > 199) && (z > -1 && z < 32)) || ((x == biggestX + 1) && (y < 232 && y > 199) && (z > -1 && z < 32)) || ((y == 199) && (z > -1 && z < 32) && (x > -1 && x < biggestX + 1)) || ((y == 232) && (z > -1 && z < 32) && (x > -1 && x < biggestX + 1)) || ((z == -1) && (y > -1 && y < 232) && (x > -1 && x < biggestX + 1)) || ((z == 32) && (y > -1 && y < 232) && (x < biggestX + 1))) {
					event.setCancelled(true);
				}
			}
		}	
	}
	
	@EventHandler
	public void piston(BlockPistonExtendEvent event) {
		List<Block> blocks = event.getBlocks();
		for(Block block: blocks) {
			for(Protection prot: Protection.protections) {
				if(prot.blocks.contains(block)) {
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void fire(BlockBurnEvent event) {
		Block block = event.getBlock();
		for(Protection prot: Protection.protections) {
			if(prot.blocks.contains(block)) {
				event.setCancelled(true);
			}
		}
	}
}
