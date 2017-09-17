package org.rs2server.rs2.content;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.bit.component.*;
import org.rs2server.rs2.model.player.Player;

/**
 * @author Vichy
 */
public class TeleportManager {

	/**
	 * @author Scripts Sends the Interface/CS2Script/Access Mask to the entity.
	 */
	public static void handleTeleport(Player player) { 
		player.getActionSender().sendInterface(187, false);
		player.getActionSender().sendCS2Script(217, new Object[] {
				"||||||||- <col=ff><u=000000>Training Locations</col></u> -|Rock Crabs|Slayer Tower|Experiments"
						+ "|Hill Giants|Yaks"

						+ "|---------------|- <col=ff><u=000000>Dungeon Teleports</u></col> -|Edgeville Dungeon"
						+ "|Taverley Dungeon|Brimhaven Dungeon|Fremennik Slayer Dungeon|Stronghold Slayer Cave"
						+ "|Asgarnian Ice Dungeon|Waterfall Dungeon|Mos Le'Harmless|Catacombs of Kourend"
						+ "|Crash Site Cavern|Ancient Cavern"

						+ "|---------------|- <col=ff><u=000000>Minigame Teleports</u></col> -"
						+ "|Barrows|Pest Control|Warriors Guild|Duel Arena|Fight Caves|Wintertodt"

						+ "|---------------|- <col=ff><u=000000>Skilling Teleports</u></col> -|Agility Locations|Runecrafting|Thieving Locations"
						+ "|Hunter Locations|Mining Locations|Fishing Locations|Woodcutting Locations|Farming Locations"

						+ "|---------------|- <col=ff><u=000000>Boss Teleports</u></col> -|Kalphite Queen|King Black Dragon<col=880000>(Lvl 42 wildy)"
						+ "|Corporeal Beast|Abyssal Sire|Godwars Dungeon|Lizardman Shaman|Zulrah|Dagannoth Kings|Giant Mole"
						+ "|Raids (WIP)|Cerberus"

						+ "|---------------|- <col=880000><u=000000>Wilderness Teleports</u></col> -|Mage Bank"
						+ "|Lava Dragons|PvP Castle|Wilderness Resource area|West Dragons"
						+ "|East Dragons|Chaos Elemental|Venenatis|Chaos Fanatic|Crazy Archaeologist",
				"<img=1>" + Constants.SERVER_NAME + " Teleports<img=1>", 0 }, "Iss");// Iss
		player.sendAccess(Access.of(187, 3, NumberRange.of(0, 170), AccessBits.CLICK_CONTINUE));
	}

