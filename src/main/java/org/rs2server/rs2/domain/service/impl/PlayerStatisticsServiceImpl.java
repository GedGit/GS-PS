package org.rs2server.rs2.domain.service.impl;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.domain.model.player.PlayerStatisticsEntity;
import org.rs2server.rs2.domain.service.api.PlayerStatisticsService;
import org.rs2server.rs2.domain.model.player.treasuretrail.clue.ClueScrollType;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.util.Helpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * @author tommo
 */
public class PlayerStatisticsServiceImpl implements PlayerStatisticsService {

	private static final Logger logger = LoggerFactory.getLogger(PlayerStatisticsServiceImpl.class); 

	@Override
	public void increaseBossKillCount(@Nonnull Player player, int npcId, int amount) {
		final PlayerStatisticsEntity statistics = player.getDatabaseEntity().getStatistics();
		statistics.getBossKillCount().put(npcId, 
				Helpers.fallback(statistics.getBossKillCount().get(npcId), 0) + amount);

		if (Constants.DEBUG)
			logger.info("Increased boss kill count for " + player.getName() + " to: npcId=" + npcId + ", amount="
					+ statistics.getBossKillCount().get(npcId));
	}

	@Override
	public void increaseSlayerMonsterKillCount(@Nonnull Player player, int npcId, int amount) {
		final PlayerStatisticsEntity statistics = player.getDatabaseEntity().getStatistics();
		statistics.getSlayerMonsterKillCount().put(npcId,
				Helpers.fallback(statistics.getSlayerMonsterKillCount().get(npcId), 0) + amount);

		if (Constants.DEBUG)
			logger.info("Increased slayer monster kill count for " + player.getName() + " to: npcId=" + npcId
					+ ", amount=" + statistics.getSlayerMonsterKillCount().get(npcId));
	}

	@Override
	public void increaseSlayerTasksCompleted(@Nonnull Player player, int amount) {
		player.getDatabaseEntity().getStatistics()
				.setSlayerTasksCompleted(player.getDatabaseEntity().getStatistics().getSlayerTasksCompleted() + amount);
	}

	@Override
	public void increaseSlayerConsecutiveTasksCompleted(@Nonnull Player player, int amount) {
		player.getDatabaseEntity().getStatistics().setSlayerConsecutiveTasksCompleted(
				player.getDatabaseEntity().getStatistics().getSlayerConsecutiveTasksCompleted() + amount);
	}

	@Override
	public void increaseSlayerRewardPoints(@Nonnull Player player, int amount) {
		player.getDatabaseEntity().getStatistics()
				.setSlayerRewardPoints(player.getDatabaseEntity().getStatistics().getSlayerRewardPoints() + amount);
	}

	@Override
	public void increaseTreasureTrailCount(@Nonnull Player player, @Nonnull ClueScrollType clueScrollType, int amount) {
		final PlayerStatisticsEntity statistics = player.getDatabaseEntity().getStatistics();
		statistics.getTreasureTrailCount().put(clueScrollType,
				Helpers.fallback(statistics.getTreasureTrailCount().get(clueScrollType), 0) + amount);
		player.getActionSender()
				.sendMessage("<col=ff0000>You have completed " + statistics.getTreasureTrailCount().get(clueScrollType)
						+ " " + clueScrollType.name().toLowerCase() + " Treasure Trails.");
	}

}
