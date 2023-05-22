package games.tribes.customEntities;

import org.bukkit.Location;
import org.bukkit.block.Biome;

import games.tribes.CustomEntity;
import net.minecraft.server.v1_16_R3.EntityCod;
import net.minecraft.server.v1_16_R3.EntityTypes;

public class Cod extends CustomEntity {
	
	public Cod(Location location) {
		super(location, new CustomCod());
		created = true;
		biome = Biome.OCEAN;
	}

	public static class CustomCod extends EntityCod {
		
		public CustomCod() {
			super(EntityTypes.COD, CustomEntity.world);
		}
		
	}
	
}
