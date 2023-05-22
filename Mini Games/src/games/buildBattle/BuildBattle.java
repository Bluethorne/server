package games.buildBattle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import games.buildBattle.menu.CopyAndPaste;
import games.buildBattle.menu.EntitySpawner;
import games.buildBattle.menu.OpenMenu;
import games.buildBattle.menu.Particles;
import games.buildBattle.menu.Sounds;
import games.game.Game;
import games.game.Land;
import games.game.PlayerWinEvent;
import games.game.Protection;
import games.game.QueDetector;
import games.game.StopSpawning;
import games.main.Main;
import games.main.Pair;
import games.main.Scoreboards;
import games.main.Structures;
import net.minecraft.server.v1_16_R3.ChatMessageType;
import net.minecraft.server.v1_16_R3.EntityFireworks;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.PacketPlayOutChat;
import net.minecraft.server.v1_16_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_16_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_16_R3.PacketPlayOutTitle.EnumTitleAction;

public class BuildBattle extends Game implements Listener{
	
	public ArrayList<Player> players = new ArrayList<Player>();
	public static ArrayList<Player> que = new ArrayList<Player>();
	static QueDetector detector = new QueDetector();
	public ArrayList<Pair<Player, ArrayList<Pair<Integer, Integer>>>> plots = new ArrayList<Pair<Player, ArrayList<Pair<Integer, Integer>>>>();
	Land land;
	public Voter voter;
	Listener t;
	Game g;
	
