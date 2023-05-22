package games.game;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import games.main.Pair;

public class PlayerWinEvent extends Event {

	Game game;
	Player player;
	ArrayList<Player> order = new ArrayList<Player>();
	ArrayList<Pair<Player, Integer>> scores = new ArrayList<Pair<Player, Integer>>();
	static HandlerList handlers = new HandlerList();
	
	public PlayerWinEvent(Game game, ArrayList<Pair<Player, Integer>> players) {
		ArrayList<Player> o = Pair.getKeys(players);
		this.game = game;
		this.player = o.get(0);
		this.order = o;
		this.scores = players;
	}
	
	public PlayerWinEvent(Game game, ArrayList<Pair<Player, Integer>> kills, Player winner) {
		this.game = game;
		this.player = winner;
		this.order = Pair.getKeys(kills);
		this.scores = kills;
	}
	
	public PlayerWinEvent(Game game, Player player) {
		this.game = game;
		this.player = player;
		this.order = null;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public Game getGame() {
		return game;
	}
	
	public Player getWinner() {
		return player;
	}
	
	public ArrayList<Player> getPlayers() {
		return order;
	}
	
	public ArrayList<Pair<Player, Integer>> getScores() {
		return scores;
	}
	
}
