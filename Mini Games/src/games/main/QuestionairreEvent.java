package games.main;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class QuestionairreEvent extends Event {

	static HandlerList handlers = new HandlerList();
	UUID uuid;
	Player player;
	String answer;
	
	public QuestionairreEvent(UUID uuid, String answer, Player player) {
		this.uuid = uuid;
		this.answer = answer;
		this.player = player;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

}
