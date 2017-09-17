package org.rs2server.rs2.action;

import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.tickable.Tickable;

/**
 * A <code>Tickable</code> used for handling game actions.
 * @author blakeman8192
 * @author Graham Edgecombe
 * 
 */
public abstract class Action extends Tickable {
	
	/**
	 * A stack policy determines whether this action should be stopped by other actions.
	 * @author Graham Edgecombe
	 *
	 */
	public enum StackPolicy {
		
		/**
		 * This indicates this action will stack with others.
		 */
		ALWAYS,
		
		/**
		 * This indicates this action will not stack with others.
		 */
		NEVER
		
	}
	
	/**
	 * A cancel policy determines when the action should destruct.
	 * @author Graham Edgecombe
	 *
	 */
	public enum CancelPolicy {
		
		/**
		 * This indicates actions will cancelled on any interaction.
		 */
		ALWAYS,
		
		/**
		 * This indicates actions will be cancelled only when walking.
		 */
		ONLY_ON_WALK
		
	}
	
	/**
	 * An animation policy determines if the action should reset animations when cancelled.
	 * @author Graham Edgecombe
	 *
	 */
	public enum AnimationPolicy {
		
		/**
		 * This indicates the action will reset your animation.
		 */
		RESET_ALL,
		
		/**
		 * This indicates the action will not reset your animation.
		 */
		RESET_NONE
		
	}

	/**
	 * The <code>Entity</code> associated with this ActionEvent.
	 */
	private Mob mob;
	
	/**
	 * The delay between execution intervals.
	 */
	private int ticks;
	
	/**
	 * The current amount of ticks before this action executes.
	 */
	private int currentTicks;
	
	/**
	 * Does this action continue.
	 */
	private boolean running = true;

	/**
	 * Creates a new ActionEvent.
	 * @param mob The entity.
	 * @param delay The initial delay.
	 */
	public Action(Mob mob, int ticks) {
		super(ticks);
		this.ticks = ticks;
		this.currentTicks = ticks;
		this.mob = mob;
	}

	/**
	 * Gets the entity.
	 * @return The entity.
	 */
	public Mob getMob() {
		return mob;
	}
	
	/**
	 * Gets the cancel policy of this action.
	 * @return The cancel policy of this action.
	 */
	public abstract CancelPolicy getCancelPolicy();
	
	/**
	 * Gets the stack policy of this action.
	 * @return The stack policy of this action.
	 */
	public abstract StackPolicy getStackPolicy();
	
	/**
	 * Gets the animation policy of this action.
	 * @return The animation policy of this action.
	 */
	public abstract AnimationPolicy getAnimationPolicy();

	/**
	 * @return The ticks.
	 */
	public int getTicks() {
		return ticks;
	}

	/**
	 * @return The currentTicks.
	 */
	public int getCurrentTicks() {
		return currentTicks;
	}

	/**
	 * @param currentTicks The currentTicks to set.
	 */
	public void setCurrentTicks(int currentTicks) {
		this.currentTicks = currentTicks;
	}

	/**
	 * @param ticks The ticks to set.
	 */
	public void setTicks(int ticks) {
		this.ticks = ticks;
	}

	/**
	 * @param ticks The ticks to set.
	 */
	public void decreaseTicks(int amount) {
		this.currentTicks -= amount;
	}
	
	public void stop() {
		running = false;
	}

	/**
	 * @return The running.
	 */
	public boolean isRunning() {
		return running;
	}
	
	/**
	 * The execute method is called when the tick is run. The general contract
	 * of the execute method is that it may take any action whatsoever.
	 */
	public abstract void execute();

}
