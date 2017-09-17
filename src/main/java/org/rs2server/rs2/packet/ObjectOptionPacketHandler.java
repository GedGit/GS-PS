package org.rs2server.rs2.packet;

import org.rs2server.Server;
import org.rs2server.cache.format.CacheObjectDefinition;
import org.rs2server.rs2.Constants;
import org.rs2server.rs2.action.Action;
import org.rs2server.rs2.action.impl.ClimbLadderAction;
import org.rs2server.rs2.content.*;
import org.rs2server.rs2.content.api.*;
import org.rs2server.rs2.content.areas.CoordinateEvent;
import org.rs2server.rs2.content.misc.*;
import org.rs2server.rs2.content.wintertodt.BrazierAction;
import org.rs2server.rs2.content.wintertodt.BrazierAction.Lightables;
import org.rs2server.rs2.domain.service.api.*;
import org.rs2server.rs2.domain.service.api.content.*;
import org.rs2server.rs2.domain.service.impl.content.BankDepositBoxServiceImpl;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.Animation.FacialAnimation;
import org.rs2server.rs2.model.Mob.InteractionMode;
import org.rs2server.rs2.model.cm.Content;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction.SpellBook;
import org.rs2server.rs2.model.container.*;
import org.rs2server.rs2.model.event.ClickEventManager;
import org.rs2server.rs2.model.event.EventListener.ClickOption;
import org.rs2server.rs2.model.gameobject.GameObjectCardinality;
import org.rs2server.rs2.model.map.RegionClipping;
import org.rs2server.rs2.model.map.path.ObjectPathFinder;
import org.rs2server.rs2.model.map.path.ObjectPathFinder.Orientation;
import org.rs2server.rs2.model.minigame.impl.Barrows;
import org.rs2server.rs2.model.minigame.impl.WarriorsGuild;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.region.Region;
import org.rs2server.rs2.model.skills.*;
import org.rs2server.rs2.model.skills.Agility.Obstacle;
import org.rs2server.rs2.model.skills.Cooking.*;
import org.rs2server.rs2.model.skills.Mining.Rock;
import org.rs2server.rs2.model.skills.ThievingAction.ThievingStalls;
import org.rs2server.rs2.model.skills.Woodcutting.Tree;
import org.rs2server.rs2.model.skills.runecrafting.Runecrafting;
import org.rs2server.rs2.model.skills.smithing.*;
import org.rs2server.rs2.model.skills.smithing.SmithingUtils.ForgingBar;
import org.rs2server.rs2.model.skills.thieving.WallSafeCracking;
import org.rs2server.rs2.net.*;
import org.rs2server.rs2.net.ActionSender.DialogueType;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

/**
 * Object option packet handler.
 *
 * @author Graham Edgecombe
 */
public class ObjectOptionPacketHandler implements PacketHandler {

	private static final int OPTION_1 = 166, OPTION_2 = 188, OPTION_3 = 218, ITEM_ON_OBJECT = 238, OPTION_EXAMINE = 101,
			OPTION_SPELL = 156, OPTION_4 = 184, OPTION_5 = 66;

	public ObjectOptionPacketHandler() {
		this.hookService = Server.getInjector().getInstance(HookService.class);
		this.pathfindingService = Server.getInjector().getInstance(PathfindingService.class);
		this.permissionService = Server.getInjector().getInstance(PermissionService.class);
		this.itemService = Server.getInjector().getInstance(ItemService.class);
		this.lootingBagService = Server.getInjector().getInstance(LootingBagService.class);
	}

	@Override
	public void handle(Player player, Packet packet) {

		boolean starter = player.getAttribute("starter");
		if (starter) {
			DialogueManager.openDialogue(player, 19000);
			return;
		}
		if (player.getAttribute("busy") != null)
			return;
		if (player.isLighting())
			return;
		player.resetAfkTolerance();
		player.getActionSender().removeChatboxInterface();
		if (player.getCombatState().isDead())
			return;
		if (player.getInterfaceState().isInterfaceOpen(Equipment.SCREEN)
				|| player.getInterfaceState().isInterfaceOpen(Bank.BANK_INVENTORY_INTERFACE)
				|| player.getInterfaceState().isInterfaceOpen(Trade.TRADE_INVENTORY_INTERFACE)
				|| player.getInterfaceState().isInterfaceOpen(Shop.SHOP_INVENTORY_INTERFACE)
				|| player.getInterfaceState().isInterfaceOpen(PriceChecker.PRICE_INVENTORY_INTERFACE)) {
			player.getActionSender().removeInventoryInterface();
		}
		player.getInterfaceState().setOpenShop(-1);
		player.getActionQueue().clearAllActions();
		player.getActionManager().stopAction();
		switch (packet.getOpcode()) {
		case OPTION_1:
			handleOption1(player, packet);
			break;
		case OPTION_2:
			handleOption2(player, packet);
			break;
		case OPTION_3:
			handleOption3(player, packet);
			break;
		case OPTION_4:
			handleOption4(player, packet);
			break;
		case OPTION_5:
			handleOption5(player, packet);
			break;
		case OPTION_EXAMINE:
			handleOptionExamine(player, packet);
			break;
		case ITEM_ON_OBJECT:
			handleItemOnObject(player, packet);
			break;
		case OPTION_SPELL:
			handleOptionSpell(player, packet);
			break;
		}
	}

	@SuppressWarnings("unused")
	private void handleOptionSpell(Player player, Packet packet) {
		int spellId = packet.getInt1();
		int objectId = packet.getShort();
		int z = packet.getByteS();
		int x = packet.getShortA();
		int y = packet.getLEShort();
		int f = packet.getLEShort();
		final Location loc = Location.create(x, y, z);
		Region r = player.getRegion();
		final GameObject obj = r.getGameObject(loc, objectId);
		if (obj == null || obj.getId() != objectId)
			return;
		CacheObjectDefinition def = CacheObjectDefinition.forID(objectId);
		player.getCombatState().setQueuedSpell(null);
		player.resetInteractingEntity();
		player.getActionQueue().clearAllActions();
		player.getActionSender().removeAllInterfaces();
		pathfindingService.travelToObject(player, obj);
		player.faceObject(obj);
		// System.out.println(spellId);
		final Action action = new Action(player, 0) {

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

			@Override
			public void execute() {
				this.stop();
				hookService.post(new GameObjectSpellEvent(player, obj, spellId));
			}
		};
		double dist = player.getLocation().distance(loc);
		if (dist <= 1)
			player.getActionQueue().addAction(action);
		else {
			World.getWorld().submitAreaEvent(player,
					new CoordinateEvent(player, x, y, def.getSizeX(), def.getSizeY(), obj.getDirection()) {

						@Override
						public void execute() {
							player.getActionQueue().addAction(action);
						}
					});
		}
	}

	private void handleOptionExamine(Player player, Packet packet) {
		int objectId = packet.getLEShort();

		CacheObjectDefinition def = CacheObjectDefinition.forID(objectId);
		if (def != null)
			player.getActionSender().sendMessage("It's an " + def.objectName + "."); // def.description.toString();

		if (Constants.DEBUG)
			player.sendMessage("Object Examine: " + objectId + ".");
	}

