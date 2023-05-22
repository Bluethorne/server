package games.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.scheduler.BukkitRunnable;

public class DataBase {
	
	public Connection connection;
	
	static String host = "clientsql.nitrous-networks.co.uk";
	static String username = "Peter184";
	static String database = "Peter184EkQbvInIe7GHAZ8mkzTf";
	static String password = "Z1xnFXQ56jqc";
	static int port = 3306;
	
	public static Connection connection() {
		DataBase base = new DataBase();
		base.openConnection();
		return base.connection;
	}
	
	void openConnection() {
		try {
            openConnection1();
            closeConnection();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
	}
	
	void openConnection1() throws SQLException, ClassNotFoundException {
	    if (connection != null && !connection.isClosed()) {
	        return;
	    }
	 
	    synchronized (Main.getPlugin(Main.class)) {
	        if (connection != null && !connection.isClosed()) {
	            return;
	        }
	        Class.forName("com.mysql.jdbc.Driver");
	        connection = DriverManager.getConnection("jdbc:mysql://" + host+ ":" + port + "/" + database, username, password);
	    }
	}
	
	void closeConnection() {
		new BukkitRunnable() {

			@Override
			public void run() {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
		}.runTaskLater(Main.getPlugin(Main.class), 300);
	}
}
