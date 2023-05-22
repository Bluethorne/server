package games.survivalGames;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import games.game.Game;
import games.main.Main;
import games.main.Pair;

public class TNT implements Listener {
	
	public static ArrayList<Pair<Player, Entity>> tnt = new ArrayList<Pair<Player, Entity>>();
	
	@EventHandler
	public void interact(PlayerInteractEvent event) {
		if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			Player player = event.getPlayer();
			if(Game.playersPlaying.contains(player)) {
				if(Game.getGame(player) instanceof SurvivalGames) {
					ItemStack item = event.getItem();
					if(item != null) {
						if(item.getType().equals(Material.TNT)) {
							item.setAmount(item.getAmount() - 1);
							Location location = player.getLocation();
							Entity entity = Main.getPlugin(Main.class).getServer().getWorld("world").spawnEntity(player.getLocation(), EntityType.PRIMED_TNT);
							entity.setVelocity(new Vector(Math.sin((location.getYaw() - 180) * Math.PI / 180), location.getPitch() / -90, Math.cos(location.getYaw() * Math.PI / 180)));
							tnt.add(new Pair<Player, Entity>(player, entity));
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void explode(EntityExplodeEvent event) {
		Entity entity = event.getEntity();
		if(Pair.getValues(tnt).contains(entity)) {
			event.blockList().clear();
			tnt.remove(Pair.getPairFromValue(entity, tnt).get(0));
		}
	}
	
}