	/**
	 * Handles the option 1 packet.
	 *
	 * @param player
	 *            The player.
	 * @param packet
	 *            The packet.
	 */
	private void handleOption1(final Player player, Packet packet) {
		int x = packet.getShort();
		int y = packet.getShort();
		packet.getByteS();
		final int objectId = packet.getLEShort();
		int z = player.getLocation().getPlane();
		if (player.getAttribute("temporaryHeight") != null)
			z = player.getAttribute("temporaryHeight");
		final Location loc = Location.create(x, y, z);
		Region r = player.getRegion();
		final GameObject obj = r.getGameObject(loc, objectId);
		if (obj == null || obj.getId() != objectId)
			return;
		CacheObjectDefinition def = CacheObjectDefinition.forID(objectId);

		if (Constants.DEBUG)
			player.sendMessage("Object Option1: " + obj.getId() + ", Location: " + obj.getLocation().toString()
					+ ", rotation: " + obj.getDirection() + ".");

		player.getCombatState().setQueuedSpell(null);
		player.resetInteractingEntity();
		player.getActionQueue().clearAllActions();
		player.getActionSender().removeAllInterfaces();

		final Obstacle obstacle = Obstacle.forLocation(loc);

		if (obj.getId() == 10777) {
			if (player.getX() < 3191)
				return;
			Agility.tackleObstacle(player, obstacle, obj);
			return;
		}

		pathfindingService.travelToObject(player, obj);

		// TODO !
		if (objectId == 26518) { // zamorak pre-boss room
			if (player.getSkills().getLevelForExperience(Skills.HITPOINTS) < 70) {
				player.sendMessage("You need at least a level of 70 Hitpoints to handle this obstacle.");
				return;
			}
			// TODO Swim across animation
			Location travelLoc = Location.create(2885, (y == 5333 ? 5347 : 5330), 2);
			if ((travelLoc.getY() == 5347 && player.getY() >= 5329)
					|| (travelLoc.getY() == 5330 && player.getY() <= 5348))
				player.setTeleportTarget(travelLoc);
			return;
		}

		if (objectId == 29322 && player.getLocation().isWithinDistance(obj.getLocation(), 4)) { // Wintertodt door TODO
			if (player.getSkills().getLevelForExperience(Skills.FIREMAKING) < 50) {
				player.sendMessage("You need at least a level of 50 Firemaking to enter Wintertodt.");
				return;
			}
			Location zz = Location.create(1630, (player.getY() >= 3968 ? 3963 : 3968));
			player.setTeleportTargetObj(zz);
		}

		Action action;

		final Tree tree = Tree.forId(objectId);
		final Rock rock = Rock.forId(objectId);

		final ThievingStalls stall = ThievingAction.THIEVING_STALLS.get(objectId);

		if (stall != null)
			action = new ThievingAction(player, obj);
		else if (tree != null)
			action = new Woodcutting(player, obj);
		else if (rock != null)
			action = new Mining(player, obj);
		else if (obstacle != null) {
			action = new Action(player, 0) {
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

				@Override
				public void execute() {
					this.stop();
					Agility.tackleObstacle(player, obstacle, obj);
				}
			};
		} else {

			action = new Action(player, 0) {
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

				@Override
				public void execute() {
					this.stop();

					hookService.post(new GameObjectActionEvent(player, GameObjectActionEvent.ActionType.OPTION_1, obj));

					if (ClickEventManager.getEventManager().handleObjectOption(player, obj.getId(), obj,
							obj.getLocation(), ClickOption.FIRST)) {
						return;
					} else if (Levers.handle(player, obj)) {
						return;
					} else if (SpiderWeb.slash(player, obj)) {
						return;
					} else if (Runecrafting.handleObject(player, obj)) {
						return;
					} else if (Barrows.stairInteraction(player, obj.getId())) {
						return;
					} else if (Doors.manageDoor(obj)) {
						return;
					} else if (MageArenaGodPrayer.godPrayer(player, obj)) {
						return;
					} else {
						if (def.getName().toLowerCase().contains("altar")
								&& def.getOptions()[0].toLowerCase().contains("pray")) {
							player.getSkills().getPrayer().prayAltar();
							return;
						}
						if (obj.getDefinition().getName().equalsIgnoreCase("Anvil")) {
							int barId = 0;
							if (player.getInventory().contains(2349))
								barId = 2349;
							if (player.getInventory().contains(2351))
								barId = 2351;
							if (player.getInventory().contains(2353))
								barId = 2353;
							if (player.getInventory().contains(2355))
								barId = 2355;
							if (player.getInventory().contains(2357))
								barId = 2357;
							if (player.getInventory().contains(2359))
								barId = 2359;
							if (player.getInventory().contains(2361))
								barId = 2361;
							if (player.getInventory().contains(2363))
								barId = 2363;
							ForgingBar bar = ForgingBar.forId(barId);
							if (bar == null) {
								player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE, -1, null,
										"You don't have any bars to work with.");
								return;
							}
							Smithing.openSmithingInterface(player, bar);
							return;
						}
						switch (objectId) {
						case 14918:
							if (player.getSkills().getLevel(Skills.AGILITY) < 74) {
								player.getActionSender().sendMessage(
										"<col=ff0000>You need an Agility level of 74 to use this shortcut.");
								return;
							}
							Location obor1 = player.getLocation();
							if (obor1.getY() == 3807) {
								player.setTeleportTarget(Location.create(3201, 3810, 0));
							} else if (obor1.getY() == 3810) {
								player.setTeleportTarget(Location.create(3201, 3807, 0));
							}
							break;

						case 11005:
							if (!player.isSilverMember()) {
								player.getActionSender().sendDialogue("Donator Zone", DialogueType.MESSAGE, 1,
										FacialAnimation.DEFAULT,
										"You need to be a Silver Member to pass this barrier.");
								return;
							}
							Agility.forceWalkingQueue(player, player.getWalkAnimation(), player.getX(),
									player.getY() + (player.getY() == 4526 ? -2 : 2), 0, 1, true);
							break;

						case 1579:
							player.climbStairsDown(Location.create(3096, 9867, 0));
							break;

						case 7236:
							WallSafeCracking wallsafe = new WallSafeCracking(player, obj);
							wallsafe.execute();
							player.submitTick("skill_action_tick", wallsafe, true);
							break;

						case 28849:
							if (player.getSkills().getLevel(Skills.AGILITY) < 63) {
								player.getActionSender().sendMessage(
										"<col=ff0000>You need an Agility level of 63 to use this shortcut.");
								return;
							}
							Location obor2 = player.getLocation();
							if (obor2.getX() == 2936) {
								player.setTeleportTarget(Location.create(2934, 9810, 0));
							} else if (obor2.getY() == 9810) {
								player.setTeleportTarget(Location.create(2936, 9810, 0));
							}
							break;
						case 6279: // smoke dungeon - climb down
							player.climbStairsUp(Location.create(3206, 9379, 0));
							break;
						case 6439: // smoke dungeon - climb up
							player.climbStairsUp(Location.create(3311, 2962, 0));
							break;
						case 11797: // champions guild stairs - up
							player.setTeleportTarget(Location.create(3189, 3354, 1));
							break;
						case 11799: // stairs down
							if (obj.getX() == 3188 && obj.getY() == 3355)
								player.setTeleportTarget(Location.create(3189, 3358, 0));
							else
								player.setTeleportTarget(Location.create(1618, 3666, 0));
							break;
						case 26709: // enter stronghold slayer dungeon - main
									// area
							player.setTeleportTarget(Location.create(2444, 9825, 0));
							break;
						case 26710: // exit stronghold slayer dungeon
							player.setTeleportTarget(Location.create(2430, 3424, 0));
							break;
						case 26711: // enter stronghold slayer dungeon -
									// kalphite section
							// player.setTeleportTarget(Location.create(2430,
							// 3424, 0));
							player.sendMessage("Kalphite Slayer dungeon section is not currently added!");
							break;
						case 7257: // trapdoor - enter rogues den
							player.climbStairsDown(Location.create(3061, 4985, 1));
							break;
						case 7258: // passage way - exit rogues den
							player.setLocation(Location.create(2906, 3537, 0));
							break;
						case 29150: // home occult altar
							DialogueManager.openDialogue(player, 1000);
							break;
						case 29776:
							RaidingParties.handleRaidParty(player);
							break;
						case 21722: // brimhaven dungeon - stairs - up
							player.setTeleportTarget(Location.create(2643, 9594, 2));
							break;
						case 21724: // brimhaven dungeon - stairs - down
							player.setTeleportTarget(Location.create(2649, 9591, 0));
							break;
						case 21725: // brimhaven dungeon - stairs - up
							player.setTeleportTarget(Location.create(2637, 9510, 2));
							break;
						case 21726: // brimhaven dungeon - stairs - down
							player.setTeleportTarget(Location.create(2637, 9517, 0));
							break;
						case 15477:
						case 15478:
						case 15479:
						case 15480:
						case 15481:
						case 15482:
							player.getConstruction().enterHouse(true);
							break;
						case 677:// corp
							player.setTeleportTarget(Location.create(2974, 4384, 2));
							break;
						case 1732:
						case 1733:// wizard's guild doubledoor
							if (player.getSkills().getLevelForExperience(Skills.MAGIC) < 66 && player.getX() >= 2597) {
								player.getActionSender().sendDialogue("", DialogueType.MESSAGE, 1,
										FacialAnimation.DEFAULT, "You are not high enough Magic level to enter here.");
								player.sendMessage("You need at least a level of 66 Magic to enter here.");
								return;
							}
							Location zap = Location.create((player.getX() >= 2597 ? 2596 : 2597), player.getY());
							player.setTeleportTargetObj(zap);
							break;
						case 15645:// wizard's guild staircase up
							if (player.getPlane() == 0)
								player.setTeleportTargetObj(Location.create(2591, 3092, 1));
							else if (player.getPlane() == 1)
								player.setTeleportTargetObj(Location.create(2590, 3087, 2));
							break;
						case 15648:// wizard's guild staircase down
							if (player.getPlane() == 1)
								player.setTeleportTargetObj(Location.create(2591, 3088, 0));
							else if (player.getPlane() == 2)
								player.setTeleportTargetObj(Location.create(2590, 3083, 1));
							break;
						case 27979:
							if (!player.getInventory().contains(13445)) {
								player.getActionSender()
										.sendMessage("<col=ff0000>A dark power energizes the Essence block.");
								return;
							}
							player.getSkills().addExperience(Skills.RUNECRAFTING, 2.5);
							player.getInventory().remove(new Item(13445, 1));
							player.getInventory().add(new Item(13446, 1));
							break;
						case 29315:// Wintertodt Sprouting roots
							if (System.currentTimeMillis() - player.getLastHarvest() > 600) {
								player.setLastHarvest(System.currentTimeMillis());
								if (player.getInventory().add(new Item(20698, 1))) {
									player.playAnimation(Animation.create(2280));
									player.getSkills().addExperience(Skills.FARMING, 2);
									player.getInventory().add(new Item(20527, 1));
									player.getActionSender().sendMessage("You manage to harvest a Bruma herb...");
									if (Misc.random(6) == 0) {
										World.getWorld().replaceObject(obj, null, 10);// 10
																						// =
																						// number
																						// of
																						// cycles
										player.getActionSender().sendMessage(
												"<col=ff0000>The Sprouting roots need to replenish before harvesting again...");
									}
								}
							}
							break;
						case 28900:
							if (!player.getInventory().contains(19685)) {
								player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT,
										19685, null, "You need a Dark totem to access Skotizo.");
								return;
							}
							player.getInventory().remove(new Item(19685, 1));
							player.setTeleportTarget(Location.create(1693, 9886, 0));
							break;
						case 28686:
							player.setTeleportTarget(Location.create(2128, 5647, 0));
							break;
						case 28687:
							player.setTeleportTarget(Location.create(2027, 5611, 0));
							break;
						case 29147:
							player.getActionSender().sendConfig(439, 1);
							player.getCombatState()
									.setSpellBook(MagicCombatAction.SpellBook.ANCIENT_MAGICKS.getSpellBookId());
							player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT,
									4675, null, "An ancient wisdom fills your mind...");
							break;
						case 29148:
							player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT,
									9084, null, "Lunar spells activated!");
							player.getActionSender().sendConfig(439, 2);
							player.getCombatState()
									.setSpellBook(MagicCombatAction.SpellBook.LUNAR_MAGICS.getSpellBookId());
							break;
						case 29149:
							player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT,
									1381, null, "Your magic book has been changed to the Regular spellbook.");
							player.getActionSender().sendConfig(439, 0);
							player.getCombatState()
									.setSpellBook(MagicCombatAction.SpellBook.MODERN_MAGICS.getSpellBookId());
							break;
						case 20877:
							if (!player.getInventory().contains(995)) {
								player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT,
										995, null, "You need 785 gp to enter this dungeon.");
								return;
							}
							player.getInventory().remove(new Item(995, 785));
							player.setTeleportTarget(Location.create(2713, 9564, 0));
							player.sendMessage("You paid 785 coins to enter this dungeon.");
							break;
						case 20878:
							player.setTeleportTarget(Location.create(2744, 3152, 0));
							break;
						case 5946: // lumbridge swamp cave exit
							player.climbStairsUp(Location.create(3169, 3171, 0));
							break;
						case 5947: // lumbridge swamp cave entrance
							if (!player.getInventory().contains(954)) {
								player.sendMessage("You will need a rope in order to climb down there.");
								return;
							}
							if (!player.getInventory().containsOneItem(594, 33, 4531, 4524, 4539, 4550)
									&& !player.getEquipment().contains(5013) && !player.getEquipment().contains(9804)
									&& !player.getEquipment().contains(9805) && !player.getEquipment().contains(13137)
									&& !player.getEquipment().contains(13138) && !player.getEquipment().contains(13139)
									&& !player.getEquipment().contains(13140)
									&& !player.getEquipment().contains(20720)) {
								player.sendMessage(
										"It's too dark down there, perhaps you should bring a light source of some sorts!");
								return;
							}
							player.setTeleportTarget(Location.create(3168, 9572, 0));
							break;
						case 21738:
							player.setTeleportTarget(Location.create(2647, 9557, 0));
							break;
						case 6552:
							player.playAnimation(Animation.create(645));
							player.setAttribute("busy", true);
							World.getWorld().submit(new Tickable(2) {

								public void execute() {
									this.stop();
									int spellbook = player.getCombatState().getSpellBook();
									if (spellbook == SpellBook.MODERN_MAGICS.getSpellBookId()
											|| spellbook == SpellBook.LUNAR_MAGICS.getSpellBookId()) {
										spellbook = SpellBook.ANCIENT_MAGICKS.getSpellBookId();
									} else {
										spellbook = SpellBook.MODERN_MAGICS.getSpellBookId();
									}
									int config = spellbook == SpellBook.MODERN_MAGICS.getSpellBookId() ? 0 : 2;
									player.removeAttribute("busy");
									player.getCombatState().setSpellBook(spellbook);
									player.getActionSender().sendConfig(439, config);
								}
							});
							break;

