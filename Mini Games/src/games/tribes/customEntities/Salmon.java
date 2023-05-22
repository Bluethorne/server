package games.tribes.customEntities;

import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.scheduler.BukkitRunnable;

import games.tribes.CustomEntity;
import games.main.Main;
import net.minecraft.server.v1_16_R3.EntitySalmon;
import net.minecraft.server.v1_16_R3.EntityTypes;

public class Salmon extends CustomEntity{

	Location spawnLocation;
	
	public Salmon(Location location) {
		super(location, new CustomSalmon());
		spawnLocation = location;
		swimDown();
		biome = Biome.RIVER;
	}

	public static class CustomSalmon extends EntitySalmon {
		
		public CustomSalmon() {
			super(EntityTypes.SALMON, CustomEntity.world);
			created = true;
		}
		
	}
	
	void swimUp() {
		new BukkitRunnable() {

			@Override
			public void run() {
				
			}
			
		}.runTaskLater(Main.getPlugin(Main.class), 876000);
	}
	
	void swimDown() {
		
	}
	
}
