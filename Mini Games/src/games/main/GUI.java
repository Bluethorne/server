package games.main;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class GUI {
	
	public static class NMSManager {
		   
	    public static final Map<Class<?>, Class<?>> CORRESPONDING_TYPES = new HashMap<Class<?>, Class<?>>();
	   
	    public static Class<?> getPrimitiveType(Class<?> Class) { return CORRESPONDING_TYPES.containsKey(Class) ? CORRESPONDING_TYPES.get(Class) : Class; }
	   
	    public static Class<?>[] toPrimitiveTypeArray(Class<?>[] Classes) {
	        int L = Classes != null ? Classes.length : 0;
	        Class<?>[] T = new Class<?>[L];
	        for(int i = 0; i < L; i++) T[i] = getPrimitiveType(Classes[i]);
	        return T;
	    }
	   
	    public static boolean equalsTypeArray(Class<?>[] Value1, Class<?>[] Value2) {
	        if(Value1.length != Value2.length) return false;
	        for(int i = 0; i < Value1.length; i++) if(!Value1[i].equals(Value2[i]) && !Value1[i].isAssignableFrom(Value2[i])) return false;
	        return true;
	    }
	   
	    public static boolean classListEqual(Class<?>[] Value1, Class<?>[] Value2) {
	        if(Value1.length != Value2.length) return false;
	        for(int i = 0; i < Value1.length; i++) if(Value1[i] != Value2[i]) return false;
	        return true;
	    }
	   
	    public static String getVersion() {
	        String V = Bukkit.getServer().getClass().getPackage().getName();
	       return V.substring(V.lastIndexOf('.') + 1) + ".";
	    }
	   
	    public static boolean useNewVersion() {
	        try {
	            Class.forName("net.minecraft.server." + getVersion() + "ContainerAccess");
	            return true;
	        } catch (Exception e) { return false; }
	    }
	   
	    public static Field getField(Class<?> Class, String Field) {
	        try {
	            Field F = Class.getDeclaredField(Field);
	            F.setAccessible(true);
	            return F;
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	   
	    public static Class<?> getNMSClass(String ClassName) {
	        Class<?> C = null;
	        try { return Class.forName("net.minecraft.server." + getVersion() + ClassName); } catch (Exception e) { e.printStackTrace(); }
	        return C;
	    }
	   
	    public static Method getMethod(Class<?> Class, String ClassName, Class<?>... Parameters) {
	        for(Method M : Class.getMethods()) if(M.getName().equals(ClassName) && (Parameters.length == 0 || classListEqual(Parameters, M.getParameterTypes()))) {
	            M.setAccessible(true);
	            return M;
	        }
	        return null;
	    }
	   
	    public static Method getMethod(String MethodName, Class<?> Class, Class<?>... Parameters) {
	        Class<?>[] T = toPrimitiveTypeArray(Parameters);
	        for(Method M : Class.getMethods()) if(M.getName().equals(MethodName) && equalsTypeArray(toPrimitiveTypeArray(M.getParameterTypes()), T)) return M;
	        return null;
	    }
	   
	    public static Object getHandle(Object Object) {
	        try { return getMethod("getHandle", Object.getClass()).invoke(Object); }
	        catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	   
	    public static Object getPlayerField(Player Player, String Field) throws SecurityException, NoSuchMethodException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
	        Object P = Player.getClass().getMethod("getHandle").invoke(Player);
	        return P.getClass().getField(Field).get(P);
	    }
	   
	    public static Object invokeMethod(String MethodName, Object Parameter) {
	        try { return getMethod(MethodName, Parameter.getClass()).invoke(Parameter); }
	        catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	   
	    public static Object invokeMethodWithArgs(String MethodName, Object Object, Object... Parameters) {
	        try { return getMethod(MethodName, Object.getClass()).invoke(Object, Parameters); }
	        catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	   
	    public static boolean set(Object Object, String Field, Object Value) {
	        Class<?> C = Object.getClass();
	        while(C != null) {
	            try {
	                Field F = C.getDeclaredField(Field);
	                F.setAccessible(true);
	                F.set(Object, Value);
	                return true;
	            } catch (NoSuchFieldException e) { C = C.getSuperclass(); } catch (Exception e) { throw new IllegalStateException(e); }
	        }
	        return false;
	    }
	   
	}
	
	public static class AnvilGUI {
		   
	    private Plugin plugin = Main.getPlugin(Main.class);
	   
	    private boolean colorrename = true;
	   
	    private Player player;
	   
	    private char colorchar = '&';
	   
	    private String title = "";
	   
	    private String defaulttext = "";
	   
	    private Inventory inventory;
	   
	    private HashMap<AnvilSlot, ItemStack> items = new HashMap<AnvilSlot, ItemStack>();
	   
	    private Listener listener;
	   
	    private AnvilClickEventHandler handler;
	   
	    private Class<?> BlockPosition;
	    private Class<?> PacketPlayOutOpenWindow;
	    private Class<?> ContainerAnvil;
	    private Class<?> ChatMessage;
	    private Class<?> EntityHuman;
	    private Class<?> ContainerAccess;
	    private Class<?> Containers;
	   
	    private boolean useNewVersion = NMSManager.useNewVersion();
	   
	    private void loadClasses() {
	       
	        BlockPosition = NMSManager.getNMSClass("BlockPosition");
	        PacketPlayOutOpenWindow = NMSManager.getNMSClass("PacketPlayOutOpenWindow");
	        ContainerAnvil = NMSManager.getNMSClass("ContainerAnvil");
	        ChatMessage = NMSManager.getNMSClass("ChatMessage");
	        EntityHuman = NMSManager.getNMSClass("EntityHuman");
	        if(useNewVersion) {
	            ContainerAccess = NMSManager.getNMSClass("ContainerAccess");
	            Containers = NMSManager.getNMSClass("Containers");
	        }
	       
	    }
	   
	    public enum AnvilSlot {
	       
	        INPUT_LEFT(0), INPUT_RIGHT(1), OUTPUT(2);
	       
	        private int slot;
	       
	        private AnvilSlot(int Slot) { slot = Slot; }
	       
	        public int getSlot() { return slot; }
	       
	        public static AnvilSlot bySlot(int Slot) {
	           
	            for(AnvilSlot AS : values()) if(AS.getSlot() == Slot) return AS;
	           
	            return null;
	           
	        }
	       
	    }
	   
	    public interface AnvilClickEventHandler {
	       
	        public void onAnvilClick(AnvilClickEvent event);
	       
	    }
	   
	    public class AnvilClickEvent {
	       
	        private AnvilSlot slot;
	        private ItemStack item;
	        private String text;
	        private boolean close = false;
	        private boolean destroy = false;
	       
	        public AnvilClickEvent(AnvilSlot Slot, ItemStack Item, String Text) {
	           
	            slot = Slot;
	            item = Item;
	            text = Text;
	           
	        }
	       
	        public AnvilSlot getSlot() { return slot; }
	       
	        public ItemStack getItemStack() { return item; }
	       
	        public void setItemStack(ItemStack Item) {
	           
	            item = Item;
	           
	            inventory.setItem(slot.getSlot(), item);
	           
	        }
	       
	        public boolean hasText() { return text != null; }
	       
	        public String getText() { return text != null ? text : defaulttext; }
	       
	        public boolean getWillClose() { return close; }
	       
	        public void setWillClose(boolean Close) { close = Close; }
	       
	        public boolean getWillDestroy() { return destroy; }
	       
	        public void setWillDestroy(boolean Destroy) { destroy = Destroy; }
	       
	    }
	   
	    public boolean getColorRename() { return colorrename; }
	   
	    public void setColorRename(boolean ColorRename) { colorrename = ColorRename; }
	   
	    public Player getPlayer() { return player; }
	   
	    public String getTitle() { return title; }
	   
	    public void setTitle(String Title) { title = Title; }
	   
	    public String getDefaultText() { return defaulttext; }
	   
	    public void setDefaultText(String DefaultText) { defaulttext = DefaultText; }
	   
	    public ItemStack getSlot(AnvilSlot Slot) { return items.get(Slot); }
	   
	    public void setSlot(AnvilSlot Slot, ItemStack Item) { items.put(Slot, Item); }
	   
	    public String getSlotName(AnvilSlot Slot) {
	       
	        ItemStack IS = getSlot(Slot);
	       
	        if(IS != null && IS.hasItemMeta()) {
	           
	            ItemMeta M = IS.getItemMeta();
	           
	            return M.hasDisplayName() ? M.getDisplayName() : "";
	           
	        } else return "";
	       
	    }
	   
	    public void setSlotName(AnvilSlot Slot, String Name) {
	       
	        ItemStack IS = getSlot(Slot);
	       
	        if(IS != null) {
	           
	            ItemMeta M = IS.getItemMeta();
	           
	            M.setDisplayName(Name != null ? ChatColor.translateAlternateColorCodes(colorchar, Name) : null);
	           
	            IS.setItemMeta(M);
	           
	            setSlot(Slot, IS);
	           
	        }
	       
	    }
	   
	    public AnvilGUI(Player Player, final AnvilClickEventHandler Handler) {
	       
	        loadClasses();
	       
	        player = Player;
	        handler = Handler;
	        listener = new Listener() {
	           
	            @EventHandler
	            public void ICE(InventoryClickEvent e) {
	               
	                if(e.getInventory().equals(inventory)) {
	                   
	                    e.setCancelled(true);
	                   
	                    if(e.getClick() != ClickType.LEFT && e.getClick() != ClickType.RIGHT) return;
	                   
	                    ItemStack IS = e.getCurrentItem();
	                   
	                    int S = e.getRawSlot();
	                   
	                    String T = null;
	                   
	                    if(IS != null && IS.hasItemMeta()) {
	                       
	                        ItemMeta M = IS.getItemMeta();
	                       
	                        if(M.hasDisplayName()) T = M.getDisplayName();
	                       
	                    }
	                   
	                    AnvilClickEvent ACE = new AnvilClickEvent(AnvilSlot.bySlot(S), IS, T);
	                   
	                    handler.onAnvilClick(ACE);
	                   
	                    if(ACE.getWillClose() || ACE.getWillDestroy()) e.getWhoClicked().closeInventory();
	                   
	                    if(ACE.getWillDestroy()) HandlerList.unregisterAll(listener);
	                   
	                }
	               
	            }
	           
	            @EventHandler
	            public void PAE(PrepareAnvilEvent e) {
	               
	                if(e.getInventory().equals(inventory)) {
	               
	                    ItemStack IS = e.getResult();
	                   
	                    if(colorrename && IS != null && IS.hasItemMeta()) {
	                       
	                        ItemMeta M = IS.getItemMeta();
	                       
	                        if(M.hasDisplayName()) M.setDisplayName(ChatColor.translateAlternateColorCodes(colorchar, M.getDisplayName()));
	                       
	                        IS.setItemMeta(M);
	                       
	                        e.setResult(IS);
	                       
	                    }
	                   
	                }
	               
	            }
	           
	            @EventHandler
	            public void ICE(InventoryCloseEvent e) {
	               
	                if(e.getInventory().equals(inventory)) {
	                   
	                    player.setLevel(player.getLevel() - 1);
	                    inventory.clear();
	                    HandlerList.unregisterAll(listener);
	                   
	                }
	               
	            }
	           
	            @EventHandler
	            public void PQE(PlayerQuitEvent e) {
	               
	                if(e.getPlayer().equals(player)) {
	                   
	                    player.setLevel(player.getLevel() - 1);
	                    HandlerList.unregisterAll(listener);
	                   
	                }
	               
	            }
	           
	        };
	       
	        Bukkit.getPluginManager().registerEvents(listener, plugin);
	       
	    }
	   
	    public void open() { open(title); }
	   
	    public void open(String Title) {
	       
	        player.setLevel(player.getLevel() + 1);
	       
	        try {
	           
	            Object P = NMSManager.getHandle(player);
	           
	            Constructor<?> CM = ChatMessage.getConstructor(String.class, Object[].class);
	           
	            if(useNewVersion) {
	               
	                Method CAM = NMSManager.getMethod("at", ContainerAccess, NMSManager.getNMSClass("World"), BlockPosition);
	               
	                Object CA = ContainerAnvil.getConstructor(int.class, NMSManager.getNMSClass("PlayerInventory"), ContainerAccess).newInstance(9, NMSManager.getPlayerField(player, "inventory"), CAM.invoke(ContainerAccess, NMSManager.getPlayerField(player, "world"), BlockPosition.getConstructor(int.class, int.class, int.class).newInstance(0, 0, 0)));
	                NMSManager.getField(NMSManager.getNMSClass("Container"), "checkReachable").set(CA, false);
	               
	                inventory = (Inventory) NMSManager.invokeMethod("getTopInventory", NMSManager.invokeMethod("getBukkitView", CA));
	               
	                for(AnvilSlot AS : items.keySet()) inventory.setItem(AS.getSlot(), items.get(AS));
	               
	                int ID = (Integer) NMSManager.invokeMethod("nextContainerCounter", P);
	               
	                Object PC = NMSManager.getPlayerField(player, "playerConnection");
	                Object PPOOW = PacketPlayOutOpenWindow.getConstructor(int.class, Containers, NMSManager.getNMSClass("IChatBaseComponent")).newInstance(ID, NMSManager.getField(Containers, "ANVIL").get(Containers), CM.newInstance(ChatColor.translateAlternateColorCodes(colorchar, Title), new Object[]{}));
	               
	                Method SP = NMSManager.getMethod("sendPacket", PC.getClass(), PacketPlayOutOpenWindow);
	                SP.invoke(PC, PPOOW);
	               
	                Field AC = NMSManager.getField(EntityHuman, "activeContainer");
	               
	                if(AC != null) {
	                   
	                    AC.set(P, CA);
	                   
	                    NMSManager.getField(NMSManager.getNMSClass("Container"), "windowId").set(AC.get(P), ID);
	                   
	                    NMSManager.getMethod("addSlotListener", AC.get(P).getClass(), P.getClass()).invoke(AC.get(P), P);
	                   
	                }
	               
	            } else {
	               
	                Object CA = ContainerAnvil.getConstructor(NMSManager.getNMSClass("PlayerInventory"), NMSManager.getNMSClass("World"), BlockPosition, EntityHuman).newInstance(NMSManager.getPlayerField(player, "inventory"), NMSManager.getPlayerField(player, "world"), BlockPosition.getConstructor(int.class, int.class, int.class).newInstance(0, 0, 0), P);
	                NMSManager.getField(NMSManager.getNMSClass("Container"), "checkReachable").set(CA, false);
	               
	                inventory = (Inventory) NMSManager.invokeMethod("getTopInventory", NMSManager.invokeMethod("getBukkitView", CA));
	               
	                for(AnvilSlot AS : items.keySet()) inventory.setItem(AS.getSlot(), items.get(AS));
	               
	                int ID = (Integer) NMSManager.invokeMethod("nextContainerCounter", P);
	               
	                Object PC = NMSManager.getPlayerField(player, "playerConnection");
	                Object PPOOW = PacketPlayOutOpenWindow.getConstructor(int.class, String.class, NMSManager.getNMSClass("IChatBaseComponent"), int.class).newInstance(ID, "minecraft:anvil", CM.newInstance(ChatColor.translateAlternateColorCodes(colorchar, Title), new Object[]{}), 0);
	               
	                Method SP = NMSManager.getMethod("sendPacket", PC.getClass(), PacketPlayOutOpenWindow);
	                SP.invoke(PC, PPOOW);
	               
	                Field AC = NMSManager.getField(EntityHuman, "activeContainer");
	               
	                if(AC != null) {
	                   
	                    AC.set(P, CA);
	                   
	                    NMSManager.getField(NMSManager.getNMSClass("Container"), "windowId").set(AC.get(P), ID);
	                   
	                    NMSManager.getMethod("addSlotListener", AC.get(P).getClass(), P.getClass()).invoke(AC.get(P), P);
	                   
	                }
	               
	            }
	           
	        } catch (Exception e) { e.printStackTrace(); }
	       
	    }
	   
	}
}
