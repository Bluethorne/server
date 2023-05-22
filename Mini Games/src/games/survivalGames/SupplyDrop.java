package games.survivalGames;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.loot.LootTable;
import org.bukkit.scheduler.BukkitRunnable;

import games.main.Main;
import games.main.Pair;
import games.main.Scoreboards;
import games.main.Structures;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.DefinedStructure;
import net.minecraft.server.v1_16_R3.EntityFireworks;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.TileEntityLootable;

public class SupplyDrop {
	
	int X;
	int Z;
	Integer x1 = null;
	Integer z1 = null;
	boolean cancel = false;
	SurvivalGames game;
	HashMap<DefinedStructure, Pair<Integer, Integer>> spawns = new HashMap<DefinedStructure, Pair<Integer, Integer>>();
	static ArrayList<Location> l = new ArrayList<Location>();
	
	public SupplyDrop(Integer x, Integer z, SurvivalGames game) {
		X = x;
		Z = z;
		this.game = game;
		runnable();
	}
	
	public void cancel() {
		cancel = true;
	}
	
	void runnable() {
		new BukkitRunnable() {
			@Override
			public void run() {
				if(!cancel) {
					if(!SurvivalGames.allChests(l) && !isAllOutOfBorder(l, game.shrinker.size / 4 * 3, X, Z)) {
						int r = (int) Math.random() * (l.size() - 1) + 1;
						while(l.get(r).getBlock().getType().equals(Material.CHEST) && isInBorder(l.get(r), game.shrinker.size / 4 * 3, X, Z)) {
							r = (int) Math.random() * (l.size() - 1) + 1;
						}
						Location location = l.get(r);
						x1 = location.getBlockX();
						z1 = location.getBlockZ();
						World world = Main.getPlugin(Main.class).getServer().getWorld("world");
						int y = world.getHighestBlockYAt(x1, z1);
						Location[] corners = {new Location(world, x1 - 4, y - 1, z1 - 4), new Location(world, x1+ 4, y, z1 + 4)};
						spawns.put(Structures.createSingleStructure(corners), new Pair<Integer, Integer>(x1 - 4, z1 - 4));
						Structures.pasteStructure(new File("plugins/miniGames/SurvivalGames/supply_drop_spawn.nbt"), new Location(world, x1 - 3, y, z1 - 4));	
						for(Player player: game.players) {
							player.sendMessage(ChatColor.GOLD + "Supply Drop at " + ChatColor.RED + "" + x1 + " " + z1);
						}
						ten(x1, z1);
						runnable();
						updateScoreboard();
					}
				}
			}
			
		}.runTaskLater(Main.getPlugin(Main.class), 2400);
	}
	
	private static boolean isInBorder(Location location, float size, int X, int Z) {
		int x = location.getBlockX();
		int z = location.getBlockZ();
		if((x < X + 100 + size && x > X + 100 - size) && (z < Z + 100 + size && z > Z + 100 - size)) {
			return true;
		}
		return false;
	}
	
	private static boolean isAllOutOfBorder(ArrayList<Location> locations, float size, int X, int Z) {
		for(Location location: locations) {
			if(isInBorder(location, size, X, Z)) {
				return false;
			}
		}
		return true;
	}
	
	void updateScoreboard() {
		for(Player player: game.players) {
			int kills;
			if(Pair.getValue(((Player) player), game.kills.kills).size() == 0) {
				kills = 0;
			} else {
				kills = Pair.getValue(((Player) player), game.kills.kills).get(0);
			}
			Scoreboards.addScoreboardSurvivalGames(player, kills, "X: " + x1 + " Z: " + z1, game.shrinker.shrinking);
		}
	}
	
	void ten(int x, int z) {	
		new BukkitRunnable() {
			@Override
			public void run() {
				firework(x, Main.getPlugin(Main.class).getServer().getWorld("world").getHighestBlockYAt(x, z) + 1, z, 100);
			}
			
		}.runTaskLater(Main.getPlugin(Main.class), 200);
	}
	
	@SuppressWarnings("deprecation")
	void spawn(int x, int y, int z) {
		World world = Main.getPlugin(Main.class).getServer().getWorld("world");
		Location location = new Location(world, x, y, z);
		world.playSound(location, Sound.BLOCK_BEACON_POWER_SELECT, 1, 1);
		Block block = location.getBlock();
		block.setType(Material.CHEST);
		Chest chest = (Chest) block.getState();
		while(SurvivalGames.isEmptyInv(chest.getBlockInventory().getStorageContents()) || SurvivalGames.isTwo(chest.getBlockInventory())) {
			chest.getBlockInventory().clear();
			LootTable loot = Main.getPlugin(Main.class).getServer().getLootTable(new NamespacedKey("survival_games", "good_chest"));
			chest.setLootTable(loot);
			chest.update();
			((TileEntityLootable) ((CraftWorld) location.getWorld()).getHandle().getTileEntity(new BlockPosition(location.getX(), location.getY(), location.getZ()))).d(((CraftPlayer) location.getWorld().getPlayers().get(0)).getHandle());
		}
		SurvivalGames.moveToOneSlot(chest.getBlockInventory());
		game.chests.add(chest);
		game.remove.add(chest.getLocation());
		game.noInteract.chests.add(chest);
	}
	
	void firework(int x, int y, int z, int count) {
		new BukkitRunnable() {

			@Override
			public void run() {
				World world = Main.getPlugin(Main.class).getServer().getWorld("world");
				EntityFireworks nmsfirework = new EntityFireworks(EntityTypes.FIREWORK_ROCKET, ((CraftWorld) world).getHandle());
				Firework firework = (Firework) nmsfirework.getBukkitEntity();
				FireworkMeta meta = firework.getFireworkMeta();
				Builder builder = FireworkEffect.builder();
				Integer r = (int) (Math.random() * 255 + 1);
				Integer g = (int) (Math.random() * 255 + 1);
				Integer b = (int) (Math.random() * 255 + 1);
				builder.withColor(Color.fromRGB(r, g, b)).flicker(true);
				meta.addEffect(builder.build());
				meta.setPower(0);
				firework.setFireworkMeta(meta);
				nmsfirework.setLocation(x, y + count, z, 0, 0);
				((CraftWorld) world).getHandle().addEntity(nmsfirework);
				if(!cancel) {
					if(count > 0) {
						firework(x, y, z, count - 1);
					} else {
						spawn(x, y, z);
					}
				}
			}
			
		}.runTaskLater(Main.getPlugin(Main.class), 10);
		
	}
	
}
