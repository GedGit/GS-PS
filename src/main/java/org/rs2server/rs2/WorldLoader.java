package org.rs2server.rs2;

import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.player.PlayerDetails;

/**
 * An interface which describes the methods for loading persistent world
 * information such as players.
 * @author Graham Edgecombe
 *
 */
public interface WorldLoader {
	
	/**
	 * Represents the result of a login request.
	 * @author Graham Edgecombe
	 */
	class LoginResult {

		public static final int SUCCESS = 2;

        public static final int ALREADY_LOGGED_IN = 5;

		public static final int UPDATE_IN_PROGRESS = 14;

		public static final int VERSION_MISMATCH = 6;

		public static final int INVALID_CREDENTIALS = 3;

		public static final int BANNED = 4;

		public static final int UNKNOWN_ERROR = 11;

		public static final int TOO_MANY_CONNECTIONS = 9;
		
		/**
		 * The return code.
		 */
		private int returnCode;
		
		/**
		 * The player object, or <code>null</code> if the login failed.
		 */
		private Player player;
		
		/**
		 * Creates a login result that failed.
		 * @param returnCode The return code.
		 */
		public LoginResult(int returnCode) {
			this(returnCode, null);
		}
		
		/**
		 * Creates a login result that succeeded.
		 * @param returnCode The return code.
		 * @param player The player object.
		 */
		public LoginResult(int returnCode, Player player) {
			this.returnCode = returnCode;
			this.player = player;
		}
		
		/**
		 * Gets the return code.
		 * @return The return code.
		 */
		public int getReturnCode() {
			return returnCode;
		}
		
		/**
		 * Gets the player.
		 * @return The player.
		 */
		public Player getPlayer() {
			return player;
		}
		
	}
	
	/**
	 * Checks if a set of login details are correct. If correct, creates but
	 * does not load, the player object.
	 * @param pd The login details.
	 * @return The login result.
	 */
	LoginResult checkLogin(PlayerDetails pd);
	
	/**
	 * Loads player information.
	 * @param player The player object.
	 * @return <code>true</code> on success, <code>false</code> on failure.
	 */
	boolean loadPlayer(Player player);
	
	/**
	 * Saves player information.
	 * @param player The player object.
	 * @return <code>true</code> on success, <code>false</code> on failure.
	 */
	boolean savePlayer(Player player);

}
