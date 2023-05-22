package games.main;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import games.buildBattle.BuildBattle;
import games.game.Game;
import games.survivalGames.SurvivalGames;

public class HubProtection implements Listener{
	
	@EventHandler
	public void prot(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		if(entity instanceof Player) {
			Player player = (Player) entity;
			if(!Game.playersPlaying.contains(player) || SurvivalGames.que.contains(player) || BuildBattle.que.contains(player)) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void food(FoodLevelChangeEvent event) {
		if(!Game.playersPlaying.contains(event.getEntity()) || SurvivalGames.que.contains(event.getEntity()) || BuildBattle.que.contains(event.getEntity())) {
			event.setCancelled(true);
		}
	}
	
}
