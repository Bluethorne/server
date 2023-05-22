package games.survivalGames;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.loot.LootTable;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import games.game.Game;
import games.game.Land;
import games.game.PlayerWinEvent;
import games.game.QueDetector;
import games.main.Main;
import games.main.Pair;
import games.main.Scoreboards;
import games.main.Structures;
import net.minecraft.server.v1_16_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_16_R3.PacketPlayOutChat;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.ChatMessageType;
import net.minecraft.server.v1_16_R3.DefinedStructure;
import net.minecraft.server.v1_16_R3.EntityFireworks;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_16_R3.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_16_R3.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_16_R3.PacketPlayOutWorldBorder.EnumWorldBorderAction;
import net.minecraft.server.v1_16_R3.TileEntityLootable;
import net.minecraft.server.v1_16_R3.WorldBorder;

public class SurvivalGames extends Game implements Listener{
	
	public static ArrayList<Player> que = new ArrayList<Player>();
	static QueDetector detector = new QueDetector();
	Land land = new Land(200, 1);
	public ArrayList<Player> players = new ArrayList<Player>();
	ArrayList<Chest> chests = new ArrayList<Chest>();
	ArrayList<Location> remove = new ArrayList<Location>();
	ArrayList<Location> startLocs = new ArrayList<Location>();
	public NoInteract noInteract;
	public NoMove noMove;
	public KillRecorder kills;
	SupplyDrop supply;
	SurvivalGames game = this;
	Shrinker shrinker;
	public boolean ended = false;
	ArrayList<Ship> ships = new ArrayList<Ship>();
	static ArrayList<Location> l = new ArrayList<Location>();
	
