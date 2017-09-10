package org.rs2server.rs2.model.skills.hunter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.skills.hunter.Trap.TrapState;
import org.rs2server.rs2.util.Misc;

public class Hunter {

	/**
	 * The list which contains all Traps
	 */
	public static List<Trap> traps = new CopyOnWriteArrayList<>();

	/**
	 * The Hash map which contains all Hunting NPCS
	 */
	public static List<NPC> HUNTER_NPC_LIST = new CopyOnWriteArrayList<>();

	/**
	 * Can this client lay a trap here?
	 *
	 * @param client
	 */
	public static boolean canLay(Player client) {
		if (!goodArea(client)) {
			client.getActionSender().sendMessage("You need to be in a hunting area to lay a trap.");
			return false;
		}
		for (final Trap trap : traps) {
			if (trap == null)
				continue;
			if (trap.getGameObject().getLocation().getX() == client.getLocation().getX()
					&& trap.getGameObject().getLocation().getY() == client.getLocation().getY()) {
				client.getActionSender()
						.sendMessage("There is already a trap here, please place yours somewhere else.");
				return false;
			}
		}
		int x = client.getLocation().getX();
		int y = client.getLocation().getY();
		for (final NPC npc : HUNTER_NPC_LIST) {
			if (npc == null)
				continue;
			if (x == npc.getLocation().getX() && y == npc.getLocation().getY()
					|| x == npc.getLocation().getX() && y == npc.getLocation().getY()) {
				client.getActionSender()
						.sendMessage("You cannot place your trap right here, try placing it somewhere else.");

				return false;
			}
		}
		// if (client.getTrapsLaid() >= getMaximumTraps(client)) {
		// client.getActionSender().sendMessage(
		// "You can only have a max of " + getMaximumTraps(client) + " traps
		// setup at once.");
		// return false;
		// }
		return true;
	}

	public static void handleRegionChange(Player client) {
		// if (client.getTrapsLaid() > 0) {
		// for (final Trap trap : traps) {
		// if (trap == null)
		// continue;
		// if (trap.getOwner() != null &&
		// trap.getOwner().getName().equals(client.getName())
		// && !Location.hasDistance(trap.getGameObject().getLocation(),
		// client.getLocation(), 50)) {
		// deregister(trap);
		// client.getActionSender()
		// .sendMessage("You didn't watch over your trap well enough, it has
		// collapsed.");
		// }
		// }
		// }
	}

	/**
	 * Checks if the user is in the area where you can lay boxes.
	 *
	 * @param client
	 * @return
	 */
	public static boolean goodArea(Player client) {
		int x = client.getLocation().getX();
		int y = client.getLocation().getY();
		return x >= 2758 && x <= 2965 && y >= 2880 && y <= 2954;
	}

	/**
	 * Returns the maximum amount of traps this player can have
	 *
	 * @param client
	 * @return
	 */
	public static int getMaximumTraps(Player client) {
		return client.getSkills().getLevel(Skills.HUNTER) / 20 + 1;
	}

	/**
	 * Gets the ObjectID required by NPC ID
	 *
	 * @param npcId
	 */
	public static int getObjectIDByNPCID(int npcId) {
		switch (npcId) {
		case 5073:
			return 19180;
		case 5079:
			return 19191;
		case 5080:
			return 19189;
		case 5075:
			return 19184;
		case 5076:
			return 19186;
		case 5074:
			return 19182;
		case 5072:
			return 19178;
		}
		return 0;
	}

	/**
	 * Searches the specific Trap that belongs to this WorldObject
	 *
	 * @param object
	 */
	public static Trap getTrapForGameObject(final GameObject object) {
		for (final Trap trap : traps) {
			if (trap == null)
				continue;
			if (trap.getGameObject().getLocation().equals(object.getLocation()))
				return trap;
		}
		return null;
	}

	/**
	 * Dismantles a Trap
	 *
	 * @param client
	 */
	public static void dismantle(Player client, GameObject trap) {
		if (trap == null)
			return;
		final Trap theTrap = getTrapForGameObject(trap);
		if (theTrap != null && theTrap.getOwner() == client) {
			if (theTrap instanceof SnareTrap)
				client.getInventory().add(new Item(10006, 1));
			else if (theTrap instanceof BoxTrap) {
				client.getInventory().add(new Item(10008, 1));
				client.playAnimation(Animation.create(827));
			}
			client.getActionSender().sendMessage("You dismantle the trap..");
		} else
			client.getActionSender().sendMessage("You cannot dismantle someone else's trap.");
	}

	/**
	 * Sets up a trap
	 *
	 * @param client
	 * @param trap
	 */
	public static void layTrap(Player client, Trap trap) {
		int id = 10006;
		if (trap instanceof BoxTrap) {
			id = 10008;
			if (client.getSkills().getLevel(Skills.HUNTER) < 60) {
				client.getActionSender().sendMessage("You need a Hunter level of at least 60 to lay this trap.");
				return;
			}
		}
		if (!client.getInventory().contains(id))
			return;
		// if (canLay(client)) {
		// register(trap);
		// client.getClickDelay().reset();
		// client.getMovementQueue().reset();
		// PathFinder.moveStep(client);
		// client.setPositionToFace(trap.getGameObject().getLocation());
		// client.playAnimation(Animation.create(827));
		// if (trap instanceof SnareTrap) {
		// client.getActionSender().sendMessage("You set up a bird snare..");
		// client.getInventory().delete(10006, 1);
		// } else if (trap instanceof BoxTrap) {
		// if (client.getSkills().getLevel(Skills.HUNTER) < 27) {
		// client.getActionSender()
		// .sendMessage("You need a Hunter level of at least 27 to do this.");
		// return;
		// }
		// client.getActionSender().sendMessage("You set up a box trap..");
		// client.getInventory().delete(10008, 1);
		// }
		// //HunterTrapsTask.fireTask();
		// }
	}

