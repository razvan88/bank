package database;


import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
	private Connection mConnection;
	
	public DBConnection() {
	}
	
	/**
	 * This method is the first one that should be called,
	 * for opening the connection
	 */
	public void openConnection() {
        try {
			Class.forName(Credentials.DRIVER).newInstance();
			String link = Credentials.getLink(Credentials.DEFAULT_DATABASE);
            mConnection = DriverManager.getConnection(link);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	/**
	 * Closes the connection with the database.
	 * This is the last function that should be called.
	 */
    public void closeConnection() {
        try {
            mConnection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * @return the sql Connection object
     * wrapped in this class
     */
    public Connection getConnection() {
    	return mConnection;
    }
}
