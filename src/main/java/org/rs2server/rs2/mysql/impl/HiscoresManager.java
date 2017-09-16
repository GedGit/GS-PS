package org.rs2server.rs2.mysql.impl;

import java.sql.*;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.domain.service.api.PermissionService.PlayerPermissions;
import org.rs2server.rs2.model.player.Player;

/**
 * Handles updating of websites database hiscores.
 * 
 * @author Vichy
 */
public class HiscoresManager implements Runnable {

	/**
	 * Database creditentials
	 */
	public static final String HOST = "localhost"; // website ip address
	public static final String USER = "salve-ps_admin";
	public static final String PASS = "bCs6m_17";
	public static final String DATABASE = "admin_hiscores";

	/**
	 * Database table
	 */
	public static final String TABLE = "hs_users";

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
	public HiscoresManager(Player player) {
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
		if (Constants.DEBUG)
			return false;
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

			PreparedStatement statement = prepare("DELETE FROM " + TABLE + " WHERE username=?");
			statement.setString(1, name);

			// Execute twice since it likes to add it twice, dunno why TODO
			statement.execute();
			statement.execute();

			PreparedStatement stmt2 = prepare(generateQuery());
			stmt2.setString(1, name);
			stmt2.setInt(2, getPlayerRights()); // player
												// rights

			stmt2.setInt(3, getGameMode()); // game mode number
			stmt2.setInt(4, player.getSkills().getTotalLevel());

			stmt2.setLong(5, player.getSkills().getTotalExperience());

			// We don't have summoning as an actually train-able skill,
			// stopping at construction
			for (int i = 0; i < 23; i++)
				stmt2.setInt(6 + i, (int) player.getSkills().getExperience(i));

			stmt2.execute();

			if (Constants.DEBUG)
				System.out
						.println("Saved players [" + name + "] hiscores; hiscores rights - " + getPlayerRights() + ".");

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
		sb.append("INSERT INTO " + TABLE + " (");
		sb.append("username, ");
		sb.append("rights, ");
		sb.append("mode, ");
		sb.append("total_level, ");
		sb.append("overall_xp, ");
		sb.append("attack_xp, ");
		sb.append("defence_xp, ");
		sb.append("strength_xp, ");
		sb.append("constitution_xp, ");
		sb.append("ranged_xp, ");
		sb.append("prayer_xp, ");
		sb.append("magic_xp, ");
		sb.append("cooking_xp, ");
		sb.append("woodcutting_xp, ");
		sb.append("fletching_xp, ");
		sb.append("fishing_xp, ");
		sb.append("firemaking_xp, ");
		sb.append("crafting_xp, ");
		sb.append("smithing_xp, ");
		sb.append("mining_xp, ");
		sb.append("herblore_xp, ");
		sb.append("agility_xp, ");
		sb.append("thieving_xp, ");
		sb.append("slayer_xp, ");
		sb.append("farming_xp, ");
		sb.append("runecrafting_xp, ");
		sb.append("hunter_xp, ");
		sb.append("construction_xp)");
		sb.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		return sb.toString();
	}

	/**
	 * Gets players game-mode as integer.
	 * 
	 * @return the game-mode
	 */
	private int getGameMode() {
		if (player.getPermissionService().is(player, PlayerPermissions.IRON_MAN))
			return 1;
		if (player.getPermissionService().is(player, PlayerPermissions.ULTIMATE_IRON_MAN))
			return 2;
		if (player.getPermissionService().is(player, PlayerPermissions.HARDCORE_IRON_MAN))
			return 3;
		return 0;
	}

	/**
	 * Gets players rights as integer used to display them on the websites hiscores.
	 * 
	 * @return the integer rights
	 */
	private int getPlayerRights() {
		if (player.getPermissionService().is(player, PlayerPermissions.MODERATOR))
			return 1;
		if (player.getPermissionService().is(player, PlayerPermissions.HELPER))
			return 2;
		if (player.getPermissionService().is(player, PlayerPermissions.YOUTUBER))
			return 9;
		if (player.getPermissionService().is(player, PlayerPermissions.DIAMOND_MEMBER))
			return 7;
		if (player.getPermissionService().is(player, PlayerPermissions.PLATINUM_MEMBER))
			return 6;
		if (player.getPermissionService().is(player, PlayerPermissions.GOLD_MEMBER))
			return 5;
		if (player.getPermissionService().is(player, PlayerPermissions.SILVER_MEMBER))
			return 4;
		if (player.getPermissionService().is(player, PlayerPermissions.BRONZE_MEMBER))
			return 3;
		return 0;
	}
}