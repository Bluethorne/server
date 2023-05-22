package games.tribes;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Registry;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftNamespacedKey;

import games.tribes.CustomEntity.CustomEntitys;
import net.minecraft.server.v1_16_R3.BiomeBase;
import net.minecraft.server.v1_16_R3.BiomeSettingsMobs;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.EnumCreatureType;
import net.minecraft.server.v1_16_R3.RegistryGeneration;

public class Spawner{
	
	static ArrayList<BiomeBase> biomebase = new ArrayList<BiomeBase>();
	static HashMap<Biome, ArrayList<Class<? extends CustomEntity>>> biomes = new HashMap<Biome, ArrayList<Class<? extends CustomEntity>>>();
	
	public Spawner() {
		CustomEntitys[] enums = CustomEntity.CustomEntitys.values();
		for(CustomEntitys e: enums) {
			Class<? extends CustomEntity> clazz = e.clazz;
			try {
				Field field = e.clazz.getField("biome");
				Biome biome = (Biome) field.get(null);
				if(biomes.containsKey(biome)) {
					biomes.get(biome).add(clazz);
				} else {
					ArrayList<Class<? extends CustomEntity>> array = new ArrayList<Class<? extends CustomEntity>>();
					array.add(clazz);
					biomes.put(biome, array);
				}
				//@SuppressWarnings("unchecked")
				//Class<? extends Entity> entityclazz = (Class<? extends Entity>) clazz.getClasses()[0];
				//Method a = EntityTypes.class.getDeclaredMethod("a");
				//a.setAccessible(true);
				//a.invoke(null, entityclazz.getName().replaceAll("(.)([A-Z])", "$1_$2").toUpperCase(), entityclazz);
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e1) {
				e1.printStackTrace();
			}
		}
		for(Biome biomeBukkit: Registry.BIOME) {
			BiomeBase biome = RegistryGeneration.WORLDGEN_BIOME.get(CraftNamespacedKey.toMinecraft(biomeBukkit.getKey()));
			BiomeSettingsMobs.a builder = new BiomeSettingsMobs.a();
			//ArrayList<Class<? extends CustomEntity>> entitys = Spawner.biomes.get(biomeBukkit);	
			//for(Class<? extends CustomEntity> clazz: entitys) {
				builder.a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.PARROT, 10, 10, 10));builder.a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.PANDA, 10, 10, 10));//builder.a(EnumCreatureType.CREATURE, new BiomeSettingsMobs.c(EntityTypes.a(clazz.getName().replaceAll("(.)([A-Z])", "$1_$2").toUpperCase()).get(), 10, 10, 10));
			//}
			BiomeSettingsMobs settings = builder.b();
			try {
				Field field = BiomeBase.class.getDeclaredField("l");
				field.setAccessible(true);
				field.set(biome, settings);
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e1) {
				e1.printStackTrace();
			}
		}
	}
}
