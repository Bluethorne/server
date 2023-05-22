package games.flappyChicken;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import games.main.Main;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;

public class Pillar extends ArrayList<PillarPart>{

	private static final long serialVersionUID = 1L;
	int size;
	Type type;
	FlappyChicken game;
	Location location;
	static int startX = 6;
	static int endX = 27;
	
	public Pillar(int size, Type type, FlappyChicken game) {
		this.game = game;
		this.size = size;
		this.type = type;
		for(int count = 0; count < size; count++) {
			add(new PillarPart(count, this));
		}
	}
	
	public void spawn() {
		for(PillarPart part: this) {
			double y;
			int totalY;
			if(type.equals(Type.UP)) {
				totalY = 119;
				y = 119 + (Math.abs(part.order - size) * 0.5);
			} else {
				totalY = 130;
				y = 130 - (Math.abs(part.order - size) * 0.5);
				if(part.order == 0) {
					y += 0.25;
				}
			}
			location = new Location(FlappyChicken.world, startX, totalY, -132);
			Location newLoc = new Location(FlappyChicken.world, startX, y, -132);
			part.setLocation(newLoc.getX(), newLoc.getY(), newLoc.getZ(), 0, 0);
			FlappyChicken.worldnms.addEntity(part);
		}
		for(Player player: Main.getPlugin(Main.class).getServer().getOnlinePlayers()) {
			if(!player.equals(game.player)) {
				sendKillPackets(player);
			}
		}
	}
	
	public void movementTick() {
		if(this.get(0).getBukkitEntity().getLocation().getX() >= endX) {
			kill();
			game.pillars.remove(this);
			if(type.equals(Type.UP)) {
				game.spawnNewPillars();
				game.speed = true;
			}
		}
	}
	
	public void kill() {
		for(PillarPart part: this) {
			part.getBukkitEntity().remove();
		}
	}
	
	public void sendKillPackets(Player player) {
		for(PillarPart part: this) {
			PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(part.getId());
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		
		}
	}
	
	public enum Type {
		UP,
		DOWN;
	}
	
}
