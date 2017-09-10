package org.rs2server.rs2.model.quests.impl;

import org.rs2server.rs2.model.Animation.FacialAnimation;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Shop;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.container.Inventory;
import org.rs2server.rs2.model.map.Directions.NormalDirection;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.quests.Quest;
import org.rs2server.rs2.net.ActionSender.DialogueType;
import org.rs2server.rs2.tickable.Tickable;

/**
 * @author Stank1337
 */

public class LunarDiplomacy extends Quest<LunarStates> {

	public LunarDiplomacy(Player player, LunarStates state) {
		super(player, state);
	}

	private Item scroll = new Item(608, 1);

	@Override
	public boolean hasRequirements() {
		return player.getSkills().getLevelForExperience(Skills.FLETCHING) > 59
				&& player.getSkills().getLevelForExperience(Skills.RANGE) > 59
				&& player.getSkills().getLevelForExperience(Skills.DEFENCE) > 39
				&& player.getSkills().getLevelForExperience(Skills.ATTACK) > 59
				&& player.getSkills().getLevelForExperience(Skills.STRENGTH) > 59
				&& player.getSkills().getLevelForExperience(Skills.MAGIC) > 65;
	}

	@Override
	public void advanceDialogue(int index) {
		int dialogueId = getNextDialogueId(index);
		if (dialogueId == -1) {
			player.getActionSender().removeChatboxInterface();
			return;
		}
		openDialogue(dialogueId);
	}