						case 28857: // Zeah redwood ladders up
							Location upstairs = null;
							if (obj.getLocation().equals(Location.create(1566, 3482, 0)))
								upstairs = Location.create(1567, 3482, 2);
							if (obj.getLocation().equals(Location.create(1566, 3493, 0)))
								upstairs = Location.create(1567, 3493, 2);
							if (obj.getLocation().equals(Location.create(1575, 3493, 0)))
								upstairs = Location.create(1574, 3493, 2);
							if (obj.getLocation().equals(Location.create(1575, 3482, 0)))
								upstairs = Location.create(1574, 3482, 2);
							if (upstairs != null)
								player.climbStairsUp(upstairs);
							break;

						case 28858: // Zeah redwood ladders down
							Location downstairs = null;
							if (obj.getLocation().equals(Location.create(1566, 3482, 2)))
								downstairs = Location.create(1566, 3482, 0);
							if (obj.getLocation().equals(Location.create(1566, 3493, 2)))
								downstairs = Location.create(1566, 3493, 0);
							if (obj.getLocation().equals(Location.create(1575, 3493, 2)))
								downstairs = Location.create(1575, 3493, 0);
							if (obj.getLocation().equals(Location.create(1575, 3482, 2)))
								downstairs = Location.create(1575, 3482, 0);
							if (downstairs != null)
								player.climbStairsDown(downstairs);
							break;

						case 29317:
							player.getInventory().add(new Item(946, 1));
							break;

						case 29319:
							player.getInventory().add(new Item(590, 1));
							break;

						case 27057:
							DialogueManager.openDialogue(player, 550);
							break;

						case 29318:
							player.getInventory().add(new Item(1351, 1));
							break;

						case 29316:
							player.getInventory().add(new Item(2347, 1));
							break;

						case 29332:
							player.setTeleportTarget(Location.create(1630, 3968, 0));
							break;
						case 29777:
							/**
							 * int pane = player.getAttribute("tabmode"); int tabId = pane == 548 ? 65 :
							 * pane == 161 ? 56 : 56; player.getActionSender().sendSidebarInterface(tabId,
							 * 500); player.getActionSender().sendConfig(1055, 8768);
							 * player.getActionSender().sendConfig(1430, 1336071168);
							 * player.getActionSender().sendConfig(1432, 1); // party size
							 * player.getActionSender().sendMessage("<col=E172E5>A new test raid has
							 * begun!"); player.setTeleportTarget(Location.create(3299, 5188, 0));
							 * 
							 * World.getWorld().sendWorldMessage( "<col=ff0000>News: " + player.getName() +
							 * " Has just entered the raids dungeon."); break;
							 */
							break;

						case 29312:
							if (!player.getInventory().contains(20695)) {
								player.sendMessage("You'll need some bruma roots to light the brazier!");
								return;
							}
							if (!player.getInventory().contains(590)) {
								player.sendMessage("You'll need a tinderbox to light the brazier!");
								return;
							}
							player.playAnimation(Animation.create(733));
							player.setAttribute("lighting", true);
							player.setAttribute("busy", true);
							World.getWorld().submit(new Tickable(3) {

								@Override
								public void execute() {
									player.removeAttribute("lighting");
									player.removeAttribute("busy");
									player.playAnimation(Animation.create(-1));
									World.getWorld().replaceObject(obj, new GameObject(obj.getLocation(), 29314,
											obj.getType(), obj.getDirection(), false), Misc.random(60, 180));
									player.getActionSender().sendMessage("You lighten the brazier!");
									player.getSkills().addExperience(Skills.FIREMAKING, 45);
									this.stop();
								}
							});

							break;

						case 29314:
							BrazierAction action = new BrazierAction(player, Lightables.BRUMA_ROOT, obj);
							if (player.getInventory().contains(20696))
								action = new BrazierAction(player, Lightables.BRUMA_KINDLING, obj);
							action.execute();
							player.submitTick("skill_action_tick", action, true);
							break;

						case 28579:// mine wall 1
							if (!player.getInventory().contains(1755)) {
								player.getActionSender().sendDialogue("", DialogueType.MESSAGE, 1755,
										FacialAnimation.DEFAULT, "You'll need a chisel in order to do this.");
								return;
							}
							if (!player.getInventory().contains(13573)) {
								player.getActionSender().sendDialogue("", DialogueType.MESSAGE, 13573,
										FacialAnimation.DEFAULT, "You'll need something to blast the ore out.");
								return;
							}
							player.getSkills().addExperience(Skills.CRAFTING, 85);
							player.getInventory().remove(new Item(454, 1));
							player.playAnimation(Animation.create(7199));
							player.getActionSender()
									.sendMessage("You chipped away at the wall, and placed dynamite into it!");
							if (Misc.random(2) == 0)
								World.getWorld().replaceObject(obj, new GameObject(obj.getLocation(), 28583,
										obj.getType(), obj.getDirection(), false), 30);
							break;
						case 28580:// mine wall 2
							if (!player.getInventory().contains(1755)) {
								player.getActionSender().sendDialogue("", DialogueType.MESSAGE, 1755,
										FacialAnimation.DEFAULT, "You'll need a chisel in order to do this.");
								return;
							}
							if (!player.getInventory().contains(13573)) {
								player.getActionSender().sendDialogue("", DialogueType.MESSAGE, 13573,
										FacialAnimation.DEFAULT, "You'll need something to blast the ore out.");
								return;
							}
							player.getSkills().addExperience(Skills.CRAFTING, 85);
							player.getInventory().add(new Item(454, 1));
							player.playAnimation(Animation.create(7199));
							player.getActionSender()
									.sendMessage("You chipped away at the wall, and placed dynamite into it!");
							if (Misc.random(2) == 0)
								World.getWorld().replaceObject(obj, new GameObject(obj.getLocation(), 28583,
										obj.getType(), obj.getDirection(), false), 30);
							break;
						case 28582:// Blast mining crevice into dynamite
							if (!player.getInventory().contains(1755)) {
								player.getActionSender().sendDialogue("", DialogueType.MESSAGE, 1755,
										FacialAnimation.DEFAULT, "You'll need a chisel in order to do this.");
								return;
							}
							if (!player.getInventory().contains(13573)) {
								player.getActionSender().sendDialogue("", DialogueType.MESSAGE, 13573,
										FacialAnimation.DEFAULT, "You'll need something to blast the ore out.");
								return;
							}
							player.getInventory().remove(new Item(13573, 1));
							player.playAnimation(Animation.create(833));
							player.getActionSender().sendMessage("You placed dynamite in the cavity.");
							if (Misc.random(4) == 0)
								World.getWorld().replaceObject(obj, new GameObject(obj.getLocation(), 28583,
										obj.getType(), obj.getDirection(), false), 20);

						case 28583:// Blast mining lighting dynamite
							if (!player.getInventory().contains(590)) {
								player.getActionSender().sendDialogue("", DialogueType.MESSAGE, 590,
										FacialAnimation.DEFAULT, "You'll need a tinderbox to light this.");
								return;
							}
							player.getInventory().remove(new Item(454, 5));
							player.getInventory().remove(new Item(13573, 1));

							player.playAnimation(Animation.create(833));
							player.playGraphics(Graphic.create(157));
							player.getActionSender().sendMessage("You carefully light the dynamite.");
							World.getWorld().replaceObject(obj,
									new GameObject(obj.getLocation(), 28588, obj.getType(), obj.getDirection(), false),
									20);

