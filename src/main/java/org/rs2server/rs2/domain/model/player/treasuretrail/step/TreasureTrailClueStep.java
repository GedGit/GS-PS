package org.rs2server.rs2.domain.model.player.treasuretrail.step;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * A step within a clue which must be completed for the player to advance to the next clue.
 *
 * @author tommo
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include= JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class TreasureTrailClueStep {

	private boolean completed = false;

	public TreasureTrailClueStep() {

	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
}
