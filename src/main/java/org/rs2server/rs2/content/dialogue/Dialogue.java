package org.rs2server.rs2.content.dialogue;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.ActionSender;
import org.rs2server.rs2.util.Helpers;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.List;

/**
 * A single dialogue state.
 * All implementing dialogue types should be IMMUTABLE.
 * @author tommo
 */
@Immutable
public abstract class Dialogue {

	public Dialogue() {

	}

	/**
	 * Called when this dialogue is opened.
	 * The implementing dialogue should be handled here.
	 * @param player The player for whom to open this dialogue.
	 * @param index The dialogue index.
	 */
	public abstract void open(@Nonnull Player player, int index);

	public static Dialogue npcSaying(final int npcId, final String title, final Animation.FacialAnimation facialAnimation, final String dialogue) {
		return new TalkingDialogue(ActionSender.DialogueType.NPC, title, npcId, facialAnimation, dialogue);
	}

	public static Dialogue playerSaying(@Nonnull Player player, final Animation.FacialAnimation facialAnimation, final String dialogue) {
		return new TalkingDialogue(ActionSender.DialogueType.PLAYER, player.getName(), -1, facialAnimation, dialogue);
	}

	public static Dialogue message(final String dialogue) {
		return new MessageDialogue("", ActionSender.DialogueType.MESSAGE, -1, dialogue);
	}

	public static Dialogue message(final String title, final String dialogue) {
		return new MessageDialogue(title, ActionSender.DialogueType.MESSAGE, -1, dialogue);
	}

	public static Dialogue messageItem(int itemId, final String dialogue) {
		return new MessageDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, itemId, dialogue);
	}

	public static Dialogue messageItem(final String title, int itemId, final String dialogue) {
		return new MessageDialogue(title, ActionSender.DialogueType.MESSAGE_MODEL_LEFT, itemId, dialogue);
	}

	public static Dialogue oneOf(@Nonnull Dialogue ... dialogues) {
		return dialogues[Helpers.randInt(dialogues.length)];
	}

	public static Dialogue oneOf(@Nonnull List<Dialogue> dialogues) {
		return dialogues.get(Helpers.randInt(dialogues.size()));
	}

}
