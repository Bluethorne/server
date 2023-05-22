package games.main;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import games.flappyChicken.FlappyChicken;
import games.game.Game;
import games.game.LeaderBoard;
import games.mind.Mind;
import games.tribes.Tribes;
import games.skinMaker.SkinMaker;
import net.minecraft.server.v1_16_R3.DataWatcher;
import net.minecraft.server.v1_16_R3.DataWatcherRegistry;
import net.minecraft.server.v1_16_R3.EntityArmorStand;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import net.minecraft.server.v1_16_R3.PacketPlayInUseEntity;
import net.minecraft.server.v1_16_R3.PacketPlayInUseEntity.EnumEntityUseAction;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_16_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_16_R3.PlayerInteractManager;
import net.minecraft.server.v1_16_R3.WorldServer;
import net.minecraft.server.v1_16_R3.IChatBaseComponent.ChatSerializer;

public class HubNPC implements Listener {
	
	static ArrayList<Pair<String, Integer>> n = new ArrayList<Pair<String, Integer>>();
	static World world = Main.getPlugin(Main.class).getServer().getWorld("world");
	static ArrayList<Pair<Location, Pair<String, Pair<String, String>>>> npcs = new ArrayList<Pair<Location, Pair<String, Pair<String, String>>>>(Arrays.asList(new Pair<Location, Pair<String, Pair<String, String>>>(new Location(world, 36.5, 122.0, -86.5, (float) ((Math.atan(6.5/6.5)) * 180 / Math.PI), (float) 0.0), new Pair<String, Pair<String, String>>("§eBuild Battle", new Pair<String, String>("eyJ0aW1lc3RhbXAiOjE1NzU3MTk2MjUxNTEsInByb2ZpbGVJZCI6ImRlNTcxYTEwMmNiODQ4ODA4ZmU3YzlmNDQ5NmVjZGFkIiwicHJvZmlsZU5hbWUiOiJNSEZfTWluZXNraW4iLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzZiMTNhYTZmZjMyY2ZlYzlhOTIyZDQzYjM2YjUyYzBlNDlmNDE4YTdkYzQ2OTNhYzJkNzNiZTY4YzVjYThmMGIifX19", "Z9ZDeDgOumV7tOyD7uhwjDqMmhO9np9taeKlWkcFbllCTts9P8ymMdVMR/sXNOo/goBvRNzOY74CSuLUoAO2c7IT3cIAeSQ5rFXTptxdt6whm2yV1E1TWCq9zng+pr/yZm6PyQkBvKIlYV+spouQSJx1FtCNVQkbpwVNXXxyycwAoGScUJeU5D8tz5+Xn1nXZNXz04lT5Ok/WPra/DUaG/XJPWKFSt3+lJilTENHkiuaSImTdsCsfVWh/k9MSAHiRmzeeZ3s3U+op5PHjnzMJTVDSF1qVmaLqEihAfBBPjmCxEmRN20fb4f+N5Pt2/RDq5aaQT3GCH4hT5RT5ZCMsy8qDPaXUEM87UboxwulsgB1x9FZ72h5lkhNylrv9KlCiRJ2eXMZnD8YfPI6G20mS6nyjDymtON9Wc1Vk74og1XKWs2v14K6A1ZPyaxgXzLIXAxgT3BQeFNoE5TwQ07ji8MA+/L0GHj5A4EB99kfQA0ML1/vIkW6dT1vea2yi/hemSiJG0f8y6B0nV4f8pQ7OsYcuLQzwbufYXsNpkZb1AAUxqNFnPEspuT8meJlDuJYvn0IBCSBIcCaK6mbsr3uhMIIIk00e35v1m/ApnVv/I1KGoTMqX0YPEzeoqzeWE+bwPQNGWP1MV7p0JDIg9IfThxcHARLgSd620i9QIMMJaU="))),
																																						new Pair<Location, Pair<String, Pair<String, String>>>(new Location(world, 24.5, 122.0, -74.5, (float) (270 - ((Math.atan(5.5/5.5)) * 180 / Math.PI)), (float) 0.0), new Pair<String, Pair<String, String>>("§eBuild Battle", new Pair<String, String>("eyJ0aW1lc3RhbXAiOjE1NzU3MTk2MjUxNTEsInByb2ZpbGVJZCI6ImRlNTcxYTEwMmNiODQ4ODA4ZmU3YzlmNDQ5NmVjZGFkIiwicHJvZmlsZU5hbWUiOiJNSEZfTWluZXNraW4iLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzZiMTNhYTZmZjMyY2ZlYzlhOTIyZDQzYjM2YjUyYzBlNDlmNDE4YTdkYzQ2OTNhYzJkNzNiZTY4YzVjYThmMGIifX19", "Z9ZDeDgOumV7tOyD7uhwjDqMmhO9np9taeKlWkcFbllCTts9P8ymMdVMR/sXNOo/goBvRNzOY74CSuLUoAO2c7IT3cIAeSQ5rFXTptxdt6whm2yV1E1TWCq9zng+pr/yZm6PyQkBvKIlYV+spouQSJx1FtCNVQkbpwVNXXxyycwAoGScUJeU5D8tz5+Xn1nXZNXz04lT5Ok/WPra/DUaG/XJPWKFSt3+lJilTENHkiuaSImTdsCsfVWh/k9MSAHiRmzeeZ3s3U+op5PHjnzMJTVDSF1qVmaLqEihAfBBPjmCxEmRN20fb4f+N5Pt2/RDq5aaQT3GCH4hT5RT5ZCMsy8qDPaXUEM87UboxwulsgB1x9FZ72h5lkhNylrv9KlCiRJ2eXMZnD8YfPI6G20mS6nyjDymtON9Wc1Vk74og1XKWs2v14K6A1ZPyaxgXzLIXAxgT3BQeFNoE5TwQ07ji8MA+/L0GHj5A4EB99kfQA0ML1/vIkW6dT1vea2yi/hemSiJG0f8y6B0nV4f8pQ7OsYcuLQzwbufYXsNpkZb1AAUxqNFnPEspuT8meJlDuJYvn0IBCSBIcCaK6mbsr3uhMIIIk00e35v1m/ApnVv/I1KGoTMqX0YPEzeoqzeWE+bwPQNGWP1MV7p0JDIg9IfThxcHARLgSd620i9QIMMJaU="))),
																																						new Pair<Location, Pair<String, Pair<String, String>>>(new Location(world, 26.5, 122.0, -72.5, (float) (270 - ((Math.atan(7.5/3.5)) * 180 / Math.PI)), (float) 0.0), new Pair<String, Pair<String, String>>("§eSurvival Games", new Pair<String, String>("eyJ0aW1lc3RhbXAiOjE1ODY3NzY1ODA2MDcsInByb2ZpbGVJZCI6ImQ1NjVkYWE1NTVlODQxNWVhMjI0YTkxOWU5ZmFhNDdhIiwicHJvZmlsZU5hbWUiOiJTdW5ueU50YyIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjg3ZTVjMzIxNzE2YzFhOTY0MGMyMjBlMTIyMTRhYmQ1N2NlZDlhYTU2MzgxZDQwZDI1OGUxMjA4YWMxZjRlIiwibWV0YWRhdGEiOnsibW9kZWwiOiJzbGltIn19fX0=","tQfrMU0ZZPEUEx8lSJFk/2KczbYUhLQJIDLYzoZNKpknCSSFH8VUK9ULqnfgEh0qPjcV1P9O3PN81OpyjKlHab2NEe1Cf9pM4bWlWez/udDmcOwDUgDLpUq6cdyRkvQQYaOt4r2Awa0SimNK3XqhrqbF7MmY1Tjah9d6XYvzCE3/dPPrXOuh1nrg/je/zQTVqq80zyfG/JXRYuAJS8lj4HkMYXC55Vb0hlhIZd5hs6eLt1HOPHvJAfiTni3jwQCw6fbF4M4yfb14smRcPs8ZMSzS1aDqgZ2qUwModKXko6/IGYhLOD3hn4k/C1SOkafjlge1pKru60VDZ2O3PjHHOUk2I3CKmIjlQbscfsi+bAg0hiYvAU6ciBtOz7wORYB/NdE9KTf0VHLZ3EFhnTYtEfMVr6rO6QCmaNGBZiOM0Sy6CY3vR0qUqVnl4Gf7ExpwnBqgmo+hTqEJ4gsJcAP8K579O2ELSoqNpe13LazjAxvSq3FoHSneDqXXKQZkM+eJiWedoj+FGuqo7znjRG32azEja/oof6KWbjP8rgzCJx8qswDcxzQ70OjvnWT+0l/K4mfnCT9qAw1tzpJYTqzXZSk1LK+J8Voj16b0m3U2FfX3LvFEKviquq3t/61FMmYKf/yikZWxhFLN/aZSZxxuigyVBgYojOSNFWYimYUbxt8="))),
																																						new Pair<Location, Pair<String, Pair<String, String>>>(new Location(world, 38.5, 122.0, -84.5, (float) ((Math.atan(8.5/4.5)) * 180 / Math.PI), (float) 0.0), new Pair<String, Pair<String, String>>("§eSurvival Games", new Pair<String, String>("eyJ0aW1lc3RhbXAiOjE1ODY3NzY1ODA2MDcsInByb2ZpbGVJZCI6ImQ1NjVkYWE1NTVlODQxNWVhMjI0YTkxOWU5ZmFhNDdhIiwicHJvZmlsZU5hbWUiOiJTdW5ueU50YyIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjg3ZTVjMzIxNzE2YzFhOTY0MGMyMjBlMTIyMTRhYmQ1N2NlZDlhYTU2MzgxZDQwZDI1OGUxMjA4YWMxZjRlIiwibWV0YWRhdGEiOnsibW9kZWwiOiJzbGltIn19fX0=","tQfrMU0ZZPEUEx8lSJFk/2KczbYUhLQJIDLYzoZNKpknCSSFH8VUK9ULqnfgEh0qPjcV1P9O3PN81OpyjKlHab2NEe1Cf9pM4bWlWez/udDmcOwDUgDLpUq6cdyRkvQQYaOt4r2Awa0SimNK3XqhrqbF7MmY1Tjah9d6XYvzCE3/dPPrXOuh1nrg/je/zQTVqq80zyfG/JXRYuAJS8lj4HkMYXC55Vb0hlhIZd5hs6eLt1HOPHvJAfiTni3jwQCw6fbF4M4yfb14smRcPs8ZMSzS1aDqgZ2qUwModKXko6/IGYhLOD3hn4k/C1SOkafjlge1pKru60VDZ2O3PjHHOUk2I3CKmIjlQbscfsi+bAg0hiYvAU6ciBtOz7wORYB/NdE9KTf0VHLZ3EFhnTYtEfMVr6rO6QCmaNGBZiOM0Sy6CY3vR0qUqVnl4Gf7ExpwnBqgmo+hTqEJ4gsJcAP8K579O2ELSoqNpe13LazjAxvSq3FoHSneDqXXKQZkM+eJiWedoj+FGuqo7znjRG32azEja/oof6KWbjP8rgzCJx8qswDcxzQ70OjvnWT+0l/K4mfnCT9qAw1tzpJYTqzXZSk1LK+J8Voj16b0m3U2FfX3LvFEKviquq3t/61FMmYKf/yikZWxhFLN/aZSZxxuigyVBgYojOSNFWYimYUbxt8="))),
																																						new Pair<Location, Pair<String, Pair<String, String>>>(new Location(world, 21.5, 122.0, -79.5, (float) 270, (float) 0), new Pair<String, Pair<String, String>>("§4Tribes", new Pair<String, String>("ewogICJ0aW1lc3RhbXAiIDogMTU4OTYyODAwNDU0MywKICAicHJvZmlsZUlkIiA6ICI4NjNiMjNhOTFmNTE0NmJhYjY5ZDQ4NGRjZGM4NTc2YyIsCiAgInByb2ZpbGVOYW1lIiA6ICJCbHVldGhvcm5lIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2I1NTJkYTMzNWRkMjUyYWI2YTYyNTc0ZjFiYjQ3ZDA4OGVkZTE0ODFiNGYzNjk5NTMzMTdhYzgyY2IzMjdjNDIiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==", "AEqmKPT8sR7TAcOuBLn9RBwr3qnaCa/tEC1ZNXdZKW9R5SRtrwBDBM6ofmHMoDMOfRYEdR8fRnXFpg8FfwkiSQ3SXQPH4dPk5PUZikSgOxa+tz5YbfdP9F6+kvuqfxZYcO4SEDShtIz2laT2kucRNYUBw78b8Pl90d+meprsNHkFjG/+/FnzFAXowpAFlP4PMsryaDTyXn+1qvR7MYK/p5yRyPcheNZnkjHIxmVixXD5ebRluYbtEHTKfbDyWQ7iGlUdnYoNQXHDboXjfjabhYGqH5BZ0mHt+iyAljE+MuezovTXkw+qBzK7A+5fpktf6v3v33nd+d/dx8ZVNLtGuiXd7AgRMby3SVIhb5Cj5QbyEHvogaqbPzU68+vhsQFTkFTrJFKh0fCVXceqxnZ1PL6eYwhXAaPxUvduQ2oQhe1BArKo/SUx9SjP7zZESERxk1YnGlOR328t8L1C48WfRVpJhHXXy/slUSUPalVPCFvYtysTdr4EH1ezuS/nBF4VyYZSDES2hSY+DkZT7yPxQfjOugjo0ZQdL/F97070g9WDqOD6W/5A88IslsJDupVJqiDpZe89uoOKxSTXzqjXkHYbdrgz/OnOlJym3UK2qRZeBwSIti6UnTn8F2rdJHp3svPUcAfU9fqsAOrSVmZlPTkGIsoapOLfyVVpeuYdYhg="))),
																																						new Pair<Location, Pair<String, Pair<String, String>>>(new Location(world, 31.5, 122.0, -67.5, (float) 180, (float) 0), new Pair<String, Pair<String, String>>("§4Skin Maker", new Pair<String, String>("ewogICJ0aW1lc3RhbXAiIDogMTU5NDEzMjE5NzUzMiwKICAicHJvZmlsZUlkIiA6ICJhMzYyZTQ3NDQyZTQ0ZmI3YjdjNzQwZjBlNDBlMWEyZCIsCiAgInByb2ZpbGVOYW1lIiA6ICI4NjciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzg1ZDY5MjRhNzI2MjkxNzI3ZWYzNTM1NGRmZTQwMjY1YzVmMzk4MGZhNzU5OTIzOTE5Y2ViMzFiMjU4MDg0OCIKICAgIH0KICB9Cn0=", "nKi383Fwjdpl5GC2KI7DhGVv0CtQ/et6QQHpiBx+6zPQHoPR/11xx9K8YRayq8HkoUK9l3wbWpHnU8lWOPOGynt6lMsnv81jx6kydAO+bnQyglkAj4UuMp2iUJOxfhrq7hBwvyxhoc9Sfo71LC9+voDBvmmnoU9omRpsGXoOaB+R75nFOMcoHYo6CAWMZvic6YibBEHNT14OEn/SCe7WfwjjZc2Z9zapWbON9YiDvSRQkRFpK1cgEiANpQobB+ffdNz40zRrsxjqLKTPEEmaZDG0eMeRrXwWYATchyYjf732z6IW0PxSt/GinwRVYRuysnBWjcNd6ly5DSZYpr2G37JxCgwOTeGj+yqPHE34xEa1MwwUgWaUTazwJ/CTumkzz3wPBQJzj9hIAP2aN976rSXrfb/FevUQGUEUx9fXocb4hPW7OZW2jxA/dDMp5+A3CNgMM44e/nOdp19zZ9chxkJKfNRTCDER6qkzKQy2mxHuu5wcmv5T8CuTtV2R2jGTxHkQeybO2wI5fA54E30E7P0qW2FS2pEeuMA1B2uST/A9Unlq9JufHX3D7CLwGU1WOAV3Smha2UO24vyJfRFD48/z8CJCKRy2rVzthLD56luYAeWrWKvGMqQh2dQZ//olukXMsg5L8AMjduPu9eHHnZoi2R3cMhW9y+JiYjDph5A="))),
																																						new Pair<Location, Pair<String, Pair<String, String>>>(new Location(world, 29.5, 121.0, -86.5, (float) 0, (float) 0), new Pair<String, Pair<String, String>>("§eLeaderboard", new Pair<String, String>("ewogICJ0aW1lc3RhbXAiIDogMTU5OTM5NDgxNDUxOSwKICAicHJvZmlsZUlkIiA6ICJkNDJhMzFlZGY1YmU0ZDAxOGY3N2FkMzQzNGQxNjFkZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJQcm9NYXN0ZXJfIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2M0MWY4YmVjZTAwMzcwM2MzODE3MTFhYzliN2NhNDA0YjljYWViZjUxNGMxNDJhMjhjMjNiNDNjYjJmNDVhMWIiCiAgICB9CiAgfQp9", "JPDKaTh/tRbZO90T37xmuTR++9Q4Vu/OAkc6HGfHG9eF1ekCXbf+nl8RFcmxc9Mb5utzbFJe+laep29b5LLUkNUJDzbH5fiA+83vWsfzj8xVAQBh6PDdg5ToN9tDOblIo3cKKg3/gSrClRZ5m6hSAkd7trZTx++R+EDFNHLKGiz18l8pZM7m3222ycxdQJoNIHoGvexdmdckWH8H6XWLzOcY/shfcmkIt0ht71mPDt1hT1NSeDdhNSk9nOliNjqS0TViQSeJ2KmsMs8IpvNq/JH+eII0L+XsXmkV9Fz1k1VOKt9YqAkGk/I/04sHTM7XBrdzuoZHs/1ApO+6xnIr63PTd5hdUoppikOUMjuV8Fj17/YkjlY42qznkqjS2uGP4tzX18ZrbGcAhlH2dccVcIb6HRvxs+5+Uu//yPWNNQijSq5pBsyJcGCZ4caUjy1mXn73YsFvmldoQjAViYON3RCefQUhFOPEGi+cSrsCGw7/Ix1uxMqI21l0DRzsdK0GPYR9upJTklLvBjpAdue2y8TuxY72H61vQjUJSK5Tp8mbHoGtT4J/GjGkaQWdXzagVS+IIRIXKFCSedWoKKGZOdqaOVV+axK4iccS9FYURw1R4KtiRng3h/F+x45gOKszXJbqYS+Gp8sD1EwC+0vlkVq6ZT6Ug89S6rYz3wS1GGQ="))),
																																						new Pair<Location, Pair<String, Pair<String, String>>>(new Location(world, 31.5, 121.0, -86.5, (float) 0, (float) 0), new Pair<String, Pair<String, String>>("§4Flappy Chicken", new Pair<String, String>("ewogICJ0aW1lc3RhbXAiIDogMTU5OTM5NDc4MzU4OSwKICAicHJvZmlsZUlkIiA6ICJiNjc2YTlmODAwZGU0ZWMzYjgxNGVjYzU4NzZkN2RkYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJyb2JvdGNoaWNrZW5zIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzk1MmI3MDA4NjA5MjZjMGRmNmU2YjA0ZjdhMmExNmVmNDRhYTE4MmMzNDJjOTkxNjA2NGQ0N2I3MDkzZmZkNDMiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==", "bXh0902nox0RYcfroUNvBjALP5Q/gs5dnojgcVNj3cPH2lcHK3rp/vca/ydx/U5QdwIqFzPx+ed1K3mzV6ynksVZO+WhmeS+3YbNxX+lApmDosziEg63Jks7Otedh45Z9yz3hMWsjqIlAJUlK59HXJ7YK5A2jeqDZTTO/pUOuGB0pztSWjhO4BrOxUNg5KSaRJcwsydhIIaNOdw8peS4wyiKYTbR/48bIloOoFbVTDm9HWWQg0dbZZFD17xD9jnQaVPQwH2WJcbGKDy/DIIJeAWQJhWJAvYC/gbJiAgf3idQ5mOye25BdH1G9mhTYuq/AITDQRurKbhYPiUe0hL/+SlQlZvMuIeRjia3ETUVoBJupwHBud30xTaEv6Msm/sfj/n3mxIWq2bc/nkA2WdaZ3aef6dEe3i+Imfpocwo78msOxQLf/mahUMCpn37zfZYlVJjC9I7nk0XlGCLnEfbeAYiSfSNpe1af/MiMAln6K4ChQ/ZOp5BEM7hDR/ttty+/zolK4vD3vB7HTE1OOcMEG6pUMGrByNpHgNJIoTto8LNyDcFSXmTgm0cAxw1bi1OQteMlv4ha9zhuOeSs4KHmx+zWPXPUbQBq4n62nog1zbPGiMv+Nq3IE0hZNcjg9nko7xbAMUavt8qgkKDskkFeaKml+CBeEZqqI+EwLoLoms="))),
																																						new Pair<Location, Pair<String, Pair<String, String>>>(new Location(world, 32.5, 121.0, -86.5, (float) 0, (float) 0), new Pair<String, Pair<String, String>>("§eColors", new Pair<String, String>("ewogICJ0aW1lc3RhbXAiIDogMTYwODc2MzU4OTk2MSwKICAicHJvZmlsZUlkIiA6ICIzNTk2ZDQ0MGNkZjU0ZDllOWI5MjM1NTIxMjIwZmQ4MCIsCiAgInByb2ZpbGVOYW1lIiA6ICJxcHRyIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ZmYzUwYTMzNmEyMDkxZGJiMmNhMzJlZDM2ZTJlZGVjNWMyMzhiNjgxNDRjZjZmMTNkNDQzZmEyYmJiY2IyNmUiCiAgICB9CiAgfQp9", "qi8A6PvjadsF8VFtPuQwGOesf7Pj/NacSmz5rFjA8dbBmQV52jXuIMOAVCCltWfAki6MAbHGLQzBO7fJqMNVRE12/YXOnbe+IhcKjPO4rTHIq2KpGEMZcuG1L20NJ8hTwkftm6pPNyS4g04bDyXCynzlT70yPbfKizLpgfkzlbdgOdIeqOSk3ShCiOIW5PcrsGEwZjkETDP/WXuzd4y2b3v0hkXcP+Ap6wiAQf43saIfN1XqGJs2IrgrEDH2FouiAlZxXqRv/zfxAyfMcJl813hYgFgmun/2WySjjAmsM5GbJ8+1gCRMCMSTnREPzw0r+HG/X/rBgZ7GsPaUxr6zyfDfQ+LxK+hdc93YjcZ3jy9sSfFME8o8WRHGSd2VT8B8xqyh5fYgiZuetLMU1FRlzSPtzKj4nytzyMHhCOnAE+3jmmHBS0+hd8wAPoIKbzwP5M5ldDvjRZA5cZL/6dctvuCHADg7CoBCz9+kFbHFjiz8QBdkKL1kPhx1KVzdL4K1sOdjFiDxO5vezArH4MjD7lR8xVzaUvV2ACeKXgR8051lwo1O9SUtGi4Np3Vr9E35eqMM9DxyJNYvpb1dos5BuesnOxcNBAI/DUxUsy7imOblE0gL/vamSeAKvax5R93QLeM3amaC3gqKczdt65B6cxlQNg9bNfXe2hS78pRu0vI=")))));
	
