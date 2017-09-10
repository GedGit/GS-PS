package org.rs2server.rs2.domain.model.player.treasuretrail.clue;

import org.rs2server.rs2.domain.model.player.treasuretrail.step.TreasureTrailClueStep;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * A treasure trail clue containing an anagram of which npc to talk to.
 *
 * @author tommo
 */
public class TreasureTrailAnagramClue extends TreasureTrailClue {

	private int npcId;

	private String anagram;

	/**
	 * Required for Mongojack.
	 */
	public TreasureTrailAnagramClue() {

	}

	public TreasureTrailAnagramClue(final int npcId, final String anagram) {
		this.npcId = npcId;
		this.anagram = anagram;
	}

	@Override
	public void onRead(@Nonnull Player player, @Nonnull ClueScrollType clueScrollType) {
		player.getActionSender().sendInterface(203, false);
		player.getActionSender().sendString(203, 2, "This anagram reveals<br>who to speak to next:<br>" + anagram);
	}

	@Override
	public List<Class<? extends TreasureTrailClueStep>> getSteps(ClueScrollType type) {
		return Collections.emptyList();
	}

	public int getNpcId() {
		return npcId;
	}

	public void setNpcId(int npcId) {
		this.npcId = npcId;
	}

	public String getAnagram() {
		return anagram;
	}

	public void setAnagram(String anagram) {
		this.anagram = anagram;
	}
}
