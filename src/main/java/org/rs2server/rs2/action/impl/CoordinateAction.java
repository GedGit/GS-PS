package org.rs2server.rs2.action.impl;

import org.rs2server.rs2.action.Action;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.skills.fish.Fishing;

/**
 * An action for arriving to a location before performing an action.
 *
 */

@SuppressWarnings("unused")
public class CoordinateAction extends Action {

	/**
	 * The action.
	 */
	private Action action;

	/**
	 * The distance required to be near the location.
	 */
	private int distance;

	/**
	 * The allocated width.
	 */
	private int width;

	/**
	 * The allocated height.
	 */
	private int height;

	/**
	 * The required location.
	 */
	private Location otherLocation;

	/**
	 * The other locations width.
	 */
	private int otherWidth;

	/**
	 * The other locations height.
	 */
	private int otherHeight;
	
	private Fishing fishing;

	private int id;

	public CoordinateAction(final Mob mob, final Location target, final Action action) {
		this(mob, mob.getWidth(), mob.getHeight(), target, 1, 1, (int) mob.getLocation().distance(target), action);
	}

	/**
	 * Creates the action.
	 * @param mob The mob.
	 * @param action The action.
	 * @param location The required location.
	 */
	public CoordinateAction(Mob mob, int width, int height, Location otherLocation, int otherWidth, int otherHeight, int distance, Action action, int id) {
		super(mob, 0);
		this.action = action;
		this.distance = distance;
		this.width = width;
		this.height = height;
		this.otherLocation = otherLocation;
		this.otherWidth = otherWidth;
		this.otherHeight = otherHeight;
		this.id = id;
	}

	public CoordinateAction(Mob mob, int width, int height, Location otherLocation, int otherWidth, int otherHeight, int distance, Action action) {
		super(mob, 0);
		this.action = action;
		this.distance = distance;
		this.width = width;
		this.height = height;
		this.otherLocation = otherLocation;
		this.otherWidth = otherWidth;
		this.otherHeight = otherHeight;
	}
	
	public CoordinateAction(Mob mob, int width, int height, Location otherLocation, int otherWidth, int otherHeight, int distance, Action action, Fishing fishing) {
		super(mob, 0);
		this.action = action;
		this.distance = distance;
		this.width = width;
		this.height = height;
		this.otherLocation = otherLocation;
		this.otherWidth = otherWidth;
		this.otherHeight = otherHeight;
		this.fishing = fishing;
	}

	@Override
	public void execute() {
		// DO NOT RE ENABLE THIS
		//if (getMob().getLocation().isWithinDistance(width, height, otherLocation, otherWidth, otherHeight, distance)) {
		if (getMob().getWalkingQueue().isEmpty()) {
			if (fishing == null) {
				getMob().getActionQueue().addAction(action);
				this.stop();
			} else {
				if (getMob().isPlayer()) {
					fishing.execute();
					Player player = (Player) getMob();
					player.submitTick("skill_action_tick", fishing, true);
					this.stop();
				}
			}
		}
	}

	@Override
	public AnimationPolicy getAnimationPolicy() {
		return AnimationPolicy.RESET_NONE;
	}

	@Override
	public CancelPolicy getCancelPolicy() {
		return CancelPolicy.ALWAYS;
	}

	@Override
	public StackPolicy getStackPolicy() {
		return StackPolicy.NEVER;
	}
}