	/**
	 * Gets the required level for the NPC.
	 *
	 * @param npcType
	 */
	public static int requiredLevel(int npcType) {
		int levelToReturn = 1;
		if (npcType == 5072)
			levelToReturn = 19;
		else if (npcType == 5072)
			levelToReturn = 1;
		else if (npcType == 5074)
			levelToReturn = 11;
		else if (npcType == 5075)
			levelToReturn = 5;
		else if (npcType == 5076)
			levelToReturn = 9;
		else if (npcType == 5079)
			levelToReturn = 53;
		else if (npcType == 5080)
			levelToReturn = 63;
		return levelToReturn;
	}

	public static boolean isHunterNPC(int npc) {
		return npc >= 5072 && npc <= 5080;
	}

	public static void lootTrap(Player client, GameObject trap) {
		if (trap != null) {
			// client.setPositionToFace(trap.getLocation());
			final Trap theTrap = getTrapForGameObject(trap);
			if (theTrap != null) {
				if (theTrap.getOwner() != null)
					if (theTrap.getOwner() == client) {
						if (theTrap instanceof SnareTrap) {
							client.getInventory().add(new Item(10006, 1));
							client.getInventory().add(new Item(526, 1));
							if (theTrap.getGameObject().getId() == 19180) {

								client.getInventory().add(new Item(10088, 20 + Misc.random(30)));
								client.getInventory().add(new Item(9978, 1));
								client.getActionSender().sendMessage("You've successfully caught a crimson swift.");
								client.getSkills().addExperience(Skills.HUNTER, 34);

							} else if (theTrap.getGameObject().getId() == 19184) {

								client.getInventory().add(new Item(10090, 20 + Misc.random(30)));
								client.getInventory().add(new Item(9978, 1));
								client.getActionSender().sendMessage("You've successfully caught a Golden Warbler.");
								client.getSkills().addExperience(Skills.HUNTER, 47);

							} else if (theTrap.getGameObject().getId() == 19186) {

								client.getInventory().add(new Item(10091, 20 + Misc.random(50)));
								client.getInventory().add(new Item(9978, 1));
								client.getActionSender().sendMessage("You've successfully caught a Copper Longtail.");
								client.getSkills().addExperience(Skills.HUNTER, 61);

							} else if (theTrap.getGameObject().getId() == 19182) {

								client.getInventory().add(new Item(10089, 20 + Misc.random(30)));
								client.getInventory().add(new Item(9978, 1));
								client.getActionSender().sendMessage("You've successfully caught a Cerulean Twitch.");
								client.getSkills().addExperience(Skills.HUNTER, 65);

							} else if (theTrap.getGameObject().getId() == 19178) {

								client.getInventory().add(new Item(10087, 20 + Misc.random(30)));
								client.getInventory().add(new Item(9978, 1));
								client.getActionSender().sendMessage("You've successfully caught a Tropical Wagtail.");
								client.getSkills().addExperience(Skills.HUNTER, 95);

							}
						} else if (theTrap instanceof BoxTrap) {
							client.getInventory().add(new Item(10008, 1));

							if (theTrap.getGameObject().getId() == 19191) {

								client.getInventory().add(new Item(10033, 1));
								client.getSkills().addExperience(Skills.HUNTER, 198);
								client.getActionSender().sendMessage("You've successfully caught a chinchompa!");

							} else if (theTrap.getGameObject().getId() == 19189) {

								client.getInventory().add(new Item(10034, 1));
								client.getSkills().addExperience(Skills.HUNTER, 315);
								client.getActionSender().sendMessage("You've successfully caught a red chinchompa!");

							}
						}
						client.playAnimation(Animation.create(827));
					} else
						client.getActionSender().sendMessage("This is not your trap.");
			}
		}

	}

	/**
	 * Try to catch an NPC
	 *
	 * @param trap
	 * @param npc
	 */
	public static void catchNPC(Trap trap, NPC npc) {
		if (trap.getTrapState().equals(TrapState.CAUGHT))
			return;
		if (trap.getOwner() != null) {
			if (trap.getOwner().getSkills().getLevel(22) < requiredLevel(npc.getId())) {
				trap.getOwner().getActionSender()
						.sendMessage("You failed to catch the animal because your Hunter level is too low.");
				trap.getOwner().getActionSender().sendMessage(
						"You need at least " + requiredLevel(npc.getId()) + " Hunter to catch this animal");
				return;
			}
			if (trap instanceof SnareTrap)
				HUNTER_NPC_LIST.remove(npc);
		}
	}

	public static boolean hasLarupia(Player client) {
		return client.getEquipment().getItems()[Equipment.SLOT_HELM].getId() == 10045
				&& client.getEquipment().getItems()[Equipment.SLOT_CHEST].getId() == 10043
				&& client.getEquipment().getItems()[Equipment.SLOT_BOTTOMS].getId() == 10041;
	}
}
