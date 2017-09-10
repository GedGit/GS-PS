package org.rs2server.rs2.action.impl;

import org.rs2server.rs2.action.Action;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.net.ActionSender.DialogueType;

/**
 * <p>
 * A harvesting action is a resource-gathering action, which includes, but is
 * not limited to, woodcutting and mining.
 * </p>
 * 
 * <p>
 * This class implements code related to all harvesting-type skills, such as
 * dealing with the action itself, looping, expiring the object (i.e. changing
 * rocks to the gray rock and trees to the stump), checking requirements and
 * giving out the harvested resources.
 * </p>
 * 
 * <p>
 * The individual woodcutting and mining classes implement things specific to
 * these individual skills such as random events.
 * </p>
 * 
 * @author Michael Bull
 *
 */
public abstract class HarvestingAction extends Action {

	/**
	 * Creates the harvesting action for the specified mob.
	 * 
	 * @param mob
	 *            The mob to create the action for.
	 */
	public HarvestingAction(Mob mob) {
		super(mob, 0);
	}

	/**
	 * Optoinally overrideable method which is called whenever an item has been
	 * harvested.
	 * 
	 * @param item
	 *            The item.
	 */
	public void onSuccessfulHarvest(final Item item) {

	}

	/**
	 * Gets the amount of cycles before the object is interacted with.
	 * 
	 * @return The amount of cycles before the object is interacted with.
	 */
	public abstract int getCycleCount();

	/**
	 * Gets the game object we are harvesting.
	 * 
	 * @return The game object we are harvesting.
	 */
	public abstract GameObject getGameObject();

	/**
	 * Gets the game object that replaces the object we harvest.
	 * 
	 * @return The game object that replaces the object we harvest.
	 */
	public abstract GameObject getReplacementObject();

	/**
	 * Gets the game objects maximum health.
	 * 
	 * @return The game objects maximum health.
	 */
	public abstract int getGameObjectMaxHealth();

	/**
	 * Gets the amount of cycles it takes for the object to respawn.
	 * 
	 * @return The amount of cycles it takes for the object to respawn.
	 */
	public abstract int getObjectRespawnTimer();

	/**
	 * Gets the reward from harvesting the object.
	 * 
	 * @return The reward from harvesting the object.
	 */
	public abstract Item getReward();

	/**
	 * Gets the skill we are using to harvest.
	 * 
	 * @return The skill we are using to harvest.
	 */
	public abstract int getSkill();

	/**
	 * Gets the required level to harvest this object.
	 * 
	 * @return The required level to harvest this object.
	 */
	public abstract int getRequiredLevel();

	/**
	 * Gets the experience granted for each item that is successfully harvested.
	 * 
	 * @return The experience granted for each item that is successfully harvested.
	 */
	public abstract double getExperience();

	/**
	 * Gets the message sent when the mob's level is too low to harvest this object.
	 * 
	 * @return The message sent when the mob's level is too low to harvest this
	 *         object.
	 */
	public abstract String getLevelTooLowMessage();

	/**
	 * Gets the message sent when the harvest successfully begins.
	 * 
	 * @return The message sent when the harvest successfully begins.
	 */
	public abstract String getHarvestStartedMessage();

	/**
	 * Gets the message sent when the mob successfully harvests from the object.
	 * 
	 * @return The message sent when the mob successfully harvests from the object.
	 */
	public abstract String getSuccessfulHarvestMessage();

	/**
	 * Gets the message sent when the mob has a full inventory.
	 * 
	 * @return The message sent when the mob has a full inventory.
	 */
	public abstract String getInventoryFullMessage();

	/**
	 * Gets the animation played whilst harvesting the object.
	 * 
	 * @return The animation played whilst harvesting the object.
	 */
	public abstract Animation getAnimation();

	/**
	 * Performs extra checks that a specific harvest event independently uses, e.g.
	 * checking for a pickaxe in mining.
	 */
	public abstract boolean canHarvest();

	/**
	 * This starts the actions animation and requirement checks, but prevents the
	 * harvest from immediately executing.
	 */
	private boolean started = false;

	/**
	 * The current cycle time.
	 */
	private int currentCycles = 0;

	/**
	 * The amount of cycles before an animation.
	 */
	private int lastAnimation = 0;

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
		final Item reward = getReward();
		if (World.getWorld().getRegionManager().getGameObject(getGameObject().getLocation(),
				getGameObject().getId()) == null) {
			this.stop();
			return;
		}
		if (reward != null && !getMob().getInventory().hasRoomFor(reward)) {
			getMob().getActionSender().removeAllInterfaces().removeInterface2();
			getMob().getActionSender().sendDialogue("", DialogueType.MESSAGE, -1, null,
					"You don't have enough inventory space for this.");
			getMob().playAnimation(Animation.create(-1));
			this.stop();
			return;
		}
		if (getMob().getSkills().getLevelForExperience(getSkill()) < getRequiredLevel()) {
			getMob().getActionSender().removeAllInterfaces().removeInterface2();
			getMob().getActionSender().sendDialogue("", DialogueType.MESSAGE, -1, null, getLevelTooLowMessage());
			getMob().playAnimation(Animation.create(-1));
			this.stop();
			return;
		}
		if (!canHarvest()) {
			this.stop();
			return;
		}
		if (!started) {
			started = true;
			getMob().playAnimation(getAnimation());
			getMob().getActionSender().sendMessage(getHarvestStartedMessage());
			if (getGameObject().getMaxHealth() == 0)
				getGameObject().setMaxHealth(getGameObjectMaxHealth());
			currentCycles = getCycleCount();
			return;
		}

		getMob().face(getGameObject().getCentreLocation());

		if (lastAnimation > 3) {
			getMob().playAnimation(getAnimation()); // keeps the emote playing
			lastAnimation = 0;
		}
		lastAnimation++;

		if (currentCycles > 0) {
			currentCycles--;
			return;
		}

		// execute
		currentCycles = getCycleCount();
		getGameObject().decreaseCurrentHealth(1); // decreases the health by one, e.g. lost a log
		if (getSuccessfulHarvestMessage() != null)
			getMob().getActionSender().sendMessage(getSuccessfulHarvestMessage());

		if (reward != null) {
			getMob().getInventory().add(reward);
			onSuccessfulHarvest(reward);
		}
		getMob().getSkills().addExperience(getSkill(), getExperience());

		if (getGameObject().getCurrentHealth() < 1) {
			getGameObject().setCurrentHealth(getGameObjectMaxHealth());

			World.getWorld().replaceObject(getGameObject(), getReplacementObject(), getObjectRespawnTimer());

			getMob().playAnimation(Animation.create(-1));
			this.stop(); // stops the action as the object is completely harvested.
			return;
		}
		if (reward != null && !getMob().getInventory().hasRoomFor(reward)) {
			getMob().getActionSender().sendDialogue("", DialogueType.MESSAGE, -1, null, getInventoryFullMessage());
			getMob().playAnimation(Animation.create(-1));
			this.stop();
			return;
		}

		if (getGameObject().getCurrentHealth() < 1) {
			this.stop();
			return;
		}
	}
}