	@EventHandler
	public void join(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		spawnNPCs(player);
	}
	
	public static void spawnNPCs(Player player) {
		MinecraftServer server = ((CraftServer) Main.getPlugin(Main.class).getServer()).getServer();
		WorldServer world = ((CraftWorld) Main.getPlugin(Main.class).getServer().getWorld("world")).getHandle();
		for(Pair<Location, Pair<String, Pair<String, String>>> pair: npcs) {
			Location location = pair.getKey();
			String name = pair.getValue().getKey();
			String skin = pair.getValue().getValue().getKey();
			String signature = pair.getValue().getValue().getValue();
			GameProfile profile = new GameProfile(UUID.randomUUID(), name);
			profile.getProperties().put("textures", new Property("textures", skin, signature));
			EntityPlayer npc = new EntityPlayer(server, world, profile, new PlayerInteractManager(world));
			npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
			PacketPlayOutPlayerInfo packet1 = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, npc);
			PacketPlayOutNamedEntitySpawn packet2 = new PacketPlayOutNamedEntitySpawn(npc);
			DataWatcher watcher = npc.getDataWatcher();
			watcher.set(DataWatcherRegistry.a.a((byte) 16), (byte) 127);
	        PacketPlayOutEntityMetadata packet3 = new PacketPlayOutEntityMetadata(npc.getId(), watcher, true);
	        PacketPlayOutEntityHeadRotation packet4 = new PacketPlayOutEntityHeadRotation(npc, (byte) (location.getYaw() * 256 / 360));
	        PacketPlayOutPlayerInfo packet5 = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, npc);
	        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet1);
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet2);
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet3);
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet4);
			if(name.equals("§4Tribes")) {
				EntityArmorStand stand = new EntityArmorStand(EntityTypes.ARMOR_STAND, world);
				stand.setLocation(location.getX(), location.getY() - 0.4, location.getZ(), 0, 0);
				stand.setCustomName(ChatSerializer.a("{\"text\":\"§c§lCOMING SOON!\"}"));
				stand.setCustomNameVisible(true);
				stand.setNoGravity(true);
				stand.setInvulnerable(true);
				stand.setInvisible(true);
				PacketPlayOutSpawnEntity packetSpawn = new PacketPlayOutSpawnEntity(stand);
				((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetSpawn);
				PacketPlayOutEntityMetadata packetMetadata = new PacketPlayOutEntityMetadata(stand.getId(), stand.getDataWatcher(), true);
				((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetMetadata);
			}
			new BukkitRunnable() {

				@Override
				public void run() {
					((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet5);
				}
			}.runTaskLater(Main.getPlugin(Main.class), 50);
			n.add(new Pair<String, Integer>(name, npc.getId()));
		}
	}
	
	public static void testForClickNPC(Object object, Player player) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
		if(object instanceof PacketPlayInUseEntity) {
			PacketPlayInUseEntity packet = (PacketPlayInUseEntity) object;
			Field entityID = PacketPlayInUseEntity.class.getDeclaredField("a");
            entityID.setAccessible(true);
            int id = entityID.getInt(packet);
			if(Pair.getValues(n).contains(id)) {
				if(packet.b().equals(EnumEntityUseAction.ATTACK)) {
					String name = Pair.getPairFromValue(id, n).get(0).getKey().substring(2);
					String game = name.replace(" ", "");
					if(!Game.playersPlaying.contains(player)) {
						player.sendMessage(ChatColor.GREEN + "You have chosen to play " + name);
						Game.playersPlaying.add(player);
						if(game.equals("SkinMaker")) {
							SkinMaker.join(player);
						} else if(game.equals("Tribes")) {
							Tribes.join(player);
						} else if(game.equals("Leaderboard")) {
							LeaderBoard.tpLeaderBoard(player);
						} else if(game.equals("FlappyChicken")) {
							new BukkitRunnable() {

								@Override
								public void run() {
									new FlappyChicken(player);
								}
							}.runTask(Main.getPlugin(Main.class));
						} else if(game.equals("§eColors")) {
							Mind.join(player);
						} else {
							Class.forName("games." + game.substring(0, 1).toLowerCase() + game.substring(1) + "." + game).getMethod("addToQue", new Class[] {Player.class}).invoke(null, player);
						}
					} else {
						player.sendMessage(ChatColor.RED + "You are already playing a game or are in a queue");
					}
				}
			}
		}
	}
	
}
