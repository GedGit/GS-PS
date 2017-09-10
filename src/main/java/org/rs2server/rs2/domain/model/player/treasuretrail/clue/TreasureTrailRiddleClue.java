package org.rs2server.rs2.domain.model.player.treasuretrail.clue;

import org.apache.commons.lang3.text.WordUtils;
import org.rs2server.rs2.domain.model.player.treasuretrail.step.TreasureTrailClueStep;
import org.rs2server.rs2.model.boundary.Area;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * A treasure trail clue containing a riddle pointing the player to the next clue.
 *
 * The riddle will either point to an npc, an object, or a location to dig at.
 *
 * @author tommo
 */
public class TreasureTrailRiddleClue extends TreasureTrailClue {

	private int npcId;
	private int objectId;
	private Area digArea;
	private String riddle;

	/**
	 * Required for Mongojack.
	 */
	public TreasureTrailRiddleClue() {

	}

	public TreasureTrailRiddleClue(final String riddle, final int npcId, final int objectId, @Nullable final Area digArea) {
		this.riddle = riddle;
		this.npcId = npcId;
		this.objectId = objectId;
		this.digArea = digArea;
	}

	@Override
	public void onRead(@Nonnull Player player, @Nonnull ClueScrollType clueScrollType) {
		player.getActionSender().sendInterface(203, false);
		player.getActionSender().sendString(203, 2, WordUtils.wrap(riddle, 35, "<br>", true));
	}

	@Override
	public List<Class<? extends TreasureTrailClueStep>> getSteps(ClueScrollType type) {
		return Collections.emptyList();
	}

	public String getRiddle() {
		return riddle;
	}

	public void setRiddle(String riddle) {
		this.riddle = riddle;
	}

	public int getNpcId() {
		return npcId;
	}

	public void setNpcId(int npcId) {
		this.npcId = npcId;
	}

	public int getObjectId() {
		return objectId;
	}

	public void setObjectId(int objectId) {
		this.objectId = objectId;
	}

	public Area getDigArea() {
		return digArea;
	}

	public void setDigArea(Area digArea) {
		this.digArea = digArea;
	}
}
