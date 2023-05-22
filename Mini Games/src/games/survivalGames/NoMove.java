package games.survivalGames;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import games.main.Main;

public class NoMove implements Listener {
	
	public ArrayList<Player> players = new ArrayList<Player>();
	
	public NoMove(ArrayList<Player> players) {
		this.players = players;
		Main.getPlugin(Main.class).getServer().getPluginManager().registerEvents(this, Main.getPlugin(Main.class));
	}
	
	@EventHandler
	public void move(PlayerMoveEvent event) {
		if(players.contains(event.getPlayer())) {
			Location from = event.getFrom();
			Location to = event.getTo();
			Location newTo = new Location(from.getWorld(), from.getX(), from.getY(), from.getZ(), to.getYaw(), to.getPitch());
			event.setTo(newTo);
		}
	}
	
	@EventHandler
	public void open(PlayerInteractEvent event) {
		if(players.contains(event.getPlayer())) {
			if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				Block block = event.getClickedBlock();
				if(block.getType().equals(Material.CHEST)) {
					event.setCancelled(true);
				}
			}
		}
	}
	
	public void end() {
		PlayerMoveEvent.getHandlerList().unregister(this);
		PlayerInteractEvent.getHandlerList().unregister(this);
	}
	
}
