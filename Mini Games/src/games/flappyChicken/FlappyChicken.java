package games.flappyChicken;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Hex;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import games.flappyChicken.Pillar.Type;
import games.game.Game;
import games.main.DataBase;
import games.main.Main;
import net.minecraft.server.v1_16_R3.WorldServer;
import net.minecraft.server.v1_16_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_16_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_16_R3.PacketPlayOutTitle.EnumTitleAction;

public class FlappyChicken extends Game implements Listener {
	
	boolean started = false;
	public static ArrayList<FlappyChicken> games = new ArrayList<FlappyChicken>();
	public ArrayList<Pillar> pillars = new ArrayList<Pillar>();
	boolean cancel = false;
	Player player;
	public Chicken chicken = new Chicken(this);
	public static final World world = Main.getPlugin(Main.class).getServer().getWorld("world");
	static final WorldServer worldnms = ((CraftWorld) world).getHandle();
	static final Location location = new Location(world, 16.5, 125, -127.5, 180, 0);
	double startTime = System.currentTimeMillis();
	static final Location chickenLoc = new Location(world, 16.5, 126, -132, 90, 0);
	public Cloud cloud;
	boolean speed = false;
	
	public void start(Player player) {
		Main.getPlugin(Main.class).getServer().getPluginManager().registerEvents(this, Main.getPlugin(Main.class));
		this.player = player;
		player.teleport(location);
		player.setGameMode(GameMode.ADVENTURE);
		player.getInventory().clear();
		player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false, false));
		counter(3);
	}
	
	public FlappyChicken(Player player) {
		super(new ArrayList<Player>(Arrays.asList(player)));
		games.add(this);
		try {
			player.setResourcePack("https://storage.live.com/items/4F14B5D8A3513AFD%2143581?authkey=AFGPz3fbnM-EFc8", Hex.decodeHex("5039bc1672a441f87c48419b0c57801ec1d23407".toCharArray()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		new BukkitRunnable() {

			@Override
			public void run() {
				if(!cancel) {
					start(player);
				}
			}
			
		}.runTaskLater(Main.getPlugin(Main.class), 200);
	}

	void timer() {
		new BukkitRunnable() {

			@Override
			public void run() {
				if(cancel) {
					this.cancel();
				}
				double currentTime = System.currentTimeMillis();
				double time = currentTime - startTime;
				double seconds = time / 1000;
				PacketPlayOutTitle packet = new PacketPlayOutTitle(EnumTitleAction.ACTIONBAR, ChatSerializer.a("{\"text\":\"" + seconds + "\"}"));
				((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
			}
			
		}.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
	}
	
	void counter(int count) {
		ChatColor color;
		if(count == 3) {
			color = ChatColor.GREEN;
		} else if(count == 2) {
			color = ChatColor.GOLD;
		} else if(count == 1) {
			color = ChatColor.RED;
		} else {
			PacketPlayOutTitle packet = new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{\"text\":\"" + ChatColor.GREEN + "GO!\"}"));
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 100, 100);
			chicken.spawn();
			timer();
			cloud = new Cloud(this);
			spawnNewPillars();
			started = true;
			new BukkitRunnable() {

				@Override
				public void run() {
					spawnNewPillars();
				}
				
			}.runTaskLater(Main.getPlugin(Main.class), 100);
			return;
		}
		PacketPlayOutTitle packet = new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{\"text\":\"" + color + count + "\"}"));
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 100, 100);
		new BukkitRunnable() {

			@Override
			public void run() {
				counter(count - 1);
			}
			
		}.runTaskLater(Main.getPlugin(Main.class), 20);
	}
	
	@EventHandler
	public void move(PlayerMoveEvent event) {
		if(event.getPlayer().equals(player)) {
			Location to = event.getTo();
			Location from = event.getFrom();
			double y = to.getY();
			if(to.getY() - from.getY() > 0) {
				chicken.jump();
				y = from.getY();
			}
			event.setTo(new Location(from.getWorld(), from.getX(), y, from.getZ(), from.getYaw(), from.getPitch()));
		}
	}
	
	public void spawnNewPillars() {
		int upSize = ((int) (Math.random() * 6 + 1)) + 5;
		int downSize = 16 - upSize;
		Pillar up = new Pillar(upSize, Type.UP, this);
		Pillar down = new Pillar(downSize, Type.DOWN, this);
		up.spawn();
		down.spawn();
		pillars.add(up);
		pillars.add(down);
	}
	
	public void finish() {
		double time = (System.currentTimeMillis() - startTime) / 1000;
		player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 100, 100);
		PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{\"text\":\"" + ChatColor.GOLD + "Game ended\"}"));
		PacketPlayOutTitle subtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, ChatSerializer.a("{\"text\":\"" + ChatColor.YELLOW + "Your score was " + ChatColor.WHITE + time + "\"}"));
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitle);
		try {
			Statement statement = DataBase.connection().createStatement();
			ResultSet result = statement.executeQuery("SELECT * FROM FlappyChicken WHERE UUID = '" + player.getUniqueId() + "'");
			if(result.next()) {
				int played = result.getInt("Played");
				double total = result.getDouble("TotalScore");
				double highest = result.getDouble("HighestScore");
				played++;
				total += time;
				if(time > highest) {
					highest = time;
					PacketPlayOutTitle t = new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{\"text\":\"" + ChatColor.GOLD + "NEW HIGHEST SCORE!\"}"));
					((CraftPlayer) player).getHandle().playerConnection.sendPacket(t);
					player.sendMessage(ChatColor.YELLOW + "NEW HIGHEST SCORE: " + ChatColor.WHITE + time);
				}
				statement.executeUpdate("UPDATE FlappyChicken SET Played = " + played + " WHERE UUID = '" + player.getUniqueId() + "'");
				statement.executeUpdate("UPDATE FlappyChicken SET TotalScore = " + total + " WHERE UUID = '" + player.getUniqueId() + "'");
				statement.executeUpdate("UPDATE FlappyChicken SET HighestScore = " + highest + " WHERE UUID = '" + player.getUniqueId() + "'");
				statement.executeUpdate("UPDATE FlappyChicken SET AverageScore = " + (total / played) + " WHERE UUID = '" + player.getUniqueId() + "'");
			} else {
				statement.executeUpdate("INSERT INTO FlappyChicken (UUID, HighestScore, TotalScore, AverageScore, Played) VALUES ('" + player.getUniqueId() + "', " + time + ", " + time + ", " + time + ", 1)");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		end();
	}
	
	public void end() {
		PlayerMoveEvent.getHandlerList().unregister(this);
		chicken.getBukkitEntity().remove();
		if(started) {
			cloud.getBukkitEntity().remove();
		}
		for(Pillar pillar: pillars) {
			pillar.kill();
		}
		cancel = true;
		games.remove(this);
		endGame();
	}
}
