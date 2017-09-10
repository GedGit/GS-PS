package org.rs2server.rs2.domain.model.player.treasuretrail;

import org.rs2server.rs2.domain.model.player.treasuretrail.clue.TreasureTrailClue;
import org.rs2server.rs2.domain.model.player.treasuretrail.clue.ClueScrollType;
import org.rs2server.rs2.domain.model.player.treasuretrail.step.TreasureTrailClueStep;

import java.util.List;

/**
 * A player's treasure trail state.
 *
 * @author tommo
 */
public class PlayerTreasureTrail {

	private ClueScrollType type;

	/**
	 * The trail of clues which make up this treasure trail.
	 */
	private List<TreasureTrailClue> trail;

	/**
	 * The current clue within the trail.
	 */
	private TreasureTrailClue currentClue;

	/**
	 * The index in the clue trail at which the current clue is.
	 */
	private int currentClueIndex;

	/**
	 * A list of steps which must be completed for this step to advance to the next clue.
	 */
	private List<TreasureTrailClueStep> currentClueSteps;

	public ClueScrollType getType() {
		return type;
	}

	public void setType(ClueScrollType type) {
		this.type = type;
	}

	public List<TreasureTrailClue> getTrail() {
		return trail;
	}

	public void setTrail(List<TreasureTrailClue> trail) {
		this.trail = trail;
	}

	public TreasureTrailClue getCurrentClue() {
		return currentClue;
	}

	public void setCurrentClue(TreasureTrailClue currentClue) {
		this.currentClue = currentClue;
	}

	public List<TreasureTrailClueStep> getCurrentClueSteps() {
		return currentClueSteps;
	}

	public void setCurrentClueSteps(List<TreasureTrailClueStep> currentClueSteps) {
		this.currentClueSteps = currentClueSteps;
	}

	public int getCurrentClueIndex() {
		return currentClueIndex;
	}

	public void setCurrentClueIndex(int currentClueIndex) {
		this.currentClueIndex = currentClueIndex;
	}
}
