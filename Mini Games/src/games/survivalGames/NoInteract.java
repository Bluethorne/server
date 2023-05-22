package games.survivalGames;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import games.main.Main;
import games.main.Pair;

public class NoInteract implements Listener{
	
	public ArrayList<Player> players = new ArrayList<Player>();
	ArrayList<Chest> chests = new ArrayList<Chest>();
	ArrayList<Pair<Integer, Integer>> land = new ArrayList<Pair<Integer, Integer>>();
	ArrayList<Entity> droppedItems = new ArrayList<Entity>();
	SurvivalGames game;
	
	public NoInteract(ArrayList<Player> players, ArrayList<Chest> chests, ArrayList<Pair<Integer, Integer>> land, SurvivalGames game) {
		this.players = players;
		this.game = game;
		this.chests = chests;
		this.land = land;
		Main.getPlugin(Main.class).getServer().getPluginManager().registerEvents(this, Main.getPlugin(Main.class));
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void fish(ProjectileHitEvent event) {
		Entity entity = event.getHitEntity();
		if(entity != null) {
			if(entity instanceof Player) {
				Player player = (Player) entity;
				if(players.contains(player)) {
					if(event.getEntity() instanceof FishHook) {
						FishHook hook = (FishHook) event.getEntity();
						ProjectileSource source = hook.getShooter();
						if(source instanceof Player) {
							if(players.contains(source)) {
								Player pl = (Player) source;
								player.damage(0.001);
								Vector p = player.getLocation().toVector();
								Vector h = pl.getLocation().toVector();
								Vector s = h.subtract(p);
								double up = 0.375;
								if(player.isOnGround()) {
									up = 0;
								}
								Vector d = new Vector(s.getX() * - 1, up, s.getZ() * -1);
								Vector v = d.normalize();
								player.setVelocity(player.getVelocity().add(v));
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void interact(PlayerInteractEvent e) {
		Action action = e.getAction();
		Player player = e.getPlayer();
		if(action.equals(Action.PHYSICAL)) {
			if(players.contains(player)) {
				e.setCancelled(true);
			}
		} else if(action.equals(Action.RIGHT_CLICK_BLOCK)) {
			Block block = e.getClickedBlock();
			Material type = block.getType();
			if(type.equals(Material.CHEST)) {
				if(chests.contains((Chest) block.getState())) {
					if(!players.contains(player) || game.kills.dead.contains(player)) {
						e.setCancelled(true);
					}
				} else {
					if(players.contains(player)) {
						e.setCancelled(true);
					}
				}
			} else if(!(type.equals(Material.ENCHANTING_TABLE))) {
				if(type.isInteractable()) {
					e.setCancelled(true);
				}
			} 
		}
	}
	
	@EventHandler
	public void item(EntityPickupItemEvent e) {
		Entity entity = e.getEntity();
		Item item = e.getItem();
		if((!droppedItems.contains(item) && players.contains(entity)) || (!players.contains(entity) && droppedItems.contains(item))) {
			e.setCancelled(true);
		}
		if(game.kills.dead.contains(entity)) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void drop(PlayerDropItemEvent e) {
		Player player = e.getPlayer();
		if(players.contains(player)) {
			droppedItems.add(e.getItemDrop());
		}
	}
	
	@EventHandler
	public void target(EntityTargetEvent e) {
		if(players.contains(e.getTarget())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void damage(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		if(entity instanceof Player) {
			Player player = (Player) entity;
			if(game.kills.dead.contains(player) || game.ended) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void kill(EntityDamageByEntityEvent e) {
		if((players.contains(e.getDamager()) && !players.contains(e.getEntity())) || (players.contains(e.getEntity()) && (!players.contains(e.getDamager()) && !(e.getDamager() instanceof Arrow || e.getDamager() instanceof TNTPrimed)))) {
			e.setCancelled(true);
		}
		if(e.getDamager() instanceof TNTPrimed) {
			if(!Pair.getValues(TNT.tnt).contains(e.getDamager())) {
				e.setCancelled(true);
			}
		} else if(e.getDamager() instanceof Arrow) {
			if(!arrows.contains(e.getDamager())) {
				e.setCancelled(true);
			}
		}
		if(game.kills.dead.contains(e.getDamager())) {
			e.setCancelled(true);
		}
	}
	
	ArrayList<Entity> arrows = new ArrayList<Entity>();
	
	@EventHandler
	public void arrow(EntityShootBowEvent event) {
		if(players.contains(event.getEntity())) {
			arrows.add(event.getProjectile());
		}
	}
	
	@EventHandler
	public void food(FoodLevelChangeEvent event) {
		Entity entity = event.getEntity();
		if(entity instanceof Player) {
			Player player = (Player) entity;
			if(game.kills.dead.contains(player)) {
				event.setCancelled(true);
			} else if(players.contains(player) ) {
				player.setSaturation(0);
			}
		}
	}
	
	public void end() {
		PlayerInteractEvent.getHandlerList().unregister(this);
		EntityTargetEvent.getHandlerList().unregister(this);
		EntityDamageEvent.getHandlerList().unregister(this);
		EntityPickupItemEvent.getHandlerList().unregister(this);
		PlayerDropItemEvent.getHandlerList().unregister(this);
		EntityDamageEvent.getHandlerList().unregister(this);
		ProjectileHitEvent.getHandlerList().unregister(this);
		for(Entity entity: droppedItems) {
			entity.remove();
		}
	}
} 
