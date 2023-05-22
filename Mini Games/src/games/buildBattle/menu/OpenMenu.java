package games.buildBattle.menu;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import games.buildBattle.BuildBattle;
import games.game.Game;
import games.main.Main;
import games.main.Pair;

public class OpenMenu implements Listener {

	public static ArrayList<Pair<Player, Inventory>> invs = new ArrayList<Pair<Player, Inventory>>();
	static ItemStack fillbox = new ItemStack(Material.STONE);
	static ItemStack particleSpawner = new ItemStack(Material.BLAZE_POWDER);
	static ItemStack clear = new ItemStack(Material.BARRIER);
	static ItemStack heads = new ItemStack(Material.PLAYER_HEAD);
	static ItemStack copy = new ItemStack(Material.WOODEN_AXE);
	static ItemStack removeEntity = new ItemStack(Material.BARRIER);
	static ItemStack darkness = new ItemStack(Material.CLOCK);
	static ItemStack sound = new ItemStack(Material.MUSIC_DISC_CHIRP);
	
	public static ArrayList<Pair<Player, Boolean>> dark = new ArrayList<Pair<Player, Boolean>>();
	
	@EventHandler
	public void interact(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		@SuppressWarnings("deprecation")
		ItemStack item = player.getItemInHand();
		if(Main.buildbattle_menu.equals(item)) {
			if(!Pair.getKeys(invs).contains(player)) {
				invs.add(new Pair<Player, Inventory>(player, loadInventory()));
				CopyAndPaste.loadStructures(player);
			}
			player.openInventory(Pair.getValue(player, invs).get(0));
		}
	}
	
	public Inventory loadInventory() {
		Inventory inv = Main.getPlugin(Main.class).getServer().createInventory(null, 54, ChatColor.BLUE + "Menu");
		addToInv(ChatColor.AQUA + "Set the block of your box", inv, fillbox, 31, new ArrayList<String>(Arrays.asList(ChatColor.GOLD + "Replace this block with the block you want to set the box to")));
		addToInv(ChatColor.AQUA + "Add particles", inv, particleSpawner, 29);
		addToInv(ChatColor.RED + "Clear your plot", inv, clear, 53);
		addToInv(ChatColor.AQUA + "Heads", inv, heads, 33);
		addToInv(ChatColor.AQUA + "Copy and Paste Structures into your world", inv, copy, 14);
		addToInv(ChatColor.RED + "Remove all entities in your plot", inv, removeEntity, 10);
		addToInv(ChatColor.AQUA + "Click to change how light your plot is", inv, darkness, 16, new ArrayList<String>(Arrays.asList(ChatColor.GOLD + "Darkness: " + ChatColor.RED + "Off")));
		addToInv(ChatColor.AQUA + "Add sounds", inv, sound, 12);
		return inv;
	}
	
	public static void loadExtraInvs() {
		Particles.loadParticles();
		Clear.loadConfirm();
		Heads.loadHeads();
		CopyAndPaste.loadCopy();
		Sounds.loadSounds();
	}

