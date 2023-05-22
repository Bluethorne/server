package games.tribes;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import games.main.Main;
import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.EntityArmorStand;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.World;

import games.tribes.customEntities.*;

public class CustomEntity implements Listener{
	
	public static ArrayList<CustomEntity> entities = new ArrayList<CustomEntity>();
	public static final World world = ((CraftWorld) Main.getPlugin(Main.class).getServer().getWorld("Tribes")).getHandle();
	protected Entity entity;
	EntityArmorStand armorStand;
	ItemStack item;
	protected static int entityNum;
	protected static boolean created;
	boolean killed = false;
	public static Biome biome;
	Class<?> clazz;
	
	public CustomEntity(Location location, Entity entity) {
		entities.add(this);
		this.entity = entity;
		entity.setLocation(location.getX(), location.getY(), location.getZ(), 0, 0);
		world.addEntity(entity);
		if(!created) {
			item = new ItemStack(Material.MUSIC_DISC_CAT);
			ItemMeta meta = item.getItemMeta();
			meta.setCustomModelData(entityNum);
			item.setItemMeta(meta);
			armorStand = new EntityArmorStand(EntityTypes.ARMOR_STAND, world);
			armorStand.setInvisible(true);
			armorStand.setNoGravity(true);
			((ArmorStand) armorStand.getBukkitEntity()).getEquipment().setHelmet(item);
			armorStand.setLocation(location.getX(), location.getY(), location.getZ(), 0, 0);
			world.addEntity(armorStand);
			teleport();
		}
		Main.getPlugin(Main.class).getServer().getPluginManager().registerEvents(this, Main.getPlugin(Main.class));
	}
	
	@EventHandler
	public void die(EntityDeathEvent event) {
		if(((CraftEntity) event.getEntity()).getHandle().equals(entity)) {
			kill();
		}
	}
	
	public void kill() {
		entity.killEntity();
		armorStand.getBukkitEntity().remove();
	}
	
	public void teleport() {
		new BukkitRunnable() {

			@Override
			public void run() {
				Location eloc = entity.getBukkitEntity().getLocation();
				Location loc = new Location(eloc.getWorld(), eloc.getX(), eloc.getY() - 1, eloc.getZ(), eloc.getYaw(), eloc.getPitch());
				armorStand.getBukkitEntity().teleport(loc);
				if(killed == true) {
					this.cancel();
				}
			}
			
		}.runTaskTimer(Main.getPlugin(Main.class), 0, 0);
	}
	
	public enum CustomEntitys {
		
		COD(Cod.class),
		SALMON(Salmon.class),
		SARDINE(Sardine.class),
		//WHALE,
		//OCTOPUS,
		//TURTLE,
		//SEAL,
		//DOLPHIN,
		//SHARK,
		//WALRUS,
		CLAM(Clam.class);
		//SEAGULL,
		//EAGLE,
		//CRAB,
		
		Class<? extends CustomEntity> clazz;
		
		private CustomEntitys(Class<? extends CustomEntity> clazz) {
			this.clazz = clazz;
		}
	}
	
}
