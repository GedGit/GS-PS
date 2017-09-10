package org.rs2server.rs2.content.dialogue;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.ActionSender;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * A dialogue type which includes talking to an entity.
 *
 * @author tommo
 */
@Immutable
public class TalkingDialogue extends Dialogue {

	private final ActionSender.DialogueType dialogueType;
	private final String title;
	private final int entityId;
	private final Animation.FacialAnimation facialAnimation;
	private final String dialogue;

	protected TalkingDialogue(final ActionSender.DialogueType dialogueType, final String title, final int entityId,
							final Animation.FacialAnimation facialAnimation, final String dialogue) {
		this.dialogueType = dialogueType;
		this.title = title;
		this.entityId = entityId;
		this.facialAnimation = facialAnimation;
		this.dialogue = dialogue;
	}

	@Override
	public void open(@Nonnull Player player, int index) {
		player.getActionSender().sendDialogue(title, dialogueType, entityId, facialAnimation, dialogue);
	}
}
