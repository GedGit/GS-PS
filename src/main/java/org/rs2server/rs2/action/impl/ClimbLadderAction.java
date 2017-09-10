package org.rs2server.rs2.action.impl;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;

/**
 * An action for which a player climbs a ladder.
 *
 * @author tommo
 */
public class ClimbLadderAction extends AbstractStatefulAction<ClimbLadderAction.ClimbState> {

	private static final int CLIMB_LADDER_ANIMATION_ID = 828;

	private final Location destination;

	/**
	 * Creates a new ActionEvent.
	 *
	 * @param mob The entity.
	 * @param destination The destination to where the entity will be teleported.
	 */
	public ClimbLadderAction(final Mob mob, final Location destination) {
		super(mob, 0, ClimbState.ANIMATE);
		this.destination = destination;
	}

	@Override
	public CancelPolicy getCancelPolicy() {
		return CancelPolicy.ONLY_ON_WALK;
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
	public ClimbState onState(ClimbState state) {
		if (state == ClimbState.ANIMATE) {
			getMob().playAnimation(Animation.create(CLIMB_LADDER_ANIMATION_ID));
			return ClimbState.PAUSE;
		} else if (state == ClimbState.PAUSE) {
			return ClimbState.CLIMB;
		} else {
			getMob().setTeleportTarget(destination);
			getMob().getActionSender().sendMessage("You climb the ladder.");
			return null;
		}
	}

	public enum ClimbState {
		ANIMATE, PAUSE, CLIMB
	}

}
