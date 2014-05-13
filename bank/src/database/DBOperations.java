package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import net.sf.json.JSONObject;

public class DBOperations {

	private static DBConnection sConnection;

	static {
		sConnection = new DBConnection();
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
				info.put("rauPlatnic", result.getInt("rauPlatnic"));

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
			Connection connection = sConnection.getConnection();
			Statement statement = connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);

			String query = "INSERT INTO "
					+ Credentials.TABEL_CLIENTI
					+ " (`nume`, `prenume`, `cnp`, `domeniu`, `experienta`, `rauPlatnic`) VALUES (";
			query += "'" + info.getString("nume") + "', ";
			query += "'" + info.getString("prenume") + "', ";
			query += "'" + info.getString("cnp") + "', ";
			query += info.getInt("domeniu") + ", ";
			query += info.getInt("rauPlatnic");

			statement.executeUpdate(query);
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

		return;
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

	public static int getDomainCoeffByUserId(int userId) {
		try {
			Connection connection = sConnection.getConnection();
			Statement statement = connection
					.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);

			String query = "SELECT `domeniu` FROM " + Credentials.TABEL_CLIENTI
					+ " WHERE `id`=" + userId;
			ResultSet result = statement.executeQuery(query);
			if (result.next()) {
				int domeniuId = result.getInt("domeniu");

				query = "SELECT `coeficient` FROM " + Credentials.TABEL_DOMENII
						+ " WHERE `id`=" + domeniuId;
				ResultSet resultCoeff = statement.executeQuery(query);

				if (resultCoeff.next()) {
					return resultCoeff.getInt("coeficient");
				}
			}
		} catch (Exception e) {

		}

		return 0;
	}

	public static int getDomainCoeffByDomainId(int domeniuId) {
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

	public static int getExperienceCoeff(int experientaId) {
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
}