	@EventHandler
	public void click(InventoryClickEvent event) {
		Player player = Main.getPlugin(Main.class).getServer().getPlayer(event.getWhoClicked().getName());
		Inventory i = event.getClickedInventory();
		if(Pair.getKeys(invs).contains(player)) {
			ItemStack item = event.getCurrentItem();
			if(Pair.getValue(player, invs).get(0).equals(i)) {	
				if(Pair.getValue(player, invs).get(0).getItem(31).equals(item)) {
					ItemStack fill = event.getCursor();
					if(FillBox.isMaterialOk(fill.getType())) {
						Pair.getValue(player, invs).get(0).getItem(31).setType(fill.getType());
						ArrayList<Pair<Integer, Integer>> plot = Pair.getValue(player, ((BuildBattle) Game.getGame(player)).plots).get(0);
						Integer x = Collections.min(Pair.getKeys(plot));
						Integer z = Collections.min(Pair.getValues(plot));
						FillBox.fillWall(x, z, fill.getType());
						if(player.getLocation().getBlockY() == 199) {
							player.getLocation().setY(player.getLocation().getY() + 1);
						}
					}else {
						player.sendMessage(ChatColor.RED + "You cant set the box to this block");
					}
				} else if(particleSpawner.equals(item)){
					player.openInventory(Particles.particleInv);
				} else if(clear.equals(item)) {
					Clear.confirm(player);
				} else if(heads.equals(item)) {
					player.openInventory(Heads.headsInv);
				} else if(copy.equals(item)) {
					player.openInventory(CopyAndPaste.copyorpaste);
				} else if(removeEntity.equals(item)) {
					ArrayList<Pair<Player, Entity>> entities = Pair.getPairFromKey(player, EntitySpawner.entitys);
					for(Entity entity: Pair.getValues(entities)) {
						entity.remove();
					}
					EntitySpawner.entitys.removeAll(entities);
				} else if(Pair.getValue(player, invs).get(0).getItem(16).equals(item)) {
					ItemMeta meta = item.getItemMeta();
					String lore = Pair.getValue(player, invs).get(0).getItem(16).getItemMeta().getLore().get(0);
					if(lore.contains("Off")) {
						lore = lore.replace(ChatColor.RED + "Off", ChatColor.GREEN + "On");
						player.removePotionEffect(PotionEffectType.NIGHT_VISION);
						Pair.getPairFromKey(player, dark).get(0).setValue(true);
					} else {
						lore = lore.replace(ChatColor.GREEN + "On", ChatColor.RED + "Off");
						player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false, false));
						Pair.getPairFromKey(player, dark).get(0).setValue(false);
					}
					meta.setLore(new ArrayList<String>(Arrays.asList(lore)));
					item.setItemMeta(meta);
				} else if(sound.equals(item)) {
					player.openInventory(Sounds.soundInv.get(0));
				}
				event.setCancelled(true);
			} else if(Particles.particleInv.equals(player.getOpenInventory().getTopInventory())) {
				event.setCancelled(true);
				if(Particles.particleInv.equals(i)) {
					if(item != null) {
						if(Pair.getValue(item, Particles.particleNames).size() != 0) {
							if(Particles.getParticles(player).size() < 10) {
								Particle particle = Pair.getValue(item, Particles.particleNames).get(0);
								new Particles(player, particle);
							} else {
								player.sendMessage(ChatColor.RED + "You have too many particles");
							}
						} else if(item.equals(Particles.cancel)) {
							Particles.removeP(player);
						}	
					}		
				}
			} else if(Clear.confirm.equals(player.getOpenInventory().getTopInventory())) {
				event.setCancelled(true);
				if(Clear.confirm.equals(i)) {
					if(item != null) {
						player.closeInventory();
						if(Clear.ok.equals(item)) {
							ArrayList<Pair<Integer, Integer>> plot = Pair.getValue(player, ((BuildBattle) Game.getGame(player)).plots).get(0);
							Integer x = Collections.min(Pair.getKeys(plot));
							Integer z = Collections.min(Pair.getValues(plot));;
							Clear.clear(x, z);
						}
					}
				}
			} else if(Heads.headsInv.equals(player.getOpenInventory().getTopInventory())) {
				event.setCancelled(true);
				if(Heads.headsInv.equals(i)) {
					if(item != null) {
						if(item.equals(Heads.search)) {
							Heads.askForName(player);
						} else {
							String choice = item.getItemMeta().getDisplayName();
							Heads.choosecat(choice, player);
						}
					}
				}
			} else if(Heads.alphabet.contains(player.getOpenInventory().getTopInventory()) || Heads.blocks.contains(player.getOpenInventory().getTopInventory()) || Heads.characters.contains(player.getOpenInventory().getTopInventory()) || Heads.color.contains(player.getOpenInventory().getTopInventory()) || Heads.devices.contains(player.getOpenInventory().getTopInventory()) || Heads.food.contains(player.getOpenInventory().getTopInventory()) || Heads.games.contains(player.getOpenInventory().getTopInventory()) || Heads.alphabet.contains(player.getOpenInventory().getTopInventory()) || Heads.interior.contains(player.getOpenInventory().getTopInventory()) || Heads.miscellaneous.contains(player.getOpenInventory().getTopInventory()) || Heads.mobs.contains(player.getOpenInventory().getTopInventory()) || Heads.pokemon.contains(player.getOpenInventory().getTopInventory())) {
				event.setCancelled(true);
				String cat = event.getView().getTitle().substring(2);
				ArrayList<Inventory> invs = Heads.getInventorys(cat.toLowerCase());
				Integer pos = invs.indexOf(player.getOpenInventory().getTopInventory());
				if(player.getOpenInventory().getTopInventory().equals(i)) {
					if(item != null) {
						if(item.equals(Heads.back)) {
							player.openInventory(invs.get(pos - 1));
						} else if(item.equals(Heads.forward)) {
							player.openInventory(invs.get(pos + 1));
						} else {
							player.getInventory().addItem(item);
						}
					}
				}
			} else if(CopyAndPaste.copyorpaste.equals(player.getOpenInventory().getTopInventory())) {
				event.setCancelled(true);
				if(item != null) {
					if(item.equals(CopyAndPaste.choose)) {
						player.openInventory(Pair.getValue(player, CopyAndPaste.structures).get(0).get(0));
					} else if(item.equals(CopyAndPaste.copy)) {
						player.getInventory().addItem(CopyAndPaste.copyAxe);
					} else if(item.equals(CopyAndPaste.paste)) {
						player.getInventory().addItem(CopyAndPaste.pasteAxe);
					}
				}
			} else if(Pair.getValue(player, CopyAndPaste.structures).get(0).contains(player.getOpenInventory().getTopInventory())) {
				event.setCancelled(true);
				if(item != null) {
					ArrayList<Inventory> invs = Pair.getValue(player, CopyAndPaste.structures).get(0);
					if(item.equals(CopyAndPaste.copyStructure)) {
						CopyAndPaste.copy(player);
					} else if(item.equals(Heads.forward)) {
						player.openInventory(invs.get(invs.indexOf(player.getOpenInventory().getTopInventory()) + 1));
					} else if(item.equals(Heads.back)) {
						player.openInventory(invs.get(invs.indexOf(player.getOpenInventory().getTopInventory()) - 1));
					} else {
						CopyAndPaste.paste(item.getItemMeta().getDisplayName(), player.getUniqueId().toString());
					}
				}
			} else if(Sounds.soundInv.contains(player.getOpenInventory().getTopInventory())) {
				event.setCancelled(true);
				if(item != null) {
					if(item.equals(Sounds.cancel)) {
						Sounds.removeP(player);
					} else if(item.equals(Heads.forward)) {
						player.openInventory(Sounds.soundInv.get(Sounds.soundInv.indexOf(player.getOpenInventory().getTopInventory()) + 1));
					} else if(item.equals(Heads.back)) {
						player.openInventory(Sounds.soundInv.get(Sounds.soundInv.indexOf(player.getOpenInventory().getTopInventory()) - 1));
					} else {
						if(Sounds.getSounds(player).size() < 10) {
							new Sounds(player, item).addSound();
						} else {
							player.sendMessage(ChatColor.RED + "You have too many sounds");
						}
					}
				}
			}
		}
	}
	
	static void addToInv(String name, Inventory inv, ItemStack item, int slot, List<String> lore) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		item.setItemMeta(meta);
		inv.setItem(slot, item);
	}
	
	public static void addToInv(String name, Inventory inv, Material material, int slot) {
		ItemStack item = new ItemStack(material);
		addToInv(name, inv, item, slot);
	}
	
	static void addToInv(String name, Inventory inv, ItemStack item, int slot) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		inv.setItem(slot, item);
	}

	static void addToInv(String name, Inventory inv, Material material, int slot, List<String> lore) {
		ItemStack item = new ItemStack(material);
		addToInv(name, inv, item, slot, lore);
	}
	
	@EventHandler
	public void drag(InventoryDragEvent event) {
		Player player = Main.getPlugin(Main.class).getServer().getPlayer(event.getWhoClicked().getName());
		Inventory i = event.getInventory();
		try {
			if(Pair.getValue(player, invs).get(0).equals(i) || player.getOpenInventory().getTopInventory().equals(Particles.particleInv) || player.getOpenInventory().getTopInventory().equals(Clear.confirm) || player.getOpenInventory().getTopInventory().equals(Heads.headsInv)) {
				event.setCancelled(true);
			}
		}catch(IndexOutOfBoundsException e) {}
	}
	
	
}
