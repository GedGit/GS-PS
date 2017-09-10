package org.rs2server.rs2.content.api;

import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.concurrent.Immutable;

/**
 * An event which is fired when a player digs with a space.
 *
 * @author tommo
 */
@Immutable
public class GameSpadeDigEvent {

	private final Player player;

	private final Location location;

	public GameSpadeDigEvent(final Player player, final Location location) {
		this.player = player;
		this.location = location;
	}

	public Player getPlayer() {
		return player;
	}

	public Location getLocation() {
		return location;
	}
}
