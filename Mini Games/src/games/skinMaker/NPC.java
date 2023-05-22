package games.skinMaker;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import games.main.Main;
import net.minecraft.server.v1_16_R3.DataWatcher;
import net.minecraft.server.v1_16_R3.DataWatcherRegistry;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntity.PacketPlayOutEntityLook;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_16_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_16_R3.PlayerInteractManager;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;

public class NPC extends EntityPlayer {
	
	private Location location;
	static ArrayList<NPC> npcs = new ArrayList<NPC>();
	Player player;
	Skin skin;
	boolean despawned = true;
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	public Location getLocation() {
		return location;
	}
	
	private NPC(GameProfile profile) {
		super(((CraftServer) Main.getPlugin(Main.class).getServer()).getServer(), ((CraftWorld) Main.getPlugin(Main.class).getServer().getWorld("world")).getHandle(), profile, new PlayerInteractManager(((CraftWorld) Main.getPlugin(Main.class).getServer().getWorld("world")).getHandle()));
	}
	
	public void spawn(Player player) {
		despawned = false;
		this.player = player;
		this.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		PacketPlayOutPlayerInfo packet1 = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, this);
		PacketPlayOutNamedEntitySpawn packet2 = new PacketPlayOutNamedEntitySpawn(this);
		DataWatcher watcher = this.getDataWatcher();
		watcher.set(DataWatcherRegistry.a.a((byte) 16), (byte) 127);
        PacketPlayOutEntityMetadata packet3 = new PacketPlayOutEntityMetadata(this.getId(), watcher, true);
        PacketPlayOutEntityHeadRotation packet4 = new PacketPlayOutEntityHeadRotation(this, (byte) (location.getYaw() * 256 / 360));
        PacketPlayOutPlayerInfo packet5 = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, this);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet1);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet2);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet3);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet4);
		new BukkitRunnable() {

			@Override
			public void run() {
				((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet5);
			}
		}.runTaskLater(Main.getPlugin(Main.class), 50);
		npcs.add(this);
	}
	
	static NPC newNPC(Skin skin, String name, Location location) {
		GameProfile profile = new GameProfile(UUID.randomUUID(), name);
		if(skin.base64 != null && skin.signature != null) {
			profile.getProperties().put("textures", new Property("textures", skin.base64, skin.signature));
		}
		NPC npc = new NPC(profile);
		npc.setLocation(location);
		npc.skin = skin;
		skin.npc = npc;
		return npc;
	}
	
	public static NPC getNPCFromId(int id) {
		for(NPC npc: npcs) {
			int id2 = npc.getId();
			if(id == id2) {
				return npc;
			}
		}
		return null;
	}
	
	public void remove(boolean deleteFile) {
		despawned = true;
		if(deleteFile) {
			skin.file.delete();
		}
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, this);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        PacketPlayOutEntityDestroy packet1 = new PacketPlayOutEntityDestroy(this.getId());
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet1);
	}
	
	public void rotate(float yaw) {
		location.setYaw(yaw);
		PacketPlayOutEntityLook packet2 = new PacketPlayOutEntityLook(this.getId(), (byte) (yaw * 256 / 360), (byte) 0, true);
		PacketPlayOutEntityHeadRotation packet = new PacketPlayOutEntityHeadRotation(this, (byte) (yaw * 256 / 360));
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet2);
	}
	
	public void changeSkin() {
		changeSkin(skin.base64, skin.signature);
	}
	
	public void changeSkin(String base64, String signature) {
		this.getProfile().getProperties().clear();
		this.getProfile().getProperties().put("textures", new Property("textures", base64, signature));
		PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, this);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        PacketPlayOutEntityDestroy packet1 = new PacketPlayOutEntityDestroy(this.getId());
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet1);
        PacketPlayOutPlayerInfo packet2 = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, this);
		PacketPlayOutNamedEntitySpawn packet3 = new PacketPlayOutNamedEntitySpawn(this);
		DataWatcher watcher = this.getDataWatcher();
		watcher.set(DataWatcherRegistry.a.a((byte) 16), (byte) 127);
        PacketPlayOutEntityMetadata packet4 = new PacketPlayOutEntityMetadata(this.getId(), watcher, true);
        PacketPlayOutEntityHeadRotation packet5 = new PacketPlayOutEntityHeadRotation(this, (byte) (location.getYaw() * 256 / 360));
        PacketPlayOutPlayerInfo packet6 = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, this);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet2);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet3);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet4);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet5);
		new BukkitRunnable() {

			@Override
			public void run() {
				((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet6);
			}
		}.runTaskLater(Main.getPlugin(Main.class), 50);
	}
}
