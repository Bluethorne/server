package games.main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.entity.Player;

public class Rank {
	
	public static String getRank(Player player) {
		String rank = "";
		try {
			Statement statement = DataBase.connection().createStatement();
			ResultSet result = statement.executeQuery("SELECT Rank FROM Hub WHERE UUID ='" + player.getUniqueId() + "'");
			if(result.next()) {
				rank = result.getString("Rank");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return rank;
	}
	
}
