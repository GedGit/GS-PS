package org.rs2server.rs2.content.api;

import org.rs2server.rs2.model.Mob;

import javax.annotation.concurrent.Immutable;

/**
 * An event which is fired when a mob has died.
 *
 * @author tommo
 */
@Immutable
public class GameMobDeathEvent {

	private final Mob mob;

	/**
	 * The killer. Can be null.
	 */
	private final Mob killer;

	public GameMobDeathEvent(final Mob mob, final Mob killer) {
		this.mob = mob;
		this.killer = killer;
	}

	public Mob getMob() {
		return mob;
	}

	public Mob getKiller() {
		return killer;
	}
}
