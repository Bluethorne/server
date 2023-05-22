package games.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;

import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.Blocks;
import net.minecraft.server.v1_16_R3.ChunkCoordIntPair;
import net.minecraft.server.v1_16_R3.DefinedStructure;
import net.minecraft.server.v1_16_R3.DefinedStructureInfo;
import net.minecraft.server.v1_16_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.WorldServer;

public class Structures {
	
	static DefinedStructure loadSingleStructure(File source) throws FileNotFoundException, IOException {
	    DefinedStructure structure = new DefinedStructure();
	    structure.b(NBTCompressedStreamTools.a(new FileInputStream(source)));
	    return structure;
	}
	
	public static void insertSingleStructure(DefinedStructure structure, Location startEdge) {
	    WorldServer world = ((CraftWorld) startEdge.getWorld()).getHandle();
		DefinedStructureInfo structInfo = new DefinedStructureInfo().a(true).a((ChunkCoordIntPair) null).c(false).a(new Random());
		structure.a(world, new BlockPosition(startEdge.getBlockX(), startEdge.getBlockY(), startEdge.getBlockZ()), structInfo, new Random());
	}
	
	public static void pasteStructure(File file, Location location) {
		try {
			insertSingleStructure(loadSingleStructure(file), location);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public static DefinedStructure createSingleStructure(Location[] corners) {
	    if (corners.length != 2) throw new IllegalArgumentException("An area needs to be set up by exactly 2 opposite edges!");
	    Location[] normalized = normalizeEdges(corners[0], corners[1]);
	    WorldServer world = ((CraftWorld) normalized[0].getWorld()).getHandle();
	    int[] dimensions = getDimensions(normalized);
	    if (dimensions[0] > 32 || dimensions[1] > 32 || dimensions[2] > 32) throw new IllegalArgumentException("A single structure can only be 32x32x32!");
	    DefinedStructure structure = new DefinedStructure();
	    structure.a(world, new BlockPosition(normalized[0].getBlockX(), normalized[0].getBlockY(), normalized[0].getBlockZ()), new BlockPosition(dimensions[0], dimensions[1], dimensions[2]), true, Blocks.STRUCTURE_VOID);
	    return structure;
	}
	
	static void saveSingleStructure(DefinedStructure structure, File destination) throws FileNotFoundException, IOException {
	    NBTTagCompound fileTag = new NBTTagCompound();
	    fileTag = structure.a(fileTag);
	    NBTCompressedStreamTools.a(fileTag, new FileOutputStream(new File(destination + ".nbt")));
	}
	
	public static void saveStructure(File file, Location[] locations) {
		try {
			saveSingleStructure(createSingleStructure(locations), file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static Location[] normalizeEdges(Location startBlock, Location endBlock) {
	    int xMin, xMax, yMin, yMax, zMin, zMax;
	    if (startBlock.getBlockX() <= endBlock.getBlockX()) {
	        xMin = startBlock.getBlockX();
	        xMax = endBlock.getBlockX();
	    } else {
	        xMin = endBlock.getBlockX();
	        xMax = startBlock.getBlockX();
	    }
	    if (startBlock.getBlockY() <= endBlock.getBlockY()) {
	        yMin = startBlock.getBlockY();
	        yMax = endBlock.getBlockY();
	    } else {
	        yMin = endBlock.getBlockY();
	        yMax = startBlock.getBlockY();
	    }
	    if (startBlock.getBlockZ() <= endBlock.getBlockZ()) {
	        zMin = startBlock.getBlockZ();
	        zMax = endBlock.getBlockZ();
	    } else {
	        zMin = endBlock.getBlockZ();
	        zMax = startBlock.getBlockZ();
	    }
	    return new Location[] { new Location(startBlock.getWorld(), xMin, yMin, zMin), new Location(startBlock.getWorld(), xMax, yMax, zMax) };
	}
	
	static int[] getDimensions(Location[] corners) {
	    if (corners.length != 2) throw new IllegalArgumentException("An area needs to be set up by exactly 2 opposite edges!");
	    return new int[] { corners[1].getBlockX() - corners[0].getBlockX() + 1, corners[1].getBlockY() - corners[0].getBlockY() + 1, corners[1].getBlockZ() - corners[0].getBlockZ() + 1 };
	}
}
