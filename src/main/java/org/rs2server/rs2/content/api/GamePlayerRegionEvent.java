package org.rs2server.rs2.content.api;

import org.rs2server.rs2.model.player.Player;

import javax.annotation.concurrent.Immutable;

/**
 * A event fired when a player enters a new region.
 *
 * @author tommo
 */
@Immutable
public class GamePlayerRegionEvent {

	private final Player player;

	public GamePlayerRegionEvent(final Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}
}
