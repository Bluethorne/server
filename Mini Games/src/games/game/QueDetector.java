package games.game;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import games.buildBattle.BuildBattle;
import games.main.Array;
import games.main.Main;
import games.survivalGames.SurvivalGames;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_16_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_16_R3.PacketPlayOutTitle.EnumTitleAction;

public class QueDetector {
	
	int time = 30;
	boolean timed = false;
	
	public void runQueDetect(ArrayList<Player> que, String game) {
		if(que.size() >= 2) {
			if(!timed) {
				timed = true;
				timer(que);
			}
			for(Player player: que) {
				player.sendMessage(ChatColor.GOLD + "Enough players have joined the queue. The game will start in 30 seconds");
			}
			new BukkitRunnable() {

				@Override
				public void run() {
					if(que.size() >= 2) {
						ArrayList<Player> players = Array.copy(que);
						if(game.equals(BuildBattle.class.getSimpleName())) {
							new BuildBattle(players);
							BuildBattle.que.clear();
						} else if(game.equals(SurvivalGames.class.getSimpleName())) {
							new SurvivalGames(players);
							SurvivalGames.que.clear();
						}
					} else {
						for(Player player: que) {
							player.sendMessage(ChatColor.GOLD + "There are not enough players to start");
						}
					}
				}
				
			}.runTaskLater(Main.getPlugin(Main.class), 600);	
		}
	}
	
	public void timer(ArrayList<Player> que) {
		new BukkitRunnable() {

			@Override
			public void run() {
				for(Player player: que) {
					IChatBaseComponent comp = ChatSerializer.a("{\"text\":\"§a" + time + " seconds until your game starts.\"}");
					((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutTitle(EnumTitleAction.ACTIONBAR, comp));
					
				}
				if(time > 0) {
					time--;
					timer(que);
				} else {
					this.cancel();
					time = 30;
					timed = false;
				}
			}
			
		}.runTaskLater(Main.getPlugin(Main.class), 20);
	}
	
}
