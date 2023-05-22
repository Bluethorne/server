package games.buildBattle.menu;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import games.buildBattle.BuildBattle;
import games.game.Game;
import games.main.GUI;
import games.main.Main;
import games.main.Pair;
import games.main.Structures;
import net.minecraft.server.v1_16_R3.DefinedStructure;

public class CopyAndPaste implements Listener {
	
	public static Inventory copyorpaste = Main.getPlugin(Main.class).getServer().createInventory(null, 27, ChatColor.BLUE + "Copy or Paste");
	public static ArrayList<Pair<Player, ArrayList<Inventory>>> structures = new ArrayList<Pair<Player, ArrayList<Inventory>>>();
	
	public static ItemStack copy = new ItemStack(Material.WOODEN_AXE);
	public static ItemStack paste = new ItemStack(Material.WOODEN_AXE);
	public static ItemStack choose = new ItemStack(Material.STONE);
	
	public static ItemStack copyAxe = new ItemStack(Material.WOODEN_AXE);
	public static ItemStack pasteAxe = new ItemStack(Material.WOODEN_AXE);
	
	public static ItemStack copyStructure = new ItemStack(Material.STONE);
	
	public static void loadCopy() {
		ItemMeta copyMeta = copyStructure.getItemMeta();
		copyMeta.setDisplayName(ChatColor.AQUA + "Click to make a new structure");
		copyStructure.setItemMeta(copyMeta);
		
		ItemMeta cmeta = copy.getItemMeta();
		cmeta.setDisplayName(ChatColor.AQUA + "Click to get the Copy Axe");
		copy.setItemMeta(cmeta);
		copyorpaste.setItem(11 , copy);
		
		ItemMeta pmeta = paste.getItemMeta();
		pmeta.setDisplayName(ChatColor.AQUA + "Click to get the Paste Axe");
		paste.setItemMeta(pmeta);
		copyorpaste.setItem(15 , paste);
		
		ItemMeta smeta = choose.getItemMeta();
		smeta.setDisplayName(ChatColor.AQUA + "Click to set the structure to paste");
		choose.setItemMeta(smeta);
		copyorpaste.setItem(13 , choose);
		
		ItemMeta cameta = copyAxe.getItemMeta();
		cameta.setDisplayName(ChatColor.AQUA + "Copy Axe");
		List<String> clore = new ArrayList<String>();
		clore.add(ChatColor.GOLD + "Left click on a block to set the start location of the copy");
		clore.add(ChatColor.GOLD + "Right click on a block to set the corner location of the copy");
		cameta.setLore(clore);
		copyAxe.setItemMeta(cameta);
		
		ItemMeta pameta = pasteAxe.getItemMeta();
		pameta.setDisplayName(ChatColor.AQUA + "Paste Axe");
		List<String> plore = new ArrayList<String>();
		plore.add(ChatColor.GOLD + "Left click on a block to set the location of the paste");
		pameta.setLore(plore);
		pasteAxe.setItemMeta(pameta);
	}
	
	public static void loadStructures(Player player) {
		Inventory inv = Main.getPlugin(Main.class).getServer().createInventory(null, 54, ChatColor.BLUE + "Structure Inventory");
		File folder = new File("plugins/miniGames/BuildBattle/Structures/");
		File[] files = folder.listFiles();
		for(File file: files) {
			if(file.isFile()) {
				String name = file.getName().replaceAll(".nbt", "");
				ItemStack item = new ItemStack(Material.STONE);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(ChatColor.YELLOW + name);
				item.setItemMeta(meta);
				inv.addItem(item);
			}
		}
		inv.setItem(49, copyStructure);
		structures.add(new Pair<Player, ArrayList<Inventory>>(player, new ArrayList<Inventory>(Arrays.asList(inv))));
	}
	
