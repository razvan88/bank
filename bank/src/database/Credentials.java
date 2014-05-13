package database;

public abstract class Credentials {
	public static final String IP = "localhost";
	public static final String USER = "root";
	public static final String PASSWORD = "";
	
	public static final String DRIVER = "com.mysql.jdbc.Driver";
	
	public static String getDefaultLink() {
		return "jdbc:mysql://" + IP + "/" + 
			"?user=" + USER + "&password=" + PASSWORD;
	}
	
	public static String getLink(String databaseName) {
		return "jdbc:mysql://" + IP + "/" + databaseName + 
			"?user=" + USER + "&password=" + PASSWORD;
	}
	
	public static final String DEFAULT_DATABASE = "bank";
	public static final String TABEL_CLIENTI = "clienti";
	public static final String TABEL_DOMENII = "domenii";
	public static final String TABEL_EXPERIENTA = "experienta";
	public static final String TABEL_MONEDE = "monede";
	public static final String TABEL_RATE = "rate";
	public static final String TABEL_RATING = "rating";
	public static final String TABEL_SOLDURI = "solduri";
}
