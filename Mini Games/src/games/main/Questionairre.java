package games.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftMetaBook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.IChatBaseComponent.ChatSerializer;

public class Questionairre implements Listener {
	
	static File folder = new File("plugins/miniGames/Hub/Questionairre");
	ArrayList<Pair<Integer, String>> answers = new ArrayList<Pair<Integer, String>>();
	static File[] questionairres = folder.listFiles();
	HashMap<String, Object> json;
	Player player;
	UUID uuid;
	ArrayList<HashMap<String, Object>> questions;
	int qNum = -1;
	String name;
	
	@SuppressWarnings("unchecked")
	public static void check(Player player) {
		long lastPlayed = player.getLastPlayed();
		for(File file: questionairres) {
			if(file.isFile()) {
				long fileTime = Integer.MAX_VALUE;
				try {
					fileTime = Files.readAttributes(file.toPath(), BasicFileAttributes.class).creationTime().toMillis();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(lastPlayed < fileTime) {
					new Questionairre((HashMap<String, Object>) JSON.getJSON(file), player);
				}
			}
		}
	}
	
	void ask() {
		name = ((String) json.get("name"));
		uuid = UUID.randomUUID();
		askMCQuestion("{\"text\":\"" + "§2" + name.replace(" ", " §2") + new String(new char[98]).replace('\0', ' ') + "\",\"extra\":[{\"text\":\"§a[Accept]\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/respond " + uuid.toString() + " Accept\"}},{\"text\":\"       \"},{\"text\":\"§4[Decline]\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/respond " + uuid + " Decline\"}}]}", name);
	}
	
	@EventHandler
	public void q(QuestionairreEvent event) {
		if(uuid.equals(event.uuid)) {
			if(player.equals(event.player)) {
				String answer = event.answer;
				if(qNum == -1) {
					if(answer.equals("Accept")) {
						getNextQ();
					}
				} else {
					next(answer);
				}
			}
		}
	}
	
	void waiter(int currentNum, int time) {
		new BukkitRunnable() {

			@Override
			public void run() {
				if(qNum == currentNum) {
					if(time == 0) {
						finish();
					} else {
						waiter(currentNum, time - 1);
					}
				}
			}
			
		}.runTaskLater(Main.getPlugin(Main.class), 1);
	}
	
	void finish() {
		QuestionairreEvent.getHandlerList().unregister(this);
		recordAnswers();
		player.sendMessage(ChatColor.GREEN + "Thank you for completing the questionaire");
	}
	
	void next(String answer) {
		answers.add(new Pair<Integer, String>((Integer) questions.get(qNum).get("number"), answer));
		if(questions.size() > qNum + 1) {
			getNextQ();
		} else {
			finish();
		}
	}
	
	@SuppressWarnings("unchecked")
	void recordAnswers() {
		File file = new File(folder.getPath() + "/Answers/" + name + ".json");
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		HashMap<String, ArrayList<HashMap<String, Object>>> results = (HashMap<String, ArrayList<HashMap<String, Object>>>) JSON.getJSON(file);
		ArrayList<HashMap<String, Object>> qs;
		if(results == null) {
			results = new HashMap<String, ArrayList<HashMap<String, Object>>>();
			qs = new ArrayList<HashMap<String, Object>>();
			results.put("Questions", qs);
		} else {
			qs = results.get("Questions");
			if(qs == null) {
				qs = new ArrayList<HashMap<String, Object>>();
				results.put("Questions", qs);
			}
		}
		for(Pair<Integer, String> pair: this.answers) {
			HashMap<String, Object> q = JSON.number(qs, pair.getKey());
			if(q == null) {
				q = new HashMap<String, Object>();
				q.put("number", pair.getKey());
				q.put("answers", new HashMap<String, Integer>());
				qs.add(q);
			}
			HashMap<String, Integer> as = (HashMap<String, Integer>) q.get("answers");
			String answer = pair.getValue().replace(" ", "_");
			Integer num = as.get(answer);
			if(num == null) {
				as.put(answer, 1);
			} else {
				as.put(answer, num + 1);
			}
		}
		try {
			FileWriter filew = new FileWriter(file);
            filew.write(JSON.toJSON(results));
            filew.close();
        } catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void getNextQ() {
		qNum++;
		HashMap<String, Object> question = (HashMap<String, Object>) questions.get(qNum);
		Integer number = (Integer) question.get("number");
		if(number.equals(qNum + 1)) {
			Integer show = (Integer) question.get("show");
			uuid = UUID.randomUUID();
			if(show == 1) {
				askQ(question);
			} else if(show == 2) {
				@SuppressWarnings("unchecked")
				HashMap<String, Object> ifs = (HashMap<String, Object>) question.get("if");
				Integer ifQNum = (Integer) ifs.get("number");
				String answer = (String) ifs.get("answer");
				if(!Pair.getValue(ifQNum, answers).isEmpty()) {
					if(Pair.getValue(ifQNum, answers).get(0).equals(answer)) {
						askQ(question);
					} else {
						getNextQ();
					}
				} else {
					getNextQ();
				}
			}
		}
	}
	
	void askQ(HashMap<String, Object> question) {
		waiter(new Integer(qNum), 6000);
		String questionString = (String) question.get("Question");
		Integer type = (Integer) question.get("type");
		if(type == 1) {
			@SuppressWarnings("unchecked")
			HashMap<String, String> answers = (HashMap<String, String>) question.get("answers");
			String string = "";
			for(int count = 1; count <= answers.size(); count++) {
				if(count != 1) {
					string = string + ",";
				}
				String answer = (String) answers.get(String.valueOf(count));
				string = string + "{\"text\":\"" + answer + new String(new char[spaces(113 - (size(answer) % 113)) + 1]).replace('\0', ' ') + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/respond " + uuid.toString() + " " + answer.replace(" ", "_") + "\"}}";
			}
			askMCQuestion("{\"text\":\"§2" + questionString.replace(" ", " §2") + new String(new char[spaces(113 - (size(questionString) % 113)) + 116]).replace('\0', ' ') + "\",\"extra\":[" + string + "]}", name);
		} else if(type == 2) {
			askTextQ(questionString);
		}
	}
	
	int spaces(int pixels) {
		boolean ok = false;
		int size = 0;
		int spaces = 0;
		while(!ok) {
			if(size + 3 < pixels) {
				size = size + 3;
				spaces = spaces + 1;
			} else {
				ok = true;
			}
			if(size + 1 < pixels) {
				size = size + 1;
			} else {
				ok = true;
			}
		}
		return spaces;
	}
	
	int size(String sentence) {
		int totalSize = 0;
		int lineSize = 0;
		for(String word: sentence.split(" ")) {
			int wordSize = sizeWord(word);
			if(lineSize + wordSize <= 113) {
				lineSize += wordSize;
			} else {
				totalSize += 113;
				lineSize = wordSize;
			}
			if(lineSize + 1 >= 113) {
				totalSize += 113;
				lineSize = 0;
			} else if(lineSize + 1 < 113) {
				lineSize += 1;
			}
			if(lineSize + 3 >= 113) {
				totalSize += 113;
				lineSize = 0;
			} else if(lineSize + 3 < 113) {
				lineSize += 3;
			}
			if(lineSize + 1 >= 113) {
				totalSize += 113;
				lineSize = 0;
			} else if(lineSize + 1 < 113) {
				lineSize += 1;
			}
		}
		totalSize = totalSize + lineSize - 4;
		return totalSize;
	}
	
	int sizeWord(String word) {
		int size = 0;
		for(char c: word.toCharArray()) {
			size += size(c) + 1;
		}
		return size - 1;
	}
	
	int size(char letter) {
		if(letter == '!' || letter == '\'' || letter == ',' || letter == '.' || letter == ':' || letter == ';' || letter == 'i' || letter == '|') {
			return 1;
		} else if(letter == '`' || letter == 'l') {
			return 2;
		} else if(letter == ' ' || letter == '"' || letter == '(' || letter == ')' || letter == '*' || letter == 'I' || letter == ']' || letter == '[' || letter == 't' || letter == '{' || letter == '}') {
			return 3;
		} else if(letter == '<' || letter == '>' || letter == 'f' || letter == 'k') {
			return 4;
		} else if(letter == '@' || letter == '~') {
			return 6;
		} else {
			return 5;
		}
	}
	
	void askTextQ(String questionString) {
		GUI.AnvilGUI gui = new GUI.AnvilGUI(player, new GUI.AnvilGUI.AnvilClickEventHandler() {
				@Override
				public void onAnvilClick(GUI.AnvilGUI.AnvilClickEvent e) {
					if(e.getSlot() == GUI.AnvilGUI.AnvilSlot.OUTPUT && e.hasText()) {
						e.setWillClose(true);
						String answer = e.getText();
						new BukkitRunnable() {

							@Override
							public void run() {
								QuestionairreEvent event = new QuestionairreEvent(uuid, answer, player);
								Main.getPlugin(Main.class).getServer().getPluginManager().callEvent(event);
							}
		
						}.runTaskLater(Main.getPlugin(Main.class), 1);
					}
				}
			});
			ItemStack i = new ItemStack(Material.PAPER);
			ItemMeta meta = i.getItemMeta();
			meta.setDisplayName(ChatColor.AQUA + questionString);
			i.setItemMeta(meta);
			gui.setSlot(GUI.AnvilGUI.AnvilSlot.INPUT_LEFT, i);
			gui.setTitle(ChatColor.BLACK + questionString);
			gui.open();
	}
	
	void askMCQuestion(String json, String name) {
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta meta = (BookMeta) book.getItemMeta();
		try {
			@SuppressWarnings("unchecked")
			List<IChatBaseComponent> pages = (List<IChatBaseComponent>) CraftMetaBook.class.getDeclaredField("pages").get(meta);
			pages.add(ChatSerializer.a(json));
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		meta.setTitle(name);
		meta.setAuthor("Blank");
		book.setItemMeta(meta);
		player.openBook(book);
	}
	
	@SuppressWarnings("unchecked")
	public Questionairre(HashMap<String, Object> questions, Player player) {
		Main.getPlugin(Main.class).getServer().getPluginManager().registerEvents(this, Main.getPlugin(Main.class));
		this.json = questions;
		this.player = player;
		ask();
		this.questions = (ArrayList<HashMap<String, Object>>) json.get("Questions");
	}
}
