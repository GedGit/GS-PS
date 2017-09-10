package org.rs2server.rs2.model.quests.impl;

import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.quests.Quest;

public class CooksAssistant extends Quest<CooksAssistantState> {

	@SuppressWarnings("unused")
	private Item MILK = new Item(1927, 1), FLOUR = new Item(1933, 1), EGG = new Item(1944, 1);
	
	private int config = 222;//29
	
	public CooksAssistant(Player player, CooksAssistantState state) {
		super(player, state);
	}

	@Override
	public boolean hasRequirements() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void advanceDialogue(int index) {
		int dialogueId = getNextDialogueId(index);
		if(dialogueId == -1) {
			player.getActionSender().removeChatboxInterface();
			return;
		}
		//openDialogue(dialogueId);
	}

	@Override
	public void updateProgress() {
		// TODO Auto-generated method stub
		switch (state) {
		case NOT_STARTED:
			
			break;
		
		case STARTED:
			player.getActionSender().sendConfig(config, 1);
			break;
			
		case COMPLETED:
			setState(CooksAssistantState.COMPLETED);
			player.getActionSender().sendConfig(config, 3);
			player.getSkills().addExperience(Skills.COOKING, 300);
			break;
		default:
			break;
		
		}
		
		
	}

    @Override
    public void showQuestInterface() {
        player.getActionSender().sendString(275, 2, "<col=800000>Cook's Assistant");
        boolean started = state != CooksAssistantState.NOT_STARTED;
        if (started) {
            switch (state) {
                case STARTED:
                    player.getActionSender().sendString(275, 4, "It's the Duke of Lumbridge's birthday and I have to help");
                    player.getActionSender().sendString(275, 5, "his Cook make him a birthday cake. To do this I need to");
                    player.getActionSender().sendString(275, 6, "bring him the following ingredients:");
                    player.getActionSender().sendString(275, 7, "I need to find a bucket of milk.");
                    player.getActionSender().sendString(275, 8, "I need to find a pot of flour.");
                    player.getActionSender().sendString(275, 9, "I need to find an egg.");
                    for (int i = 10; i <= 133; i++) {
                        player.getActionSender().sendString(275, i, "");
                    }
                    break;
               
                case COMPLETED:
                    player.getActionSender().sendString(275, 4, "<str>It was the Duke of Lumbridge's birthday, but his cook had");
                    player.getActionSender().sendString(275, 5, "<str>forgotten to buy the ingredients he needed to make him a");
                    player.getActionSender().sendString(275, 6, "<str>cake. I brought the cook an egg, some flour and some milk");
                    player.getActionSender().sendString(275, 7, "<str>and then cook made a delicious looking cake with them.");
                    player.getActionSender().sendString(275, 8, "<str>As a reward he now lets me use his high quality range");
                    player.getActionSender().sendString(275, 9, "<str>which lets me burn things less whenever I wish to cook");
                    player.getActionSender().sendString(275, 10, "<str>there.");
                    player.getActionSender().sendString(275, 11, "");
                    player.getActionSender().sendString(275, 12, "<col=ff0000>QUEST COMPLETE!");
                    player.getActionSender().sendString(275, 13, "<col=800000>Reward:");
                    player.getActionSender().sendString(275, 14, "1 Quest Point");
                    player.getActionSender().sendString(275, 15, "300 Cooking XP");
                    //player.getSkills().addExperience(Skills.COOKING, 300);
                    
                    //player.getActionSender().sendConfig(29, 2);
                    for (int i = 16; i <= 133; i++) {
                        player.getActionSender().sendString(275, i, "");
                    }
                    break;
			default:
				break;
            }
        } else {
            player.getActionSender().sendString(275, 4, "<col=08088A>I can start this quest by speaking to the <col=8A0808>Cook</col> <col=08088A>in the");
            player.getActionSender().sendString(275, 5, "<col=8A0808>Kitchen</col> <col=08088A>on the ground floor of <col=8A0808>Lumbridge Castle.<col=8A0808>");
            for (int i = 6; i <= 133; i++) {
                player.getActionSender().sendString(275, i, "");
            }
        }

        player.getActionSender().sendInterface(275, false);
    }

}
