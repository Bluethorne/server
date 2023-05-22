package games.flappyChicken;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import games.main.Main;
import net.minecraft.server.v1_16_R3.EntityArmorStand;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;

public class Cloud extends EntityArmorStand {

	CloudType type;
	FlappyChicken game;
	static int startX = 6;
	static int endX = 27;
	int currentX;
	static Location startLoc = new Location(FlappyChicken.world, startX, 126.0, -133.5);
	PacketPlayOutEntityDestroy killPacket = new PacketPlayOutEntityDestroy(this.getId());
	
	public Cloud(FlappyChicken game) {
		super(EntityTypes.ARMOR_STAND, FlappyChicken.worldnms);
		this.game = game;
		type = CloudType.getRandom();
		setInvisible(true);
		setInvulnerable(true);
		((ArmorStand) getBukkitEntity()).getEquipment().setHelmet(new ItemStack(Material.SPONGE));
		setLocation(startLoc.getX(), startLoc.getY(), startLoc.getZ(), type.yaw, 0);
		spawn();		
	}
	
	public void spawn() {
		FlappyChicken.worldnms.addEntity(this);
		for(Player player: Main.getPlugin(Main.class).getServer().getOnlinePlayers()) {
			if(!player.equals(game.player)) {
				sendKillPacket(player);
			}
		}
	}
	
	public void sendKillPacket(Player player) {
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(killPacket);
	}
	
	@Override
	public void movementTick() {
		this.getBukkitEntity().setVelocity(new Vector(0.1, 0, 0));
		if(this.getBukkitEntity().getLocation().getX() >= endX) {
			type = CloudType.getRandom();
			game.cloud = new Cloud(game);
			this.getBukkitEntity().remove();
		}
		super.movementTick();
	}
	
	public enum CloudType {
		
		A(0),
		B(180);
		
		public float yaw;
		
		private CloudType(float yaw) {
			this.yaw = yaw;
		}
		
		public static CloudType getRandom() {
			int random = (int) (Math.random() * values().length + 1);
			return values()[random - 1];
		}
	}

}
