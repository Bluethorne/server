package games.main;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Chat implements Listener{
	
	@EventHandler
	public void chat(AsyncPlayerChatEvent event) {
		event.setMessage(event.getMessage().replace("&", "§"));
		event.setFormat("%1$s §f%2$s");
	}
}
