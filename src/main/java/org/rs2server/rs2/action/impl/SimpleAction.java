package org.rs2server.rs2.action.impl;

import org.rs2server.rs2.action.Action;
import org.rs2server.rs2.model.Mob;

/**
 * Simple action implementation which only expects <code>execute()</code> to be implemented.
 *
 * @author tommo
 */
public abstract class SimpleAction extends Action {

	public SimpleAction(Mob mob, int ticks) {
		super(mob, ticks);
	}

	@Override
	public CancelPolicy getCancelPolicy() {
		return CancelPolicy.ALWAYS;
	}

	@Override
	public StackPolicy getStackPolicy() {
		return StackPolicy.ALWAYS;
	}

	@Override
	public AnimationPolicy getAnimationPolicy() {
		return AnimationPolicy.RESET_ALL;
	}

	@Override
	public abstract void execute();

}
