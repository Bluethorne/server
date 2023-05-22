package games.skinMaker;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import games.game.Game;
import games.main.DataBase;
import games.main.GUI;
import games.main.HubNPC;
import games.main.JSON;
import games.main.Main;
import net.minecraft.server.v1_16_R3.ChatMessageType;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_16_R3.PacketPlayOutChat;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;

public class SkinMaker extends Game implements Listener {
	
	public static final UUID gameUUID = UUID.randomUUID();
	public static ArrayList<Player> players = new ArrayList<Player>();
	static final File jsonFile = new File("plugins/miniGames/SkinMaker/Skins/skins.json");
	public boolean stop = false;
	SkinMaker t = this;
	static ArrayList<SkinMaker> skinmakers = new ArrayList<SkinMaker>();
	Player player;
	static final Location location = new Location(Main.getPlugin(Main.class).getServer().getWorld("world"), -35.5, 134, -81.5, 0, 0);
	static final Location npcLocation = new Location(Main.getPlugin(Main.class).getServer().getWorld("world"), -39.5, 134, -77.5,(float) (180 + (Math.atan((location.getX() + 39.5)/3.5) * 180 / Math.PI)),(float) 0);
	BodyPart part = BodyPart.MODEL;
	Color color = Color.fromRGB(160, 101, 64);
	NPC npc;
	ArrayList<NPC> npcs = new ArrayList<NPC>();
	static ArrayList<Location> npcLocations = new ArrayList<Location>();
	Skin[] skins = new Skin[BodyPart.values().length];
	HashMap<String, HashMap<String, HashMap<String, Object>>> json;
	
	static {
		for(int y = 134; y < 137; y+= 2) {
			for(int x = -31; x > -39; x--) {
				npcLocations.add(new Location(location.getWorld(), x, y, -78, (float) (180 + (Math.atan((location.getX() - x)/3.5) * 180 / Math.PI)),(float) 0));
			}
		}
	}
	
	public static void join(Player player) {
		players.add(player);
		new BukkitRunnable() {

			@Override
			public void run() {
				player.setGameMode(GameMode.ADVENTURE);
				new SkinMaker(player);	
			}
				
		}.runTaskLater(Main.getPlugin(Main.class), 1);
	}
	
	public static void leaveGame(Player player) {
		SkinMaker.players.remove(player);
		SkinMaker s = SkinMaker.getSkinMaker(player);
		if(s != null) {
			s.stop();
		}
	}
	
