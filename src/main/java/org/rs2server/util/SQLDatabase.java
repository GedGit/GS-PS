package org.rs2server.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SQLDatabase {

	private static SQLDatabase singleton;
	private static ExecutorService SQLService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	public Connection getConnection(final String user, final String pass, final String host) throws SQLException {

		Connection conn = null;
		Properties connectionProps = new Properties();
		connectionProps.put("user", user);
		connectionProps.put("password", pass);
		conn = DriverManager.getConnection("jdbc:mysql://" + host + "/", connectionProps);
		return conn;
	}

	public static final long getTotals(int... values) {
		long total = 0;
		for(final int i : values) {
			total += i;
		}
		return total;
	}

	public void update(final String username, final int attackLevel, final int attackExperience, final int defenceLevel, final int defenceExperience, final int strengthLevel, final int strengthExperience,
			final int hitpointsLevel, final int hitpointsExperience, final int rangedLevel, final int rangedExperience, final int prayerLevel, final int prayerExperience, 
			final int magicLevel, final int magicExperience, final int cookingLevel, final int cookingExperience, final int woodcuttingLevel, final int woodcuttingExperience,
			final int fletchingLevel, final int fletchingExperience, final int fishingLevel, final int fishingExperience, final int firemakingLevel, final int firemakingExperience,
			final int craftingLevel, final int craftingExperience, final int smithingLevel, final int smithingExperience, final int miningLevel, final int miningExperience,
			final int herbloreLevel, final int herbloreExperience, final int agilityLevel, final int agilityExperience, final int thievingLevel, final int thievingExperience,
			final int slayerLevel, final int slayerExperience, final int farmingLevel, final int farmingExperience, final int runecraftingLevel, final int runecraftingExperience,
			final int totalLevel, final long totalExperience, final int kills, final int deaths, final float kdr, final int rank,
			final int zulrahKills, final int armadylKills, final int bandosKills, final int saradominKills, final int zamorakKills, final int callistoKills, final int vetionKills, final int venenatisKills,
			final long bestZulrahTime, final int chaos_fanatic_kills, final int lizardman_shaman_kills, final int cerberusKills, final int chaos_elemental_kills) throws SQLException {
		SQLService.submit(() -> {
			Connection connection = null;
			Statement statement = null;
			ResultSet resultSet = null;

			try{
				connection = getConnection("lostisle_player", "sebtimqwe3321", "162.212.253.190");
				statement = connection.createStatement();
				statement.executeUpdate("DELETE FROM `lostisle_highscore`.`highscores` WHERE `Name` = '"+username+"';");
				statement.close();

				statement = connection.createStatement();
				statement.executeUpdate("INSERT INTO `lostisle_highscore`.`highscores` "
						+ "(`Name`, `0l`, `0xp`, `1l`, `1xp`, `2l`, `2xp`, `3l`, `3xp`, `4l`, `4xp`,"
						+ " `5l`, `5xp`, `6l`, `6xp`, `7l`, `7xp`, `8l`, `8xp`,"
						+ " `9l`, `9xp`, `10l`, `10xp`, `11l`, `11xp`, `12l`, `12xp`,"
						+ " `13l`, `13xp`, `14l`, `14xp`, `15l`, `15xp`, `16l`, `16xp`,"
						+ " `17l`, `17xp`, `18l`, `18xp`, `19l`, `19xp`, `20l`, `20xp`,"
						+ " `21l`, `21xp`, `total_level`, `total_exp`, `kills`, `deaths`, `kdr`, `rank`, `zulrah_kills`, `armadyl_kills`, `bandos_kills`, `saradomin_kills`, `zamorak_kills`, `callisto_kills`, `vetion_kills`, `venenatis_kills`,"
						+ "`best_zulrah_time`, `chaos_fanatic_kills`, `lizardman_shaman_kills`, `cerberus_kils`, `chaos_elemental_kills`)"
						+ " VALUES "
						+ "('"+username+"', '"+attackLevel+"', '"+attackExperience+"', '"+defenceLevel+"', '"+defenceExperience+"', '"+strengthLevel+"', '"+strengthExperience+"', '"+hitpointsLevel+"', '"+hitpointsExperience+"', '"+rangedLevel+"', '"+rangedExperience+"',"
						+ " '"+prayerLevel+"', '"+prayerExperience+"', '"+magicLevel+"', '"+magicExperience+"', '"+cookingLevel+"', '"+cookingExperience+"', '"+woodcuttingLevel+"', '"+woodcuttingExperience+"',"
						+ " '"+fletchingLevel+"', '"+fletchingExperience+"', '"+fishingLevel+"', '"+fishingExperience+"', '"+firemakingLevel+"', '"+firemakingExperience+"', '"+craftingLevel+"', '"+craftingExperience+"',"
						+ " '"+smithingLevel+"', '"+smithingExperience+"', '"+miningLevel+"', '"+miningExperience+"', '"+herbloreLevel+"', '"+herbloreExperience+"', '"+agilityLevel+"', '"+agilityExperience+"',"
						+ " '"+thievingLevel+"', '"+thievingExperience+"', '"+slayerLevel+"', '"+slayerExperience+"', '"+farmingLevel+"', '"+farmingExperience+"', '"+runecraftingLevel+"', '"+runecraftingExperience+"',"
						+ " '0', '0', '"+totalLevel+"', '"+totalExperience+"', '"+kills+"', '"+deaths+"', '"+kdr+"', '"+rank+"', '" + zulrahKills  +"', '" + armadylKills + "', '" + bandosKills + "', '" + saradominKills + "', '" + zamorakKills + "', '" + callistoKills + "', '" + vetionKills + "', '" + venenatisKills + "',"
						
						+ "'" + bestZulrahTime + "', '"+chaos_fanatic_kills+"', '"+lizardman_shaman_kills+ "', '"+cerberusKills+ "', '"+chaos_elemental_kills+ "');");
			} catch(SQLException e) {
				e.printStackTrace();
			} finally {
				if (resultSet != null) try { resultSet.close(); } catch (SQLException ignore) {}
				if (statement != null) try { statement.close(); } catch (SQLException ignore) {}
				if (connection != null) try { connection.close(); } catch (SQLException ignore) {}
			}
		});

	}

	public static SQLDatabase getSingleton() {
		if(singleton == null) {
			singleton = new SQLDatabase();
		}
		return singleton;
	}

}

