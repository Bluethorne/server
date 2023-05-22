package games.flappyChicken;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Hex;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import games.main.Main;
import net.minecraft.server.v1_16_R3.EntityArmorStand;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;

public class ReplayGUI implements Listener{
	
	Player player;
	public static Location replayLocation = new Location(FlappyChicken.world, 22.0, 122.0, -138.0);
	public static double distanceToBoard;
	Button quit = new Button(new Location(FlappyChicken.world, 0, 0, 0), Material.EGG);
	Button play = new Button(new Location(FlappyChicken.world, 0, 0, 0), Material.ENDER_EYE);
	Pointer pointer = new Pointer();
	
	public ReplayGUI(Player player) {
		Main.getPlugin(Main.class).getServer().getPluginManager().registerEvents(this, Main.getPlugin(Main.class));
		this.player = player;
	}
	
	@EventHandler
	public void move(PlayerMoveEvent event) {
		if(event.getPlayer().equals(player)) {
			Location location = event.getTo();
			float yaw = location.getYaw();
			float pitch = location.getPitch();
			double x = distanceToBoard * Math.atan(yaw);
			double y = distanceToBoard * Math.atan(pitch);
			double z = pointer.locZ();
			pointer.setLocation(x, y, z, 0, 0);
			event.setTo(event.getFrom());
		}
	}
	
	@EventHandler
	public void click(PlayerInteractEvent event) {
		if(event.getPlayer().equals(player)) {
			if(event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
				double z = pointer.locZ();
				if(z == quit.locZ()) {
					try {
						player.setResourcePack("https://storage.live.com/items/4F14B5D8A3513AFD%2150985?authkey=APW0lkmBnKTr36M", Hex.decodeHex("0000000000000000000000000000000000000000".toCharArray()));
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if(z == play.locZ()) {
					
				}
			}
		}
	}
	
	public void stop() {
		PlayerMoveEvent.getHandlerList().unregister(this);
		PlayerInteractEvent.getHandlerList().unregister(this);
		quit.getBukkitEntity().remove();
		play.getBukkitEntity().remove();
		pointer.getBukkitEntity().remove();
	}
	
	public static class Button extends EntityArmorStand {
		
		PacketPlayOutEntityDestroy killPacket = new PacketPlayOutEntityDestroy(this.getId());
		
		public Button(Location location, Material material) {
			super(EntityTypes.ARMOR_STAND, FlappyChicken.worldnms);
			setLocation(location.getX(), location.getY(), location.getZ(), 0, 0);
			((ArmorStand) getBukkitEntity()).getEquipment().setHelmet(new ItemStack(material));
			FlappyChicken.worldnms.addEntity(this);
			for(Player player: Main.getPlugin(Main.class).getServer().getOnlinePlayers()) {
				if(!player.equals(player)) {
					sendKillPacket(player);
				}
			}
		}
		
		public void sendKillPacket(Player player) {
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(killPacket);
		}
		
	}
	
	public static class Pointer extends EntityArmorStand {
		
		public static Material material = Material.EMERALD_BLOCK;
		PacketPlayOutEntityDestroy killPacket = new PacketPlayOutEntityDestroy(this.getId());
		
		public Pointer() {
			super(EntityTypes.ARMOR_STAND, FlappyChicken.worldnms);
			setLocation(replayLocation.getX(), replayLocation.getY(), replayLocation.getZ() - distanceToBoard, 0, 0);
			((ArmorStand) getBukkitEntity()).getEquipment().setHelmet(new ItemStack(material));
			setSmall(true);
			FlappyChicken.worldnms.addEntity(this);
			for(Player player: Main.getPlugin(Main.class).getServer().getOnlinePlayers()) {
				if(!player.equals(player)) {
					sendKillPacket(player);
				}
			}
		}
		
		public void sendKillPacket(Player player) {
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(killPacket);
		}
		
	}
	
}
