package games.buildBattle.menu;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import games.main.GUI;
import games.main.Main;

public class Heads implements Listener {
	
	public static Inventory headsInv = Main.getPlugin(Main.class).getServer().createInventory(null, 18, ChatColor.BLUE + "Heads");
	public static ItemStack search = new ItemStack(Material.PLAYER_HEAD);
	public static ItemStack back = new ItemStack(Material.ARROW);
	public static ItemStack forward = new ItemStack(Material.ARROW);
	
	public static ArrayList<Inventory> alphabet = new ArrayList<Inventory>();
	public static ArrayList<Inventory> blocks = new ArrayList<Inventory>();
	public static ArrayList<Inventory> characters = new ArrayList<Inventory>();
	public static ArrayList<Inventory> color = new ArrayList<Inventory>();
	public static ArrayList<Inventory> devices = new ArrayList<Inventory>();
	public static ArrayList<Inventory> food = new ArrayList<Inventory>();
	public static ArrayList<Inventory> games = new ArrayList<Inventory>();
	public static ArrayList<Inventory> interior = new ArrayList<Inventory>();
	public static ArrayList<Inventory> miscellaneous = new ArrayList<Inventory>();
	public static ArrayList<Inventory> mobs = new ArrayList<Inventory>();
	public static ArrayList<Inventory> pokemon = new ArrayList<Inventory>();
	
	public static void loadHeads() {
		OpenMenu.addToInv(ChatColor.AQUA + "Alphabet", headsInv, Material.OAK_SIGN , 0);
		OpenMenu.addToInv(ChatColor.AQUA + "Blocks", headsInv, Material.GRASS_BLOCK , 1);
		OpenMenu.addToInv(ChatColor.AQUA + "Characters", headsInv, Material.PLAYER_HEAD , 2);
		OpenMenu.addToInv(ChatColor.AQUA + "Color", headsInv, Material.PINK_CONCRETE , 3);
		OpenMenu.addToInv(ChatColor.AQUA + "Devices", headsInv, Material.OBSERVER , 4);
		OpenMenu.addToInv(ChatColor.AQUA + "Food", headsInv, Material.PORKCHOP , 5);
		OpenMenu.addToInv(ChatColor.AQUA + "Games", headsInv, Material.DIAMOND , 6);
		OpenMenu.addToInv(ChatColor.AQUA + "Interior", headsInv, Material.PAINTING , 7);
		OpenMenu.addToInv(ChatColor.AQUA + "Miscellaneous", headsInv, Material.MUSIC_DISC_CAT , 8);
		OpenMenu.addToInv(ChatColor.AQUA + "Mobs", headsInv, Material.ZOMBIE_HEAD , 9);
		OpenMenu.addToInv(ChatColor.AQUA + "Pokemon", headsInv, Material.TURTLE_EGG , 10);
		
		ItemMeta meta = search.getItemMeta();
		meta.setDisplayName(ChatColor.AQUA + "Search for a players head");
		search.setItemMeta(meta);
		headsInv.setItem(17, search);
		
		ItemMeta metab = back.getItemMeta();
		metab.setDisplayName(ChatColor.YELLOW + "Back");
		back.setItemMeta(metab);
		
		ItemMeta metaf = forward.getItemMeta();
		metaf.setDisplayName(ChatColor.YELLOW + "Forward");
		forward.setItemMeta(metaf);
		
		loadHeadInvs();
	}
	
	static void loadHeadInvs() {
		File file = new File("plugins/miniGames/BuildBattle/heads.txt");
		try {
			List<String> lines = Files.readAllLines(file.toPath());
			for(String line: lines) {
				List<String> parts = new ArrayList<String>(Arrays.asList(line.split(" ")));
				Inventory inv = getInventory(parts.get(0));
				ItemStack head = new ItemStack(Material.PLAYER_HEAD);
				SkullMeta meta = (SkullMeta) head.getItemMeta();
				GameProfile profile = new GameProfile(UUID.fromString(parts.get(2)), null);
                profile.getProperties().put("textures", new Property("textures", parts.get(3)));
                try {
                    Field profileField = meta.getClass().getDeclaredField("profile");
                    profileField.setAccessible(true);
                    profileField.set(meta, profile);
                } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
                    e1.printStackTrace();
                }
                String name = parts.get(1).replace("_", " ");
                meta.setDisplayName(ChatColor.YELLOW + name);
                head.setItemMeta(meta);
                inv.addItem(head);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static Inventory getInventory(String string) {
		ArrayList<Inventory> invs = getInventorys(string);
		if(invs.size() != 0) {
			if(invs.get(invs.size() - 1).getItem(44) == null) {
				return invs.get(invs.size() - 1);
			} else {
				Inventory inv = Main.getPlugin(Main.class).getServer().createInventory(null, 54, ChatColor.BLUE + string.substring(0, 1).toUpperCase() + string.substring(1));
				inv.setItem(45, back);
				invs.get(invs.size() - 1).setItem(53, forward);
				invs.add(inv);
				return inv;
			}
		} else {
			Inventory inv = Main.getPlugin(Main.class).getServer().createInventory(null, 54, ChatColor.BLUE + string.substring(0, 1).toUpperCase() + string.substring(1));
			invs.add(inv);
			return inv;
		}
	}
	
	public static void choosecat(String choice, Player player) {
		player.openInventory(getInventorys(choice.toLowerCase().substring(2)).get(0));
	}
	
	public static ArrayList<Inventory> getInventorys(String string) {
		try {
			@SuppressWarnings("unchecked")
			ArrayList<Inventory> inv = (ArrayList<Inventory>) Heads.class.getField(string).get(null);
			return inv;
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void askForName(Player player) {
		GUI.AnvilGUI gui = new GUI.AnvilGUI(player, new GUI.AnvilGUI.AnvilClickEventHandler() {
			@Override
            public void onAnvilClick(GUI.AnvilGUI.AnvilClickEvent e) {
                if(e.getSlot() == GUI.AnvilGUI.AnvilSlot.OUTPUT && e.hasText()) {
                	e.setWillClose(true);
                    String name = e.getText();
					try {
						URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
						Scanner scanner = new Scanner(url.openStream());
						try {
							scanner.next();
							@SuppressWarnings("deprecation")
							OfflinePlayer owner = Main.getPlugin(Main.class).getServer().getOfflinePlayer(name);
							ItemStack head = new ItemStack(Material.PLAYER_HEAD);
							SkullMeta meta = (SkullMeta) head.getItemMeta();
							meta.setDisplayName(ChatColor.YELLOW + name + "'s head");
							meta.setOwningPlayer(owner);
							head.setItemMeta(meta);
							player.getInventory().addItem(head);
						} catch(NoSuchElementException e2) {
							player.sendMessage(ChatColor.RED + name + " is not a player");
						}
						scanner.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}	
                }
            }
        });
        ItemStack i = new ItemStack(Material.PAPER);
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Enter a players name");
        i.setItemMeta(meta);
        gui.setSlot(GUI.AnvilGUI.AnvilSlot.INPUT_LEFT, i);
        gui.setTitle(ChatColor.BLUE + "Enter a players name");
        gui.open();
	}
}
