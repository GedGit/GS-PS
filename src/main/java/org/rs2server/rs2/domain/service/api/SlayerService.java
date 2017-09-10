package org.rs2server.rs2.domain.service.api;

import org.rs2server.rs2.domain.model.player.PlayerStatisticsEntity;
import org.rs2server.rs2.model.bit.BitConfigBuilder;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.skills.slayer.SlayerTask;

import javax.annotation.Nonnull;

/**
 * Provides methods pertaining to the Slayer skill.
 *
 * @author tommo
 * @author twelve
 */
public interface SlayerService {

	/**
	 * Assigns a new slayer task to a player.
	 * @param player The player to whom the task will be assigned.
	 * @param master The slayer master assigning the task.
	 * @return The player's new task.
	 */
	@Nonnull
	SlayerTask assignTask(@Nonnull final Player player, @Nonnull final SlayerTask.Master master);

	/**
	 * Called every time a player kills a monster for their task.
	 * Rewards slayer experience, points, etc where appropriate.
	 * @param player The player
	 * @param npc The players slayer task
	 */
	void onTaskKill(@Nonnull final Player player, @Nonnull final NPC npc);

	/**
	 * Rewards the player for completing their slayer task.
	 * @param player The player.
	 * @param task The task which was completed.
	 */
	void rewardPlayer(@Nonnull final Player player, @Nonnull final SlayerTask task);

	/**
	 * Sends a check task message to the player.
	 * @param player The player.
	 */
	void sendCheckTaskMessage(@Nonnull final Player player);

	/**
	 * Opens the task interface for a player.
	 * @param player The player.
	 */
	void openRewardsScreen(@Nonnull final Player player);
	
	/**
	 * @return a {@link BitConfigBuilder} which is used to set the 5th task slot to "free" at this moment.
	 */
	BitConfigBuilder fifthSlotBuilder();

	/**
	 * @param statistics The player statistics instance.
	 * @return a {@link BitConfigBuilder} which is used to set a player's reward points on an interface.
	 */
	BitConfigBuilder rewardPointBuilder(@Nonnull final PlayerStatisticsEntity statistics);

	BitConfigBuilder blockedTaskConfig(@Nonnull final SlayerTask.TaskGroup[] blockedTasks);

	BitConfigBuilder unlockBuilder(@Nonnull final PlayerStatisticsEntity statistics);

	/**
	 * Executed when a player rubs their slayer ring.
	 * @param player The player.
	 */
	void onRubSlayerRing(@Nonnull final Player player);

	SlayerTask.TaskGroup getTaskGroup(final SlayerTask task);

	/**
	 * Checks if the player has blocked the task group.
	 * @param player The player.
	 * @param group The slayer task true.
	 * @return true if the task is blocked, false if not.
	 */
	boolean isTaskGroupBlocked(@Nonnull final Player player, @Nonnull final SlayerTask.TaskGroup group);

	@Nonnull
	SlayerTask.TaskGroup[] getBlockedTaskGroups(@Nonnull final Player player);

	/**
	 * Resets the player's slayer task.
	 * @param player The player whos task to reset.
	 * @param deductPoints Should the players reward points be deducted as a penalty.
	 */
	void cancelTask(@Nonnull final Player player, boolean deductPoints);

	/**
	 * Blocks and resets the player's current slayer task.
	 * Deducts reward points from the player.
	 * @param player The player.
	 */
	void blockTask(@Nonnull final Player player);

	/**
	 * Unblocks a blocked task for a player.
	 * @param player The player.
	 * @param index The blocked task index [0..4]
	 */
	void unblockTask(@Nonnull final Player player, int index);

}
