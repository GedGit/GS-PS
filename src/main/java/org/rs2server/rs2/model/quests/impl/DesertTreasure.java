package org.rs2server.rs2.model.quests.impl;

import org.rs2server.rs2.model.Animation.FacialAnimation;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Shop;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction.SpellBook;
import org.rs2server.rs2.model.map.Directions.NormalDirection;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.quests.Quest;
import org.rs2server.rs2.net.ActionSender.DialogueType;
import org.rs2server.rs2.tickable.Tickable;

/*
 * Author Stank1337
 */
public class DesertTreasure extends Quest<DTStates> {

	private Item BLOOD_DIAMOND = new Item(4670, 1);
	private Item ICE_DIAMOND = new Item(4671, 1);
	private Item SMOKE_DIAMOND = new Item(4672, 1);
	private Item SHADOW_DIAMOND = new Item(4673, 1);

	@SuppressWarnings("unused")
	private int config = 130;

	public DesertTreasure(Player player, DTStates state) {
		super(player, state);
	}

	@Override
	public boolean hasRequirements() {
		return player.getSkills().getLevelForExperience(Skills.MAGIC) > 49;
	}

	private int CLOSE = 1337;

	@SuppressWarnings("incomplete-switch")
	@Override
	public void updateProgress() {
		for (int i = 0; i < 5; i++)
			setNextDialogueId(i, -1);
		switch (state) {
		case NOT_STARTED:
			openDialogue(0);
			break;
		case ONE:
			if (player.getAttribute("talkingNpc") != null) {
				switch ((int) player.getAttribute("talkingNpc")) {
				case 684:
					openDialogue(7);
					break;
				case 1902:
					openDialogue(8);
					break;
				}
			}
			break;
		case TWO:
			if (player.getAttribute("talkingNpc") != null) {
				switch ((int) player.getAttribute("talkingNpc")) {
				case 684:
					openDialogue(15);
					break;
				case 1902:
					openDialogue(14);
					break;
				}
			}
			break;
		case THREE:
			if (player.getAttribute("talkingNpc") != null) {
				switch ((int) player.getAttribute("talkingNpc")) {
				case 684:
					openDialogue(32);
					break;
				}
			}
			break;
		case FOUR:
			if (player.getAttribute("talkingNpc") != null) {
				switch ((int) player.getAttribute("talkingNpc")) {
				case 684:
					openDialogue(36);
					break;
				}
			}
			break;
		case FIVE:
			if (player.getAttribute("talkingNpc") != null) {
				switch ((int) player.getAttribute("talkingNpc")) {
				case 684:
					openDialogue(39);
					break;
				}
			}
			break;
		case SIX:
			if (player.getAttribute("talkingNpc") != null) {
				switch ((int) player.getAttribute("talkingNpc")) {
				case 684:
					openDialogue(42);
					break;
				}
			}
			break;
		case SEVEN:
			if (player.getAttribute("talkingNpc") != null) {
				switch ((int) player.getAttribute("talkingNpc")) {
				case 684:
					openDialogue(43);
					break;
				}
			}
			break;
		case COMPLETED:
			if (player.getAttribute("talkingNpc") != null) {
				switch ((int) player.getAttribute("talkingNpc")) {
				case 684:
					openDialogue(46);
					break;
				}
			}
			break;
		}
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
			return;
		}
		switch (dialogue) {
		/**
		 * State = NOT_STARTED
		 */
		case 0:// not enough
			sendDialogue("Archaeologist", DialogueType.NPC, 684, FacialAnimation.DEFAULT, "Hello, how can I help you?");
			setNextDialogueId(0, 1);
			break;
		case 1:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Who are you and why are you here?");
			setNextDialogueId(0, 2);
			break;
		case 2:
			sendDialogue("Archaeologist", DialogueType.NPC, 684, FacialAnimation.DEFAULT,
					"I've been doing research in the desert on a new form of Magical Spells,"
					+ "<br>I've learned that they are located somewhere in the desert.");
			setNextDialogueId(0, 3);
			break;
		case 3:
			sendDialogue("Archaeologist", DialogueType.NPC, 684, FacialAnimation.DEFAULT,
					"Would you like to help me uncover the secrets of this new magic form?");
			setNextDialogueId(0, 4);
			break;
		case 4:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes i'd love to help you|No thanks.");
			setNextDialogueId(0, 5);
			setNextDialogueId(1, 6);
			break;
		case 5:
			if (!hasRequirements()) {
				sendDialogue("", DialogueType.MESSAGE, -1, null,
						"You don't have the requirements to start this quest.");
				setNextDialogueId(0, CLOSE);
			}
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Yes i'd love to help you.");
			setNextDialogueId(0, 7);
			setState(DTStates.ONE);
			player.getActionSender().sendConfig(130, 1);
			break;
		/**
		 * State = ONE
		 */
		case 6:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"No thanks.");
			setNextDialogueId(0, CLOSE);
			break;
		case 7:
			sendDialogue("Archaeologist", DialogueType.NPC, 684, FacialAnimation.DEFAULT,
					"Great to begin, We need to do more research on these great magics, "
					+ "go to the varrock museum and speak to Historian Minas.");
			setNextDialogueId(0, CLOSE);
			player.removeAttribute("questnpc");
			break;
		case 8:
			sendDialogue("Historian Minas", DialogueType.NPC, 1902, FacialAnimation.DEFAULT, "How can I help you?");
			setNextDialogueId(0, 9);
			break;
		case 9:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"The Archaeologist from Al-Kharid sent me to speak to you about some form of new magics.");
			setNextDialogueId(0, 10);
			break;
		case 10:
			sendDialogue("Historian Minas", DialogueType.NPC, 1902, FacialAnimation.DEFAULT,
					"Ahh right i've been expecting you. I've prepared a book for you to give to the archaeologist.");
			setNextDialogueId(0, 11);
			break;
		case 11:
			if (player.getInventory().add(new Item(7633, 1))) {
				sendDialogue("Historian Minas", DialogueType.NPC, 1902, FacialAnimation.DEFAULT,
						"I've just given you the book, Take it back to the archaeologist as soon as possible!");
				setState(DTStates.TWO);
				setNextDialogueId(0, 13);
			} else {
				player.getActionSender().removeChatboxInterface();
				player.getActionSender().sendMessage("Not enough inventory space to add item.");
			}
			break;
		case 13:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Okay, Thanks!");
			setNextDialogueId(0, -1);
			break;
		/**
		 * State = TWO
		 */
		case 14:
			sendDialogue("Historian Minas", DialogueType.NPC, 1902, FacialAnimation.DEFAULT,
					"Take the book i've given you to the archaeologist.");
			setNextDialogueId(0, -1);
			player.removeAttribute("questnpc");
			break;
		case 15:
			sendDialogue("Archaeologist", DialogueType.NPC, 684, FacialAnimation.DEFAULT,
					"Hello, What news do you return with?");
			setNextDialogueId(0, 16);
			break;
		case 16:
			boolean hasBook = player.getInventory().hasItem(new Item(7633, 1));
			int next = hasBook ? 17 : 18;
			if (hasBook) {
				player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1,
						FacialAnimation.DEFAULT,
						"Historian Minas has told me to report back to you with this ancient book.");
			} else {
				player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1,
						FacialAnimation.DEFAULT,
						"Historian Minas has told me to report back to you with an ancient book, But i seem to have misplaced it.");
			}
			setNextDialogueId(0, next);
			break;
		case 17:
			sendDialogue("Archaeologist", DialogueType.NPC, 684, FacialAnimation.DEFAULT,
					"Wonderful now let me see the book.");
			setNextDialogueId(0, 19);
			break;
		case 18:
			sendDialogue("Archaeologist", DialogueType.NPC, 684, FacialAnimation.DEFAULT,
					"I recommend you go find it.");
			setNextDialogueId(0, -1);
			player.removeAttribute("questnpc");
			break;
		case 19:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Sure");
			setNextDialogueId(0, 20);
			break;
		case 20:
			player.getInventory().remove(new Item(7633, 1));
			sendDialogue("Archaeologist", DialogueType.NPC, 684, FacialAnimation.DEFAULT,
					"Wow, this is quite amazing...");
			setNextDialogueId(0, 21);
			break;
		case 21:
			sendDialogue("Archaeologist", DialogueType.NPC, 684, FacialAnimation.DEFAULT,
					"It seems that their are 5 warlords that defend these magics from the unworthy.");
			setNextDialogueId(0, 22);
			break;
		case 22:
			sendDialogue("Archaeologist", DialogueType.NPC, 684, FacialAnimation.DEFAULT,
					"To unlock the magics you must defeat the 5 warlords and claim each of their respective diamonds.");
			setNextDialogueId(0, 23);
			break;
		case 23:
			sendDialogue("Archaeologist", DialogueType.NPC, 684, FacialAnimation.DEFAULT,
					"Do you think you're up for the task?");
			setNextDialogueId(0, 24);
			break;
		case 24:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Absolutely!|I don't think so.");
			setNextDialogueId(0, 25);
			setNextDialogueId(1, 26);
			break;
		case 25:
			sendDialogue("Archaeologist", DialogueType.NPC, 684, FacialAnimation.DEFAULT,
					"Awesome! Come back to me when you're ready to take on the task and I can take you to the warlords.");
			setState(DTStates.THREE);
			setNextDialogueId(0, 26);
			player.removeAttribute("questnpc");
			break;
		case 26:
			setNextDialogueId(0, CLOSE);
			player.removeAttribute("questnpc");
			break;

		/**
		 * State = Three
		 */
		case 27:
			sendDialogue("Archaeologist", DialogueType.NPC, 684, FacialAnimation.DEFAULT,
					"Would you like to start taking on the warlords?");
			setState(DTStates.THREE);
			setNextDialogueId(0, 28);
			break;
		case 28:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes|No.");
			setNextDialogueId(0, 29);
			setNextDialogueId(1, 30);
			break;
		case 29:
			sendDialogue("Archaeologist", DialogueType.NPC, 684, FacialAnimation.DEFAULT,
					"Alright, I will take you to the first warlord you need to defeat.");
			setNextDialogueId(0, 31);
			break;
		case 30:
			setNextDialogueId(0, -1);
			player.removeAttribute("questnpc");
			break;
		case 31:
			player.getActionSender().removeChatboxInterface();
			player.setTeleportTarget(Location.create(3570, 3409, 0));
			player.getActionSender().sendMessage("Dessous will spawn soon...");
			World.getWorld().submit(new Tickable(3) {

				@Override
				public void execute() {
					this.stop();
					NPC n = new NPC(3459, Location.create(3570, 3406), Location.create(3570, 3406),
							Location.create(3570, 3406), NormalDirection.NORTH_EAST.intValue());
					World.getWorld().register(n);
					n.setInstancedPlayer(player);
					player.setAttribute("ownedNPC", n);
					n.getCombatState().startAttacking(player, player.isAutoRetaliating());
				}

			});
			break;
		/**
		 * State = Four
		 */
		case 32:
			if (player.getInventory().hasItem(BLOOD_DIAMOND)) {
				player.getInventory().remove(BLOOD_DIAMOND);
				sendDialogue("Archaeologist", DialogueType.NPC, 684, FacialAnimation.DEFAULT,
						"Great job on defeating Dessous, Would you like me to take you to the next warlord?");
				setNextDialogueId(0, 33);
				setState(DTStates.FOUR);
			} else {
				sendDialogue("Archaeologist", DialogueType.NPC, 684, FacialAnimation.DEFAULT,
						"Alright, I will take you to the first warlord you need to defeat.");
				setNextDialogueId(0, 31);
			}
			break;
		case 33:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes|No.");
			setNextDialogueId(0, 34);
			setNextDialogueId(1, 35);
			break;
		case 34:
			player.getActionSender().removeChatboxInterface();
			player.setTeleportTarget(Location.create(2865, 3762, 0));
			player.getActionSender().sendMessage("Kamil will spawn soon...");
			World.getWorld().submit(new Tickable(3) {

				@Override
				public void execute() {
					this.stop();
					NPC n = new NPC(3458, Location.create(2865, 3765), Location.create(2865, 3765),
							Location.create(2865, 3765), NormalDirection.SOUTH_WEST.intValue());
					World.getWorld().register(n);
					n.setInstancedPlayer(player);
					player.setAttribute("ownedNPC", n);
					n.getCombatState().startAttacking(player, player.isAutoRetaliating());
				}

			});
			break;
		case 35:
			setNextDialogueId(0, -1);
			player.removeAttribute("questnpc");
			break;
		case 36:
			if (player.getInventory().hasItem(ICE_DIAMOND)) {
				player.getInventory().remove(ICE_DIAMOND);
				sendDialogue("Archaeologist", DialogueType.NPC, 684, FacialAnimation.DEFAULT,
						"Great job on defeating Kamil, Would you like me to take you to the next warlord?");
				setNextDialogueId(0, 37);
				setState(DTStates.FIVE);
			} else {
				sendDialogue("Archaeologist", DialogueType.NPC, 684, FacialAnimation.DEFAULT,
						"Alright, I will take you to the first warlord you need to defeat.");
				setNextDialogueId(0, 34);
			}
			break;
		/**
		 * State = Five
		 */
		case 37:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes|No.");
			setNextDialogueId(0, 38);
			setNextDialogueId(1, 1337);
			break;
		case 38:
			player.getActionSender().removeChatboxInterface();
			player.setTeleportTarget(Location.create(3305, 9376, 0));
			player.getActionSender().sendMessage("Fareed will spawn soon...");
			World.getWorld().submit(new Tickable(3) {

				@Override
				public void execute() {
					this.stop();
					NPC n = new NPC(3456, Location.create(3313, 9376), Location.create(3313, 9376),
							Location.create(3313, 9376), NormalDirection.SOUTH_EAST.intValue());
					World.getWorld().register(n);
					n.setInstancedPlayer(player);
					player.setAttribute("ownedNPC", n);
					n.getCombatState().startAttacking(player, player.isAutoRetaliating());
				}

			});
			break;
		case 39:
			if (player.getInventory().hasItem(SMOKE_DIAMOND)) {
				player.getInventory().remove(SMOKE_DIAMOND);
				sendDialogue("Archaeologist", DialogueType.NPC, 684, FacialAnimation.DEFAULT,
						"Great job on defeating Fareed, Would you like me to take you to the next warlord?");
				setNextDialogueId(0, 40);
				setState(DTStates.SIX);
			} else {
				sendDialogue("Archaeologist", DialogueType.NPC, 684, FacialAnimation.DEFAULT,
						"Alright, I will take you to the first warlord you need to defeat.");
				setNextDialogueId(0, 38);
			}
			break;
		/**
		 * State = Six
		 */
		case 40:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes|No.");
			setNextDialogueId(0, 41);
			setNextDialogueId(1, 1337);
			break;
		case 41:
			player.getActionSender().removeChatboxInterface();
			player.setTeleportTarget(Location.create(2738, 5081, 0));
			player.getActionSender().sendMessage("Damis will spawn soon...");
			World.getWorld().submit(new Tickable(3) {

				@Override
				public void execute() {
					this.stop();
					NPC n = new NPC(683, Location.create(2738, 5085), Location.create(2738, 5085),
							Location.create(2738, 5085), NormalDirection.SOUTH_WEST.intValue());
					World.getWorld().register(n);
					n.setInstancedPlayer(player);
					player.setAttribute("ownedNPC", n);
					n.getCombatState().startAttacking(player, player.isAutoRetaliating());
				}

			});
			break;
		case 42:
			if (player.getInventory().hasItem(SHADOW_DIAMOND)) {
				player.getInventory().remove(SHADOW_DIAMOND);
				sendDialogue("Archaeologist", DialogueType.NPC, 684, FacialAnimation.DEFAULT,
						"Great job on defeating Damis, That was the final warlord to defeat now we can unlock the secrets to this new magic.");
				setNextDialogueId(0, 43);
				setState(DTStates.SEVEN);
			} else {
				sendDialogue("Archaeologist", DialogueType.NPC, 684, FacialAnimation.DEFAULT,
						"Alright, I will take you to the first warlord you need to defeat.");
				setNextDialogueId(0, 41);
			}
			break;
		/**
		 * Stage = Seven
		 */
		case 43:
			sendDialogue("Archaeologist", DialogueType.NPC, 684, FacialAnimation.DEFAULT,
					"Let me go ahead and add the collected diamonds to the book to unveil the secrets to this new magic.");
			setNextDialogueId(0, 44);
			break;
		case 44:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"I can feel the ancient magics flowing through me...");
			setNextDialogueId(0, 45);
			break;
		case 45:
			player.getActionSender().removeChatboxInterface();
			player.removeAttribute("questnpc");
			player.getActionSender().sendMessage(
					"Congratulations you've finished Desert Treasure, You can now speak to The Archaeologist to switch magics and purchase items.");
			setState(DTStates.COMPLETED);
			player.getActionSender().sendConfig(130, 4);
			break;
		/**
		 * Stage = Completed
		 */
		case 46:
			sendDialogue("Archaeologist", DialogueType.NPC, 684, FacialAnimation.DEFAULT,
					"Welcome back! How can I help you?");
			setNextDialogueId(0, 47);
			break;
		case 47:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"I'd like to see your shop|I'd like to change my magics|Nothing.");
			setNextDialogueId(0, 48);
			setNextDialogueId(1, 49);
			setNextDialogueId(2, 1337);
			break;
		case 48:
			player.getActionSender().removeChatboxInterface();
			player.removeAttribute("questnpc");
			Shop.open(player, 22, 1);
			break;
		case 49:
			player.getActionSender().removeChatboxInterface();
			player.removeAttribute("questnpc");
			boolean ancients = player.getCombatState().getSpellBook() == SpellBook.ANCIENT_MAGICKS.getSpellBookId();
			int config = ancients ? 0 : 1;
			SpellBook book = ancients ? MagicCombatAction.SpellBook.MODERN_MAGICS
					: MagicCombatAction.SpellBook.ANCIENT_MAGICKS;
			player.getActionSender().sendConfig(439, config);
			player.getCombatState().setSpellBook(book.getSpellBookId());
			break;

		case 60:
			sendDialogue("Archaeologist", DialogueType.NPC, 684, FacialAnimation.DEFAULT,
					"Come back when you have the blood diamond.");
			setNextDialogueId(0, 47);
			break;

		/*
		 * Break operations and remove all dialogue
		 */
		case 1337:
			player.getActionSender().removeChatboxInterface();
			player.removeAttribute("questnpc");
			break;
		}
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void showQuestInterface() {
		player.getActionSender().sendString(275, 2, "<col=800000>Desert Treasure");
		boolean started = state != DTStates.NOT_STARTED;
		if (started) {
			switch (state) {
			case ONE:
				player.getActionSender().sendString(275, 4, "The Archaeologist has asked me to speak to");
				player.getActionSender().sendString(275, 5, "Historian Minas in the Varrock museum.");
				for (int i = 6; i <= 133; i++) {
					player.getActionSender().sendString(275, i, "");
				}
				break;
			case TWO:
				player.getActionSender().sendString(275, 4, "<str>The Archaeologist has asked me to speak to");
				player.getActionSender().sendString(275, 5, "<str>Historian Minas in the Varrock museum.");
				player.getActionSender().sendString(275, 6, "Historian Minas has asked me to take the book back to");
				player.getActionSender().sendString(275, 7, "The Archaeologist.");
				for (int i = 8; i <= 133; i++) {
					player.getActionSender().sendString(275, i, "");
				}
				break;
			case THREE:
				player.getActionSender().sendString(275, 4, "<str>The Archaeologist has asked me to speak to");
				player.getActionSender().sendString(275, 5, "<str>Historian Minas in the Varrock museum.");
				player.getActionSender().sendString(275, 6,
						"<str>Historian Minas has asked me to take the book back to");
				player.getActionSender().sendString(275, 7, "<str>The Archaeologist.");
				player.getActionSender().sendString(275, 8, "I must defeat the 5 warlords, Speak to the Archaelogist");
				player.getActionSender().sendString(275, 9, "to fight them.");
				for (int i = 10; i <= 133; i++) {
					player.getActionSender().sendString(275, i, "");
				}
				break;
			case FOUR:
				player.getActionSender().sendString(275, 4, "<str>The Archaeologist has asked me to speak to");
				player.getActionSender().sendString(275, 5, "<str>Historian Minas in the Varrock museum.");
				player.getActionSender().sendString(275, 6,
						"<str>Historian Minas has asked me to take the book back to");
				player.getActionSender().sendString(275, 7, "<str>The Archaeologist.");
				player.getActionSender().sendString(275, 8,
						"<str>I must defeat the 5 warlords, Speak to the Archaelogist");
				player.getActionSender().sendString(275, 9, "<str>to fight them.");
				player.getActionSender().sendString(275, 10, "Warlords Killed:");
				player.getActionSender().sendString(275, 11, "Dessous");
				for (int i = 12; i <= 133; i++) {
					player.getActionSender().sendString(275, i, "");
				}
				break;
			case FIVE:
				player.getActionSender().sendString(275, 4, "<str>The Archaeologist has asked me to speak to");
				player.getActionSender().sendString(275, 5, "<str>Historian Minas in the Varrock museum.");
				player.getActionSender().sendString(275, 6,
						"<str>Historian Minas has asked me to take the book back to");
				player.getActionSender().sendString(275, 7, "<str>The Archaeologist.");
				player.getActionSender().sendString(275, 8,
						"<str>I must defeat the 5 warlords, Speak to the Archaelogist");
				player.getActionSender().sendString(275, 9, "<str>to fight them.");
				player.getActionSender().sendString(275, 10, "Warlords Killed:");
				player.getActionSender().sendString(275, 11, "Dessous");
				player.getActionSender().sendString(275, 12, "Kamil");
				for (int i = 13; i <= 133; i++) {
					player.getActionSender().sendString(275, i, "");
				}
				break;
			case SIX:
				player.getActionSender().sendString(275, 4, "<str>The Archaeologist has asked me to speak to");
				player.getActionSender().sendString(275, 5, "<str>Historian Minas in the Varrock museum.");
				player.getActionSender().sendString(275, 6,
						"<str>Historian Minas has asked me to take the book back to");
				player.getActionSender().sendString(275, 7, "<str>The Archaeologist.");
				player.getActionSender().sendString(275, 8,
						"<str>I must defeat the 5 warlords, Speak to the Archaelogist");
				player.getActionSender().sendString(275, 9, "<str>to fight them.");
				player.getActionSender().sendString(275, 10, "Warlords Killed:");
				player.getActionSender().sendString(275, 11, "Dessous");
				player.getActionSender().sendString(275, 12, "Kamil");
				player.getActionSender().sendString(275, 13, "Fareed");
				for (int i = 14; i <= 133; i++) {
					player.getActionSender().sendString(275, i, "");
				}
				break;
			case SEVEN:
				player.getActionSender().sendString(275, 4, "<str>The Archaeologist has asked me to speak to");
				player.getActionSender().sendString(275, 5, "<str>Historian Minas in the Varrock museum.");
				player.getActionSender().sendString(275, 6,
						"<str>Historian Minas has asked me to take the book back to");
				player.getActionSender().sendString(275, 7, "<str>The Archaeologist.");
				player.getActionSender().sendString(275, 8,
						"<str>I must defeat the 5 warlords, Speak to the Archaelogist");
				player.getActionSender().sendString(275, 9, "<str>to fight them.");
				player.getActionSender().sendString(275, 10, "Warlords Killed:");
				player.getActionSender().sendString(275, 11, "Dessous");
				player.getActionSender().sendString(275, 12, "Kamil");
				player.getActionSender().sendString(275, 13, "Fareed");
				player.getActionSender().sendString(275, 14, "Damis");
				for (int i = 15; i <= 133; i++) {
					player.getActionSender().sendString(275, i, "");
				}
				break;
			case COMPLETED:
				player.getActionSender().sendString(275, 4, "<str>The Archaeologist has asked me to speak to");
				player.getActionSender().sendString(275, 5, "<str>Historian Minas in the Varrock museum.");
				player.getActionSender().sendString(275, 6,
						"<str>Historian Minas has asked me to take the book back to");
				player.getActionSender().sendString(275, 7, "<str>The Archaeologist.");
				player.getActionSender().sendString(275, 8,
						"<str>I must defeat the 5 warlords, Speak to the Archaelogist");
				player.getActionSender().sendString(275, 9, "<str>to fight them.");
				player.getActionSender().sendString(275, 10, "<str>Warlords Killed:");
				player.getActionSender().sendString(275, 11, "<str>Dessous");
				player.getActionSender().sendString(275, 12, "<str>Kamil");
				player.getActionSender().sendString(275, 13, "<str>Fareed");
				player.getActionSender().sendString(275, 14, "<str>Damis");
				player.getActionSender().sendString(275, 15, "");
				player.getActionSender().sendString(275, 16, "<col=ff0000>QUEST COMPLETE!");
				player.getActionSender().sendString(275, 17, "<col=800000>Reward:");
				player.getActionSender().sendString(275, 18, "<col=000080>Access to Ancient magic spellbook");
				player.getActionSender().sendString(275, 19, "<col=000080>Access to The Archaeologists Shop");
				for (int i = 20; i <= 133; i++) {
					player.getActionSender().sendString(275, i, "");
				}
				break;
			}
		} else {
			player.getActionSender().sendString(275, 4, "Speak to the Archaeologist in Al'kharid to begin this quest");
			player.getActionSender().sendString(275, 5, "<col=800000>Requirements:");
			player.getActionSender().sendString(275, 6, "<col=000080>50 Magic");
			for (int i = 7; i <= 133; i++)
				player.getActionSender().sendString(275, i, "");
		}
		player.getActionSender().sendInterface(275, false);
	}
}