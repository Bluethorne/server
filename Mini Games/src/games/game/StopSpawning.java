package games.game;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import games.main.Main;
import games.main.Pair;

public class StopSpawning implements Listener {
	
	ArrayList<Pair<Integer, Integer>> coords = new ArrayList<Pair<Integer, Integer>>();
	static ArrayList<StopSpawning> stopspawnings = new ArrayList<StopSpawning>();
	public static ArrayList<Entity> allowed = new ArrayList<Entity>();
	
	public StopSpawning(ArrayList<Pair<Integer, Integer>> coords) {
		this.coords = coords;
		stopspawnings.add(this);
	}
	
	public void setSpawn(boolean p) {
		HandlerList handlers = EntitySpawnEvent.getHandlerList();
		Plugin plugin = Main.getPlugin(Main.class);
		PluginManager manager = plugin.getServer().getPluginManager();
		if(p == true) {
			manager.registerEvents(this, plugin);
		} else if(p == false) {
			handlers.unregister(this);
			stopspawnings.remove(this);
		}
	}
	
	@EventHandler
	public void destroy(EntitySpawnEvent event) {
		Location location = event.getLocation();
		Integer x = location.getBlockX();
		Integer z = location.getBlockZ();
		Integer y = location.getBlockY();
		for(Pair<Integer, Integer> pair: this.coords) {
			if(pair.getKey().equals(x) && pair.getValue().equals(z)) {
				if(y < 232 && y > 200) {
					if(!allowed.contains(event.getEntity())) {
						event.setCancelled(true);
					} else {
						allowed.remove(event.getEntity());
					}
				}
			}
		}
	}
	
	public static StopSpawning getStopSpawning(ArrayList<Pair<Integer, Integer>> coords) {
		for(StopSpawning stopspawning: stopspawnings) {
			if(stopspawning.coords.equals(coords)) {
				return stopspawning;
			}
		}
		return null;
	}
}
