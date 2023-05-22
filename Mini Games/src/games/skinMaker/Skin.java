package games.skinMaker;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.bukkit.Color;
import org.bukkit.scheduler.BukkitRunnable;

import games.main.JSON;
import games.main.Main;

public class Skin {
	
	boolean registering = false;
	boolean slim;
	String base64;
	String signature;
	public final File original;
	public final File file = new File("plugins/miniGames/SkinMaker/Skins/temp/" + UUID.randomUUID().toString() + ".png");
	public NPC npc;
	BufferedImage temp = null;
	
	public Skin() {
		original = null;
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Skin(String base64, String signature, boolean slim, File file) {
		this.original = file;
		this.base64 = base64;
		this.signature = signature;
		this.slim = slim;
		try {
			Files.copy(original.toPath(), this.file.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void combine(Skin[] skins) throws IOException {
		BufferedImage newImage = new BufferedImage(64, 64, 6);
		for(Skin skin: skins) {
			if(skin != null) {
				BufferedImage image = ImageIO.read(skin.file);
				for(int x = 0; x < image.getWidth(); x++) {
					for(int y = 0; y < image.getHeight(); y++) {
						int rgb = image.getRGB(x, y);
						if(!String.format("%08x", rgb).substring(0, 2).equals("00")) {
							int newX = x;
							int newY = y;
							if(y < 16) {
								if(x < 32) {
									newX += 32;
								}
							} else if(y < 32) {
								newY += 16;
							} else if(y >= 48) {
								if(x < 32) {
									if(x >= 16) {
										newX -= 16;
									}
								} else {
									if(x < 48) {
										newX += 16;
									}
								}
							}
							java.awt.Color color = new java.awt.Color(0, 0, 0, 0);
							newImage.setRGB(newX, newY, color.getRGB());
						}
					}
				}
				for(int x = 0; x < image.getWidth(); x++) {
					for(int y = 0; y < image.getHeight(); y++) {
						int rgb = image.getRGB(x, y);
						if(!String.format("%08x", rgb).substring(0, 2).equals("00")) {
							java.awt.Color color = new java.awt.Color(rgb);
							java.awt.Color newColor = new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue(), 255);
							newImage.setRGB(x, y, newColor.getRGB());
						}
					}
				}
			}
		}
		ImageIO.write(newImage, "png", file);
	}
	
	public void changeColor(Color newMainColor) throws IOException {
		BufferedImage image = ImageIO.read(original);
		BufferedImage image2 = ImageIO.read(original);
		Color oldMainColor = getColor(image);
		int rdiff = newMainColor.getRed() - oldMainColor.getRed();
		int gdiff = newMainColor.getGreen() - oldMainColor.getGreen();
		int bdiff = newMainColor.getBlue() - oldMainColor.getBlue();
		for(int x = 0; x < image.getWidth(); x++) {
			for(int y = 0; y < image.getHeight(); y++) {
				String argb = String.format("%08x", image.getRGB(x, y));
				if(argb.substring(0, 2).equals("80")) {
					Color color = Color.fromRGB(Integer.parseInt(argb.substring(2), 16));
					int r = color.getRed();
					int g = color.getGreen();
					int b = color.getBlue();
					java.awt.Color newColor = new java.awt.Color(newColorInt(r, rdiff), newColorInt(g, gdiff), newColorInt(b, bdiff), 255);
					int rgb = newColor.getRGB();
					image.setRGB(x, y, rgb);
					image2.setRGB(x, y, rgb);
				} else if(argb.equals("00ffffff")) {
					image.setRGB(x, y, new java.awt.Color(255, 255, 255, 255).getRGB());
				}
			}
		}
		ImageIO.write(image, "png", file);
		temp = image2;
	}
	
	private int newColorInt(int color, int diff) {
		int num = color + diff;
		if(num < 0) {
			num = 0;
		} else if(num > 255) {
			num = 255;
		}
		return num;
	}
	
	private Color getColor(BufferedImage image) {
		int height = image.getHeight();
		int width = image.getWidth();
		int[] rgbs = image.getRGB(0, 0, width, height, null, 0, width);
		HashMap<String, Integer> num = new HashMap<String, Integer>();
		for(int rgb: rgbs) {
			String argb = String.format("%08x", rgb);
			if(argb.substring(0, 2).equals("80")) {
				num.put(argb.substring(2), num.getOrDefault(argb.substring(2), 0) + 1);
			}
		}
		String rgb = "";
		int max = 0;
		for(Entry<String, Integer> entry: num.entrySet()) {
			if(entry.getValue() > max) {
				max = entry.getValue();
				rgb = entry.getKey();
			}
		}
		if(rgb.equals("")) {
			return null;
		}
		return Color.fromRGB(Integer.parseInt(rgb, 16));
	}
	
	@SuppressWarnings("unchecked")
	public void register(boolean changeSkin) {
		new BukkitRunnable() {

			@Override
			public void run() {
				if(!registering) {
					registering = true;
					base64 = null;
					signature = null;
					try {
						URL url = new URL("https://api.mineskin.org/generate/upload?name=&model=" + getSlim() + "&visibility=1");
						HttpURLConnection http = (HttpURLConnection) url.openConnection();;
						http.setRequestMethod("POST");
						http.setDoOutput(true);
						http.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
						String boundary = UUID.randomUUID().toString();
						http.setRequestProperty("Content-Type","multipart/form-data; boundary=" + boundary);
						OutputStream output = http.getOutputStream();
						PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true);
						writer.append("--" + boundary).append("\r\n");
						writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"").append("\r\n");
						writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(file.getName())).append("\r\n");
						writer.append("Content-Transfer-Encoding: binary").append("\r\n");
						writer.append("\r\n").flush();
						Files.copy(file.toPath(), output);
						output.flush();
						writer.append("\r\n").flush();
						writer.append("--" + boundary + "--").append("\r\n").flush();
						int responseCode = http.getResponseCode();
						if (responseCode == HttpURLConnection.HTTP_OK) {
							InputStream stream = http.getInputStream();
							Scanner scanner = new Scanner(stream);
							String string = "";
							while(scanner.hasNext()) {
								string += scanner.next();
							}
							scanner.close();
							HashMap<String, Object> json = (HashMap<String, Object>) JSON.getJSON(string);
							HashMap<String, String> texture = ((HashMap<String, String>) ((HashMap<String, Object>) json.get("data")).get("texture"));
							base64 = texture.get("value");
							signature = texture.get("signature");
							if(changeSkin) {
								if(!npc.despawned) {
									npc.changeSkin();
								}
							}
							if(temp != null) {
								ImageIO.write(temp, "png", file);
								temp = null;
							}
							registering = false;
						} else {
							new BukkitRunnable() {
								
								@Override
								public void run() {
									register(changeSkin);
								}
							
							}.runTaskLater(Main.getPlugin(Main.class), 200);
							if(responseCode != 429) {
								System.out.println("POST Response Code " + responseCode);
								InputStream stream = http.getErrorStream();
								Scanner scanner = new Scanner(stream);
								String string = "";
								while(scanner.hasNext()) {
									string += scanner.next();
								}
								scanner.close();
								System.out.println("Message: " + string);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
		}.runTaskAsynchronously(Main.getPlugin(Main.class));
		
	}
	
	private String getSlim() {
		if(slim) {
			return "slim";
		} else {
			return "steve";
		}
	}
}
