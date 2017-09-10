package org.rs2server.rs2.model.minigame.warriorsguild;

import org.rs2server.cache.format.CacheItemDefinition;
import org.rs2server.rs2.action.Action;
import org.rs2server.rs2.event.Event;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.DialogueManager;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.container.Inventory;
import org.rs2server.rs2.model.npc.MetalArmour;
import org.rs2server.rs2.model.npc.NPCDefinition;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.skills.Agility;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WarriorsGuild {

	/**
	 * The java.util.Random instance used for warious things.
	 */
	private static final Random r = new Random();

	/**
	 * A list of all the players inside the Cyclops arena.
	 */
	public static final List<Player> IN_GAME = new ArrayList<Player>();

	/**
	 * The tokens we're rewarded through all games in Warriors Guild.
	 */
	public static final int TOKENS = 8851;

	/**
	 * Set of all the Armour items used for the Animation Room. (Bronze - Rune)
	 * {helm, chest, legs}
	 */
	private static final int[][] ARMOUR_SET = { { 1155, 1117, 1075 }, // Bronze
			{ 1153, 1115, 1067 }, // Iron
			{ 1157, 1119, 1069 }, // Steel
			{ 1165, 1125, 1077 }, // Black
			{ 1159, 1121, 1071 }, // Mithril
			{ 1161, 1123, 1073 }, // Adamant
			{ 1163, 1127, 1079 }, // Rune
	};

	/**
	 * Set of all the animated Armour, with indexes corresponding with the indexes
	 * from the 2-d array above.
	 */
	private static final int[] ANIMATED_ARMOURS = { 2450, // Animated Bronze Armour
			2451, // Animated Iron Armour
			2452, // Animated Steel Armour
			2453, // Animated Black Armour
			2454, // Animated Mithril Armour
			2455, // Animated Adamant Armour
			2456, // Animated Rune Armour
	};

	/**
	 * The locations of the two Animator objects, to prevent client hacks. (Simply
	 * editing the file ID)
	 */
	private static final Location ANIMATOR_1 = Location.create(2851, 3536, 0);
	private static final Location ANIMATOR_2 = Location.create(2857, 3536, 0);

	/**
	 * Locations to stand at before placing the Armour's on the animator..
	 */
	private static final Location ANIMATOR_1_STAND = Location.create(2851, 3537, 0);// TODO Fix walking
	private static final Location ANIMATOR_2_STAND = Location.create(2857, 3537, 0);

	private static final Animation BONE_BURYING_ANIMATION = Animation.create(827);

	/**
	 * Checks if a specific NPC id is an Animated Armour.
	 * 
	 * @return <code>true</code> if, <code>false</code> if not.
	 */
	public static boolean isMetalArmour(int id) {
		for (int armour : ANIMATED_ARMOURS) {
			if (armour == id) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Constructs the Warriors guild MiniGame for a specific player.
	 * 
	 * @param player
	 *            The player to construct the Warriors Guild MiniGame for.
	 */
	public WarriorsGuild(Player player) {
		this.player = player;
	}

	/**
	 * This handles the chance of armour pieces crumbling to dust.
	 */
	public void dropArmours() {
		assert currentArmour != null;
		boolean destroyed = false;
		for (int index = 0; index < ANIMATED_ARMOURS.length; index++) {
			if (ANIMATED_ARMOURS[index] == currentArmour.getDefinition().getId()) {
				for (final int armour : ARMOUR_SET[index]) {
					/*
					 * Slight chance, based on how good the Armour is, for the Armour to be
					 * destroyed..
					 */
					if (r.nextInt((index + 1) * 50) == 0 && !destroyed) {
						player.getActionSender().sendMessage("Unfortunately your "
								+ CacheItemDefinition.get(armour).getName().toLowerCase() + " crumbled to dust..");
						destroyed = true;
					}
				}
			}
		}
		currentArmour = null;
	}

	private Item defenderDrop = null;

	/**
	 * Called everytime this player is killing a cyclop. This will randomly drop the
	 * next RuneDefender for us.
	 */
	public void onCyclopsKill(Location pos) {
		defenderDrop = getNextDefender(getCurrentDefender());
		/* Make sure our player is actually in game.. */
		if (IN_GAME.contains(player)) {
			if (r.nextInt(15) == 0) { // XXX change to higher number
				Inventory.addDroppable(player, defenderDrop);
				player.sendMessage("<col=ff0000>You've received a " + defenderDrop.getDefinition2().getName()
						+ " drop from the Cyclops!");
			}
		}
	}

	/**
	 * Called once this player runs out of tokens..
	 */
	public void outOfTokens() {
		DialogueManager.openDialogue(player, 32);
		player.sendMessage("<col=ff0000>You're out of Warrior guild tokens; leave before Kamfreena kicks you out!");

		World.getWorld().submit(new Tickable(41) {

			@Override
			public void execute() {
				if (IN_GAME.contains(player)) {
					DialogueManager.openDialogue(player, 34);
					IN_GAME.remove(player);
					player.setTeleportTarget(Location.create(2844, 3539, 2));
				}
				this.stop();
			}
		});
	}

	/**
	 * Gets the next defender drop, based on the players currently worn defender.
	 * 
	 * @return The next defender to drop.
	 */
	private Item getNextDefender(Item currentDefender) {
		if (currentDefender == null) {
			return new Item(8844);
		} else if (currentDefender.getId() == 12954) {
			return currentDefender;
		} else {
			return currentDefender.getId() == 8850 ? new Item(12954) : new Item(currentDefender.getId() + 1);
		}
	}

	private static final Location ENTER_LOC = Location.create(2847, 3540, 2);

	public boolean handleDoorClick(Location loc) {
		if (loc.equals(GAME_DOOR_1) || loc.equals(GAME_DOOR_2)) {
			if (IN_GAME.contains(player)) {
				player.setTeleportTarget(ENTER_LOC.transform(-1, 0, 0));
				IN_GAME.remove(player);
			} else {
				// Make sure we have at least 100 tokens..
				if (player.getInventory().getCount(8851) < 100)
					player.getActionSender().sendItemDialogue(8851,
							"You need at least 100 Warrior Guild tokens to enter.");
				else {
					player.setTeleportTarget(ENTER_LOC);
					IN_GAME.add(player);
					defenderDrop = getNextDefender(getCurrentDefender());
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Gets the players currently worn Defender.
	 * 
	 * @return null if nothing found, else the players highest worn Defender.
	 */
	private Item getCurrentDefender() {
		Item defender = null; // The best defender so far.
		Item shield = player.getEquipment().get(Equipment.SLOT_SHIELD);
		
		// First check if we have one equipped
		if (shield != null && shield.getDefinition2().getName().contains("defender"))
			defender = shield;

		Item[] inv = player.getInventory().toArray();

		// Then check if we have one in inventory
		for (final Item item : inv) {
			if (item != null) {
				CacheItemDefinition defs = item.getDefinition2();
				if (defs != null && defs.getName().contains("defender")) {
					// The one with the highest ID is the best one. xD
					if (defender == null || defender.getId() < item.getId())
						defender = item;
				}
			}
		}
		return defender;
	}

	public static final Location GAME_DOOR_1 = Location.create(2847, 3540, 2);
	private static final Location GAME_DOOR_2 = Location.create(2847, 3541, 2);

	/**
	 * Handles any item on object actions to do with Warriors Guild.
	 * 
	 * @param item
	 *            The item used on an object.
	 * @param objectId
	 *            The object id.
	 * @param loc
	 *            The location of the object.
	 * @return <code>true</code> if there was an action to handle,
	 *         <code>false</code> if not.
	 */
	public boolean handleItemOnObject(final Item item, int objectId, final Location loc) {
		/*
		 * Magical Animator
		 */
		if (objectId == 23955 && (loc.equals(ANIMATOR_1) || loc.equals(ANIMATOR_2))) {
			/*
			 * The client will automatically walk to a specific point, lets wait for it.
			 * (Normal within distance thing doesn't work..)
			 */
			player.getActionQueue().clearAllActions();
			final Location walkTo = loc.equals(ANIMATOR_1) ? ANIMATOR_1_STAND : ANIMATOR_2_STAND;
			player.getActionQueue().addAction(new Action(player, 0) {

				@Override
				public void execute() {
					if (player.getLocation().equals(walkTo)) {
						for (int index = 0; index < ARMOUR_SET.length; index++) {
							for (int armour : ARMOUR_SET[index]) {
								if (armour == item.getId()) {
									boolean stop = false;
									for (int armour1 : ARMOUR_SET[index]) {
										if (!player.getInventory().contains(armour1)) {
											String name = CacheItemDefinition.get(armour1).getName().toLowerCase();
											player.getActionSender().sendMessage("You're missing "
													+ Misc.withPrefix(name) + " in order to animate this armour.");
											stop = true;
										}
									}
									if (!stop) {
										if (currentArmour == null) {
											for (int armour1 : ARMOUR_SET[index])
												player.getInventory().remove(new Item(armour1));
											player.playAnimation(BONE_BURYING_ANIMATION);
											player.setAttribute("busy", true);
											final int fIndex = index;

											World.getWorld().submit(new Event(1200) {

												@Override
												public void execute() {
													int[] forceMovementVars = { 0, 0, 0, 3, 0, 20, 60, 1, 0 };
													Agility.forceMovement(player, player.getWalkAnimation(),
															forceMovementVars, 3, false);
													World.getWorld().submit(new Event(2400) {

														@Override
														public void execute() {
															player.removeAttribute("busy");
															player.getActionSender().removeInterface();
															currentArmour = new MetalArmour(
																	NPCDefinition.forId(ANIMATED_ARMOURS[fIndex]), loc,
																	player);

															player.face(loc);
															World.getWorld().register(currentArmour);

															currentArmour.getCombatState().startAttacking(player,
																	false);

															/*
															 * Place a hint icon above the NPC..
															 */
															player.getActionSender().setTargetHintIcon(currentArmour);
															this.stop();
														}
													});
													this.stop();
												}
											});
											this.stop();

										} else
											player.getActionSender().sendMessage(
													"You've already animated a set of armour; finish it off!");
									}
								}
							}
						}
						this.stop();
					}
					this.setTickDelay(500);
				}

				@Override
				public CancelPolicy getCancelPolicy() {
					return CancelPolicy.ALWAYS;
				}

				@Override
				public StackPolicy getStackPolicy() {
					return StackPolicy.NEVER;
				}

				@Override
				public AnimationPolicy getAnimationPolicy() {
					return AnimationPolicy.RESET_ALL;
				}

			});
			/*
			 * We handled what stuff related to the Animator, and every thing is cool.
			 * (Missing the "nothing interesting happens" though, but I couldn't think of
			 * other ways. *Shitty ActionSystem*
			 */
			return true;
		}
		return false;
	}

	/**
	 * Gets the current animated Armour spawned.
	 * 
	 * @return The currently spawned animation Armour.
	 */
	public MetalArmour getCurrentArmour() {
		return currentArmour;
	}

	/**
	 * The currently summoned Metal Armour.
	 */
	private MetalArmour currentArmour = null;

	/**
	 * The player we're going to handle Warriors Guild for.
	 */
	private final Player player;

	public void setCurrentArmour(MetalArmour armour) {
		this.currentArmour = armour;
	}

	/**
	 * Shanomi's random force chat.
	 */
	public static String[] SHANOMI_QUOTES = { "Those things which cannot be seen, perceive them.",
			"Do nothing which is of no use.", "Think not dishonestly.", "The Way in training is.",
			"Gain and loss between you must distinguish.", "Trifles pay attention even to.",
			"Way of the warrior this is.", "Acquainted with every art become.", "Ways of all professions know you.",
			"Judgment and understanding for everything develop you must." };
}