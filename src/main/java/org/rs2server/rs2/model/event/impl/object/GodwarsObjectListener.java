package org.rs2server.rs2.model.event.impl.object;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.Animation.FacialAnimation;
import org.rs2server.rs2.model.event.*;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.ActionSender.DialogueType;

/**
 * Handles everything object-related in the Godwars dungeon.
 * 
 * @author Vichy
 */
public class GodwarsObjectListener extends EventListener {

	@Override
	public void register(ClickEventManager manager) {
		manager.registerObjectListener(26419, this); // main entrance
		manager.registerObjectListener(26370, this); // main exit - rope

		manager.registerObjectListener(26461, this); // bandos stronghold room
		manager.registerObjectListener(26503, this); // bandos boss room door
		manager.registerObjectListener(26366, this); // bandos boss room altar

		manager.registerObjectListener(26561, this); // saradomin encampment 1
		manager.registerObjectListener(26562, this); // saradomin encampment 2
		manager.registerObjectListener(26504, this); // saradomin boss room door
		manager.registerObjectListener(26364, this); // saradomin boss room
														// altar

		manager.registerObjectListener(26380, this); // armadyl eyric room
		manager.registerObjectListener(26502, this); // armadyl boss room door
		manager.registerObjectListener(26365, this); // armadyl boss room door

		manager.registerObjectListener(26505, this); // zamorak boss room door
		manager.registerObjectListener(26363, this); // zamorak boss room altar

		// manager.registerObjectListener(26518, this); // zamorak pre-boss room
	}

