package org.rs2server.rs2.tickable.impl;

import org.rs2server.rs2.tickable.Tickable;

/**
 * @author twelve
 */
public abstract class StoppingTick extends Tickable {

	/**
	 * Creates a tickable with the specified amount of ticks.
	 *
	 * @param ticks
	 *            The amount of ticks.
	 */
	public StoppingTick(int ticks) {
		super(ticks);
	}

	@Override
	public void execute() {
		stop();
		executeAndStop();
	}

	public abstract void executeAndStop();
}