	public void openDialogue(int dialogue) {
		if (dialogue == -1) {
			player.getActionSender().removeChatboxInterface();
			return;
		}
		switch (dialogue) {
		case 0:
			sendDialogue("Lokar", DialogueType.NPC, 3855, FacialAnimation.DEFAULT,
					"You there! Please, I beg of your assistance!!");
			setState(LunarStates.COMPLETED);
			player.removeAttribute("questnpc");
			player.getActionSender().sendMessage(
					"Congratulations you've finished Lunar Isle, You can now speak to Lokar to Teleport to Lunar Isle and purchase items.");
			player.getActionSender().removeChatboxInterface();
			player.getActionSender().sendConfig(29, 2);
			player.getActionSender().sendConfig(101, 5);
			setNextDialogueId(0, 1);
			break;
		case 1:
			sendDialogue("", DialogueType.OPTION, -1, null, "Slow down! What do you need?|Sorry. I'm not interested.");
			setNextDialogueId(0, 2);
			setNextDialogueId(1, 3);
			break;
		case 2:
			sendDialogue("Lokar", DialogueType.NPC, 3855, FacialAnimation.DEFAULT,
					"Thank Saradomin! Off the shores of Lunar Isle dwells a terrifying sea snake. Can you slay such a beast");
			setNextDialogueId(0, 4);
			break;
		case 3:
			player.getActionSender().removeChatboxInterface();
			break;
		case 4:
			sendDialogue("", DialogueType.OPTION, -1, null,
					"I am always up for a challenge!|I don't think I'm ready for that!");
			setNextDialogueId(0, 18);
			setNextDialogueId(1, 3);
			break;
		case 5:
			setState(LunarStates.STARTED);
			player.getActionSender().sendConfig(29, 1);
			sendDialogue("Lokar", DialogueType.NPC, 3855, FacialAnimation.DEFAULT,
					"Adventurer, this is no normal snake. We must have a plan to defeat it.");
			setNextDialogueId(0, 6);
			break;
		case 6:
			sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"It sounds awfully strong. Do you know what we need to bring it down?");
			setNextDialogueId(0, 19);
			break;
		case 7:
			sendDialogue("Lokar", DialogueType.NPC, 3855, FacialAnimation.DEFAULT,
					"I have been tracking and studying the snake over the past few days. Take this scroll of information that I have gathered. Make sure she gets it.");
			setNextDialogueId(0, 8);
			break;
		case 8:
			Inventory.addDroppable(player, scroll);
			sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Okay. Where can I find this mage?");
			setNextDialogueId(0, 9);
			break;
		case 9:
			sendDialogue("Lokar", DialogueType.NPC, 3855, FacialAnimation.DEFAULT,
					"I suggest searching on the Lunar Isle. Would you like a teleport?");
			setNextDialogueId(0, 10);
			break;
		case 10:
			sendDialogue("", DialogueType.OPTION, -1, null, "Yes, please. I am ready.|No thank you, let me prepare.");
			setNextDialogueId(0, 11);
			setNextDialogueId(1, 3);
			break;
		case 11:
			sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Yes, please. I am ready.");
			setNextDialogueId(0, 12);
			break;
		case 12:
			setState(LunarStates.FIND_METEORA);
			player.getActionSender().removeChatboxInterface();
			player.setTeleportTarget(Location.create(2118, 3895, 0));
			break;
		case 13:
			sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Are you the mage that can help me?");
			setNextDialogueId(0, 14);
			break;
		case 14:
			sendDialogue("Meteora", DialogueType.NPC, 3839, FacialAnimation.DEFAULT,
					"Who sojourns the lair of Meteora?");
			setNextDialogueId(0, 15);
			break;
		case 15:
			sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Relax! There is a giant Sea Snake terrorising the town. Is there any way you can help?");
			setNextDialogueId(0, 16);
			break;
		case 16:
			sendDialogue("Meteora", DialogueType.NPC, 3839, FacialAnimation.DEFAULT,
					"I will need more information about this Snake.");
			setNextDialogueId(0, 17);
			break;
		case 17:
			if (player.getInventory().hasItem(scroll)) {
				player.getInventory().remove(scroll);
				sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
						"This is what the old man Lokar has learned so far about the snake.");
				setNextDialogueId(0, 20);
			} else {
				sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
						"I seem to have lost the scroll, I'll come back when I have it.");
				setNextDialogueId(0, 3);
			}
			break;
		case 18:// LOKAR
			sendDialogue("Lokar", DialogueType.NPC, 3855, FacialAnimation.DEFAULT, "Blessings be upon you!");
			setNextDialogueId(0, 5);
			break;
		case 19:
			sendDialogue("Lokar", DialogueType.NPC, 3855, FacialAnimation.DEFAULT,
					"I don�t know for sure, but legend has it there is a mage named Meteora who is a master of magic. Maybe she could help you.");
			setNextDialogueId(0, 7);
			break;
		case 20:
			sendDialogue("Meteora", DialogueType.NPC, 3839, FacialAnimation.DEFAULT,
					"Oh my. This is serious. The snake, if we should even call it that, is not a normal one. It was summoned by a Zamorakian mage. We can only kill it using something special.");
			setNextDialogueId(0, 21);
			break;
		case 21:
			sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"What do you mean something special?");
			setNextDialogueId(0, 22);
			break;
		case 22:
			sendDialogue("Meteora", DialogueType.NPC, 3839, FacialAnimation.DEFAULT,
					"Tell me, Adventurer. Are you skilled with the art of fletching and magic?");
			setNextDialogueId(0, 23);
			break;
		case 23:
			sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Yes, I believe I am skilled enough.");
			setNextDialogueId(0, 24);
			break;
		case 24:
			sendDialogue("Meteora", DialogueType.NPC, 3839, FacialAnimation.DEFAULT,
					"There are special bolts that you must fletch and bless. Only the power of Saradomin can smite such a beast.");
			setNextDialogueId(0, 25);
			break;
		case 25:
			sendDialogue("Meteora", DialogueType.NPC, 3839, FacialAnimation.DEFAULT,
					"Use your magic skills to enchant at least ten bolts.");
			setNextDialogueId(0, 26);
			break;
		case 26:
			setState(LunarStates.MAKE_BOLTS);
			sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT, "Okay, I'll try.");
			setNextDialogueId(0, 27);
			break;
		case 27:
			player.getActionSender().removeChatboxInterface();
			break;
		case 28:
			sendDialogue("Meteora", DialogueType.NPC, 3839, FacialAnimation.DEFAULT,
					"How's the progress with the magical bolts?");
			setNextDialogueId(0, 29);
			break;
		case 29:
			sendDialogue("", DialogueType.OPTION, -1, null,
					"I think I have enough! I'm ready!|Not yet, I think I need more.");
			setNextDialogueId(0, 30);
			setNextDialogueId(1, 27);
			break;
		case 30:
			sendDialogue("Meteora", DialogueType.NPC, 3839, FacialAnimation.DEFAULT,
					"How's the progress with the magical bolts?");
			setNextDialogueId(0, 31);
			break;
		case 31:
			if (hasEnchantedBolts()) {
				setState(LunarStates.SNAKE);
				sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT, "I have the bolts!");
				setNextDialogueId(0, 32);
			} else {
				sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
						"I don't have the bolts yet.");
				setNextDialogueId(0, 27);
			}
			break;
		case 32:
			sendDialogue("Meteora", DialogueType.NPC, 3839, FacialAnimation.DEFAULT,
					"Perfect, now all you have to do is kill the sea snake!");
			setNextDialogueId(0, 33);
			break;
		case 33:
			sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Great! Where can I find the Sea Snake?");
			setNextDialogueId(0, 34);
			break;
		case 34:
			sendDialogue("Meteora", DialogueType.NPC, 3839, FacialAnimation.DEFAULT,
					"Good question. I have no idea! I suggest asking around, someone has to know.");
			setNextDialogueId(0, 35);
			break;
		case 35:
			sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT, "Thanks for your help!");
			setNextDialogueId(0, 36);
			break;
		case 36:
			player.getActionSender().removeChatboxInterface();
			break;
		case 37:
			sendDialogue("Lokar", DialogueType.NPC, 3855, FacialAnimation.DEFAULT, "Hello there again traveler!");
			setNextDialogueId(0, 38);
			break;
		case 38:
			sendDialogue("", DialogueType.OPTION, -1, null,
					"Do you know where the Sea Snake is?|Can you take me to Lunar Isle");
			setNextDialogueId(0, 39);
			setNextDialogueId(1, 204);
			break;
		case 39:
			sendDialogue("Lokar", DialogueType.NPC, 3855, FacialAnimation.DEFAULT,
					"I happen to know how to get you there! Would you like to go?");
			setNextDialogueId(0, 40);
			break;
		case 40:
			sendDialogue("", DialogueType.OPTION, -1, null, "Yes, I'm ready!|Wait, I forgot something.");
			setNextDialogueId(0, 41);
			setNextDialogueId(1, 42);
			break;
		case 41:
			sendDialogue("Lokar", DialogueType.NPC, 3855, FacialAnimation.DEFAULT, "To Lunar Isle it is!");
			setNextDialogueId(0, 43);
			break;
		case 42:
			player.getActionSender().removeChatboxInterface();
			break;
		case 43:
			player.getActionSender().removeChatboxInterface();
			player.setTeleportTarget(Location.create(2108, 3953, 0));
			World.getWorld().submit(new Tickable(3) {

				@Override
				public void execute() {
					this.stop();
					NPC n = new NPC(1101, Location.create(2107, 3957), Location.create(2107, 3957),
							Location.create(2107, 3957), NormalDirection.SOUTH.intValue());
					World.getWorld().register(n);
					n.setInstancedPlayer(player);
					player.setAttribute("ownedNPC", n);
					n.getCombatState().startAttacking(player, player.isAutoRetaliating());
				}

			});
			break;
		case 44:
			sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"I've done it! I've killed the Snake!");
			setNextDialogueId(0, 45);
			break;
		case 45:
			sendDialogue("Lokar", DialogueType.NPC, 3855, FacialAnimation.DEFAULT,
					"Well Done! You've saved the City of Lunar Isle.");
			setNextDialogueId(0, 46);
			break;
		case 46:
			sendDialogue("Lokar", DialogueType.NPC, 3855, FacialAnimation.DEFAULT,
					"As a reward, You can speak to me to purchase items from me and Teleport to Lunar Isle to use Lunar Magics.");
			setNextDialogueId(0, 47);
			break;
		case 47:
			setState(LunarStates.COMPLETED);
			player.removeAttribute("questnpc");
			player.getActionSender().sendMessage(
					"Congratulations you've finished Lunar Isle, You can now speak to Lokar to Teleport to Lunar Isle and purchase items.");
			player.getActionSender().removeChatboxInterface();
			player.getActionSender().sendConfig(29, 2);
			break;

		case 200:
			sendDialogue("Lokar", DialogueType.NPC, 3855, FacialAnimation.DEFAULT, "Hello there again traveler!");
			setNextDialogueId(0, 201);
			break;
		case 201:
			sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.ANNOYED,
					"No time to talk! I�ve got a sea snake to slay");
			setNextDialogueId(0, 202);
			break;
		case 202:
			sendDialogue("Lokar", DialogueType.NPC, 3855, FacialAnimation.DEFAULT,
					"So would you like me to take you back to Lunar Isle again?");
			setNextDialogueId(0, 203);
			break;
		case 203:
			sendDialogue("", DialogueType.OPTION, -1, null, "Yes, let�s go.|Wait! I forgot something!");
			setNextDialogueId(0, 204);
			setNextDialogueId(1, 3);
			break;
		case 204:
			player.getActionSender().removeChatboxInterface();
			player.setTeleportTarget(Location.create(2118, 3895, 0));
			break;
		case 1000:
			sendDialogue("Lokar", DialogueType.NPC, 3855, FacialAnimation.DEFAULT, "Hello Traveler!");
			setNextDialogueId(0, 1001);
			break;
		case 1001:
			player.getActionSender().removeChatboxInterface();
			break;

		case 2000:
			sendDialogue("Lokar", DialogueType.NPC, 3855, FacialAnimation.DEFAULT, "Hey there, " + player.getName());
			setNextDialogueId(0, 2001);
			break;
		case 2001:
			sendDialogue("", DialogueType.OPTION, -1, null, "Can I see your shop?|Can you take me to Lunar Isle?");
			setNextDialogueId(0, 2002);
			setNextDialogueId(1, 204);
			break;
		case 2002:
			player.getActionSender().removeChatboxInterface();
			Shop.open(player, 32, 2);
			break;
		}
	}

	@Override
	public void updateProgress() {
		for (int i = 0; i < 5; i++) {
			setNextDialogueId(i, -1);
		}
		switch (state) {
		case NOT_STARTED:
			if (!hasRequirements()) {
				openDialogue(1000);
			} else {
				openDialogue(0);
			}
			break;
		case STARTED:
			if (player.getAttribute("talkingNpc") != null) {
				switch ((int) player.getAttribute("talkingNpc")) {
				case 3855:
					openDialogue(7);
					break;
				}
			}
			break;
		case FIND_METEORA:
			if (player.getAttribute("talkingNpc") != null) {
				switch ((int) player.getAttribute("talkingNpc")) {
				case 3855:
					openDialogue(200);
					break;
				case 3839:
					openDialogue(13);
					break;
				}
			}
			break;
		case MAKE_BOLTS:
			if (player.getAttribute("talkingNpc") != null) {
				switch ((int) player.getAttribute("talkingNpc")) {
				case 3855:
					openDialogue(200);
					break;
				case 3839:
					openDialogue(28);
					break;
				}
			}
			break;
		case SNAKE:
			if (player.getAttribute("talkingNpc") != null) {
				switch ((int) player.getAttribute("talkingNpc")) {
				case 3855:
					openDialogue(37);
					break;
				}
			}
			break;
		case KILLED_SNAKE:
			if (player.getAttribute("talkingNpc") != null) {
				switch ((int) player.getAttribute("talkingNpc")) {
				case 3855:
					openDialogue(44);
					break;
				}
			}
			break;
		case COMPLETED:
			if (player.getAttribute("talkingNpc") != null) {
				switch ((int) player.getAttribute("talkingNpc")) {
				case 3855:
					openDialogue(2000);
					break;
				}
			}
			break;
		}
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void showQuestInterface() {
		player.getActionSender().sendString(275, 2, "<col=800000>Lunar Diplomacy");
		boolean started = state != LunarStates.NOT_STARTED;
		if (started) {
			switch (state) {
			case STARTED:
				player.getActionSender().sendString(275, 4, "Lokar has told me he needs help saving Lunar Isle.");
				for (int i = 5; i <= 133; i++) {
					player.getActionSender().sendString(275, i, "");
				}
				break;
			case FIND_METEORA:
				player.getActionSender().sendString(275, 4, "<str>Lokar has told me he needs help saving Lunar Isle.");
				player.getActionSender().sendString(275, 5, "Lokar has told me to travel to");
				player.getActionSender().sendString(275, 6, "Lunar-Isle and find a Mage named Meteora.");
				for (int i = 7; i <= 133; i++) {
					player.getActionSender().sendString(275, i, "");
				}
				break;
			case MAKE_BOLTS:
				player.getActionSender().sendString(275, 4, "<str>Lokar has told me he needs help saving Lunar Isle.");
				player.getActionSender().sendString(275, 5, "<str>Lokar has told me to travel to");
				player.getActionSender().sendString(275, 6, "<str>Lunar-Isle and find a Mage named Meteora.");
				player.getActionSender().sendString(275, 7, "Meteora has told me I need to use");
				player.getActionSender().sendString(275, 8, "Enchanted bolts to kill the Giant Sea Snake.");
				for (int i = 9; i <= 133; i++) {
					player.getActionSender().sendString(275, i, "");
				}
				break;
			case SNAKE:
				player.getActionSender().sendString(275, 4, "<str>Lokar has told me he needs help saving Lunar Isle.");
				player.getActionSender().sendString(275, 5, "<str>Lokar has told me to travel to");
				player.getActionSender().sendString(275, 6, "<str>Lunar-Isle and find a Mage named Meteora.");
				player.getActionSender().sendString(275, 7, "<str>Meteora has told me I need to use");
				player.getActionSender().sendString(275, 8, "<str>Enchanted bolts to kill the Giant Sea Snake.");
				player.getActionSender().sendString(275, 9, "I have the requirements to kill the Snake.");
				player.getActionSender().sendString(275, 10,
						"Now I just need to find him...Maybe i should speak with Lokar.");
				for (int i = 11; i <= 133; i++) {
					player.getActionSender().sendString(275, i, "");
				}
				break;
			case KILLED_SNAKE:
				player.getActionSender().sendString(275, 4, "<str>Lokar has told me he needs help saving Lunar Isle.");
				player.getActionSender().sendString(275, 5, "<str>Lokar has told me to travel to");
				player.getActionSender().sendString(275, 6, "<str>Lunar-Isle and find a Mage named Meteora.");
				player.getActionSender().sendString(275, 7, "<str>Meteora has told me I need to use");
				player.getActionSender().sendString(275, 8, "<str>Enchanted bolts to kill the Giant Sea Snake.");
				player.getActionSender().sendString(275, 9, "<str>I have the requirements to kill the Snake.");
				player.getActionSender().sendString(275, 10,
						"<str>Now I just need to find him...Maybe i should speak with Lokar.");
				player.getActionSender().sendString(275, 11, "I've killed the snake I should go");
				player.getActionSender().sendString(275, 12, "Speak to Lokar.");
				for (int i = 13; i <= 133; i++) {
					player.getActionSender().sendString(275, i, "");
				}
				break;
			case COMPLETED:
				player.getActionSender().sendString(275, 4, "<str>Lokar has told me he needs help saving Lunar Isle.");
				player.getActionSender().sendString(275, 5, "<str>Lokar has told me to travel to");
				player.getActionSender().sendString(275, 6, "<str>Lunar-Isle and find a Mage named Meteora.");
				player.getActionSender().sendString(275, 7, "<str>Meteora has told me I need to use");
				player.getActionSender().sendString(275, 8, "<str>Enchanted bolts to kill the Giant Sea Snake.");
				player.getActionSender().sendString(275, 9, "<str>I have the requirements to kill the Snake.");
				player.getActionSender().sendString(275, 10,
						"<str>Now I just need to find him...Maybe i should speak with Lokar.");
				player.getActionSender().sendString(275, 11, "<str>I've killed the snake I should go");
				player.getActionSender().sendString(275, 12, "<str>Speak to Lokar.");
				player.getActionSender().sendString(275, 13, "");
				player.getActionSender().sendString(275, 14, "<col=ff0000>QUEST COMPLETE!");
				player.getActionSender().sendString(275, 15, "<col=800000>Reward:");
				player.getActionSender().sendString(275, 16, "<col=000080>Access to Lunar magic spellbook");
				player.getActionSender().sendString(275, 17, "<col=000080>Access to Lokar's Shop");
				for (int i = 18; i <= 133; i++)
					player.getActionSender().sendString(275, i, "");
				break;
			}
		} else {
			player.getActionSender().sendString(275, 4, "Speak to Lokar in Port Sarim to start");
			player.getActionSender().sendString(275, 5, "<col=800000>Requirements:");
			player.getActionSender().sendString(275, 6, "<col=000080>60 Attack");
			player.getActionSender().sendString(275, 7, "<col=000080>60 Strength");
			player.getActionSender().sendString(275, 8, "<col=000080>40 Defence");
			player.getActionSender().sendString(275, 9, "<col=000080>60 Range");
			player.getActionSender().sendString(275, 10, "<col=000080>66 Magic");
			player.getActionSender().sendString(275, 11, "<col=000080>60 Fletching");
			for (int i = 12; i <= 133; i++)
				player.getActionSender().sendString(275, i, "");
		}

		player.getActionSender().sendInterface(275, false);
	}

	public boolean hasEnchantedBolts() {
		for (int i = 9236; i <= 9245; i++) {
			if (player.getInventory().contains(i))
				return true;
			if (player.getEquipment().get(Equipment.SLOT_ARROWS) != null
					&& player.getEquipment().get(Equipment.SLOT_ARROWS).getId() == i)
				return true;
		}
		return false;
	}
}