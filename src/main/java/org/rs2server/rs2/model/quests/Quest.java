package org.rs2server.rs2.model.quests;

import org.rs2server.rs2.model.Animation.FacialAnimation;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.ActionSender;
import org.rs2server.rs2.net.ActionSender.DialogueType;

public abstract class Quest<T extends Enum<?>> {

	protected T state;
	protected Player player;
	protected int[] nextDialogueId = new int[] { -1, -1, -1, -1, -1 };
	
	public Quest(Player player, T state) {
		this.player = player;
		this.state = state;
	}
	
	public final ActionSender sendDialogue(String title, DialogueType dialogueType, int entityId, FacialAnimation animation, String... text) {
		return player.getActionSender().sendDialogue(title, dialogueType, entityId, animation, text);
	}
	
	/**
	 * @return the nextDialogueId
	 */
	public int getNextDialogueId(int index) {
		return nextDialogueId[index];
	}

	/**
	 * @param nextDialogueId the nextDialogueId to set
	 */
	public void setNextDialogueId(int index, int nextDialogueId) {
		this.nextDialogueId[index] = nextDialogueId;
	}
	
	public T getState() {
		return state;
	}
	
	public void setState(T state) {
		this.state = state;
	}
	
	public abstract boolean hasRequirements();
	
	public abstract void advanceDialogue(int index);
	
	public abstract void updateProgress();
	
	public abstract void showQuestInterface();
	
}
