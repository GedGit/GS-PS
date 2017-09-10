package org.rs2server.rs2.task.impl;

import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.task.Task;

/**
 * A task that is executed when a player has logged in.
 * @author Graham Edgecombe
 *
 */
public class SessionLoginTask implements Task {

	/**
	 * The player.
	 */
	private Player player;
	
	/**
	 * Creates the session login task.
	 * @param player The player that logged in.
	 */
	public SessionLoginTask(Player player) {
		this.player = player;
	}

	@Override
	public void execute() {
		World.getWorld().register(player);
	}

}
