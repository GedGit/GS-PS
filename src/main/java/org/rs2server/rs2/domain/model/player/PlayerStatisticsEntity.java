package org.rs2server.rs2.domain.model.player;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import org.joda.time.Duration;
import org.rs2server.rs2.domain.dao.MongoEntity;
import org.rs2server.rs2.domain.model.player.treasuretrail.clue.ClueScrollType;

import java.util.HashMap;
import java.util.Map;

/**
 * Player statistics entity.
 *
 * @author tommo
 */
public final @Setter @Getter class PlayerStatisticsEntity extends MongoEntity {

	/**
	 * The overall player kill count.
	 */
	private int playerKillCount; 

	/**
	 * The overall Barrows run count.
	 */
	private int barrowsChestCount;

	/**
	 * The overall amount of slayer tasks completed.
	 */
	private int slayerTasksCompleted;

	/**
	 * The amount of consecutive slayer tasks completed, with skipping any.
	 */
	private int slayerConsecutiveTasksCompleted;

	/**
	 * The amount of slayer reward points.
	 */
	private int slayerRewardPoints;

	/**
	 * The amount of pest control points.
	 */
	private int pestControlPoints;

	/**
	 * The new raid prayers.
	 */
	public boolean augury, rigour, preserve;

	/**
	 * A map of NPC ID -> Kill count for boss monsters.
	 */
	private Map<Integer, Integer> bossKillCount = new HashMap<>();

	/**
	 * A map of NPC ID -> Kill time for boss monsters.
	 */
	@JsonProperty
	private Map<Integer, Duration> bossKillTimes = new HashMap<>();

	/**
	 * A map of NPC ID -> Kill count for slayer monsters.
	 */
	private Map<Integer, Integer> slayerMonsterKillCount = new HashMap<>();

	/**
	 * A map of ClueScrollType -> Completion count for treasure trails.
	 */
	private Map<ClueScrollType, Integer> treasureTrailCount;

	public int getKillCountForId(int id) {
		if (getBossKillCount().get(id) == null)
			return 0;
		return getBossKillCount().get(id);
	}

	public void incrementPestControlPoints(int amt) {
		setPestControlPoints(getPestControlPoints() + amt);
	}

	public int getPlayerKillCount() {
		return playerKillCount;
	}

	public void setPlayerKillCount(int playerKillCount) {
		this.playerKillCount = playerKillCount;
	}

	public int getBarrowsChestCount() {
		return barrowsChestCount;
	}

	public void setBarrowsChestCount(int barrowsChestCount) {
		this.barrowsChestCount = barrowsChestCount;
	}

	public int getSlayerTasksCompleted() {
		return slayerTasksCompleted;
	}

	public void setSlayerTasksCompleted(int slayerTasksCompleted) {
		this.slayerTasksCompleted = slayerTasksCompleted;
	}

	public int getSlayerConsecutiveTasksCompleted() {
		return slayerConsecutiveTasksCompleted;
	}

	public void setSlayerConsecutiveTasksCompleted(int slayerConsecutiveTasksCompleted) {
		this.slayerConsecutiveTasksCompleted = slayerConsecutiveTasksCompleted;
	}

	public int getSlayerRewardPoints() {
		return slayerRewardPoints;
	}

	public void setSlayerRewardPoints(int slayerRewardPoints) {
		this.slayerRewardPoints = slayerRewardPoints;
	}

	public int getPestControlPoints() {
		return pestControlPoints;
	}

	public void setPestControlPoints(int pestControlPoints) {
		this.pestControlPoints = pestControlPoints;
	}

	public Map<Integer, Integer> getBossKillCount() {
		return bossKillCount;
	}

	public void setBossKillCount(Map<Integer, Integer> bossKillCount) {
		this.bossKillCount = bossKillCount;
	}

	public Map<Integer, Integer> getSlayerMonsterKillCount() {
		return slayerMonsterKillCount;
	}

	public void setSlayerMonsterKillCount(Map<Integer, Integer> slayerMonsterKillCount) {
		this.slayerMonsterKillCount = slayerMonsterKillCount;
	}

	public Map<ClueScrollType, Integer> getTreasureTrailCount() {
		return treasureTrailCount;
	}

	public void setTreasureTrailCount(Map<ClueScrollType, Integer> treasureTrailCount) {
		this.treasureTrailCount = treasureTrailCount;
	}

	public Map<Integer, Duration> getBossKillTimes() {
		return bossKillTimes;
	}

	@SuppressWarnings("rawtypes")
	public void setBossKillTimes(HashMap hashMap) {
		// TODO Auto-generated method stub
		return;
	}
	
	public boolean hasUnlockedAugury() {
		return augury;
	}

	public void unlockAugury(boolean toggle) {
		this.augury = toggle;
	}

	public boolean hasUnlockedRigour() {
		return rigour;
	}

	public void unlockRigour(boolean toggle) {
		this.rigour = toggle;
	}

	public boolean hasUnlockedPreserve() {
		return preserve;
	}

	public void unlockPreserve(boolean toggle) {
		this.preserve = toggle;
	}
}
