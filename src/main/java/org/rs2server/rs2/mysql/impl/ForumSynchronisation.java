package org.rs2server.rs2.mysql.impl;

import java.sql.*;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.domain.service.api.PermissionService.PlayerPermissions;
import org.rs2server.rs2.model.player.Player;

/**
 * Handles updating of IPB forum groups.
 * 
 * @author Vichy
 */
public class ForumSynchronisation implements Runnable {

	/**
	 * Database creditentials
	 */
	public static final String HOST = Constants.DEBUG ? "144.217.242.115" : "localhost";
	public static final String USER = "salve-ps_admin";
	public static final String PASS = "bCs6m_17";
	public static final String DATABASE = "admin_forums";

	/**
	 * Database table
	 */
	public static final String TABLE = "members";

	/**
	 * Defining the players hiscores table to update
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
	 *            the players hiscores.
	 */
	public ForumSynchronisation(Player player) {
		this.player = player;
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
		// if (Constants.DEBUG)
		// return false;
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

			// Don't update administrators since I don't want to be demoted to admin lool
			if (player.isAdministrator() || player.getName().equalsIgnoreCase("cloud"))
				return;

			// Don't update moderators since we have ingame/forum/global roles
			if (getMemberGroupId() == 8)
				return;

			// If we can't connect we shouldn't try anything - simple.
			if (!connect(HOST, DATABASE, USER, PASS))
				return;

			String name = player.getName();

			PreparedStatement statement = prepare("UPDATE members SET member_group_id=? WHERE name=?");

			statement.setInt(1, getMemberGroupId());
			statement.setString(2, name);

			statement.executeUpdate();
			statement.close();

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
	 * Gets players rights as integer used to display them on the websites hiscores.
	 * 
	 * @return the integer rights
	 */
	private int getMemberGroupId() {
		if (player.getPermissionService().is(player, PlayerPermissions.MODERATOR))
			return 8; // need to do game, forum and global mods seperately
		if (player.getPermissionService().is(player, PlayerPermissions.HELPER))
			return 11;
		if (player.getPermissionService().is(player, PlayerPermissions.YOUTUBER))
			return 17;
		if (player.getPermissionService().is(player, PlayerPermissions.DIAMOND_MEMBER))
			return 12;
		if (player.getPermissionService().is(player, PlayerPermissions.PLATINUM_MEMBER))
			return 13;
		if (player.getPermissionService().is(player, PlayerPermissions.GOLD_MEMBER))
			return 14;
		if (player.getPermissionService().is(player, PlayerPermissions.SILVER_MEMBER))
			return 15;
		if (player.getPermissionService().is(player, PlayerPermissions.BRONZE_MEMBER))
			return 16;
		if (player.getPermissionService().is(player, PlayerPermissions.ULTIMATE_IRON_MAN))
			return 20;
		if (player.getPermissionService().is(player, PlayerPermissions.HARDCORE_IRON_MAN))
			return 19;
		if (player.getPermissionService().is(player, PlayerPermissions.IRON_MAN))
			return 18;
		return 3;
	}
}