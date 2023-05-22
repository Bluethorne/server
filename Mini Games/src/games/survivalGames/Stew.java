package games.survivalGames;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import games.main.Main;

public class Stew implements Listener{
	
	static ArrayList<Player> players = new ArrayList<Player>();
	
	@EventHandler
	public void eat(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if(players.contains(player)) {
			Action action = event.getAction();
			if(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
				ItemStack item = event.getItem();
				if(item != null) {
					if(item.getType().equals(Material.MUSHROOM_STEW)) {
						Main.getPlugin(Main.class).getServer().getWorld("world").playSound(player.getLocation(), Sound.BLOCK_GRASS_BREAK, 1, 1);
						player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 0));
						event.setCancelled(true);
						player.getInventory().setItem(player.getInventory().getHeldItemSlot(), null);
					}
				}
			}
		}
	}
	
	public static void addPlayers(ArrayList<Player> players) {
		Stew.players.addAll(players);
	}
	
	public static void removePlayers(ArrayList<Player> players) {
		Stew.players.removeAll(players);
	}
	
	public static void removePlayer(Player player) {
		Stew.players.remove(player);
	}
}
