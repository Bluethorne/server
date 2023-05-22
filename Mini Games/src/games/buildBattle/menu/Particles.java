package games.buildBattle.menu;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import games.buildBattle.BuildBattle;
import games.main.Main;
import games.main.Pair;

public class Particles {
	
	static Inventory particleInv = Main.getPlugin(Main.class).getServer().createInventory(null, 36, ChatColor.BLUE + "Particles");
	public static ItemStack cancel = new ItemStack(Material.BARRIER);
	static ArrayList<Pair<ItemStack, Particle>> particleNames = new ArrayList<Pair<ItemStack, Particle>>();
	Location location;
	Particle particle;
	Player player;
	static ArrayList<Particles> particles = new ArrayList<Particles>();
	Particles p = this;
	
	public Particles(Player player, Particle particle) {
		this.player = player;
		this.location = player.getLocation();
		this.particle = particle;
		particles.add(this);
		particle();
	}
	
	public static void loadParticles() {
		addParticle(ChatColor.GOLD + "Angry villager", Particle.VILLAGER_ANGRY, Material.STONE_AXE, 1);
		addParticle(ChatColor.GOLD + "Barrier", Particle.BARRIER, Material.APPLE, 2);
		addParticle(ChatColor.GOLD + "Bubble", Particle.WATER_BUBBLE, Material.LAPIS_LAZULI, 3);
		addParticle(ChatColor.GOLD + "Bubble pop", Particle.BUBBLE_POP, Material.ENDER_PEARL, 4);
		addParticle(ChatColor.GOLD + "Cloud", Particle.CLOUD, Material.SNOWBALL, 5);
		addParticle(ChatColor.GOLD + "Smoke", Particle.CAMPFIRE_SIGNAL_SMOKE, Material.CHARCOAL, 6);
		addParticle(ChatColor.GOLD + "Black heart", Particle.DAMAGE_INDICATOR, Material.COAL, 7);
		addParticle(ChatColor.GOLD + "Sparkle", Particle.END_ROD, Material.NETHER_STAR, 8);
		addParticle(ChatColor.GOLD + "Explosion", Particle.EXPLOSION_LARGE, Material.GUNPOWDER, 9);
		addParticle(ChatColor.GOLD + "Big Explosion", Particle.EXPLOSION_HUGE, Material.TNT, 10);
		addParticle(ChatColor.GOLD + "Firework Spark", Particle.FIREWORKS_SPARK, Material.FIREWORK_ROCKET, 11);
		addParticle(ChatColor.GOLD + "Flash", Particle.FLASH, Material.BEACON, 12);
		addParticle(ChatColor.GOLD + "Flame", Particle.FLAME, Material.TORCH, 13);
		addParticle(ChatColor.GOLD + "Emerald", Particle.VILLAGER_HAPPY, Material.EMERALD, 14);
		addParticle(ChatColor.GOLD + "Heart", Particle.HEART, Material.APPLE, 15);
		addParticle(ChatColor.GOLD + "Spark", Particle.LAVA, Material.LAVA_BUCKET, 16);
		addParticle(ChatColor.GOLD + "Black Smoke", Particle.SMOKE_LARGE, Material.GRAY_WOOL, 17);
		addParticle(ChatColor.GOLD + "Slime", Particle.SNEEZE, Material.SLIME_BALL, 18);
		addParticle(ChatColor.GOLD + "Music note", Particle.NOTE, Material.MUSIC_DISC_CAT, 19);
		addParticle(ChatColor.GOLD + "Totem", Particle.TOTEM, Material.TOTEM_OF_UNDYING, 20);
		addParticle(ChatColor.GOLD + "Purple sparkle", Particle.SPELL_WITCH, Material.REDSTONE, 0);
		
		ItemMeta meta = cancel.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "Cancel all particles");
		cancel.setItemMeta(meta);
		particleInv.setItem(35, cancel);
	}
	
	static void addParticle(String name, Particle particle, Material material, int slot) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		particleNames.add(new Pair<ItemStack, Particle>(item, particle));
		particleInv.setItem(slot, item);
	}
	
	public static void removeP(Player player) {
		ArrayList<Particles> p = new ArrayList<Particles>();
		for(Particles particle: particles) {
			if(particle.player.equals(player)) {
				p.add(particle);
			}
		}
		particles.removeAll(p);
	}
	
	public static void removeG(BuildBattle game) {
		ArrayList<Particles> removers = new ArrayList<Particles>();
		for(Particles particle: particles) {
			if(Pair.getKeys(game.plots).contains(particle.player)) {
				removers.add(particle);
			}
		}
		particles.removeAll(removers);
	}
	
	public void particle() {
		new BukkitRunnable() {

			@Override
			public void run() {
				Main.getPlugin(Main.class).getServer().getWorld("world").spawnParticle(particle, location, 1);
				if(!particles.contains(p)) {
					this.cancel();
				}
			}
			
		}.runTaskTimer(Main.getPlugin(Main.class), 0, 5);
	}
	
	static ArrayList<Particles> getParticles(Player player) {
		ArrayList<Particles> p = new ArrayList<Particles>();
		for(Particles particle: particles) {
			if(particle.player.equals(player)) {
				p.add(particle);
			}
		}
		return p;
	}
	
}
