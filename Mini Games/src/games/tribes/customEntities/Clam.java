package games.tribes.customEntities;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import games.tribes.CustomEntity;
import net.minecraft.server.v1_16_R3.EntitySilverfish;
import net.minecraft.server.v1_16_R3.EntityTypes;

public class Clam extends CustomEntity {

	Block block;
	
	public Clam(Location location) {
		super(location, new CustomClam());
		block = location.getBlock();
		block.setType(Material.STONE_BUTTON);
		biome = Biome.BEACH;
	}
	
	public static class CustomClam extends EntitySilverfish {
		
		public CustomClam() {
			super(EntityTypes.SILVERFISH, CustomEntity.world);
			created = false;
			entityNum = 2;
			this.setNoAI(true);
			this.setInvisible(true);
			this.setInvulnerable(true);
		}
	}
	
	@EventHandler
	public void breakb(BlockBreakEvent event) {
		Block block = event.getBlock();
		if(block.equals(this.block)) {
			kill();
			BlockBreakEvent.getHandlerList().unregister(this);
		}
	}
	
	@Override
	public void die(EntityDeathEvent event) {
		return;
	}
}
