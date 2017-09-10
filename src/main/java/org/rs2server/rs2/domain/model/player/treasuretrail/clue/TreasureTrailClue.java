package org.rs2server.rs2.domain.model.player.treasuretrail.clue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.collect.ImmutableList;
import org.rs2server.rs2.domain.model.player.treasuretrail.step.TreasureTrailClueStep;
import org.rs2server.rs2.model.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * A single clue within a treasure trail.
 *
 * @author tommo
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include=JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class TreasureTrailClue {

	private static transient final Logger logger = LoggerFactory.getLogger(TreasureTrailClue.class);

	public TreasureTrailClue() {

	}

	/**
	 * Called when a player 'reads' the clue scroll to get a clue.
	 * @param player The player who reads the clue.
	 * @param clueScrollType The type of clue scroll. Clues may differ depending on the difficulty.
	 */
	public abstract void onRead(@Nonnull Player player, @Nonnull ClueScrollType clueScrollType);

	/**
	 * Returns a list of required steps to finish this clue.
	 * @return The list of clue steps.
	 * @param type
	 */
	@Nonnull
	@JsonIgnore
	public abstract List<Class<? extends TreasureTrailClueStep>> getSteps(ClueScrollType type);

	@JsonIgnore
	public List<TreasureTrailClueStep> getStepsAsConcreteClasses(ClueScrollType type) {
		if (getSteps(type).size() == 0) return ImmutableList.of();

		final List<TreasureTrailClueStep> concreteSteps = new ArrayList<>();
		for (final Class<? extends TreasureTrailClueStep> cStep : getSteps(type)) {
			try {
				concreteSteps.add(cStep.newInstance());
			} catch (Exception e) {
				logger.error("Exception occurred whilst instantiating concrete clue step.", e);
			}
		}
		return concreteSteps;
	}

}
