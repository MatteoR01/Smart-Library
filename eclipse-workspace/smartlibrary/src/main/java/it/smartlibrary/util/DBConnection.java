package it.smartlibrary.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static DBConnection instance;
    Connection connection;

    private DBConnection() throws SQLException {
        try {
            String testMode = System.getProperty("smartlibrary.testdb");
            if ("true".equalsIgnoreCase(testMode)) {
                connection = DriverManager.getConnection("jdbc:h2:mem:smartlibrary;DB_CLOSE_DELAY=-1", "sa", "");
            } 
            else {
            	// Database embedded locale 
            	String url = "jdbc:h2:./data/smartlibrary;AUTO_RECONNECT=TRUE"; 
            	connection = DriverManager.getConnection(url, "sa", "");
            }
        } 
        catch (SQLException e) {
            throw e;
        }
    }

    public static synchronized DBConnection getInstance() throws SQLException {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public static synchronized void shutdown() {
        if (instance != null) {
            try {
            	if (instance.connection != null && !instance.connection.isClosed()) { 
            		instance.connection.createStatement().execute("SHUTDOWN"); 
            		instance.connection.close(); 
            		System.out.println("H2 shutdown eseguito correttamente."); 
            	}
            }
            catch (Exception e) {
                e.printStackTrace();
            } 
            finally {
                instance = null;
            }
        }
    }
}
