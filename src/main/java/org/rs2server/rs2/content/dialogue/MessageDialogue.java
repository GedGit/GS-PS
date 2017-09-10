package org.rs2server.rs2.content.dialogue;

import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.ActionSender;

import javax.annotation.Nonnull;

/**
 * A simple message dialogue.
 *
 * @author tommo
 */
public class MessageDialogue extends Dialogue {

	private String title;
	private ActionSender.DialogueType dialogueType;
	private int itemId;
	private String dialogue;

	public MessageDialogue(final String title, final ActionSender.DialogueType dialogueType, final int itemId, final String dialogue) {
		this.title = title;
		this.dialogueType = dialogueType;
		this.itemId = itemId;
		this.dialogue = dialogue;
	}

	@Override
	public void open(@Nonnull Player player, int index) {
		player.getActionSender().sendDialogue(title, dialogueType, itemId, null, dialogue);
	}

}
