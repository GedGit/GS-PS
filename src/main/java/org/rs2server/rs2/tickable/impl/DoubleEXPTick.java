package org.rs2server.rs2.tickable.impl;

import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;

/**
 * A tickable which deducts 1 second of players double exp seconds
 * 
 * @author Vichy
 */
public class DoubleEXPTick extends Tickable {

	/**
	 * Defining the player.
	 */
	private Player player;

	/**
	 * Creates the tickable to run every 2 ticks which is 1.4 second
	 */
	public DoubleEXPTick(Player player) {
		super(2); // TODO
		this.player = player;
	}

	@Override
	public void execute() {
		// Checks if player is existent
		if (player == null) {
			this.stop();
			return;
		}
		// Checks if player is still online ?
		if (!World.getWorld().isPlayerOnline(player.getName())) {
			this.stop();
			return;
		}
		// Check if the player has more than 0 double exp seconds left
		if (player.getDatabaseEntity().getDoubleExp() > 0) {
			player.getDatabaseEntity().setDoubleExp(player.getDatabaseEntity().getDoubleExp() - 1);

			// If we hit 0 announce the player to go vote
			if (player.getDatabaseEntity().getDoubleExp() == 0)
				player.sendMessage("<col=ff0000>Your double experience has ended; go ::vote to receive more!");
		}
	}
}