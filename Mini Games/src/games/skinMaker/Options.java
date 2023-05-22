package games.skinMaker;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import games.main.Main;

public class Options extends ItemStack implements Listener{

	SkinMaker maker;
	static final String okString = ChatColor.GREEN + "OK";
	static final String nothing = ChatColor.WHITE + "";
	static final String bodyString = ChatColor.YELLOW + "Body Part: ";
	static final String colorString = "Colour: ";
	static final Inventory staticColourInv = Main.getPlugin(Main.class).getServer().createInventory(null, 54, "");
	String option;
	Inventory colourInv = Main.getPlugin(Main.class).getServer().createInventory(null, 54, ChatColor.BLUE + "Colour Inventory");
	
	static {
		addItem(ChatColor.BLACK + "Black", Material.INK_SAC);
		addItem(ChatColor.WHITE + "White", Material.BONE_MEAL);
		addItem(ChatColor.GRAY + "Gray", Material.LIGHT_GRAY_DYE);
		addItem(ChatColor.DARK_GRAY + "Dark Gray", Material.GRAY_DYE);
		addItem(ChatColor.GOLD + "Brown", Material.COCOA_BEANS);
		addItem(ChatColor.RED + "Red", Material.RED_DYE);
		addItem(ChatColor.GOLD + "Orange", Material.ORANGE_DYE);
		addItem(ChatColor.YELLOW + "Yellow", Material.YELLOW_DYE);
		addItem(ChatColor.GREEN + "Green", Material.LIME_DYE);
		addItem(ChatColor.DARK_GREEN + "Dark green", Material.GREEN_DYE);
		addItem(ChatColor.BLUE + "Light Blue", Material.LIGHT_BLUE_DYE);
		addItem(ChatColor.BLUE + "Cyan", Material.CYAN_DYE);
		addItem(ChatColor.DARK_BLUE + "Dark Blue", Material.LAPIS_LAZULI);
		addItem(ChatColor.DARK_PURPLE + "Purple", Material.PURPLE_DYE);
		addItem(ChatColor.LIGHT_PURPLE + "Magenta", Material.MAGENTA_DYE);
		addItem(ChatColor.LIGHT_PURPLE + "Pink", Material.PINK_DYE);
		
		setItem(ChatColor.YELLOW + "Your current color", 31, Material.LEATHER_HELMET);
		setItem(ChatColor.RED + "Add Red", 39, Material.RED_TERRACOTTA);
		setItem(ChatColor.GREEN + "Add Green", 40, Material.GREEN_TERRACOTTA);
		setItem(ChatColor.BLUE + "Add Blue", 41, Material.BLUE_TERRACOTTA);
		setItem(ChatColor.RED + "Remove Red", 48, Material.RED_TERRACOTTA);
		setItem(ChatColor.GREEN + "Remove Green", 49, Material.GREEN_TERRACOTTA);
		setItem(ChatColor.BLUE + "Remove Blue", 50, Material.BLUE_TERRACOTTA);
	}
	