	@EventHandler
	public void interact(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		Action action = event.getAction();
		ConfigurationSection config = Main.getPlugin(Main.class).getConfig();
		if(item != null) {
			if(item.equals(copyAxe)) {
				event.setCancelled(true);
				if(action.equals(Action.RIGHT_CLICK_BLOCK)) {
					Location location = event.getClickedBlock().getLocation();
					player.sendMessage(ChatColor.GREEN + "The corner copy location is set to " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
					player.playNote(player.getLocation(), Instrument.CHIME, new Note(10));
					config.set("Structures." + player.getUniqueId() + ".copy.corner", location);
					Main.getPlugin(Main.class).saveConfig();
				} else if(action.equals(Action.LEFT_CLICK_BLOCK)) {
					Location location = event.getClickedBlock().getLocation();
					player.sendMessage(ChatColor.GREEN + "The copy location is set to " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
					player.playNote(player.getLocation(), Instrument.CHIME, new Note(10));
					config.set("Structures." + player.getUniqueId() + ".copy.set", location);
					Main.getPlugin(Main.class).saveConfig();
				}
			} else if(item.equals(pasteAxe)) {
				event.setCancelled(true);
				if(action.equals(Action.LEFT_CLICK_BLOCK)) {
					Location location = event.getClickedBlock().getLocation();
					player.sendMessage(ChatColor.GREEN + "The paste location is set to " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
					player.playNote(player.getLocation(), Instrument.CHIME, new Note(10));
					config.set("Structures." + player.getUniqueId() + ".paste", location);
					Main.getPlugin(Main.class).saveConfig();
				}
			}
		}
	}
	
	public static void copy(Player player) {
		GUI.AnvilGUI gui = new GUI.AnvilGUI(player, new GUI.AnvilGUI.AnvilClickEventHandler() {
			@Override
            public void onAnvilClick(GUI.AnvilGUI.AnvilClickEvent event) {
                if(event.getSlot() == GUI.AnvilGUI.AnvilSlot.OUTPUT && event.hasText()) {
                	event.setWillClose(true);
                    String name = event.getText();
                    ConfigurationSection config = Main.getPlugin(Main.class).getConfig();
                    Location location1 = config.getLocation("Structures." + player.getUniqueId() + ".copy.set");
                    Location location2 = config.getLocation("Structures." + player.getUniqueId() + ".copy.corner");
                    try {
                    	if(location1 != null && location2 != null) {
                    		Location[] locations = {location1, location2};
                    		if(!new File("plugins/miniGames/BuildBattle/Structures/" + player.getUniqueId().toString() + "/").exists()) {
                    			new File("plugins/miniGames/BuildBattle/Structures/" + player.getUniqueId().toString() + "/").mkdir();
                    		}	
                    		Structures.saveStructure(new File("plugins/miniGames/BuildBattle/Structures/" + player.getUniqueId().toString() + "/" + name.replace(" ", "_")), locations);
                    		ItemStack item = new ItemStack(Material.OAK_PLANKS);
                    		ItemMeta meta = item.getItemMeta();
                    		meta.setDisplayName(ChatColor.YELLOW + name);
                    		item.setItemMeta(meta);
                    		Inventory inv = Pair.getValue(player, structures).get(0).get(Pair.getValue(player, structures).size() - 1);
                    		if(inv.getItem(44) == null) {
                    			inv.setItem(inv.firstEmpty(), item);
                    		} else {
                    			inv.setItem(53, Heads.forward);
                    			Inventory newInv = Main.getPlugin(Main.class).getServer().createInventory(null, 54, ChatColor.BLUE + "Structure Inventory");
                    			newInv.setItem(45, Heads.back);
                    			newInv.addItem(item);
                    			Pair.getValue(player, structures).get(0).add(newInv);
                    		}
                    	} else {
                    		player.sendMessage(ChatColor.RED + "You do not have two copy locations");
                    	}
                    } catch(IllegalArgumentException e) {
                   		player.sendMessage(ChatColor.RED + "This is too big to copy");
                   	}
                }
            }
        });
		ItemStack i = new ItemStack(Material.PAPER);
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Enter name of copied blocks");
        i.setItemMeta(meta);
        gui.setSlot(GUI.AnvilGUI.AnvilSlot.INPUT_LEFT, i);
        gui.setTitle(ChatColor.BLUE + "Enter the name of the blocks");
        gui.open();
	}
	
	public static void paste(String name, String uuid) {
		ConfigurationSection config = Main.getPlugin(Main.class).getConfig();
		Location location = config.getLocation("Structures." + uuid + ".paste");
		if(location != null) {
			ArrayList<Pair<Integer, Integer>> plot = null;
			for(Game game: Game.games) {
				Player player = Main.getPlugin(Main.class).getServer().getPlayer(UUID.fromString(uuid));
				if(game.players.contains(player)) {
					BuildBattle buildBattle = (BuildBattle) game;
					plot = Pair.getValue(player, buildBattle.plots).get(0);
				}
			}
			ArrayList<Integer> xs = Pair.getKeys(plot);
			int min = Collections.min(xs);
			World world = Main.getPlugin(Main.class).getServer().getWorld("world");
			ArrayList<Pair<Location, DefinedStructure>> keepArea = new ArrayList<Pair<Location, DefinedStructure>>(Arrays.asList(
					new Pair<Location, DefinedStructure>(new Location(world, min - 31, 169, -31), Structures.createSingleStructure(new Location[]{new Location(world, min - 31, 169, -31), new Location(world, min, 200, 0)})),
					new Pair<Location, DefinedStructure>(new Location(world, min, 169, -31), Structures.createSingleStructure(new Location[]{new Location(world, min, 169, -31), new Location(world, min + 31, 200, 0)})),
					new Pair<Location, DefinedStructure>(new Location(world, min + 31, 169, -31), Structures.createSingleStructure(new Location[]{new Location(world, min + 31, 169, -31), new Location(world, min + 62, 200, 0)})),
					new Pair<Location, DefinedStructure>(new Location(world, min - 31, 200, -31), Structures.createSingleStructure(new Location[]{new Location(world, min - 31, 200, -31), new Location(world, min, 231, 0)})),
					new Pair<Location, DefinedStructure>(new Location(world, min, 200, -31), Structures.createSingleStructure(new Location[]{new Location(world, min, 200, -31), new Location(world, min + 31, 231, 0)})),
					new Pair<Location, DefinedStructure>(new Location(world, min + 31, 200, -31), Structures.createSingleStructure(new Location[]{new Location(world, min + 31, 200, -31), new Location(world, min + 62, 231, 0)})),
					new Pair<Location, DefinedStructure>(new Location(world, min - 31, 231, -31), Structures.createSingleStructure(new Location[]{new Location(world, min - 31, 231, -31), new Location(world, min, 262, 0)})),
					new Pair<Location, DefinedStructure>(new Location(world, min, 231, -31), Structures.createSingleStructure(new Location[]{new Location(world, min, 231, -31), new Location(world, min + 31, 262, 0)})),
					new Pair<Location, DefinedStructure>(new Location(world, min + 31, 231, -31), Structures.createSingleStructure(new Location[]{new Location(world, min + 31, 231, -31), new Location(world, min + 62, 262, 0)})),
					new Pair<Location, DefinedStructure>(new Location(world, min - 31, 169, 0), Structures.createSingleStructure(new Location[]{new Location(world, min - 31, 169, 0), new Location(world, min, 200, 31)})),
					new Pair<Location, DefinedStructure>(new Location(world, min, 169, 0), Structures.createSingleStructure(new Location[]{new Location(world, min, 169, 0), new Location(world, min + 31, 200, 31)})),
					new Pair<Location, DefinedStructure>(new Location(world, min + 31, 169, 0), Structures.createSingleStructure(new Location[]{new Location(world, min + 31, 169, 0), new Location(world, min + 62, 200, 31)})),
					new Pair<Location, DefinedStructure>(new Location(world, min - 31, 200, 0), Structures.createSingleStructure(new Location[]{new Location(world, min - 31, 200, 0), new Location(world, min, 231, 31)})),
					new Pair<Location, DefinedStructure>(new Location(world, min + 31, 200, 0), Structures.createSingleStructure(new Location[]{new Location(world, min + 31, 200, 0), new Location(world, min + 62, 231, 31)})),
					new Pair<Location, DefinedStructure>(new Location(world, min - 31, 231, 0), Structures.createSingleStructure(new Location[]{new Location(world, min - 31, 231, 0), new Location(world, min, 262, 31)})),
					new Pair<Location, DefinedStructure>(new Location(world, min, 231, 0), Structures.createSingleStructure(new Location[]{new Location(world, min, 231, 0), new Location(world, min + 31, 262, 31)})),
					new Pair<Location, DefinedStructure>(new Location(world, min + 31, 231, 0), Structures.createSingleStructure(new Location[]{new Location(world, min + 31, 231, 0), new Location(world, min + 62, 262, 31)})),
					new Pair<Location, DefinedStructure>(new Location(world, min - 31, 169, 31), Structures.createSingleStructure(new Location[]{new Location(world, min - 31, 169, 31), new Location(world, min, 200, 62)})),
					new Pair<Location, DefinedStructure>(new Location(world, min, 169, 31), Structures.createSingleStructure(new Location[]{new Location(world, min, 169, 31), new Location(world, min + 31, 200, 62)})),
					new Pair<Location, DefinedStructure>(new Location(world, min + 31, 169, 31), Structures.createSingleStructure(new Location[]{new Location(world, min + 31, 169, 31), new Location(world, min + 62, 200, 62)})),
					new Pair<Location, DefinedStructure>(new Location(world, min - 31, 200, 31), Structures.createSingleStructure(new Location[]{new Location(world, min - 31, 200, 31), new Location(world, min, 231, 62)})),
					new Pair<Location, DefinedStructure>(new Location(world, min, 200, 31), Structures.createSingleStructure(new Location[]{new Location(world, min, 200, 31), new Location(world, min + 31, 231, 62)})),
					new Pair<Location, DefinedStructure>(new Location(world, min + 31, 200, 31), Structures.createSingleStructure(new Location[]{new Location(world, min + 31, 200, 31), new Location(world, min + 62, 231, 62)})),
					new Pair<Location, DefinedStructure>(new Location(world, min - 31, 231, 31), Structures.createSingleStructure(new Location[]{new Location(world, min - 31, 231, 31), new Location(world, min, 262, 62)})),
					new Pair<Location, DefinedStructure>(new Location(world, min, 231, 31), Structures.createSingleStructure(new Location[]{new Location(world, min, 231, 31), new Location(world, min + 31, 262, 62)})),
					new Pair<Location, DefinedStructure>(new Location(world, min + 31, 231, 31), Structures.createSingleStructure(new Location[]{new Location(world, min + 31, 231, 31), new Location(world, min + 62, 262, 62)}))));
			if(new File("plugins/miniGames/BuildBattle/Structures/" + name.replace(" ", "_").substring(2) + ".nbt").exists()) {
				Structures.pasteStructure(new File("plugins/miniGames/BuildBattle/Structures/" + name.replace(" ", "_").substring(2) + ".nbt"),  location);
			} else if(new File("plugins/miniGames/BuildBattle/Structures/" + uuid + "/" + name.replace(" ", "_").substring(2) + ".nbt").exists()) {
				Structures.pasteStructure(new File("plugins/miniGames/BuildBattle/Structures/" + uuid + "/" + name.replace(" ", "_").substring(2) + ".nbt"), location);
			}
			for(Pair<Location, DefinedStructure> structure: keepArea) {
				Structures.insertSingleStructure(structure.getValue(), structure.getKey());
			}
		} else {
			Main.getPlugin(Main.class).getServer().getPlayer(UUID.fromString(uuid)).sendMessage(ChatColor.RED + "You do not have a paste location");
		}
	}
}
