package games.buildBattle.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import games.buildBattle.BuildBattle;
import games.game.Game;
import games.main.Main;
import games.main.Pair;
import net.md_5.bungee.api.ChatColor;

public class EntitySpawner implements Listener {
	
	public static ArrayList<Pair<Player, Entity>> entitys = new ArrayList<Pair<Player, Entity>>();
	
	@EventHandler
	public void spawn(PlayerInteractEvent event) {
		ItemStack item = event.getItem();
		Block block = event.getClickedBlock();
		Player player = event.getPlayer();
		event.getBlockFace();
		if(Game.playersPlaying.contains(player)) {
			for(Game game: Game.games) {
				if(game instanceof BuildBattle) {
					BuildBattle buildbattle = (BuildBattle) game;
					if(buildbattle.players.contains(player)) {
						if(item != null) {
							switch(item.getType()) {
							case BAT_SPAWN_EGG:
								spawn(EntityType.BAT, block, player);
								break;
							case BEE_SPAWN_EGG:
								spawn(EntityType.BEE, block, player);
								break;
							case BLAZE_SPAWN_EGG:
								spawn(EntityType.BLAZE, block, player);
								break;
							case CAT_SPAWN_EGG:
								spawn(EntityType.CAT, block, player);
								break;
							case CAVE_SPIDER_SPAWN_EGG:
								spawn(EntityType.CAVE_SPIDER, block, player);
								break;
							case CHICKEN_SPAWN_EGG:
								spawn(EntityType.CHICKEN, block, player);
								break;
							case COD_SPAWN_EGG:
								spawn(EntityType.COD, block, player);
								break;
							case COW_SPAWN_EGG:
								spawn(EntityType.COW, block, player);
								break;
							case CREEPER_SPAWN_EGG:
								spawn(EntityType.CREEPER, block, player);
								break;
							case DOLPHIN_SPAWN_EGG:
								spawn(EntityType.DOLPHIN, block, player);
								break;
							case DONKEY_SPAWN_EGG:
								spawn(EntityType.DONKEY, block, player);
								break;
							case DROWNED_SPAWN_EGG:
								spawn(EntityType.DROWNED, block, player);
								break;
							case ELDER_GUARDIAN_SPAWN_EGG:
								spawn(EntityType.ELDER_GUARDIAN, block, player);
								break;
							case ENDERMAN_SPAWN_EGG:
								spawn(EntityType.ENDERMAN, block, player);
								break;
							case ENDERMITE_SPAWN_EGG:
								spawn(EntityType.ENDERMITE, block, player);
								break;
							case EVOKER_SPAWN_EGG:
								spawn(EntityType.EVOKER, block, player);
								break;
							case FOX_SPAWN_EGG:
								spawn(EntityType.FOX, block, player);
								break;
							case GHAST_SPAWN_EGG:
								spawn(EntityType.GHAST, block, player);
								break;
							case GUARDIAN_SPAWN_EGG:
								spawn(EntityType.GUARDIAN, block, player);
								break;
							case HORSE_SPAWN_EGG:
								spawn(EntityType.HORSE, block, player);
								break;
							case HUSK_SPAWN_EGG:
								spawn(EntityType.HUSK, block, player);
								break;
							case LLAMA_SPAWN_EGG:
								spawn(EntityType.LLAMA, block, player);
								break;
							case MAGMA_CUBE_SPAWN_EGG:
								spawn(EntityType.MAGMA_CUBE, block, player);
								break;
							case MOOSHROOM_SPAWN_EGG:
								spawn(EntityType.MUSHROOM_COW, block, player);
								break;
							case MULE_SPAWN_EGG:
								spawn(EntityType.MULE, block, player);
								break;
							case OCELOT_SPAWN_EGG:
								spawn(EntityType.OCELOT, block, player);
								break;
							case PANDA_SPAWN_EGG:
								spawn(EntityType.PANDA, block, player);
								break;
							case PARROT_SPAWN_EGG:
								spawn(EntityType.PARROT, block, player);
								break;
							case PHANTOM_SPAWN_EGG:
								spawn(EntityType.PHANTOM, block, player);
								break;
							case PIG_SPAWN_EGG:
								spawn(EntityType.PIG, block, player);
								break;
							case PILLAGER_SPAWN_EGG:
								spawn(EntityType.PILLAGER, block, player);
								break;
							case POLAR_BEAR_SPAWN_EGG:
								spawn(EntityType.POLAR_BEAR, block, player);
								break;
							case PUFFERFISH_SPAWN_EGG:
								spawn(EntityType.PUFFERFISH, block, player);
								break;
							case RABBIT_SPAWN_EGG:
								spawn(EntityType.RABBIT, block, player);
								break;
							case RAVAGER_SPAWN_EGG:
								spawn(EntityType.RAVAGER, block, player);
								break;
							case SALMON_SPAWN_EGG:
								spawn(EntityType.SALMON, block, player);
								break;
							case SHEEP_SPAWN_EGG:
								spawn(EntityType.SHEEP, block, player);
								break;
							case SHULKER_SPAWN_EGG:
								spawn(EntityType.SHULKER, block, player);
								break;
							case SILVERFISH_SPAWN_EGG:
								spawn(EntityType.SILVERFISH, block, player);
								break;
							case SKELETON_HORSE_SPAWN_EGG:
								spawn(EntityType.SKELETON_HORSE, block, player);
								break;
							case SKELETON_SPAWN_EGG:
								spawn(EntityType.SKELETON, block, player);
								break;
							case SLIME_SPAWN_EGG:
								spawn(EntityType.SLIME, block, player);
								break;
							case SPIDER_SPAWN_EGG:
								spawn(EntityType.SPIDER, block, player);
								break;
							case SQUID_SPAWN_EGG:
								spawn(EntityType.SQUID, block, player);
								break;
							case STRAY_SPAWN_EGG:
								spawn(EntityType.STRAY, block, player);
								break;
							case TRADER_LLAMA_SPAWN_EGG:
								spawn(EntityType.TRADER_LLAMA, block, player);
								break;
							case TROPICAL_FISH_SPAWN_EGG:
								spawn(EntityType.TROPICAL_FISH, block, player);
								break;
							case TURTLE_SPAWN_EGG:
								spawn(EntityType.TURTLE, block, player);
								break;
							case VILLAGER_SPAWN_EGG:
								spawn(EntityType.VILLAGER, block, player);
								break;
							case VINDICATOR_SPAWN_EGG:
								spawn(EntityType.VINDICATOR, block, player);
								break;
							case WANDERING_TRADER_SPAWN_EGG:
								spawn(EntityType.WANDERING_TRADER, block, player);
								break;
							case WITHER_SKELETON_SPAWN_EGG:
								spawn(EntityType.WITHER_SKELETON, block, player);
								break;
							case WOLF_SPAWN_EGG:
								spawn(EntityType.WOLF, block, player);
								break;
							case ZOMBIE_HORSE_SPAWN_EGG:
								spawn(EntityType.ZOMBIE_HORSE, block, player);
								break;
							case ZOMBIFIED_PIGLIN_SPAWN_EGG:
								spawn(EntityType.ZOMBIFIED_PIGLIN, block, player);
								break;
							case ZOMBIE_SPAWN_EGG:
								spawn(EntityType.ZOMBIE, block, player);
								break;
							case ZOMBIE_VILLAGER_SPAWN_EGG:
								spawn(EntityType.ZOMBIE_VILLAGER, block, player);
								break;
							case STRIDER_SPAWN_EGG:
								spawn(EntityType.STRIDER, block, player);
								break;
							case PIGLIN_SPAWN_EGG:
								spawn(EntityType.PIGLIN, block, player);
								break;
							case ZOGLIN_SPAWN_EGG:
								spawn(EntityType.ZOGLIN, block, player);
								break;
							case HOGLIN_SPAWN_EGG:
								spawn(EntityType.HOGLIN, block, player);
								break;
							default:
								break;
							}
						}
					}
				}
			}
		}
	}
	
	void spawn(EntityType entitytype, Block block, Player player) {
		List<Block> blocks = player.getLineOfSight(null, 7);
		if(Pair.getValue(player, entitys).size() <= 30) {
			if(block != null) {
				World world = Main.getPlugin(Main.class).getServer().getWorld("world");
				Entity entity = world.spawnEntity(new Location(world, -1, 200, -1), entitytype);
				entity.teleport(blocks.get(blocks.size() - 2).getLocation());
				entitys.add(new Pair<Player, Entity>(player, entity));
			}
		} else {
			player.sendMessage(ChatColor.RED + "You cannot spawn any more entities");
		}
	}
	
	@EventHandler
	public void die(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		if(Pair.getValues(entitys).contains(entity)) {
			entitys.remove(Pair.getPairFromValue(entity, entitys).get(0));
		}
	}

}
