package org.rs2server.rs2.content.gwd;

import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.player.Player;

/**
 * Handles everything related to the Godwars dungeon.
 * 
 * @author Vichy
 * 
 * ConfigID 1069
 * for bandos its 1 * bandos kills
 * for zamorak its 4096 * zamorak kills
 * 
 * ConfigID 1048
 * for saradomin it's 153 * sara kills
 *  TODO
 */
public class GodwarsDungeon {

	/**
	 * Finalising the location where player is spawned in.
	 */
	public static final Location DUNGEON_START = Location.create(2882, 5311, 2);

	/**
	 * Handles the dungeon entrance.
	 * 
	 * @param player
	 *            the player entering
	 * @param login
	 *            if the player is logging in while already inside the dungeon
	 * @param teleport
	 *            if being teleported in
	 */
	public static void start(Player player, boolean login, boolean teleport) {
		if (!login) {
			if (!teleport)
				player.climbStairsDown(DUNGEON_START);
			else
				player.teleport(DUNGEON_START, 0, 0, false);
		}
		player.getActionSender().sendWalkableInterface(406).sendConfig(1068, 0).sendConfig(1048, 0);
	}

}
