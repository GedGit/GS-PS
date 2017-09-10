package org.rs2server.rs2.action.impl;

import org.rs2server.rs2.action.Action;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;

/**
 * An action which will delegate to a given action upon reaching a given location.
 *
 * @author tommo
 */
public class LocationDelegateAction extends Action {

	public LocationDelegateAction(final Mob mob, final Location location) {
		super(mob, 0);
	}

	@Override
	public CancelPolicy getCancelPolicy() {
		return CancelPolicy.ALWAYS;
	}

	@Override
	public StackPolicy getStackPolicy() {
		return StackPolicy.NEVER;
	}

	@Override
	public AnimationPolicy getAnimationPolicy() {
		return AnimationPolicy.RESET_ALL;
	}

	@Override
	public void execute() {

	}
}
