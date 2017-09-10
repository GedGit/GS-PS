package org.rs2server.rs2.domain.model.player.treasuretrail.clue;

import org.apache.commons.lang3.text.WordUtils;
import org.rs2server.rs2.domain.model.player.treasuretrail.step.TreasureTrailClueStep;
import org.rs2server.rs2.domain.model.player.treasuretrail.step.TreasureTrailDoubleAgentStep;
import org.rs2server.rs2.domain.model.player.treasuretrail.step.TreasureTrailOpenCasketStep;
import org.rs2server.rs2.domain.model.player.treasuretrail.step.TreasureTrailUriStep;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.boundary.Area;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * A treasure trail clue instructing the player to perform an emote at a certain
 * location and optionally to wear a set of items.
 *
 * @author tommo
 */
public class TreasureTrailEmoteClue extends TreasureTrailClue {

	private Animation.Emote emote;
	private Area area;
	private List<Item> requiredEquipment;
	private String hint;

	/**
	 * Required for Mongojack.
	 */
	public TreasureTrailEmoteClue() {

	}

	public TreasureTrailEmoteClue(final Area area, final Animation.Emote emote, @Nullable final List<Item> requiredEquipment, final String hint) {
		this.area = area;
		this.emote = emote;
		this.requiredEquipment = requiredEquipment;
		this.hint = hint;
	}

	@Override
	public void onRead(@Nonnull Player player, @Nonnull ClueScrollType clueScrollType) {
		player.getActionSender().sendInterface(203, false);

		final StringBuilder builder = new StringBuilder();
		builder.append(WordUtils.capitalize(emote.name().toLowerCase())).append(" ").append(hint);

		if (clueScrollType == ClueScrollType.HARD || clueScrollType == ClueScrollType.ELITE) {
			builder.append(" Beware of double agents.");
		}

		if (requiredEquipment != null && requiredEquipment.size() > 0) {
			builder.append(" Equip a ");

			for (int i = 0; i < requiredEquipment.size(); i++) {
				final Item item = requiredEquipment.get(i);

				builder.append(item.getDefinition2().getName().toLowerCase());

				if (i == requiredEquipment.size() - 2) {
					builder.append(", and a ");
				} else {
					builder.append(", ");
				}
			}
		}

		player.getActionSender().sendString(203, 2, WordUtils.wrap(builder.toString(), 35, "<br>", true));
	}

	@Nonnull
	@Override
	public List<Class<? extends TreasureTrailClueStep>> getSteps(ClueScrollType type) {
		final List<Class<? extends TreasureTrailClueStep>> steps = new ArrayList<>();
		if (type == ClueScrollType.HARD || type == ClueScrollType.ELITE) {
			steps.add(TreasureTrailDoubleAgentStep.class);
		}
		steps.add(TreasureTrailUriStep.class);
		steps.add(TreasureTrailOpenCasketStep.class);
		return steps;
	}

	public Animation.Emote getEmote() {
		return emote;
	}

	public void setEmote(Animation.Emote emote) {
		this.emote = emote;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public List<Item> getRequiredEquipment() {
		return requiredEquipment;
	}

	public void setRequiredEquipment(List<Item> requiredEquipment) {
		this.requiredEquipment = requiredEquipment;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}
}
