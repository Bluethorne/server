package games.main;

import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import games.game.Game;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;

public class PacketListener implements Listener {
	
	private void injectPlayer(Player player) {
		ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
			
			@Override
			public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {System.out.println(packet);
				if(!Game.playersPlaying.contains(player)) {
					HubNPC.testForClickNPC(packet, player);
				}
				super.channelRead(channelHandlerContext, packet);
			}
			
			@Override 
			public void write(ChannelHandlerContext context, Object packet, io.netty.channel.ChannelPromise promise) throws Exception {
				System.out.println(packet);
				super.write(context, packet, promise);
			}
			
		};
		
		ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline();
		pipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler);
	} 
	
	private void removePlayer(Player player) {
		Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
		channel.eventLoop().submit(() -> {
			channel.pipeline().remove(player.getName());
			return null;
		});
	}
	
	@EventHandler
	public void onleave(PlayerQuitEvent event) {
		removePlayer(event.getPlayer());
	}
	
	@EventHandler
	public void onjoin(PlayerJoinEvent event) {
		injectPlayer(event.getPlayer());
	}

}