	/**
	 * Scripts Handle the Options for these Teleports. Do not use Options 0 ->
	 *         5; Using those Options will break the Dialogue system.
	 */
	public static boolean handleTeleportActions(Player player, int option) {

		// Remove chatbox interface first if selecting different option
		player.getActionSender().removeChatboxInterface();

		switch (option) {
		case 9:// Monster Teleports Option
			handleTeleport(player, Location.create(2673, 3714, 0));

			break;
		case 10:
			handleTeleport(player, Location.create(3429, 3523, 0));

			break;
		case 11:
			handleTeleport(player, Location.create(3577, 9927, 0));

			break;
		case 12:
			handleTeleport(player, Location.create(3117, 9860, 0));

			break;
		case 13:
			handleTeleport(player, Location.create(2321, 3803, 0));

			break;

		// Dungeon teleports
		case 16:
			handleTeleport(player, Location.create(3097, 9868, 0));// edge
																	// dungeon

			break;
		case 17:
			handleTeleport(player, Location.create(2884, 9798, 0));// taverly
																	// dungeon

			break;
		case 18:
			handleTeleport(player, Location.create(2744, 3148, 0));// brimhaven
																	// dungeon

			break;
		case 19:
			handleTeleport(player, Location.create(2807, 10003, 0));// fremmenik
																	// dungeon

			break;
		case 20:
			handleTeleport(player, Location.create(2433, 3423, 0));// slayer
																	// dungeon

			break;
		case 21:
			handleTeleport(player, Location.create(3007, 9549, 0));// asgarnian
																	// dungeon

			break;
		case 22:
			handleTeleport(player, Location.create(2575, 9862, 0));// waterfall
																	// dungeon

			break;
		case 23:
			handleTeleport(player, Location.create(3745, 9374, 0));// mos'le
																	// dungeon

			break;
		case 24:
			handleTeleport(player, Location.create(1665, 10051, 0));// Kourend
																	// catacombs

			break;

		case 25:
			handleTeleport(player, Location.create(2027, 5611, 0));// Crashsite
																	// caverns

			break;
		case 26:
			handleTeleport(player, Location.create(1747, 5324, 0));// ancient
																	// caverns

			break;

		// Minigame teleports
		case 29:
			handleTeleport(player, Location.create(3565, 3316, 0));// barrows

			break;
		case 30:
			handleTeleport(player, Location.create(2659, 2659, 0));// pest
																	// control

			break;

		case 31:
			handleTeleport(player, Location.create(2881, 3546, 0));// warriors
																	// guild

			break;

		case 32:
			handleTeleport(player, Location.create(3316, 3235, 0));// duel
																	// arena

			break;

		case 33:
			handleTeleport(player, Location.create(2441, 5171, 0));// fight
																	// caves

			break;
		case 34:
			handleTeleport(player, Location.create(1631, 3942, 0));// wintertodt

			break;

		// Skilling teleports
		case 37:
			player.getActionSender().closeAll();
			DialogueManager.openDialogue(player, 100000); // Agility Teleports
			break;
		case 38:
			handleTeleport(player, Location.create(3048, 4823, 0)); // runecrafting

			break;
		case 39:
			player.getActionSender().closeAll();
			DialogueManager.openDialogue(player, 100006); // Thieving Teleports
			break;
		case 40:
			// DialogueManager.openDialogue(player, 100012); // Hunter Teleports
			player.sendMessage("We're sorry, but the Hunter skill is not available yet!");
			break;
		case 41:
			player.getActionSender().closeAll();
			DialogueManager.openDialogue(player, 100018); // Mining Teleports
			break;
		case 42:
			player.getActionSender().closeAll();
			DialogueManager.openDialogue(player, 100024); // Fishing Teleports
			break;
		case 43:
			player.getActionSender().closeAll();
			DialogueManager.openDialogue(player, 100030); // Woodcutting
															// Teleports
			break;
		case 44:
			player.getActionSender().closeAll();
			DialogueManager.openDialogue(player, 100036); // Farming Teleports
			break;

		// Boss teleports
		case 47:
			handleTeleport(player, Location.create(3506, 9493, 0));// kalphite
																	// queen

			break;
		case 48:
			handleTeleport(player, Location.create(3013, 3848, 0));// kbd

			break;
		case 49:
			handleTeleport(player, Location.create(2968, 4384, 2));// Corp

			break;
		case 50:
			handleTeleport(player, Location.create(3037, 4766, 0));// Abyssal
																	// sire

			break;
		case 51:
			handleTeleport(player, Location.create(2880, 5311, 2));// Godwars

			break;
		case 52:
			handleTeleport(player, Location.create(1464, 3688, 0));// shaman

			break;
		case 53:
			handleTeleport(player, Location.create(2199, 3056, 0));// Zulrah

			break;
		case 54:
			handleTeleport(player, Location.create(1910, 4367, 0));// Dag
																	// kings

			break;
		case 55:
			handleTeleport(player, Location.create(1761, 5197, 0));// Giant
																	// mole

			break;
		case 56:
			handleTeleport(player, Location.create(1258, 3564, 0));// Raids

			break;
		case 57:
			handleTeleport(player, Location.create(2872, 9847, 0));// cerberus

			break;

		// Wilderness teleports
		case 60:
			handleTeleport(player, Location.create(2539, 4716, 0));// Mage
																	// bank

			break;
		case 61:
			handleTeleport(player, Location.create(3202, 3859, 0));// Lava
																	// dragons

			break;
		case 62:
			handleTeleport(player, Location.create(3012, 3632, 0));// PvP
																	// Castle

			break;
		case 63:
			handleTeleport(player, Location.create(3184, 3953, 0));// Resource
																	// dungeon

			break;
		case 64:
			handleTeleport(player, Location.create(2985, 3596, 0));// west
																	// dragons

			break;
		case 65:
			handleTeleport(player, Location.create(3351, 3670, 0));// east
																	// dragons

			break;
		case 66:
			handleTeleport(player, Location.create(3254, 3910, 0)); // chaos ele
			break;
		case 67:
			handleTeleport(player, Location.create(3340, 3729, 0)); // venenatis
			break;
		case 68:
			handleTeleport(player, Location.create(2979, 3833, 0)); // chaos fanatic
			break;
		case 69:
			handleTeleport(player, Location.create(2968, 3693, 0)); // crazy archaeologist
			break;
		}
		return false;
	}

	/**
	 * Handles the actual teleporting.
	 * 
	 * @param player
	 *            the player
	 * @param location
	 *            the teleport location
	 */
	public static void handleTeleport(Player player, Location location) {
		player.teleport(location, 0, 0, true);
		player.lastLocation = location; // for last teleport destionation
		player.getActionSender().removeAllInterfaces().removeInterface();
	}
}