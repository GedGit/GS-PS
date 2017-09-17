package org.rs2server.rs2.mysql.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.rs2server.cache.format.CacheItemDefinition;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.util.DonationRank;

/**
 * Using this class: To call this class, it's best to make a new thread. You can
 * do it below like so: new Thread(new Donation(player)).start();
 */
public class DonationManager implements Runnable {

	public static final String HOST = "localhost"; // website ip address
	public static final String USER = "salve-ps_admin";
	public static final String PASS = "bCs6m_17";
	public static final String DATABASE = "admin_store";

	private Player player;
	private Connection conn;
	private Statement stmt;

	/**
	 * The constructor
	 * 
	 * @param player
	 */
	public DonationManager(Player player) {
		this.player = player;
	}

	@Override
	public void run() {
		try {
			
			if (!connect(HOST, DATABASE, USER, PASS))
				return;

			String name = player.getName().replace("_", " ");
			ResultSet rs = executeQuery(
					"SELECT * FROM payments WHERE player_name='" + name + "' AND status='Completed' AND claimed=0");

			while (rs.next()) {
				int item_number = rs.getInt("item_number");
				double paid = rs.getDouble("amount");
				int quantity = rs.getInt("quantity");

				switch (item_number) {

				case 14: // ags
					handleAddition(player, new Item(11802, quantity), (int) paid);
					break;
				case 15: // sgs
					handleAddition(player, new Item(11806, quantity), (int) paid);
					break;
				case 16: // d warhammer
					handleAddition(player, new Item(13576, quantity), (int) paid);
					break;
				case 17: // d claws
					handleAddition(player, new Item(13652, quantity), (int) paid);
					break;
				case 18: // toxic staff of the dead
					handleAddition(player, new Item(12904, quantity), (int) paid);
					break;
				case 19: // arma cbow
					handleAddition(player, new Item(11785, quantity), (int) paid);
					break;
				case 20: // d bow
					handleAddition(player, new Item(11235, quantity), (int) paid);
					break;
				case 21: // whip
					handleAddition(player, new Item(4151, quantity), (int) paid);
					break;
				case 22: // sara sword
					handleAddition(player, new Item(11838, quantity), (int) paid);
					break;
				case 23: // d defender
					handleAddition(player, new Item(12954, quantity), (int) paid);
					break;
				case 24: // f torso
					handleAddition(player, new Item(10551, quantity), (int) paid);
					break;
				case 25: // fury
					handleAddition(player, new Item(6585, quantity), (int) paid);
					break;
				case 26: // warriors ring i
					handleAddition(player, new Item(11772, quantity), (int) paid);
					break;
				case 27: // seers ring i
					handleAddition(player, new Item(11770, quantity), (int) paid);
					break;
				case 28: // archers ring i
					handleAddition(player, new Item(11771, quantity), (int) paid);
					break;
				case 29: // berserker ring
					handleAddition(player, new Item(11773, quantity), (int) paid);
					break;
				case 30: // torag armor set
					handleAddition(player, new Item(12879, quantity), (int) paid);
					break;
				case 31: // karils armor set
					handleAddition(player, new Item(12883, quantity), (int) paid);
					break;
				case 32: // dharoks armor set
					handleAddition(player, new Item(12877, quantity), (int) paid);
					break;
				case 33: // guthans armor set
					handleAddition(player, new Item(12873, quantity), (int) paid);
					break;
				case 34: // ahrims armor set
					handleAddition(player, new Item(12881, quantity), (int) paid);
					break;
				case 35: // veracs armor set
					handleAddition(player, new Item(12875, quantity), (int) paid);
					break;
				case 36: // fire cape
					handleAddition(player, new Item(6570, quantity), (int) paid);
					break;
				case 37: // dragon bolts e
					handleAddition(player, new Item(9244, quantity * 250), (int) paid);
					break;
				case 38: // onyx bolts e
					handleAddition(player, new Item(9245, quantity * 250), (int) paid);
					break;
				case 39: // ruby bolts e
					handleAddition(player, new Item(9242, quantity * 250), (int) paid);
					break;
				case 40: // 250 dark crab
					handleAddition(player, new Item(11936, quantity * 250), (int) paid);
					break;
				case 41: // 250 anglerfish
					handleAddition(player, new Item(13441, quantity * 250), (int) paid);
					break;
				case 42: // 50 combat pots
					handleAddition(player, new Item(12695, quantity * 50), (int) paid);
					break;
				case 43: // dwarf cannon
					handleAddition(player, new Item(12863, quantity), (int) paid);
					break;
				case 44: // 250 cballs
					handleAddition(player, new Item(2, quantity * 250), (int) paid);
					break;
				case 45: // dice bag TODO
					handleAddition(player, new Item(2, quantity), (int) paid);
					break;
				case 46: // mystery box
					handleAddition(player, new Item(6199, quantity), (int) paid);
					break;
				case 47: // augmented box TODO
					handleAddition(player, new Item(11806, quantity), (int) paid);
					break;
				case 49: // 2 crystal key
					handleAddition(player, new Item(989, quantity * 2), (int) paid);
					break;
				case 50: // halloween set
					handleAddition(player, new Item(13175, quantity), (int) paid);
					break;
				case 51: // partyhat set
					handleAddition(player, new Item(13173, quantity), (int) paid);
					break;
				case 52: // santa hat
					handleAddition(player, new Item(1050, quantity), (int) paid);
					break;
				case 53: // pet penance queen
					handleAddition(player, new Item(12703, quantity), (int) paid);
					break;
				case 54: // pet dark core
					handleAddition(player, new Item(12816, quantity), (int) paid);
					break;
				case 55: // pet chompy chick
					handleAddition(player, new Item(13071, quantity), (int) paid);
					break;
				case 56: // christmas cracker
					handleAddition(player, new Item(962, quantity), (int) paid);
					break;
				case 57: // 2 herb box
					handleAddition(player, new Item(11738, quantity * 2), (int) paid);
					break;
				}
				rs.updateInt("claimed", 1);
				rs.updateRow();
			}
			this.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param host
	 *            the host ip address or url
	 * @param database
	 *            the name of the database
	 * @param user
	 *            the user attached to the database
	 * @param pass
	 *            the users password
	 * @return true if connected
	 */
	public boolean connect(String host, String database, String user, String pass) {
		try {
			this.conn = DriverManager.getConnection("jdbc:mysql://" + host + ":3306/" + database, user, pass);
			return true;
		} catch (SQLException e) {
			System.out.println("Failing connecting to database!");
			return false;
		}
	}

	/**
	 * Disconnects from the MySQL server and destroy the connection and
	 * statement instances
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
	 * Executres a query on the database
	 * 
	 * @param query
	 * @see {@link Statement#executeQuery(String)}
	 * @return the results, never null
	 */
	public ResultSet executeQuery(String query) {
		try {
			this.stmt = this.conn.createStatement(1005, 1008);
			ResultSet results = stmt.executeQuery(query);
			return results;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Handles the adding of purchased products and logging the system.
	 * 
	 * @param player
	 *            the player that donated.
	 * @param item
	 *            the item product donated for.
	 * @param donationAmount
	 *            the total amount donated.
	 */
	private void handleAddition(Player player, Item item, int donationAmount) {

		int requiredSlots = item.getCount();

		if (item.getDefinition().isStackable())
			requiredSlots = 1;

		if (player.getInventory().freeSlots() >= requiredSlots
				|| (player.getInventory().contains(item.getId()) && item.getDefinition().isStackable()))
			player.getInventory().add(item);
		else
			player.getBank().add(item);

		String itemName = CacheItemDefinition.get(item.getId()).getName();

		player.sendMessage(
				"You've purchased: [<col=ff0000>" + item.getCount() + "</col> x <col=ff0000>" + itemName + "</col>].");

		DonationRank.handleDonation(player, item, (int) donationAmount);
	}
}