	public static void changeSkin(Player player, String texture, String signature) {
		Location location = player.getLocation();
		EntityPlayer nmsplayer = ((CraftPlayer) player).getHandle();
		GameProfile profile = nmsplayer.getProfile();
		PacketPlayOutPlayerInfo packet1 = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, nmsplayer);
		for(Player p: Main.getPlugin(Main.class).getServer().getOnlinePlayers()) {
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet1);
		}
		profile.getProperties().clear();
		player.teleport(new Location(getNewWorld(player), 0, 300, 0));
		profile.getProperties().put("textures", new Property("textures", texture, signature));
		PacketPlayOutPlayerInfo packet2 = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, nmsplayer);
		PacketPlayOutEntityDestroy packet3 = new PacketPlayOutEntityDestroy(nmsplayer.getId());
		PacketPlayOutNamedEntitySpawn packet4 = new PacketPlayOutNamedEntitySpawn(nmsplayer);
		for(Player p: Main.getPlugin(Main.class).getServer().getOnlinePlayers()) {
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet2);
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet3);
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet4);
		}
		player.teleport(location);
		HubNPC.spawnNPCs(player);
	}
	
	static World getNewWorld(Player player) {
		World world = player.getWorld();
		String name = world.getName();
		if(name.equals("world")) {
			return Main.getPlugin(Main.class).getServer().getWorld("Tribes");
		} else {
			return Main.getPlugin(Main.class).getServer().getWorld("world");
		}
	}
	
	@SuppressWarnings("unchecked")
	public SkinMaker(Player player) {
		super(new ArrayList<Player>(Arrays.asList(player)));
		player.getInventory().clear();
		json = (HashMap<String, HashMap<String, HashMap<String, Object>>>) JSON.getJSON(jsonFile);
		skinmakers.add(this);
		this.player = player;
		npc = NPC.newNPC(new Skin(), ChatColor.YELLOW + "" + ChatColor.BOLD + "Your skin", npcLocation);
		Main.getPlugin(Main.class).getServer().getPluginManager().registerEvents(this, Main.getPlugin(Main.class));
		player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false, false));
		hidePlayer(player);
		player.teleport(location);
		player.getInventory().setItem(0, new Options("body", t));
		player.getInventory().setItem(1, new Options("color", t));
		player.getInventory().setItem(8, new Options("ok", t));
		player.getInventory().setItem(2, new Options("nothing", t));
		player.getInventory().setItem(3, new Options("nothing", t));
		player.getInventory().setItem(4, new Options("nothing", t));
		player.getInventory().setItem(5, new Options("nothing", t));
		player.getInventory().setItem(6, new Options("nothing", t));
		player.getInventory().setItem(7, new Options("nothing", t));
		player.sendMessage(ChatColor.BLUE + "==========================================");
		player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "How to play:");
		player.sendMessage(ChatColor.YELLOW + "Right click holding the head to change the body part");
		player.sendMessage(ChatColor.YELLOW + "Right click holding the helmet and click on a color to change the color of it");
		player.sendMessage(ChatColor.YELLOW + "Left click on a skin holding the helmet to change the color of that skin");
		player.sendMessage(ChatColor.YELLOW + "Left click on a skin with the glass panes to add a skin to your skin");
		player.sendMessage(ChatColor.YELLOW + "Right click holding the green clay block to finish making it your skin");
		player.sendMessage(ChatColor.BLUE + "==========================================");
		npc.spawn(player);
		spawnNPCs();
	}
	
	void spawnNPCs() {
		HashMap<String, HashMap<String, Object>> skins = json.get(part.name());
		for(Entry<String, HashMap<String, Object>> entry: skins.entrySet()) {
			if(entry != null) {
				String name = entry.getKey();
				String texture = (String) entry.getValue().get("texture");
				String signature = (String) entry.getValue().get("signature");
				boolean slim = false;
				if(entry.getValue().containsKey("slim")) {
					slim = (boolean) entry.getValue().get("slim");
				}
				Location location = npcLocations.get(npcs.size()).clone();
				NPC npc = NPC.newNPC(new Skin(texture, signature, slim, new File("plugins/miniGames/SkinMaker/Skins/" + part.name() + "/" + name + ".png")), ChatColor.YELLOW + name, location);
				npc.spawn(player);
				npcs.add(npc);
			}
		}
	}
	
	@EventHandler
	public void click(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if(player.equals(this.player)) {
			Action action = event.getAction();
			for(NPC clickedNPC: npcs) {
				if(isLooking(player, clickedNPC)) {
					if(action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR)) {
						float yaw = clickedNPC.getLocation().getYaw();
						if(yaw < 180) {
							yaw += 180;
						} else {
							yaw -= 180;
						}
						clickedNPC.rotate(yaw);
					} else if(action.equals(Action.LEFT_CLICK_BLOCK) || action.equals(Action.LEFT_CLICK_AIR)) {
						if(part.equals(BodyPart.MODEL)) {
							String name = clickedNPC.displayName;
							if(name.equals("§eSteve")) {
								npc.skin.slim = false;
							} else if(name.equals("§eAlex")) {
								npc.skin.slim = true;
							}
							if(npc.skin.signature == null) {
								HashMap<String, Object> skin = json.get("MODEL").get(name.substring(2));
								String base64 = (String) skin.get("texture");
								String signature = (String) skin.get("signature");
								npc.changeSkin(base64, signature);
							} else {
								npc.skin.register(true);
							}
						} else {
							ItemStack item = event.getItem();
							if(item.getType().equals(Material.LEATHER_HELMET)) {
								try {
									clickedNPC.skin.changeColor(color);
								} catch (IOException e) {
									e.printStackTrace();
								}
								clickedNPC.skin.register(true);
							} else {
								skins[part.ordinal()] = clickedNPC.skin;
								try {
									npc.skin.combine(skins);
								} catch (IOException e) {
									e.printStackTrace();
								} 
								npc.skin.register(true);
							}
						}		
					}
				}
			}
			if(isLooking(player, npc)) {
				if(action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR)) {
					float yaw = npc.getLocation().getYaw();
					if(yaw < 180) {
						yaw += 180;
					} else {
						yaw -= 180;
					}
					npc.rotate(yaw);
				}
			}
		}
	}
	
	boolean isLooking(Player player, NPC npc) {
		BoundingBox box = npc.getBukkitEntity().getBoundingBox();
		Vector min = box.getMin();
		Vector max = box.getMax();
		Vector looking = player.getEyeLocation().getDirection();
		Vector location = player.getEyeLocation().toVector();
		if(isIntersecting(looking, location, min, new Vector(max.getX(), max.getY(), min.getZ())) || isIntersecting(looking, location, min, new Vector(max.getX(), min.getY(), max.getZ())) || isIntersecting(looking, location, min, new Vector(min.getX(), max.getY(), max.getZ())) || isIntersecting(looking, location, new Vector(max.getX(), min.getY(), min.getZ()), max) || isIntersecting(looking, location, new Vector(min.getX(), max.getY(), min.getZ()), max) || isIntersecting(looking, location, new Vector(min.getX(), min.getY(), max.getZ()), max)) {
			return true;
		}
		return false;
	}
	
	boolean isIntersecting(Vector ray, Vector origin, Vector min, Vector max) {
		double plane = 0;
		Vector normal = null;
		if(min.getX() == max.getX()) {
			plane = min.getX();
			normal = new Vector(1, 0, 0);
		} else if(min.getY() == max.getY()) {
			plane = min.getY();
			normal = new Vector(0, 1, 0);
		} else if(min.getZ() == max.getZ()) {
			plane = max.getZ();
			normal = new Vector(0, 0, 1);
		}
		if(ray.dot(normal) == 0) {
			return false;
		}
		double distance = (plane - (origin.dot(normal)))/(ray.dot(normal));
		Vector position = origin.clone().add(ray.clone().multiply(distance));
		if((position.getX() >= min.getX() && position.getX() <= max.getX()) && (position.getY() >= min.getY() && position.getY() <= max.getY()) && (position.getZ() >= min.getZ() && position.getZ() <= max.getZ())) {
			return true;
		}
		return false;
	}
	
	void despawnNPCs() {
		for(NPC npc: npcs) {
			npc.remove(!new ArrayList<Skin>(Arrays.asList(skins)).contains(npc.skin));
		}
		npcs.clear();
	}
	
	static void hidePlayer(Player hidden) {
		for(Player player: Main.getPlugin(Main.class).getServer().getOnlinePlayers()) {
			hidden.hidePlayer(Main.getPlugin(Main.class), player);
		}
	}
	
	static void showPlayer(Player hidden) {
		for(Player player: Main.getPlugin(Main.class).getServer().getOnlinePlayers()) {
			hidden.showPlayer(Main.getPlugin(Main.class), player);
		}
	}
	
	public static void hideNewJoin(Player player) {
		for(SkinMaker s: SkinMaker.skinmakers) {
			Player hidden = s.player;
			hidden.hidePlayer(Main.getPlugin(Main.class), player);
		}
	}
	
	@EventHandler
	public void move(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if(player.equals(this.player)) {
			Location to = event.getTo();
 			Location newto = new Location(to.getWorld(), location.getX(), location.getY(), location.getZ(), to.getYaw(), to.getPitch());
 			event.setTo(newto);
		}
	}
	
	@EventHandler
	public void food(FoodLevelChangeEvent event) {
		if(event.getEntity().equals(player)) {
			event.setCancelled(true);
		}
	}
	
	public void stop() {
		stop = true;
		if(player.isOnline()) {
			player.removePotionEffect(PotionEffectType.NIGHT_VISION);
		}
		FoodLevelChangeEvent.getHandlerList().unregister(this);
		PlayerMoveEvent.getHandlerList().unregister(this);
		PlayerInteractEvent.getHandlerList().unregister(this);
		npc.remove(true);
		for(NPC npc: npcs) {
			npc.remove(true);
		}
		for(Skin skin: skins) {
			if(skin != null) {
				skin.file.delete();
			}
		}
		showPlayer(player);
		skinmakers.remove(this);
	}
	
	public static SkinMaker getSkinMaker(Player player) {
		for(SkinMaker s: skinmakers) {
			if(s.player.equals(player)) {
				return s;
			}
		}
		return null;
	}
	
	public void ok() {
		if(npc.skin.base64 != null || npc.skin.signature != null) {
			GUI.AnvilGUI gui = new GUI.AnvilGUI(player, new GUI.AnvilGUI.AnvilClickEventHandler() {
				@Override
	        	public void onAnvilClick(GUI.AnvilGUI.AnvilClickEvent e) {
	            	if(e.getSlot() == GUI.AnvilGUI.AnvilSlot.OUTPUT && e.hasText()) {
	            		e.setWillClose(true);
	                	String name = e.getText();
	                	try {
	                   		ResultSet result2 = DataBase.connection().createStatement().executeQuery("SELECT * FROM Skins WHERE LOWER(name) = '" + name.toLowerCase() +  "' AND UUID = '" + player.getUniqueId() + "'");
	                   		if(result2.next()) {
	                   			player.sendMessage(ChatColor.RED + "You already have a skin with this name");
	                   		} else {
	                   			ResultSet result = DataBase.connection().createStatement().executeQuery("SELECT texture, signature FROM Skins WHERE UUID = '" + player.getUniqueId() + "'");
	                   			UUID skinID = UUID.randomUUID();
	                   			if(!result.next()) {
	                   				DataBase.connection().createStatement().executeUpdate("UPDATE Hub SET SetSkin = true WHERE UUID = '" + player.getUniqueId() + "'");
	                   				DataBase.connection().createStatement().executeUpdate("UPDATE Hub SET skinID = '" + skinID + "' WHERE UUID = '" + player.getUniqueId() + "'");
	                   			}
	                   			DataBase.connection().createStatement().executeUpdate("INSERT INTO Skins (skinID, UUID, name, texture, signature) VALUES ('" + skinID + "', '" + player.getUniqueId() + "', '" + name + "', '" + npc.skin.base64 + "', '" + npc.skin.signature + "')");
	                   			player.sendMessage(ChatColor.GREEN + "You have made your skin");
								IChatBaseComponent comp = ChatSerializer.a("{\"text\":\"§eClick to download skin\",\"underlined\":\"true\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://nitropanel.com/file/download/72057222/72057222:plugins:miniGames:SkinMaker:Skins:temp/" + npc.skin.file.getName() + "\"}}");
								PacketPlayOutChat packet = new PacketPlayOutChat(comp, ChatMessageType.CHAT, player.getUniqueId());
								((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
								Game.leave(player, false);
								player.sendMessage(ChatColor.BLUE + "==========================================");
								player.sendMessage(ChatColor.GOLD + "/skin on - " + ChatColor.YELLOW + "Uses one of the skins you made");
								player.sendMessage(ChatColor.GOLD + "/skin off - " + ChatColor.YELLOW + "Uses your normal skin");
								player.sendMessage(ChatColor.GOLD + "/skin list - " + ChatColor.YELLOW + "Lists the skins you have made");
								player.sendMessage(ChatColor.GOLD + "/skin change <skin> - " + ChatColor.YELLOW + "Changes which skin you use from the skins you have made");
								player.sendMessage(ChatColor.GOLD + "/skin delete <skin> - " + ChatColor.YELLOW + "Deletes a skin");
								player.sendMessage(ChatColor.BLUE + "==========================================");
	                   		}
	                	} catch(SQLException error) {
	                		error.printStackTrace();
	                	}
	            	}
				}
        	});
        	ItemStack i = new ItemStack(Material.PAPER);
        	ItemMeta meta = i.getItemMeta();
        	meta.setDisplayName("New skin");
        	i.setItemMeta(meta);
        	gui.setSlot(GUI.AnvilGUI.AnvilSlot.INPUT_LEFT, i);
	    	gui.setTitle("Enter the name of your skin");
	    	gui.open();
		}
	}
}
	

enum BodyPart {
	
	MODEL,
	SKIN,
	MOUTH,
	EYES,
	TROUSERS,
	TOP,
	COATS,
	SHOES,
	HAIR,
	EXTRAS
	
}
