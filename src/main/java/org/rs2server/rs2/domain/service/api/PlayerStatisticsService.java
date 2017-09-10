package org.rs2server.rs2.domain.service.api;

import org.rs2server.rs2.domain.model.player.treasuretrail.clue.ClueScrollType;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;

/**
 * Provides helper methods for player statistics
 *
 * @author tommo
 */
public interface PlayerStatisticsService {

	/**
	 * Increases the boss kill count for the player.
	 * @param player The player whos statistics to increase.
	 * @param npcId The npc id for the slayer task.
	 * @param amount The amount to increase by.
	 */
	void increaseBossKillCount(@Nonnull final Player player, int npcId, int amount);

	/**
	 * Increases the slayer monster kill count for the player.
	 * @param player The player whos statistics to increase.
	 * @param npcId The npc id for the slayer task.
	 * @param amount The amount to increase by.
	 */
	void increaseSlayerMonsterKillCount(@Nonnull final Player player, int npcId, int amount);

	void increaseSlayerTasksCompleted(@Nonnull final Player player, int amount);

	void increaseSlayerConsecutiveTasksCompleted(@Nonnull final Player player, int amount);

	void increaseSlayerRewardPoints(@Nonnull final Player player, int amount);

	void increaseTreasureTrailCount(@Nonnull Player player, @Nonnull ClueScrollType clueScrollType, int amount);

}
