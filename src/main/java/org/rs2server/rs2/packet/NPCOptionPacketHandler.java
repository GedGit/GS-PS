package org.rs2server.rs2.packet;

import org.rs2server.Server;
import org.rs2server.rs2.Constants;
import org.rs2server.rs2.action.Action;
import org.rs2server.rs2.content.*;
import org.rs2server.rs2.content.api.GameNpcActionEvent;
import org.rs2server.rs2.content.areas.CoordinateEvent;
import org.rs2server.rs2.domain.model.claim.ClaimType;
import org.rs2server.rs2.domain.model.player.PlayerSettingsEntity;
import org.rs2server.rs2.domain.service.api.*;
import org.rs2server.rs2.domain.service.api.PermissionService.PlayerPermissions;
import org.rs2server.rs2.domain.service.api.content.*;
import org.rs2server.rs2.domain.service.api.content.bounty.BountyHunterService;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.Animation.FacialAnimation;
import org.rs2server.rs2.model.Mob.InteractionMode;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction.*;
import org.rs2server.rs2.model.container.*;
import org.rs2server.rs2.model.map.path.SizedPathFinder;
import org.rs2server.rs2.model.map.path.astar.NpcReachedPrecondition;
import org.rs2server.rs2.model.npc.*;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.quests.impl.*;
import org.rs2server.rs2.model.skills.ThievingAction;
import org.rs2server.rs2.model.skills.ThievingAction.PickpocketableNPC;
import org.rs2server.rs2.model.skills.fish.Fishing;
import org.rs2server.rs2.model.skills.hunter.PuroPuro;
import org.rs2server.rs2.net.ActionSender.DialogueType;
import org.rs2server.rs2.net.Packet;
import org.rs2server.rs2.tickable.Tickable;

/**
 * Remove item options.
 *
 * @author Graham Edgecombe
 */
