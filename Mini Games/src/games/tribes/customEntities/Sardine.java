package games.tribes.customEntities;

import org.bukkit.Location;
import org.bukkit.block.Biome;

import games.tribes.CustomEntity;
import net.minecraft.server.v1_16_R3.EntityCod;
import net.minecraft.server.v1_16_R3.EntityTypes;

public class Sardine extends CustomEntity {

	public Sardine(Location location) {
		super(location, new CustomSardine());
		biome = Biome.OCEAN;
	}
	
	public static class CustomSardine extends EntityCod {
		
		public CustomSardine() {
			super(EntityTypes.COD, CustomEntity.world);
			created = false;
			entityNum = 1;
		}
	}

}
