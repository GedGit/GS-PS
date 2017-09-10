package org.rs2server.rs2.mysql.impl;

import java.sql.*;
import org.rs2server.rs2.Constants;
import org.rs2server.rs2.domain.service.api.PermissionService.PlayerPermissions;
import org.rs2server.rs2.model.player.Player;

/**
 * Handles updating of players online.
 * 
 * @author Vichy
 */
public class PlayersOnlineManager implements Runnable {

	/**
	 * Database creditentials
	 */
	public static final String HOST = Constants.DEBUG ? "144.217.242.115" : "localhost"; // website ip address
	public static final String USER = "salve-ps_admin";
	public static final String PASS = "bCs6m_17";
	public static final String DATABASE = "admin_players";

	/**
	 * Defining the player to update
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
	 * Defining our Prepared statement.
	 */
	PreparedStatement statement;

	/**
	 * Whether we're deleting the player name from our database
	 */
	private boolean remove;

	/**
	 * Constructing this class
	 * 
	 * @param player
	 *            the players hiscores.
	 */
	public PlayersOnlineManager(Player player, boolean remove) {
		this.player = player;
		this.remove = remove;
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

			if (Constants.DEBUG)
				return;

			if (!connect(HOST, DATABASE, USER, PASS))
				return;

			String name = player.getName();

			if (remove) {
				statement = prepare("DELETE FROM players WHERE username=?");
				statement.setString(1, name);
				statement.execute();
			} else {
				statement = prepare(generateQuery());
				statement.setString(1, name);
				statement.setString(2, getPlayerIcon());
			}

			statement.execute();

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
		sb.append("INSERT INTO players (");
		sb.append("username, ");
		sb.append("image)");
		sb.append("VALUES (?, ?)");
		return sb.toString();
	}

	/**
	 * Gets players rights as integer used to display them on the websites hiscores.
	 * 
	 * @return the integer rights
	 */
	private String getPlayerIcon() {
		if (player.getName().equalsIgnoreCase("salve"))
			return "owner.png";
		if (player.isAdministrator())
			return "admin.png";
		if (player.getPermissionService().is(player, PlayerPermissions.MODERATOR))
			return "game_mod.gif";
		if (player.getPermissionService().is(player, PlayerPermissions.HELPER))
			return "support.png";
		if (player.getPermissionService().is(player, PlayerPermissions.HARDCORE_IRON_MAN))
			return "hardcore_ironman.png";
		if (player.getPermissionService().is(player, PlayerPermissions.ULTIMATE_IRON_MAN))
			return "ultimate_ironman.png";
		if (player.getPermissionService().is(player, PlayerPermissions.IRON_MAN))
			return "ironman.png";
		if (player.getPermissionService().is(player, PlayerPermissions.YOUTUBER))
			return "youtuber.png";
		if (player.getPermissionService().is(player, PlayerPermissions.DIAMOND_MEMBER))
			return "diamond.png";
		if (player.getPermissionService().is(player, PlayerPermissions.PLATINUM_MEMBER))
			return "platinum.png";
		if (player.getPermissionService().is(player, PlayerPermissions.GOLD_MEMBER))
			return "gold.png";
		if (player.getPermissionService().is(player, PlayerPermissions.SILVER_MEMBER))
			return "silver.png";
		if (player.getPermissionService().is(player, PlayerPermissions.BRONZE_MEMBER))
			return "bronze.png";
		return "";
	}
}