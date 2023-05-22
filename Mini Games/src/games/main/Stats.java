package games.main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;

public class Stats implements Listener {
	
	static ArrayList<Inventory> inv = new ArrayList<Inventory>();
	static Inventory inventory = Main.getPlugin(Main.class).getServer().createInventory(null, 54, "");
	
	static {
		int count = 0;
		for(int i = 0; i < 54; i++) {
			int num1 = i % 9;
			int num2 = ((int) i / 9);
			if(num1 == 0 || num1 == 8 || num2 == 0 || num2 == 5) {
				ItemStack item = new ItemStack(getMaterial(count));
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(ChatColor.RED + "");
				item.setItemMeta(meta);
				inventory.setItem(i, item);
			}
			count++;
			count %= 2;
		}
	}
	
	static Material getMaterial(int num) {
		if(num == 0) {
			return Material.LIME_STAINED_GLASS_PANE;
		} else if(num == 1) {
			return Material.GREEN_STAINED_GLASS_PANE;
		}
		return null;
	}
	
	@EventHandler
	public void click(InventoryClickEvent event) {
		Inventory i = event.getView().getTopInventory();
		if(inv.contains(i)) {
			event.setCancelled(true);
		}
	}
	
	public static void showStats(OfflinePlayer player, Player show) {
		try {
			Inventory i = Main.getPlugin(Main.class).getServer().createInventory(null, 54, ChatColor.DARK_BLUE + player.getName() + "'s Stats");
			i.setContents(Stats.inventory.getContents());
			FileConfiguration config = Main.getPlugin(Main.class).getConfig();
			Set<String> strings = config.getConfigurationSection("Games.").getKeys(false);
			Statement statement = DataBase.connection().createStatement();
			new BukkitRunnable() {

				@Override
				public void run() {
					try {
						int count = 18;
						ResultSet tables = statement.executeQuery("SELECT TABLE_NAME FROM information_schema.tables WHERE TABLE_SCHEMA = 'Peter184EkQbvInIe7GHAZ8mkzTf';");
						while(tables.next()) {
							if(!(tables.getNString(1).equals("Skins") || tables.getNString(1).equals("Tribes"))) {
								String material = null;
								for(String s: strings) {
									String m = config.getString("Games." + s);
									if(m.equals(tables.getNString(1).replaceAll("(.)([A-Z])", "$1 $2"))) {
										material = s;
									}
								}
								ItemStack item;
								if(material == null) {
									item = new ItemStack(Material.PLAYER_HEAD);
									SkullMeta meta = (SkullMeta) item.getItemMeta();
									meta.setOwningPlayer(player);
									item.setItemMeta(meta);
								} else {
									item = new ItemStack(Material.valueOf(material));
								}
								ItemMeta meta = item.getItemMeta();
								meta.setDisplayName(ChatColor.YELLOW + tables.getNString(1).replaceAll("(.)([A-Z])", "$1 $2"));
								meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
								List<String> lore = new ArrayList<String>();
								try {
									ResultSet result = DataBase.connection().createStatement().executeQuery("SELECT * FROM " + tables.getNString(1) + " WHERE uuid = '" + player.getUniqueId() + "';");
									count+=2;
									while(result.next()) {
										int n = 1;
										for(boolean stop = false; stop == false; n++) {
											try {
												String print = result.getObject(n).toString();
												if(!print.equals(player.getUniqueId().toString()) && !result.getMetaData().getColumnName(n).equals("skinID") && print != null && print != "") {														
													lore.add(ChatColor.GOLD + result.getMetaData().getColumnName(n).replaceAll("(.)([A-Z])", "$1 $2") + ": " + print);														
												}	
											} catch(SQLException | NullPointerException e) {
												stop = true;
											}
										}
									}							
									meta.setLore(lore);
									item.setItemMeta(meta);
									i.setItem(count, item);							
								} catch(MySQLSyntaxErrorException e) {}
							}
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				
			}.runTaskAsynchronously(Main.getPlugin(Main.class));				
			Stats.inv.add(i);
			show.openInventory(i);
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
}
