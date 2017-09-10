package org.rs2server.rs2.domain.model.player.treasuretrail.clue;

import org.rs2server.rs2.domain.model.player.treasuretrail.step.TreasureTrailClueStep;
import org.rs2server.rs2.domain.model.player.treasuretrail.step.TreasureTrailDigCasketStep;
import org.rs2server.rs2.domain.model.player.treasuretrail.step.TreasureTrailOpenCasketStep;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * A treasure trail map clue where the location to dig is marked with an X.
 *
 * @author tommo
 */
public class TreasureTrailMapClue extends TreasureTrailClue {

	private int locationX;

	private int locationY;

	private int locationZ;

	private int interfaceId;

	/**
	 * Required for Mongojack.
	 */
	public TreasureTrailMapClue() {

	}

	public TreasureTrailMapClue(final int locationX, final int locationY, final int locationZ, final int interfaceId) {
		this.locationX = locationX;
		this.locationY = locationY;
		this.locationZ = locationZ;
		this.interfaceId = interfaceId;
	}

	@Override
	public void onRead(@Nonnull Player player, @Nonnull ClueScrollType clueScrollType) {
		player.getActionSender().sendInterface(interfaceId, false);
	}

	@Override
	public List<Class<? extends TreasureTrailClueStep>> getSteps(ClueScrollType type) {
		final List<Class<? extends TreasureTrailClueStep>> steps = new ArrayList<>();
		steps.add(TreasureTrailDigCasketStep.class);
		steps.add(TreasureTrailOpenCasketStep.class);
		return steps;
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

	public int getInterfaceId() {
		return interfaceId;
	}

	public void setInterfaceId(int interfaceId) {
		this.interfaceId = interfaceId;
	}

}
