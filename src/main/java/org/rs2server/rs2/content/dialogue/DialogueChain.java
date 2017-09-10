package org.rs2server.rs2.content.dialogue;

import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A chain of dialogues.
 *
 * @author tommo
 */
public class DialogueChain {

	private final List<Dialogue> dialogues;
	private int dialogueIndex;

	/**
	 * An optional dialogue to be opened when the dialogue has finished or has been closed.
	 */
	private Dialogue onClose;

	private DialogueChain(List<Dialogue> dialogues) {
		this.dialogues = dialogues;
	}

	public static DialogueChain build(Dialogue ... dialogues) {
		return new DialogueChain(new ArrayList<>(Arrays.asList(dialogues)));
	}

	public DialogueChain then(Dialogue dialogue) {
		dialogues.add(dialogue);
		return this;
	}

	public DialogueChain onClose(Dialogue onClose) {
		this.onClose = onClose;
		return this;
	}

	public void fireOnClose(@Nonnull Player player) {
		if (onClose != null) {
			onClose.open(player, -1);
		}
	}

	/**
	 * Called to proceed or close the dialogue.
	 * @param player The player for whom to proceed the dialogue.
	 * @param index The option index.
	 */
	public void proceed(@Nonnull Player player, int index) {
		player.setDialogueChain(this);
		if (dialogueIndex >= dialogues.size()) {
			player.getActionSender().removeChatboxInterface();
		} else {
			final Dialogue dialogue = dialogues.get(dialogueIndex);
			dialogueIndex += 1;
			dialogue.open(player, index);
		}
	}

}
