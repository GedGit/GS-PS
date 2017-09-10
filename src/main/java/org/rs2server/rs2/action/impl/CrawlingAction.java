package org.rs2server.rs2.action.impl;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;

/**
 * @author tommo
 */
public class CrawlingAction extends AbstractStatefulAction<CrawlingAction.CrawlingState> {

	private static final Animation CRAWLING_ANIMATION = Animation.create(2796);

	private final Location destination;

	/**
	 * Creates a new ActionEvent.
	 *
	 * @param mob The entity.
	 * @param destination The destination to where the entity will be teleported.
	 */
	public CrawlingAction(final Mob mob, final Location destination) {
		super(mob, 0, CrawlingState.ANIMATE);
		this.destination = destination;
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
	public CrawlingState onState(CrawlingState state) {
		if (state == CrawlingState.ANIMATE) {
			getMob().playAnimation(CRAWLING_ANIMATION);
			return CrawlingState.PAUSE;
		} else if (state == CrawlingState.PAUSE) {
			return CrawlingState.TELEPORT;
		} else {
			getMob().setLocation(destination);
			getMob().getActionSender().sendMessage("You crawl through the tunnel.");
			return null;
		}
	}

	public enum CrawlingState {
		ANIMATE, PAUSE, TELEPORT
	}

}
