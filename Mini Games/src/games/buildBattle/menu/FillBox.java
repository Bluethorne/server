package games.buildBattle.menu;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import games.main.Main;

public class FillBox {
	
	public static void fillWall(Integer Startx, Integer Startz, Material material) {
		for(int x = Startx; x < Startx + 32; x++) {
			for(int y = 200; y < 232; y++) {
				for(int z = Startz; z < Startz + 32; z++) {
					if(x == Startx || x == Startx + 31 || y == 200 || y == 231 || z == Startz || z == Startz + 31) {
						if(!isWall(material)) {
							Block block = new Location(Main.getPlugin(Main.class).getServer().getWorld("world"), x, y, z).getBlock();
							block.setType(material);
						} else {
							if(y != 200 && y != 231) {
								Block block = new Location(Main.getPlugin(Main.class).getServer().getWorld("world"), x, y, z).getBlock();
								block.setType(material);
							}
						}
					}
				}
			}
		}
	}
	
	public static boolean isMaterialOk(Material material) {
		if(!material.isSolid()) {
			return false;
		} else if(material.hasGravity()) {
			return false;
		}
		switch (material) {
		case OAK_SLAB:
		case SPRUCE_SLAB:
		case BIRCH_SLAB:
		case JUNGLE_SLAB:
		case ACACIA_SLAB:
		case DARK_OAK_SLAB:
		case STONE_SLAB:
		case SMOOTH_STONE_SLAB:
		case SANDSTONE_SLAB:
		case CUT_SANDSTONE_SLAB:
		case PETRIFIED_OAK_SLAB:
		case COBBLESTONE_SLAB:
		case BRICK_SLAB:
		case STONE_BRICK_SLAB:
		case NETHER_BRICK_SLAB:
		case QUARTZ_SLAB:
		case RED_SANDSTONE_SLAB:
		case CUT_RED_SANDSTONE_SLAB:
		case PURPUR_SLAB:
		case PRISMARINE_SLAB:
		case PRISMARINE_BRICK_SLAB:
		case DARK_PRISMARINE_SLAB:
		case PURPUR_STAIRS:
		case OAK_STAIRS:
		case COBBLESTONE_STAIRS:
		case BRICK_STAIRS:
		case STONE_BRICK_STAIRS:
		case SANDSTONE_STAIRS:
		case SPRUCE_STAIRS:
		case BIRCH_STAIRS:
		case JUNGLE_STAIRS:
		case QUARTZ_STAIRS:
		case ACACIA_STAIRS:
		case DARK_OAK_STAIRS:
		case PRISMARINE_STAIRS:
		case PRISMARINE_BRICK_STAIRS:
		case DARK_PRISMARINE_STAIRS:
		case RED_SANDSTONE_STAIRS:
		case POLISHED_GRANITE_STAIRS:
		case SMOOTH_RED_SANDSTONE_STAIRS:
		case MOSSY_STONE_BRICK_STAIRS:
		case POLISHED_DIORITE_STAIRS:
		case MOSSY_COBBLESTONE_STAIRS:
		case END_STONE_BRICK_STAIRS:
		case STONE_STAIRS:
		case SMOOTH_SANDSTONE_STAIRS:
		case SMOOTH_QUARTZ_STAIRS:
		case GRANITE_STAIRS:
		case ANDESITE_STAIRS:
		case RED_NETHER_BRICK_STAIRS:
		case POLISHED_ANDESITE_STAIRS:
		case DIORITE_STAIRS:
		case POLISHED_GRANITE_SLAB:
		case SMOOTH_RED_SANDSTONE_SLAB:
		case MOSSY_STONE_BRICK_SLAB:
		case POLISHED_DIORITE_SLAB:
		case MOSSY_COBBLESTONE_SLAB:
		case END_STONE_BRICK_SLAB:
		case SMOOTH_SANDSTONE_SLAB:
		case SMOOTH_QUARTZ_SLAB:
		case GRANITE_SLAB:
		case ANDESITE_SLAB:
		case DIORITE_SLAB:
		case RED_NETHER_BRICK_SLAB:
		case POLISHED_ANDESITE_SLAB:
		case OAK_LEAVES:
		case SPRUCE_LEAVES:
		case BIRCH_LEAVES:
		case JUNGLE_LEAVES:
		case ACACIA_LEAVES:
		case DARK_OAK_LEAVES:
		case CHEST:
		case TRAPPED_CHEST:
		case ENDER_CHEST:
		case CACTUS:
		case ENCHANTING_TABLE:
		case END_PORTAL_FRAME:
		case SHULKER_BOX:
		case RED_SHULKER_BOX:
		case ORANGE_SHULKER_BOX:
		case YELLOW_SHULKER_BOX:
		case LIME_SHULKER_BOX:
		case GREEN_SHULKER_BOX:
		case LIGHT_BLUE_SHULKER_BOX:
		case CYAN_SHULKER_BOX:
		case BLUE_SHULKER_BOX:
		case PURPLE_SHULKER_BOX:
		case PINK_SHULKER_BOX:
		case MAGENTA_SHULKER_BOX:
		case BROWN_SHULKER_BOX:
		case WHITE_SHULKER_BOX:
		case LIGHT_GRAY_SHULKER_BOX:
		case GRAY_SHULKER_BOX:
		case BLACK_SHULKER_BOX:
		case DEAD_BRAIN_CORAL:
		case DEAD_BRAIN_CORAL_FAN:
		case DEAD_TUBE_CORAL:
		case DEAD_TUBE_CORAL_FAN:
		case DEAD_FIRE_CORAL:
		case DEAD_FIRE_CORAL_FAN:
		case DEAD_BUBBLE_CORAL:
		case DEAD_BUBBLE_CORAL_FAN:
		case DEAD_HORN_CORAL:
		case DEAD_HORN_CORAL_FAN:
		case OAK_SIGN:
		case SPRUCE_SIGN:
		case BIRCH_SIGN:
		case JUNGLE_SIGN:
		case ACACIA_SIGN:
		case DARK_OAK_SIGN:
		case RED_BED:
		case ORANGE_BED:
		case YELLOW_BED:
		case LIME_BED:
		case GREEN_BED:
		case LIGHT_BLUE_BED:
		case CYAN_BED:
		case BLUE_BED:
		case PURPLE_BED:
		case PINK_BED:
		case MAGENTA_BED:
		case BROWN_BED:
		case LIGHT_GRAY_BED:
		case GRAY_BED:
		case WHITE_BED:
		case BLACK_BED:
		case RED_BANNER:
		case ORANGE_BANNER:
		case YELLOW_BANNER:
		case LIME_BANNER:
		case GREEN_BANNER:
		case LIGHT_BLUE_BANNER:
		case CYAN_BANNER:
		case BLUE_BANNER:
		case PURPLE_BANNER:
		case PINK_BANNER:
		case MAGENTA_BANNER:
		case BROWN_BANNER:
		case LIGHT_GRAY_BANNER:
		case GRAY_BANNER:
		case WHITE_BANNER:
		case BLACK_BANNER:
		case CAMPFIRE:
		case LANTERN:
		case BELL:
		case STONECUTTER:
		case GRINDSTONE:
		case BREWING_STAND:
		case CAULDRON:
		case CAKE:
		case COMPOSTER:
		case BEACON:
		case TURTLE_EGG:
		case CONDUIT:
		case BAMBOO:
		case DISPENSER:
		case DROPPER:
		case PISTON:
		case STICKY_PISTON:
		case TNT:
		case STONE_PRESSURE_PLATE:
		case OAK_PRESSURE_PLATE:
		case SPRUCE_PRESSURE_PLATE:
		case BIRCH_PRESSURE_PLATE:
		case JUNGLE_PRESSURE_PLATE:
		case ACACIA_PRESSURE_PLATE:
		case DARK_OAK_PRESSURE_PLATE:
		case LIGHT_WEIGHTED_PRESSURE_PLATE:
		case HEAVY_WEIGHTED_PRESSURE_PLATE:
		case DAYLIGHT_DETECTOR:
		case OAK_TRAPDOOR:
		case SPRUCE_TRAPDOOR:
		case BIRCH_TRAPDOOR:
		case JUNGLE_TRAPDOOR:
		case ACACIA_TRAPDOOR:
		case DARK_OAK_TRAPDOOR:
		case OAK_FENCE_GATE:
		case SPRUCE_FENCE_GATE:
		case BIRCH_FENCE_GATE:
		case JUNGLE_FENCE_GATE:
		case DARK_OAK_FENCE_GATE:
		case ACACIA_FENCE_GATE:
		case IRON_TRAPDOOR:
		case IRON_DOOR:
		case OAK_DOOR:
		case SPRUCE_DOOR:
		case BIRCH_DOOR:
		case JUNGLE_DOOR:
		case ACACIA_DOOR:
		case DARK_OAK_DOOR:
		case HOPPER:
		case OBSERVER:
		case LECTERN:
			return false;
		default:
			return true;
		}
	}
	
