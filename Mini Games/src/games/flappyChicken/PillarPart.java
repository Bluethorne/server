package games.flappyChicken;

import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_16_R3.EntityArmorStand;
import net.minecraft.server.v1_16_R3.EntityTypes;

public class PillarPart extends EntityArmorStand {

	public int order;
	Pillar pillar;
	
	public PillarPart(int order, Pillar pillar) {
		super(EntityTypes.ARMOR_STAND, FlappyChicken.worldnms);
		this.pillar = pillar;
		this.order = order;
		setInvisible(true);
		Material material = Material.SANDSTONE;
		if(order == 0) {
			material = Material.SANDSTONE_SLAB;
		}
		ItemStack head = new ItemStack(material);
		((ArmorStand) getBukkitEntity()).getEquipment().setHelmet(head);
	}
	
	@Override
	public void movementTick() {
		double takeY = 0;
		if (order == 0) {
			takeY = (5/16);
		}
		BoundingBox tempBB = this.getBukkitEntity().getBoundingBox();
		BoundingBox box = new BoundingBox(tempBB.getMinX() + (3/16), tempBB.getMinY() + (22/16), tempBB.getMinZ() + (3/16), tempBB.getMaxX() - (3/16), tempBB.getMaxY() - takeY, tempBB.getMaxZ() - (3/16));
		if(box.overlaps(pillar.game.chicken.getBukkitEntity().getBoundingBox())) {
			pillar.game.chicken.collide(this);
		}
		if(order == 0) {
			pillar.movementTick();
		}
		move();
		super.movementTick();
	}
	
	public void move() {
		double speed = 0;
		if(pillar.game.speed) {
			speed = (System.currentTimeMillis() - pillar.game.startTime) / 1000000;
		}
		speed += 0.1;
		getBukkitEntity().setVelocity(new Vector(speed, 0, 0));
	}
	
}
