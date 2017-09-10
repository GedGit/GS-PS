package org.rs2server.rs2.content.api;

import org.rs2server.rs2.model.player.Player;

import javax.annotation.concurrent.Immutable;

/**
 * An event which is fired when a player logs into the game world.
 *
 * @author tommo
 */
@Immutable
public class GamePlayerLoginEvent {

	private Player player;

	public GamePlayerLoginEvent(final Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}
}
