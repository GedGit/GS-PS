package org.rs2server.rs2.model;

import org.rs2server.Server;
import org.rs2server.cache.format.*;
import org.rs2server.rs2.Constants;
import org.rs2server.rs2.content.BossKillLog;
import org.rs2server.rs2.content.StarterMap;
import org.rs2server.rs2.domain.service.api.*;
import org.rs2server.rs2.domain.service.api.PermissionService.PlayerPermissions;
import org.rs2server.rs2.domain.service.api.content.*;
import org.rs2server.rs2.domain.service.api.content.bounty.BountyHunterService;
import org.rs2server.rs2.domain.service.api.skill.farming.FarmingService;
import org.rs2server.rs2.domain.service.impl.*;
import org.rs2server.rs2.domain.service.impl.skill.FarmingServiceImpl;
import org.rs2server.rs2.model.Animation.FacialAnimation;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction;
import org.rs2server.rs2.model.container.Bank;
import org.rs2server.rs2.model.container.Inventory;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.skills.slayer.SlayerTask;
import org.rs2server.rs2.model.skills.slayer.SlayerTask.Master;
import org.rs2server.rs2.net.ActionSender;
import org.rs2server.rs2.net.ActionSender.DialogueType;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

import java.text.NumberFormat;
import java.util.*;

/**
 * Handles the old dialogue system :(
 * 
 * @author Vichy
 */
public class DialogueManager {

	/**
	 * Advanced the current dialogue onto next index.
	 * 
	 * @param player
	 *            the player having a dialogue.
	 * @param index
	 *            the dialogue index ID.
	 */
	public static void advanceDialogue(Player player, int index) {
		try {
			int dialogueId = player.getInterfaceState().getNextDialogueId(index);
			if (dialogueId == -1) {
				player.getActionSender().removeChatboxInterface();
				return;
			}
			openDialogue(player, dialogueId);
		} catch (Exception e) {
			//
		}
	}

