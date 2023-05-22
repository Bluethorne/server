package games.mind;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import games.game.Game;
import games.main.Main;
import net.minecraft.server.v1_16_R3.EntityArmorStand;
import net.minecraft.server.v1_16_R3.EntityTypes;

public class Mind extends Game {
	
	public static Location location = new Location(Main.getPlugin(Main.class).getServer().getWorld("world"), 0, 0, 0);
	EntityArmorStand[] a = {new EntityArmorStand(EntityTypes.ARMOR_STAND, ((CraftWorld) Main.getPlugin(Main.class).getServer().getWorld("world")).getHandle()), new EntityArmorStand(EntityTypes.ARMOR_STAND, ((CraftWorld) Main.getPlugin(Main.class).getServer().getWorld("world")).getHandle()), new EntityArmorStand(EntityTypes.ARMOR_STAND, ((CraftWorld) Main.getPlugin(Main.class).getServer().getWorld("world")).getHandle()), new EntityArmorStand(EntityTypes.ARMOR_STAND, ((CraftWorld) Main.getPlugin(Main.class).getServer().getWorld("world")).getHandle())};
	
	private Mind(ArrayList<Player> players) {
		super(players);
		players.get(0).teleport(location);
		for(int x = 0; x < 4; x++) {
			a[x].setLocation(0 + x, 0, 0, 0, 0);
			a[x].setInvisible(true);
		}
		Inventory inv = players.get(0).getInventory();
		inv.setItem(0, new ItemStack(Material.RED_DYE));
		inv.setItem(1, new ItemStack(Material.ORANGE_DYE));
		inv.setItem(2, new ItemStack(Material.YELLOW_DYE));
		inv.setItem(3, new ItemStack(Material.GREEN_DYE));
		inv.setItem(4, new ItemStack(Material.BLUE_DYE));
		inv.setItem(5, new ItemStack(Material.WHITE_DYE));
		inv.setItem(6, new ItemStack(Material.BROWN_DYE));
		inv.setItem(7, new ItemStack(Material.BLACK_DYE));
	}
	
	public static void join(Player player) {
		new Mind(new ArrayList<Player>(Arrays.asList(player)));
	}
	
	public void end() {
		
	}

}
