package games.flappyChicken;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import games.main.Main;
import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.EntityChicken;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;

public class Chicken extends EntityChicken {
	
	FlappyChicken game;
	PacketPlayOutEntityDestroy killPacket = new PacketPlayOutEntityDestroy(this.getId());
	
	public Chicken(FlappyChicken game) {
		super(EntityTypes.CHICKEN, FlappyChicken.worldnms);
		this.game = game;
		((CraftLivingEntity) this.getBukkitEntity()).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0);
		this.setLocation(FlappyChicken.chickenLoc.getX(), FlappyChicken.chickenLoc.getY(), FlappyChicken.chickenLoc.getZ(), 90 * 256 / 360, 0);
		this.setInvulnerable(true);
		this.setSilent(true);
	}
	
	public void jump() {
		this.getBukkitEntity().setVelocity(new Vector(0, 0.25, 0));
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
	public void collide(Entity entity) {
		game.finish();
	}
	
	@Override
	public void movementTick() {
		double y = this.locY();
		if(y < 119 || y > 130) {
			game.finish();
		}
		Location location = this.getBukkitEntity().getLocation();
		location.setYaw(90);
		this.getBukkitEntity().teleport(location);
		super.movementTick();
	}
}