	/**
	 * Opens up a dialogue based on the dialogue ID provided.
	 * 
	 * @param player
	 *            the player having a dialogue.
	 * @param dialogueId
	 *            the dialogue ID to open.
	 */
	public static void openDialogue(final Player player, int dialogueId) {
		final SlayerService slayerService = Server.getInjector().getInstance(SlayerService.class);
		final GemBagService gemBagService = Server.getInjector().getInstance(GemBagService.class);
		final FarmingService farmingService = Server.getInjector().getInstance(FarmingService.class);
		if (dialogueId == -1)
			return;
		for (int i = 0; i < 5; i++)
			player.getInterfaceState().setNextDialogueId(i, -1);
		player.getInterfaceState().setOpenDialogueId(dialogueId);

		switch (dialogueId) {
		case 1000:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Modern Spellbook|Ancient Spellbook|Lunar Spellbook|Arceuus Spellbook");
			player.getInterfaceState().setNextDialogueId(0, 1001);
			player.getInterfaceState().setNextDialogueId(1, 1002);
			player.getInterfaceState().setNextDialogueId(2, 1003);
			player.getInterfaceState().setNextDialogueId(3, 1004);
			if (player.getSkills().getPrayerPoints() < player.getSkills().getLevelForExperience(Skills.PRAYER)) {
				player.getSkills().setLevel(Skills.PRAYER, player.getSkills().getLevelForExperience(Skills.PRAYER));
				player.getSkills().setPrayerPoints(player.getSkills().getLevelForExperience(Skills.PRAYER), true);
				if (player.getActionSender() != null)
					player.getActionSender().sendSkills();
				player.getActionSender().sendMessage("You pray at the altar...");
				player.playAnimation(Animation.create(645));
			}
			break;
		case 1001:
			player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 1381, null,
					"You've activated the Modern (default) spellbook!!");
			player.getActionSender().sendConfig(439, 0);
			player.getCombatState().setSpellBook(MagicCombatAction.SpellBook.MODERN_MAGICS.getSpellBookId());
			break;
		case 1002:
			player.getActionSender().sendConfig(439, 1);
			player.getCombatState().setSpellBook(MagicCombatAction.SpellBook.ANCIENT_MAGICKS.getSpellBookId());
			player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 4675, null,
					"An ancient wisdom fills your mind...");
			break;
		case 1003:
			player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 9084, null,
					"You've activated the Lunar spellbook!");
			player.getActionSender().sendConfig(439, 2);
			player.getCombatState().setSpellBook(MagicCombatAction.SpellBook.LUNAR_MAGICS.getSpellBookId());
			break;
		case 1004:
			player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 19943, null,
					"You've activated the Arceuus spellbook!");
			player.getActionSender().sendConfig(439, 3);
			player.getCombatState().setSpellBook(MagicCombatAction.SpellBook.ARCEUUS_MAGICS.getSpellBookId());
			break;
		case 19000:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Normal Player|<img=2>Ironman|<img=3>Ultimate Ironman|<img=10>Hardcore Ironman");
			player.getInterfaceState().setNextDialogueId(0, 19001);
			player.getInterfaceState().setNextDialogueId(1, 19002);
			player.getInterfaceState().setNextDialogueId(2, 19003);
			player.getInterfaceState().setNextDialogueId(3, 19004);
			break;
		case 19001:
			StarterMap.handleStarter(player, 0);
			player.getActionSender().removeChatboxInterface();
			player.getActionSender().sendInterface(269, false);
			player.removeAttribute("busy");
			break;
		case 19002:
			PlayerService playerService2 = Server.getInjector().getInstance(PlayerService.class);
			PermissionService perms = Server.getInjector().getInstance(PermissionService.class);
			player.setIsIronMan(true);
			perms.give(player, PermissionService.PlayerPermissions.IRON_MAN);
			playerService2.giveItem(player, new Item(12810, 1), true);
			playerService2.giveItem(player, new Item(12811, 1), true);
			playerService2.giveItem(player, new Item(12812, 1), true);
			StarterMap.handleStarter(player, 1);
			player.getActionSender().removeChatboxInterface();
			player.getActionSender().sendInterface(269, false);
			player.removeAttribute("busy");
			break;
		case 19003:
			PlayerService playerService3 = Server.getInjector().getInstance(PlayerService.class);
			PermissionService perms1 = Server.getInjector().getInstance(PermissionService.class);
			player.setUltimateIronMan(true);
			perms1.give(player, PermissionService.PlayerPermissions.ULTIMATE_IRON_MAN);
			playerService3.giveItem(player, new Item(12813, 1), true);
			playerService3.giveItem(player, new Item(12814, 1), true);
			playerService3.giveItem(player, new Item(12815, 1), true);
			StarterMap.handleStarter(player, 2);
			player.getActionSender().removeChatboxInterface();
			player.getActionSender().sendInterface(269, false);
			player.removeAttribute("busy");
			break;
		case 19004:
			PlayerService playerService4 = Server.getInjector().getInstance(PlayerService.class);
			PermissionService perms2 = Server.getInjector().getInstance(PermissionService.class);
			player.setHardcoreIronMan(true);
			perms2.give(player, PermissionService.PlayerPermissions.HARDCORE_IRON_MAN);
			playerService4.giveItem(player, new Item(20792, 1), true);
			playerService4.giveItem(player, new Item(20794, 1), true);
			playerService4.giveItem(player, new Item(20796, 1), true);
			StarterMap.handleStarter(player, 3);
			player.getActionSender().removeChatboxInterface();
			player.getActionSender().sendInterface(269, false);
			player.removeAttribute("busy");
			break;
		case 0:
			player.getActionSender().sendDialogue("Test", DialogueType.NPC, 2044, FacialAnimation.DEFAULT,
					"Hello, how may I help you?");
			player.getInterfaceState().setNextDialogueId(0, 1);
			break;
		case 1:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"I'd like to bank my items.|Nevermind.");
			player.getInterfaceState().setNextDialogueId(0, 2);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 2:
			Bank.open(player);
			player.getActionSender().removeChatboxInterface();
			break;
		case 3:
			player.getActionSender().sendDialogue("Tool Leprechaun", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Open equipment storage|Teleport to other patches");
			player.getInterfaceState().setNextDialogueId(0, 4);
			player.getInterfaceState().setNextDialogueId(1, 5);
			break;
		case 4:
			farmingService.openToolInterface(player);
			player.getActionSender().removeChatboxInterface();
			break;
		case 5:
			player.getActionSender().sendDialogue("Farming locations", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Tree patch locations|Allotment locations");
			player.getInterfaceState().setNextDialogueId(0, 6);
			player.getInterfaceState().setNextDialogueId(1, 100036);
			break;
		case 6:
			player.getActionSender().sendDialogue("Farming Tree locations", DialogueType.OPTION, -1,
					FacialAnimation.DEFAULT, "Lumbridge|Varrock|Falador|Taverley|Treen Gnome Stronghold");
			player.getInterfaceState().setNextDialogueId(0, 7);
			player.getInterfaceState().setNextDialogueId(1, 8);
			player.getInterfaceState().setNextDialogueId(2, 9);
			player.getInterfaceState().setNextDialogueId(3, 10);
			player.getInterfaceState().setNextDialogueId(4, 11);
			break;
		case 7:
			if (!FarmingServiceImpl.canTeleport(player))
				return;
			player.getActionSender().removeChatboxInterface();
			player.teleport(Location.create(3195, 3231, 0), 0, 0, true);
			break;
		case 8:
			if (!FarmingServiceImpl.canTeleport(player))
				return;
			player.getActionSender().removeChatboxInterface();
			player.teleport(Location.create(3227, 3459, 0), 0, 0, true);
			break;
		case 9:
			if (!FarmingServiceImpl.canTeleport(player))
				return;
			player.getActionSender().removeChatboxInterface();
			player.teleport(Location.create(3006, 3373, 0), 0, 0, true);
			break;
		case 10:
			if (!FarmingServiceImpl.canTeleport(player))
				return;
			player.getActionSender().removeChatboxInterface();
			player.teleport(Location.create(2934, 3438, 0), 0, 0, true);
			break;
		case 11:
			if (!FarmingServiceImpl.canTeleport(player))
				return;
			player.getActionSender().removeChatboxInterface();
			player.teleport(Location.create(2438, 3415, 0), 0, 0, true);
			break;

		case 12:
			player.getActionSender().sendDialogue("Ring of Wealth", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Boss Log|Coin Collection (<shad=000000>"
							+ (player.getDatabaseEntity().hasCoinCollection() ? "<col=00ff00>enabled"
									: "<col=ff0000>disabled")
							+ "</shad></col>)");
			player.getInterfaceState().setNextDialogueId(0, 13);
			player.getInterfaceState().setNextDialogueId(1, 14);
			break;
		case 13:
			player.getActionSender().removeChatboxInterface();
			BossKillLog.handleBossLog(player);
			break;
		case 14:
			player.getDatabaseEntity().toggleCoinCollection();
			player.getActionSender().sendDialogue("Ring of Wealth", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Boss Log|Coin Collection (<shad=000000>"
							+ (player.getDatabaseEntity().hasCoinCollection() ? "<col=00ff00>enabled"
									: "<col=ff0000>disabled")
							+ "</col></shad>)");
			player.getInterfaceState().setNextDialogueId(0, 13);
			player.getInterfaceState().setNextDialogueId(1, 15);
			break;
		case 15:
			player.getDatabaseEntity().toggleCoinCollection();
			player.getActionSender().sendDialogue("Ring of Wealth", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Boss Log|Coin Collection (<shad=000000>"
							+ (player.getDatabaseEntity().hasCoinCollection() ? "<col=00ff00>enabled"
									: "<col=ff0000>disabled")
							+ "</col></shad>)");
			player.getInterfaceState().setNextDialogueId(0, 13);
			player.getInterfaceState().setNextDialogueId(1, 14);
			break;
		case 26:
			final Location teleportTo = player.getAttribute("teleportTo");
			if (teleportTo != null) {
				player.playAnimation(Animation.create(714));
				player.playGraphics(Graphic.create(308, 48, 100));
				World.getWorld().submit(new Tickable(4) {
					@Override
					public void execute() {
						player.setTeleportTarget(teleportTo);
						player.playAnimation(Animation.create(-1));
						player.playAnimation(Animation.create(715));
						this.stop();
					}
				});
			}
			player.getActionSender().removeChatboxInterface();
			break;
		case 27:
			player.getActionSender().sendDialogue("Make-over mage", DialogueType.NPC, 1306, FacialAnimation.DEFAULT,
					"Hello there! I am known as the make-over mage! I have"
							+ "<br>spent many years researching magics that can change"
							+ "<br>your physical appearance!");
			player.getInterfaceState().setNextDialogueId(0, 28);
			break;
		case 28:
			player.getActionSender().sendDialogue("Make-over mage", DialogueType.NPC, 1306, FacialAnimation.DEFAULT,
					"I can alter your physical form for a small fee of only"
							+ "<br>3000 gold coins! Would you like me to perform my" + "<br>magics upon you?");
			player.getInterfaceState().setNextDialogueId(0, 29);
			break;
		case 29:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes|No");
			player.getInterfaceState().setNextDialogueId(0, 30);
			player.getInterfaceState().setNextDialogueId(1, 31);
			break;
		case 30:
			if (player.getInventory().hasItem(new Item(995, 3000))) {
				player.getActionSender().sendInterface(269, false);
				player.getInventory().remove(new Item(995, 3000));
			} else {
				player.getActionSender().sendMessage("You don't have enough gold to do this.");
			}
			player.getActionSender().removeChatboxInterface();
			break;
		case 31:
			player.getActionSender().removeChatboxInterface();
			break;
		case 32:// o.o?
			player.getActionSender().sendDialogue("Kamfreena", DialogueType.NPC, 2461, FacialAnimation.ANGER_1,
					"Time is up! You've ran out of Guild Tokens."
							+ "<br>Please leave the Cyclops room as quick as possible!");
			player.getInterfaceState().setNextDialogueId(0, -1);
			break;
		case 34:
			player.getActionSender().sendDialogue("Kamfreena", DialogueType.NPC, 2461, FacialAnimation.ANGER_2,
					"I said TIME UP! Please leave by yourself next time.");
			player.getInterfaceState().setNextDialogueId(0, -1);
			break;

		case 36:
			player.getActionSender().sendDialogue("Shop keeper", DialogueType.NPC, 514, FacialAnimation.DEFAULT,
					"Hello! Would you like to see my wide variety of items?");
			player.getInterfaceState().setNextDialogueId(0, 37);
			break;
		case 37:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes|No");
			player.getInterfaceState().setNextDialogueId(0, 38);
			player.getInterfaceState().setNextDialogueId(1, 39);
			break;
		case 38:
			Shop.open(player, 0, 1);
			player.getActionSender().removeChatboxInterface();
			break;
		case 39:
			player.getActionSender().removeChatboxInterface();
			break;
		case 40:
			player.getActionSender().sendDialogue("Shop keeper", DialogueType.NPC, 516, FacialAnimation.DEFAULT,
					"Hello! Would you like to see my wide variety of items?");
			player.getInterfaceState().setNextDialogueId(0, 41);
			break;
		case 41:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes|No");
			player.getInterfaceState().setNextDialogueId(0, 42);
			player.getInterfaceState().setNextDialogueId(1, 43);
			break;
		case 42:
			Shop.open(player, 1, 1);
			player.getActionSender().removeChatboxInterface();
			break;
		case 43:
			player.getActionSender().removeChatboxInterface();
			break;
		case 44:
			player.getActionSender().sendDialogue("Shop keeper", DialogueType.NPC, 518, FacialAnimation.DEFAULT,
					"Hello! Would you like to see my wide variety of items?");
			player.getInterfaceState().setNextDialogueId(0, 45);
			break;
		case 45:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes|No");
			player.getInterfaceState().setNextDialogueId(0, 46);
			player.getInterfaceState().setNextDialogueId(1, 47);
			break;
		case 46:
			Shop.open(player, 2, 1);
			player.getActionSender().removeChatboxInterface();
			break;
		case 47:
			player.getActionSender().removeChatboxInterface();
			break;
		case 48:
			player.getActionSender().sendDialogue("You've found a hidden tunnel, do you want to enter?",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT, "Yes|No.");
			player.getInterfaceState().setNextDialogueId(0, 49);
			player.getInterfaceState().setNextDialogueId(1, 50);
			break;
		case 49:
			player.setTeleportTarget(Location.create(3551, 9694, 0));
			player.getActionSender().updateMinimap(ActionSender.BLACKOUT_MAP);
			player.getActionSender().removeChatboxInterface();
			break;
		case 50:
			player.getActionSender().removeChatboxInterface();
			break;
		case 51:
			player.getActionSender().sendDialogue("Shop keeper", DialogueType.NPC, 519, FacialAnimation.DEFAULT,
					"Hello! Would you like to see my wide variety of items?");
			player.getInterfaceState().setNextDialogueId(0, 52);
			break;
		case 52:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes|No");
			player.getInterfaceState().setNextDialogueId(0, 53);
			player.getInterfaceState().setNextDialogueId(1, 54);
			break;
		case 53:
			Shop.open(player, 3, 1);
			player.getActionSender().removeChatboxInterface();
			break;
		case 54:
			player.getActionSender().removeChatboxInterface();
			break;
		case 55:
			player.getActionSender().sendDialogue("Emblem Trader", DialogueType.NPC, 315, FacialAnimation.DEFAULT,
					"Hello, what would you like to do?");
			player.getInterfaceState().setNextDialogueId(0, 56);
			break;
		case 56:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Trade in my emblems|Restore my Special Energy|Give me a skull");
			player.getInterfaceState().setNextDialogueId(0, 10000);
			player.getInterfaceState().setNextDialogueId(1, 57);
			player.getInterfaceState().setNextDialogueId(2, 58);
			break;
		case 57:
			if (player.getCombatState().getSpecialEnergy() != 100) {
				player.getActionSender().sendDialogue("Emblem Trader", DialogueType.NPC, 315, FacialAnimation.DEFAULT,
						"I've restored your special attack energy.");
				player.getCombatState().setSpecialEnergy(100);
				player.getActionSender().sendConfig(300, 1000);
			} else
				player.getActionSender().sendDialogue("Emblem Trader", DialogueType.NPC, 315, FacialAnimation.DEFAULT,
						"It seems you're already at full special attack energy.");
			player.getInterfaceState().setNextDialogueId(0, -1);
			break;
		case 58:
			player.getCombatState().setSkullTicks(1000);
			player.getActionSender().sendDialogue("Emblem Trader", DialogueType.NPC, 315, FacialAnimation.DEFAULT,
					"Be careful now, you will loose all of your items if you die.");
			player.getInterfaceState().setNextDialogueId(0, -1);
			break;
		case 60:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Buy - 10|Buy - 50|Buy - 100|Buy - 500|Buy - 1000");
			player.getInterfaceState().setNextDialogueId(0, 61);
			player.getInterfaceState().setNextDialogueId(1, 62);
			player.getInterfaceState().setNextDialogueId(2, 63);
			player.getInterfaceState().setNextDialogueId(3, 64);
			player.getInterfaceState().setNextDialogueId(4, 65);
			break;
		case 61:
			if (player.getInterfaceState().getShopItem() != -1 && player.getInterfaceState().getShopSlot() != -1) {
				Shop.buyItem(player, player.getInterfaceState().getShopSlot(), player.getInterfaceState().getShopItem(),
						10);
				player.getInterfaceState().setShopItem(-1, -1);
			}
			player.getActionSender().removeChatboxInterface();
			break;
		case 62:
			if (player.getInterfaceState().getShopItem() != -1 && player.getInterfaceState().getShopSlot() != -1) {
				Shop.buyItem(player, player.getInterfaceState().getShopSlot(), player.getInterfaceState().getShopItem(),
						50);
				player.getInterfaceState().setShopItem(-1, -1);
			}
			player.getActionSender().removeChatboxInterface();
			break;
		case 63:
			if (player.getInterfaceState().getShopItem() != -1 && player.getInterfaceState().getShopSlot() != -1) {
				Shop.buyItem(player, player.getInterfaceState().getShopSlot(), player.getInterfaceState().getShopItem(),
						100);
				player.getInterfaceState().setShopItem(-1, -1);
			}
			player.getActionSender().removeChatboxInterface();
			break;
		case 64:
			if (player.getInterfaceState().getShopItem() != -1 && player.getInterfaceState().getShopSlot() != -1) {
				Shop.buyItem(player, player.getInterfaceState().getShopSlot(), player.getInterfaceState().getShopItem(),
						500);
				player.getInterfaceState().setShopItem(-1, -1);
			}
			player.getActionSender().removeChatboxInterface();
			break;
		case 65:
			if (player.getInterfaceState().getShopItem() != -1 && player.getInterfaceState().getShopSlot() != -1) {
				Shop.buyItem(player, player.getInterfaceState().getShopSlot(), player.getInterfaceState().getShopItem(),
						1000);
				player.getInterfaceState().setShopItem(-1, -1);
			}
			player.getActionSender().removeChatboxInterface();
			break;

		case 6990:
			player.getActionSender().sendDialogue(CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(),
					DialogueType.NPC, player.getAttribute("talkingNpc"), FacialAnimation.DEFAULT,
					"'Ello and what are you after then?");
			player.getInterfaceState().setNextDialogueId(0, 6991);
			break;

		// selecting another assignment
		case 6991:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"I need another assignment.|Do you have anything for trade?|About the task system...|Er...nothing...");
			player.getInterfaceState().setNextDialogueId(0, 6992);
			player.getInterfaceState().setNextDialogueId(1, 6993);
			player.getInterfaceState().setNextDialogueId(2, 6994);
			player.getInterfaceState().setNextDialogueId(3, 6995);
			break;
		case 6992:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"I need another assignment.");
			player.getInterfaceState().setNextDialogueId(0, 512);
			break;
		case 6993:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
					"Do you have anything for trade?");
			player.getInterfaceState().setNextDialogueId(0, 507);
			break;
		case 6994:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Tell me about the Task System.|Sorry I was just leaving.");
			player.getInterfaceState().setNextDialogueId(0, 509);
			player.getInterfaceState().setNextDialogueId(1, 510);
			break;
		case 6995:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Er...nothing...");
			player.getInterfaceState().setNextDialogueId(0, 6996);
			break;
		case 6996:
			player.getActionSender().removeChatboxInterface();
			break;
		case 7000:
			player.getActionSender().sendDialogue("Estate Agent", DialogueType.NPC, 5419, FacialAnimation.DEFAULT,
					"'Ello mate, what can I do ye for?");
			player.getInterfaceState().setNextDialogueId(0, 7001);
			break;
		case 7001:
			player.getActionSender().sendDialogue("Select an option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"What can you teach me?|What is that cape you're wearing?|Oh, nevermind");
			player.getInterfaceState().setNextDialogueId(0, 7002);
			player.getInterfaceState().setNextDialogueId(1, 7003);
			player.getInterfaceState().setNextDialogueId(2, 7004);
			break;
		case 7002:
			player.getActionSender().sendDialogue("Estate Agent", DialogueType.NPC, 5419, FacialAnimation.DEFAULT,
					"Take this hammer and build me 2 chairs, and we'll talk.");
			break;

		case 100:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.ATTACK_LEVEL_UP, -1,
					FacialAnimation.DEFAULT, "<col=000080>  Congratulations, you just advanced an Attack level!",
					"You have now reached level " + player.getSkills().getLevelForExperience(Skills.ATTACK) + ".");
			break;
		case 101:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.DEFENCE_LEVEL_UP, -1,
					FacialAnimation.DEFAULT, "<col=000080> Congratulations, you just advanced a Defence level!",
					"You have now reached level " + player.getSkills().getLevelForExperience(Skills.DEFENCE) + ".");
			if (player.getSkills().getLevelForExperience(Skills.DEFENCE) > 98) {
				player.getInterfaceState().setNextDialogueId(0, 107);
			}
			break;
		case 102:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.STRENGTH_LEVEL_UP, -1,
					FacialAnimation.DEFAULT, "<col=000080>Congratulations, you just advanced a Strength level!",
					"You have now reached level " + player.getSkills().getLevelForExperience(Skills.STRENGTH) + ".");
			break;
		case 103:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.HITPOINT_LEVEL_UP, -1,
					FacialAnimation.DEFAULT, "<col=000080>Congratulations, you just advanced a Hitpoints level!",
					"You have now reached level " + player.getSkills().getLevelForExperience(Skills.HITPOINTS) + ".");
			break;
		case 104:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.RANGING_LEVEL_UP, -1,
					FacialAnimation.DEFAULT, "<col=000080>Congratulations, you just advanced a Ranged level!",
					"You have now reached level " + player.getSkills().getLevelForExperience(Skills.RANGE) + ".");
			if (player.getSkills().getLevelForExperience(Skills.RANGE) > 98) {
				player.getInterfaceState().setNextDialogueId(0, 108);
			}
			break;
		case 105:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PRAYER_LEVEL_UP, -1,
					FacialAnimation.DEFAULT, "<col=000080>Congratulations, you just advanced a Prayer level!",
					"You have now reached level " + player.getSkills().getLevelForExperience(Skills.PRAYER) + ".");
			break;
		case 106:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.MAGIC_LEVEL_UP, -1,
					FacialAnimation.DEFAULT, "<col=000080>Congratulations, you just advanced a Magic level!",
					"You have now reached level " + player.getSkills().getLevelForExperience(Skills.MAGIC) + ".");
			break;
		case 107:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.COOKING_LEVEL_UP, -1,
					FacialAnimation.DEFAULT, "<col=000080>Congratulations, you just advanced a Cooking level!",
					"You have now reached level " + player.getSkills().getLevelForExperience(Skills.COOKING) + ".");
			break;
		case 108:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.WOODCUTTING_LEVEL_UP, -1,
					FacialAnimation.DEFAULT, "<col=000080>Congratulations, you just advanced a Woodcutting level!",
					"You have now reached level " + player.getSkills().getLevelForExperience(Skills.WOODCUTTING) + ".");
			break;
		case 109:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.FLETCHING_LEVEL_UP, -1,
					FacialAnimation.DEFAULT, "<col=000080>  Congratulations, you just advanced a Fletching level!",
					"You have now reached level " + player.getSkills().getLevelForExperience(Skills.FLETCHING) + ".");
			break;
		case 110:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.FISHING_LEVEL_UP, -1,
					FacialAnimation.DEFAULT, "<col=000080>Congratulations, you just advanced a Fishing level!",
					"You have now reached level " + player.getSkills().getLevelForExperience(Skills.FISHING) + ".");
			break;
		case 111:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.FIREMAKING_LEVEL_UP, -1,
					FacialAnimation.DEFAULT, "<col=000080>Congratulations, you just advanced a Firemaking level!",
					"You have now reached level " + player.getSkills().getLevelForExperience(Skills.FIREMAKING) + ".");
			break;
		case 112:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.CRAFTING_LEVEL_UP, -1,
					FacialAnimation.DEFAULT, "<col=000080>Congratulations, you just advanced a Crafting level!",
					"You have now reached level " + player.getSkills().getLevelForExperience(Skills.CRAFTING) + ".");
			break;
		case 113:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.SMITHING_LEVEL_UP, -1,
					FacialAnimation.DEFAULT, "<col=000080>  Congratulations, you just advanced a Smithing level!",
					"You have now reached level " + player.getSkills().getLevelForExperience(Skills.SMITHING) + ".");
			break;
		case 114:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.MINING_LEVEL_UP, -1,
					FacialAnimation.DEFAULT, "<col=000080>  Congratulations, you just advanced a Mining level!",
					"You have now reached level " + player.getSkills().getLevelForExperience(Skills.MINING) + ".");
			break;
		case 115:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.HERBLORE_LEVEL_UP, -1,
					FacialAnimation.DEFAULT, "<col=000080>Congratulations, you just advanced a Herblore level!",
					"You have now reached level " + player.getSkills().getLevelForExperience(Skills.HERBLORE) + ".");
			break;
		case 116:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.AGILITY_LEVEL_UP, -1,
					FacialAnimation.DEFAULT, "<col=000080>Congratulations, you just advanced an Agility level!",
					"You have now reached level " + player.getSkills().getLevelForExperience(Skills.AGILITY) + ".");
			break;
		case 117:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.THIEVING_LEVEL_UP, -1,
					FacialAnimation.DEFAULT, "<col=000080>Congratulations, you just advanced a Thieving level!",
					"You have now reached level " + player.getSkills().getLevelForExperience(Skills.THIEVING) + ".");
			break;
		case 118:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.SLAYER_LEVEL_UP, -1,
					FacialAnimation.DEFAULT, "<col=000080>Congratulations, you just advanced a Slayer level!",
					"You have now reached level " + player.getSkills().getLevelForExperience(Skills.SLAYER) + ".");
			break;
		case 119:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.FARMING_LEVEL_UP, -1,
					FacialAnimation.DEFAULT, "<col=000080>Congratulations, you just advanced a Farming level!",
					"You have now reached level " + player.getSkills().getLevelForExperience(Skills.FARMING) + ".");
			break;
		case 120:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.RUNECRAFTING_LEVEL_UP, -1,
					FacialAnimation.DEFAULT, "<col=000080>Congratulations, you just advanced a Runecrafting level!",
					"You have now reached level " + player.getSkills().getLevelForExperience(Skills.RUNECRAFTING)
							+ ".");
			break;

		case 122:
			player.setTeleportTarget(Location.create(2659, 2676));
			player.getActionSender().removeChatboxInterface();
			break;

		// Salve-PS Advisor guide / town crier
		case 276:
			player.getActionSender().sendDialogue("Salve-PS Advisor", DialogueType.NPC, 276, FacialAnimation.DEFAULT,
					"Hello, I'm " + Constants.SERVER_NAME + "'s reward advisor, how can I help you?");
			player.getInterfaceState().setNextDialogueId(0, 277);
			break;
		case 277:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Claim vote reward points|Open vote point store|How much have I donated in total?|Open loyalty point store");
			player.getInterfaceState().setNextDialogueId(0, 278);
			player.getInterfaceState().setNextDialogueId(1, 279);
			player.getInterfaceState().setNextDialogueId(2, 280);
			player.getInterfaceState().setNextDialogueId(3, 281);
			break;
		case 278:
			Misc.handleVoteReward(player);
			break;
		case 279:
			player.getActionSender().removeChatboxInterface();
			Shop.open(player, 65, 0);// vote point shop
			break;
		case 280:
			int donated = player.getDatabaseEntity().getAmountDonated();
			player.getActionSender().sendDialogue("Salve-PS Advisor", DialogueType.NPC, 276, FacialAnimation.DEFAULT,
					"Your total donation amount is currently at $" + donated
							+ (donated > 0 ? "<br>Thanks for supporting us!" : " ."));
			player.getInterfaceState().setNextDialogueId(0, 277);
			break;
		case 281:
			player.getActionSender().removeChatboxInterface();
			Shop.open(player, 2, 0);
			break;

		case 311:
			if (!player.getPermissionService().isAny(player, PlayerPermissions.IRON_MAN,
					PlayerPermissions.ULTIMATE_IRON_MAN, PlayerPermissions.HARDCORE_IRON_MAN)) {
				player.getActionSender().sendDialogue("Ironman Advisor", DialogueType.NPC, 311, FacialAnimation.ANNOYED,
						"I don't speak to your kind.");
				player.getInterfaceState().setNextDialogueId(0, -1);
				return;
			}
			player.getActionSender().sendDialogue("Ironman Advisor", DialogueType.NPC, 311, FacialAnimation.DEFAULT,
					"Hello, what can I do for you?");
			player.getInterfaceState().setNextDialogueId(0, 312);
			break;

		case 312:
			String ironman = player.getAppearance().getGender() == 0 ? "ironman" : "ironwoman";
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, 0, FacialAnimation.DEFAULT,
					"I was wondering.. if you could... and would.... remove my status as an " + ironman + ", please?");
			player.getInterfaceState().setNextDialogueId(0, 313);
			break;
		case 313:
			ironman = player.getAppearance().getGender() == 0 ? "ironman" : "ironwoman";
			player.getActionSender().sendDialogue("Ironman Advisor", DialogueType.NPC, 311, FacialAnimation.ANNOYED,
					"It's possible, yes. This will be a one time thing and you will <col=ff0000>NOT</col> "
							+ "be able to get your status as an " + ironman + " back ever again.");
			player.getInterfaceState().setNextDialogueId(0, 314);
			break;
		case 314:
			ironman = player.getAppearance().getGender() == 0 ? "ironman" : "ironwoman";
			player.getActionSender().sendDialogue("<col=ff0000>Remove " + ironman + " status?", DialogueType.OPTION, -1,
					FacialAnimation.DEFAULT, "Yes.|No.");
			player.getInterfaceState().setNextDialogueId(0, 315);
			player.getInterfaceState().setNextDialogueId(1, 316);
			break;
		case 315:
			player.getActionSender().sendDialogue("Ironman Advisor", DialogueType.NPC, 311, FacialAnimation.SAD,
					"It has been done, farewell, " + player.getName() + ".");
			player.getPermissionService().remove(player, PlayerPermissions.IRON_MAN);
			player.getPermissionService().remove(player, PlayerPermissions.ULTIMATE_IRON_MAN);
			player.getPermissionService().remove(player, PlayerPermissions.HARDCORE_IRON_MAN);
			player.setHardcoreIronMan(false);
			player.setUltimateIronMan(false);
			player.setIsIronMan(false);
			player.getActionSender().sendConfig(499, 0);
			player.getInterfaceState().setNextDialogueId(0, -1);
			break;
		case 316:
			player.getActionSender().sendDialogue("Ironman Advisor", DialogueType.NPC, 311, FacialAnimation.HAPPY,
					"A wise choice, " + player.getName() + "!");
			player.getInterfaceState().setNextDialogueId(0, -1);
			break;
		// Slayer master
		case 500:
			player.getActionSender().sendDialogue(CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(),
					DialogueType.NPC, player.getAttribute("talkingNpc"), FacialAnimation.DEFAULT,
					"'Ello and what are you after then?");
			player.getInterfaceState().setNextDialogueId(0, 501);
			break;
		// Slayer master options
		case 501:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"I need another assignment.|Do you have anything for trade?|Could you teleport me to my task?|Er...nothing...");
			player.getInterfaceState().setNextDialogueId(0, 502);
			player.getInterfaceState().setNextDialogueId(1, 503);
			player.getInterfaceState().setNextDialogueId(2, 504);
			player.getInterfaceState().setNextDialogueId(3, 505);
			break;
		case 502:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"I need another assignment.");
			player.getInterfaceState().setNextDialogueId(0, 512);
			break;
		case 503:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
					"Do you have anything for trade?");
			player.getInterfaceState().setNextDialogueId(0, 507);
			break;
		case 504:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
					"Could you teleport me directly to your assigned task, please?");
			player.getInterfaceState().setNextDialogueId(0, 509);
			break;
		case 505:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Er...nothing...");
			player.getInterfaceState().setNextDialogueId(0, 506);
			break;
		case 506:
			player.getActionSender().removeChatboxInterface();
			break;
		case 507:
			player.getActionSender().sendDialogue(CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(),
					DialogueType.NPC, player.getAttribute("talkingNpc"), FacialAnimation.HAPPY,
					"I have a wide selection of Slayer equipment; take a look!");
			player.getInterfaceState().setNextDialogueId(0, 508);
			break;
		case 508:
			Shop.open(player, 12, 1);
			player.getActionSender().removeChatboxInterface();
			break;
		case 509:
			player.getActionSender().sendDialogue(CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(),
					DialogueType.NPC, player.getAttribute("talkingNpc"), FacialAnimation.HAPPY,
					"Certainly, though it will cost you 25k gold coins!");
			player.getInterfaceState().setNextDialogueId(0, 524);
			break;

		// case 524 is slayer master
		case 524:
			player.getActionSender().sendDialogue("Teleport to task for 25k?", DialogueType.OPTION, -1,
					FacialAnimation.DEFAULT, "Yes.|No.");
			player.getInterfaceState().setNextDialogueId(0, 525);
			player.getInterfaceState().setNextDialogueId(1, 501);
			break;

		case 525:
			if (player.getSlayer().getSlayerTask() != null) {
				SlayerTask task = player.getSlayer().getSlayerTask();
				Location location = (Location) task.getMaster().getData()[task.getTaskId()][6];
				if (location == null) {
					player.getActionSender().sendDialogue(
							CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(), DialogueType.NPC,
							player.getAttribute("talkingNpc"), FacialAnimation.SAD,
							"I'm sorry, but I cannot teleport you to your current task..");
					player.getInterfaceState().setNextDialogueId(0, 506);
					return;
				}
				boolean isMember = player.getPermissionService().is(player, PlayerPermissions.SILVER_MEMBER);
				if (isMember) {
					player.getActionSender().removeChatboxInterface();
					player.teleport(location, 0, 0, true);
					return;
				}
				if (player.getInventory().hasItem(new Item(995, 25000))) {
					player.getInventory().remove(new Item(995, 25000));
					player.getActionSender().removeChatboxInterface();
					player.teleport(location, 0, 0, true);
				} else {
					player.getActionSender().sendDialogue(
							CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(), DialogueType.NPC,
							player.getAttribute("talkingNpc"), FacialAnimation.DEFAULT,
							"It seems that you don't have enough gold coins on you, come back later when you do!");
					player.getInterfaceState().setNextDialogueId(0, 506);
				}
				return;
			}
			player.getActionSender().sendDialogue(CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(),
					DialogueType.NPC, player.getAttribute("talkingNpc"), FacialAnimation.SAD,
					"You do not have an active task..");
			player.getInterfaceState().setNextDialogueId(0, 506);
			break;
		case 510:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
					"Sorry I was just leaving.");
			player.getInterfaceState().setNextDialogueId(0, 506);
			break;
		case 511:
			player.getActionSender().sendDialogue(CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(),
					DialogueType.NPC, player.getAttribute("talkingNpc"), FacialAnimation.DEFAULT,
					"There isn't much information on it now, come back later.");
			player.getInterfaceState().setNextDialogueId(0, 506);
			break;
		case 512:
			if (player.getSlayer().getSlayerTask() != null) {
				player.getActionSender().sendDialogue(
						CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(), DialogueType.NPC,
						player.getAttribute("talkingNpc"), FacialAnimation.DEFAULT,
						"You're still hunting " + player.getSlayer().getSlayerTask().getName()
								+ "; come back when you've<br>finished your task.");
				player.getInterfaceState().setNextDialogueId(0, 506);
			} else {
				player.getActionSender().sendDialogue("Choose your task difficulty", DialogueType.OPTION, -1,
						FacialAnimation.DEFAULT, "Easy task|Medium task|Hard task|Elite task|Boss task");
				player.getInterfaceState().setNextDialogueId(0, 526);
				player.getInterfaceState().setNextDialogueId(1, 527);
				player.getInterfaceState().setNextDialogueId(2, 528);
				player.getInterfaceState().setNextDialogueId(3, 529);
				player.getInterfaceState().setNextDialogueId(4, 530);
			}
			break;
		case 526: // Easy
			Master master = Master.forId(401);
			SlayerTask newTask = slayerService.assignTask(player, master);
			if (newTask != null) {
				player.getActionSender().sendDialogue(
						CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(), DialogueType.NPC,
						player.getAttribute("talkingNpc"), FacialAnimation.DEFAULT,
						"Great, you're doing great. Your new task is to kill<br>" + newTask.getTaskAmount() + " "
								+ player.getSlayer().getSlayerTask().getName() + "s");
				player.getInterfaceState().setNextDialogueId(0, 506);
			}
			break;
		case 527: // Medium
			master = Master.forId(404);
			newTask = slayerService.assignTask(player, master);
			if (newTask != null) {
				player.getActionSender().sendDialogue(
						CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(), DialogueType.NPC,
						player.getAttribute("talkingNpc"), FacialAnimation.DEFAULT,
						"Great, you're doing great. Your new task is to kill<br>" + newTask.getTaskAmount() + " "
								+ player.getSlayer().getSlayerTask().getName() + "s");
				player.getInterfaceState().setNextDialogueId(0, 506);
			}
			break;
		case 528: // Hard
			master = Master.forId(403);
			newTask = slayerService.assignTask(player, master);
			if (newTask != null) {
				player.getActionSender().sendDialogue(
						CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(), DialogueType.NPC,
						player.getAttribute("talkingNpc"), FacialAnimation.DEFAULT,
						"Great, you're doing great. Your new task is to kill<br>" + newTask.getTaskAmount() + " "
								+ player.getSlayer().getSlayerTask().getName() + "s");
				player.getInterfaceState().setNextDialogueId(0, 506);
			}
			break;
		case 529: // Elite
			master = Master.forId(6798);
			newTask = slayerService.assignTask(player, master);
			if (newTask != null) {
				player.getActionSender().sendDialogue(
						CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(), DialogueType.NPC,
						player.getAttribute("talkingNpc"), FacialAnimation.DEFAULT,
						"Great, you're doing great. Your new task is to kill<br>" + newTask.getTaskAmount() + " "
								+ player.getSlayer().getSlayerTask().getName() + "s");
				player.getInterfaceState().setNextDialogueId(0, 506);
			}
			break;
		case 530: // Boss
			master = Master.forId(490);
			if (player.getSkills().getLevelForExperience(Skills.SLAYER) < 95) {
				player.getActionSender().sendDialogue(
						CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(), DialogueType.NPC,
						player.getAttribute("talkingNpc"), FacialAnimation.SAD,
						"Ah, I'm sorry, but you'll need a Slayer level of at least 95 to get assigned Boss tasks!");
				player.getInterfaceState().setNextDialogueId(0, 512);
				return;
			}
			newTask = slayerService.assignTask(player, master);
			if (newTask != null) {
				player.getActionSender().sendDialogue(
						CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(), DialogueType.NPC,
						player.getAttribute("talkingNpc"), FacialAnimation.DEFAULT,
						"Great, you're doing great. Your new task is to kill<br>" + newTask.getTaskAmount() + " "
								+ player.getSlayer().getSlayerTask().getName() + "s");
				player.getInterfaceState().setNextDialogueId(0, 506);
			}
			break;

		case 513:
			player.getActionSender().sendDialogue("Vannaka", DialogueType.NPC, 403, FacialAnimation.HAPPY,
					"Hello there, " + player.getName() + ", what can I help you with?");
			player.getInterfaceState().setNextDialogueId(0, 514);
			break;
		case 514:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"How am I doing so far?|Who are you?|Where are you?|Got any tips for me|Nothing really.");
			player.getInterfaceState().setNextDialogueId(0, 515);
			player.getInterfaceState().setNextDialogueId(1, 516);
			player.getInterfaceState().setNextDialogueId(2, 517);
			player.getInterfaceState().setNextDialogueId(3, 518);
			player.getInterfaceState().setNextDialogueId(4, 519);
			break;
		case 515:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
					"How am I doing so far?");
			player.getInterfaceState().setNextDialogueId(0, 520);
			break;
		case 516:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
					"Who are you?");
			player.getInterfaceState().setNextDialogueId(0, 521);
			break;
		case 517:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
					"Where are you?");
			player.getInterfaceState().setNextDialogueId(0, 522);
			break;
		case 518:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
					"Got any tips for me?");
			player.getInterfaceState().setNextDialogueId(0, 523);
			break;
		case 519:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
					"Nothing really.");
			player.getInterfaceState().setNextDialogueId(0, 73);
			break;
		case 520:
			if (player.getSlayer().getSlayerTask() != null) {
				player.getActionSender()
						.sendDialogue("Vannaka", DialogueType.NPC, 403, FacialAnimation.HAPPY,
								"You're current assigned to kill " + player.getSlayer().getSlayerTask().getName()
										+ "; only " + player.getSlayer().getSlayerTask().getTaskAmount() + " more",
								"to go.");
				player.getInterfaceState().setNextDialogueId(0, 514);
			} else {
				player.getActionSender().sendDialogue("Vannaka", DialogueType.NPC, 403, FacialAnimation.HAPPY,
						"You currently have no task, come to me so I can assign you one.");
				player.getInterfaceState().setNextDialogueId(0, 514);
			}
			break;
		case 521:
			player.getActionSender().sendDialogue("Vannaka", DialogueType.NPC, 403, FacialAnimation.HAPPY,
					"My name's Vannaka; I'm a Slayer Master.");
			player.getInterfaceState().setNextDialogueId(0, 514);
			break;
		case 522:
			player.getActionSender().sendDialogue("Vannaka", DialogueType.NPC, 403, FacialAnimation.HAPPY,
					"You'll find me at " + Constants.SERVER_NAME
							+ "'s home area.<br>I'll be here when you need a new task.");
			player.getInterfaceState().setNextDialogueId(0, 514);
			break;
		case 523:
			player.getActionSender().sendDialogue("Vannaka", DialogueType.NPC, 403, FacialAnimation.HAPPY,
					"At the moment, no.");
			player.getInterfaceState().setNextDialogueId(0, 514);
			break;

		case 822:
			player.getActionSender().sendDialogue("Oziach", DialogueType.NPC, 822, FacialAnimation.HAPPY,
					"Hello, What can I do for you?");
			player.getInterfaceState().setNextDialogueId(0, 823);
			break;
		case 823:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Can you make a dragonfire shield for me?|Nothing.");
			player.getInterfaceState().setNextDialogueId(0, 824);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 824:
			boolean hasItems = player.getInventory().contains(11286) && player.getInventory().contains(1540);
			if (hasItems) {
				player.getActionSender().sendDialogue("Oziach", DialogueType.NPC, 822, FacialAnimation.HAPPY,
						"Sure! It'll cost you 750,000 coins. Are you sure you want to pay this?");
				player.getInterfaceState().setNextDialogueId(0, 825);
			} else {
				player.getActionSender().sendDialogue("Oziach", DialogueType.NPC, 822, FacialAnimation.HAPPY,
						"You don't seem to have the visage and anti-fire shield with you.");
				player.getInterfaceState().setNextDialogueId(0, -1);
			}
			break;
		case 825:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes please|No");
			player.getInterfaceState().setNextDialogueId(0, 826);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 826:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
					"Yes please");
			player.getInterfaceState().setNextDialogueId(0, 827);
			break;
		case 827:
			hasItems = player.getInventory().contains(11286) && player.getInventory().contains(1540)
					&& player.getInventory().getCount(995) >= 750000;
			if (hasItems) {
				player.getActionSender().removeChatboxInterface();
				player.getInventory().remove(new Item(11286, 1));
				player.getInventory().remove(new Item(1540, 1));
				player.getInventory().remove(new Item(995, 750000));
				player.getInventory().add(new Item(11283, 1));
				player.getActionSender().sendMessage("Oziach takes the items and combines them for you.");
			} else {
				player.getActionSender().sendDialogue("Oziach", DialogueType.NPC, 822, FacialAnimation.HAPPY,
						"You don't seem to have all the required items, Come back when you have them.");
				player.getInterfaceState().setNextDialogueId(0, -1);
			}
			break;

		/**
		 * Bardur - Yaks food trader
		 */
		case 2263:
			player.getActionSender().sendDialogue("Bardur", DialogueType.NPC, 2263, FacialAnimation.DEFAULT,
					"Hey there, adventurer, have you managed to get any equipment from those yaks over there?");
			player.getInterfaceState().setNextDialogueId(0, 2264);
			break;
		case 2264:
			if (player.getInventory().containsOneItem(3757, 3748, 3758)) {
				player.getActionSender().sendDialogue("Trade fremennik equipment for food?", DialogueType.OPTION, -1,
						FacialAnimation.DEFAULT, "Yeah, sure|No thank you");
				player.getInterfaceState().setNextDialogueId(0, 2269);
				player.getInterfaceState().setNextDialogueId(1, 2268);
				return;
			}
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.SAD,
					"No I haven't, what's in it for me if I do find some?");
			player.getInterfaceState().setNextDialogueId(0, 2266);
			break;
		case 2266:
			player.getActionSender().sendDialogue("Bardur", DialogueType.NPC, 2263, FacialAnimation.DEFAULT,
					"I'll give you up to 15 cooked sharks for each item you bring me!");
			player.getInterfaceState().setNextDialogueId(0, 2267);
			break;
		case 2267:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"That's a generoues deal, I'll make sure to bring you any items I find.");
			player.getInterfaceState().setNextDialogueId(0, -1);
			break;
		case 2268:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"No thank you, I'll keep MY gear.");
			player.getInterfaceState().setNextDialogueId(0, -1);
			break;
		case 2269:
			int blades = player.getInventory().getCount(3757);
			int helms = player.getInventory().getCount(3748);
			int shields = player.getInventory().getCount(3758);
			int total = blades + helms + shields;
			if (total > 0) {
				player.getActionSender().sendDialogue("Bardur", DialogueType.NPC, 2263, FacialAnimation.HAPPY,
						"Thanks mate, here are your sharks as promised!");
				player.getInterfaceState().setNextDialogueId(0, 2270);
				return;
			}
			player.getActionSender().sendDialogue("Bardur", DialogueType.NPC, 2263, FacialAnimation.ANGER_1,
					"You don't seem to have any items I want..");
			player.getInterfaceState().setNextDialogueId(0, -1);
			break;
		case 2270:
			blades = player.getInventory().getCount(3757);
			helms = player.getInventory().getCount(3748);
			shields = player.getInventory().getCount(3758);
			total = blades + helms + shields;
			player.getInventory().remove(new Item(3757, blades));
			player.getInventory().remove(new Item(3748, helms));
			player.getInventory().remove(new Item(3758, shields));
			Inventory.addDroppable(player, new Item(386, total * 15));
			player.getActionSender().sendItemDialogue(385,
					"Bardur gave you " + total * 15 + " x Cooked Shark for your Fremennik equipment.");
			break;
		/**
		 * Warriors guild kamfreena
		 */
		case 2461:
			String message = "You'll need at least 100 warriors guild tokens to enter the Cyclops room. "
					+ "Once you have those, simply go through these doors and start killing the Cyclopses inside.";
			player.getActionSender().sendDialogue("Kamfreena", DialogueType.NPC, 2461, FacialAnimation.HAPPY, message);
			player.getInterfaceState().setNextDialogueId(0, 2462);
			break;
		case 2462:
			message = "Defender drops will go straight into your inventory or dropped on the ground under you if "
					+ "not enough space in your inventory. Also.. 10 of your guild tokens will be removed every "
					+ "minute you're in there.";
			player.getActionSender().sendDialogue("Kamfreena", DialogueType.NPC, 2461, FacialAnimation.HAPPY, message);
			player.getInterfaceState().setNextDialogueId(0, 2463);
			break;
		case 2463:
			message = "Ah, understood. Do I have to come check in with you if I get a defender?";
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, 2461,
					FacialAnimation.DISINTERESTED, message);
			player.getInterfaceState().setNextDialogueId(0, 2464);
			break;
		case 2464:
			message = "There's no need for that. You can just stay inside the room until you get up to a Dragon defender.<br>"
					+ "Good luck, " + player.getName() + "!";
			player.getActionSender().sendDialogue("Kamfreena", DialogueType.NPC, 2461, FacialAnimation.HAPPY, message);
			player.getInterfaceState().setNextDialogueId(0, -1);
			break;

		case 1603:
			if (player.getSettings().completedMageArena()) {
				player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
						"Hello, Kolodion.");
				player.getInterfaceState().setNextDialogueId(0, 1621);
			} else {
				player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
						"Hello there. What is this place?");
				player.getInterfaceState().setNextDialogueId(0, 1604);
			}
			break;
		case 1604:
			player.getActionSender().sendDialogue("Kolodion", DialogueType.NPC, 1603, FacialAnimation.HAPPY,
					"I am the great Kolodion, master of battle magic, and<br>this is my battle arena. Top wizards travel from all over<br>Survival to fight here.");
			player.getInterfaceState().setNextDialogueId(0, 1605);
			break;
		case 1605:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Can I fight here?|Fairwell!");
			player.getInterfaceState().setNextDialogueId(0, 1606);
			player.getInterfaceState().setNextDialogueId(1, 1607);
			break;
		case 1606:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
					"Can I fight here?");
			player.getInterfaceState().setNextDialogueId(0, 1608);
			break;
		case 1607:
			player.getActionSender().removeChatboxInterface();
			break;
		case 1608:
			player.getActionSender().sendDialogue("Kolodion", DialogueType.NPC, 1603, FacialAnimation.HAPPY,
					"My arena is open to any high level wizard, but this is<br>no game. Many wizards fall in this arena, never to rise<br>again. The strongest mages have been destroyed.");
			player.getInterfaceState().setNextDialogueId(0, 1609);
			break;
		case 1609:
			player.getActionSender().sendDialogue("Kolodion", DialogueType.NPC, 1603, FacialAnimation.HAPPY,
					"If you're sure you want in?");
			player.getInterfaceState().setNextDialogueId(0, 1610);
			break;
		case 1610:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes indeedy.|No I don't.");
			player.getInterfaceState().setNextDialogueId(0, 1611);
			player.getInterfaceState().setNextDialogueId(1, 1607);
			break;
		case 1611:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
					"Yes indeedy.");
			player.getInterfaceState().setNextDialogueId(0, 1612);
			break;
		case 1612:
			player.getActionSender().sendDialogue("Kolodion", DialogueType.NPC, 1603, FacialAnimation.HAPPY,
					"Good, good. You have a healthy sense of competition.");
			player.getInterfaceState().setNextDialogueId(0, 1613);
			break;
		case 1613:
			player.getActionSender().sendDialogue("Kolodion", DialogueType.NPC, 1603, FacialAnimation.HAPPY,
					"Remember, traveller - in my arena, hand-to-hand<br>combat is useless. Your strength will diminish as you<br>enter the arena, but the spells you can learn are<br>amonst the most powerful in all of Survival.");
			player.getInterfaceState().setNextDialogueId(0, 1614);
			break;
		case 1614:
			player.getActionSender().sendDialogue("Kolodion", DialogueType.NPC, 1603, FacialAnimation.HAPPY,
					"Before I can accept you in, we must duel.");
			player.getInterfaceState().setNextDialogueId(0, 1615);
			break;
		case 1615:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Okay, let's fight.|No thanks.");
			player.getInterfaceState().setNextDialogueId(0, 1616);
			player.getInterfaceState().setNextDialogueId(1, 1607);
			break;
		case 1616:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
					"Okay, let's fight.");
			player.getInterfaceState().setNextDialogueId(0, 1617);
			break;
		case 1617:
			player.getActionSender().sendDialogue("Kolodion", DialogueType.NPC, 1603, FacialAnimation.HAPPY,
					"I must first check that you are up to scratch.");
			if (player.getSkills().getLevelForExperience(Skills.MAGIC) > 59) {
				player.getInterfaceState().setNextDialogueId(0, 1618);
			} else {
				player.getInterfaceState().setNextDialogueId(0, 1626);
			}
			break;
		case 1618:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
					"You don't need to worry about that.");
			player.getInterfaceState().setNextDialogueId(0, 1619);
			break;
		case 1619:
			player.getActionSender().sendDialogue("Kolodion", DialogueType.NPC, 1603, FacialAnimation.HAPPY,
					"Not just any magician can enter - only the most<br>powerfl and most feared. Before you can use the<br>power of this arena, you must prove yourself against me.");
			player.getInterfaceState().setNextDialogueId(0, 1620);
			break;
		case 1620:
			player.getActionSender().removeChatboxInterface();
			player.getMageArena().start();
			break;
		case 1621:
			player.getActionSender().sendDialogue("Kolodion", DialogueType.NPC, 1603, FacialAnimation.HAPPY,
					"Hello, young mage. You're a tough one.");
			player.getInterfaceState().setNextDialogueId(0, 1622);
			break;
		case 1622:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
					"What now?");
			player.getInterfaceState().setNextDialogueId(0, 1623);
			break;
		case 1623:
			player.getActionSender().sendDialogue("Kolodion", DialogueType.NPC, 1603, FacialAnimation.HAPPY,
					"Step into the magic pool. It will take you to a chamber.<br>There, you must decide which god you will represent in<br>the arena.");
			player.getInterfaceState().setNextDialogueId(0, 1624);
			break;
		case 1624:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.HAPPY,
					"Thanks, Kolodion");
			player.getInterfaceState().setNextDialogueId(0, 1625);
			break;
		case 1625:
			player.getActionSender().sendDialogue("Kolodion", DialogueType.NPC, 1603, FacialAnimation.HAPPY,
					"That's what I'm here for.");
			player.getInterfaceState().setNextDialogueId(0, 1607);
			break;
		case 1626:
			player.getActionSender().sendDialogue("Kolodion", DialogueType.NPC, 1603, FacialAnimation.HAPPY,
					"You don't seem to be a powerful enough magician yet.");
			player.getInterfaceState().setNextDialogueId(0, 1607);
			break;
		case 1712:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Edgeville|Karamja|Draynor Village|Al Kharid|Nothing.");
			player.getInterfaceState().setNextDialogueId(0, 1713);
			player.getInterfaceState().setNextDialogueId(1, 1714);
			player.getInterfaceState().setNextDialogueId(2, 1715);
			player.getInterfaceState().setNextDialogueId(3, 1716);
			player.getInterfaceState().setNextDialogueId(4, 1717);
			break;
		case 1713:
			player.getJewellery().gemTeleport(player, Location.create(3089, 3496, 0));
			player.getActionSender().removeChatboxInterface();
			break;
		case 1714:
			player.getJewellery().gemTeleport(player, Location.create(2918, 3176, 0));
			player.getActionSender().removeChatboxInterface();
			break;
		case 1715:
			player.getJewellery().gemTeleport(player, Location.create(3105, 3249, 0));
			player.getActionSender().removeChatboxInterface();
			break;
		case 1716:
			player.getJewellery().gemTeleport(player, Location.create(3293, 3163, 0));
			player.getActionSender().removeChatboxInterface();
			break;
		case 1717:
			player.getActionSender().removeChatboxInterface();
			break;
		case 1718:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Burthorpe Games Room|Barbarian Outpost|Nowhere");
			player.getInterfaceState().setNextDialogueId(0, 1719);
			player.getInterfaceState().setNextDialogueId(1, 1720);
			player.getInterfaceState().setNextDialogueId(2, 1721);
			break;
		case 1719:
			player.getJewellery().gemTeleport(player, Location.create(2926, 3559, 0));
			player.getActionSender().removeAllInterfaces().removeChatboxInterface();
			break;
		case 1720:
			player.getJewellery().gemTeleport(player, Location.create(2525, 3576, 0));
			player.getActionSender().removeAllInterfaces().removeChatboxInterface();
			break;
		case 1721:
			player.getActionSender().removeAllInterfaces().removeChatboxInterface();
			break;
		case 1722:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Al-Kharid Duel Arena|Castle wars Arena|Clan wars Arena|Nowhere");
			player.getInterfaceState().setNextDialogueId(0, 1724);
			player.getInterfaceState().setNextDialogueId(1, 1723);
			player.getInterfaceState().setNextDialogueId(2, 1725);
			player.getInterfaceState().setNextDialogueId(3, 1726);
			break;
		case 1723:
			player.getJewellery().gemTeleport(player, Location.create(2440, 3089, 0));
			player.getActionSender().removeAllInterfaces().removeChatboxInterface();
			break;
		case 1724:
			player.getJewellery().gemTeleport(player, Location.create(3316, 3235, 0));
			player.getActionSender().removeAllInterfaces().removeChatboxInterface();
			break;
		case 1725:
			player.getJewellery().gemTeleport(player, Location.create(3369, 3169, 0));
			player.getActionSender().removeAllInterfaces().removeChatboxInterface();
			break;
		case 1726:
			player.getActionSender().removeAllInterfaces().removeChatboxInterface();
			break;

		case 1755:
			Item item = player.getInterfaceAttribute("ring");
			if (item == null) {
				player.getActionSender().sendDialogue("Void Knight", DialogueType.NPC, 1755, FacialAnimation.HAPPY,
						"Sorry, I cannot upgrade this item for you.");
				player.getInterfaceState().setNextDialogueId(1, -1);
				return;
			}
			Optional<Constants.UPGRADABLE_ITEMS> upgradable = Constants.UPGRADABLE_ITEMS.of(item.getId());
			if (!upgradable.isPresent()) {
				player.getActionSender().sendDialogue("Void Knight", DialogueType.NPC, 1755, FacialAnimation.HAPPY,
						"Sorry, I cannot upgrade this item for you.");
				return;
			}
			player.getActionSender().sendDialogue("Void Knight", DialogueType.NPC, 1755, FacialAnimation.HAPPY,
					"Would you like to upgrade your " + item.getDefinition2().getName() + " for "
							+ upgradable.get().getPoints() + " Pest Control Points?");
			player.getInterfaceState().setNextDialogueId(0, 1756);
			break;
		case 1756:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes|No");
			player.getInterfaceState().setNextDialogueId(0, 1757);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 1757:
			item = player.getInterfaceAttribute("ring");
			upgradable = Constants.UPGRADABLE_ITEMS.of(item.getId());
			int pcPoints = player.getDatabaseEntity().getStatistics().getPestControlPoints();
			if (pcPoints < upgradable.get().getPoints()) {
				player.getActionSender().sendDialogue("Void Knight", DialogueType.NPC, 1755, FacialAnimation.ANGER_1,
						"You don't seem to have enough points to upgrade.");
			} else {
				if (player.getInventory().hasItem(item)) {
					upgradable = Constants.UPGRADABLE_ITEMS.of(item.getId());
					if (upgradable.isPresent()) {
						player.getInventory().remove(item);
						player.getInventory().add(new Item(upgradable.get().getImbued()));
						player.getDatabaseEntity().getStatistics()
								.setPestControlPoints(pcPoints - upgradable.get().getPoints());
						player.getActionSender().sendDialogue("Void Knight", DialogueType.NPC, 1755,
								FacialAnimation.ANGER_1, "I've upgraded your " + item.getDefinition2().getName()
										+ " for " + upgradable.get().getPoints() + " points!");
					} else
						player.getActionSender().removeChatboxInterface();
				} else
					player.getActionSender().removeChatboxInterface();
			}
			player.getInterfaceState().setNextDialogueId(0, -1);
			break;
		case 2000:
			boolean trimmed = player.trimmed();
			boolean skillMaster = player.getSkills()
					.getLevelForExperience(Skills.getSkillId((int) player.getAttribute("talkingNpc"))) == 99;
			Item cape = null, hood = null;
			switch ((int) player.getAttribute("talkingNpc")) {
			case 2460:// ajjat
				player.getActionSender().sendDialogue("Ajjat", DialogueType.NPC, 2460, FacialAnimation.HAPPY,
						skillMaster ? "You seem to be a master of the Attack skill do you want a skillcape for 99k?"
								: "Come speak to me when you're a master in attack.");
				cape = new Item(trimmed ? 9748 : 9747, 1);
				hood = new Item(9749, 1);
				break;
			case 3216:// melee tutor
				player.getActionSender()
						.sendDialogue("Melee combat tutor", DialogueType.NPC, 3216, FacialAnimation.HAPPY, skillMaster
								? "You seem to be a master of the Defence skill do you want a skillcape for 99k?"
								: "Come speak to me when you're a master in defence.");
				cape = new Item(trimmed ? 9754 : 9753, 1);
				hood = new Item(9755, 1);
				break;
			case 2473:// sloane
				player.getActionSender()
						.sendDialogue("Sloane", DialogueType.NPC, 2473, FacialAnimation.HAPPY, skillMaster
								? "You seem to be a master of the Strength skill do you want a skillcape for 99k?"
								: "Come speak to me when you're a master in strength.");
				cape = new Item(trimmed ? 9751 : 9750, 1);
				hood = new Item(9752, 1);
				break;
			case 6059:// armour salesman
				player.getActionSender().sendDialogue("Armour salesman", DialogueType.NPC, 6059, FacialAnimation.HAPPY,
						skillMaster ? "You seem to be a master of the Ranged skill do you want a skillcape for 99k?"
								: "Come speak to me when you're a master in ranged.");
				cape = new Item(trimmed ? 9757 : 9756, 1);
				hood = new Item(9758, 1);
				break;
			case 2578:// brother jered
				player.getActionSender().sendDialogue("Brother Jered", DialogueType.NPC, 2578, FacialAnimation.HAPPY,
						skillMaster ? "You seem to be a master of the Prayer skill do you want a skillcape for 99k?"
								: "Come speak to me when you're a master in hitpoints.");
				cape = new Item(trimmed ? 9759 : 9760, 1);
				hood = new Item(9761, 1);
				break;
			case 2658:// head chef
				player.getActionSender()
						.sendDialogue("Head Chef", DialogueType.NPC, 2658, FacialAnimation.HAPPY, skillMaster
								? "You seem to be a master of the Cooking skill do you want a skillcape for 99k?"
								: "Come speak to me when you're a master in cooking.");
				cape = new Item(trimmed ? 9802 : 9801, 1);
				hood = new Item(9803, 1);
				break;
			case 1044:// hickton
				player.getActionSender()
						.sendDialogue("Hickton", DialogueType.NPC, 1044, FacialAnimation.HAPPY, skillMaster
								? "You seem to be a master of the Fletching skill do you want a skillcape for 99k?"
								: "Come speak to me when you're a master in fletching.");
				cape = new Item(trimmed ? 9784 : 9783, 1);
				hood = new Item(9785, 1);
				break;
			case 118:// ignatius vulcan
				player.getActionSender()
						.sendDialogue("Ignatius Vulcan", DialogueType.NPC, 118, FacialAnimation.HAPPY, skillMaster
								? "You seem to be a master of the Firemaking skill do you want a skillcape for 99k?"
								: "Come speak to me when you're a master in firemaking.");
				cape = new Item(trimmed ? 9805 : 9804, 1);
				hood = new Item(9806, 1);
				break;
			case 5045:// Kaqemeex
				if (skillMaster) {
					player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1,
							FacialAnimation.DEFAULT, "Can I see your shop?|Can I purchase a cape of Herblore");
					player.getInterfaceState().setNextDialogueId(0, 2003);
					player.getInterfaceState().setNextDialogueId(1, 2004);
				} else {
					player.getActionSender().removeChatboxInterface();
					Shop.open(player, 29, 0);
				}
				return;
			case 3193:// Martin Thwait
				player.getActionSender()
						.sendDialogue("Martin Thwait", DialogueType.NPC, 3193, FacialAnimation.HAPPY, skillMaster
								? "You seem to be a master of the Thieving skill do you want a skillcape for 99k?"
								: "Come speak to me when you're a master in thieving.");
				cape = new Item(trimmed ? 9778 : 9777, 1);
				hood = new Item(9779, 1);
				break;
			case 5810:// Master Crafter
				player.getActionSender()
						.sendDialogue("Master Crafter", DialogueType.NPC, 5810, FacialAnimation.HAPPY, skillMaster
								? "You seem to be a master of the Crafting skill do you want a skillcape for 99k?"
								: "Come speak to me when you're a master in crafting.");
				cape = new Item(trimmed ? 9781 : 9780, 1);
				hood = new Item(9782, 1);
				break;
			case 2913:// Master fisher
				player.getActionSender()
						.sendDialogue("Master fisher", DialogueType.NPC, 2913, FacialAnimation.HAPPY, skillMaster
								? "You seem to be a master of the Fishing skill do you want a skillcape for 99k?"
								: "Come speak to me when you're a master in fishing.");
				cape = new Item(trimmed ? 9799 : 9798, 1);
				hood = new Item(9800, 1);
				break;
			case 3249:// Robe Store owner
				player.getActionSender().sendDialogue("Robe Store owner", DialogueType.NPC, 3249, FacialAnimation.HAPPY,
						skillMaster ? "You seem to be a master of the Magic skill do you want a skillcape for 99k?"
								: "Come speak to me when you're a master in magic.");
				cape = new Item(trimmed ? 9763 : 9762, 1);
				hood = new Item(9764, 1);
				break;
			case 3343:// Surgeon General Tafani
				player.getActionSender().sendDialogue("Surgeon General Tafani", DialogueType.NPC, 3343,
						FacialAnimation.HAPPY,
						skillMaster ? "You seem to be a master of the Hitpoints skill do you want a skillcape for 99k?"
								: "Come speak to me when you're a master in hitpoints.");
				cape = new Item(trimmed ? 9769 : 9768, 1);
				hood = new Item(9770, 1);
				break;
			case 4733:// Thurgo
				player.getActionSender()
						.sendDialogue("Thurgo", DialogueType.NPC, 4733, FacialAnimation.HAPPY, skillMaster
								? "You seem to be a master of the Smithing skill do you want a skillcape for 99k?"
								: "Come speak to me when you're a master in smithing.");
				cape = new Item(trimmed ? 9796 : 9795, 1);
				hood = new Item(9797, 1);
				break;
			case 3226:// Woodsman tutor
				player.getActionSender().sendDialogue("Woodsman tutor", DialogueType.NPC, 3226, FacialAnimation.HAPPY,
						skillMaster
								? "You seem to be a master of the Woodcutting skill do you want a skillcape for 99k?"
								: "Come speak to me when you're a master in woodcutting.");
				cape = new Item(trimmed ? 9808 : 9807, 1);
				hood = new Item(9809, 1);
				break;
			case 405:// Duradel
				player.getActionSender().sendDialogue("Duradel", DialogueType.NPC, 405, FacialAnimation.HAPPY,
						skillMaster ? "You seem to be a master of the Slayer skill do you want a skillcape for 99k?"
								: "Come speak to me when you're a master in slayer.");
				cape = new Item(trimmed ? 9787 : 9786, 1);
				hood = new Item(9788, 1);
				break;
			case 3363:
				player.getActionSender().sendDialogue("Dwarf", DialogueType.NPC, 3363, FacialAnimation.HAPPY,
						skillMaster ? "You seem to be a master of the Mining skill do you want a skillcape for 99k?"
								: "Come speak to me when you're a master in mining.");
				cape = new Item(trimmed ? 9793 : 9792, 1);
				hood = new Item(9794, 1);
				break;
			case 5832:
				player.getActionSender().sendDialogue("Martin the Master Gardener", DialogueType.NPC, 5832,
						FacialAnimation.HAPPY,
						skillMaster ? "You seem to be a master of the Farming skill do you want a skillcape for 99k?"
								: "Come speak to me when you're a master in farming.");
				cape = new Item(trimmed ? 9811 : 9810, 1);
				hood = new Item(9812, 1);
				break;
			case 637:
				player.getActionSender().sendDialogue("Aubury", DialogueType.NPC, 637, FacialAnimation.HAPPY,
						skillMaster
								? "You seem to be a master of the Runecrafting skill do you want a skillcape for 99k?"
								: "Come speak to me when you're a master in runecrafting.");
				cape = new Item(trimmed ? 9766 : 9765, 1);
				hood = new Item(9767, 1);
				break;
			}
			player.getInterfaceState().setNextDialogueId(0, skillMaster ? 2001 : -1);
			player.setAttribute("cape", cape);
			player.setAttribute("hood", hood);
			break;
		case 2001:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes|No.");
			player.getInterfaceState().setNextDialogueId(0, 2002);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 2002:
			player.getActionSender().removeChatboxInterface();
			boolean hasGold = player.getInventory().getCount(995) > 99000;
			if (hasGold) {
				if (player.hasAttribute("cape") && player.hasAttribute("hood")) {
					if (player.getInventory().add(player.getAttribute("cape"))
							&& player.getInventory().add(player.getAttribute("hood"))) {
						player.getInventory().remove(new Item(995, 99000));
						player.getActionSender().sendMessage("You purchase an attack skillcape for 99k");
					}
					player.removeAttribute("cape");
					player.removeAttribute("hood");
				}
			} else {
				player.getActionSender().sendMessage("Not enough coins to purchase this.");
			}
			break;
		case 2003:
			player.getActionSender().removeChatboxInterface();
			Shop.open(player, 29, 0);
			break;
		case 2004:
			trimmed = player.trimmed();
			player.getActionSender().sendDialogue("Kaqemeex", DialogueType.NPC, 5045, FacialAnimation.HAPPY,
					"You seem to be a master of the Herblore skill do you want a skillcape for 99k?");
			cape = new Item(trimmed ? 9775 : 9774, 1);
			hood = new Item(9776, 1);
			player.getInterfaceState().setNextDialogueId(0, 2001);
			player.setAttribute("cape", cape);
			player.setAttribute("hood", hood);
			break;

		case 2180:
			player.getActionSender().sendDialogue("Tzhaar-Mej-Jal", DialogueType.NPC, 2180, FacialAnimation.HAPPY,
					"Hello would you like to gamble your fire capes?");
			player.getInterfaceState().setNextDialogueId(0, 2181);
			break;
		case 2181:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes|No.");
			player.getInterfaceState().setNextDialogueId(0, 2182);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 2182:
			player.getActionSender().sendDialogue("Tzhaar-Mej-Jal", DialogueType.NPC, 2180, FacialAnimation.HAPPY,
					"How many would you like to gamble?");
			player.getInterfaceState().setNextDialogueId(0, 2183);
			break;
		case 2183:
			player.getActionSender().removeChatboxInterface();
			player.getActionSender().sendEnterAmountInterface();
			player.setInterfaceAttribute("gamble_firecape", true);
			break;
		case 550:
			player.getActionSender().sendDialogue("Overseer", DialogueType.NPC, 5886, FacialAnimation.HAPPY,
					"Hello, human. Bring me your Bludgeon axon, bludgeon spine, and your bludgeon claw.");
			player.getInterfaceState().setNextDialogueId(0, 551);
			break;
		case 551:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Okay!|No.");
			player.getInterfaceState().setNextDialogueId(0, 552);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 552:
			player.getActionSender().removeChatboxInterface();
			if (player.getInventory().contains(13275) && player.getInventory().contains(13276)
					&& player.getInventory().contains(13274)) {
				player.getInventory().remove(new Item(13275));
				player.getInventory().remove(new Item(13276));
				player.getInventory().remove(new Item(13274));
				player.getInventory().add(new Item(13263));
				player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 13263, null,
						"The Overseer merges your parts together and Hands you an Abyssal Bludgeon.");
			}
			break;
		case 660:
			player.getActionSender().sendDialogue(" ", DialogueType.MESSAGE_MODEL_LEFT, 13273, FacialAnimation.HAPPY,
					"You place the Unsired into the Font of Consumption...");
			player.getInterfaceState().setNextDialogueId(0, 661);
			break;
		case 661:
			player.getActionSender().sendDialogue(" ", DialogueType.MESSAGE_MODEL_LEFT, 13273, FacialAnimation.HAPPY,
					"The Font consumes the Unsired and returns you a reward.");
			break;

		/* Ranged combat tutor */
		case 1349:
			player.getActionSender().sendDialogue("Ranged combat tutor", DialogueType.NPC, 3217,
					FacialAnimation.DEFAULT,
					"Here's your ranged starter kit. You only receive one; so put it go good use.");
			break;
		case 1350:
			player.getActionSender().sendDialogue("Ranged combat tutor", DialogueType.NPC, 3217,
					FacialAnimation.DEFAULT, "You have already received your ranged starter kit.");
			break;

		/* Magic combat tutor */
		case 1351:
			player.getActionSender().sendDialogue("Magic combat tutor", DialogueType.NPC, 3218, FacialAnimation.DEFAULT,
					"Here's your magic starter kit. You only receive one; so put it to good use.");
			break;
		case 1352:
			player.getActionSender().sendDialogue("Magic combat tutor", DialogueType.NPC, 3218, FacialAnimation.DEFAULT,
					"You have already received your magic starter kit.");
			break;
		case 1353:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Slayer Cave|Cave Horrors|Abyssal Demons");
			player.getInterfaceState().setNextDialogueId(0, 1354);
			player.getInterfaceState().setNextDialogueId(1, 1355);
			player.getInterfaceState().setNextDialogueId(2, 1356);
			player.getInterfaceState().setNextDialogueId(3, 1357);
			break;
		case 1354:
			player.getJewellery().gemTeleport(player, Location.create(2438, 9822, 0));
			player.getActionSender().removeAllInterfaces().removeChatboxInterface();
			break;
		case 1355:
			player.getJewellery().gemTeleport(player, Location.create(3747, 9374, 0));
			player.getActionSender().removeAllInterfaces().removeChatboxInterface();
			break;
		case 1356:
			player.getJewellery().gemTeleport(player, Location.create(3424, 3549, 2));
			player.getActionSender().removeAllInterfaces().removeChatboxInterface();
			break;
		case 2040:
			player.getActionSender().sendDialogue("Zul-Areth", DialogueType.NPC, 2040, FacialAnimation.DEFAULT,
					"Hello, How can I help you?");
			player.getInterfaceState().setNextDialogueId(0, 2041);
			break;
		case 2041:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Can I collect my items?|Nothing.");
			player.getInterfaceState().setNextDialogueId(0, 2042);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 2042:
			boolean itemsWaiting = player.getDatabaseEntity().getZulrahState().getItemsLostZulrah().isEmpty();
			int amount = player.isSilverMember() ? 0 : 100000;
			player.getActionSender().sendDialogue("Zul-Areth", DialogueType.NPC, 2040, FacialAnimation.DEFAULT,
					itemsWaiting ? "You currently don't have any items awaiting you."
							: "Absolutely, that'll be "
									+ (amount == 0 ? "free of charge!" : Misc.formatCurrency(amount) + " coins."));
			player.getInterfaceState().setNextDialogueId(0, itemsWaiting ? -1 : 2044);
			break;
		case 2044:
			player.getZulAreth().claimItems();
			break;
		case 2045:
			player.getActionSender().sendDialogue("Zul-Areth", DialogueType.NPC, 2040, FacialAnimation.ANGER_1,
					"Come back when you have enough coins to pay me with!");
			player.getInterfaceState().setNextDialogueId(0, -1);
			break;
		case 2046:
			player.getActionSender().sendDialogue("Zul-Areth", DialogueType.NPC, 2040, FacialAnimation.HAPPY,
					"Pleasure doing business with you!");
			player.getInterfaceState().setNextDialogueId(0, -1);
			break;

		case 2914:
			player.getActionSender().sendDialogue("Otto Godblessed", DialogueType.NPC, 2914, FacialAnimation.CALM_1,
					"Hello how can I help you?");
			player.getInterfaceState().setNextDialogueId(0, 2915);
			break;
		case 2915:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Can you upgrade my spear?|Can you revert my hasta?");
			player.getInterfaceState().setNextDialogueId(0, 2916);
			player.getInterfaceState().setNextDialogueId(1, 2920);
			break;
		case 2916:
			int fee = player.getEquipment().contains(13140) ? 150000 : 300000;
			player.getActionSender().sendDialogue("Otto Godblessed", DialogueType.NPC, 2914, FacialAnimation.CALM_2,
					"For a fee of " + Misc.formatNumber(fee) + " gold coins, is that okay?");
			player.getInterfaceState().setNextDialogueId(0, 2917);
			break;
		case 2917:
			player.getActionSender().sendDialogue("Upgrade zamorakian spear?", DialogueType.OPTION, -1,
					FacialAnimation.DEFAULT, "Yes.|No.");
			player.getInterfaceState().setNextDialogueId(0, 2918);
			player.getInterfaceState().setNextDialogueId(1, 2919);
			break;
		case 2918:
			fee = player.getEquipment().contains(13140) ? 150000 : 300000;
			boolean hasSpear = player.getInventory().contains(11824);
			boolean hasCoins = player.getInventory().getCount(995) >= fee;
			if (!hasSpear)
				player.getActionSender().sendDialogue("Otto Godblessed", DialogueType.NPC, 2914,
						FacialAnimation.ANGER_1,
						"Come back when you actually have a Zamorakian spear for me to work with.");
			else if (!hasCoins)
				player.getActionSender().sendDialogue("Otto Godblessed", DialogueType.NPC, 2914,
						FacialAnimation.ANGER_2, "Come back when you actually have the money to pay me with.");
			else {
				player.getInventory().remove(new Item(995, fee));
				player.getInventory().remove(new Item(11824));
				player.getInventory().add(new Item(11889));
				player.getActionSender().sendDialogue("Otto Godblessed", DialogueType.NPC, 2914, FacialAnimation.HAPPY,
						"There you go! Pleasure doing business with you.");
			}
			player.getInterfaceState().setNextDialogueId(0, -1);
			break;
		case 2919:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"No thank you.");
			player.getInterfaceState().setNextDialogueId(0, -1);
			break;
		case 2920:
			fee = player.getEquipment().contains(13140) ? 150000 : 300000;
			player.getActionSender().sendDialogue("Otto Godblessed", DialogueType.NPC, 2914, FacialAnimation.CALM_2,
					"I can revert your zamorakian hasta back into a zamorakian spear free of charge, though, if you'd like "
							+ "me to upgrade it back into a hasta this will require another fee of "
							+ Misc.formatNumber(fee) + " gold coins.");
			player.getInterfaceState().setNextDialogueId(0, 2921);
			break;
		case 2921:
			player.getActionSender().sendDialogue("Revert zamorakian hasta?", DialogueType.OPTION, -1,
					FacialAnimation.DEFAULT, "Yes.|No.");
			player.getInterfaceState().setNextDialogueId(0, 2922);
			player.getInterfaceState().setNextDialogueId(1, 2919);
			break;
		case 2922:
			if (!player.getInventory().hasItem(new Item(11889))) {
				player.getActionSender().sendDialogue("Otto Godblessed", DialogueType.NPC, 2914,
						FacialAnimation.ANGER_1,
						"It seems that you do not have a zamorakian hasta in your inventory, come back when you do.");
				player.getInterfaceState().setNextDialogueId(0, -1);
				return;
			}
			player.getInventory().replace(11889, 11824);
			player.getActionSender().sendDialogue("Otto Godblessed", DialogueType.NPC, 2914, FacialAnimation.DEFAULT,
					"There you go! Pleasure doing business with you.");
			player.getInterfaceState().setNextDialogueId(0, -1);
			break;

		case 3227:
			player.getActionSender().sendDialogue(CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(),
					DialogueType.NPC, player.getAttribute("talkingNpc"), FacialAnimation.DEFAULT,
					"Good day, how may I help you?");
			player.getInterfaceState().setNextDialogueId(0, 3228);
			break;
		case 3228:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"How do I use the bank?|I'd like to access my bank account please.|I'd like to check my PIN settings.|I'd like to collect items.");
			player.getInterfaceState().setNextDialogueId(0, 3229);
			player.getInterfaceState().setNextDialogueId(1, 3230);
			player.getInterfaceState().setNextDialogueId(2, 3258);
			player.getInterfaceState().setNextDialogueId(3, -1);
			break;
		case 3229:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Using the bank itself.|Using Bank deposit boxes.|What's this PIN thing that people keep talking about?|Goodbye.");
			player.getInterfaceState().setNextDialogueId(0, 3230);
			player.getInterfaceState().setNextDialogueId(1, 3238);
			player.getInterfaceState().setNextDialogueId(2, 3241);
			player.getInterfaceState().setNextDialogueId(3, -1);
			break;
		case 3230:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Using the bank itself. I'm not sure how....?");
			player.getInterfaceState().setNextDialogueId(0, 3231);
			break;
		case 3231:
			player.getActionSender().sendDialogue(CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(),
					DialogueType.NPC, player.getAttribute("talkingNpc"), FacialAnimation.DEFAULT,
					"Speak to any banker and ask to see your bank<br>account. If you have set a PIN you will be asked for<br>it, then all the belongings you have placed in the bank<br>will appear in the window. To withdraw one item, left-");
			player.getInterfaceState().setNextDialogueId(0, 3232);
			break;
		case 3232:
			player.getActionSender().sendDialogue(CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(),
					DialogueType.NPC, player.getAttribute("talkingNpc"), FacialAnimation.DEFAULT, "click on it once.");
			player.getInterfaceState().setNextDialogueId(0, 3233);
			break;
		case 3233:
			player.getActionSender().sendDialogue(CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(),
					DialogueType.NPC, player.getAttribute("talkingNpc"), FacialAnimation.DEFAULT,
					"To withdraw many, right-click on the item and select<br>from the menu. The same for depositing, left-click on<br>the item in your inventory to deposit it in the bank.<br>Right-click on it to deposit many of the same items.");
			player.getInterfaceState().setNextDialogueId(0, 3234);
			break;
		case 3234:
			player.getActionSender().sendDialogue(CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(),
					DialogueType.NPC, player.getAttribute("talkingNpc"), FacialAnimation.DEFAULT,
					"To move things around in your bank: firstly select<br>Swap or Insert as your default moving mode, you can<br>find these buttons on the bank window itself. Then click<br>and drag an item to where you want it to appear.");
			player.getInterfaceState().setNextDialogueId(0, 3235);
			break;
		case 3235:
			player.getActionSender().sendDialogue(CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(),
					DialogueType.NPC, player.getAttribute("talkingNpc"), FacialAnimation.DEFAULT,
					"You may withdraw 'notes' or 'certificates' when the<br>items you are trying to withdraw do not stack in your<br>ivnentory. This will only work for items which are<br>tradable.");
			player.getInterfaceState().setNextDialogueId(0, 3236);
			break;
		case 3236:
			player.getActionSender().sendDialogue(CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(),
					DialogueType.NPC, player.getAttribute("talkingNpc"), FacialAnimation.DEFAULT,
					"For instance, if you wanted to sell 100 logs to another<br>player, they would not fin in one inventory and you<br>would need to do multiple trades. Instead, click the<br>Note button to do withdraw the logs as 'certs' or 'notes'.");
			player.getInterfaceState().setNextDialogueId(0, 3237);
			break;
		case 3237:
			player.getActionSender().sendDialogue(CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(),
					DialogueType.NPC, player.getAttribute("talkingNpc"), FacialAnimation.DEFAULT,
					"then withdraw the items you need.");
			player.getInterfaceState().setNextDialogueId(0, 3229);
			break;
		case 3238:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Using Bank deposit boxes.... what are they?");
			player.getInterfaceState().setNextDialogueId(0, 3239);
			break;
		case 3239:
			player.getActionSender().sendDialogue(CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(),
					DialogueType.NPC, player.getAttribute("talkingNpc"), FacialAnimation.DEFAULT,
					"They look like grey pillars, there's one just over there,<br>Bank Deposit boxes save so much time. If you're<br>simply wanting to deposit a single item, 'Use'<br>it on the deposit box.");
			player.getInterfaceState().setNextDialogueId(0, 3240);
			break;
		case 3240:
			player.getActionSender().sendDialogue(CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(),
					DialogueType.NPC, player.getAttribute("talkingNpc"), FacialAnimation.DEFAULT,
					"Otherwise, simply click once on the box and it will give<br>you a choice of what to deposit in an interface very<br>similiar to the bank itself. Very quick for when you're<br>simply fishing or mining etc.");
			player.getInterfaceState().setNextDialogueId(0, 3229);
			break;
		case 3241:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"What's this PIN thing that people keep talking about?");
			player.getInterfaceState().setNextDialogueId(0, 3242);
			break;
		case 3242:
			player.getActionSender().sendDialogue(CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(),
					DialogueType.NPC, player.getAttribute("talkingNpc"), FacialAnimation.DEFAULT,
					"The PIN - Personal Identification Number - can be<br>set on your bank account to protect the items there in<br>case someone finds out your account password. It<br>consists of four numbers that you remember and tell");
			player.getInterfaceState().setNextDialogueId(0, 3243);
			break;
		case 3243:
			player.getActionSender().sendDialogue(CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(),
					DialogueType.NPC, player.getAttribute("talkingNpc"), FacialAnimation.DEFAULT, "no one.");
			player.getInterfaceState().setNextDialogueId(0, 3244);
			break;
		case 3244:
			player.getActionSender().sendDialogue(CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(),
					DialogueType.NPC, player.getAttribute("talkingNpc"), FacialAnimation.DEFAULT,
					"So if someone did manage to get your password they<br>couldn't steal your items if they were in the bank.");
			player.getInterfaceState().setNextDialogueId(0, 3245);
			break;
		case 3245:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"How do I set my PIN?|How do I remove my PIN?|What happens if I forget my PIN?|I know about the PIN, tell me about the bank.|Goodbye.");
			player.getInterfaceState().setNextDialogueId(0, 3246);
			player.getInterfaceState().setNextDialogueId(1, 3252);
			player.getInterfaceState().setNextDialogueId(2, 3255);
			player.getInterfaceState().setNextDialogueId(3, 3229);
			player.getInterfaceState().setNextDialogueId(4, -1);
			break;
		case 3246:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"How do I set my PIN?");
			player.getInterfaceState().setNextDialogueId(0, 3247);
			break;
		case 3247:
			player.getActionSender().sendDialogue(CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(),
					DialogueType.NPC, player.getAttribute("talkingNpc"), FacialAnimation.DEFAULT,
					"You can set your PIN by talking to any banker, they<br>will allow you to access your bank pin settings. Here<br>you can choose to set your pin and recovery delay.<br>Remember not to set it to anything personal such as");
			player.getInterfaceState().setNextDialogueId(0, 3248);
			break;
		case 3248:
			player.getActionSender().sendDialogue(CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(),
					DialogueType.NPC, player.getAttribute("talkingNpc"), FacialAnimation.DEFAULT,
					"your real life bank PIN or birthday.");
			player.getInterfaceState().setNextDialogueId(0, 3249);
			break;
		case 3249:
			player.getActionSender().sendDialogue(CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(),
					DialogueType.NPC, player.getAttribute("talkingNpc"), FacialAnimation.DEFAULT,
					"The recovery delay is to protect your banked items<br>from account thieves. If someone stole your account<br>and asked to have the PIN deleted, they would have to<br>wait a few days before accessing your bank account to");
			player.getInterfaceState().setNextDialogueId(0, 3250);
			break;
		case 3250:
			player.getActionSender().sendDialogue(CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(),
					DialogueType.NPC, player.getAttribute("talkingNpc"), FacialAnimation.DEFAULT,
					"steal your items. This will give you time to recover<br>your account.");
			player.getInterfaceState().setNextDialogueId(0, 3251);
			break;
		case 3251:
			player.getActionSender().sendDialogue(CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(),
					DialogueType.NPC, player.getAttribute("talkingNpc"), FacialAnimation.DEFAULT,
					"There will also be a delay in actually setting the PIN<br>to be used, this is so that if your account is stolen and<br>a PIN set, you can cancel it before it comes into use!");
			player.getInterfaceState().setNextDialogueId(0, 3245);
			break;
		case 3252:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"How do I remove my PIN?");
			player.getInterfaceState().setNextDialogueId(0, 3253);
			break;
		case 3253:
			player.getActionSender().sendDialogue(CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(),
					DialogueType.NPC, player.getAttribute("talkingNpc"), FacialAnimation.DEFAULT,
					"Talking to any banker will enable you to access your<br>PIN settings. There you can cancel or change your<br>PIN, but you will need to wait for your recovery<br>delay to expire to be able to access your bank. This");
			player.getInterfaceState().setNextDialogueId(0, 3254);
			break;
		case 3254:
			player.getActionSender().sendDialogue(CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(),
					DialogueType.NPC, player.getAttribute("talkingNpc"), FacialAnimation.DEFAULT,
					"can be set in the settings page and will protect your<br>items should your account be stolen.");
			player.getInterfaceState().setNextDialogueId(0, 3245);
			break;
		case 3255:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"What happens if I forget my PIN?");
			player.getInterfaceState().setNextDialogueId(0, 3256);
			break;
		case 3256:
			player.getActionSender().sendDialogue(CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(),
					DialogueType.NPC, player.getAttribute("talkingNpc"), FacialAnimation.DEFAULT,
					"If you find yourself faced with the PIN keypad and<br>you don't know the PIN, just look on the right-hand<br>side for a button marked 'I don't know it'. Click this<br>button. Your PIN will be deleted (after a delay of a");
			player.getInterfaceState().setNextDialogueId(0, 3257);
			break;
		case 3257:
			player.getActionSender().sendDialogue(CacheNPCDefinition.get(player.getAttribute("talkingNpc")).getName(),
					DialogueType.NPC, player.getAttribute("talkingNpc"), FacialAnimation.DEFAULT,
					"few days) and you'll be able to use your bank as<br>before. You may still use the bank deposit box without<br>your PIN.");
			player.getInterfaceState().setNextDialogueId(0, 3245);
			break;
		case 3258:
			BankPinService service = Server.getInjector().getInstance(BankPinService.class);

			player.getActionSender().removeChatboxInterface();
			service.openPinSettingsInterface(player, BankPinServiceImpl.SettingScreenType.DEFAULT);
			break;

		case 5919:
			player.getActionSender().sendDialogue("Grace", DialogueType.NPC, 5919, FacialAnimation.DEFAULT,
					"Hello, How can I help you?");
			player.getInterfaceState().setNextDialogueId(0, 5920);
			break;
		case 5920:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"I'd like to color my graceful clothing.|Nothing.");
			player.getInterfaceState().setNextDialogueId(0, 5921);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 5921:
			player.getActionSender().sendDialogue("Grace", DialogueType.NPC, 5919, FacialAnimation.DEFAULT,
					"What color would you like to upgrade to?");
			player.getInterfaceState().setNextDialogueId(0, 5922);
			break;
		case 5922:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Purple|Blue|Yellow|Red|Green|");
			player.getInterfaceState().setNextDialogueId(0, 5923);
			player.getInterfaceState().setNextDialogueId(1, 5924);
			player.getInterfaceState().setNextDialogueId(2, 5925);
			player.getInterfaceState().setNextDialogueId(3, 5926);
			player.getInterfaceState().setNextDialogueId(4, 5927);
			break;
		case 5923:
			// Purple
			if (player.getInventory().containsItems(Constants.GRACEFUL)
					&& player.getInventory().getCount(11849) >= 90) {
				player.getActionSender().removeChatboxInterface();
				player.getInventory().removeItems(Constants.GRACEFUL);
				player.getInventory().remove(new Item(11849, 90));
				player.getInventory().addItems(Constants.PURPLE_GRACEFUL);
			} else {
				player.getActionSender().sendDialogue("Grace", DialogueType.NPC, 5919, FacialAnimation.DEFAULT,
						"You need a full graceful set and 90 marks of grace to upgrade.");
				player.getInterfaceState().setNextDialogueId(0, -1);
			}
			break;
		case 5924:
			// Blue
			if (player.getInventory().containsItems(Constants.GRACEFUL)
					&& player.getInventory().getCount(11849) >= 90) {
				player.getActionSender().removeChatboxInterface();
				player.getInventory().removeItems(Constants.GRACEFUL);
				player.getInventory().remove(new Item(11849, 90));
				player.getInventory().addItems(Constants.BLUE_GRACEFUL);
			} else {
				player.getActionSender().sendDialogue("Grace", DialogueType.NPC, 5919, FacialAnimation.DEFAULT,
						"You need a full graceful set and 90 marks of grace to upgrade.");
				player.getInterfaceState().setNextDialogueId(0, -1);
			}
			break;
		case 5925:
			// Yellow
			if (player.getInventory().containsItems(Constants.GRACEFUL)
					&& player.getInventory().getCount(11849) >= 90) {
				player.getActionSender().removeChatboxInterface();
				player.getInventory().removeItems(Constants.GRACEFUL);
				player.getInventory().remove(new Item(11849, 90));
				player.getInventory().addItems(Constants.YELLOW_GRACEFUL);
			} else {
				player.getActionSender().sendDialogue("Grace", DialogueType.NPC, 5919, FacialAnimation.DEFAULT,
						"You need a full graceful set and 90 marks of grace to upgrade.");
				player.getInterfaceState().setNextDialogueId(0, -1);
			}
			break;
		case 5926:
			// Red
			if (player.getInventory().containsItems(Constants.GRACEFUL)
					&& player.getInventory().getCount(11849) >= 90) {
				player.getActionSender().removeChatboxInterface();
				player.getInventory().removeItems(Constants.GRACEFUL);
				player.getInventory().remove(new Item(11849, 90));
				player.getInventory().addItems(Constants.RED_GRACEFUL);
			} else {
				player.getActionSender().sendDialogue("Grace", DialogueType.NPC, 5919, FacialAnimation.DEFAULT,
						"You need a full graceful set and 90 marks of grace to upgrade.");
				player.getInterfaceState().setNextDialogueId(0, -1);
			}
			break;
		case 5927:
			// Green
			if (player.getInventory().containsItems(Constants.GRACEFUL)
					&& player.getInventory().getCount(11849) >= 90) {
				player.getActionSender().removeChatboxInterface();
				player.getInventory().removeItems(Constants.GRACEFUL);
				player.getInventory().remove(new Item(11849, 90));
				player.getInventory().addItems(Constants.GREEN_GRACEFUL);
			} else {
				player.getActionSender().sendDialogue("Grace", DialogueType.NPC, 5919, FacialAnimation.DEFAULT,
						"You need a full graceful set and 90 marks of grace to upgrade.");
				player.getInterfaceState().setNextDialogueId(0, -1);
			}
			break;

		case 9948:
			player.getActionSender().sendDialogue("Select a Destination", DialogueType.OPTION, -1,
					FacialAnimation.DEFAULT, "Feldip Hunter area|Wilderness Hunter area");
			player.getInterfaceState().setNextDialogueId(0, 9949);
			player.getInterfaceState().setNextDialogueId(1, 9950);
			break;
		case 9949:
			player.getActionSender().removeChatboxInterface();
			player.teleport(Location.create(2525, 2913, 0), 1, 1, false);
			break;
		case 9950:
			player.getActionSender().removeChatboxInterface();
			player.teleport(Location.create(3143, 3770, 0), 1, 1, false);
			break;

		case 12954:
			int interfaceId1 = 193;

			player.getActionSender().sendInterface(162, 546, interfaceId1, false);
			player.getActionSender().sendAccessMask(1, 193, 2, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 3, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 4, -1, -1);

			player.getActionSender().sendString(interfaceId1, 1, "Would you like to merge these items?");
			player.getActionSender().sendString(interfaceId1, 2, "Click here to continue.");
			player.getActionSender().sendItemOnInterface(193, 0, 12954, 150);

			player.getInterfaceState().setNextDialogueId(0, 12955);
			break;
		case 12955:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes i'd like to merge them.|No.");
			player.getInterfaceState().setNextDialogueId(0, 12956);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 12956:
			player.getActionSender().removeChatboxInterface();
			if (player.getInventory().contains(12954) && player.getInventory().contains(20143)) {
				player.getInventory().remove(new Item(12954));
				player.getInventory().remove(new Item(20143));
				player.getInventory().add(new Item(19722));
				player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 19722, null,
						"You merge the two together to make an Dragon Defender (t).");
				player.getActionSender().sendMessage("You merge the two together to make a Dragon Defender (t).");
			}
			break;

		case 11920:
			int interfaceId2 = 193;

			player.getActionSender().sendInterface(162, 546, interfaceId2, false);
			player.getActionSender().sendAccessMask(1, 193, 2, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 3, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 4, -1, -1);

			player.getActionSender().sendString(interfaceId2, 1, "Would you like to merge these items?");
			player.getActionSender().sendString(interfaceId2, 2, "Click here to continue.");
			player.getActionSender().sendItemOnInterface(193, 0, 11920, 150);

			player.getInterfaceState().setNextDialogueId(0, 11921);
			break;
		case 11921:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes i'd like to merge them.|No.");
			player.getInterfaceState().setNextDialogueId(0, 11922);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 11922:
			player.getActionSender().removeChatboxInterface();
			if (player.getInventory().contains(11920) && player.getInventory().contains(12800)) {
				player.getInventory().remove(new Item(11920));
				player.getInventory().remove(new Item(12800));
				player.getInventory().add(new Item(12797));
				player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 12797, null,
						"You merge the two together to make an Dragon Pickaxe.");
				player.getActionSender().sendMessage("You merge the two together to make a Dragon Pickaxe.");
			}
			break;

		case 11335:
			int interfaceId3 = 193;

			player.getActionSender().sendInterface(162, 546, interfaceId3, false);
			player.getActionSender().sendAccessMask(1, 193, 2, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 3, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 4, -1, -1);

			player.getActionSender().sendString(interfaceId3, 1, "Would you like to merge these items?");
			player.getActionSender().sendString(interfaceId3, 2, "Click here to continue.");
			player.getActionSender().sendItemOnInterface(193, 0, 11335, 150);

			player.getInterfaceState().setNextDialogueId(0, 11336);
			break;
		case 11336:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes i'd like to merge them.|No.");
			player.getInterfaceState().setNextDialogueId(0, 11337);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 11337:
			player.getActionSender().removeChatboxInterface();
			if (player.getInventory().contains(11335) && player.getInventory().contains(12538)) {
				player.getInventory().remove(new Item(11335));
				player.getInventory().remove(new Item(12538));
				player.getInventory().add(new Item(12417));
				player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 12417, null,
						"You merge the two together to make an Dragon fullhelm (g).");
				player.getActionSender().sendMessage("You merge the two together to make a Dragon fullhelm (g).");
			}
			break;

		case 1187:
			int interfaceId4 = 193;

			player.getActionSender().sendInterface(162, 546, interfaceId4, false);
			player.getActionSender().sendAccessMask(1, 193, 2, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 3, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 4, -1, -1);

			player.getActionSender().sendString(interfaceId4, 1, "Would you like to merge these items?");
			player.getActionSender().sendString(interfaceId4, 2, "Click here to continue.");
			player.getActionSender().sendItemOnInterface(193, 0, 1187, 150);

			player.getInterfaceState().setNextDialogueId(0, 1188);
			break;
		case 1188:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes i'd like to merge them.|No.");
			player.getInterfaceState().setNextDialogueId(0, 1189);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 1189:
			player.getActionSender().removeChatboxInterface();
			if (player.getInventory().contains(1187) && player.getInventory().contains(12532)) {
				player.getInventory().remove(new Item(1187));
				player.getInventory().remove(new Item(12532));
				player.getInventory().add(new Item(12418));
				player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 12418, null,
						"You merge the two together to make an Dragon sq shield (g).");
				player.getActionSender().sendMessage("You merge the two together to make a Dragon sq shiel (g).");
			}
			break;

		case 11787:
			int interfaceId5 = 193;

			player.getActionSender().sendInterface(162, 546, interfaceId5, false);
			player.getActionSender().sendAccessMask(1, 193, 2, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 3, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 4, -1, -1);

			player.getActionSender().sendString(interfaceId5, 1, "Would you like to merge these items?");
			player.getActionSender().sendString(interfaceId5, 2, "Click here to continue.");
			player.getActionSender().sendItemOnInterface(193, 0, 11787, 150);

			player.getInterfaceState().setNextDialogueId(0, 11788);
			break;
		case 11788:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes i'd like to merge them.|No.");
			player.getInterfaceState().setNextDialogueId(0, 11789);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 11789:
			player.getActionSender().removeChatboxInterface();
			if (player.getInventory().contains(11787) && player.getInventory().contains(12798)) {
				player.getInventory().remove(new Item(11787));
				player.getInventory().remove(new Item(12798));
				player.getInventory().add(new Item(12795));
				player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 12795, null,
						"You merge the two together to make an Steam Battlestaff.");
				player.getActionSender().sendMessage("You merge the two together to make a Steam Battlestaff.");
			}
			break;

		case 11924:
			int interfaceId6 = 193;

			player.getActionSender().sendInterface(162, 546, interfaceId6, false);
			player.getActionSender().sendAccessMask(1, 193, 2, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 3, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 4, -1, -1);

			player.getActionSender().sendString(interfaceId6, 1, "Would you like to merge these items?");
			player.getActionSender().sendString(interfaceId6, 2, "Click here to continue.");
			player.getActionSender().sendItemOnInterface(193, 0, 11924, 150);

			player.getInterfaceState().setNextDialogueId(0, 11925);
			break;
		case 11925:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes i'd like to merge them.|No.");
			player.getInterfaceState().setNextDialogueId(0, 11926);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 11926:
			player.getActionSender().removeChatboxInterface();
			if (player.getInventory().contains(11924) && player.getInventory().contains(12802)) {
				player.getInventory().remove(new Item(11924));
				player.getInventory().remove(new Item(12802));
				player.getInventory().add(new Item(12806));
				player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 12806, null,
						"You merge the two together to make an Malediction ward.");
				player.getActionSender().sendMessage("You merge the two together to make a Malediction ward.");
			}
			break;

		case 11927:
			int interfaceId7 = 193;

			player.getActionSender().sendInterface(162, 546, interfaceId7, false);
			player.getActionSender().sendAccessMask(1, 193, 2, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 3, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 4, -1, -1);

			player.getActionSender().sendString(interfaceId7, 1, "Would you like to merge these items?");
			player.getActionSender().sendString(interfaceId7, 2, "Click here to continue.");
			player.getActionSender().sendItemOnInterface(193, 0, 11926, 150);

			player.getInterfaceState().setNextDialogueId(0, 11928);
			break;
		case 11928:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes i'd like to merge them.|No.");
			player.getInterfaceState().setNextDialogueId(0, 11929);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 11929:
			player.getActionSender().removeChatboxInterface();
			if (player.getInventory().contains(11926) && player.getInventory().contains(12802)) {
				player.getInventory().remove(new Item(11926));
				player.getInventory().remove(new Item(12802));
				player.getInventory().add(new Item(12807));
				player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 12807, null,
						"You merge the two together to make an Odium ward.");
				player.getActionSender().sendMessage("You merge the two together to make a Odium ward.");
			}
			break;

		case 11930:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes i'd like to merge them.|No.");
			player.getInterfaceState().setNextDialogueId(0, 11931);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 11931:
			player.getActionSender().removeChatboxInterface();
			if (player.getInventory().contains(4153) && player.getInventory().contains(12849)) {
				player.getInventory().remove(new Item(4153));
				player.getInventory().remove(new Item(12849));
				player.getInventory().add(new Item(12848));
				player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 12848, null,
						"You merge the two together to make a Granite maul (or).");
				player.getActionSender().sendMessage("You merge the two together to make a Granite maul (or).");
			}
			break;

		case 4587:
			int interfaceId8 = 193;

			player.getActionSender().sendInterface(162, 546, interfaceId8, false);
			player.getActionSender().sendAccessMask(1, 193, 2, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 3, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 4, -1, -1);

			player.getActionSender().sendString(interfaceId8, 1, "Would you like to merge these items?");
			player.getActionSender().sendString(interfaceId8, 2, "Click here to continue.");
			player.getActionSender().sendItemOnInterface(193, 0, 4587, 150);

			player.getInterfaceState().setNextDialogueId(0, 4588);
			break;
		case 4588:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes i'd like to merge them.|No.");
			player.getInterfaceState().setNextDialogueId(0, 4589);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 4589:
			player.getActionSender().removeChatboxInterface();
			if (player.getInventory().contains(4587) && player.getInventory().contains(20002)) {
				player.getInventory().remove(new Item(4587));
				player.getInventory().remove(new Item(20002));
				player.getInventory().add(new Item(20000));
				player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 20000, null,
						"You merge the two together to make an Dragon Scimitar (or).");
				player.getActionSender().sendMessage("You merge the two together to make a Dragon Scimitar (or).");
			}
			break;

		case 19553:
			int interfaceId9 = 193;

			player.getActionSender().sendInterface(162, 546, interfaceId9, false);
			player.getActionSender().sendAccessMask(1, 193, 2, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 3, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 4, -1, -1);

			player.getActionSender().sendString(interfaceId9, 1, "Would you like to merge these items?");
			player.getActionSender().sendString(interfaceId9, 2, "Click here to continue.");
			player.getActionSender().sendItemOnInterface(193, 0, 19553, 150);

			player.getInterfaceState().setNextDialogueId(0, 19554);
			break;
		case 19554:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes i'd like to merge them.|No.");
			player.getInterfaceState().setNextDialogueId(0, 19555);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 19555:
			player.getActionSender().removeChatboxInterface();
			if (player.getInventory().contains(19553) && player.getInventory().contains(20062)) {
				player.getInventory().remove(new Item(19553));
				player.getInventory().remove(new Item(20062));
				player.getInventory().add(new Item(20366));
				player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 20366, null,
						"You merge the two together to make an Amulet of Torture (or).");
				player.getActionSender().sendMessage("You merge the two together to make a Amulet of Torture (or).");
			}
			break;

		case 12002:
			int interfaceId10 = 193;

			player.getActionSender().sendInterface(162, 546, interfaceId10, false);
			player.getActionSender().sendAccessMask(1, 193, 2, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 3, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 4, -1, -1);

			player.getActionSender().sendString(interfaceId10, 1, "Would you like to merge these items?");
			player.getActionSender().sendString(interfaceId10, 2, "Click here to continue.");
			player.getActionSender().sendItemOnInterface(193, 0, 12002, 150);

			player.getInterfaceState().setNextDialogueId(0, 12003);
			break;
		case 12003:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes i'd like to merge them.|No.");
			player.getInterfaceState().setNextDialogueId(0, 12004);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 12004:
			player.getActionSender().removeChatboxInterface();
			if (player.getInventory().contains(12002) && player.getInventory().contains(20065)) {
				player.getInventory().remove(new Item(12002));
				player.getInventory().remove(new Item(20065));
				player.getInventory().add(new Item(19720));
				player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 19720, null,
						"You merge the two together to make an Occult necklace (or).");
				player.getActionSender().sendMessage("You merge the two together to make a Occult necklace (or).");
			}
			break;

		case 11804:
			int interfaceId11 = 193;

			player.getActionSender().sendInterface(162, 546, interfaceId11, false);
			player.getActionSender().sendAccessMask(1, 193, 2, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 3, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 4, -1, -1);

			player.getActionSender().sendString(interfaceId11, 1, "Would you like to merge these items?");
			player.getActionSender().sendString(interfaceId11, 2, "Click here to continue.");
			player.getActionSender().sendItemOnInterface(193, 0, 11804, 150);

			player.getInterfaceState().setNextDialogueId(0, 11805);
			break;
		case 11805:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes i'd like to merge them.|No.");
			player.getInterfaceState().setNextDialogueId(0, 11806);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 11806:
			player.getActionSender().removeChatboxInterface();
			if (player.getInventory().contains(11804) && player.getInventory().contains(20071)) {
				player.getInventory().remove(new Item(11804));
				player.getInventory().remove(new Item(20071));
				player.getInventory().add(new Item(20370));
				player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 20370, null,
						"You merge the two together to make an Bandos godsword (or).");
				player.getActionSender().sendMessage("You merge the two together to make a Bandos godsword (or).");
			}
			break;

		case 11807:
			int interfaceId12 = 193;

			player.getActionSender().sendInterface(162, 546, interfaceId12, false);
			player.getActionSender().sendAccessMask(1, 193, 2, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 3, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 4, -1, -1);

			player.getActionSender().sendString(interfaceId12, 1, "Would you like to merge these items?");
			player.getActionSender().sendString(interfaceId12, 2, "Click here to continue.");
			player.getActionSender().sendItemOnInterface(193, 0, 11806, 150);

			player.getInterfaceState().setNextDialogueId(0, 11808);
			break;
		case 11808:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes i'd like to merge them.|No.");
			player.getInterfaceState().setNextDialogueId(0, 11809);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 11809:
			player.getActionSender().removeChatboxInterface();
			if (player.getInventory().contains(11806) && player.getInventory().contains(20074)) {
				player.getInventory().remove(new Item(11806));
				player.getInventory().remove(new Item(20074));
				player.getInventory().add(new Item(20372));
				player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 20372, null,
						"You merge the two together to make an Saradomin godsword (or).");
				player.getActionSender().sendMessage("You merge the two together to make a Saradomin godsword (or).");
			}
			break;

		case 11810:
			int interfaceId13 = 193;

			player.getActionSender().sendInterface(162, 546, interfaceId13, false);
			player.getActionSender().sendAccessMask(1, 193, 2, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 3, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 4, -1, -1);

			player.getActionSender().sendString(interfaceId13, 1, "Would you like to merge these items?");
			player.getActionSender().sendString(interfaceId13, 2, "Click here to continue.");
			player.getActionSender().sendItemOnInterface(193, 0, 11806, 150);

			player.getInterfaceState().setNextDialogueId(0, 11811);
			break;
		case 11811:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes i'd like to merge them.|No.");
			player.getInterfaceState().setNextDialogueId(0, 11812);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 11812:
			player.getActionSender().removeChatboxInterface();
			if (player.getInventory().contains(11806) && player.getInventory().contains(20074)) {
				player.getInventory().remove(new Item(11806));
				player.getInventory().remove(new Item(20074));
				player.getInventory().add(new Item(20372));
				player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 20372, null,
						"You merge the two together to make an Saradomin godsword (or).");
				player.getActionSender().sendMessage("You merge the two together to make a Saradomin godsword (or).");
			}
			break;

		case 11813:
			int interfaceId14 = 193;

			player.getActionSender().sendInterface(162, 546, interfaceId14, false);
			player.getActionSender().sendAccessMask(1, 193, 2, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 3, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 4, -1, -1);

			player.getActionSender().sendString(interfaceId14, 1, "Would you like to merge these items?");
			player.getActionSender().sendString(interfaceId14, 2, "Click here to continue.");
			player.getActionSender().sendItemOnInterface(193, 0, 11808, 150);

			player.getInterfaceState().setNextDialogueId(0, 11814);
			break;
		case 11814:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes i'd like to merge them.|No.");
			player.getInterfaceState().setNextDialogueId(0, 11815);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 11815:
			player.getActionSender().removeChatboxInterface();
			if (player.getInventory().contains(11808) && player.getInventory().contains(20077)) {
				player.getInventory().remove(new Item(11808));
				player.getInventory().remove(new Item(20077));
				player.getInventory().add(new Item(20374));
				player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 20374, null,
						"You merge the two together to make an Zamorak godsword (or).");
				player.getActionSender().sendMessage("You merge the two together to make a Zamorak godsword (or).");
			}
			break;

		case 11816:
			int interfaceId15 = 193;

			player.getActionSender().sendInterface(162, 546, interfaceId15, false);
			player.getActionSender().sendAccessMask(1, 193, 2, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 3, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 4, -1, -1);

			player.getActionSender().sendString(interfaceId15, 1, "Would you like to merge these items?");
			player.getActionSender().sendString(interfaceId15, 2, "Click here to continue.");
			player.getActionSender().sendItemOnInterface(193, 0, 11802, 150);

			player.getInterfaceState().setNextDialogueId(0, 11817);
			break;
		case 11817:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes i'd like to merge them.|No.");
			player.getInterfaceState().setNextDialogueId(0, 11818);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 11818:
			player.getActionSender().removeChatboxInterface();
			if (player.getInventory().contains(11802) && player.getInventory().contains(20068)) {
				player.getInventory().remove(new Item(11802));
				player.getInventory().remove(new Item(20068));
				player.getInventory().add(new Item(20368));
				player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 20368, null,
						"You merge the two together to make an Armadyl godsword (or).");
				player.getActionSender().sendMessage("You merge the two together to make a Armadyl godsword (or).");
			}
			break;

		case 11819:
			int interfaceId16 = 193;

			player.getActionSender().sendInterface(162, 546, interfaceId16, false);
			player.getActionSender().sendAccessMask(1, 193, 2, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 3, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 4, -1, -1);

			player.getActionSender().sendString(interfaceId16, 1, "Would you like to merge these items?");
			player.getActionSender().sendString(interfaceId16, 2, "Click here to continue.");
			player.getActionSender().sendItemOnInterface(193, 0, 11235, 150);

			player.getInterfaceState().setNextDialogueId(0, 11820);
			break;
		case 11820:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes i'd like to merge them.|No.");
			player.getInterfaceState().setNextDialogueId(0, 11821);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 11821:
			player.getActionSender().removeChatboxInterface();
			if (player.getInventory().contains(11235) && player.getInventory().contains(12757)) {
				player.getInventory().remove(new Item(11235));
				player.getInventory().remove(new Item(12757));
				player.getInventory().add(new Item(12766));
				player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 12766, null,
						"You merge the two together to make an Blue Dark bow.");
				player.getActionSender().sendMessage("You merge the two together to make a Blue Dark bow.");
			}
			break;

		case 11822:
			int interfaceId17 = 193;

			player.getActionSender().sendInterface(162, 546, interfaceId17, false);
			player.getActionSender().sendAccessMask(1, 193, 2, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 3, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 4, -1, -1);

			player.getActionSender().sendString(interfaceId17, 1, "Would you like to merge these items?");
			player.getActionSender().sendString(interfaceId17, 2, "Click here to continue.");
			player.getActionSender().sendItemOnInterface(193, 0, 11235, 150);

			player.getInterfaceState().setNextDialogueId(0, 11823);
			break;
		case 11823:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes i'd like to merge them.|No.");
			player.getInterfaceState().setNextDialogueId(0, 11824);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 11824:
			player.getActionSender().removeChatboxInterface();
			if (player.getInventory().contains(11235) && player.getInventory().contains(12759)) {
				player.getInventory().remove(new Item(11235));
				player.getInventory().remove(new Item(12759));
				player.getInventory().add(new Item(12765));
				player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 12765, null,
						"You merge the two together to make an Green Dark bow.");
				player.getActionSender().sendMessage("You merge the two together to make a Green Dark bow.");
			}
			break;

		case 11825:
			int interfaceId18 = 193;

			player.getActionSender().sendInterface(162, 546, interfaceId18, false);
			player.getActionSender().sendAccessMask(1, 193, 2, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 3, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 4, -1, -1);

			player.getActionSender().sendString(interfaceId18, 1, "Would you like to merge these items?");
			player.getActionSender().sendString(interfaceId18, 2, "Click here to continue.");
			player.getActionSender().sendItemOnInterface(193, 0, 11235, 150);

			player.getInterfaceState().setNextDialogueId(0, 11826);
			break;
		case 11826:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes i'd like to merge them.|No.");
			player.getInterfaceState().setNextDialogueId(0, 11827);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 11827:
			player.getActionSender().removeChatboxInterface();
			if (player.getInventory().contains(11235) && player.getInventory().contains(12761)) {
				player.getInventory().remove(new Item(11235));
				player.getInventory().remove(new Item(12761));
				player.getInventory().add(new Item(12767));
				player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 12767, null,
						"You merge the two together to make an Yellow Dark bow.");
				player.getActionSender().sendMessage("You merge the two together to make a Yellow Dark bow.");
			}
			break;

		case 11828:
			int interfaceId19 = 193;

			player.getActionSender().sendInterface(162, 546, interfaceId19, false);
			player.getActionSender().sendAccessMask(1, 193, 2, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 3, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 4, -1, -1);

			player.getActionSender().sendString(interfaceId19, 1, "Would you like to merge these items?");
			player.getActionSender().sendString(interfaceId19, 2, "Click here to continue.");
			player.getActionSender().sendItemOnInterface(193, 0, 11235, 150);

			player.getInterfaceState().setNextDialogueId(0, 11829);
			break;
		case 11829:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes i'd like to merge them.|No.");
			player.getInterfaceState().setNextDialogueId(0, 11830);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 11830:
			player.getActionSender().removeChatboxInterface();
			if (player.getInventory().contains(11235) && player.getInventory().contains(12763)) {
				player.getInventory().remove(new Item(11235));
				player.getInventory().remove(new Item(12763));
				player.getInventory().add(new Item(12768));
				player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 12768, null,
						"You merge the two together to make an White Dark bow.");
				player.getActionSender().sendMessage("You merge the two together to make a White Dark bow.");
			}
			break;

		case 13126:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Cast Alchemy|Run Replenish");
			player.getInterfaceState().setNextDialogueId(0, 13127);
			player.getInterfaceState().setNextDialogueId(1, 13128);
			break;
		case 13127:
			player.getActionSender().sendItemDialogue(13127, "Alchemy option is disabled until further notice.");
			player.getInterfaceState().setNextDialogueId(0, -1);
			break;
		case 13128:
			player.getWalkingQueue().setEnergy(100);
			player.getActionSender().sendRunEnergy();
			player.sendMessage("Your run energy has been fully replenished.");
			player.getActionSender().removeChatboxInterface();
			break;

		case 4151:
			int interfaceId = 193;

			player.getActionSender().sendInterface(162, 546, interfaceId, false);
			player.getActionSender().sendAccessMask(1, 193, 2, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 3, -1, -1);
			player.getActionSender().sendAccessMask(0, 193, 4, -1, -1);

			player.getActionSender().sendString(interfaceId, 1,
					"Would you like to merge these items? This is irreverisble.");
			player.getActionSender().sendString(interfaceId, 2, "Click here to continue.");
			player.getActionSender().sendItemOnInterface(193, 0, 4151, 150);

			player.getInterfaceState().setNextDialogueId(0, 4152);
			break;
		case 4152:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes i'd like to merge them.|No.");
			player.getInterfaceState().setNextDialogueId(0, 4153);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 4153:
			player.getActionSender().removeChatboxInterface();
			if (player.getInventory().contains(4151) && player.getInventory().contains(12004)) {
				player.getInventory().remove(new Item(4151));
				player.getInventory().remove(new Item(12004));
				player.getInventory().add(new Item(12006));
				player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 12006, null,
						"You merge the two together to make an Abyssal Tentacle.");
				player.getActionSender().sendMessage("You merge the two together to make an Abyssal Tentacle.");
			}
			break;

		case 6599:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Who are you and what is this place?");
			player.getInterfaceState().setNextDialogueId(0, 6600);
			break;
		case 6600:
			player.getActionSender().sendDialogue("Mandrith", DialogueType.NPC, 6599, FacialAnimation.DEFAULT,
					"My name is Mandrith.");
			player.getInterfaceState().setNextDialogueId(0, 6601);
			break;
		case 6601:
			player.getActionSender().sendDialogue("Mandrith", DialogueType.NPC, 6599, FacialAnimation.DEFAULT,
					"I collect valuable resources and pawn off access to them<br>to foolish adventurers, like yourself.");
			player.getInterfaceState().setNextDialogueId(0, 6602);
			break;
		case 6602:
			player.getActionSender().sendDialogue("Mandrith", DialogueType.NPC, 6599, FacialAnimation.DEFAULT,
					"You should take a look inside my arena. There's an<br> abundance of valuable resources inside.");
			player.getInterfaceState().setNextDialogueId(0, 6603);
			break;
		case 6603:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"And I can take whatever I want?");
			player.getInterfaceState().setNextDialogueId(0, 6604);
			break;
		case 6604:
			player.getActionSender().sendDialogue("Mandrith", DialogueType.NPC, 6599, FacialAnimation.DEFAULT,
					"It's all yours. All i ask is you pay the upfront fee of 30,000 coins.");
			player.getInterfaceState().setNextDialogueId(0, 6605);
			break;
		case 6605:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Will others be able to kill me inside?");
			player.getInterfaceState().setNextDialogueId(0, 6606);
			break;
		case 6606:
			player.getActionSender().sendDialogue("Mandrith", DialogueType.NPC, 6599, FacialAnimation.DEFAULT,
					"Yes. These walls will only hold them back for so long.");
			player.getInterfaceState().setNextDialogueId(0, 6607);
			break;
		case 6607:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"You'll endGame them though, right?");
			player.getInterfaceState().setNextDialogueId(0, 6608);
			break;
		case 6608:
			player.getActionSender().sendDialogue("Mandrith", DialogueType.NPC, 6599, FacialAnimation.DEFAULT,
					"Haha! For the right price, I won't deny any one access<br>to my arena. Even if their intention is to kill you.");
			player.getInterfaceState().setNextDialogueId(0, 6609);
			break;
		case 6609:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"Right...");
			player.getInterfaceState().setNextDialogueId(0, 6610);
			break;
		case 6610:
			player.getActionSender().sendDialogue("Mandrith", DialogueType.NPC, 6599, FacialAnimation.DEFAULT,
					"My arena holds many treasures that I've acquired at<br>great expense, adventurer. Their bounty can come at a<br>price.");
			player.getInterfaceState().setNextDialogueId(0, 6611);
			break;
		case 6611:
			player.getActionSender().sendDialogue("Mandrith", DialogueType.NPC, 6599, FacialAnimation.DEFAULT,
					"One day, adventurer, I will boast ownership of a much<br>larger, much more dangerous arena than this. Take<br>advantage of this offer while it lasts.");
			player.getInterfaceState().setNextDialogueId(0, -1);
			break;

		case 6481:
			player.getActionSender().sendDialogue("Wise Old Man", DialogueType.NPC, 2108, FacialAnimation.DEFAULT,
					"Hello, how can I help you?");
			player.getInterfaceState().setNextDialogueId(0, 6482);
			break;
		case 6482:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, 2108, FacialAnimation.DEFAULT,
					"Can I buy a Max cape from you?");
			player.getInterfaceState().setNextDialogueId(0, 6483);
			break;
		case 6483:
			int totalLevel = player.getSkills().getTotalLevel();
			String text = totalLevel >= Constants.MAX_LEVEL
					? "Sure, You seem to be a master of all skills, it will cost 2m gold coins, though. Is that alright?"
					: "Sorry, please come back when you're a master of all skills.";
			int nextDialogue = totalLevel >= Constants.MAX_LEVEL ? 6484 : -1;
			player.getActionSender().sendDialogue("Wise Old Man", DialogueType.NPC, 2108, FacialAnimation.DEFAULT,
					text);
			player.getInterfaceState().setNextDialogueId(0, nextDialogue);
			break;
		case 6484:
			player.getActionSender().sendDialogue("Buy the Max Cape for 2m?", DialogueType.OPTION, -1,
					FacialAnimation.DEFAULT, "Yes.|No.");
			player.getInterfaceState().setNextDialogueId(0, 6485);
			player.getInterfaceState().setNextDialogueId(1, 6499);
			break;
		case 6485:
			player.getActionSender().removeChatboxInterface();
			hasGold = player.getInventory().getCount(995) >= 2000000;
			if (hasGold) {
				player.getInventory().remove(new Item(995, 2000000));
				Inventory.addDroppable(player, new Item(13280));
				Inventory.addDroppable(player, new Item(13281));
				player.getActionSender().sendDialogue("Wise Old Man", DialogueType.NPC, 2108, FacialAnimation.HAPPY,
						"Pleasure doing business with you!");
				player.getInterfaceState().setNextDialogueId(0, -1);
			} else
				player.getActionSender().sendDialogue("Wise Old Man", DialogueType.NPC, 2108, FacialAnimation.SAD,
						"Ah, you don't have enough coins. Come back when you do!.");
			player.getInterfaceState().setNextDialogueId(0, -1);
			break;

		case 6499:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, 2108, FacialAnimation.DEFAULT,
					"No thank you, maybe some other time!");
			player.getInterfaceState().setNextDialogueId(0, -1);
			break;

		case 6486: // TODO doesn't want to proceed with dialogue :L
			player.getActionSender().sendItemDialogue(12929, "Dismantling the Serpentine helm "
					+ "will give you 20'000 Zulrah's scale, but will destroy the item in the process!");
			player.getInterfaceState().setNextDialogueId(0, 6487);
			break;
		case 6487:
			player.getActionSender().sendDialogue("<col=ff0000>Dismantle Serpentine helm</col>?", DialogueType.OPTION,
					-1, FacialAnimation.DEFAULT, "Yes.|No.");
			player.getInterfaceState().setNextDialogueId(0, 6488);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 6488:
			player.sendMessage("You've dismantled your Serpentine helm in exchange for 20'000 Zulrah's scales.");
			player.getInventory().remove(new Item(12929));
			player.getInventory().add(new Item(12934, 20000));
			player.getActionSender().removeChatboxInterface();
			break;
		case 6489:
			player.getActionSender().sendDialogue("<col=ff0000>Dismantle Toxic Staff of the Dead</col>?",
					DialogueType.OPTION, -1, FacialAnimation.DEFAULT, "Yes.|No.");
			player.getInterfaceState().setNextDialogueId(0, 6490);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 6490:
			player.sendMessage("You've reverted your Toxic Staff of the Dead back to its original form.");
			player.getInventory().remove(new Item(12902));
			player.getInventory().add(new Item(11791));
			Inventory.addDroppable(player, new Item(12932));
			player.getActionSender().removeChatboxInterface();
			break;
		case 6491:
			player.getActionSender().sendDialogue("<col=ff0000>Dismantle Magic Fang</col>?", DialogueType.OPTION, -1,
					FacialAnimation.DEFAULT, "Yes.|No.");
			player.getInterfaceState().setNextDialogueId(0, 6492);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 6492:
			player.sendMessage("You've dismantled your Magic fang in exchange for 20'000 Zulrah's scales.");
			player.getInventory().remove(new Item(12932));
			player.getInventory().add(new Item(12934, 20000));
			player.getActionSender().removeChatboxInterface();
			break;
		case 6493:
			player.getActionSender().sendDialogue("<col=ff0000>Dismantle Toxic Blowpipe</col>?", DialogueType.OPTION,
					-1, FacialAnimation.DEFAULT, "Yes.|No.");
			player.getInterfaceState().setNextDialogueId(0, 6494);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 6494:
			player.sendMessage("You've dismantled your Toxic blowpipe in exchange for 20'000 Zulrah's scales.");
			player.getInventory().remove(new Item(12924));
			player.getInventory().add(new Item(12934, 20000));
			player.getActionSender().removeChatboxInterface();
			break;
		case 6495:
			player.getActionSender().sendDialogue("<col=ff0000>Dismantle Tanzanite fang</col>?", DialogueType.OPTION,
					-1, FacialAnimation.DEFAULT, "Yes.|No.");
			player.getInterfaceState().setNextDialogueId(0, 6496);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 6496:
			player.sendMessage("You've dismantled your Tanzanite fang in exchange for 20'000 Zulrah's scales.");
			player.getInventory().remove(new Item(12922));
			player.getInventory().add(new Item(12934, 20000));
			player.getActionSender().removeChatboxInterface();
			break;
		case 6497:
			player.getActionSender().sendDialogue("<col=ff0000>Dissolve Abyssal Tentacle</col>?", DialogueType.OPTION,
					-1, FacialAnimation.DEFAULT, "Yes.|No.");
			player.getInterfaceState().setNextDialogueId(0, 6498);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 6498:
			player.sendMessage("You've dissolved your Abyssal Tentacle and are left only with a kraken tentacle.");
			player.getInventory().remove(new Item(12006));
			player.getInventory().add(new Item(12004));
			player.getActionSender().removeChatboxInterface();
			break;
		case 6500:
			player.getActionSender().sendDialogue("<col=ff0000>Dismantle Toxic Trident</col>?", DialogueType.OPTION, -1,
					FacialAnimation.DEFAULT, "Yes.|No.");
			player.getInterfaceState().setNextDialogueId(0, 6501);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 6501:
			player.sendMessage("You've dismantled your trident and received back it's original components.");
			player.getInventory().remove(new Item(12900));
			player.getInventory().add(new Item(11908));
			Inventory.addDroppable(player, new Item(12932));
			player.getActionSender().removeChatboxInterface();
			break;

		/**
		 * Warriors guild
		 */
		case 6502:
			int level = player.getSkills().getLevelForExperience(Skills.STRENGTH)
					+ player.getSkills().getLevelForExperience(Skills.ATTACK);
			if (level < 130) {
				player.getActionSender().sendDialogue("Ghommal", DialogueType.NPC, 2457, FacialAnimation.DEFAULT,
						"You not pass. You too weedy.");
				player.getInterfaceState().setNextDialogueId(0, 6503);
			} else {
				player.getActionSender().sendDialogue("Ghommal", DialogueType.NPC, 2457, FacialAnimation.DEFAULT,
						"You may pass. You strong warrior!");
				player.getInterfaceState().setNextDialogueId(0, -1);
			}
			break;
		case 6503:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, 2457, FacialAnimation.DEFAULT,
					"What? But I'm a warrior!");
			player.getInterfaceState().setNextDialogueId(0, 6504);
			break;
		case 6504:
			player.getActionSender().sendDialogue("Ghommal", DialogueType.NPC, 2457, FacialAnimation.DEFAULT,
					"Heehee... he say he warrior... I not heard that one for... at leas' 5 minutes!");
			player.getInterfaceState().setNextDialogueId(0, 6505);
			break;
		case 6505:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, 2457, FacialAnimation.DEFAULT,
					"Go on, let me in, you know you want to. I could... make it worth your while...");
			player.getInterfaceState().setNextDialogueId(0, 6506);
			break;
		case 6506:
			player.getActionSender().sendDialogue("Ghommal", DialogueType.NPC, 2457, FacialAnimation.DEFAULT,
					"No! You is not a strong warrior, you not enter till you bigger. Ghommal does not take bribes.");
			player.getInterfaceState().setNextDialogueId(0, 6507);
			break;
		case 6507:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, 2457, FacialAnimation.DEFAULT,
					"Why not?");
			player.getInterfaceState().setNextDialogueId(0, 6508);
			break;
		case 6508:
			player.getActionSender().sendDialogue("Ghommal", DialogueType.NPC, 2457, FacialAnimation.DEFAULT,
					"Ghommal stick to Warrior's Code of Honour. When you a bigger, stronger warrior, you come back.");
			player.getInterfaceState().setNextDialogueId(0, -1);
			break;

		/**
		 * Emblem trader bullshit
		 */
		case 10000:
			int pointsPrior = player.getDatabaseEntity().getBountyHunter().getBountyShopPoints();
			for (Item inv : player.getInventory().toArray()) {
				if (inv == null)
					continue;
				BountyHunterService.Emblems.of(inv.getId()).ifPresent(e -> {
					player.getInventory().remove(inv);
					int points = player.getDatabaseEntity().getBountyHunter().getBountyShopPoints();
					player.getDatabaseEntity().getBountyHunter().setBountyShopPoints(e.getCost() + points);
				});
			}
			int addedPoints = player.getDatabaseEntity().getBountyHunter().getBountyShopPoints() - pointsPrior;
			player.getActionSender().sendMessage("You've sold all your emblems for "
					+ NumberFormat.getNumberInstance(Locale.ENGLISH).format(addedPoints) + " Bounties.");
			player.getActionSender().sendDialogue("Emblem Trader", DialogueType.NPC, 315, FacialAnimation.DEFAULT,
					"I've bought all your emblems for "
							+ NumberFormat.getNumberInstance(Locale.ENGLISH).format(addedPoints) + " bounties.");
			break;

		case 11864:
			player.getActionSender().sendDialogue("Vannaka", DialogueType.NPC, 403, FacialAnimation.DEFAULT,
					"Would you like to imbue your Slayer helm for 500 Slayer Points?");
			player.getInterfaceState().setNextDialogueId(0, 11865);
			break;
		case 11865:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes|No.");
			player.getInterfaceState().setNextDialogueId(0, 11866);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 11866:
			int points = player.getDatabaseEntity().getStatistics().getSlayerRewardPoints();
			if (points < 500) {
				player.getActionSender().sendDialogue("Vannaka", DialogueType.NPC, 403, FacialAnimation.DEFAULT,
						"You don't have enough points to complete this.");
				player.getInterfaceState().setNextDialogueId(0, -1);
			} else {
				player.getActionSender().removeChatboxInterface();
				if (player.getInventory().hasItem(new Item(11864))) {
					player.getInventory().remove(new Item(11864));
					player.getInventory().add(new Item(11865));
					player.getDatabaseEntity().getStatistics().setSlayerRewardPoints(points - 500);
					player.getActionSender().sendMessage("You imbued your Slayer helm for 500 Slayer reward points.");
				}
				player.getActionSender().sendDialogue("Vannaka", DialogueType.NPC, 403, FacialAnimation.HAPPY,
						"Pleasure doing business with you.");
			}
			break;

		case 11867:
			player.getActionSender().sendDialogue("Vannaka", DialogueType.NPC, 403, FacialAnimation.DEFAULT,
					"Would you like to imbue your Red Slayer helm for 500 Slayer Points?");
			player.getInterfaceState().setNextDialogueId(0, 11868);
			break;
		case 11868:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes|No.");
			player.getInterfaceState().setNextDialogueId(0, 11869);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 11869:
			points = player.getDatabaseEntity().getStatistics().getSlayerRewardPoints();
			if (points < 500) {
				player.getActionSender().sendDialogue("Vannaka", DialogueType.NPC, 403, FacialAnimation.DEFAULT,
						"You don't have enough points to complete this.");
				player.getInterfaceState().setNextDialogueId(0, -1);
			} else {
				player.getActionSender().removeChatboxInterface();
				if (player.getInventory().hasItem(new Item(19647))) {
					player.getInventory().remove(new Item(19647));
					player.getInventory().add(new Item(19649));
					player.getDatabaseEntity().getStatistics().setSlayerRewardPoints(points - 500);
					player.getActionSender()
							.sendMessage("You imbued your Red Slayer helm for 500 Slayer reward points.");
				}
				player.getActionSender().sendDialogue("Vannaka", DialogueType.NPC, 403, FacialAnimation.HAPPY,
						"Pleasure doing business with you.");
			}
			break;

		case 11941:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"One|Five|All");
			player.getInterfaceState().setNextDialogueId(0, 11942);
			player.getInterfaceState().setNextDialogueId(1, 11943);
			player.getInterfaceState().setNextDialogueId(2, 11944);
			break;
		case 11942:
			Item one = player.getInterfaceAttribute("lootingBagItem");
			int oneIndex = player.getInterfaceAttribute("lootingBagIndex");
			LootingBagService lootingBagService = Server.getInjector().getInstance(LootingBagService.class);
			if (one != null && oneIndex != -1) {
				lootingBagService.deposit(player, oneIndex, one.getId(), 1);
			}
			break;
		case 11943:
			Item five = player.getInterfaceAttribute("lootingBagItem");
			int fiveIndex = player.getInterfaceAttribute("lootingBagIndex");
			lootingBagService = Server.getInjector().getInstance(LootingBagService.class);
			if (five != null && fiveIndex != -1) {
				lootingBagService.deposit(player, fiveIndex, five.getId(), 5);
			}
			break;
		case 11944:
			Item all = player.getInterfaceAttribute("lootingBagItem");
			int allIndex = player.getInterfaceAttribute("lootingBagIndex");
			lootingBagService = Server.getInjector().getInstance(LootingBagService.class);
			if (all != null && allIndex != -1) {
				lootingBagService.deposit(player, allIndex, all.getId(), player.getInventory().getCount(all.getId()));
			}
			break;

		case 12020:
			Map<GemBagService.Gems, Integer> gemBag = player.getDatabaseEntity().getGemBag();
			if (gemBag.size() <= 0)
				return;
			List<GemBagService.Gems> gems = new ArrayList<>();
			StringBuilder sb = new StringBuilder();
			for (@SuppressWarnings("rawtypes")
			Map.Entry pair : gemBag.entrySet()) {
				GemBagService.Gems key = (GemBagService.Gems) pair.getKey();
				int value = (int) pair.getValue();
				sb.append(key.getName()).append(" (").append(value).append(")|");
				gems.add(key);
			}
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					sb.toString());
			for (int i = 0; i < gems.size(); i++) {
				player.getInterfaceState().setNextDialogueId(i, 12021 + i);
				player.setInterfaceAttribute("gemBagType" + i, gems.get(i));
			}
			break;
		case 12021:
			player.getActionSender().removeChatboxInterface();
			GemBagService.Gems gemOne = player.getInterfaceAttribute("gemBagType0");
			if (gemOne != null) {
				gemBagService.withdraw(player, gemOne);
			}
			break;
		case 12022:
			player.getActionSender().removeChatboxInterface();
			GemBagService.Gems gemTwo = player.getInterfaceAttribute("gemBagType1");
			if (gemTwo != null) {
				gemBagService.withdraw(player, gemTwo);
			}
			break;
		case 12023:
			player.getActionSender().removeChatboxInterface();
			GemBagService.Gems gemThree = player.getInterfaceAttribute("gemBagType2");
			if (gemThree != null) {
				gemBagService.withdraw(player, gemThree);
			}
			break;
		case 12024:
			player.getActionSender().removeChatboxInterface();
			GemBagService.Gems gemFour = player.getInterfaceAttribute("gemBagType3");
			if (gemFour != null) {
				gemBagService.withdraw(player, gemFour);
			}
			break;
		case 12025:
			player.getActionSender().removeChatboxInterface();
			GemBagService.Gems gemFive = player.getInterfaceAttribute("gemBagType4");
			if (gemFive != null) {
				gemBagService.withdraw(player, gemFive);
			}
			break;
		case 12929:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"If I do this i'll lose my mutagen.... Should I continue?");
			player.getInterfaceState().setNextDialogueId(0, 12930);
			break;
		case 12930:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes|No.");
			player.getInterfaceState().setNextDialogueId(0, 12931);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 12931:
			player.getActionSender().removeChatboxInterface();
			if (player.getInventory().contains(13196)) {
				if (player.getInventory().add(new Item(12929))) {
					player.getInventory().remove(new Item(13196));
				}
			}
			break;

		case 12932:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.DEFAULT,
					"If I do this I'll lose my mutagen...");
			player.getInterfaceState().setNextDialogueId(0, 12933);
			break;
		case 12933:
			player.getActionSender().sendDialogue("Select an Option", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Yes|No.");
			player.getInterfaceState().setNextDialogueId(0, 12934);
			player.getInterfaceState().setNextDialogueId(1, -1);
			break;
		case 12934:
			player.getActionSender().removeChatboxInterface();
			if (player.getInventory().contains(13198)) {
				if (player.getInventory().add(new Item(12929))) {
					player.getInventory().remove(new Item(13198));
				}
			}
			break;

		case 13114:
			player.getActionSender().sendDialogue("Where would you like to teleport?", DialogueType.OPTION, -1,
					FacialAnimation.DEFAULT, "Ectofuntus|Burgh De Rott");
			player.getInterfaceState().setNextDialogueId(0, 13115);
			player.getInterfaceState().setNextDialogueId(1, 13116);
			break;
		case 13115:
			player.getActionSender().removeChatboxInterface();
			player.teleport(Location.create(3656, 3522, 0), 1, 1, false);
			break;
		case 13116:
			player.getActionSender().removeChatboxInterface();
			player.teleport(Location.create(3481, 3229, 0), 3, 3, false);
			break;

		case 13122:
			player.getActionSender().sendDialogue("Where would you like to teleport?", DialogueType.OPTION, -1,
					FacialAnimation.DEFAULT, "Kandarin Monastery|Ardougne Farm");
			player.getInterfaceState().setNextDialogueId(0, 13123);
			player.getInterfaceState().setNextDialogueId(1, 13124);
			break;
		case 13123:
			player.getActionSender().removeChatboxInterface();
			player.teleport(Location.create(2606, 3222, 0), 1, 1, false);
			break;
		case 13124:
			player.getActionSender().removeChatboxInterface();
			player.teleport(Location.create(2664, 3374, 0), 1, 1, false);
			break;

		case 13103:
			player.getActionSender().sendDialogue("Where would you like to teleport?", DialogueType.OPTION, -1,
					FacialAnimation.DEFAULT, "Shilo Village|Duradel");
			player.getInterfaceState().setNextDialogueId(0, 13104);
			player.getInterfaceState().setNextDialogueId(1, 13105);
			break;
		case 13104:
			player.getActionSender().removeChatboxInterface();
			player.teleport(Location.create(2826, 2995, 0), 4, 4, false);
			break;
		case 13105:
			player.getActionSender().sendItemDialogue(13103,
					"Sorry, we couldn't teleport you there. Use the Slayer master at home!");
			player.getInterfaceState().setNextDialogueId(0, -1);
			break;

		/**
		 * World Switcher (Widget 69)
		 */

		case 6969:
			player.getActionSender().sendDialogue("", ActionSender.DialogueType.WORLD_SWITCHER, -1, null, "");//
			break;

		/*
		 * Tome of fire
		 */
		case 20714:
			player.getActionSender().sendDialogue("Tome of Fire", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Add Pages|Remove Pages");
			player.getInterfaceState().setNextDialogueId(0, 20715);
			player.getInterfaceState().setNextDialogueId(1, 20716);
			break;

		case 20715:
			int charges = player.getItemService().getCharges(player, new Item(20714));
			if (player.getInventory().getCount(20718) < 1) {
				player.getActionSender().sendDialogue("", DialogueType.MESSAGE, 1, FacialAnimation.DEFAULT,
						"You don't have any burnt pages on you.");
				player.getInterfaceState().setNextDialogueId(0, -1);
				return;
			}
			if (charges >= 2000) {
				player.getActionSender().sendItemDialogue(20714, "Your tome can't hold any more pages.");
				player.getInterfaceState().setNextDialogueId(0, -1);
				return;
			}
			player.setInterfaceAttribute("tome_of_fire", true);
			player.getActionSender().sendEnterAmountInterface();
			break;

		case 20717:
			charges = player.getItemService().getCharges(player, new Item(20714));

			amount = player.getInterfaceAttribute("tome_of_fire_x");
			player.removeInterfaceAttribute("tome_of_fire_x");
			if (amount < 1)
				amount = 1;

			if (player.getInventory().getCount(20718) == 1) {
				player.getActionSender().sendItemDialogue(20714, "You added <col=ff0000>1</col> page to your tome.");
				player.getItemService().setCharges(player, new Item(20714), charges + 20);
				return;
			}
			if (amount > 1000)
				amount = 1000; // max pages to add at a single time

			if (amount > player.getInventory().getCount(20718))
				amount = player.getInventory().getCount(20718);

			int possibleCharges = 2000 - charges; // charges that can be addable
			if (possibleCharges < 20)
				possibleCharges = 20; // cuz 1 page is 20 charges
			int pages = possibleCharges / 20; // burnt pages to remove from inventory
			if (amount > pages)
				amount = pages;

			int chargesToAdd = amount * 20;
			if (chargesToAdd > chargesToAdd + charges)
				chargesToAdd = 2000;

			player.getActionSender().sendItemDialogue(20714,
					"You added <col=ff0000>" + amount + "</col> pages to your tome.");
			player.getItemService().setCharges(player, new Item(20714), charges + chargesToAdd);
			player.getInventory().remove(new Item(20718, amount));
			break;

		case 20716:
			charges = player.getItemService().getCharges(player, new Item(20714));

			if (charges < 20) {
				player.getActionSender().sendItemDialogue(20714, "Your tome has no pages left.");
				player.getInterfaceState().setNextDialogueId(0, -1);
				return;
			}
			pages = charges / 20;
			int chargesLeft = charges - (pages * 20);
			player.getActionSender().sendItemDialogue(20714,
					"You removed <col=ff0000>" + pages + "</col> pages from your tome.");
			player.getItemService().setCharges(player, new Item(20714), chargesLeft);
			Inventory.addDroppable(player, new Item(20718, pages));
			player.getInterfaceState().setNextDialogueId(0, -1);
			break;

		case 100000:
			player.getActionSender().sendDialogue("Agility Teleports", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Gnome Course (Level 1)|Draynor Rooftops (Level 10)|Varrock Rooftops (Level 30)"
							+ "|Seer's Rooftops (Level 60)|Ardougne Rooftops (Level 90)");
			player.getInterfaceState().setNextDialogueId(0, 100001);
			player.getInterfaceState().setNextDialogueId(1, 100002);
			player.getInterfaceState().setNextDialogueId(2, 100003);
			player.getInterfaceState().setNextDialogueId(3, 100004);
			player.getInterfaceState().setNextDialogueId(4, 100005);
			break;
		case 100001:
			handleTeleport(player, Location.create(2474, 3436, 0), false);
			player.getActionSender().closeAll();
			break;
		case 100002:
			handleTeleport(player, Location.create(3107, 3279, 0), false);
			player.getActionSender().closeAll();
			break;
		case 100003:
			handleTeleport(player, Location.create(3223, 3415, 0), false);
			player.getActionSender().closeAll();
			break;
		case 100004:
			handleTeleport(player, Location.create(2729, 3485, 0), false);
			player.getActionSender().closeAll();
			break;
		case 100005:
			handleTeleport(player, Location.create(2673, 3294, 0), false);
			player.getActionSender().closeAll();
			break;

		case 110000:
			player.getActionSender().sendDialogue(CacheNPCDefinition.get(4626).getName(), DialogueType.NPC, 4626,
					FacialAnimation.NEARLYC_RYING, "Hello there!<br>Could you help me defeat Culinaromancer, please?");
			player.getInterfaceState().setNextDialogueId(0, 110001);
			break;
		case 110001:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1,
					FacialAnimation.BOWS_HEAD_WHILE_SAD, "What's in it for me?");
			player.getInterfaceState().setNextDialogueId(0, 110002);
			break;
		case 110002:
			player.getActionSender().sendDialogue(CacheNPCDefinition.get(4626).getName(), DialogueType.NPC, 4626,
					FacialAnimation.CALM_1,
					"See this chest over here? I'll grant you permission to buy one of the best gloves on "
							+ Constants.SERVER_NAME + " in exchange for your help.");
			player.getInterfaceState().setNextDialogueId(0, 110003);
			break;
		case 110003:
			player.getActionSender().sendDialogue("Start Recipe for Disaster?", DialogueType.OPTION, -1,
					FacialAnimation.DEFAULT, "I'll help!|Maybe some other time.");
			player.getInterfaceState().setNextDialogueId(0, 110004);
			player.getInterfaceState().setNextDialogueId(1, 110005);
			break;
		case 110004:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.CALM_2,
					"Sure, I'll help.");
			player.getInterfaceState().setNextDialogueId(0, 110006);
			break;
		case 110005:
			player.getActionSender().sendDialogue(player.getName(), DialogueType.PLAYER, -1, FacialAnimation.CALM_1,
					"Sorry, maybe some other time.");
			player.getInterfaceState().setNextDialogueId(0, -1);
			break;
		case 110006:
			player.getActionSender().sendDialogue(CacheNPCDefinition.get(4626).getName(), DialogueType.NPC, 4626,
					FacialAnimation.ANNOYED,
					"Excellent!<br>And before you ask.. no, you do <col=ff0000>NOT</col> loose your items on death!<br>"
							+ "Good luck.");
			player.getInterfaceState().setNextDialogueId(0, 110007);
			break;
		case 110007:
			player.getActionSender().removeChatboxInterface();
			player.getRFD().start();
			break;

		case 100036:
			player.getActionSender().sendDialogue("Farming Teleports", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Catherby Allotment|Ardougne Allotment|Falador Allotment|Canifis Allotment");
			player.getInterfaceState().setNextDialogueId(0, 100037);
			player.getInterfaceState().setNextDialogueId(1, 100038);
			player.getInterfaceState().setNextDialogueId(2, 100039);
			player.getInterfaceState().setNextDialogueId(3, 100040);
			break;
		case 100037:
			handleTeleport(player, Location.create(2807, 3463, 0), false);
			player.getActionSender().closeAll();
			break;
		case 100038:
			handleTeleport(player, Location.create(2664, 3374, 0), false);
			player.getActionSender().closeAll();
			break;
		case 100039:
			handleTeleport(player, Location.create(3052, 3304, 0), false);
			player.getActionSender().closeAll();
			break;
		case 100040:
			handleTeleport(player, Location.create(3599, 3522, 0), false);
			player.getActionSender().closeAll();
			break;

		case 100024:
			player.getActionSender().sendDialogue("Fishing Teleports", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Catherby (Levels 1 - 76)|Piscatoris Colony (Level 62)|Fishing Guild (Levels 68 - 76)");
			player.getInterfaceState().setNextDialogueId(0, 100025);
			player.getInterfaceState().setNextDialogueId(1, 100026);
			player.getInterfaceState().setNextDialogueId(2, 100027);
			break;
		case 100025:
			handleTeleport(player, Location.create(2835, 3435, 0), false);
			player.getActionSender().closeAll();
			break;
		case 100026:
			handleTeleport(player, Location.create(2337, 3695, 0), false);
			player.getActionSender().closeAll();
			break;
		case 100027:
			handleTeleport(player, Location.create(2598, 3410, 0), false);
			player.getActionSender().closeAll();
			break;

		case 100012:
			player.getActionSender().sendDialogue("Hunter Teleports", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Crimson Swifts (Level 1)|Tropical Wagtails (Level 19)|Grey Chinchompas (Level 53)|Grey Chinchompas (Level 63)");
			player.getInterfaceState().setNextDialogueId(0, 100013);
			player.getInterfaceState().setNextDialogueId(1, 100014);
			player.getInterfaceState().setNextDialogueId(2, 100015);
			player.getInterfaceState().setNextDialogueId(3, 100016);
			break;
		case 100013:
			handleTeleport(player, Location.create(2602, 2906, 0), false);
			player.getActionSender().closeAll();
			break;
		case 100014:
			handleTeleport(player, Location.create(3080, 3250, 0), false);
			player.getActionSender().closeAll();
			break;
		case 100015:
			handleTeleport(player, Location.create(3056, 4968, 1), false);
			player.getActionSender().closeAll();
			break;
		case 100016:
			handleTeleport(player, Location.create(3056, 4968, 1), false);
			player.getActionSender().closeAll();
			break;

		case 100018:
			player.getActionSender().sendDialogue("Mining Teleports", DialogueType.OPTION, -1, FacialAnimation.DEFAULT,
					"Varrock East (Levels 1 - 15)|Ardougne East (Levels 15 - 30)|Falador Mines (Levels 1 - 70)|"
							+ "Motherlode Mines (Levels 30 - 72)");
			player.getInterfaceState().setNextDialogueId(0, 100019);
			player.getInterfaceState().setNextDialogueId(1, 100020);
			player.getInterfaceState().setNextDialogueId(2, 100021);
			player.getInterfaceState().setNextDialogueId(3, 100022);
			break;
		case 100019:
			handleTeleport(player, Location.create(3285, 3366, 0), false);
			player.getActionSender().closeAll();
			break;
		case 100020:
			handleTeleport(player, Location.create(2700, 3330, 0), false);
			player.getActionSender().closeAll();
			break;
		case 100021:
			handleTeleport(player, Location.create(3050, 9773, 0), false);
			player.getActionSender().closeAll();
			break;
		case 100022:
			handleTeleport(player, Location.create(3758, 5666, 0), false);
			player.getActionSender().closeAll();
			break;

		case 100006:
			player.getActionSender().sendDialogue("Thieving Teleports", DialogueType.OPTION, -1,
					FacialAnimation.DEFAULT,
					"Ardougne Square (Level 1)|Draynor Square (Level 22)|Rogues Den (Level 50)");
			player.getInterfaceState().setNextDialogueId(0, 100007);
			player.getInterfaceState().setNextDialogueId(1, 100008);
			player.getInterfaceState().setNextDialogueId(2, 100009);
			break;
		case 100007:
			handleTeleport(player, Location.create(2662, 3305, 0), false);
			player.getActionSender().closeAll();
			break;
		case 100008:
			handleTeleport(player, Location.create(3080, 3250, 0), false);
			player.getActionSender().closeAll();
			break;
		case 100009:
			handleTeleport(player, Location.create(3056, 4968, 1), false);
			player.getActionSender().closeAll();
			break;

		case 100042:
			player.getActionSender().sendDialogue("Training Teleports", DialogueType.OPTION, -1,
					FacialAnimation.DEFAULT, "Cows (Level 2)|Rock Crabs (Level 13)|Yaks (Level 22)|"
							+ "Experiments (Level 25)|Hill Giants (Level 28)");
			player.getInterfaceState().setNextDialogueId(0, 100043);
			player.getInterfaceState().setNextDialogueId(1, 100044);
			player.getInterfaceState().setNextDialogueId(2, 100045);
			player.getInterfaceState().setNextDialogueId(3, 100046);
			player.getInterfaceState().setNextDialogueId(4, 100047);
			break;
		case 100043:
			handleTeleport(player, Location.create(3257, 3260, 0), true);
			player.getActionSender().closeAll();
			break;
		case 100044:
			handleTeleport(player, Location.create(2670, 3710, 0), true);
			player.getActionSender().closeAll();
			break;
		case 100045:
			handleTeleport(player, Location.create(2320, 3802), true);
			player.getActionSender().closeAll();
			break;
		case 100046:
			handleTeleport(player, Location.create(3552, 9943, 0), true);
			player.getActionSender().closeAll();
			break;
		case 100047:
			handleTeleport(player, Location.create(3116, 9841, 0), true);
			player.getActionSender().closeAll();
			break;

		case 100030:
			player.getActionSender().sendDialogue("Woodcutting Teleports", DialogueType.OPTION, -1,
					FacialAnimation.DEFAULT, "Lumbridge (Level 1)|Seer's Village (Levels 1 - 45)|"
							+ "Sorcerer's Tower (Levels 1 - 75)|Woodcutting Guild (Levels 1 - 90)");
			player.getInterfaceState().setNextDialogueId(0, 100031);
			player.getInterfaceState().setNextDialogueId(1, 100032);
			player.getInterfaceState().setNextDialogueId(2, 100033);
			player.getInterfaceState().setNextDialogueId(3, 100034);
			break;
		case 100031:
			handleTeleport(player, Location.create(3188, 3223, 0), false);
			player.getActionSender().closeAll();
			break;
		case 100032:
			handleTeleport(player, Location.create(2719, 3501, 0), false);
			player.getActionSender().closeAll();
			break;
		case 100033:
			handleTeleport(player, Location.create(2691, 3421, 0), false);
			player.getActionSender().closeAll();
			break;
		case 100034:
			handleTeleport(player, Location.create(1647, 3504, 0), false);
			player.getActionSender().closeAll();
			break;
		}
	}

	/**
	 * Handles the actual player teleporting.
	 * 
	 * @param player
	 *            the player we're teleporting.
	 * @param location
	 *            the location to where we're teleporting the player to.
	 */
	private static void handleTeleport(Player player, Location location, boolean randomize) {
		player.teleport(location, (randomize ? 4 : 0), (randomize ? 4 : 0), true);
		player.lastLocation = location; // for previous teleport destination
		player.getActionSender().closeAll();
	}
}