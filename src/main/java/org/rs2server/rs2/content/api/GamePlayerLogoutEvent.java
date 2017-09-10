package org.rs2server.rs2.content.api;

import org.rs2server.rs2.model.player.Player;

import javax.annotation.concurrent.Immutable;

/**
 * An event which is fired when a player logs out of the game world.
 *
 * @author tommo
 */
@Immutable
public class GamePlayerLogoutEvent {

	private Player player;

	public GamePlayerLogoutEvent(final Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}
}