	public static void addToQue(Player player) {
		IChatBaseComponent comp = ChatSerializer.a("{\"text\":\"§aYou §awere §aadded §ato §athe §aqueue. §aThe §agame §awill §astart §awhen §athere §aare §atwo §apeople.\",\"extra\":[{\"text\":\" §e§lCLICK HERE \",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/leave\"}}, {\"text\":\"§ato §aleave\"}]}");
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(comp, ChatMessageType.SYSTEM, player.getUniqueId()));
		que.add(player);
		detector.runQueDetect(que, SurvivalGames.class.getSimpleName());
	}
	
	public static void loadChests() {
		World world = Main.getPlugin(Main.class).getServer().getWorld("world");
		File chests = new File("plugins/miniGames/SurvivalGames/chests.txt");
		File supply = new File("plugins/miniGames/SurvivalGames/supply_drops.txt");
		try {
			Scanner scanner1 = new Scanner(chests);
			while(scanner1.hasNextLine()) {
				String line = scanner1.nextLine();
				String[] nums = line.split(" ");
				Location location = new Location(world, Integer.valueOf(nums[0]), Integer.valueOf(nums[1]), Integer.valueOf(nums[2]));
				l.add(location);
			}
			scanner1.close();
			Scanner scanner2 = new Scanner(supply);
			while(scanner2.hasNextLine()) {
				String line = scanner2.nextLine();
				String[] nums = line.split(" ");
				Location location = new Location(world, Integer.valueOf(nums[0]), Integer.valueOf(nums[1]), Integer.valueOf(nums[2]));
				SupplyDrop.l.add(location);
			}
			scanner2.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public SurvivalGames(ArrayList<Player> players) {
		super(players);
		this.players = players;
		addBorder();
		setMiddle();
		for(int count = 0; count < (players.size() * 20); count++) {
			if(Collections.min(Pair.getKeys(land.getLand().get(0))) == 0 && Collections.min(Pair.getValues(land.getLand().get(0))) == 0) {
				Chest chest = spawnChest();
				if(chest != null) {
					chests.add(chest);
				}
			} else {
				chests.add(spawnChest(200, 0, 0, "bad_chest"));
			}
		}
		for(Player player: players) {
			Location temp = startLocs.get(players.indexOf(player));
			temp.setY(temp.getY() + 3.5);
			temp.setX(temp.getX() + 0.5);
			temp.setZ(temp.getZ() + 0.5);
	        double dx = (Collections.min(Pair.getKeys(land.getLand().get(0))) + 100)  - temp.getX();
	        double dz = (Collections.min(Pair.getValues(land.getLand().get(0))) + 100) - temp.getZ();
	        if (dx != 0) {
	            if (dx < 0) {
	                temp.setYaw((float) (1.5 * Math.PI));
	            } else {
	                temp.setYaw((float) (0.5 * Math.PI));
	            }
	            temp.setYaw((float) temp.getYaw() - (float) Math.atan(dz / dx));
	        } else if (dz < 0) {
	            temp.setYaw((float) Math.PI);
	        }
	        double dxz = Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2));
	        temp.setPitch((float) -Math.atan(0 / dxz));
	        temp.setYaw(-temp.getYaw() * 180f / (float) Math.PI);
	        temp.setPitch(temp.getPitch() * 180f / (float) Math.PI);
			player.teleport(temp);
			player.setGameMode(GameMode.ADVENTURE);
			player.getInventory().clear();
			player.setExp(0);
			player.setLevel(0);
			player.setHealth(20);
			player.setFoodLevel(20);
			Scoreboards.addScoreboardSurvivalGames(player, 0, "No Supply Drop", shrinker.shrinking);
		}
		supply = new SupplyDrop(Collections.min(Pair.getKeys(land.getLand().get(0))), Collections.min(Pair.getValues(land.getLand().get(0))), game);
		noInteract = new NoInteract(players, chests, land.getLand().get(0), this);
		noMove = new NoMove(players);
		Stew.addPlayers(players);
		Enchanter.addPlayers(players);
		kills = new KillRecorder(players, this);
		counter3sec();
		Main.getPlugin(Main.class).getServer().getPluginManager().registerEvents(this, Main.getPlugin(Main.class));
	}
	
	void end() {
		noInteract.end();
		Stew.removePlayers(players);
		WorldBorder border = new WorldBorder();
		border.setCenter(0, 0);
		border.setSize(60000000);
		border.world = ((CraftWorld) Main.getPlugin(Main.class).getServer().getWorld("world")).getHandle();
		PacketPlayOutWorldBorder packet = new PacketPlayOutWorldBorder(border, EnumWorldBorderAction.INITIALIZE);
		for(Player player: players) {
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
			player.setFlying(false);
			player.setAllowFlight(false);
			player.removePotionEffect(PotionEffectType.INVISIBILITY);
			EntityPlayer nmsplayer = ((CraftPlayer) player).getHandle();
			for(EntityPlayer nmsdead: kills.fake) {
				PacketPlayOutEntityDestroy p1 = new PacketPlayOutEntityDestroy(nmsdead.getId());
				nmsplayer.playerConnection.sendPacket(p1);
				PacketPlayOutPlayerInfo p2 = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, nmsdead);
				nmsplayer.playerConnection.sendPacket(p2);
			}
			Scoreboards.addScoreboardJoin(player);
		}
		endGame();
		for(Location block: remove) {
			if(block.getBlock().getState() instanceof Chest) {
				((Chest) block.getBlock().getState()).getBlockInventory().clear();
			}
			block.getBlock().setType(Material.AIR);
		}
		for(Ship ship: ships) {
			ship.despawn();
			ship.cancel = true;
		}
		World world = Main.getPlugin(Main.class).getServer().getWorld("world");System.out.println(supply.spawns.keySet());
		for(DefinedStructure structure: supply.spawns.keySet()) {
			Pair<Integer, Integer> pair = supply.spawns.get(structure);
			Structures.insertSingleStructure(structure, new Location(world, pair.getKey(), world.getHighestBlockYAt(pair.getKey(), pair.getValue()), pair.getValue()));
		}
		land.unclaim();
	}
	
	@EventHandler
	public void win(PlayerWinEvent e) {
		Game game = e.getGame();
		if(game.equals(this)) {
			PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{\"text\":\"" + ChatColor.GREEN + "The winner is " + e.getWinner().getName() + "!\"}"));
			ArrayList<Pair<Player, Integer>> order = e.getScores();
			for(Player player: players) {
				((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
				player.sendMessage(ChatColor.DARK_BLUE + "=======================================");
				player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Thornecraft Network");
				player.sendMessage(ChatColor.GRAY + "Game - Survival Games");
				player.sendMessage("");
				if(order.size() >= 1) {
					player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "1st Place: " + ChatColor.WHITE + order.get(0).getKey().getName());
				}
				if(order.size() >= 2) {
					player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "2nd Place: " + ChatColor.WHITE + order.get(1).getKey().getName());
				}
				if(order.size() >= 3) {
					player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "3rd Place: " + ChatColor.WHITE + order.get(2).getKey().getName());
				}
				player.sendMessage("");
				player.sendMessage(ChatColor.DARK_BLUE + "=======================================");
			}
			ended = true;
			kills.stop();
			Enchanter.removePlayers(players);
			supply.cancel();
			shrinker.cancel = true;
			firework(20, e.getWinner());
		}
	}
	
	void firework(int fireworkNum, Player winner) {
		new BukkitRunnable() {

			@Override
			public void run() {
				int r = (int) (Math.random() * 255 + 1);
				int g = (int) (Math.random() * 255 + 1);
				int b = (int) (Math.random() * 255 + 1);
				Location loc = winner.getLocation();
				EntityFireworks nmsfirework = new EntityFireworks(EntityTypes.FIREWORK_ROCKET, ((CraftWorld) Main.getPlugin(Main.class).getServer().getWorld("world")).getHandle());
				Firework firework = (Firework) nmsfirework.getBukkitEntity();
				FireworkMeta meta = firework.getFireworkMeta();
				Builder builder = FireworkEffect.builder();
				builder.withColor(Color.fromRGB(r, g, b)).flicker(true);
				meta.addEffect(builder.build());
				meta.setPower(1);
				firework.setFireworkMeta(meta);
				nmsfirework.setLocation(loc.getX(), loc.getY(), loc.getZ(), 0, 0);
				((CraftWorld) Main.getPlugin(Main.class).getServer().getWorld("world")).getHandle().addEntity(nmsfirework);
				if(fireworkNum > 0 && players.contains(winner)) {
					firework(fireworkNum - 1, winner);
				} else {
					end();
				}
			}
			
		}.runTaskLater(Main.getPlugin(Main.class), 10);
	}
	
	int count = 4;
	
	void counter3sec() {
		new BukkitRunnable() {

			@Override
			public void run() {
				String title = "";
				Sound sound = null;
				switch(count) {
				case 4:
					break;
				case 3:
					title = ChatColor.RED + "3";
					sound = Sound.BLOCK_NOTE_BLOCK_CHIME;
					break;
				case 2:
					title = ChatColor.GOLD + "2";
					sound = Sound.BLOCK_NOTE_BLOCK_CHIME;
					break;
				case 1:
					title = ChatColor.YELLOW + "1";
					sound = Sound.BLOCK_NOTE_BLOCK_CHIME;
					break;
				case 0:
					title = ChatColor.GREEN + "GO!";
					sound = Sound.BLOCK_NOTE_BLOCK_PLING;
					break;
				case -1:
					noMove.end();
					this.cancel();
				}
				PacketPlayOutTitle packet = new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{\"text\":\"" + title + "\"}"), 0, 1, 0);
				for(Player player: players) {
					((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
					player.playSound(player.getLocation(), sound, 1, 1);
				}
				count--;
				counter3sec();
			}
			
		}.runTaskLater(Main.getPlugin(Main.class), 20);
	}
	
	void addBorder() {
		ArrayList<Pair<Integer, Integer>> c = land.getLand().get(0);
		WorldBorder border = new WorldBorder();
		border.setCenter(Collections.min(Pair.getKeys(c)) + 100, Collections.min(Pair.getValues(c)) + 100);
		border.setSize(200);
		border.world = ((CraftWorld) Main.getPlugin(Main.class).getServer().getWorld("world")).getHandle();
		PacketPlayOutWorldBorder packet = new PacketPlayOutWorldBorder(border, EnumWorldBorderAction.INITIALIZE);
		for(Player player: players) {
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		}
		border.setDamageAmount(1);
		border.setDamageBuffer(1);
		shrinker = new Shrinker(this, border);
	}
	
	@SuppressWarnings("deprecation")
	Chest spawnChest(int range, int addX, int addZ, String chestType) {
		int x = (int) (Math.random() * range + 1);
		int z = (int) (Math.random() * range + 1);
		Location location = new Location(Main.getPlugin(Main.class).getServer().getWorld("world"), x + Collections.min(Pair.getKeys(land.getLand().get(0))) + addX, Main.getPlugin(Main.class).getServer().getWorld("world").getHighestBlockYAt(x + Collections.min(Pair.getKeys(land.getLand().get(0))) + addX, z  + Collections.min(Pair.getValues(land.getLand().get(0))) + addZ) + 1, z + Collections.min(Pair.getValues(land.getLand().get(0))) + addZ);
		Block block = location.getBlock();
		block.setType(Material.CHEST);
		Chest chest = (Chest) block.getState();
		while(isEmptyInv(chest.getBlockInventory().getStorageContents()) || isTwo(chest.getBlockInventory())) {
			chest.getBlockInventory().clear();
			LootTable loot = Main.getPlugin(Main.class).getServer().getLootTable(new NamespacedKey("survival_games", chestType));
			chest.setLootTable(loot);
			chest.update();
			((TileEntityLootable) ((CraftWorld) location.getWorld()).getHandle().getTileEntity(new BlockPosition(location.getX(), location.getY(), location.getZ()))).d(((CraftPlayer) location.getWorld().getPlayers().get(0)).getHandle());
		}
		moveToOneSlot(chest.getBlockInventory());
		remove.add(location);
		return chest;
	}
	
	@SuppressWarnings("deprecation")
	Chest spawnChest() {
		if(!allChests(l)) {
			int r = (int) Math.random() * (l.size() - 1) + 1;
			while(l.get(r).getBlock().getType().equals(Material.CHEST)) {System.out.println(r);
				r = (int) Math.random() * (l.size() - 1) + 1;
			}
			Location location = l.get(r);
			Block block = location.getBlock();
			block.setType(Material.CHEST);
			Chest chest = (Chest) block.getState();
			while(isEmptyInv(chest.getBlockInventory().getStorageContents()) || isTwo(chest.getBlockInventory())) {
				chest.getBlockInventory().clear();
				LootTable loot = Main.getPlugin(Main.class).getServer().getLootTable(new NamespacedKey("survival_games", "bad_chest"));
				chest.setLootTable(loot);
				chest.update();
				((TileEntityLootable) ((CraftWorld) location.getWorld()).getHandle().getTileEntity(new BlockPosition(location.getX(), location.getY(), location.getZ()))).d(((CraftPlayer) location.getWorld().getPlayers().get(0)).getHandle());
			}
			moveToOneSlot(chest.getBlockInventory());
			remove.add(location);
			return chest;
		}
		return null;
	}
	
	public static boolean allChests(ArrayList<Location> locations) {
		for(Location location: locations) {
			if(!location.getBlock().getType().equals(Material.CHEST)) {
				return false;
			}
		}
		return true;
	}
	
	public static void moveToOneSlot(Inventory inv) {
		HashMap<Material, Pair<Integer, ArrayList<Integer>>> mat = new HashMap<Material, Pair<Integer, ArrayList<Integer>>>();
		for(int i = 0; i < inv.getContents().length; i++) {
			ItemStack item = inv.getContents()[i];
			if(item != null) {
				if(inv.contains(item.getType(), 2)) {
					Pair<Integer, ArrayList<Integer>> pair = mat.get(item.getType());
					if(pair == null) {
						pair = new Pair<Integer, ArrayList<Integer>>(1, new ArrayList<Integer>(Arrays.asList(i)));
					} else {
						pair.setKey(pair.getKey() + 1);
						pair.getValue().add(i);
					}
					mat.put(item.getType(), pair);
				}
			}
		}
		for(Material material: mat.keySet()) {
			int num = (int) Math.random() * mat.get(material).getValue().size() + 1;
			int slot = mat.get(material).getValue().get(num);
			for(int s: mat.get(material).getValue()) {
				if(s != slot) {
					inv.setItem(s, new ItemStack(Material.AIR));
				} else {
					ItemStack i = new ItemStack(material);
					i.setAmount(mat.get(material).getKey());
					inv.setItem(s, i);
				}
			}
		}
	}

	public static boolean isEmptyInv(ItemStack[] items) {
		for(ItemStack item: items) {
			if(item != null) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isTwo(Inventory inv) {
		ArrayList<Material> mats = new ArrayList<Material>();
		for(ItemStack item: inv.getStorageContents()) {
			if(item != null) {
				Material mat = item.getType();
				if(mats.contains(mat)) {
					if(!mat.isEdible() && !mat.equals(Material.ARROW)) {
						return true;
					}
				} else {
					mats.add(mat);
				}
			}
		}
		return false;
	}	
	
	void setMiddle() {
		int largestEdge;
		if(players.size() < 16) {
			largestEdge = 3;
		} else if(players.size() > 24) {
			largestEdge = 7;
		} else {
			largestEdge = ((int) ((-(Math.pow((48-(players.size() * 2)),(1/2)))/2) + 5)) + 1;
			if(largestEdge % 2 == 0) {
				largestEdge++;
			} else {
				largestEdge = largestEdge + 2;
			}
		}
		int radius = (int) ((((Math.pow(largestEdge, 2)) - 3)/2) + largestEdge);
		int x = Collections.min(Pair.getKeys(land.getLand().get(0))) + 100;
		int z = Collections.min(Pair.getValues(land.getLand().get(0))) + 100;
		ArrayList<Pair<Integer, Integer>> circle = getCircle(x, z, radius);
		int y = getHighest(circle);
		set(circle, y, Material.COBBLESTONE);
		setStairs(y, x, z, radius, circle);
		startLocs = getStartLocs(getCircle(x, z, radius - 1), y, getCircle(x, z, radius - 2));
		setSpawnLoc();
		for(int count = 0; count < (players.size()/3) + 1; count++) {
			chests.add(spawnChest(3, 100, 100, "medium_chest"));
		}
		Block block = new Location(Main.getPlugin(Main.class).getServer().getWorld("world"), x, Main.getPlugin(Main.class).getServer().getWorld("world").getHighestBlockYAt(x, z) + 1, z).getBlock();
		block.setType(Material.ENCHANTING_TABLE);
		remove.add(block.getLocation());
	}
	
	ArrayList<Pair<Integer, Integer>> getCircle(int xCenter, int zCenter, int radius) {
		ArrayList<Pair<Integer, Integer>> circle = new ArrayList<Pair<Integer, Integer>>();
		for(int x = -radius; x <= radius; x++) {
			for(int z = -radius; z <= radius; z++) {
				if((Math.pow(x + 0.5, 2)) + (Math.pow(z + 0.5, 2)) <= (Math.pow(radius, 2))) {
					circle.add(new Pair<Integer, Integer>(x + xCenter, z + zCenter));
				}
			}
		}
		return circle;
	}
	
	public static int getHighest(ArrayList<Pair<Integer, Integer>> c) {
		World world = Main.getPlugin(Main.class).getServer().getWorld("world");
		int highest = 0;
		for(Pair<Integer, Integer> pair: c) {
			int y = world.getHighestBlockYAt(pair.getKey(), pair.getValue()) + 1;
			if(y > highest) {
				highest = y;
			}
		}
		return highest;
	}
	
	void set(ArrayList<Pair<Integer, Integer>> xAndz, int y, Material material) {
		World world = Main.getPlugin(Main.class).getServer().getWorld("world");
		for(Pair<Integer, Integer> pair: xAndz) {
			Location location = new Location(world, pair.getKey(), y, pair.getValue());
			if(location.getBlock().getType().equals(Material.AIR) && world.getHighestBlockYAt(location) < y) {
				remove.add(location);
				location.getBlock().setType(material);
			}
		}
	}
	
	void setStairs(int highestY, int x, int z, int radius, ArrayList<Pair<Integer, Integer>> circle) {
		boolean onFloor = false;
		for(int stairY = highestY; onFloor == false; stairY--) {
			if(stairY != highestY) {
				set(circle, stairY, Material.COBBLESTONE);
			}
			radius++;
			circle = getCircle(x, z, radius);
			onFloor = true;
			for(Pair<Integer, Integer> pair: circle) {
				if(Main.getPlugin(Main.class).getServer().getWorld("world").getHighestBlockYAt(pair.getKey(), pair.getValue()) + 1 < stairY) {
					onFloor = false;
				}
			}
		}
	}
	
	ArrayList<Location> getStartLocs(ArrayList<Pair<Integer, Integer>> bigCircle, int y, ArrayList<Pair<Integer, Integer>> smallCircle) {
		World world = Main.getPlugin(Main.class).getServer().getWorld("world");
		ArrayList<Location> locations = new ArrayList<Location>();
		ArrayList<Pair<Integer, Integer>> tempCoords = bigCircle;
		Pair.removeAllEqualTo(tempCoords, smallCircle);
		ArrayList<Location> tempLocations = new ArrayList<Location>();
		for(Pair<Integer, Integer> pair: tempCoords) {
			tempLocations.add(new Location(world, pair.getKey(), y, pair.getValue()));
		}
		ArrayList<Location> pp = new ArrayList<Location>();
		ArrayList<Location> pn = new ArrayList<Location>();
		ArrayList<Location> np = new ArrayList<Location>();
		ArrayList<Location> nn = new ArrayList<Location>();
		for(Location location: tempLocations) {
			if(location.getBlockX() < Collections.min(Pair.getKeys(land.getLand().get(0))) + 100) {
				if(location.getBlockZ() < Collections.min(Pair.getValues(land.getLand().get(0))) + 100) {
					nn.add(location);
				} else {
					np.add(location);
				}
			} else {
				if(location.getBlockZ() < Collections.min(Pair.getValues(land.getLand().get(0))) + 100) {
					pn.add(location);
				} else {
					pp.add(location);
				}
			}
		}
		boolean done = false;
		while(!done) {
			done = true;
			Location temp;
			for(int count = 0; count < pp.size() - 1; count++) {
				if(pp.get(count).getBlockX() == pp.get(count + 1).getBlockX()) {
					if(pp.get(count).getBlockZ() < pp.get(count + 1).getBlockZ()) {
						done = false;
						temp = pp.get(count);
						pp.set(count, pp.get(count + 1));
						pp.set(count + 1, temp);
					}
				}
			}
		}
		done = false;
		while(!done) {
			done = true;
			Location temp;
			for(int count = 0; count < nn.size() - 1; count++) {
				if(nn.get(count).getBlockX() == nn.get(count + 1).getBlockX()) {
					if(nn.get(count).getBlockZ() < nn.get(count + 1).getBlockZ()) {
						done = false;
						temp = nn.get(count);
						nn.set(count, nn.get(count + 1));
						nn.set(count + 1, temp);
					}
				}
			}
		}
		Collections.reverse(np);
		Collections.reverse(pp);
		tempLocations.clear();
		tempLocations.addAll(pp);
		tempLocations.addAll(np);
		tempLocations.addAll(nn);
		tempLocations.addAll(pn);
		int skip = tempLocations.size() / players.size();
		for(int count = 0; count <= players.size() - 1; count++) {
			locations.add(tempLocations.get(count * skip));
		}
		return locations;
	}
	
	void setSpawnLoc() {
		World world = Main.getPlugin(Main.class).getServer().getWorld("world");
		for(int count = 0; count < 3; count++) {
			for(Location location: startLocs) {
				int tempy = world.getHighestBlockYAt(location) + 1;
				Block set = new Location(world, location.getBlockX(), tempy, location.getBlockZ()).getBlock();
				remove.add(set.getLocation());
				if(count == 2) {
					set.setType(Material.STONE_SLAB);
				} else {
					set.setType(Material.COBBLESTONE_WALL);
				}
			}
		}
	}
}
