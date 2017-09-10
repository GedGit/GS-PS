package org.rs2server.rs2.mysql.impl;

import java.sql.*;

import org.rs2server.Server;
import org.rs2server.rs2.Constants;
import org.rs2server.rs2.model.player.Player;

/**
 * Handles updating of websites news.
 * 
 * @author Vichy
 */
public class NewsManager implements Runnable {

	/**
	 * Database creditentials
	 */
	public static final String HOST = Constants.DEBUG ? "144.217.242.115" : "localhost"; // website ip address
	public static final String USER = "salve-ps_admin";
	public static final String PASS = "bCs6m_17";
	public static final String DATABASE = "admin_news";

	/**
	 * The message to send.
	 */
	private String newsMessage;

	/**
	 * Defining the players news table to update
	 */
	private Player player;

	/**
	 * Defining the SQL connection
	 */
	private Connection conn;

	/**
	 * Defining the SQL statement
	 */
	private Statement stmt;

	/**
	 * Constructing this class
	 * 
	 * @param player
	 *            the players news.
	 */
	public NewsManager(Player player, String message) {
		this.player = player;
		this.newsMessage = message;
	}

	/**
	 * Handles the actual database connection.
	 * 
	 * @param host
	 *            the website host
	 * @param database
	 *            the database name
	 * @param user
	 *            the database username
	 * @param pass
	 *            the database password
	 * @return if connection successful
	 */
	public boolean connect(String host, String database, String user, String pass) {
		try {
			this.conn = DriverManager.getConnection("jdbc:mysql://" + host + ":3306/" + database, user, pass);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void run() {
		try {

			if (player.isAdministrator())
				return;

			if (!connect(HOST, DATABASE, USER, PASS))
				return;

			String name = player.getName();

			PreparedStatement statement = prepare(generateQuery());
			statement.setString(1, name);
			statement.setString(2, newsMessage); // the news message

			statement.execute();

			/**
			 * Reformat the message to fix discord environment
			 */
			int index = newsMessage.indexOf("achieved");
			if (index == -1)
				index = newsMessage.indexOf("received");
			if (index == -1)
				index = newsMessage.indexOf("died");
			if (index == -1)
				index = newsMessage.indexOf("opened");
			if (index == -1)
				index = newsMessage.indexOf("killed");

			newsMessage = newsMessage.substring(index);

			Server.sendDiscordMessage("[SERVER] " + name + ": " + newsMessage);

			destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prepares an SQL query statement
	 * 
	 * @param query
	 *            the query to run
	 * @return the prepared statement
	 * @throws SQLException
	 *             sql error
	 */
	public PreparedStatement prepare(String query) throws SQLException {
		return conn.prepareStatement(query);
	}

	/**
	 * Destroys the SQL connection
	 */
	public void destroy() {
		try {
			conn.close();
			conn = null;
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generates the query to run
	 * 
	 * @return the query as String
	 */
	public static String generateQuery() {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO message (");
		sb.append("username, ");
		sb.append("news)");
		sb.append("VALUES (?, ?)");
		return sb.toString();
	}
}