public class NPCOptionPacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		if (player.getAttribute("cutScene") != null)
			return;
		if (player.getInterfaceAttribute("fightPitOrbs") != null)
			return;
		if (player.getAttribute("teleporting") != null)
			return;
 
		boolean starter = player.getAttribute("starter");
		if (starter) {
			DialogueManager.openDialogue(player, 19000);
			return;
		}
		player.resetAfkTolerance();
		player.getActionSender().removeChatboxInterface();
		if (player.isLighting())
			return;
		if (player.getInterfaceState().isInterfaceOpen(Equipment.SCREEN)
				|| player.getInterfaceState().isInterfaceOpen(Bank.BANK_INVENTORY_INTERFACE)
				|| player.getInterfaceState().isInterfaceOpen(Trade.TRADE_INVENTORY_INTERFACE)
				|| player.getInterfaceState().isInterfaceOpen(Shop.SHOP_INVENTORY_INTERFACE)
				|| player.getInterfaceState().isInterfaceOpen(PriceChecker.PRICE_INVENTORY_INTERFACE))
			player.getActionSender().removeInventoryInterface();
		player.getActionQueue().clearAllActions();
		player.getActionManager().stopAction();
		player.getInterfaceState().setOpenShop(-1);
		switch (packet.getOpcode())

		{
		case OPTION_1:
			handleOption1(player, packet);
			break;
		case OPTION_2:
			handleOption2(player, packet);
			break;
		case OPTION_3:
			handleOption3(player, packet);
			break;
		case OPTION_TRADE:
			handleOptionTrade(player, packet);
			break;
		case OPTION_ATTACK:
			handleOptionAttack(player, packet);
			break;
		case OPTION_SPELL:
			handleOptionSpell(player, packet);
			break;
		case OPTION_ITEM_ON_NPC:
			handleOptionItemOnNpc(player, packet);
			break;
		case OPTION_EXAMINE:
			handleOptionExamine(player, packet);
			break;
		}
	}

	private void handleOptionItemOnNpc(Player player, Packet packet) {
		packet.getInt();
		packet.getLEShortA();
		packet.getByte();
		int id = packet.getLEShort();
		int slot = packet.getLEShortA();
		Item item = player.getInventory().get(slot);
		if (id < 0 || id >= Constants.MAX_NPCS || item == null) {
			return;
		}
		if (player.getCombatState().isDead()) {
			return;
		}
		player.getActionQueue().clearRemovableActions();

		final NPC npc = (NPC) World.getWorld().getNPCs().get(id);// where is
																	// combat
																	// following
																	// xd thats
		// need some sort of system to decide which tile to go to, to interact
		// with the npc. ye find the object walking code object only works
		// because you can't clip through it.
		int followX = npc.getLocation().getX();
		int followY = npc.getLocation().getY();
		if (player.getLocation().getY() < npc.getLocation().getY()) {
			followY--;
		} else if (player.getLocation().getY() > npc.getLocation().getY()) {
			followY++;
		} else if (player.getLocation().getX() < npc.getLocation().getX()) {
			followX--;
		} else if (player.getLocation().getX() > npc.getLocation().getX()) {
			followX++;
		}
		World.getWorld().doPath(new SizedPathFinder(true), player, followX, followY);

		if (npc != null) {
			Action action = null;

			action = new Action(player, 0) {// could just use the following
				@Override
				public AnimationPolicy getAnimationPolicy() {
					return AnimationPolicy.RESET_ALL;
				}

				@Override
				public CancelPolicy getCancelPolicy() {
					return CancelPolicy.ALWAYS;
				}

				@Override
				public StackPolicy getStackPolicy() {
					return StackPolicy.NEVER;
				}

				// bit till we reach the npc
				// then endGame following. ye
				@Override
				public void execute() {
					if (player.getCombatState().isDead()) {
						this.stop();
						return;
					}
					hookService
							.post(new GameNpcActionEvent(player, GameNpcActionEvent.ActionType.ITEM_ON_NPC, npc, item));
					npc.getWalkingQueue().reset();
					npc.face(player.getLocation());
					player.face(npc.getLocation());
					String npcName = npc.getDefinition().getName().toLowerCase();
					if (npcName.contains("banker") || npc.getId() == 3194) {
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

					switch (npc.getId()) {
					case 13:
						ResourceArenaService resourceArenaService = Server.getInjector()
								.getInstance(ResourceArenaService.class);
						resourceArenaService.handleItemOnNPC(player, npc, item);
						break;
					case 403:
						if (item.getId() == 11864 || item.getId() == 19647) {
							DialogueManager.openDialogue(player, (item.getId() == 19647 ? 11866 : 11864));
							this.stop();
							return;
						}
						player.sendMessage("Nothing interesting happens.");
						break;
					case 1755:
						player.setInterfaceAttribute("ring", item);
						DialogueManager.openDialogue(player, 1755);
						break;

					}
					this.stop();
				}
			};

			if (action != null) {
				player.addCoordinateAction(player.getWidth(), player.getHeight(), npc.getLocation(), 1, 1, 1, action);
			}
		}
	}

	/**
	 * Handles npc option 1.
	 *
	 * @param player
	 *            The player.
	 * @param packet
	 *            The packet.
	 */
	private void handleOption1(final Player player, Packet packet) {
		packet.getByteC();
		int id = packet.getShort() & 0xFFFF;
		if (id < 0 || id >= Constants.MAX_NPCS)
			return;
		if (player.getCombatState().isDead())
			return;
		player.getActionQueue().clearRemovableActions();

		final NPC npc = (NPC) World.getWorld().getNPCs().get(id);

		if (Constants.DEBUG)
			player.sendMessage("NPC Option1: " + npc.getId() + ", Location: " + npc.getLocation().toString());

		Fishing fishing = Fishing.isAction(player, npc, 1);

		player.setInteractingEntity(InteractionMode.TALK, npc);

		Action action;

		action = new Action(player, 0) {
			@Override
			public void execute() {

				hookService.post(new GameNpcActionEvent(player, GameNpcActionEvent.ActionType.OPTION_1, npc));

				this.stop();

				npc.face(player.getLocation());

				// Watson - clue scroll guy
				if (npc.getId() == 6585) {
					if (npc.getCombatState().isDead())
						return;
					if (npc.getInstancedPlayer() != null && npc.getInstancedPlayer() != player) {
						player.getActionSender().sendMessage("This NPC was not spawned for you.");
						return;
					}
					player.getCombatState().setQueuedSpell(null);
					player.getCombatState().startAttacking(npc, false);
					return;
				}

				String npcName = npc.getDefinition().getName().toLowerCase();
				if (npc.getDefinition().getOptions()[0].startsWith("Talk")) {
					if (npcName.contains("banker") || npcName.contains("gundai")) {
						player.setAttribute("talkingNpc", npc.getId());
						DialogueManager.openDialogue(player, 3227);
					}
				}
				if (fishing != null) {
					fishing.execute();
					Player player = (Player) getMob();
					player.submitTick("skill_action_tick", fishing, true);
				}
				switch (npc.getId()) {

				case 3248:
					npc.playAnimation(Animation.create(722));
					npc.playGraphics(Graphic.create(343, 0, 0));
					npc.forceChat("Senventior Disthine Molenko!");
					World.getWorld().submit(new Tickable(2) {

						@Override
						public void execute() {
							this.stop();
							player.teleport(Location.create(2899, 4818, 0), 0, 0, true);
						}
					});
					break;

				case 2989: // ak-haranu
					player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, npc.getId(),
							FacialAnimation.BOWS_HEAD_WHILE_SAD, "I do not speak to strangers.");
					break;

				case 7240: // perrys axe shop wc guild
					Shop.open(player, 66, 0);
					break;

				case 1602: // chamber guardian mage bank pool
					Shop.open(player, 62, 0);
					break;

				case 524: // peksas helm store barb vill
					Shop.open(player, 60, 0);
					break;

				case 4761: // rokuh's choco store nardah
					Shop.open(player, 58, 0);
					break;

				case 4754: // seddu's adventurer store nardah
					Shop.open(player, 57, 0);
					break;

				case 523: // scavvo's rune store champ guild
					Shop.open(player, 56, 0);
					break;

				case 522: // valaines champ store champ guild
					Shop.open(player, 55, 0);
					break;

				case 1026: // wydins food port sarim
					Shop.open(player, 54, 0);
					break;

				case 1027: // gerrants fish port sarim
					Shop.open(player, 53, 0);
					break;

				case 1025: // grums jewellery port sarim
					Shop.open(player, 52, 0);
					break;

				case 1052: // bettys magic port sarim
					Shop.open(player, 51, 0);
					break;

				case 1028: // brians battleaxes port sarim
					Shop.open(player, 50, 0);
					break;

				case 3247: // wizards guild magic store
					Shop.open(player, 44, 0);
					break;

				case 1601: // mage banks magic rune store
					Shop.open(player, 46, 0);
					break;

				case 1049: // faladors mace store - flynn
					Shop.open(player, 47, 0);
					break;

				case 1050: // faladors chainmail shop - wayne
					Shop.open(player, 48, 0);
					break;

				case 5449:
					if (player.getInventory().getCount(995) < 50000 && !player.isBronzeMember()) {
						player.getActionSender().sendDialogue("Bob Barter", DialogueType.NPC, 5449, FacialAnimation.SAD,
								"It seems you don't have enough coins..");
						player.sendMessage("You'll need 50'000 coins in order to decant your potions.");
						return;
					}
					if (!player.isBronzeMember())
						player.getInventory().remove(new Item(995, 50000));
					player.getActionSender().removeChatboxInterface();
					PotionDecanterService potionDecanterService = Server.getInjector()
							.getInstance(PotionDecanterService.class);
					potionDecanterService.decantPotions(player);
					player.getActionSender().sendDialogue("Bob Barter", DialogueType.NPC, 5449, FacialAnimation.HAPPY,
							"There you go!");
					break;

				case 4626: // cook
					if (player.getSettings().getRFDState() == 4) {
						player.getActionSender().sendDialogue("Cook", DialogueType.NPC, 4626, FacialAnimation.HAPPY,
								"Thanks for helping me defeat Culinaromancer!<br>I've granted you full access to my chest.");
						this.stop();
						return;
					}
					DialogueManager.openDialogue(player, 110000);
					break;
					
				case 311:
					DialogueManager.openDialogue(player, 311);
					break;

				case 1635:
				case 1636:
				case 1637:
				case 1638:
				case 1639:
				case 1640:
				case 1641:
				case 1642:
				case 1643:
				case 1644:
					PuroPuro.catchImpling(player, npc);
					break;

				case 7374: // ignisia wintertodt rewards shop
					Shop.open(player, 41, 0);
					break;

				case 2108: // wise old man skillcape hoods
					Shop.open(player, 30, 0);
					break;

				case 1768: // void knight general store
					Shop.open(player, 40, 0);
					break;

				case 1767: // void knight fletching store
					Shop.open(player, 38, 0);
					break;

				case 1765: // void knight magic store
					Shop.open(player, 39, 0);
					break;

				case 2470: // lilly warr guild pot shop
					Shop.open(player, 37, 0);
					break;

				case 1152:
					Shop.open(player, 15, 0);
					break;

				case 996: // nardok - bone weapon merchant
					Shop.open(player, 16, 0);
					break;

				case 6904:
				case 535:// Horvik
					Shop.open(player, 3, 0);
					break;

				case 508:
					Shop.open(player, 0, 0);
					break;

				case 7492:
					Shop.open(player, 5, 0);
					break;

				case 7608:
					Shop.open(player, 7, 0);
					break;
				case 4474:
					Shop.open(player, 11, 0);
					break;
				case 7502:
					Shop.open(player, 8, 0);
					break;

				case 5419:
					DialogueManager.openDialogue(player, 7000);
					break;

				case 276:
					DialogueManager.openDialogue(player, 276);
					break;

				case 534:// Thessalia
					player.getActionSender().sendInterface(269, false);
					break;

				case 4256:// Edmond / Untradables
					Shop.open(player, 11, 0);
					break;

				case 2180:
					DialogueManager.openDialogue(player, 2180);
					break;
				case 5919:
					DialogueManager.openDialogue(player, 5919);
					break;
				case 1755:
					pestControlService.openShop(player);
					break;
				case 6481:
					DialogueManager.openDialogue(player, 6481);
					break;
				case 2914:
					DialogueManager.openDialogue(player, 2914);
					break;
				case 2040:
					DialogueManager.openDialogue(player, 2040);
					break;
				case 822:
					DialogueManager.openDialogue(player, 822);
					break;
				case 2457:
					DialogueManager.openDialogue(player, 6502);
					break;
				case 1603:
					DialogueManager.openDialogue(player, 1603);
					break;
				case 2460:
				case 3216:
				case 2473:
				case 6059:
				case 2578:
				case 2658:
				case 1044:
				case 118:
				case 5045:
				case 3193:
				case 5810:
				case 2913:
				case 3249:
				case 3343:
				case 4733:
				case 3226:
				case 3363:
				case 5832:
				case 637:
					DialogueManager.openDialogue(player, 2000);
					break;
				case 684:
				case 1902:
					DesertTreasure quest = (DesertTreasure) player.getQuests().get(DesertTreasure.class);
					if (quest == null) {
						quest = new DesertTreasure(player, DTStates.NOT_STARTED);
						player.getQuests().put(DesertTreasure.class, quest);
					}
					quest.updateProgress();
					player.setAttribute("questnpc", true);
					break;
				case 3855:
				case 3839:
					LunarDiplomacy lunar = (LunarDiplomacy) player.getQuests().get(LunarDiplomacy.class);
					if (lunar == null) {
						lunar = new LunarDiplomacy(player, LunarStates.NOT_STARTED);
						player.getQuests().put(LunarDiplomacy.class, lunar);
					}
					lunar.updateProgress();
					player.setAttribute("questnpc", true);
					break;
				case 2461:
					DialogueManager.openDialogue(player, 2461);
					break;
				case 306:
					DialogueManager.openDialogue(player, 306);
					break;
				case 3231:
					player.getActionSender().sendTanningInterface();
					break;
				case 4159:
					TeleportManager.handleTeleport(player);
					break;

				/* Ranged combat tutor */
				case 3217:
					if (claimService.hasClaimedByIpAddress(player, ClaimType.STARTER_KIT_RANGED))
						DialogueManager.openDialogue(player, 1350);
					else {
						DialogueManager.openDialogue(player, 1349);
						playerService.giveItem(player, new Item(1167, 1), true);
						playerService.giveItem(player, new Item(1129, 1), true);
						playerService.giveItem(player, new Item(1095, 1), true);
						playerService.giveItem(player, new Item(1063, 1), true);
						playerService.giveItem(player, new Item(841, 1), true);
						playerService.giveItem(player, new Item(882, 250), true);
						playerService.giveItem(player, new Item(1478, 1), true);
						claimService.claim(player, ClaimType.STARTER_KIT_RANGED);
					}
					break;

				/* Magic combat tutor */
				case 3218:
					if (claimService.hasClaimedByIpAddress(player, ClaimType.STARTER_KIT_MAGIC)) {
						DialogueManager.openDialogue(player, 1352);
					} else {
						DialogueManager.openDialogue(player, 1351);
						playerService.giveItem(player, new Item(558, 50), true);
						playerService.giveItem(player, new Item(556, 250), true);
						playerService.giveItem(player, new Item(555, 125), true);
						playerService.giveItem(player, new Item(554, 125), true);
						playerService.giveItem(player, new Item(557, 125), true);
						playerService.giveItem(player, new Item(1727, 1), true);
						playerService.giveItem(player, new Item(577, 1), true);
						playerService.giveItem(player, new Item(579, 1), true);
						playerService.giveItem(player, new Item(1011, 1), true);
						claimService.claim(player, ClaimType.STARTER_KIT_MAGIC);
					}
					break;

				case 401:
				case 403:
				case 490:
				case 402:
				case 6797:
				case 404:
				case 405:
				case 6798:
					player.setAttribute("talkingNpc", npc.getId());
					DialogueManager.openDialogue(player, 500);
					break;
				case 3666:// super mem
					DialogueManager.openDialogue(player, 3666);
					break;
				case 5979:
					DialogueManager.openDialogue(player, 64);
					break;
				case 514:
					DialogueManager.openDialogue(player, 36);
					break;
				case 516:
					DialogueManager.openDialogue(player, 40);
					break;
				case 518:
					DialogueManager.openDialogue(player, 44);
					break;
				case 519:
					DialogueManager.openDialogue(player, 51);
					break;
				case 1306:// Makeover mage
					player.getActionSender().sendInterface(269, false);
					// DialogueManager.openDialogue(player, 27);
					break;
				case 315:
					DialogueManager.openDialogue(player, 55);
					break;
				}
			}

			@Override
			public AnimationPolicy getAnimationPolicy() {
				return AnimationPolicy.RESET_ALL;
			}

			@Override
			public CancelPolicy getCancelPolicy() {
				return CancelPolicy.ALWAYS;
			}

			@Override
			public StackPolicy getStackPolicy() {
				return StackPolicy.NEVER;
			}
		};
		// if (fishing != null && pathfindingService.travelToNpc(player, npc)) {
		// interactFishing(player, fishing, npc);
		// //player.addCoordinateAction(player.getWidth(), player.getHeight(),
		// npc.getLocation(), 1, 1, 1, action, fishing);
		// } else {
		interact(player, action, npc);
		// }
	}

	/**
	 * Handles npc option 2.
	 *
	 * @param player
	 *            The player.
	 * @param packet
	 *            The packet.
	 */
	private void handleOption2(final Player player, Packet packet) {
		int id = packet.getShortA() & 0xFFFF;
		packet.getByteS();
		if (id < 0 || id >= Constants.MAX_NPCS)
			return;
		if (player.getCombatState().isDead())
			return;
		player.getActionQueue().clearRemovableActions();

		if (player.getAttribute("isStealing") != null)
			return;
		final NPC npc = (NPC) World.getWorld().getNPCs().get(id);

		if (Constants.DEBUG)
			player.sendMessage("NPC Option2: " + npc.getId() + ", Location: " + npc.getLocation().toString());

		player.setInteractingEntity(InteractionMode.TALK, npc);

		Action action;

		PickpocketableNPC npcP = ThievingAction.NPCS.get(npc.getDefinition().getId());
		if (npcP != null)
			action = new ThievingAction(player, npc);
		else {
			action = new Action(player, 0) {
				@Override
				public void execute() {
					if (player.getCombatState().isDead()) {
						this.stop();
						return;
					}

					hookService.post(new GameNpcActionEvent(player, GameNpcActionEvent.ActionType.OPTION_2, npc));

					npc.face(player.getLocation());

					Pet.Pets pets = Pet.Pets.fromNpc(npc.getId());
					PlayerSettingsEntity settings = player.getDatabaseEntity().getPlayerSettings();
					if (pets != null) {
						if (player.getPet() != null && npc.getInstancedPlayer() == player) {
							this.stop();
							player.playAnimation(Animation.create(827));
							World.getWorld().unregister(player.getPet());
							Pet newPet = null;
							int[] snakes = { 2127, 2128, 2129 };
							int[] guardians = { 7337, 7338, 7339, 7340, 7341, 7342, 7343, 7344, 7345, 7346, 7347, 7348,
									7349, 7350 };

							for (int i : snakes) {
								if (npc.getId() == i)
									newPet = new Pet(player, (i == 2129 ? 2127 : npc.getId() + 1));
							}
							for (int i : guardians) {
								if (npc.getId() == i)
									newPet = new Pet(player, (i == 7350 ? 7337 : npc.getId() + 1));
							}

							settings.setPetId(newPet.getId());
							player.setPet(newPet);
							World.getWorld().register(newPet);
							player.sendMessage("You've metamorphosed your " + npc.getDefinition().getName() + " pet.");
							return;
						}
					}

					switch (npc.getId()) {
					case 4642: // shantay pass
						if (player.getInventory().hasItem(new Item(995, 25))) {
							player.getInventory().remove(new Item(995, 25));
							Inventory.addDroppable(player, new Item(1854, 1));
							player.getActionSender().sendDialogue("Shantay", DialogueType.NPC, 4642,
									FacialAnimation.HAPPY, "Pleasure doing business with you!");
							this.stop();
							return;
						}
						player.getActionSender().sendDialogue("Shantay", DialogueType.NPC, 4642, FacialAnimation.SAD,
								"Come back when you have 25 gold pieces!");
						break;

					case 0: // tool leprechaun
						DialogueManager.openDialogue(player, 5);
						break;

					case 514:// Shopkeeper
					case 515:
					case 508:
					case 509:
						Shop.open(player, 23, 0);// Supplies
						break;

					case 276:
						Shop.open(player, 65, 0);// vote point shop
						break;

					case 2108: // wise old man trimmed skillcapes
						Shop.open(player, 32, 0);
						break;

					case 6904:
					case 535:// Horvik
						Shop.open(player, 4, 0);
						break;

					case 7492:
						Shop.open(player, 6, 0);
						break;
					case 502:
						Shop.open(player, 14, 0);
						break;
					case 403:
					case 490:
					case 405:
					case 402:
					case 404:
					case 401:
					case 6798:
					case 6797:
						Shop.open(player, 12, 0);
						break;
					case 311:
					case 317:
						Item[] regular = { new Item(12810), new Item(12811), new Item(12812) };
						Item[] hardcore = { new Item(20792), new Item(20794), new Item(20796) };
						Item[] ultimate = { new Item(12813), new Item(12814), new Item(12815) };
						if (player.getPermissionService().is(player, PlayerPermissions.IRON_MAN)) {
							for (Item armour : regular) {
								if (!player.hasItem(armour))
									Inventory.addDroppable(player, armour);
							}
							player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, 311,
									FacialAnimation.HAPPY, "That should be everything!");
						} else if (player.getPermissionService().is(player, PlayerPermissions.HARDCORE_IRON_MAN)) {
							for (Item armour : hardcore) {
								if (!player.hasItem(armour))
									Inventory.addDroppable(player, armour);
							}
							player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, 311,
									FacialAnimation.HAPPY, "That should be everything!");
						} else if (player.getPermissionService().is(player, PlayerPermissions.ULTIMATE_IRON_MAN)) {
							for (Item armour : ultimate) {
								if (!player.hasItem(armour))
									Inventory.addDroppable(player, armour);
							}
							player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, 311,
									FacialAnimation.HAPPY, "That should be everything!");
						} else
							player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC, 311,
									FacialAnimation.DEFAULT,
									"Only ironmen players may claim their free special armour.");
						break;
					case 7608:
						Shop.open(player, 10, 0);
						break;

					case 7502:
						Shop.open(player, 9, 0);
						break;
					case 2182:
						Bank.open(player);
						break;
					case 3248:
						DialogueManager.openDialogue(player, 71);
					case 5419:
						DialogueManager.openDialogue(player, 7000);
						break;
					case 1306:// Makeover mage
						player.getActionSender().sendInterface(269, false);
						break;
					case 637:
						npc.playAnimation(Animation.create(722));
						npc.playGraphics(Graphic.create(343, 0, 0));
						npc.forceChat("Senventior Disthine Molenko!");
						World.getWorld().submit(new Tickable(2) {

							@Override
							public void execute() {
								this.stop();
								player.teleport(Location.create(2899, 4818, 0), 0, 0, true);
							}

						});
						break;
					case 505: // bob repair items option
						player.getActionSender().sendDialogue("Bob", DialogueType.NPC, 505, FacialAnimation.DEFAULT,
								"Items do not currently degrade, come back and talk to me when Salve has that functionality.");
						break;
					}
					this.stop();
				}

				@Override
				public AnimationPolicy getAnimationPolicy() {
					return AnimationPolicy.RESET_ALL;
				}

				@Override
				public CancelPolicy getCancelPolicy() {
					return CancelPolicy.ALWAYS;
				}

				@Override
				public StackPolicy getStackPolicy() {
					return StackPolicy.NEVER;
				}
			};
		}

		interact(player, action, npc);
	}

	public void handleOption3(Player player, Packet packet) {
		int id = packet.getShort() & 0xFFFF;
		packet.get();

		if (id < 0 || id >= Constants.MAX_NPCS)
			return;
		if (player.getCombatState().isDead())
			return;
		player.getActionQueue().clearRemovableActions();

		if (player.getAttribute("isStealing") != null)
			return;
		final NPC npc = (NPC) World.getWorld().getNPCs().get(id);

		if (Constants.DEBUG)
			player.sendMessage("NPC Option3: " + npc.getId() + ", Location: " + npc.getLocation().toString());

		if (npc != null) {
			player.setInteractingEntity(InteractionMode.TALK, npc);

			Action action = new Action(player, 0) {
				@Override
				public void execute() {
					this.stop();
					hookService.post(new GameNpcActionEvent(player, GameNpcActionEvent.ActionType.OPTION_3, npc));
					npc.face(player.getLocation());
					player.face(npc.getLocation());
					switch (npc.getId()) {
					case 7608:
						Shop.open(player, 9, 0);
						break;
					case 276:
						Shop.open(player, 2, 0);
						break;
					case 2108:
						DialogueManager.openDialogue(player, 6481);
						break;
					
					}
					if (npc.getId() == 315) {// emblem trader skulling
						player.getCombatState().setSkullTicks(1000);
						player.getActionSender().sendMessage("The Emblem Trader marks you with a skull.");
					} else if (npc.getId() == 490 || (npc.getId() >= 401 && npc.getId() <= 405) || npc.getId() == 6798)
						slayerService.openRewardsScreen(player);
				}

				@Override
				public AnimationPolicy getAnimationPolicy() {
					return AnimationPolicy.RESET_ALL;
				}

				@Override
				public CancelPolicy getCancelPolicy() {
					return CancelPolicy.ALWAYS;
				}

				@Override
				public StackPolicy getStackPolicy() {
					return StackPolicy.NEVER;
				}
			};
			if (player.getLocation().distanceToEntity(player, npc) > 1) {
				player.addCoordinateAction(player.getWidth(), player.getHeight(), npc.getLocation(), 1, 1, 1, action);
				int followX = npc.getLocation().getX();
				int followY = npc.getLocation().getY();
				if (player.getLocation().getY() < npc.getLocation().getY()) {
					followY--;
				} else if (player.getLocation().getY() > npc.getLocation().getY()) {
					followY++;
				} else if (player.getLocation().getX() < npc.getLocation().getX()) {
					followX--;
				} else if (player.getLocation().getX() > npc.getLocation().getX()) {
					followX++;
				}
				World.getWorld().doPath(new SizedPathFinder(true), player, followX, followY);

			} else {
				player.getActionManager().appendAction(action);
			}
		}
	}

	private void handleOptionTrade(final Player player, Packet packet) {
		int id = packet.getLEShort() & 0xFFFF;
		packet.getByteC();
		if (id < 0 || id >= Constants.MAX_NPCS)
			return;
		if (player.getCombatState().isDead())
			return;
		player.getActionQueue().clearRemovableActions();

		if (player.getAttribute("isStealing") != null)
			return;
		final NPC npc = (NPC) World.getWorld().getNPCs().get(id);

		// pathfindingService.travelToNpc(player, npc);
		Fishing fishing = Fishing.isAction(player, npc, 2);

		if (Constants.DEBUG)
			player.sendMessage("NPC OptionTrade: " + npc.getId() + ", Location: " + npc.getLocation().toString());

		player.setInteractingEntity(InteractionMode.TALK, npc);
		Action action;
		PickpocketableNPC npcP = ThievingAction.NPCS.get(npc.getDefinition().getId());
		if (npcP != null)
			action = new ThievingAction(player, npc);
		else {
			action = new Action(player, 0) {
				@Override
				public void execute() {

					this.stop();
					hookService.post(new GameNpcActionEvent(player, GameNpcActionEvent.ActionType.OPTION_TRADE, npc));

					if (fishing != null) {
						fishing.execute();
						Player player = (Player) getMob();
						player.submitTick("skill_action_tick", fishing, true);
					}

					npc.face(player.getLocation()); // XXX

					Pet.Pets pets = Pet.Pets.fromNpc(npc.getId());
					if (pets != null && player.getInventory().size() < player.getInventory().capacity()) {
						PlayerSettingsEntity settings = player.getDatabaseEntity().getPlayerSettings();
						if (player.getPet() != null && npc.getInstancedPlayer() == player) {
							player.playAnimation(Animation.create(827));
							if (player.getInventory().add(new Item(pets.getItem()))) {
								World.getWorld().unregister(player.getPet());
								settings.setPetSpawned(false);
								player.setPet(null);
							}
							return;
						}
					}

					switch (npc.getId()) {

					case 311: // ironman advisor
						Shop.open(player, 29, 0);
						break;

					case 502: // vanessa's farming store
						Shop.open(player, 64, 0);
						break;

					case 2989: // ak-haranu
						player.getActionSender().sendDialogue(npc.getDefinition().getName(), DialogueType.NPC,
								npc.getId(), FacialAnimation.ANGER_3, "Get lost, stranger.");
						break;

					case 2581: // mage of zamorak's rune store
						Shop.open(player, 63, 0);
						break;

					case 1176: // zenesha's platebody store ardy
						Shop.open(player, 61, 0);
						break;

					case 822: // oziachs arm store edge
						Shop.open(player, 59, 0);
						break;

					case 687: // bartender
						Shop.open(player, 49, 0);
						break;

					case 637: // aubury varrock
						Shop.open(player, 45, 0);
						break;

					case 3249: // wizards guild robe store owner
						Shop.open(player, 43, 0);
						break;

					case 2185: // tzhaar rune shop
						Shop.open(player, 42, 0);
						break;

					case 2183: // tzhaar wep shop
						Shop.open(player, 34, 0);
						break;

					case 2184: // tzhaar gem shop
						Shop.open(player, 35, 0);
						break;

					case 2108: // wise old man skillcapes
						Shop.open(player, 31, 0);
						break;

					case 534:// Thessalia
						Shop.open(player, 33, 0);
						break;

					case 2148:
						geService.openGrandExchange(player);
						break;

					case 2180:
						player.setInterfaceAttribute("gamble_firecape", true);
						player.getActionSender().sendEnterAmountInterface();
						break;
					case 4159:
						if (player.lastLocation == null) {
							player.getActionSender().sendDialogue("Teleporter", DialogueType.NPC, 4159,
									FacialAnimation.DEFAULT, "You don't seem to have a previous destionation.");
							stop();
							return;
						}
						npc.playAnimation(Animation.create(1818));
						npc.playGraphics(Graphic.create(343, 0, 0));
						player.playGraphics(Graphic.create(342, 0));
						player.teleport(player.lastLocation, 0, 0, true);
						break;

					case 276:
						DialogueManager.openDialogue(player, 278);
						break;

					case 403:
					case 490:
					case 405:
					case 402:
					case 404:
					case 401:
					case 6798:
					case 6797:
						player.setAttribute("talkingNpc", npc.getId());
						DialogueManager.openDialogue(player, 512);
						break;

					case 996: // nardok - bone weapon merchant
						Shop.open(player, 16, 0);
						break;
					case 531: // al'kharid crafting store
						Shop.open(player, 18, 0);
						break;
					case 528: // al'kharid platelegs store
						Shop.open(player, 19, 0);
						break;
					case 530: // al'kharid plateskirt store
						Shop.open(player, 20, 0);
						break;
					case 527: // al'kharid scimitar store
						Shop.open(player, 21, 0);
						break;
					case 526: // al'kharid gem trader
						Shop.open(player, 22, 0);
						break;
					case 4642: // al'kharid shantay pass
						Shop.open(player, 28, 0);
						break;

					case 5919: // rogues den grace
						Shop.open(player, 25, 0);
						break;

					case 1173: // taverley 2h sword
						Shop.open(player, 24, 0);
						break;

					case 1046: // falador cassies shields
						Shop.open(player, 26, 0);
						break;

					case 1045: // catherby harrys fishing store
						Shop.open(player, 27, 0);
						break;

					case 2182:
					case 1600:
						Bank.open(player);
						break;

					case 1152:
						player.resetHits();
						player.getSkills().increaseLevelToMaximum(Skills.HITPOINTS, 99);
						player.getSkills().setLevel(Skills.PRAYER,
								player.getSkills().getLevelForExperience(Skills.PRAYER));
						player.getSkills().setPrayerPoints(player.getSkills().getLevelForExperience(Skills.PRAYER),
								true);
						player.getCombatState().increaseSpecial(100);
						if (player.getPoisonDrainTick() != null)
							player.getPoisonDrainTick().stop();
						if (player.getCombatState().getPoisonDamage() > 0)
							player.getCombatState().setPoisonDamage(0, null);
						player.removeVenom();
						player.getWalkingQueue().setEnergy(100);
						player.getActionSender().sendRunEnergy();
						player.venomDamage = 6;
						player.getActionSender().sendDialogue("Nurse Sarah", DialogueType.NPC, 1152,
								FacialAnimation.HAPPY,
								"There you go; I've completely cured you and restored your hitpoints as well as your prayer points!");
						break;

					case 2462:
						Shop.open(player, 2, 0);
						break;
					case 315:
						Shop.open(player, 11, 0);
						break;
					case 1755:
						pestControlService.openShop(player);
						break;
					case 394:
					case 395:
					case 3194:
						Bank.open(player);
						break;

					case 7492:
						Shop.open(player, 7, 0);
						break;

					case 7608:
						Shop.open(player, 8, 0);
						break;

					case 7502:
						Shop.open(player, 10, 0);
						break;
					case 536:
						Shop.open(player, 5, 0);
						break;
					case 3248:
						DialogueManager.openDialogue(player, 74);
						break;
					case 514:
					case 515:
					case 508:
					case 509:
					case 516:
						Shop.open(player, 1, 1);
						break;
					case 518:
						Shop.open(player, 2, 1);
						break;
					case 519:
						Shop.open(player, 3, 1);
						break;
					}
					this.stop();
				}

				@Override
				public AnimationPolicy getAnimationPolicy() {
					return AnimationPolicy.RESET_ALL;
				}

				@Override
				public CancelPolicy getCancelPolicy() {
					return CancelPolicy.ALWAYS;
				}

				@Override
				public StackPolicy getStackPolicy() {
					return StackPolicy.NEVER;
				}
			};
		}

		// if (fishing != null && pathfindingService.travelToNpc(player, npc)) {
		// interactFishing(player, fishing, npc);
		// //player.addCoordinateAction(player.getWidth(), player.getHeight(),
		// npc.getLocation(), 1, 1, 1, action, fishing);
		// } else {
		interact(player, action, npc);
		// }
	}

	/**
	 * Handles npc attack option.
	 *
	 * @param player
	 *            The player.
	 * @param packet
	 *            The packet.
	 */
	private void handleOptionAttack(final Player player, Packet packet) {
		packet.getByteS();
		final int id = packet.getShortA() & 0xFFFF;
		if (id < 0 || id >= Constants.MAX_NPCS)
			return;
		if (player.getCombatState().isDead())
			return;
		player.getActionQueue().clearRemovableActions();
		player.getActionSender().removeChatboxInterface();

		final NPC npc = (NPC) World.getWorld().getNPCs().get(id);

		if (Constants.DEBUG)
			player.sendMessage("NPC OptionAttack: " + npc.getId() + ", Location: " + npc.getLocation().toString());

		if (npc != null) {
			if (npc.getCombatState().isDead())
				return;
			if (npc.getInstancedPlayer() != null && npc.getInstancedPlayer() != player) {
				player.getActionSender().sendMessage("This NPC was not spawned for you.");
				return;
			}
			player.getCombatState().setQueuedSpell(null);
			player.getCombatState().startAttacking(npc, true);
		}
	}

	/**
	 * Handles npc spell option.
	 *
	 * @param player
	 *            The player.
	 * @param packet
	 *            The packet.
	 */
	private void handleOptionSpell(final Player player, Packet packet) {
		packet.getByteA();
		int interfaceHash = packet.getLEInt();
		int childButton = interfaceHash & 0xFFFF;
		int id = packet.getLEShort();
		packet.getShort();
		if (id < 0 || id >= Constants.MAX_NPCS) {
			return;
		}
		if (player.getCombatState().isDead()) {
			return;
		}
		player.getActionQueue().clearRemovableActions();

		NPC npc = (NPC) World.getWorld().getNPCs().get(id);

		int spellOffset = player.getCombatState().getSpellBook() == SpellBook.ANCIENT_MAGICKS.getSpellBookId() ? -2
				: -1;
		int spellId = (childButton) + (spellOffset);
		player.getActionSender().sendDebugPacket(packet.getOpcode(), "NpcSpell",
				new Object[] { "ID: " + npc.getDefinition().getId(), "Index: " + id, "Spell Id: " + spellId });
		
		Spell spell = Spell.forId(spellId, SpellBook.forId(player.getCombatState().getSpellBook()));
		if (spell != null) {
			if (spell.getSpellType() == SpellType.NON_COMBAT) {
				return;
			}
			player.setAttribute("magicMove", true);
			player.setAttribute("castSpell", spell);
			// MagicCombatAction.setAutocast(player, null, -1, false);
			player.getCombatState().setQueuedSpell(spell);
			player.getCombatState().startAttacking(npc, false);
		}
	}

	/**
	 * Handles NPC option examine.
	 *
	 * @param player
	 *            The player.
	 * @param packet
	 *            The packet.
	 */
	private void handleOptionExamine(Player player, Packet packet) {
		int id = packet.getLEShort() & 0xFFFF;
		if (id < 0 || id >= Constants.MAX_NPCS)
			return;
		if (player.getCombatState().isDead())
			return;

		NPCDefinition npcDef = NPCDefinition.forId(id);

		if (id == 2989) {
			player.sendMessage("He really seems to act as if he'd be waiting for something to happen..");
			return;
		}

		if (npcDef != null)
			player.getActionSender().sendMessage(npcDef.getDescription());

		examineService.openMonsterExamine(player, id);

	}

	private void interact(Player player, Action action, NPC npc) {

		final NpcReachedPrecondition reached = new NpcReachedPrecondition(npc);
		boolean under = player.getLocation().equals(npc.getLocation());

		if (reached.targetReached(player.getX(), player.getY(), npc.getX(), npc.getY()) || under) {
			player.getActionQueue().addAction(action);
			return;
		}

		if (pathfindingService.travelToNpc(player, npc) || under) {
			final Action submit = action;
			final WalkingQueue.Point target = player.getWalkingQueue().isEmpty()
					? new WalkingQueue.Point(player.getLocation().getX(), player.getLocation().getY(), -1)
					: player.getWalkingQueue().getWaypoints().getLast();

			final Location npcTarget = npc.getLocation().transform(0, 0, 0);
			int size = npc.getSize();
			World.getWorld().submitAreaEvent(player, new CoordinateEvent(player, target.getX(), target.getY(), size) {
				@Override
				public void execute() {
					if (reached.targetReached(player.getLocation().getX(), player.getLocation().getY(),
							npcTarget.getX(), npcTarget.getY())) {
						player.getActionQueue().addAction(submit);
					}
				}
			});
		}
	}

	private static final int OPTION_1 = 136, OPTION_2 = 212, OPTION_3 = 208, OPTION_TRADE = 52, OPTION_ATTACK = 45,
			OPTION_SPELL = 121, OPTION_ITEM_ON_NPC = 232, OPTION_EXAMINE = 202;

	private final ClaimService claimService;
	private final PlayerService playerService;
	private final SlayerService slayerService;
	private final HookService hookService;
	private final PathfindingService pathfindingService;
	private final PestControlService pestControlService;
	private final MonsterExamineService examineService;
	private final LootingBagService lootingBagService;
	private final ItemService itemService;
	private final GrandExchangeService geService;

	public NPCOptionPacketHandler() {
		this.claimService = Server.getInjector().getInstance(ClaimService.class);
		this.playerService = Server.getInjector().getInstance(PlayerService.class);
		this.slayerService = Server.getInjector().getInstance(SlayerService.class);
		this.hookService = Server.getInjector().getInstance(HookService.class);
		this.pathfindingService = Server.getInjector().getInstance(PathfindingService.class);
		Server.getInjector().getInstance(BountyHunterService.class);
		Server.getInjector().getInstance(TournamentSuppliesService.class);
		this.pestControlService = Server.getInjector().getInstance(PestControlService.class);
		Server.getInjector().getInstance(ItemService.class);
		this.examineService = Server.getInjector().getInstance(MonsterExamineService.class);
		this.lootingBagService = Server.getInjector().getInstance(LootingBagService.class);
		this.itemService = Server.getInjector().getInstance(ItemService.class);
		this.geService = Server.getInjector().getInstance(GrandExchangeService.class);
	}
}