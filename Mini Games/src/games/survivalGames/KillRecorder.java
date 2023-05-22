package games.survivalGames;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import games.game.PlayerWinEvent;
import games.main.Array;
import games.main.Main;
import games.main.Pair;
import games.main.Scoreboards;
import net.minecraft.server.v1_16_R3.DataWatcher;
import net.minecraft.server.v1_16_R3.DataWatcherRegistry;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.EntityPose;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_16_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_16_R3.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_16_R3.PlayerInteractManager;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_16_R3.PacketPlayOutWorldBorder.EnumWorldBorderAction;

public class KillRecorder implements Listener {
	
	public ArrayList<Player> players = new ArrayList<Player>();
	public ArrayList<Pair<Player, Integer>> kills = new ArrayList<Pair<Player, Integer>>();
	public ArrayList<Player> dead = new ArrayList<Player>();
	SurvivalGames game;
	public ArrayList<EntityPlayer> fake = new ArrayList<EntityPlayer>();
	
	public KillRecorder(ArrayList<Player> players, SurvivalGames game) {
		this.players = players;
		this.game = game;
		for(Player player: players) {
			kills.add(new Pair<Player, Integer>(player, 0));
		}
		Main.getPlugin(Main.class).getServer().getPluginManager().registerEvents(this, Main.getPlugin(Main.class));
	}
	
	@EventHandler
	public void kill(PlayerDeathEvent event) {
		if(event.getEntity() instanceof Player) {
			Player player = event.getEntity();
			if(players.contains(player)) {
				if(players.contains(player.getKiller())) {
					event.setDeathMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " was killed by " + ChatColor.YELLOW + player.getKiller().getName());
					((Player) player.getKiller()).giveExp(10);
					Pair.getPairFromKey((Player) player.getKiller(), kills).get(0).setValue(Pair.getPairFromKey((Player) player.getKiller(), kills).get(0).getValue() + 1);
					String supply;
					if(game.supply.x1 != null && game.supply.z1 != null) {
						supply = "X: " + game.supply.x1 + " Z: " + game.supply.z1;
					} else {
						supply = "No supply drop";
					}
					Scoreboards.addScoreboardSurvivalGames(((Player) player.getKiller()), Pair.getValue(((Player) player.getKiller()), kills).get(0), supply, game.shrinker.shrinking);
				} else {
					event.setDeathMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " has died");
				}
				Location location = player.getLocation();
				location.setY(location.getY() + 1);
				dead.add(player);
		    	for(ItemStack stack: event.getDrops()) {
		    		game.noInteract.droppedItems.add(Main.getPlugin(Main.class).getServer().getWorld("world").dropItemNaturally(location, stack));
		    	}
		    	event.getDrops().clear();
		    	new BukkitRunnable() {

					@Override
					public void run() {
						player.spigot().respawn();
						player.teleport(location);
						player.setAllowFlight(true);
						player.setFlying(true);
						player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false, false));
						player.getInventory().setItem(8, new Compass(player));
						PacketPlayOutWorldBorder border = new PacketPlayOutWorldBorder(game.shrinker.border, EnumWorldBorderAction.INITIALIZE);
						((CraftPlayer) player).getHandle().playerConnection.sendPacket(border);
						for(EntityPlayer fake: fake) {
							PacketPlayOutPlayerInfo packet1 = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, fake);
							PacketPlayOutNamedEntitySpawn packet2 = new PacketPlayOutNamedEntitySpawn(fake);
							DataWatcher watcher = fake.getDataWatcher();
							watcher.set(DataWatcherRegistry.a.a((byte) 16), (byte) 127);
							PacketPlayOutEntityMetadata packet3 = new PacketPlayOutEntityMetadata(fake.getId(), watcher, true);
							PacketPlayOutEntityHeadRotation packet4 = new PacketPlayOutEntityHeadRotation(fake, (byte) (location.getYaw() * 256 / 360));
							PacketPlayOutPlayerInfo packet5 = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, fake);
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
						}
						if(!check()) {
							showDead(player);
						}
					}
		    	
		    	}.runTaskLater(Main.getPlugin(Main.class), 1);
			}
		}
	}
	
	void showDead(Player player) {
		Location location = player.getLocation();
		EntityPlayer nmsplayer = ((CraftPlayer) player).getHandle();
		GameProfile oldProfile = nmsplayer.getProfile();
		Property property = (Property) oldProfile.getProperties().get("textures").toArray()[0];
		String texture = property.getValue();
		String signature = property.getSignature();
		GameProfile newProfile = new GameProfile(UUID.randomUUID(), "§4Ship Arriving!");
		newProfile.getProperties().put("textures", new Property("textures", texture, signature));
		EntityPlayer newPlayer = new EntityPlayer(((CraftServer) Main.getPlugin(Main.class).getServer()).getServer(), ((CraftWorld) Main.getPlugin(Main.class).getServer().getWorld("world")).getHandle(), newProfile, new PlayerInteractManager(((CraftWorld) Main.getPlugin(Main.class).getServer().getWorld("world")).getHandle()));
		newPlayer.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		newPlayer.setPose(EntityPose.SLEEPING);
		fake.add(newPlayer);
		for(Player p: players) {
			PacketPlayOutPlayerInfo packet1 = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, newPlayer);
			PacketPlayOutNamedEntitySpawn packet2 = new PacketPlayOutNamedEntitySpawn(newPlayer);
			DataWatcher watcher = newPlayer.getDataWatcher();
			watcher.set(DataWatcherRegistry.a.a((byte) 16), (byte) 127);
			PacketPlayOutEntityMetadata packet3 = new PacketPlayOutEntityMetadata(newPlayer.getId(), watcher, true);
			PacketPlayOutEntityHeadRotation packet4 = new PacketPlayOutEntityHeadRotation(newPlayer, (byte) (location.getYaw() * 256 / 360));
			PacketPlayOutPlayerInfo packet5 = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, newPlayer);
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet1);
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet2);
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet3);
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet4);
			new BukkitRunnable() {
				
				@Override
				public void run() {
					((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet5);
				}
			}.runTaskLater(Main.getPlugin(Main.class), 50);
		}
		new Ship(Collections.min(Pair.getKeys(game.land.getLand().get(0))), game).collect(newPlayer.getBukkitEntity());
	}
	
	public void stop() {
		PlayerDeathEvent.getHandlerList().unregister(this);
	}
	
	public boolean check() {
		if(players.size() == dead.size() + 1) {
			ArrayList<Pair<Player, Integer>> order = new ArrayList<Pair<Player, Integer>>();
			ArrayList<Player> winnerl = Array.copy(players);
			winnerl.removeAll(dead);
			Player winner = winnerl.get(0);
			ArrayList<Player> temp = Array.copy(dead);
			temp.add(winner);
			Collections.reverse(temp);
			for(Player player: temp) {
				order.add(Pair.getPairFromKey(player, kills).get(0));
			}
			PlayerWinEvent e = new PlayerWinEvent(game, order, winner);
			Main.getPlugin(Main.class).getServer().getPluginManager().callEvent(e);
			return true;
		}
		return false;
	}
	
}
