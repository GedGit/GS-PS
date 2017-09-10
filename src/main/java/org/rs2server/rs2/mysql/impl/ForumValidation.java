package org.rs2server.rs2.mysql.impl;

import java.sql.*;

import org.rs2server.util.login.MD5;

public class ForumValidation implements Runnable {

	private Connection connection = null;
	private Statement statement = null;
	private static Thread thread = null;

	private String[] tableNames = new String[6];

	private void setTables() {
		tableNames = new String[] { "members", "members_display_name", "members_pass_hash", "members_pass_salt",
				"member_group_id", };
	}

	private final String hostAddress, username, password;

	public ForumValidation(String url, String database, String username, String password) {
		this.hostAddress = "jdbc:mysql://" + url + "/" + database;
		this.username = username;
		this.password = password;
		try {
			// connect();
			thread = new Thread(this);
			thread.start();
		} catch (Exception e) {
			connection = null;
			e.printStackTrace();
		}
	}

	private void connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e2) {
			return;
		}
		try {
			connection = DriverManager.getConnection(hostAddress, username, password);
			statement = connection.createStatement();
		} catch (Exception e) {
			connection = null;
			e.printStackTrace();
		}
	}

	private void ping() {
		try {
			@SuppressWarnings("unused")
			ResultSet results = null;
			String query = "SELECT * FROM " + tableNames[0] + " WHERE " + tableNames[2] + " LIKE 'null312'";
			results = statement.executeQuery(query);
		} catch (Exception e) {
			connection = null;
			connect();
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			if (connection == null) {
				setTables();
				connect();
			} else
				ping();
			Thread.sleep(10000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int[] checkUser(String name, String password) {
		int[] returnCodes = { 0, 0 };// return code for client, group id

		try {
			ResultSet results = null;
			String query = "SELECT * FROM " + tableNames[0] + " WHERE " + tableNames[1] + " LIKE '" + name + "'";
			try {
				if (statement == null)
					statement = connection.createStatement();
			} catch (Exception e5) {
				statement = null;
				connection = null;
				connect();
				statement = connection.createStatement();
			}
			results = statement.executeQuery(query);
			if (results.next()) {
				String salt = results.getString(tableNames[3]);
				String pass = results.getString(tableNames[2]);
				int group = results.getInt(tableNames[4]);
				returnCodes[1] = group;
				String pass2 = "";
				pass2 = MD5.MD5(MD5.MD5(salt) + MD5.MD5(password));
				if (pass.equals(pass2)) { // correct pass
					returnCodes[0] = 2;
					return returnCodes;
				} else { // wrong pass
					returnCodes[0] = 3;
					return returnCodes;
				}
			} else { // no user exists TODO auto-create one ;)
				returnCodes[0] = 12;
				return returnCodes;
			}
		} catch (Exception e) {
			statement = null;
			returnCodes[0] = 8;
			return returnCodes;
		}
	}

	public void checkUser(String name) {
		try {
			String query = "SELECT * FROM " + tableNames[0] + " WHERE " + tableNames[1] + " LIKE '" + name + "'";
			try {
				if (statement == null)
					statement = connection.createStatement();
			} catch (Exception e5) {
				statement = null;
				connection = null;
				connect();
				statement = connection.createStatement();
			}
			statement.executeQuery(query);
			
			
		} catch (Exception e) {
			statement = null;
		}
	}
}