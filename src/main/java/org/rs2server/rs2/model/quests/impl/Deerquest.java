package org.rs2server.rs2.model.quests.impl;

import org.rs2server.rs2.model.Animation.FacialAnimation;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.quests.Quest;
import org.rs2server.rs2.net.ActionSender.DialogueType;

public class Deerquest extends Quest<DeerquestState> {
	
	
	public Deerquest(Player player, DeerquestState dqt) {
		super(player, dqt);
	}
	
	@Override
	public boolean hasRequirements() {
		return true;//player.getSkills().getLevelForExperience(Skills.ATTACK) > 50;
	}
	
	
	@Override
	public void updateProgress() {
		for(int i = 0; i < 5; i++) {
			setNextDialogueId(i, -1);
		}
		switch (state) {
			case NOT_STARTED:
				openDialogue(0);
				break;
			case ONE:
				break;			
			case TWO:
				break;
			case THREE:
				break;
		default:
			break;
			
		}
	}
	
	@Override
	public void advanceDialogue(int index) {
		int dialogueId = getNextDialogueId(index);
		if(dialogueId == -1) {
			player.getActionSender().removeChatboxInterface();
			return;
		}
		openDialogue(dialogueId);
	}
	
	/*
	sendDialogue("Archaeologist", DialogueType.NPC, 684, FacialAnimation.DEFAULT, "Hello, how can I help you?");
			setNextDialogueId(0, 1);
	
	
	*/
	
	/*
		Types, NPC, Player, Message, Option
	*/
	
	public void openDialogue(int dialogue) {
		if (dialogue == -1) {
			return;
		}
		
		switch (dialogue) {
			case 0://START DIALOGUE FOR QUEST
				sendDialogue("Klank", DialogueType.NPC, 684, FacialAnimation.DEFAULT, "YO MOTHAFUCKA GONNA SMASH YOUR NAN");
				setNextDialogueId(0, 1);
				break;
			case 1:
				sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT, "Say what mothafucka? Ill smash that bitch.");
				setNextDialogueId(0, 2);
				break;
			case 2:
				sendDialogue("", DialogueType.MESSAGE, -1, null, "A fight soon breaks out and some nans are smashed.");
				setNextDialogueId(0, 3);
				break;
			case 3:
				player.getActionSender().removeChatboxInterface();
				break;
		}
	}

	@Override
	public void showQuestInterface() {
		// TODO Auto-generated method stub
		
	}
	
	
}