	static boolean isWall(Material material) {
		switch(material) {
		case OAK_FENCE:
		case SPRUCE_FENCE:
		case BIRCH_FENCE:
		case JUNGLE_FENCE:
		case ACACIA_FENCE:
		case DARK_OAK_FENCE:
		case IRON_BARS:
		case GLASS_PANE:
		case RED_STAINED_GLASS_PANE:
		case ORANGE_STAINED_GLASS_PANE:
		case YELLOW_STAINED_GLASS_PANE:
		case LIME_STAINED_GLASS_PANE:
		case GREEN_STAINED_GLASS_PANE:
		case LIGHT_BLUE_STAINED_GLASS_PANE:
		case BLUE_STAINED_GLASS_PANE:
		case CYAN_STAINED_GLASS_PANE:
		case PURPLE_STAINED_GLASS_PANE:
		case PINK_STAINED_GLASS_PANE:
		case MAGENTA_STAINED_GLASS_PANE:
		case WHITE_STAINED_GLASS_PANE:
		case BROWN_STAINED_GLASS_PANE:
		case LIGHT_GRAY_STAINED_GLASS_PANE:
		case GRAY_STAINED_GLASS_PANE:
		case BLACK_STAINED_GLASS_PANE:
		case COBBLESTONE_WALL:
		case MOSSY_COBBLESTONE_WALL:
		case BRICK_WALL:
		case PRISMARINE_WALL:
		case RED_SANDSTONE_WALL:
		case MOSSY_STONE_BRICK_WALL:
		case GRANITE_WALL:
		case STONE_BRICK_WALL:
		case NETHER_BRICK_WALL:
		case ANDESITE_WALL:
		case RED_NETHER_BRICK_WALL:
		case SANDSTONE_WALL:
		case END_STONE_BRICK_WALL:
		case DIORITE_WALL:
			return true;
		default:
			return false;
		}
	}
	
}
