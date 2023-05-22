package games.buildBattle;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import games.buildBattle.menu.OpenMenu;
import games.game.Game;
import games.game.PlayerWinEvent;
import games.main.Array;
import games.main.Main;
import games.main.Pair;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_16_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_16_R3.PacketPlayOutTitle.EnumTitleAction;

public class Voter implements Listener{
	
	Player player = null;
	Listener voter = this;
	Game game;
	public ArrayList<Player> players = new ArrayList<Player>();
	ArrayList<Pair<Player, Integer>> voted = new ArrayList<Pair<Player, Integer>>();
	ArrayList<Pair<Player, Integer>> votes = new ArrayList<Pair<Player, Integer>>();
	ArrayList<Pair<Player, ArrayList<Pair<Integer, Integer>>>> plots = new ArrayList<Pair<Player, ArrayList<Pair<Integer, Integer>>>>();
	
	public Voter(ArrayList<Pair<Player, ArrayList<Pair<Integer, Integer>>>> plots, Game game) {
		this.players = Pair.getKeys(plots);
		this.game = game;
		this.plots = plots;
		for(Player player: players) {
			votes.add(new Pair<>(player, 0));
		}
		voter();
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void vote(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if(players.contains(player)) {
			event.setCancelled(true);
			if(!player.equals(this.player)) {
				for(Pair<Player, Integer> pair : votes) {
					if(pair.getKey().equals(this.player)) {
						if(Pair.getKeys(voted).contains(player)) {
							pair.setValue(pair.getValue() - Pair.getValue(player, voted).get(0));
						}
						ItemStack item = player.getItemInHand();
						Integer point = 0;
						if(item.getType().equals(Material.RED_TERRACOTTA)) {
							point = 1;
							player.sendMessage(ChatColor.GOLD + "You have voted " + ChatColor.DARK_RED + "RUBBISH" + ChatColor.GOLD + " for " + this.player.getName());
						} else if(item.getType().equals(Material.PINK_TERRACOTTA)) {
							point = 2;
							player.sendMessage(ChatColor.GOLD + "You have voted " + ChatColor.RED + "BAD" + ChatColor.GOLD + " for " + this.player.getName());
						} else if(item.getType().equals(Material.ORANGE_TERRACOTTA)) {
							point = 3;
							player.sendMessage(ChatColor.GOLD + "You have voted " + ChatColor.YELLOW + "OK" + ChatColor.GOLD + " for " + this.player.getName());
						} else if(item.getType().equals(Material.LIME_TERRACOTTA)) {
							point = 4;
							player.sendMessage(ChatColor.GOLD + "You have voted " + ChatColor.GREEN + "GOOD" + ChatColor.GOLD + " for " + this.player.getName());
						} else if(item.getType().equals(Material.GREEN_TERRACOTTA)) {
							point = 5;
							player.sendMessage(ChatColor.GOLD + "You have voted " + ChatColor.DARK_GREEN + "AMAZING" + ChatColor.GOLD + " for " + this.player.getName());
						}
						pair.setValue(pair.getValue() + point);
						if(Pair.getKeys(voted).contains(player)) {
							Pair.setValue(point, player, voted);
						} else {
							voted.add(new Pair<>(player, point));
						}
					}
				}
			} else {
				if(!player.getItemInHand().getType().equals(Material.AIR)) {
					player.sendMessage(ChatColor.RED + "You cannot vote for yourself");
				}
			}
		}
	}
	
	@EventHandler
	public void place(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if(players.contains(player)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void destroy(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if(players.contains(player)) {
			event.setCancelled(true);
		}
	}
	
	Integer count;
	
	public void voter() {
		Main.getPlugin(Main.class).getServer().getPluginManager().registerEvents(this, Main.getPlugin(Main.class));
		count = players.size() - 1;
		new BukkitRunnable() {

			@Override
			public void run() {
				if(count < 0) {
					PlayerInteractEvent.getHandlerList().unregister(voter);
					player = null;
					winners();
					this.cancel();
				} else {
					player = votes.get(count).getKey();
					boolean dark = Pair.getValue(player, OpenMenu.dark).get(0);
					Integer x = Collections.min(Pair.getKeys(plots.get(count).getValue())) + 16;
					Integer z = Collections.min(Pair.getValues(plots.get(count).getValue())) + 16;
					Location location = new Location(Main.getPlugin(Main.class).getServer().getWorld("world"), x, 201, z);
					IChatBaseComponent comp = ChatSerializer.a("{\"text\":\"" + ChatColor.GOLD + "You are voting for " + player.getName() + "\"}");
					PacketPlayOutTitle packet = new PacketPlayOutTitle(EnumTitleAction.TITLE, comp);
					for(Player p: players) {
						p.teleport(location);
						p.sendMessage(ChatColor.GOLD + "You are voting for " + player.getName());
						((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
						if(dark == true) {
							p.removePotionEffect(PotionEffectType.NIGHT_VISION);
						} else {
							p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false, false));
						}
					}
				}
				voted.clear();
				count--;
			}
			
		}.runTaskTimer(Main.getPlugin(Main.class), 0, 400);
	}
	
	void winners() {
		ArrayList<Pair<Player, Integer>> order = Array.copy(votes);
		boolean done = false;
		while(done == false) {
			done = true;
			for(int count = 0; count < order.size(); count++) {
				if(count > 0) {
					if(order.get(count - 1).getValue() < order.get(count).getValue()) {
						Pair<Player, Integer> temp = order.get(count - 1);
						order.set(count - 1, order.get(count));
						order.set(count, temp);
						done = false;
					}
				}
			}
		}
		if(Pair.getKeys(order).size() != 0) {
			PlayerWinEvent event = new PlayerWinEvent(this.game, order);
			Main.getPlugin(Main.class).getServer().getPluginManager().callEvent(event);
		}
		new BukkitRunnable() {

			@Override
			public void run() {
				BlockBreakEvent.getHandlerList().unregister(voter);
				BlockPlaceEvent.getHandlerList().unregister(voter);
			}
			
		}.runTaskLater(Main.getPlugin(Main.class), 420);
	}
}
