package games.main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class Scoreboards implements Listener{	
	
	static String[] letters = new String[] {"T", "h", "o", "r", "n", "e", "c", "r", "a", "f", "t"};
	
	public static void addScoreboardJoin(Player player) {
		ScoreboardManager manager = Main.getPlugin(Main.class).getServer().getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		Objective objective = board.registerNewObjective("hubSidebar", "dummy", "§f§lT§a§lhornecraft");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		Score score1 = objective.getScore(ChatColor.GREEN + "89.34.96.16:25592");
		score1.setScore(1);
		Score score2 = objective.getScore(ChatColor.GREEN + "" + ChatColor.BOLD + "Player");
		score2.setScore(7);
		Score score3 = objective.getScore(ChatColor.DARK_GREEN + player.getName());
		score3.setScore(6);
		Score score4 = objective.getScore(ChatColor.GREEN + "" + ChatColor.BOLD + "Rank");
		score4.setScore(4);
		Score score5 = objective.getScore("");
		score5.setScore(2);
		Score score6 = objective.getScore(" ");
		score6.setScore(5);
		Score score7 = objective.getScore("  ");
		score7.setScore(8);
		String rank = "§8None";
		try {
			Statement statement = DataBase.connection().createStatement();
			ResultSet result = statement.executeQuery("SELECT Rank FROM Hub WHERE UUID ='" + player.getUniqueId() + "'");
			if(result.next()) {
				rank = result.getString("Rank");
				if(rank.equals("")) {
					rank = "§8None";
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Score score8 = objective.getScore(rank);
		score8.setScore(3);
		player.setScoreboard(board);
		ranks(player);
	}
	
	public static void addScoreboardSurvivalGames(Player player, int kills, String supplyDrop, boolean shrinking) {
		ScoreboardManager manager = Main.getPlugin(Main.class).getServer().getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		String name = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getDisplayName();
		Objective objective = board.registerNewObjective("hubSidebar", "dummy", name);
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		Score score11 = objective.getScore("   ");
		score11.setScore(11);
		Score score10 = objective.getScore(ChatColor.GREEN + "" + ChatColor.BOLD + "Kills");
		score10.setScore(10);
		Score score9 = objective.getScore(ChatColor.DARK_GREEN + new Integer(kills).toString());
		score9.setScore(9);
		Score score8 = objective.getScore("  ");
		score8.setScore(8);
		Score score7 = objective.getScore(ChatColor.GREEN + "" + ChatColor.BOLD + "Supply Drop");
		score7.setScore(7);
		Score score6 = objective.getScore(ChatColor.DARK_GREEN + supplyDrop);
		score6.setScore(6);
		Score score5 = objective.getScore(" ");
		score5.setScore(5);
		Score score4 = objective.getScore(ChatColor.GREEN + "" + ChatColor.BOLD + "Border");
		score4.setScore(4);
		String s = "";
		if(!shrinking) {
			s = "Not ";
		}
		s += "Shrinking";
		Score score3 = objective.getScore(ChatColor.DARK_GREEN + s);
		score3.setScore(3);
		Score score2 = objective.getScore("");
		score2.setScore(2);
		Score score1 = objective.getScore(ChatColor.GREEN + "89.34.96.16:25592");
		score1.setScore(1);
		
		player.setScoreboard(board);
		ranks(player);
	}
	
	public static void addScoreboardBuildBattle(Player player, String theme) {
		ScoreboardManager manager = Main.getPlugin(Main.class).getServer().getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		String name = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getDisplayName();
		Objective objective = board.registerNewObjective("hubSidebar", "dummy", name);
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		Score score1 = objective.getScore(ChatColor.GREEN + "89.34.96.16:25592");
		score1.setScore(1);
		Score score2 = objective.getScore(ChatColor.GREEN + "" + ChatColor.BOLD + "Theme");
		score2.setScore(4);
		Score score = objective.getScore(ChatColor.DARK_GREEN + theme);
		score.setScore(3);
		Score score5 = objective.getScore("");
		score5.setScore(2);
		player.setScoreboard(board);
		ranks(player);
	}
	
	static void ranks(Player player) {
		Scoreboard scoreboard = player.getScoreboard();
		try {
			Statement statement = DataBase.connection().createStatement();
			ResultSet result = statement.executeQuery("SELECT UUID, Rank FROM Hub");
			while(result.next()) {
				String joined = result.getString("Rank");
				if(scoreboard.getTeam(joined) == null) {
					scoreboard.registerNewTeam(joined);
				}
				Team team = scoreboard.getTeam(joined);
				team.setPrefix(joined + " ");
				OfflinePlayer oplayer =	Main.getPlugin(Main.class).getServer().getOfflinePlayer(UUID.fromString(result.getString("UUID")));
				if(oplayer.hasPlayedBefore()) {
			    	team.addEntry(oplayer.getName());
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void join(PlayerJoinEvent event) {
		addScoreboardJoin(event.getPlayer());
		changeScoreboard(1, event.getPlayer());
	}
	
	static void changeScoreboard(int oldPos, Player player) {
		Scoreboard scoreboard = player.getScoreboard();
		int newPos = (oldPos % 11) + 1;
		Objective objective = scoreboard.getObjective(DisplaySlot.SIDEBAR);
		String oldDisplay = objective.getDisplayName();
		char letter;
		if(oldPos != 11 && oldDisplay.length() < 20) {
			oldPos = 1;
		}
		if(oldPos == 11) {
			letter = oldDisplay.toCharArray()[1];
		} else if(oldPos == 1) {
			letter = oldDisplay.toCharArray()[6];
		} else {
			letter = oldDisplay.toCharArray()[oldPos + 9];
		}
		String newDisplay = getString(newPos, change(letter));
		new BukkitRunnable() {

			@Override
			public void run() {
				if(Main.getPlugin(Main.class).getServer().getOnlinePlayers().contains(player)) {
					objective.setDisplayName(newDisplay);
					changeScoreboard(newPos, player);
				}
			}
			
		}.runTaskLater(Main.getPlugin(Main.class), 20/3);
	}
	
	static String getString(int pos, char before) {
		String[] newLetters = Arrays.copyOf(letters, letters.length);
		char after = change(before);
		if(pos == 1) {
			newLetters[1] = "§" + after + "§l" + newLetters[1];
		} else if(pos == letters.length) {
			newLetters[0] = "§" + before + "§l" + newLetters[0];
		} else {
			newLetters[pos] = "§" + after + "§l" + newLetters[pos];
			newLetters[0] = "§" + before + "§l" + newLetters[0];
		}
		newLetters[pos - 1] = "§f§l" + newLetters[pos - 1];
		return String.join("", newLetters);
	}
	
	static char change(char letter) {
		if(letter == 'a') {
			return '2';
		} else {
			return 'a';
		}
	}
 
}


