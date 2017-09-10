package org.rs2server.rs2.domain.service.api;

import org.rs2server.rs2.model.player.Player;

/**
 * @author tommo
 */
public interface DebugService {

	/**
	 * Toggles debug mode for the given player.
	 * @param player The player.
	 * @param debug debug mode.
	 */
	void toggleDebug(Player player, boolean debug);

}
