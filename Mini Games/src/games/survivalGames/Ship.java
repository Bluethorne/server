package games.survivalGames;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import games.main.JSON;
import games.main.Main;
import net.minecraft.server.v1_16_R3.EntityArmorStand;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import net.minecraft.server.v1_16_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityTeleport;

public class Ship {
	
	public HashMap<EntityArmorStand, Vector> ship = new HashMap<EntityArmorStand, Vector>();
	ArrayList<EntityArmorStand> current = new ArrayList<EntityArmorStand>();
	Location location;
	int minX;
	SurvivalGames game;
	Vector direction;
	Location middle;
	Location ploc;
	Player player;
	Location start;
	Location stop;
	boolean cancel = false;
	
	public Ship(int minX, SurvivalGames game) {
		game.ships.add(this);
		this.minX = minX;
		this.game = game;
		@SuppressWarnings("unchecked")
		ArrayList<HashMap<String, Object>> ship = ((HashMap<String, ArrayList<HashMap<String, Object>>>) JSON.getJSON(new File("plugins/miniGames/SurvivalGames/Ship.json"))).get("Armor Stands");
		for(HashMap<String, Object> map: ship) {
			EntityArmorStand armorStand = new EntityArmorStand(EntityTypes.ARMOR_STAND, ((CraftWorld) Main.getPlugin(Main.class).getServer().getWorld("world")).getHandle());
			String head = (String) map.get("Head");
			Boolean small = (Boolean) map.get("Small");
			@SuppressWarnings("unchecked")
			HashMap<String, Double> v = (HashMap<String, Double>) map.get("Vector");
			Vector vector = new Vector(v.get("x"), v.get("y"), v.get("z"));
			armorStand.setSmall(small);
			Material material = null;
			try {
				material = (Material) Material.class.getField(head).get(null);
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				e.printStackTrace();
			}
			armorStand.setSlot(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(material)));
			armorStand.setInvisible(true);
			armorStand.setInvulnerable(true);
			armorStand.setNoGravity(true);
			armorStand.setCustomName(ChatSerializer.a("{\"text\":\"ship\"}"));
			this.ship.put(armorStand, vector);
		}
	}
	
	void collect(Player player) {
		this.player = player;
		ploc = player.getLocation();
		int yaw = (int) (new Random().nextGaussian() * 45);
		if(yaw < 0) {
			yaw += 360;
		}
		double m = Math.tan((((double) yaw) / 180) * Math.PI);
		double c = ploc.getX() - (m * ploc.getZ());
		double x = 0;
		double z = 0;
		double endX = 0;
		double endZ = 0;
		int side = (int) Math.random() * 2 + 1;
		if(c > minX && c < minX + 200) {
			if(side == 1) {
				z = 0;
				x = c;
				endZ = 200;
				endX = (200 * m) + c;
			} else {
				z = 200;
				x = (200 * m) + c;
				endX = 0;
				endZ = c;
			}
		} else {
			if(side == 1) {
				x = minX;
				z = (minX - c) / m;
				endX = minX + 200;
				endZ = (minX + 200 - c) / m;
			} else {
				x = minX + 200;
				z = (minX + 200 - c) / m;
				endX = minX;
				endZ = (minX - c) / m;
			}
		}
		int y = SurvivalGames.getHighest(game.land.getLand().get(0)) + 10;
		middle = new Location(ploc.getWorld(), ploc.getX(), y, ploc.getZ());
		top = middle.getY() - 0.5;
		count1 = new Double(top) - 1;
		bottom = ploc.getY();
		start = new Location(middle.getWorld(), x, y, z, yaw, 0);
		stop = new Location(middle.getWorld(), endX, y, endZ);
		Vector from = start.toVector();
		Vector to = stop.toVector();
		Vector distance = to.subtract(from);
		direction = distance.normalize();
		spawn(start);
		goToLocation(middle, 0);
	}
	
	void spawn(Location location) {
		for(Entry<EntityArmorStand, Vector> entry : ship.entrySet()) {
			EntityArmorStand stand = entry.getKey();
			Vector v1 = entry.getValue();System.out.println(v1);
			float yaw = location.getYaw();System.out.println(yaw);
			Vector yawVector = v1.rotateAroundY(yaw);System.out.println(yawVector);
			Vector v2 = location.toVector();System.out.println(v2);
			Vector vectorLocation = v2.add(yawVector);System.out.println(vectorLocation);
			Location l = vectorLocation.toLocation(location.getWorld());System.out.println(location);
			stand.setLocation(l.getX(), l.getY(), l.getZ(), yaw * 256 / 360, 0);
			((CraftWorld) location.getWorld()).getHandle().addEntity(stand);
			current.add(stand);
		}
		this.location = location;
		
	}
	
	void goToLocation(Location to, Integer type) {
		Location from = location.clone();
		new BukkitRunnable() {

			@Override
			public void run() {
				if(cancel) {
					this.cancel();
				}
				Vector vloc = location.toVector();
				Vector newvloc = vloc.clone().add(direction.clone());
				location = newvloc.toLocation(to.getWorld());
				for(Entry<EntityArmorStand, Vector> entry: ship.entrySet()) {
					EntityArmorStand a = entry.getKey();
					Vector v = entry.getValue();
					Vector newavloc = vloc.clone().add(v.clone().add(direction.clone()));
					Location newloc = newavloc.toLocation(to.getWorld());
					a.getBukkitEntity().teleport(newloc);
				}
				double d1 = to.toVector().clone().subtract(from.toVector()).length();
				double d2 = vloc.clone().subtract(from.toVector()).length();
				if(d1 < d2) {
					this.cancel();
					if(type == 0) {
						pickUpPlayer(player);
					} else if(type == 1) {
						despawn();
					}
				}
			}
			
		}.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
	}
	
	double top;
	double count1;
	double bottom;
	ArrayList<EntityArmorStand> chain = new ArrayList<EntityArmorStand>();
	ArrayList<EntityArmorStand> claw = new ArrayList<EntityArmorStand>();
	ArrayList<EntityArmorStand> moving = new ArrayList<EntityArmorStand>();
	int count2 = 0;
	
	void pickUpPlayer(Player player) {
		for(int i = 0; i < 7; i++) {
			EntityArmorStand a = spawn(Material.YELLOW_CONCRETE, new Location(player.getWorld(), player.getLocation().getX() - (((((i + 1) % 4) % 3) - 1.5) % 1.5), top - 2 - Math.abs(((double) ((((((int) (i / 2)) * 2) - 2) % 4) * 0.25))), player.getLocation().getZ() - (((int) Math.round((i - 3) / (Math.abs(i - 3) + 0.000000000000000000000001))) * 0.5)));
			claw.add(a);
			current.add(a);
		}
		for(int i = 0; i < 4; i++) {
			EntityArmorStand a = spawn(Material.YELLOW_CONCRETE, new Location(player.getWorld(), player.getLocation().getX() - (((int) (i / 2)) - 0.5), top - 3, player.getLocation().getZ() - ((i % 2) - 0.5)));
			claw.add(a);
			moving.add(a);
			current.add(a);
		}
		new BukkitRunnable() {

			@Override
			public void run() {
				if(cancel) {
					this.cancel();
				}
				if(count1 > bottom) {
					EntityArmorStand stand = spawn(Material.BLACK_CONCRETE, new Location(ploc.getWorld(), ploc.getX(), count1 - 1, ploc.getZ(), 0, 0));
					chain.add(stand);
					current.add(stand);
					for(EntityArmorStand a: claw) {
						ArmorStand ba = (ArmorStand) a.getBukkitEntity();
						Location loc = ba.getLocation();
						ba.teleport(new Location(loc.getWorld(), loc.getX(), loc.getY() - 0.5, loc.getZ()));
					}
				} else {
					this.cancel();
					double x = 0;
					double y = moving.get(0).getBukkitEntity().getLocation().getY();
					for(EntityArmorStand a: moving) {
						Location location = a.getBukkitEntity().getLocation();
						x += location.getX();
					}
					x /= 4;
					for(EntityArmorStand a: moving) {
						Location aloc = a.getBukkitEntity().getLocation();
						Location loc = new Location(aloc.getWorld(), x, y, aloc.getZ());
						Vector oldv = aloc.toVector();
						Vector v = loc.toVector();
						Vector oldvtov = oldv.subtract(v);
						Vector newvtov = oldvtov.multiply(0.5);
						Vector newv = v.add(newvtov);
						Location newloc = newv.toLocation(aloc.getWorld());
						a.getBukkitEntity().teleport(newloc);
					}
					Collections.reverse(chain);
					new BukkitRunnable() {

						@Override
						public void run() {
							if(cancel) {
								this.cancel();
							}
							if(count2 < chain.size()) {
								for(EntityArmorStand a: claw) {
									ArmorStand ba = (ArmorStand) a.getBukkitEntity();
									Location loc = ba.getLocation();
									ba.teleport(new Location(loc.getWorld(), loc.getX(), loc.getY() + 0.5, loc.getZ()));
								}
								chain.get(count2).getBukkitEntity().remove();
								current.remove(chain.get(count2));
								Location location = player.getLocation();
								((CraftPlayer) player).getHandle().setLocation(location.getX(), location.getY() + 0.5, location.getZ(), location.getYaw(), location.getPitch());
								PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(((CraftPlayer) player).getHandle());
								Class<?> clazz = PacketPlayOutEntityTeleport.class;
								try {
									Field x = clazz.getDeclaredField("b");
									x.setAccessible(true);
									x.set(packet, location.getX());
									Field y = clazz.getDeclaredField("c");
									y.setAccessible(true);
									y.set(packet, location.getY() + 0.5);
									Field z = clazz.getDeclaredField("d");
									z.setAccessible(true);
									z.set(packet, location.getZ());
								} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
									e.printStackTrace();
								}
								for(Player p: game.kills.players) {
									((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
								}
							} else {
								this.cancel();
								PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(player.getEntityId());
								for(Player p: game.kills.players) {
									((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
								}
								for(EntityArmorStand a: claw) {
									current.remove(a);
									a.getBukkitEntity().remove();
								}
								goToLocation(stop, 1);
							}
							count2++;
						}
			
					}.runTaskTimer(Main.getPlugin(Main.class), 0, 2);
				}
				count1 -= 0.5;
			}
			
		}.runTaskTimer(Main.getPlugin(Main.class), 0, 2);
	}
	
	EntityArmorStand spawn(Material head, Location location) {
		EntityArmorStand stand = new EntityArmorStand(EntityTypes.ARMOR_STAND, ((CraftWorld) Main.getPlugin(Main.class).getServer().getWorld("world")).getHandle());
		stand.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		stand.setSlot(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(head)));
		stand.setNoGravity(true);
		stand.setInvulnerable(true);
		stand.setInvisible(true);
		((CraftWorld) location.getWorld()).getHandle().addEntity(stand);
		return stand;
	}
	
	void despawn() {
		for(EntityArmorStand a: current) {
			a.getBukkitEntity().remove();
		}
	}

}
