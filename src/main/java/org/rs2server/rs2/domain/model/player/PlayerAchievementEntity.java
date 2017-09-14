package org.rs2server.rs2.domain.model.player;

import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Player achievement entity.
 *
 * @author Vichy
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final @Setter @Getter class PlayerAchievementEntity {

	/**
	 * An enum containing all available player achievements.
	 * 
	 * @author Vichy.
	 *
	 */
	public enum Achievements {

		TELEPORT_HOME(0, "Teleport home", "Easy", 1, "Use the home teleport."),
		
		USE_TELEPORTER(1, "Use the Teleporter", "Easy", 1, "Speak to the global teleporter."),
		
		THIEVE_STALL(2, "Thieve from stall", "Easy", 1, "Steal from the stalls at home.");

		private final String name, difficulty, description;
		private final int points, achievementId;

		Achievements(int achievementId, String name, String difficulty, int points, String description) {
			this.achievementId = achievementId;
			this.name = name;
			this.difficulty = difficulty;
			this.points = points;
			this.description = description;
		}

		public String getName() {
			return this.name;
		}

		public String getDifficulty() {
			return this.difficulty;
		}

		public int getRewardPoints() {
			return this.points;
		}

		public String getDescription() {
			return this.description;
		}
		
		public int getAchievementId() {
			return this.achievementId;
		}

		public static Optional<Achievements> byName(String name) {
			return Arrays.stream(Achievements.values()).filter(i -> name.equalsIgnoreCase(i.toString())).findFirst();
		}

		public static Optional<Achievements> byId(int id) {
			return Arrays.stream(Achievements.values()).filter(i -> id == i.achievementId).findFirst();
		}
	}

	/**
	 * Initiating player achievements.
	 */
	private Boolean[] playerAchievements = new Boolean[1024];

	/**
	 * Unlocks player achievement based on achievement ID.
	 * 
	 * @param id
	 *            the achievement id (ordinal from enum).
	 */
	public void unlockAchievement(int id) {
		this.playerAchievements[id] = true;
	}

	/**
	 * Checks if the achievement is unlocked for our player.
	 * 
	 * @param id
	 *            the id to check for.
	 * @return if the achievement has been unlocked.
	 */
	public boolean hasAchievementUnlocked(int id) {
		return this.playerAchievements[id];
	}
}