package org.rs2server.rs2.domain.model.player.treasuretrail.clue;

import org.rs2server.rs2.domain.model.player.treasuretrail.step.TreasureTrailClueStep;
import org.rs2server.rs2.domain.model.player.treasuretrail.step.TreasureTrailDigCasketStep;
import org.rs2server.rs2.domain.model.player.treasuretrail.step.TreasureTrailOpenCasketStep;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * A treasure trail clue containing a riddle pointing the player to the next clue.
 *
 * The riddle will either point to an npc, an object, or a location to dig at.
 *
 * @author tommo
 */
public class TreasureTrailCoordinateClue extends TreasureTrailClue {

	private String coordinates;
	private int locationX;
	private int locationY;
	private int locationZ;

	/**
	 * Required for Mongojack.
	 */
	public TreasureTrailCoordinateClue() {

	}

	public TreasureTrailCoordinateClue(final String coordinates, int locationX, int locationY, int locationZ) {
		this.coordinates = coordinates;
		this.locationX = locationX;
		this.locationY = locationY;
		this.locationZ = locationZ;
	}

	@Override
	public void onRead(@Nonnull Player player, @Nonnull ClueScrollType clueScrollType) {
		player.getActionSender().sendInterface(203, false);
		player.getActionSender().sendString(203, 2, coordinates);
	}

	@Override
	public List<Class<? extends TreasureTrailClueStep>> getSteps(ClueScrollType type) {
		final List<Class<? extends TreasureTrailClueStep>> steps = new ArrayList<>();
		steps.add(TreasureTrailDigCasketStep.class);
		steps.add(TreasureTrailOpenCasketStep.class);
		return steps;
	}

	public String getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(String coordinates) {
		this.coordinates = coordinates;
	}

	public int getLocationX() {
		return locationX;
	}

	public void setLocationX(int locationX) {
		this.locationX = locationX;
	}

	public int getLocationY() {
		return locationY;
	}

	public void setLocationY(int locationY) {
		this.locationY = locationY;
	}

	public int getLocationZ() {
		return locationZ;
	}

	public void setLocationZ(int locationZ) {
		this.locationZ = locationZ;
	}
}