							break;
						case 28588:// Blast mining shattered
							if (Misc.random(2) == 0) {
								World.getWorld().replaceObject(obj, new GameObject(obj.getLocation(), 28580,
										obj.getType(), obj.getDirection(), false), 5);
							}
							break;
						case 26273:
							player.getActionSender().sendInterface(206, false);
							// player.getActionSender().sendAccessMask(1054,
							// 206, 1, 0, 100);
							break;
						case 6948:
						case 29104:
						case 26254:
						case 29327:
							if (permissionService.is(player, PermissionService.PlayerPermissions.ULTIMATE_IRON_MAN)) {
								player.getActionSender().sendMessage(
										"You are an ultimate ironman and cannot use the bank deposit box.");
								player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT,
										12813, null,
										"You are an ultimate ironman and cannot use the bank deposit box.");
								return;
							} else {
								BankDepositBoxServiceImpl bankDeposit = Server.getInjector()
										.getInstance(BankDepositBoxServiceImpl.class);
								bankDeposit.openDepositBox(player);
							}
							break;

						case 29486:
						case 29487:
							player.setTeleportTarget(Location.create(3091, 9814, 0));
							break;
						case 29488:
						case 29489:
							player.setTeleportTarget(Location.create(3096, 9832, 0));
							break;
						case 29491:
							Location obor = player.getLocation();
							if (obor.getY() == 9804) {
								player.setTeleportTarget(Location.create(3092, 9807, 0));
							} else if (obor.getY() == 9807) {
								player.setTeleportTarget(Location.create(3092, 9804, 0));
							}
							break;
						case 27095:
							player.setTeleportTarget(Location.create(3327, 4751, 0).transform(Misc.random(1), 0, 0));
							break;
						case 26646:
							player.resetInteractingEntity();
							player.getCombatState().getDamageMap().reset();
							player.getCombatState().resetPrayers();
							player.getSkills().resetStats();
							player.removeAttribute("venom");
							player.venomDamage = 6;
							player.getActionQueue().clearAllActions();
							player.getDatabaseEntity().getCombatEntity().setVenomDamage(0);
							player.getDatabaseEntity().getPlayerSettings().setTeleBlockTimer(0);
							player.getDatabaseEntity().getPlayerSettings().setTeleBlocked(false);
							player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);
							player.getActionSender().removeWalkableInterface();
							player.getCombatState().setPoisonDamage(0, null);
							player.setTeleportTarget(Entity.DEFAULT_LOCATION);
							break;
						/** Lighthouse objects **/
						case 4568:
							if (obj.getLocation().equals(Location.create(2506, 3640, 0))) {
								player.setTeleportTarget(Location.create(2505, 3641, 1));
							}
							break;
						case 4569:
							if (obj.getLocation().equals(Location.create(2506, 3640, 1))) {
								player.setTeleportTarget(Location.create(2505, 3641, 2));
							}
							break;
						case 4570:
							if (obj.getLocation().equals(Location.create(2506, 3641, 2))) {
								player.setTeleportTarget(Location.create(2505, 3641, 1));
							}
							break;
						case 534:
							if (obj.getLocation().equals(Location.create(3748, 5760, 0))) {
								player.setTeleportTarget(Location.create(2356, 9782, 0));
							}
							break;
						case 154:
							if (obj.getLocation().equals(Location.create(2356, 9783, 0))) {
								player.setTeleportTarget(Location.create(3748, 5761, 0));
							}
							break;
						case 535:
							if (obj.getLocation().equals(Location.create(3722, 5798, 0))) {
								player.setTeleportTarget(Location.create(3677, 5775, 0));
							}
							break;
						case 536:
							if (obj.getLocation().equals(Location.create(3678, 5775, 0))) {
								player.setTeleportTarget(Location.create(3723, 5798, 0));
							}
							break;
						case 4031: // shantay pass
							Location tile = Location.create(player.getLocation().getX(),
									(player.getLocation().getY() <= 3115 ? 3117 : 3115), 0);
							if (tile.getY() == 3117) {
								player.setTeleportTarget(tile);
								return;
							}
							if (player.getInventory().hasItem(new Item(1854))) {
								player.getInventory().remove(new Item(1854, 1));
								player.setTeleportTarget(tile);
								return;
							}
							player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE, -1, null,
									"You need a shantay pass in order to go through here.");
							break;
						case 10562:
						case 2693:
						case 4483:
						case 10058:
						case 12309:
						case 29321:
						case 28861:
							Bank.open(player);
							break;
						case 10061:
							player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE, -1, null,
									"This feature is not enabled at this time.");
							break;

						case 11735:
							if (player.getX() >= 3188 && player.getX() <= 3193 && player.getY() == 3961
									|| player.getY() == 3962)
								Bank.open(player);
							break;
						case 20925:
							if (obj.getLocation().equals(Location.create(2611, 3394, 0))) {
								if (player.getSkills().getLevel(Skills.FISHING) < 68) {
									player.getActionSender()
											.sendMessage("You need a Fishing level of 68 to enter the Fishing Guild.");
									return;
								}
								player.setAttribute("busy", true);
								int yOff = player.getY() == 3394 ? -1 : 1;
								GameObject replace = new GameObject(obj.getLocation(), 20925, obj.getType(), 2, false);
								replace.setLocation(obj.getLocation());
								player.getActionSender().removeObject(obj);
								player.getActionSender().sendObject(replace);
								Agility.forceWalkingQueue(player, player.getWalkAnimation(), player.getX(),
										player.getY() + yOff, 0, 1, true);
								World.getWorld().submit(new Tickable(2) {

									@Override
									public void execute() {
										this.stop();
										player.getActionSender().removeObject(replace);
										player.getActionSender().sendObject(obj);
									}
								});
							}
							break;
						case 5167:
							if (obj.getLocation().equals(Location.create(3578, 3527, 0))) {
								player.setTeleportTarget(Location.create(3577, 9927, 0));
							}
							break;
						/* Dwarf mine Ladder */
						case 11867:
							if (obj.getLocation().equals(Location.create(3019, 3450, 0))) {
								final Location location = Location.create(Vector2.of(obj.getLocation()).plus(0, 6400)
										.plus(GameObjectCardinality.forFace(obj.getDirection()).getFaceVector()));
								player.getActionQueue().addAction(new ClimbLadderAction(player, location));
							}
							break;

						case 17387:
							if (obj.getLocation().equals(Location.create(3578, 9927, 0)))
								player.setTeleportTarget(Location.create(3579, 3527, 0));
							else if (obj.getLocation().equals(Location.create(2892, 9907))) {
								final Location location = Location.create(2893, 3507);
								player.getActionQueue().addAction(new ClimbLadderAction(player, location));
							} else if (obj.getLocation().equals(Location.create(3019, 9850, 0))) {
								final Location location = Location.create(3020, 3450, 0);
								player.getActionQueue().addAction(new ClimbLadderAction(player, location));
							}
							break;
						/* Burthrope Games Room Stairs */
						case 4622:
							if (obj.getLocation().equals(Location.create(2207, 4935, 0))) {
								player.setTeleportTarget(Location.create(2899, 3565));
							}
							break;
						case 4624:
							if (obj.getLocation().equals(Location.create(2899, 3566, 0))) {
								player.setTeleportTarget(Location.create(2208, 4938));
							}
							break;

						case 10068:
							if (player.getContentManager().getActiveContent(Content.ZULRAH) != null) {
								return;
							}
							player.getContentManager().start(Content.ZULRAH);
							break;
						case 17384:
							if (obj.getLocation().equals(Location.create(2892, 3507))
									|| obj.getLocation().equals(Location.create(3116, 3452))
									|| obj.getLocation().equals(Location.create(2842, 3424))) {
								final Location location = Location.create(Vector2.of(obj.getLocation()).plus(0, 6400)
										.plus(GameObjectCardinality.forFace(obj.getDirection()).getFaceVector()));
								player.getActionQueue().addAction(new ClimbLadderAction(player, location));
							}
							break;

						/* Mining guild ladder down */
						case 7452:
							if (obj.getLocation().equals(Location.create(3020, 3339, 0))
									|| obj.getLocation().equals(Location.create(3019, 3340, 0))
									|| obj.getLocation().equals(Location.create(3018, 3339, 0))
									|| obj.getLocation().equals(Location.create(3019, 3338, 0))) {
								final Location location = Location.create(Vector2.of(obj.getLocation()).plus(0, 6400)
										.plus(GameObjectCardinality.forFace(obj.getDirection()).getFaceVector()));
								player.getActionQueue().addAction(new ClimbLadderAction(player, location));
							}
							break;
						case 10229:
							player.setTeleportTarget(Location.create(1910, 4367));
							break;
						case 2641:
							player.setTeleportTarget(
									Location.create(player.getX(), player.getY(), player.getPlane() == 0 ? 1 : 0));
							break;
						case 26720:
						case 26721:
							World.getWorld().unregister(obj, true);
							RegionClipping.removeClipping(obj);
							break;
						case 26880:
							if (player.getSkills().getLevelForExperience(Skills.AGILITY) < 87) {
								player.sendMessage(
										"You need a level of at leastl 87 Agility to traverse this shortcut.");
								return;
							}
							player.setTeleportTargetObj(Location.create(2670, 9583, 2));
							break;
						case 26882:
							if (player.getSkills().getLevelForExperience(Skills.AGILITY) < 87) {
								player.sendMessage(
										"You need a level of at leastl 87 Agility to traverse this shortcut.");
								return;
							}
							player.setTeleportTargetObj(Location.create(2674, 9583, 0));
							break;
						case 20882:
						case 20884:
							int destX = player.getX() == 2682 ? 2687 : 2682;
							player.setTeleportTargetObj(Location.create(destX, 9506, 0));
							break;
						case 11794:
							if (obj.getLocation().equals(Location.create(3551, 9689, 0))) {
								player.setTeleportTarget(Location.create(3565, 3316, 0));
								player.getActionSender().removeChatboxInterface();
								World.getWorld().submit(new Tickable(1) {
									@Override
									public void execute() {
										this.stop();
										player.getActionSender().updateMinimap(ActionSender.NO_BLACKOUT);
									}
								});
							}
							break;
						case 881:
							World.getWorld().unregister(obj, true);
							World.getWorld().register(
									new GameObject(obj.getLocation(), 882, obj.getType(), obj.getDirection(), false));
							break;
						case 882:
							player.setTeleportTarget(Location.create(3237, 9858));
							break;
						case 11806:
							player.setTeleportTarget(Location.create(3236, 3458, 0));
							break;
						case 27029:
							UnsiredRewards.offerUnsired(player, obj);
							break;
						case 10090: // signpost
							CrystalChest.openRewardsInterface(player);
							break;
						case 172: // crystal chest
							CrystalChest.openChest(player, obj);
							break;
						case 26374:
							NPC npc = player.getAttribute("currentlyFightingBrother");
							if (npc != null && npc.getSkills().getLevel(Skills.HITPOINTS) > 0) {
								World.getWorld().unregister(npc);
								player.removeAttribute("currentlyFightingBrother");
							}
							player.setTeleportTarget(Location.create(3565, 3316, 0));
							World.getWorld().unregister(obj, true);
							break;
						case 25938:
						case 25939:
							Location spinningDown = Location.create(2715, 3470, 0);
							Location spinningUp = Location.create(2715, 3470, 1);
							if (obj.getLocation().equals(spinningDown) || obj.getLocation().equals(spinningUp)) {
								player.setTeleportTarget(Location.create(2715, 3471, player.getPlane() == 1 ? 0 : 1));
							}
							break;
						case 11834:
							player.getFightCave().stop();
							break;
						case 23969:
							if (obj.getLocation().equals(Location.create(3059, 9776))) {
								player.setTeleportTarget(Location.create(3061, 3376));
							}
							break;
						case 16664:
							if (obj.getLocation().equals(Location.create(3058, 3376))) {
								player.setTeleportTarget(Location.create(3058, 9776));
							}
							break;
						case 12356:
							player.getRFD().stop(false);
							break;

						case 24318:
							int level = player.getSkills().getLevelForExperience(Skills.STRENGTH)
									+ player.getSkills().getLevelForExperience(Skills.ATTACK);
							if (level < 130) {
								player.getActionSender().sendDialogue("", DialogueType.MESSAGE, 2457,
										FacialAnimation.DEFAULT,
										"You are not a high enough level to enter the guild. "
												+ "Work on your combat skills some more. You need to have a combined "
												+ "attack and strength level of at least 130.");
								return;
							}
							Location coord = Location.create((player.getLocation().getX() >= 2877 ? 2876 : 2877), 3546,
									0);
							player.setTeleportTargetObj(coord);
							break;
						case 24306:
						case 24309:
							if (player.getPlane() == 0) {
								Location zip = Location.create(player.getX(), (player.getY() >= 3546 ? 3545 : 3546), 0);
								player.setTeleportTargetObj(zip);
								return;
							}
							if (player.getPlane() == 2) {
								player.getWarriorsGuild().handleDoorClick(WarriorsGuild.GAME_DOOR_1);
								return;
							}
							break;
						case 16675:// Tree gnome bank (up)
							if (obj.getLocation().equals(Location.create(2445, 3434, 0))) {
								player.setTeleportTarget(Location.create(2445, 3433, 1));
							} else if (obj.getLocation().equals(Location.create(2444, 3414, 0))) {
								player.setTeleportTarget(Location.create(2445, 3416, 1));
							}
							break;
						case 16677:// Tree gnome bank (down)
							if (obj.getLocation().equals(Location.create(2445, 3434, 1))) {
								player.setTeleportTarget(Location.create(2445, 3433, 0));
							} else if (obj.getLocation().equals(Location.create(2445, 3415, 1))) {
								player.setTeleportTarget(Location.create(2445, 3416, 0));
							}
							break;
						case 4879:// ape atoll dungeon trapdoor
							player.setTeleportTarget(Location.create(2807, 9201, 0));
							break;
						case 4881:// ape atoll dungeon rope up
							player.setTeleportTarget(Location.create(2806, 2785, 0));
							break;
						case 9582:// staircase up
							player.setTeleportTarget(
									Location.create(player.getX(), player.getY(), player.getPlane() + 1));
							break;
						case 9584:// staircase down
							player.setTeleportTarget(
									Location.create(player.getX(), player.getY(), player.getPlane() - 1));
							break;

						case 10596:// asgarnian dungeon to wyverns
							player.setTeleportTarget(Location.create(3056, 9555, 0));
							break;
						case 10595:// sgarnian dungeon to ice
							player.setTeleportTarget(Location.create(3056, 9562, 0));
							break;
						case 1738:// asgarnia ice dungeon down
							player.setTeleportTarget(Location.create(3008, 9550, 0));
							break;

						case 10230: // down to dag kings
							player.setTeleportTarget(Location.create(2900, 4449, 0));
							break;
						case 16671: // staircase up
							if (obj.getX() == 2839 && obj.getY() == 3537) // warriors guild
								player.setTeleportTarget(Location.create(2840, 3539, 2));
							else
								player.setTeleportTarget(
										Location.create(player.getX(), player.getY(), player.getPlane() + 1));
							break;
						case 16673: // staircase down
							player.setTeleportTarget(
									Location.create(player.getX(), player.getY(), player.getPlane() - 1));
							break;
						case 24303: // warrior guild staircase down
							player.setTeleportTarget(Location.create(2841, 3538, 0));
							break;
						case 11835:
							player.setTeleportTarget(Location.create(2480, 5175, 0));
							break;
						case 11836:
							player.setTeleportTarget(Location.create(2862, 9572, 0));
							break;
						case 11441:
							player.setTeleportTarget(Location.create(2858, 9567, 0));
							break;
						case 18969:
							player.setTeleportTarget(Location.create(2856, 3167, 0));
							break;
						case 2120:
						case 2119:
							Location toLoc = player.getLocation().getPlane() == 2
									? Location.create(3412, player.getLocation().getY(), 1)
									: Location.create(3417, player.getLocation().getY(), 2);
							player.setTeleportTarget(toLoc);
							break;
						case 2114:
						case 2118:
							toLoc = player.getLocation().getPlane() == 1
									? Location.create(3438, player.getLocation().getY(), 0)
									: Location.create(3433, player.getLocation().getY(), 1);
							player.setTeleportTarget(toLoc);
							break;
						case 2100:
							player.doorOpenClose(obj, 0, 1, 0);
							break;
						case 11726:
							if (player.getDatabaseEntity().getPlayerSettings().isTeleBlocked()) {
								player.getActionSender()
										.sendMessage("You can not enter the Snake bank while teleblocked.");
								return;
							}
							if (obj.getLocation().equals(Location.create(3190, 3957, 0))) {
								if (player.getLocation().equals(Location.create(3190, 3958, 0))
										|| player.getLocation().equals(Location.create(3190, 3957, 0))) {
									int yOff = player.getY() == 3958 ? -1 : 1;
									player.doorOpenClose(obj, 0, yOff, 0);
								}
							} else if (obj.getLocation().equals(Location.create(3191, 3963, 0))) {
								if (player.getLocation().equals(Location.create(3191, 3962, 0))
										|| player.getLocation().equals(Location.create(3191, 3963, 0))) {
									int yOff = player.getY() == 3963 ? -1 : 1;
									player.doorOpenClose(obj, 0, yOff, 0);
								}
							}
							break;
						case 2102:
							int yOff = player.getLocation().getY() == 3556 ? -1 : 1;
							player.doorOpenClose(obj, 0, yOff, 0);
							break;
						case 7111:
						case 7108:
							if (obj.getLocation().equals(Location.create(2577, 9882))
									|| obj.getLocation().equals(Location.create(2576, 9882))) {
								player.setTeleportTarget(Location.create(player.getLocation().getX(),
										player.getLocation().getY() == 9883 ? player.getLocation().getY() - 1
												: player.getLocation().getY() + 1));
							} else if (obj.getLocation().equals(Location.create(2577, 9884))
									|| obj.getLocation().equals(Location.create(2576, 9884))) {
								player.setTeleportTarget(Location.create(player.getLocation().getX(),
										player.getLocation().getY() == 9885 ? player.getLocation().getY() - 1
												: player.getLocation().getY() + 1));
							} else if (obj.getLocation().equals(Location.create(2564, 9881))
									|| obj.getLocation().equals(Location.create(2565, 9881))) {
								player.setTeleportTarget(Location.create(player.getLocation().getX(),
										player.getLocation().getY() == 9882 ? player.getLocation().getY() - 1
												: player.getLocation().getY() + 1));
							}
							break;

						case 14880:
							player.climbStairsDown(Location.create(3210, 9616, 0));
							break;
						case 16680:
							if (obj.getLocation().equals(Location.create(3088, 3571))) {
								final Location location = Location.create(Vector2.of(obj.getLocation()).plus(0, 6400)
										.plus(GameObjectCardinality.forFace(obj.getDirection()).getFaceVector()));
								player.getActionQueue().addAction(new ClimbLadderAction(player, location));
							}
							break;

						case 17385:
							/* Mining guild ladder up */
							if (obj.getLocation().equals(Location.create(3020, 9739))
									|| obj.getLocation().equals(Location.create(3019, 9740))
									|| obj.getLocation().equals(Location.create(3018, 9739))
									|| obj.getLocation().equals(Location.create(3019, 9738))
									|| obj.getLocation().equals(Location.create(3116, 9852))
									|| obj.getLocation().equals(Location.create(3088, 9971))
									|| obj.getLocation().equals(Location.create(2842, 9824))) {
								final Location location = Location.create(Vector2.of(obj.getLocation()).minus(0, 6400)
										.plus(GameObjectCardinality.forFace(obj.getDirection()).getFaceVector()));
								player.getActionQueue().addAction(new ClimbLadderAction(player, location));
								break;
							}
							if (obj.getLocation().equals(Location.create(3097, 9867))) {
								final Location location = Location.create(Vector2.of(obj.getLocation()).minus(0, 6399)
										.plus(GameObjectCardinality.forFace(obj.getDirection()).getFaceVector()));
								player.getActionQueue().addAction(new ClimbLadderAction(player, location));
							}

							if (obj.getLocation().equals(Location.create(3209, 9616, 0))) {
								player.playAnimation(Animation.create(828));
								World.getWorld().submit(new Tickable(2) {
									@Override
									public void execute() {
										this.stop();
										player.setTeleportTarget(Location.create(3210, 3216, 0));
									}

								});
							}
							break;

						case 23271:
							Agility.tackleObstacle(player, Obstacle.WILDERNESS_DITCH, obj);
							break;
						case 16529:
							player.playAnimation(Animation.create(2589));
							player.setTeleportTarget(Location.create(3142, 3513));
							player.playAnimation(Animation.create(2591));
							break;
						case 16530:
							player.playAnimation(Animation.create(2589));
							player.setTeleportTarget(Location.create(3137, 3516));
							player.playAnimation(Animation.create(2591));
							break;
						case 2623:
							int xOff = 0;
							yOff = 0;
							if (player.getLocation().equals(Location.create(2923, 9803, 0))) {
								xOff = 1;
							} else if (player.getLocation().equals(Location.create(2924, 9803, 0))) {
								xOff = -1;
							}
							player.doorOpenClose(obj, xOff, yOff, 1);
							break;
						case 6:
						case 7:
						case 8:
						case 9:
							if (player.getAttribute("cannon") != null) {
								Cannon cannon = (Cannon) player.getAttribute("cannon");
								if (cannon.getGameObject().getLocation().equals(loc)) {
									if (objectId == 6)
										cannon.fire();
									else
										cannon.destroy();
								} else
									player.getActionSender().sendMessage("This is not your cannon.");
							} else
								player.getActionSender().sendMessage("This is not your cannon.");
							break;
						case 450:
						case 451:
						case 452:
						case 453:
							player.getActionSender().sendMessage("There is no ore currently available in this rock.");
							return;
						case 2878:
							if (player.getSettings().completedMageArena()) {
								player.sendMessage("You feel a magical energy running through you...");
								player.setAttribute("busy", true);
								World.getWorld().submit(new Tickable(2) {

									@Override
									public void execute() {
										this.stop();
										player.removeAttribute("busy");
										player.setTeleportTarget(Location.create(2509, 4689));
									}

								});
							} else
								player.sendMessage("You'll have to fight Kolodion first in order to enter here.");
							break;
						case 2879:
							player.getActionSender().sendMessage("You feel a magical energy running through you...");
							player.setAttribute("busy", true);
							World.getWorld().submit(new Tickable(2) {

								@Override
								public void execute() {
									this.stop();
									player.removeAttribute("busy");
									player.setTeleportTarget(Location.create(2542, 4718));
								}

							});
							break;
						case 7179:
							World.getWorld().unregister(obj, true);
							World.getWorld().register(
									new GameObject(obj.getLocation(), 7182, obj.getType(), obj.getDirection(), false));
							break;
						case 7182:
							if (obj.getLocation().equals(Location.create(3097, 3468))) {
								final Location location = Location
										.create(Vector2.of(obj.getLocation()).minus(1, 0).plus(0, 6400).plus(
												GameObjectCardinality.forFace(obj.getDirection()).getFaceVector()));
								player.getActionQueue().addAction(new ClimbLadderAction(player, location));
								break;
							}
							break;
						case 7407:
						case 7408:
							World.getWorld().unregister(obj, true);
							RegionClipping.removeClipping(obj);
							break;

						case 11833:
							player.getFightCave().start();
							break;
						case 26384:
							if (player.getSkills().getLevel(Skills.STRENGTH) < 70) {
								player.getActionSender()
										.sendMessage("You need a Strength level of 70 to bang this door down.");
							} else if (player.getInventory().getCount(2347) < 1) {
								player.getActionSender().sendMessage("You need a hammer to bang this door down.");
							} else {
								player.getActionQueue().addAction(new Action(player, 3) {
									@Override
									public void execute() {
										if (player.getLocation().getX() == 2851) {
											player.setTeleportTarget(Location.create(player.getLocation().getX() - 1,
													player.getLocation().getY(), player.getLocation().getPlane()));
										} else if (player.getLocation().getX() == 2850) {
											player.setTeleportTarget(Location.create(player.getLocation().getX() + 1,
													player.getLocation().getY(), player.getLocation().getPlane()));
										}
										this.stop();
									}

									@Override
									public AnimationPolicy getAnimationPolicy() {
										return AnimationPolicy.RESET_NONE;
									}

									@Override
									public CancelPolicy getCancelPolicy() {
										return CancelPolicy.ALWAYS;
									}

									@Override
									public StackPolicy getStackPolicy() {
										return StackPolicy.NEVER;
									}
								});
								player.playAnimation(Animation.create(7002));
							}
							break;
						default:
							if (obj.getDefinition() != null) {
								if (obj.getDefinition().getName().toLowerCase().contains("bank")) {
									NPC closestBanker = null;
									int closestDist = 10;
									for (NPC banker : World.getWorld().getRegionManager().getLocalNpcs(player)) {
										if (banker.getDefinition().getName().toLowerCase().contains("banker")) {
											if (obj.getLocation().distanceToPoint(banker.getLocation()) < closestDist) {
												closestDist = obj.getLocation().distanceToPoint(banker.getLocation());
												closestBanker = banker;
											}
										}
									}
									if (closestBanker != null) {
										player.setInteractingEntity(InteractionMode.TALK, closestBanker);
										closestBanker.setInteractingEntity(InteractionMode.TALK, player);
										DialogueManager.openDialogue(player, 0);
									}
									return;
								}
								if (obj.getDefinition().getName().toLowerCase().contains("ladder")) {
									switch (obj.getId()) {

									case 18987: // wild to kbd
										player.climbStairsDown(Location.create(3069, 10255, 0));
										break;
									case 18988:// kbd to wild
										player.climbStairsUp(Location.create(3016, 3849, 0));
										break;

									default:
										if (obj.getDefinition().getOptions()[0].contains("Climb-up"))
											player.climbStairsUp(Location.create(player.getLocation().getX(),
													player.getLocation().getY(), player.getLocation().getPlane() + 1));
										else if (obj.getDefinition().getOptions()[0].contains("Climb-down")
												&& player.getLocation().getPlane() > 0)
											player.climbStairsDown(Location.create(player.getLocation().getX(),
													player.getLocation().getY(), player.getLocation().getPlane() - 1));

										break;
									}
									return;
								}
							}
							break;
						}
					}
				}
			};
		}
		final Action submit = action;
		double dist = player.getLocation().distance(loc);
		if (dist <= 1 || (obj.getId() == 26646 && dist <= 3)) {
			player.getActionQueue().addAction(submit);
		} else {
			World.getWorld().submitAreaEvent(player,
					new CoordinateEvent(player, x, y, def.getSizeX(), def.getSizeY(), obj.getDirection()) {

						@Override
						public void execute() {
							player.getActionQueue().addAction(submit);
						}

					});
		}
	}

	/**
	 * Handles the option 2 packet.
	 *
	 * @param player
	 *            The player.
	 * @param packet
	 *            The packet.
	 */
	@SuppressWarnings("unused")
	private void handleOption2(final Player player, Packet packet) {
		int z = packet.getByteC();
		int x = packet.getLEShort();
		int y = packet.getShortA();
		int objectId = packet.getShort();
		Location loc = Location.create(x, y, player.getLocation().getPlane());
		Region r = player.getRegion();
		final GameObject obj = r.getGameObject(loc, objectId);
		if (obj == null || obj.getId() != objectId) {
			System.out.println("Object null.");
			return;
		}
		CacheObjectDefinition def = CacheObjectDefinition.forID(objectId);
		String name = def.getName();
		player.getCombatState().setQueuedSpell(null);
		player.resetInteractingEntity();
		player.getActionQueue().clearAllActions();
		player.getActionSender().removeAllInterfaces();// .removeInterface2();
		Action action;

		if (Constants.DEBUG)
			player.sendMessage("Object Option2: " + obj.getId() + ", Location: " + obj.getLocation().toString());

		ThievingStalls stall = ThievingAction.THIEVING_STALLS.get(objectId);

		pathfindingService.travelToObject(player, obj);

		if (stall != null)
			action = new ThievingAction(player, obj);
		else {
			action = new Action(player, 0) {
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

				@Override
				public void execute() {
					this.stop();
					hookService.post(new GameObjectActionEvent(player, GameObjectActionEvent.ActionType.OPTION_2, obj));
					if (ClickEventManager.getEventManager().handleObjectOption(player, obj.getId(), obj,
							obj.getLocation(), ClickOption.SECOND)) {
						return;
					}
					if (Doors.manageDoor(obj))
						return;
					if (name.equalsIgnoreCase("spinning wheel")) {
						player.getActionSender().sendInterface(459, false);
						return;
					}
					switch (objectId) {
					case 4569:
						if (obj.getLocation().equals(Location.create(2506, 3640, 1)))
							player.setTeleportTarget(Location.create(2505, 3641, 2));
						break;
					case 6552:
						player.playAnimation(Animation.create(645));
						player.setAttribute("busy", true);
						World.getWorld().submit(new Tickable(2) {

							public void execute() {
								this.stop();
								int spellbook = player.getCombatState().getSpellBook();
								if (spellbook == SpellBook.MODERN_MAGICS.getSpellBookId()
										|| spellbook == SpellBook.LUNAR_MAGICS.getSpellBookId()) {
									spellbook = SpellBook.ANCIENT_MAGICKS.getSpellBookId();
								} else {
									spellbook = SpellBook.MODERN_MAGICS.getSpellBookId();
								}
								int config = spellbook == SpellBook.MODERN_MAGICS.getSpellBookId() ? 0 : 2;
								player.removeAttribute("busy");
								player.getCombatState().setSpellBook(spellbook);
								player.getActionSender().sendConfig(439, config);
							}
						});
					case 12309:
						Shop.open(player, 13, 0);
						break;

					case 14911:
						player.playAnimation(Animation.create(645));
						player.setAttribute("busy", true);
						World.getWorld().submit(new Tickable(2) {

							@Override
							public void execute() {
								this.stop();
								int spellbook = player.getCombatState().getSpellBook();
								if (spellbook == SpellBook.ANCIENT_MAGICKS.getSpellBookId()
										|| spellbook == SpellBook.MODERN_MAGICS.getSpellBookId()) {
									spellbook = SpellBook.LUNAR_MAGICS.getSpellBookId();
								} else {
									spellbook = SpellBook.MODERN_MAGICS.getSpellBookId();
								}
								int config = spellbook == SpellBook.MODERN_MAGICS.getSpellBookId() ? 0 : 2;
								player.removeAttribute("busy");
								player.getCombatState().setSpellBook(spellbook);
								player.getActionSender().sendConfig(439, config);
							}

						});
						break;
					case 16672:
						player.setTeleportTarget(Location.create(player.getX(), player.getY(), player.getPlane() + 1));
						break;
					case 14896:
						if (System.currentTimeMillis() - player.getLastHarvest() > 600) {
							player.setLastHarvest(System.currentTimeMillis());
							if (player.getInventory().add(new Item(1779, 1))) {
								player.playAnimation(Animation.create(827));
								player.getActionSender().sendMessage("You manage to pick some Flax...");
								if (Misc.random(4) == 0)
									World.getWorld().replaceObject(obj, null, 30);
							}
						}
						break;
					case 1161:
						if (System.currentTimeMillis() - player.getLastHarvest() > 600) {
							player.setLastHarvest(System.currentTimeMillis());
							if (player.getInventory().add(new Item(1965, 1))) {
								player.playAnimation(Animation.create(827));
								player.getActionSender().sendMessage("You pick a cabbage.");
								if (Misc.random(4) == 0)
									World.getWorld().replaceObject(obj, null, 30);
							}
						}
						break;
					case 3366:
						if (System.currentTimeMillis() - player.getLastHarvest() > 600) {
							player.setLastHarvest(System.currentTimeMillis());
							if (player.getInventory().add(new Item(1957, 1))) {
								player.playAnimation(Animation.create(827));
								player.getActionSender().sendMessage("You pick an onion.");
								if (Misc.random(4) == 0)
									World.getWorld().replaceObject(obj, null, 30);
							}
						}
						break;
					case 11748:
					case 11744:
					case 24101:
					case 25808:
					case 16700:
					case 18491:
					case 27249:
					case 27718:
					case 27719:
					case 27720:
					case 27721:
					case 12121:
					case 27259:
					case 14367:
					case 6943:
					case 27264:
					case 28861:
					case 7478:
					case 7409:
					case 6944:
					case 27291:
					case 10060:
					case 28546:
					case 28549:
					case 28547:
					case 28548:
					case 6084:
					case 10517:
						Bank.open(player);
						break;
					case 24009:
					case 16469:
					case 26300:
						Smelting.furnaceInteraction(player);
						break;
					case 6:
						if (player.getAttribute("cannon") != null) {
							Cannon cannon = (Cannon) player.getAttribute("cannon");
							if (cannon.getGameObject().getLocation().equals(loc))
								cannon.destroy();
							else
								player.getActionSender().sendMessage("This is not your cannon.");
						} else
							player.getActionSender().sendMessage("This is not your cannon.");
						break;
					}

					if (obj.getDefinition().getName().toLowerCase().contains("ladder")) {

						switch (obj.getId()) {

						default:
							if (obj.getDefinition().getOptions()[1].contains("Climb-up"))
								player.climbStairsUp(Location.create(player.getLocation().getX(),
										player.getLocation().getY(), player.getLocation().getPlane() + 1));
							break;
						}
						return;
					}
					this.stop();
				}
			};
		}
		if (action != null) {
			final Action submit = action;
			double dist = player.getLocation().distance(loc);
			if (dist <= 1) {
				player.getActionQueue().addAction(submit);
			} else {
				World.getWorld().submitAreaEvent(player,
						new CoordinateEvent(player, x, y, def.getSizeX(), def.getSizeY(), obj.getDirection()) {

							@Override
							public void execute() {
								player.getActionQueue().addAction(submit);
							}

						});
			}
		}
	}

	@SuppressWarnings("unused")
	private void handleOption3(final Player player, Packet packet) {
		int objectId = packet.getLEShortA();
		int x = packet.getShortA();
		int z = packet.get();
		int y = packet.getLEShortA();
		Location loc = Location.create(x, y, player.getPlane());
		Region r = player.getRegion();
		final GameObject obj = r.getGameObject(loc, objectId);
		if (obj == null || obj.getId() != objectId) {
			System.out.println("Object null.");
			return;
		}
		CacheObjectDefinition def = CacheObjectDefinition.forID(objectId);
		player.getCombatState().setQueuedSpell(null);
		player.resetInteractingEntity();
		player.getActionQueue().clearAllActions();
		player.getActionSender().removeAllInterfaces();// .removeInterface2();
		Action action;

		if (Constants.DEBUG)
			player.sendMessage("Object Option3: " + obj.getId() + ", Location: " + obj.getLocation().toString());

		player.faceObject(obj);
		pathfindingService.travelToObject(player, obj);
		action = new Action(player, 0) {
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

			@Override
			public void execute() {
				this.stop();
				switch (objectId) {
				case 4569:
					if (obj.getLocation().equals(Location.create(2506, 3640, 1)))
						player.setTeleportTarget(Location.create(2505, 3640, 0));
					break;
				case 16672:// warrior guild level 2
					player.setTeleportTarget(Location.create(player.getX(), player.getY(), player.getPlane() - 1));
					break;
				case 10177:
					player.setTeleportTarget(Location.create(2900, 4449, 0));
					break;
				case 10060:
				case 10061:
					player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE, -1, null,
							"This feature is not enabled at this time.");
					break;
				case 12309:
					Shop.open(player, 17, 0);
					break;
				}

				if (obj.getDefinition().getName().toLowerCase().contains("ladder")) {

					switch (obj.getId()) {

					default:
						if (obj.getDefinition().getOptions()[2].contains("Climb-down")
								&& player.getLocation().getPlane() > 0)
							player.climbStairsDown(Location.create(player.getLocation().getX(),
									player.getLocation().getY(), player.getLocation().getPlane() - 1));
						break;
					}
					return;
				}
				this.stop();
			}
		};
		if (action != null) {
			final Action submit = action;
			World.getWorld().submitAreaEvent(player,
					new CoordinateEvent(player, x, y, def.getSizeX(), def.getSizeY(), obj.getDirection()) {

						@Override
						public void execute() {
							player.getActionQueue().addAction(submit);
						}

					});
		}
	}

	@SuppressWarnings("unused")
	private void handleOption4(final Player player, Packet packet) {
		int objectId = packet.getLEShortA();
		int x = packet.getShortA();
		int z = packet.getByteC();
		int y = packet.getLEShortA(); // TODO

	}

	@SuppressWarnings("unused")
	private void handleOption5(final Player player, Packet packet) {
		int objectId = packet.getLEShortA();
		int x = packet.getShortA();
		int z = packet.getByteC();
		int y = packet.getLEShortA(); // TODO
	}

	/**
	 * Handles the item on object packet.
	 *
	 * @param player
	 *            The player.
	 * @param packet
	 *            The packet.
	 */
	@SuppressWarnings("unused")
	private void handleItemOnObject(final Player player, Packet packet) {
		int y = packet.getShortA();
		int itemId = packet.getShort();
		int c = packet.getByteS();
		int x = packet.getShortA();
		int objectId = packet.getLEShortA();
		int slot = packet.getShort();
		int g = packet.getInt();
		int z = player.getLocation().getPlane();

		if (player.getAttribute("temporaryHeight") != null)
			z = player.getAttribute("temporaryHeight");

		final Location loc = Location.create(x, y, z);
		final Item item = player.getInventory().get(slot);
		if (item == null)
			return;

		final GameObject obj = player.getRegion().getGameObject(loc, objectId);

		if (obj == null)
			return;

		if (Constants.DEBUG)
			player.sendMessage("Item on Object: " + obj.getId() + ", Location: " + obj.getLocation().toString()
					+ ", itemID: " + item.getId());

		CacheObjectDefinition def = CacheObjectDefinition.forID(objectId);
		int width = 1;
		int height = 1;
		if (def != null) {
			if (obj.getDirection() != 1 && obj.getDirection() != 3) {
				width = def.getSizeX();
				height = def.getSizeY();
			} else {
				width = def.getSizeY();
				height = def.getSizeX();
			}
		}
		player.faceObject(obj);
		pathfindingService.travelToObject(player, obj);
		int distance = obj.getLocation().distanceToEntity(obj, player);
		player.getCombatState().setQueuedSpell(null);
		player.resetInteractingEntity();
		player.getActionQueue().clearAllActions();
		player.getActionSender().removeAllInterfaces();// .removeInterface2();
		Action action = null;
		action = new Action(player, 0) {
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

			@Override
			public void execute() {
				this.stop();
				hookService.post(
						new GameObjectActionEvent(player, GameObjectActionEvent.ActionType.ITEM_ON_OBJECT, obj, item));
				if (player.getWarriorsGuild().handleItemOnObject(item, objectId, loc))
					return;
				else if (DragonfireShieldAction.handleItemOnObject(player, item, obj))
					return;
				else if (AltarAction.handleItemOnObject(player, obj, item))
					return;
				if (item.getDefinition2() != null && item.getDefinition2().name != null
						&& item.getDefinition2().name.endsWith("set")) {
					if (player.getInventory().freeSlots() < 6) {
						player.getActionSender().sendItemDialogue(item.getId(),
								"Not enough space in your inventory in order to exchange this kit.");
						return;
					}
					switch (item.getDefinition2().name) {
					case "Dwarf cannon set":
						player.getInventory().remove(item);
						player.getInventory().addItems(6, 8, 10, 12);
						player.getActionSender().sendMessage("You exchange your kit for the full set.");
						break;
					case "Guthan's armour set":
						player.getInventory().remove(item);
						player.getInventory().addItems(4724, 4726, 4728, 4730);
						player.getActionSender().sendMessage("You exchange your kit for the full set.");
						break;
					case "Verac's armour set":
						player.getInventory().remove(item);
						player.getInventory().addItems(4753, 4755, 4757, 4759);
						player.getActionSender().sendMessage("You exchange your kit for the full set.");
						break;
					case "Dharok's armour set":
						player.getInventory().remove(item);
						player.getInventory().addItems(4716, 4718, 4720, 4722);
						player.getActionSender().sendMessage("You exchange your kit for the full set.");
						break;
					case "Torag's armour set":
						player.getInventory().remove(item);
						player.getInventory().addItems(4745, 4747, 4749, 4751);
						player.getActionSender().sendMessage("You exchange your kit for the full set.");
						break;
					case "Ahrim's armour set":
						player.getInventory().remove(item);
						player.getInventory().addItems(4708, 4710, 4712, 4714);
						player.getActionSender().sendMessage("You exchange your kit for the full set.");
						break;
					case "Karil's armour set":
						player.getInventory().remove(item);
						player.getInventory().addItems(4732, 4734, 4736, 4738);
						player.getActionSender().sendMessage("You exchange your kit for the full set.");
						break;
					case "Halloween mask set":
						player.getInventory().remove(item);
						player.getInventory().addItems(1053, 1055, 1057);
						player.getActionSender().sendMessage("You exchange your kit for the full set.");
						break;
					case "Partyhat set":
						player.getInventory().remove(item);
						player.getInventory().addItems(1038, 1040, 1042, 1044, 1046, 1048);
						player.getActionSender().sendMessage("You exchange your kit for the full set.");
						break;
					}
					return;
				}
				String objectName = obj.getDefinition().getName().toLowerCase();
				if (objectName.contains("booth") || objectName.contains("bank") || obj.getId() == 7478) {
					PermissionService permissionService = Server.getInjector().getInstance(PermissionService.class);
					if (item.getDefinition() == null)
						return;
					if (item.getId() == 11941) {
						lootingBagService.redeemBag(player);
						return;
					}
					if (item.getDefinition().isNoted())
						itemService.exchangeToUnNote(player, item);
					else
						itemService.exchangeToNote(player, item);
					return;
				}
				switch (obj.getId()) {
				case 24004:
				case 874:
				case 27707:
				case 27708:
				case 20776:
					WaterSourceAction.Fillables fill = WaterSourceAction.Fillables.forId(item.getId());
					if (fill != null)
						player.getActionQueue().addAction(new WaterSourceAction(player, fill));
					else
						player.sendMessage("Nothing interesting happens.");
					break;
				case 6:
					if (player.getAttribute("cannon") != null) {
						Cannon cannon = (Cannon) player.getAttribute("cannon");
						if (loc.equals(cannon.getGameObject().getLocation())) {
							if (item.getId() == 2) {
								int cannonBalls = cannon.getCannonBalls();
								if (cannonBalls >= 30) {
									player.getActionSender().sendMessage("Your cannon is already full.");
									return;
								}
								int newCannonBalls = item.getCount();
								if (newCannonBalls > 30)
									newCannonBalls = 30;
								if (newCannonBalls + cannonBalls > 30)
									newCannonBalls = 30 - cannonBalls;
								if (newCannonBalls < 1)
									return;
								player.getInventory().remove(new Item(2, newCannonBalls));
								cannon.addCannonBalls(newCannonBalls);
								player.getActionSender().sendMessage("You load " + newCannonBalls + " cannonball"
										+ (newCannonBalls > 1 ? "s" : "") + " into your cannon.");
							}
						}
					}
					break;
				case 7:
					if (player.getAttribute("cannon") != null) {
						Cannon cannon = (Cannon) player.getAttribute("cannon");
						if (loc.equals(cannon.getGameObject().getLocation())) {
							if (item.getId() == 8) {
								cannon.addPart(new Item(8, 1));
								player.playAnimation(Animation.create(827));
								player.face(obj.getCentreLocation());
							}
						}
					}
					break;
				case 8:
					if (player.getAttribute("cannon") != null) {
						Cannon cannon = (Cannon) player.getAttribute("cannon");
						if (loc.equals(cannon.getGameObject().getLocation())) {
							if (item.getId() == 10) {
								cannon.addPart(new Item(10, 1));
								player.playAnimation(Animation.create(827));
								player.face(obj.getCentreLocation());
							}
						}
					}
					break;
				case 9:
					if (player.getAttribute("cannon") != null) {
						Cannon cannon = (Cannon) player.getAttribute("cannon");
						if (loc.equals(cannon.getGameObject().getLocation())) {
							if (item.getId() == 12) {
								cannon.addPart(new Item(12, 1));
								player.playAnimation(Animation.create(827));
								player.face(obj.getCentreLocation());
							}
						}
					}
					break;
				}

				if (Cooking.getCookingItem(item.getId()) != null && Cooking.getCookingMethod(obj) != null) {
					CookingItem cookItem = Cooking.getCookingItem(item.getId());
					CookingMethod method = Cooking.getCookingMethod(obj);
					if (Cooking.canCook(method, cookItem)) {
						player.setInterfaceAttribute("cookItem", cookItem);
						player.setInterfaceAttribute("cookMethod", method);
						player.setInterfaceAttribute("cookObject", obj);
						player.getActionSender().sendChatboxInterface(307);
						player.getActionSender().sendItemOnInterface(307, 2, item.getId(), 160);
						player.getActionSender().sendString(307, 6,
								"<br><br><br><br>" + item.getDefinition2().getName());
					} else
						player.getActionSender().sendMessage("You cannot cook that on a fire!");
				}
				if (obj.getDefinition().getName().equalsIgnoreCase("Anvil")) {
					ForgingBar bar = ForgingBar.forId(item.getId());
					if (bar == null) {
						player.sendMessage("Nothing interesting happens.");
						return;
					}
					Smithing.openSmithingInterface(player, bar);
				}
				if (obj.getDefinition().getName().equalsIgnoreCase("Furnace")) {
					if (item.getDefinition2().getName().toLowerCase().contains("mould") || item.getId() == 2357) {
						player.getActionSender().sendCS2Script(917, new Object[] { -1, -1 }, "ii").sendInterface(446,
								false);
					}
				}
			}
		};
		if (action != null) {
			final Action submit = action;
			World.getWorld().submitAreaEvent(player,
					new CoordinateEvent(player, x, y, def.getSizeX(), def.getSizeY(), obj.getDirection()) {

						@Override
						public void execute() {
							player.getActionQueue().addAction(submit);
						}

					});
		}
	}

	public Location bestWalkablePath(GameObject obj) {
		CacheObjectDefinition def = obj.getDefinition();
		int width = 1;
		int height = 1;
		if (def != null) {
			if (obj.getDirection() != 1 && obj.getDirection() != 3) {
				width = def.getSizeX();
				height = def.getSizeY();
			} else {
				width = def.getSizeY();
				height = def.getSizeX();
			}
		}
		int toX = obj.getSpawnLocation().getX();
		int toY = obj.getSpawnLocation().getY();
		for (int dx = -width; dx <= width; dx++) {
			for (int dy = -height; dy <= height; dy++) {
				if (RegionClipping.isPassable(toX + dx, toY + dy, obj.getLocation().getPlane())
						&& obj.getSpawnLocation().isWithinDistance(width, height, Location.create(toX + dx, toY + dy),
								1, 1, 1)) {
					toX = toX + dx;
					toY = toY + dy;
					break;
				}
			}
		}
		return Location.create(toX, toY, obj.getSpawnLocation().getPlane());
	}

	public boolean reachedWall(int initialX, int initialY, int finalX, int finalY, int z, int orientation, int type) {
		if (initialX == finalX && initialY == finalY) {
			return true;
		}
		int clipping = RegionClipping.getClippingMask(initialX, initialY, z);
		if (type == 0) {
			if (orientation == Orientation.NORTH) {
				if (initialX == finalX - 1 && initialY == finalY) {
					return true;
				} else if (initialX == finalX && initialY == finalY + 1 && (clipping & 0x1280120) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY - 1 && (clipping & 0x1280102) == 0) {
					return true;
				}
			} else if (orientation == Orientation.EAST) {
				if (initialX == finalX && initialY == finalY + 1) {
					return true;
				} else if (initialX == finalX - 1 && initialY == finalY && (clipping & 0x1280108) == 0) {
					return true;
				} else if (initialX == finalX + 1 && initialY == finalY && (clipping & 0x1280180) == 0) {
					return true;
				}
			} else if (orientation == Orientation.SOUTH) {
				if (initialX == finalX + 1 && initialY == finalY) {
					return true;
				} else if (initialX == finalX && initialY == finalY + 1 && (clipping & 0x1280120) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY - 1 && (clipping & 0x1280102) == 0) {
					return true;
				}
			} else if (orientation == Orientation.WEST) {
				if (initialX == finalX && initialY == finalY - 1) {
					return true;
				} else if (initialX == finalX - 1 && initialY == finalY && (clipping & 0x1280108) == 0) {
					return true;
				} else if (initialX == finalX + 1 && initialY == finalY && (clipping & 0x1280180) == 0) {
					return true;
				}
			}
		}

		if (type == 2) {
			if (orientation == Orientation.NORTH) {
				if (initialX == finalX - 1 && initialY == finalY) {
					return true;
				} else if (initialX == finalX && initialY == finalY + 1) {
					return true;
				} else if (initialX == finalX + 1 && initialY == finalY && (clipping & 0x1280180) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY - 1 && (clipping & 0x1280102) == 0) {
					return true;
				}
			} else if (orientation == Orientation.EAST) {
				// UNLOADED_TILE | BLOCKED_TILE | UNKNOWN | OBJECT_TILE |
				// WALL_EAST
				if (initialX == finalX - 1 && initialY == finalY && (clipping & 0x1280108) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY + 1) {
					return true;
				} else if (initialX == finalX + 1 && initialY == finalY) {
					return true;
				} else if (initialX == finalX && initialY == finalY - 1 && (clipping & 0x1280102) == 0) {
					return true;
				}
			} else if (orientation == Orientation.SOUTH) {
				if (initialX == finalX - 1 && initialY == finalY && (clipping & 0x1280108) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY + 1 && (clipping & 0x1280120) == 0) {
					return true;
				} else if (initialX == finalX + 1 && initialY == finalY) {
					return true;
				} else if (initialX == finalX && initialY == finalY - 1) {
					return true;
				}
			} else if (orientation == Orientation.WEST) {
				if (initialX == finalX - 1 && initialY == finalY) {
					return true;
				} else if (initialX == finalX && initialY == finalY + 1 && (clipping & 0x1280120) == 0) {
					return true;
				} else if (initialX == finalX + 1 && initialY == finalY && (clipping & 0x1280180) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY - 1) {
					return true;
				}
			}
		}

		if (type == 9) {
			if (initialX == finalX && initialY == finalY + 1 && (clipping & ObjectPathFinder.WALL_SOUTH) == 0) {
				return true;
			} else if (initialX == finalX && initialY == finalY - 1 && (clipping & ObjectPathFinder.WALL_NORTH) == 0) {
				return true;
			} else if (initialX == finalX - 1 && initialY == finalY && (clipping & ObjectPathFinder.WALL_EAST) == 0) {
				return true;
			} else if (initialX == finalX + 1 && initialY == finalY && (clipping & ObjectPathFinder.WALL_WEST) == 0) {
				return true;
			}
		}

		return false;
	}

	public boolean reachedDecoration(int initialY, int initialX, int finalX, int finalY, int z, int type,
			int orientation) {
		if (initialX == finalX && initialY == finalY) {
			return true;
		}
		int clipping = RegionClipping.getClippingMask(initialX, initialY, z);
		if (type == 6 || type == 7) {
			if (type == 7) {
				orientation = orientation + 2 & 3;
			}

			if (orientation == Orientation.NORTH) {
				if (initialX == finalX + 1 && initialY == finalY && (clipping & ObjectPathFinder.WALL_WEST) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY - 1
						&& (clipping & ObjectPathFinder.WALL_NORTH) == 0) {
					return true;
				}
			} else if (orientation == Orientation.EAST) {
				if (initialX == finalX - 1 && initialY == finalY && (clipping & ObjectPathFinder.WALL_EAST) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY - 1
						&& (clipping & ObjectPathFinder.WALL_NORTH) == 0) {
					return true;
				}
			} else if (orientation == Orientation.SOUTH) {
				if (initialX == finalX - 1 && initialY == finalY && (clipping & ObjectPathFinder.WALL_EAST) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY + 1
						&& (clipping & ObjectPathFinder.WALL_SOUTH) == 0) {
					return true;
				}
			} else if (orientation == Orientation.WEST) {
				if (initialX == finalX + 1 && initialY == finalY && (clipping & ObjectPathFinder.WALL_WEST) == 0) {
					return true;
				} else if (initialX == finalX && initialY == finalY + 1
						&& (clipping & ObjectPathFinder.WALL_SOUTH) == 0) {
					return true;
				}
			}
		}

		if (type == 8) {
			if (initialX == finalX && initialY == finalY + 1 && (clipping & ObjectPathFinder.WALL_SOUTH) == 0) {
				return true;
			} else if (initialX == finalX && initialY == finalY - 1 && (clipping & ObjectPathFinder.WALL_NORTH) == 0) {
				return true;
			} else if (initialX == finalX - 1 && initialY == finalY && (clipping & ObjectPathFinder.WALL_EAST) == 0) {
				return true;
			} else if (initialX == finalX + 1 && initialY == finalY && (clipping & ObjectPathFinder.WALL_WEST) == 0) {
				return true;
			}
		}

		return false;
	}

	public boolean containsGWD(CacheObjectDefinition def) {
		if (def == null || def.getName() == null) {
			return false;
		}
		String name = def.getName().toLowerCase();
		return name.contains("armadyl") || name.contains("bandos") || name.contains("sara") || name.contains("zamorak");
	}

	public boolean usesDefaultPath(CacheObjectDefinition def) {
		if (def == null || def.getName() == null) {
			return true;
		}
		String name = def.getName().toLowerCase();
		return name.contains("wilderness") || name.contains("flax") || name.contains("lever");
	}

	public boolean usesPrimitivePath(CacheObjectDefinition def) {
		if (def == null || def.getName() == null) {
			return true;
		}
		String name = def.getName().toLowerCase();
		return name.contains("tunnel") || name.contains("sacrificial boat") || name.contains("web")
				|| name.contains("obstacle pipe") || name.contains("staircase") || name.contains("door")
				|| name.contains("gate") || name.contains("fence");
	}

	private final HookService hookService;
	private final PathfindingService pathfindingService;
	private final PermissionService permissionService;
	private final ItemService itemService;
	private final LootingBagService lootingBagService;
}