	public static void addToQue(Player player) {
		IChatBaseComponent comp = ChatSerializer.a("{\"text\":\"§aYou §awere §aadded §ato §athe §aqueue. §aThe §agame §awill §astart §awhen §athere §aare §atwo §apeople.\",\"extra\":[{\"text\":\" §e§lCLICK HERE \",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/leave\"}}, {\"text\":\"§ato §aleave\"}]}");
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(comp, ChatMessageType.SYSTEM, player.getUniqueId()));
		que.add(player);
		detector.runQueDetect(que, BuildBattle.class.getSimpleName());
	}

	public BuildBattle(ArrayList<Player> players) {
		super(players);
		this.players = players;
		for(Player player: players) {
			OpenMenu.dark.add(new Pair<Player, Boolean>(player, false));
		}
		this.land = new Land(32, players.size());
		ArrayList<ArrayList<Pair<Integer, Integer>>> p = this.land.getLand();
		for(int count = 0; count < players.size(); count++) {
			plots.add(new Pair<Player, ArrayList<Pair<Integer, Integer>>>(players.get(count), p.get(count)));
			Land.buildbattlesmallest.add(new Pair<Player, Pair<Integer, Integer>>(players.get(count), new Pair<Integer, Integer>(Collections.min(Pair.getKeys(p.get(count))), Collections.min(Pair.getValues(p.get(count))))));
		}
		t = this;
		g = this;
		start();
	}
	
	void start() {
		stopSpawning(true);
		setWallProtection(true);
		loadPlots();
		for(Pair<Player, ArrayList<Pair<Integer, Integer>>> pair: plots) {
			Player player = pair.getKey();
			Integer x = Collections.min(Pair.getKeys(pair.getValue())) + 16;
			Integer z = Collections.min(Pair.getValues(pair.getValue())) + 16;
			Location location = new Location(Main.getPlugin(Main.class).getServer().getWorld("world"), x, 201, z);
			player.getInventory().clear();
			player.teleport(location);
			player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false, false));
			player.setGameMode(GameMode.CREATIVE);
		}
		
		String word = pickWord();
		IChatBaseComponent comp = ChatSerializer.a("{\"text\":\"§aThe theme is "+ word + "\"}");
		PacketPlayOutTitle packet = new PacketPlayOutTitle(EnumTitleAction.TITLE, comp);
		for(Player player: players) {
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
			player.sendMessage(ChatColor.GREEN + "The theme is " + word);
			player.getInventory().setItem(8, Main.buildbattle_menu);
			Scoreboards.addScoreboardBuildBattle(player, word);
		}
		counter();
		new BukkitRunnable() {

			@Override
			public void run() {
				NoInvChange.players.addAll(Pair.getKeys(plots));
				for(Player player: Pair.getKeys(plots)) {
					Inventory inv = player.getInventory();
					inv.clear();
					loadInv(inv);	
				}
				voter = new Voter(plots, g);
				Main.getPlugin(Main.class).getServer().getPluginManager().registerEvents(t, Main.getPlugin(Main.class));
			}
			
		}.runTaskLater(Main.getPlugin(Main.class), 12000);
	}
	
	String pickWord() {
		File file = new File("plugins/miniGames/BuildBattle/words.txt");
		List<String> lines = new ArrayList<String>();
		try {
			lines = Files.readAllLines(file.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		Integer random = (int) (Math.random() * (lines.size() - 1));
		String word = lines.get(random);
		return word.substring(0, 1).toUpperCase() + word.substring(1);
	}
	
	void loadPlots() {
		for(ArrayList<Pair<Integer, Integer>> plot: Pair.getValues(plots)) {
			Location location = new Location(Main.getPlugin(Main.class).getServer().getWorld("world"), Collections.min(Pair.getKeys(plot)), 200, Collections.min(Pair.getValues(plot)));
			Structures.pasteStructure(new File("plugins/miniGames/BuildBattle/box.nbt"), location);
		}
	}
	
	void setWallProtection(boolean b) {
		ArrayList<Location> locations = new ArrayList<Location>();
		for(int x = 0; x < 32; x++) {
			for(int y = 0; y < 32; y++) {
				for(int z = 0; z < 32; z++) {
					if(x == 0 || x == 31 || y == 0 || y == 31 || z == 0 || z == 31) {
						locations.add(new Location(Main.getPlugin(Main.class).getServer().getWorld("world"), x, y, z));
					}
				}
			}
		}
		for(Pair<Player, ArrayList<Pair<Integer, Integer>>> pair: plots) {
			ArrayList<Block> blocks = new ArrayList<Block>();
			Integer x = Collections.min(Pair.getKeys(pair.getValue()));
			Integer z = Collections.min(Pair.getValues(pair.getValue()));
			for(Location location: locations) {
				Location newLocation = new Location(location.getWorld(), location.getX() + x, location.getY() + 200, location.getZ() + z);
				blocks.add(newLocation.getBlock());
			}
			if(Protection.getProtection(blocks) == null) {
				new Protection(blocks).setProtection(b);
			} else {
				Protection.getProtection(blocks).setProtection(b);
			}
		}
	}
	
	void stopSpawning(boolean b) {
		ArrayList<Pair<Integer, Integer>> coords = new ArrayList<Pair<Integer, Integer>>();
		for(ArrayList<Pair<Integer, Integer>> plot: Pair.getValues(plots)) {
			coords.addAll(plot);
		}
		if(StopSpawning.getStopSpawning(coords) == null) {
			new StopSpawning(coords).setSpawn(b);
		} else {
			StopSpawning.getStopSpawning(coords).setSpawn(b);
		}
	}
	
	Integer count = 600;
	
	void counter() {
		new BukkitRunnable() {

			@Override
			public void run() {
				Integer min = count / 60;
				Integer sec = count % 60;
				String string = "";
				if(count >= 300) {
					string = "§a";
				} else if(count < 300 && count > 60) {
					string = "§6";
				} else if(count <= 60) {
					string = "§c";
				}
				IChatBaseComponent comp = ChatSerializer.a("{\"text\":\"" + string + min + ":" + String.format("%02d", sec) +"\"}");
				PacketPlayOutTitle packet = new PacketPlayOutTitle(EnumTitleAction.ACTIONBAR, comp);
				for(Player player: Pair.getKeys(plots)) {
					((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
				}
				count--;
				if(count == 0) {
					this.cancel();
				}
			}
				
		}.runTaskTimer(Main.getPlugin(Main.class), 0, 20);
	}
	
	void loadInv(Inventory inv) {
		ItemStack i1 = new ItemStack(Material.RED_TERRACOTTA);
		ItemMeta m1 = i1.getItemMeta();
		m1.setDisplayName(ChatColor.DARK_RED + "RUBBISH");
		i1.setItemMeta(m1);
		inv.setItem(0, i1);
		ItemStack i2 = new ItemStack(Material.PINK_TERRACOTTA);
		ItemMeta m2 = i2.getItemMeta();
		m2.setDisplayName(ChatColor.RED + "BAD");
		i2.setItemMeta(m2);
		inv.setItem(1, i2);
		ItemStack i3 = new ItemStack(Material.ORANGE_TERRACOTTA);
		ItemMeta m3 = i3.getItemMeta();
		m3.setDisplayName(ChatColor.GOLD + "OK");
		i3.setItemMeta(m3);
		inv.setItem(2, i3);
		ItemStack i4 = new ItemStack(Material.LIME_TERRACOTTA);
		ItemMeta m4 = i4.getItemMeta();
		m4.setDisplayName(ChatColor.GREEN + "GOOD");
		i4.setItemMeta(m4);
		inv.setItem(3, i4);
		ItemStack i5 = new ItemStack(Material.GREEN_TERRACOTTA);
		ItemMeta m5 = i5.getItemMeta();
		m5.setDisplayName(ChatColor.DARK_GREEN + "AMAZING");
		i5.setItemMeta(m5);
		inv.setItem(4, i5);
	}
	
	@EventHandler
	public void win(PlayerWinEvent event) {
		Game game = event.getGame();
		if(game.equals(this)) {
			Player winner = event.getWinner();
			boolean dark = Pair.getValue(winner, OpenMenu.dark).get(0);
			IChatBaseComponent comp = ChatSerializer.a("{\"text\":\"" + ChatColor.GREEN + "The winner is " + winner.getName() + "!\"}");
			PacketPlayOutTitle packet = new PacketPlayOutTitle(EnumTitleAction.TITLE, comp);
			Integer x = Collections.min(Pair.getKeys(Pair.getValue(winner, plots).get(0))) + 16;
			Integer z = Collections.min(Pair.getValues(Pair.getValue(winner, plots).get(0))) + 16;
			Location location = new Location(Main.getPlugin(Main.class).getServer().getWorld("world"), x, 201, z);
			for(Player player: players) {
				player.teleport(location);
				((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
				player.sendMessage(ChatColor.GREEN + "The winner is " + winner.getName() + "!");
				player.sendMessage(ChatColor.BLUE + "==============================================");
				player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Thornecraft Network!");
				player.sendMessage(ChatColor.GRAY + "Game - Build Battle");
				player.sendMessage("");
				for(Pair<Player, Integer> pair: event.getScores()) {
					Player p = pair.getKey();
					Integer position = event.getPlayers().indexOf(p) + 1;
					player.sendMessage(ChatColor.YELLOW + String.valueOf(position) + ". " + ChatColor.GOLD + p.getName() + ChatColor.YELLOW + " : " + ChatColor.GOLD + pair.getValue());
				}
				player.sendMessage("");
				player.sendMessage(ChatColor.BLUE + "==============================================");
				if(dark == true) {
					player.removePotionEffect(PotionEffectType.NIGHT_VISION);
				} else {
					player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false, false));
				}
			}
			for(Player player: players) {
				player.getInventory().clear();
			}
			fireworks(x - 16, z - 16);
			new BukkitRunnable() {

				@Override
				public void run() {
					end();
				}
			}.runTaskLater(Main.getPlugin(Main.class), 400);
		}
	}
	
	Integer fireworks = 40;
	
	public void fireworks(Integer leastX, Integer leastZ) {
		new BukkitRunnable() {
			
			@Override
			public void run() {
				Integer r = (int) (Math.random() * 255 + 1);
				Integer g = (int) (Math.random() * 255 + 1);
				Integer b = (int) (Math.random() * 255 + 1);
				Integer x = (int) (Math.random() * 30 + 1) + 1;
				Integer z = (int) (Math.random() * 30 + 1) + 1;
				EntityFireworks nmsfirework = new EntityFireworks(EntityTypes.FIREWORK_ROCKET, ((CraftWorld) Main.getPlugin(Main.class).getServer().getWorld("world")).getHandle());
				Firework firework = (Firework) nmsfirework.getBukkitEntity();
				StopSpawning.allowed.add(firework);
				FireworkMeta meta = firework.getFireworkMeta();
				Builder builder = FireworkEffect.builder();
				builder.withColor(Color.fromRGB(r, g, b)).flicker(true);
				meta.addEffect(builder.build());
				meta.setPower(1);
				firework.setFireworkMeta(meta);
				nmsfirework.setLocation(leastX + x, 201, leastZ + z, 0, 0);
				((CraftWorld) Main.getPlugin(Main.class).getServer().getWorld("world")).getHandle().addEntity(nmsfirework);
				fireworks--;
				if (fireworks == 0) {
					this.cancel();
				}
			}	
		}.runTaskTimer(Main.getPlugin(Main.class), 0, 10);
	}
	
	void end() {
		Plugin plugin = Main.getPlugin(Main.class);
		Particles.removeG(this);
		Sounds.removeG(this);
		for(Player player: Pair.getKeys(plots)) {
			NoInvChange.remove(player);
			player.removePotionEffect(PotionEffectType.NIGHT_VISION);
			if(new File("plugins/miniGames/BuildBattle/Structures/" + player.getUniqueId().toString() + "/").exists()) {
				for(File file: new File("plugins/miniGames/BuildBattle/Structures/" + player.getUniqueId().toString() + "/").listFiles()) {
					file.delete();
				}
			}
			plugin.getConfig().set("Structures." + player.getUniqueId().toString(), null);
			plugin.saveConfig();
			for(Entity entity: Pair.getValue(player, EntitySpawner.entitys)) {
				entity.remove();
			}
			EntitySpawner.entitys.removeAll(Pair.getPairFromKey(player, EntitySpawner.entitys));
			OpenMenu.dark.removeAll(Pair.getPairFromKey(player, OpenMenu.dark));
			Scoreboards.addScoreboardJoin(player);
		}
		((Game) this).endGame();
		setWallProtection(false);
		for(ArrayList<Pair<Integer, Integer>> plot: Pair.getValues(plots)) {
			for(Pair<Integer, Integer> pair: plot) {
				for(int y = 200; y < 232; y++) {
					Location location = new Location(Main.getPlugin(Main.class).getServer().getWorld("world"), pair.getKey(), y, pair.getValue());
					Block block = location.getBlock();
					block.setType(Material.AIR);
				}
			}
		}
		stopSpawning(false);
		land.unclaim();
		ArrayList<Pair<Player, Inventory>> remove = new ArrayList<Pair<Player, Inventory>>();
		for(Pair<Player, Inventory> pair: OpenMenu.invs) {
			Player p = pair.getKey();
			if(players.contains(p)) {
				remove.add(pair);
				ArrayList<Pair<Player, ArrayList<Inventory>>> removeCopy = new ArrayList<Pair<Player, ArrayList<Inventory>>>();
				for(Pair<Player, ArrayList<Inventory>> copyPair: CopyAndPaste.structures) {
					if(copyPair.getKey().equals(p)) {
						removeCopy.add(copyPair);
					}
				}
				CopyAndPaste.structures.removeAll(removeCopy);
			}
		}
		OpenMenu.invs.removeAll(remove);
	}
}
