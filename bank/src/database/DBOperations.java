package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DBOperations {

	private static DBConnection sConnection;

	static {
		sConnection = new DBConnection();
		sConnection.openConnection();
	}

	public static void closeConnection() {
		sConnection.closeConnection();
	}

	public static JSONObject getUserByCnp(String cnp) {
		JSONObject info = new JSONObject();

		try {
			Connection connection = sConnection.getConnection();
			Statement statement = connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			String query = "SELECT * FROM " + Credentials.TABEL_CLIENTI
					+ " WHERE `cnp`='" + cnp + "'";
			ResultSet result = statement.executeQuery(query);

			if (result.next()) {
				info.put("nume", result.getString("nume"));
				info.put("prenume", result.getString("prenume"));
				info.put("cnp", result.getString("cnp"));

				int userId = result.getInt("id");
				int domeniuId = result.getInt("domeniu");

				info.put("id", userId);
				info.put("domeniuId", domeniuId);

				query = "SELECT `nume` FROM " + Credentials.TABEL_DOMENII
						+ " WHERE `id`=" + domeniuId;
				ResultSet resultDom = statement.executeQuery(query);
				if (resultDom.next()) {
					info.put("domeniuNume", resultDom.getString("nume"));
				}

				query = "SELECT * FROM " + Credentials.TABEL_SOLDURI
						+ " WHERE `client`=" + userId;
				ResultSet resultSold = statement.executeQuery(query);
				if (resultSold.next()) {
					info.put("iban", resultSold.getString("iban"));
					info.put("sold", resultSold.getFloat("sold"));
					info.put("blocat", resultSold.getInt("blocat"));
					int moneda = resultSold.getInt("moneda");

					query = "SELECT `nume` FROM " + Credentials.TABEL_MONEDE
							+ " WHERE `id`=" + moneda;
					ResultSet resultMoneda = statement.executeQuery(query);
					if (resultMoneda.next()) {
						info.put("moneda", resultMoneda.getString("nume"));
					}
				}

				info.put("credit", getLoan(userId));
			}
		} catch (Exception e) {

		}

		return info;
	}

	public static int getUserDomain(int userId) {
		try {
			Connection connection = sConnection.getConnection();
			Statement statement = connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			String query = "SELECT `domeniu` FROM " + Credentials.TABEL_CLIENTI
					+ " WHERE `id`=" + userId;
			ResultSet result = statement.executeQuery(query);

			if (result.next()) {
				return result.getInt("domeniu");
			}
		} catch (Exception e) {

		}

		return -1;
	}
	
	public static int getExpId(int ani) {
		try {
			Connection connection = sConnection.getConnection();
			Statement statement = connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			String query = "SELECT * FROM " + Credentials.TABEL_EXPERIENTA;
			ResultSet result = statement.executeQuery(query);

			while (result.next()) {
				int min = result.getInt("vechimeAniMin");
				int max = result.getInt("vechimeAniMax");
				if(max == 0) { //max is null
					max = 999;
				}
				
				if(ani >= min && ani < max) {
					return result.getInt("id");
				}
			}
		} catch (Exception e) {

		}

		return -1;
	}
	
	public static String getLoan(int userId) {
		try {
			Connection connection = sConnection.getConnection();
			Statement statement = connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
			String query = "SELECT `scadentar` FROM " + Credentials.TABEL_RATE
					+ " WHERE `client`=" + userId;
			ResultSet result = statement.executeQuery(query);

			if (result.next()) {
				return result.getString("scadentar");
			}
		} catch (Exception e) {

		}

		return "";
	}

	/**
	 * domeniu trebuie ca int (foreign key)
	 * 
	 * @param info
	 */
	public static void addUser(JSONObject info) {
		try {
			// Add user
			Connection connection = sConnection.getConnection();
			Statement statement = connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);

			String query = "INSERT INTO "
					+ Credentials.TABEL_CLIENTI
					+ " (`nume`, `prenume`, `cnp`, `domeniu`) VALUES (";
			query += "'" + info.getString("nume") + "', ";
			query += "'" + info.getString("prenume") + "', ";
			query += "'" + info.getString("cnp") + "', ";
			query += info.getInt("domeniu") + ")";

			statement.executeUpdate(query);

			// get user id
			query = "SELECT `id` FROM " + Credentials.TABEL_CLIENTI
					+ " WHERE `cnp`='" + info.getString("cnp") + "'";
			ResultSet result = statement.executeQuery(query);
			if (result.next()) {
				int userId = result.getInt("id");

				// create an account for the user
				query = "INSERT INTO "
						+ Credentials.TABEL_SOLDURI
						+ " (`client`, `iban`, `moneda`, `sold`, `blocat`) VALUES (";
				query += userId + ", ";

				// RO98RNCB0077092789180001
				Random rand = new Random();
				query += "'RO"
						+ (10 + rand.nextInt(90))
						+ "RNCB"
						+ (1000000000000000l + rand.nextInt(2147483647) * 1000000)
						+ "', ";
				query += "1, 0, 0)"; // moneda, sold, blocat

				statement.executeUpdate(query);
			}

		} catch (Exception e) {

		}
	}

	public static void updateUserInfo(JSONObject info) {
		try {
			Connection connection = sConnection.getConnection();
			Statement statement = connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);

			String query = "UPDATE " + Credentials.TABEL_CLIENTI + " SET ";
			query += "`nume`='" + info.getString("nume") + "', ";
			query += "`prenume`='" + info.getString("prenume") + "', ";
			query += "`cnp`='" + info.getString("cnp") + "', ";
			query += "`domeniu`=" + info.getString("domeniu");
			query += " WHERE `id`=" + info.getInt("id");

			statement.executeUpdate(query);
		} catch (Exception e) {

		}
	}

	public static void changeAccountLockStatus(int userId, int lock) {
		try {
			Connection connection = sConnection.getConnection();
			Statement statement = connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);

			String query = "UPDATE " + Credentials.TABEL_SOLDURI
					+ " SET `blocat`=" + lock + " WHERE `client`=" + userId;

			statement.executeUpdate(query);
		} catch (Exception e) {

		}
	}

	public static void updateAccount(int userId, float amount,
			boolean isWithdrawal) {
		try {
			Connection connection = sConnection.getConnection();
			Statement statement = connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);

			String query = "SELECT `sold` FROM " + Credentials.TABEL_SOLDURI
					+ " WHERE `client`=" + userId;

			ResultSet result = statement.executeQuery(query);
			if (!result.next()) {
				return;
			}

			float sum = result.getFloat("sold");
			if (isWithdrawal) {
				sum -= amount;
			} else {
				sum += amount;
			}

			query = "UPDATE " + Credentials.TABEL_SOLDURI + " SET `sold`="
					+ sum + " WHERE `client`=" + userId;

			statement.executeUpdate(query);

		} catch (Exception e) {

		}
	}

	public static void updateLoan(int userId, String loan) {
		try {
			Connection connection = sConnection.getConnection();
			Statement statement = connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);

			String query = "UPDATE " + Credentials.TABEL_RATE
					+ " SET `scadentar`='" + loan + "'";

			statement.executeUpdate(query);
		} catch (Exception e) {

		}
	}
	
	public static String getUserAccountLockStatus(int userId) {
		JSONObject acc = new JSONObject();
		
		try {
			Connection connection = sConnection.getConnection();
			Statement statement = connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);

			String query = "SELECT `blocat` FROM " + Credentials.TABEL_SOLDURI
					+ " WHERE `client`=" + userId;

			ResultSet result = statement.executeQuery(query);
			
			if(result.next()) {
				acc.put("blocat", result.getInt("blocat"));
			}
		} catch (Exception e) {

		}
		
		return acc.toString();
	}

	public static int getDomainCoeffById(int domeniuId) {
		try {
			Connection connection = sConnection.getConnection();
			Statement statement = connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);

			String query = "SELECT `coeficient` FROM "
					+ Credentials.TABEL_DOMENII + " WHERE `id`=" + domeniuId;
			ResultSet resultCoeff = statement.executeQuery(query);

			if (resultCoeff.next()) {
				return resultCoeff.getInt("coeficient");
			}
		} catch (Exception e) {

		}

		return 0;
	}

	public static int getExperienceCoeffById(int experientaId) {
		try {
			Connection connection = sConnection.getConnection();
			Statement statement = connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);

			String query = "SELECT `coeficient` FROM "
					+ Credentials.TABEL_EXPERIENTA + " WHERE `id`="
					+ experientaId;
			ResultSet resultCoeff = statement.executeQuery(query);

			if (resultCoeff.next()) {
				return resultCoeff.getInt("coeficient");
			}
		} catch (Exception e) {

		}

		return 0;
	}

	public static String getStatus(int rating) {
		try {
			Connection connection = sConnection.getConnection();
			Statement statement = connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);

			String query = "SELECT `status_code` FROM "
					+ Credentials.TABEL_RATING + " WHERE `intervalMin`<="
					+ rating + " AND `intervalMax`>=" + rating;

			ResultSet result = statement.executeQuery(query);

			if (result.next()) {
				return result.getString("status_code");
			}
		} catch (Exception e) {

		}

		return "";
	}

	public static JSONArray getDomanins() {
		JSONArray domains = new JSONArray();
		
		try {
			Connection connection = sConnection.getConnection();
			Statement statement = connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);

			String query = "SELECT * FROM " + Credentials.TABEL_DOMENII;

			ResultSet result = statement.executeQuery(query);

			while (result.next()) {
				JSONObject domain = new JSONObject();
				domain.put("id", result.getInt("id"));
				domain.put("nume", result.getString("nume"));
				domains.add(domain);
			}
		} catch (Exception e) {

		}

		return domains;
	}

	public static JSONArray getExperience() {
		JSONArray experiences = new JSONArray();
		
		try {
			Connection connection = sConnection.getConnection();
			Statement statement = connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);

			String query = "SELECT * FROM " + Credentials.TABEL_EXPERIENTA;

			ResultSet result = statement.executeQuery(query);

			while (result.next()) {
				JSONObject experience = new JSONObject();
				experience.put("id", result.getInt("id"));
				experience.put("vechimeAniMin", result.getString("vechimeAniMin"));
				experience.put("vechimeAniMax", result.getString("vechimeAniMax"));
				experiences.add(experience);
			}
		} catch (Exception e) {

		}

		return experiences;
	}
}