	static void addItem(String string, Material material) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(string);
		item.setItemMeta(meta);
		staticColourInv.addItem(item);
	}
	
	static void setItem(String string, int num, Material material) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(string);
		item.setItemMeta(meta);
		staticColourInv.setItem(num, item);
	}
	
	public Options(String option, SkinMaker maker) {
		super(getMat(option));
		if(option.equals("color")) {
			colourInv.setContents(staticColourInv.getContents());
		}
		Main.getPlugin(Main.class).getServer().getPluginManager().registerEvents(this, Main.getPlugin(Main.class));
		this.option = option;
		this.maker = maker;
		setName();
	}
	
	static Material getMat(String option) {
		if(option.equals("body")) {
			return Material.PLAYER_HEAD;
		} else if(option.equals("ok")) {
			return Material.GREEN_TERRACOTTA;
		} else if(option.equals("color")) {
			return Material.LEATHER_HELMET;
		} else if(option.equals("nothing")) {
			return Material.GRAY_STAINED_GLASS_PANE;
		}
		return null;
	}
	
	String getName(String option) {
		if(option.equals("ok")) {
			return okString;
		} else if(option.equals("body")) {
			return bodyString + maker.part;
		} else if(option.equals("color")) {
			ChatColor color = getColor(maker.color);
			return net.md_5.bungee.api.ChatColor.of(new java.awt.Color(maker.color.getRed(), maker.color.getGreen(), maker.color.getBlue())) + colorString + color.name().replace("_", " ");
		} else if(option.equals("nothing")) {
			return nothing;
		}
		return null;
	}
	
	@EventHandler
	public void interact(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if(maker.stop) {
			stop();
		} else {
			if(player.equals(maker.player)) {
				Action action = event.getAction();
				if(action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR)) {
					ItemStack item = event.getItem();
					if(item != null) {
						if(this.getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName())) {
							if(option.equals("body")) {
								BodyPart[] parts = BodyPart.values();
								BodyPart nextPart = parts[(Arrays.asList(parts).indexOf(maker.part) + 1) % parts.length];
								maker.part = nextPart;
								setName();
								player.getInventory().setItem(0, this);
								maker.despawnNPCs();
								maker.spawnNPCs();
							} else if(option.equals("color")) {
								player.openInventory(colourInv);
							} else if(option.equals("ok")) {
								maker.ok();
							}
						}
					}
					event.setCancelled(true);
					player.getInventory().setHelmet(new ItemStack(Material.AIR));
				}
			}
		}
	}
	
	void setName() {
		ItemMeta meta = this.getItemMeta();
		meta.setDisplayName(getName(option));
		this.setItemMeta(meta);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void click(InventoryClickEvent event) {
		Player player = ((Player) event.getWhoClicked());
		if(maker.stop) {
			stop();
		} else {
			if(player.equals(maker.player)) {
				event.setCancelled(true);
				if(option.equals("color")) {
					if(event.getClickedInventory() != null) {
						if(event.getClickedInventory().equals(colourInv)) {
							ItemStack item = event.getCurrentItem();
							if(item != null) {
								int slot = event.getSlot();
								if(slot == 39) {
									if(maker.color.getRed() + 16 <= 255) {
										Color newColor = Color.fromRGB(maker.color.getRed() + 16, maker.color.getGreen(), maker.color.getBlue());
										maker.color = newColor;
									}
								} else if(slot == 40) {
									if(maker.color.getGreen() + 16 <= 255) {
										Color newColor = Color.fromRGB(maker.color.getRed(), maker.color.getGreen() + 16, maker.color.getBlue());
										maker.color = newColor;
									}
								} else if(slot == 41) {
									if(maker.color.getBlue() + 16 <= 255) {
										Color newColor = Color.fromRGB(maker.color.getRed(), maker.color.getGreen(), maker.color.getBlue() + 16);
										maker.color = newColor;
									}
								} else if(slot == 48) {
									if(maker.color.getRed() - 16 >= 0) {
										Color newColor = Color.fromRGB(maker.color.getRed() - 16, maker.color.getGreen(), maker.color.getBlue());
										maker.color = newColor;
									}
								} else if(slot == 49) {
									if(maker.color.getGreen() - 16 >= 0) {
										Color newColor = Color.fromRGB(maker.color.getRed(), maker.color.getGreen() - 16, maker.color.getBlue());
										maker.color = newColor;
									}
								} else if(slot == 50) {
									if(maker.color.getBlue() - 16 >= 0) {
										Color newColor = Color.fromRGB(maker.color.getRed(), maker.color.getGreen(), maker.color.getBlue() - 16);
										maker.color = newColor;
									}
								} else {
									maker.color = DyeColor.getByDyeData(item.getData().getData()).getColor();
								}
								setName();
								ItemStack iteminv = colourInv.getItem(31);
								LeatherArmorMeta meta = (LeatherArmorMeta) iteminv.getItemMeta();
								meta.setColor(maker.color);
								iteminv.setItemMeta(meta);
								colourInv.setItem(31, iteminv);
								player.updateInventory();
								LeatherArmorMeta meta2 = (LeatherArmorMeta) this.getItemMeta();
								meta2.setColor(maker.color);
								this.setItemMeta(meta2);
								player.getInventory().setItem(1, this);
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void drop(PlayerDropItemEvent event) {
		if(maker.stop) {
			stop();
		} else {
			if(event.getPlayer().equals(maker.player)) {
				event.setCancelled(true);
			}
		}
	}
	
	void stop() {
		PlayerDropItemEvent.getHandlerList().unregister(this);
		InventoryClickEvent.getHandlerList().unregister(this);
		PlayerInteractEvent.getHandlerList().unregister(this);
		InventoryCloseEvent.getHandlerList().unregister(this);
	}
	
	static final HashMap<ChatColor, Color> chatColors = new HashMap<ChatColor, Color>() {private static final long serialVersionUID = 1L;{put(ChatColor.BLACK, Color.fromRGB(0, 0, 0));put(ChatColor.DARK_BLUE, Color.fromRGB(0, 0, 170));put(ChatColor.DARK_GREEN, Color.fromRGB(0, 170, 0));put(ChatColor.DARK_AQUA, Color.fromRGB(0, 170, 170));put(ChatColor.DARK_RED, Color.fromRGB(170, 0, 0));put(ChatColor.DARK_PURPLE, Color.fromRGB(170, 0, 170));put(ChatColor.GOLD, Color.fromRGB(255, 170, 0));put(ChatColor.GRAY, Color.fromRGB(170, 170, 170));put(ChatColor.DARK_GRAY, Color.fromRGB(85, 85, 85));put(ChatColor.BLUE, Color.fromRGB(85, 85, 255));put(ChatColor.GREEN, Color.fromRGB(85, 255, 85));put(ChatColor.AQUA, Color.fromRGB(85, 255, 255));put(ChatColor.RED, Color.fromRGB(255, 85, 85));put(ChatColor.LIGHT_PURPLE, Color.fromRGB(255, 85, 255));put(ChatColor.YELLOW, Color.fromRGB(255, 255, 85));put(ChatColor.WHITE, Color.fromRGB(255, 255, 255));}};
	
	ChatColor getColor(Color c) {
		int r = c.getRed();
		int b = c.getBlue();
		int g = c.getGreen();
		ChatColor closest = null;
		int closestNum = 256 * 3;
		for(Entry<ChatColor, Color> entry: chatColors.entrySet()) {
			ChatColor chatColor = entry.getKey();
			Color color = entry.getValue();
			int diff = difference(r, color.getRed()) + difference(g, color.getGreen()) + difference(b, color.getBlue());
			if(diff < closestNum) {
				closest = chatColor;
				closestNum = diff;
			}
		}
		return closest;
	}
	
	int difference(int k1, int k2) {
		if(k1 < k2) {
			return k2 - k1;
		} else if(k2 < k1){
			return k1 - k2;
		}
		return -1;
	}
}