	@Override
	public boolean objectAction(final Player player, int objectId, GameObject gameObject, Location location,
			ClickOption option) {

		if (Constants.DEBUG)
			player.sendMessage("This is a GodwarsObject");

		switch (objectId) {

		case 26419:
			player.climbStairsDown(Location.create(2882, 5310, 2));
			break;
		case 26370:
			player.climbStairsUp(Location.create(2916, 3746, 0));
			break;

		/** Start of Bandos **/
		case 26461:
			if (player.getSkills().getLevelForExperience(Skills.STRENGTH) < 70) {
				player.sendMessage("You need at least a level of 70 Strength in order to enter this room.");
				break;
			}
			if (!player.getInventory().contains(2347) && player.getLocation().getX() >= 2851) {
				player.sendMessage("You will need a hammer to bang on this door.");
				break;
			}
			Location loc = Location.create(player.getLocation().getX() >= 2851 ? 2850 : 2852, 5333, 2);
			player.handleAutoWalk(gameObject, loc, 3);
			break;
		case 26503:
			if (player.getX() >= 2863) {
				player.sendMessage("You cannot go through the door from this side; use the altar to exit!");
				break;
			}
			player.setTeleportTarget(Location.create(2864, 5354, 2));
			break;
		case 26366:
			if (option == ClickOption.FIRST) {
				if (player.getAttribute("GWD_Bandos") != null) {
					long time = player.getAttribute("GWD_Bandos");
					if (time > System.currentTimeMillis()) {
						player.getActionSender().sendDialogue("Bandos's Altar", DialogueType.MESSAGE, 0,
								FacialAnimation.DEFAULT, "You've recently already prayed at this Gods altar.");
						break;
					}
				}
				if (player.getSkills().getPrayerPoints() < player.getSkills().getLevelForExperience(Skills.PRAYER)) {
					player.getSkills().setLevel(Skills.PRAYER, player.getSkills().getLevelForExperience(Skills.PRAYER));
					player.getSkills().setPrayerPoints(player.getSkills().getLevelForExperience(Skills.PRAYER), true);
					if (player.getActionSender() != null)
						player.getActionSender().sendSkills();
					player.getActionSender().sendMessage("You pray at the altar...");
					player.playAnimation(Animation.create(645));
					player.setAttribute("GWD_Bandos", System.currentTimeMillis() + 900000); // 15 minutes
					break;
				}
				player.getActionSender().sendDialogue("Bandos's Altar", DialogueType.MESSAGE, 0,
						FacialAnimation.DEFAULT, "You already have full prayer points.");
				break;
			}
			player.setTeleportTarget(Location.create(2860, 5354, 2));
			break;

		/** Start of Saradomin **/
		case 26561:
			if (player.getSkills().getLevelForExperience(Skills.AGILITY) < 70) {
				player.sendMessage("You need at least a level of 70 Agility to handle this obstacle.");
				break;
			}
			if (!player.getInventory().contains(954)) {
				player.sendMessage("You will need a rope to get down there.");
				break;
			}
			player.setTeleportTargetObj(Location.create(2914, 5300, 1));
			break;
		case 26562:
			if (player.getSkills().getLevelForExperience(Skills.AGILITY) < 70) {
				player.sendMessage("You need at least a level of 70 Agility to handle this obstacle.");
				break;
			}
			if (!player.getInventory().contains(954)) {
				player.sendMessage("You will need a rope to get down there.");
				break;
			}
			player.setTeleportTargetObj(Location.create(2920, 5274, 0));
			break;
		case 26504:
			if (player.getX() <= 2908) {
				player.sendMessage("You cannot go through the door from this side; use the altar to exit!");
				break;
			}
			player.setTeleportTarget(Location.create(2907, 5265, 0));
			break;
		case 26364:
			if (option == ClickOption.FIRST) {
				if (player.getAttribute("GWD_Sara") != null) {
					long time = player.getAttribute("GWD_Sara");
					if (time > System.currentTimeMillis()) {
						player.getActionSender().sendDialogue("Saradomin's Altar", DialogueType.MESSAGE, 0,
								FacialAnimation.DEFAULT, "You've recently already prayed at this Gods altar.");
						break;
					}
				}
				if (player.getSkills().getPrayerPoints() < player.getSkills().getLevelForExperience(Skills.PRAYER)) {
					player.getSkills().setLevel(Skills.PRAYER, player.getSkills().getLevelForExperience(Skills.PRAYER));
					player.getSkills().setPrayerPoints(player.getSkills().getLevelForExperience(Skills.PRAYER), true);
					if (player.getActionSender() != null)
						player.getActionSender().sendSkills();
					player.getActionSender().sendMessage("You pray at the altar...");
					player.playAnimation(Animation.create(645));
					player.setAttribute("GWD_Sara", System.currentTimeMillis() + 900000); // 15 minutes
					break;
				}
				player.getActionSender().sendDialogue("Saradomin's Altar", DialogueType.MESSAGE, 0,
						FacialAnimation.DEFAULT, "You already have full prayer points.");
				break;
			}
			player.setTeleportTarget(Location.create(2912, 5265, 0));
			break;

		/** Start of Armadyl **/
		case 26380:
			if (player.getSkills().getLevelForExperience(Skills.RANGE) < 70) {
				player.sendMessage("You need at least a level of 70 Ranged to handle this obstacle.");
				break;
			}
			// TODO Proper grapple animation
			loc = Location.create(2872, (player.getY() <= 5269 ? 5279 : 5269), 2);
			player.setTeleportTarget(loc);
			break;
		case 26502:
			if (player.getY() >= 5295) {
				player.sendMessage("You cannot go through the door from this side; use the altar to exit!");
				break;
			}
			player.setTeleportTarget(Location.create(2839, 5296, 2));
			break;
		case 26365:
			if (option == ClickOption.FIRST) {

				if (player.getAttribute("GWD_Arma") != null) {
					long time = player.getAttribute("GWD_Arma");
					if (time > System.currentTimeMillis()) {
						player.getActionSender().sendDialogue("Armadyl's Altar", DialogueType.MESSAGE, 0,
								FacialAnimation.DEFAULT, "You've recently already prayed at this Gods altar.");
						break;
					}
				}
				if (player.getSkills().getPrayerPoints() < player.getSkills().getLevelForExperience(Skills.PRAYER)) {
					player.getSkills().setLevel(Skills.PRAYER, player.getSkills().getLevelForExperience(Skills.PRAYER));
					player.getSkills().setPrayerPoints(player.getSkills().getLevelForExperience(Skills.PRAYER), true);
					if (player.getActionSender() != null)
						player.getActionSender().sendSkills();
					player.getActionSender().sendMessage("You pray at the altar...");
					player.playAnimation(Animation.create(645));
					player.setAttribute("GWD_Arma", System.currentTimeMillis() + 900000); // 15 minutes
					break;
				}
				player.getActionSender().sendDialogue("Armadyl's Altar", DialogueType.MESSAGE, 0,
						FacialAnimation.DEFAULT, "You already have full prayer points.");
				break;
			}
			player.setTeleportTarget(Location.create(2839, 5290, 2));
			break;

		/** Start of Zamorak **/
		case 26505:
			if (player.getY() < 2925) {
				player.sendMessage("You cannot go through the door from this side; use the altar to exit!");
				break;
			}
			player.setTeleportTarget(Location.create(2925, 5331, 2));
			break;
		case 26363:
			if (option == ClickOption.FIRST) {
				if (player.getAttribute("GWD_Zammy") != null) {
					long time = player.getAttribute("GWD_Zammy");
					if (time > System.currentTimeMillis()) {
						player.getActionSender().sendDialogue("Zamorak's Altar", DialogueType.MESSAGE, 0,
								FacialAnimation.DEFAULT, "You've recently already prayed at this Gods altar.");
						break;
					}
				}
				if (player.getSkills().getPrayerPoints() < player.getSkills().getLevelForExperience(Skills.PRAYER)) {
					player.getSkills().setLevel(Skills.PRAYER, player.getSkills().getLevelForExperience(Skills.PRAYER));
					player.getSkills().setPrayerPoints(player.getSkills().getLevelForExperience(Skills.PRAYER), true);
					if (player.getActionSender() != null)
						player.getActionSender().sendSkills();
					player.getActionSender().sendMessage("You pray at the altar...");
					player.playAnimation(Animation.create(645));
					player.setAttribute("GWD_Zammy", System.currentTimeMillis() + 900000); // 15 minutes
					break;
				}
				player.getActionSender().sendDialogue("Zamorak's Altar", DialogueType.MESSAGE, 0,
						FacialAnimation.DEFAULT, "You already have full prayer points.");
				break;
			}
			player.setTeleportTarget(Location.create(2925, 5339, 2));
			break;
		}
		return true;
	}
}