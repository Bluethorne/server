package games.buildBattle.menu;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import games.buildBattle.BuildBattle;
import games.main.Main;
import games.main.Pair;

public class Sounds {
	
	static ArrayList<Sounds> sounds = new ArrayList<Sounds>();
	public static ArrayList<Pair<ItemStack, Sound>> soundNames = new ArrayList<Pair<ItemStack, Sound>>();
	public static ItemStack cancel = new ItemStack(Material.BARRIER);
	public static ArrayList<Inventory> soundInv = new ArrayList<Inventory>(Arrays.asList(Main.getPlugin(Main.class).getServer().createInventory(null, 54, ChatColor.BLUE + "Sounds")));
	Player player;
	Location location;
	Sound sound;
	Sounds s = this;
	
	public Sounds(Player player, ItemStack item) {
		this.player = player;
		this.location = player.getLocation();
		this.sound = Pair.getValue(item, soundNames).get(0);
		sounds.add(s);
	}
	
	public static void loadSounds() {
		ItemMeta metac = cancel.getItemMeta();
		metac.setDisplayName(ChatColor.RED + "Cancel all of the sounds");
		cancel.setItemMeta(metac);
		soundInv.get(0).setItem(49, cancel);
		
		for(Sound sound: Sound.values()) {
			String name = (WordUtils.capitalizeFully(sound.name().replace("_", " "))).replace("Block ", "").replace("Entity ", "").replace("Ambient ", "").replace("Ambient", "").replace("Loop", "").replace("Additions", "").replace("Rare", "").replace("Ultra", "").replace("Break", "").replace("Destroy", "").replace("Place", "").replace("Fall", "").replace("Hit", "").replace("Land", "").replace("Use", "").replace("Step", "").replace("Item", "").replace("Music Disc ", "").replace("Ui ", "").replace("  ", " ");
			ItemStack item = new ItemStack(Material.NOTE_BLOCK);
			ItemMeta meta = item.getItemMeta();
			meta.setLore(new ArrayList<String>(Arrays.asList(ChatColor.GRAY + sound.name())));
			meta.setDisplayName(ChatColor.YELLOW + name);
			item.setItemMeta(meta);
			soundNames.add(new Pair<ItemStack, Sound>(item, sound));
			Inventory inv = soundInv.get(soundInv.size() - 1);
			if(inv.getItem(44) == null) {
    			inv.setItem(inv.firstEmpty(), item);
    		} else {
    			inv.setItem(53, Heads.forward);
    			Inventory newInv = Main.getPlugin(Main.class).getServer().createInventory(null, 54, ChatColor.BLUE + "Sounds");
    			newInv.setItem(45, Heads.back);
    			newInv.addItem(item);
    			newInv.setItem(49, cancel);
    			soundInv.add(newInv);
    		}
		}
	}
	
	public static void removeP(Player player) {
		ArrayList<Sounds> remove = new ArrayList<Sounds>();
		for(Sounds sound : sounds) {
			if(sound.player.equals(player)) {
				remove.add(sound);
			}
		}
		sounds.removeAll(remove);
	}
	
	public static void removeG(BuildBattle game) {
		ArrayList<Sounds> remove = new ArrayList<Sounds>();
		for(Sounds sound: sounds) {
			if(Pair.getKeys(game.plots).contains(sound.player)) {
				remove.add(sound);
			}
		}
		sounds.removeAll(remove);
	}
	
	public void addSound() {
		new BukkitRunnable() {

			@Override
			public void run() {
				Main.getPlugin(Main.class).getServer().getWorld("world").playSound(location, sound, 1, 1);
				if(!sounds.contains(s)) {
					this.cancel();
				}
			}
			
		}.runTaskTimer(Main.getPlugin(Main.class), 0, 5);
	}
	
	static ArrayList<Sounds> getSounds(Player player) {
		ArrayList<Sounds> s = new ArrayList<Sounds>();
		for(Sounds sound: sounds) {
			if(sound.player.equals(player)) {
				s.add(sound);
			}
		}
		return s;
	}
	
}
