package org.rs2server.rs2.packet;

import org.rs2server.Server;
import org.rs2server.cache.format.CacheItemDefinition;
import org.rs2server.rs2.Constants;
import org.rs2server.rs2.action.Action;
import org.rs2server.rs2.action.impl.ConsumeItemAction;
import org.rs2server.rs2.content.*;
import org.rs2server.rs2.content.api.*;
import org.rs2server.rs2.content.misc.*;
import org.rs2server.rs2.domain.model.player.PlayerSettingsEntity;
import org.rs2server.rs2.domain.model.player.treasuretrail.clue.ClueScrollType;
import org.rs2server.rs2.domain.service.api.*;
import org.rs2server.rs2.domain.service.api.content.*;
import org.rs2server.rs2.domain.service.api.content.magic.OrbChargingService;
import org.rs2server.rs2.domain.service.api.loot.LootGenerationService;
import org.rs2server.rs2.domain.service.impl.skill.FarmingServiceImpl;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.Consumables.Drink;
import org.rs2server.rs2.model.boundary.BoundaryManager;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction;
import org.rs2server.rs2.model.container.*;
import org.rs2server.rs2.model.event.ClickEventManager;
import org.rs2server.rs2.model.event.EventListener.ClickOption;
import org.rs2server.rs2.model.map.path.ProjectilePathFinder;
import org.rs2server.rs2.model.minigame.impl.WarriorsGuild;
import org.rs2server.rs2.model.npc.Pet;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.skills.*;
import org.rs2server.rs2.model.skills.FletchingAction.*;
import org.rs2server.rs2.model.skills.PestleAndMortar;
import org.rs2server.rs2.model.skills.crafting.*;
import org.rs2server.rs2.model.skills.crafting.BoltCrafting.BoltTip;
import org.rs2server.rs2.model.skills.crafting.GemCutting.Gem;
import org.rs2server.rs2.model.skills.herblore.Herblore;
import org.rs2server.rs2.model.skills.herblore.Herblore.*;
import org.rs2server.rs2.model.skills.herblore.SuperCombatPotion;
import org.rs2server.rs2.net.*;
import org.rs2server.rs2.tickable.impl.StoppingTick;
import org.rs2server.rs2.util.Misc;
import org.slf4j.*;

import java.text.NumberFormat;
import java.util.*;

/**
 * Handles all item options and their possibilities.
 *
 * @author Vichy
 */
public class ItemOptionPacketHandler implements PacketHandler {

	/**
	 * Handles item option 1.
	 *
	 * @param player
	 *            The player.
	 * @param packet
	 *            The packet.
	 */
	private void handleItemOption1(Player player, Packet packet) { 
		int interfaceValue = packet.getInt(); 
		int slot = packet.getShort() & 0xFFFF;
		int id = packet.getShort() & 0xFFFF;
		int interfaceId = interfaceValue >> 16;
		@SuppressWarnings("unused")
		int childId = interfaceValue & 0xFFFF;
		if (player.getCombatState().isDead())
			return;

		if (player != null) {
			boolean starter = player.getAttribute("starter");
			if ((!player.isIronMan() && !player.isHardcoreIronMan() && !player.isUltimateIronMan()) && starter) {
				DialogueManager.openDialogue(player, 19000);
				return;
			}
		}
		player.getActionQueue().clearAllActions();
		player.getActionManager().stopAction();

		if (Constants.DEBUG)
			player.sendMessage("Item Option1: " + id + ", interfaceId: " + interfaceId);

		Item item = null;

		switch (interfaceId) {
		case Equipment.INTERFACE:
		case Equipment.SCREEN:
			if (slot >= 0 && slot < Equipment.SIZE) {
				item = player.getEquipment().get(slot);
				if (!player.canEmote()) // stops people unequipping during a
										// skillcape emote.
					return;
				if (!Container.transfer(player.getEquipment(), player.getInventory(), slot, id))
					player.getActionSender().sendMessage("Not enough space in inventory.");
				else {
					if (item != null && item.getEquipmentDefinition() != null) {
						for (int i = 0; i < item.getEquipmentDefinition().getBonuses().length; i++) {
							player.getCombatState().setBonus(i,
									player.getCombatState().getBonus(i) - item.getEquipmentDefinition().getBonus(i));
						}
						player.getActionSender().sendBonuses();
						if (slot == Equipment.SLOT_WEAPON) {
							player.setDefaultAnimations();
						}
					}
				}
			}
			break;
		case Inventory.INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				item = player.getInventory().get(slot);
				switch (item.getId()) {

				}
				break;
			}
		default:
			logger.info("Unhandled item option 1 : " + id + " - " + slot + " - " + interfaceId + ".");
			break;
		}
	}

	/**
	 * Handles item option 2.
	 *
	 * @param player
	 *            The player.
	 * @param packet
	 *            The packet.
	 */
	@SuppressWarnings("unused")
	private void handleItemOption2(Player player, Packet packet) {
		int interfaceValue = packet.getInt();
		int slot = packet.getShort() & 0xFFFF;
		int id = packet.getShort() & 0xFFFF;
		int interfaceId = interfaceValue >> 16;
		int childId = interfaceValue & 0xFFFF;
		if (player.getCombatState().isDead())
			return;
		player.getActionQueue().clearAllActions();
		player.getActionManager().stopAction();

		if (Constants.DEBUG)
			player.sendMessage("Item Option2: " + id + ", interfaceId: " + interfaceId);

		Item item = null;
		switch (interfaceId) {
		case Inventory.INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				item = player.getInventory().get(slot);

				if (item != null) {
					hookService.post(new GameItemInventoryActionEvent(player,
							GameItemInventoryActionEvent.ClickType.OPTION_2, item, slot));
					if (Jewellery.rubItem(player, slot, item.getId(), false))
						return;
					if (Constants.isMaxCape(item.getId())) {
						player.teleport(Location.create(2933, 3285), 0, 0, false);
						return;
					}

					Teleporting.breakTablet(player, item);

					String itemName = item.getDefinition2().getName().toLowerCase();
					if (itemName != null && itemName.contains("ring of wealth")) {
						DialogueManager.openDialogue(player, 12);
						return;
					}

					switch (item.getId()) {
					
					case 9750:
					case 9751:
						player.teleport(Location.create(2866, 3544, 0), 6, 6, false); 
						break;
					
					case 9948:
					case 9949:
						DialogueManager.openDialogue(player, 9948);
						break;
					
					case 9798:
					case 9799:
						player.teleport(Location.create(2604, 3401, 0), 1, 1, false); 
						break;

					case 9753:
					case 9754:
						player.getDatabaseEntity().toggleDefenceCape();
						player.sendMessage("Your defence cape will "
								+ (player.getDatabaseEntity().hasDefenceCape() ? "now" : "no longer")
								+ " act as a ring of life.");
						return;

					case 11808:
						if (player.getInventory().freeSlots() < 2) {
							player.sendMessage("You'll need 2 free inventory slots to dismantle your "
									+ item.getDefinition2().getName().toLowerCase() + ".");
							return;
						}
						player.getInventory().remove(item);
						player.getInventory().add(new Item(11816, 1));
						player.getInventory().add(new Item(11798, 1));
						break;
					case 11806:
						if (player.getInventory().freeSlots() < 2) {
							player.sendMessage("You'll need 2 free inventory slots to dismantle your "
									+ item.getDefinition2().getName().toLowerCase() + ".");
							return;
						}
						player.getInventory().remove(item);
						player.getInventory().add(new Item(11814, 1));
						player.getInventory().add(new Item(11798, 1));
						break;
					case 11804:
						if (player.getInventory().freeSlots() < 2) {
							player.sendMessage("You'll need 2 free inventory slots to dismantle your "
									+ item.getDefinition2().getName().toLowerCase() + ".");
							return;
						}
						player.getInventory().remove(item);
						player.getInventory().add(new Item(11812, 1));
						player.getInventory().add(new Item(11798, 1));
						break;
					case 11802:
						if (player.getInventory().freeSlots() < 2) {
							player.sendMessage("You'll need 2 free inventory slots to dismantle your "
									+ item.getDefinition2().getName().toLowerCase() + ".");
							return;
						}
						player.getInventory().remove(item);
						player.getInventory().add(new Item(11810, 1));
						player.getInventory().add(new Item(11798, 1));
						break;
					case 11864:
					case 11865:
					case 19639:
					case 19647:
					case 19643:
					case 19641:
					case 19645:
					case 19649:
						slayerService.sendCheckTaskMessage(player);
						break;
					case 13139:
					case 13140:
						player.teleport(Location.create(2729, 3424, 0), 0, 0, false);
						break;
					case 13112:
					case 13113:
						player.teleport(Location.create(3656, 3522, 0), 0, 0, false);
						break;
					case 13114:
					case 13115:
						DialogueManager.openDialogue(player, 13114);
						break;
					case 13121:
						player.teleport(Location.create(2606, 3222, 0), 0, 0, false);
						break;
					case 13122:
					case 13123:
					case 13124:
					case 20760:
						DialogueManager.openDialogue(player, 13122);
						break;
					case 13129:
					case 13130:
					case 13131:
					case 13132:
						player.teleport(Location.create(2641, 3674, 0), 4, 4, false);
						break;
					case 11140:
						player.teleport(Location.create(2826, 2995, 0), 4, 4, false);
						break;
					case 13103:
						DialogueManager.openDialogue(player, 13103);
						break;
					case 13141:
					case 13142:
					case 13143:
					case 13144:
						player.teleport(Location.create(2334, 3686, 0), 3, 3, false);
						break;
					case 13125:
						player.getWalkingQueue().setEnergy(100);
						player.getActionSender().sendRunEnergy();
						player.sendMessage("Your run energy has been fully replenished.");
						break;
					case 13126:
					case 13127:
					case 13128:
						player.teleport(Location.create(3051, 3287), 3, 3, false);
						break;
					case 11283:
					case 11284:
						player.sendMessage("Your dragonfire shield has " + player.dfsCharges + " charges.");
						break;
					case 11738:
						HerbBox box = new HerbBox(player);

						Optional<List<Item>> rewards = box.getHerbRewards();

						player.sendMessage("You open your herb box to unveil 10 herbs; they have been banked.");
						player.getInventory().remove(item);
						rewards.get().forEach(i -> player.getBank().add(new Item(i.getId() - 1, i.getCount())));
						break;
					case 12926:
					case 12931:
					case 12899:
					case 13199:
					case 13197:
					case 12904:
					case 11908: // uncharged trident of the seas
					case 11907: // charged trident of the seas
					case 12900: // uncharged toxic trident
					case 13116: // bonecrusher
						int charges = itemService.getCharges(player, item);

						if (charges < 0)
							charges = 0;

						player.getActionSender().sendMessage("Your " + item.getDefinition2().name + " has "
								+ Misc.formatNumber(charges) + " remaining charges.");
						break;
					case 20714: // tome of fire
						charges = itemService.getCharges(player, item);

						if (charges < 0)
							charges = 0;

						int pages = charges / 20;
						if (pages < 1)
							pages = 0;

						player.getActionSender().sendMessage("Your tome has " + Misc.formatNumber(charges)
								+ " charges remaining; pages: " + charges / 20 + ".");
						break;
					case 11941:
						lootingBagService.open(player);
						break;
					case 9780:
					case 9781:
						if (player.getSkills().getLevelForExperience(Skills.CRAFTING) < 99) {
							return;
						}
						player.teleport(Location.create(2933, 3285), 0, 0, false);
						break;
					}
				}
			}
			break;
		case Equipment.INTERFACE:
			if (slot >= 0 && slot < Equipment.SIZE) {
				item = player.getEquipment().get(slot);
				if (item != null) {
					switch (item.getId()) {

					case 2550:
						player.getActionSender()
								.sendMessage("<col=7f00ff>Your Ring of Recoil can deal "
										+ player.getCombatState().getRingOfRecoil()
										+ " more points of damage before shattering.");
						break;
					default:
						player.getActionSender().sendMessage("There is no way to operate that item.");
						break;
					}
				}
			}
			break;
		default:
			logger.info("Unhandled item option 2 : " + id + " - " + slot + " - " + interfaceId + ".");
			break;
		}
	}

	/**
	 * Handles item option 3.
	 *
	 * @param player
	 *            The player.
	 * @param packet
	 *            The packet.
	 */
	@SuppressWarnings("unused")
	private void handleItemOption3(Player player, Packet packet) {
		int interfaceValue = packet.getInt();
		int slot = packet.getShort() & 0xFFFF;
		int id = packet.getShort() & 0xFFFF;
		int interfaceId = interfaceValue >> 16;
		int childId = interfaceValue & 0xFFFF;
		if (player.getCombatState().isDead()) {
			return;
		}
		player.getActionQueue().clearAllActions();
		player.getActionManager().stopAction();

		if (Constants.DEBUG)
			player.sendMessage("Item Option3: " + id + ", interfaceId: " + interfaceId);

		Item item = null;

		switch (interfaceId) {

		case Inventory.INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				item = player.getInventory().get(slot);

				if (item != null) {
					hookService.post(new GameItemInventoryActionEvent(player,
							GameItemInventoryActionEvent.ClickType.OPTION_3, item, slot));

					switch (item.getId()) {
					case 13226:
						// player.getHerbSack().handleCheckSack();
						break;
					}
				}
			}
			break;
		default:
			logger.info("Unhandled item option 3 : " + id + " - " + slot + " - " + interfaceId + ".");
			break;
		}
	}

	/**
	 * Handles item option 4.
	 *
	 * @param player
	 *            The player.
	 * @param packet
	 *            The packet.
	 */
	@SuppressWarnings("unused")
	private void handleItemOption4(Player player, Packet packet) {
		int interfaceValue = packet.getLEInt();
		int slot = packet.getLEShort() & 0xFFFF;
		int id = packet.getLEShortA() & 0xFFFF;
		int interfaceId = interfaceValue >> 16;
		int childId = interfaceValue & 0xFFFF;
		player.getActionQueue().clearAllActions();
		player.getActionManager().stopAction();

		if (Constants.DEBUG)
			player.sendMessage("Item Option4: " + id + ", interfaceId: " + interfaceId);

		Item item = null;
		switch (interfaceId) {
		case Inventory.INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				item = player.getInventory().get(slot);

				if (item != null) {
					hookService.post(new GameItemInventoryActionEvent(player,
							GameItemInventoryActionEvent.ClickType.OPTION_4, item, slot));

					if (item.getId() >= 11866 && item.getId() <= 11873) {
						slayerService.sendCheckTaskMessage(player);
						return;
					}

					String itemName = item.getDefinition2().getName().toLowerCase();
					if (itemName != null && itemName.contains("slayer helmet")) {
						if (SlayerHelmAction.disassembleHelm(player, item))
							return;
					}
					if (item.getId() == 2572 || item.getId() == 12785) {
						player.sendMessage("Your ring has no charges left.");
						return;
					}
					if (itemName != null && itemName.contains("black mask")) {
						player.getInventory().remove(item);
						player.getInventory().add(new Item(8921));
						player.sendMessage("You've removed all charges from your mask.");
					}
					if (Jewellery.rubItem(player, slot, item.getId(), false))
						return;
					switch (item.getId()) {

					case 20714:
						DialogueManager.openDialogue(player, 20714);
						break;

					case 13125:
						player.getActionSender().sendItemDialogue(13125,
								"Alchemy option is disabled until further notice.");
						break;

					case 13126:
					case 13127:
					case 13128:
						DialogueManager.openDialogue(player, 13126);
						break;

					case 12929: // dismantle serp helm
						DialogueManager.openDialogue(player, 6487);
						break;

					case 12902: // dismantle toxic staff
						DialogueManager.openDialogue(player, 6489);
						break;

					case 12932: // dismantle magic fang
						DialogueManager.openDialogue(player, 6491);
						break;

					case 12924: // dismantle toxic blowpipe
						DialogueManager.openDialogue(player, 6493);
						break;

					case 11738: // herb box check
						player.sendMessage("This herb box contains 10 more grimy herbs.");
						break;

					case 12922: // dismantle tanzanite fang
						DialogueManager.openDialogue(player, 6495);
						break;

					case 12900: // dismantle toxic trident
						DialogueManager.openDialogue(player, 6500);
						break;

					// restore dye from dyed serp helms
					case 13196: // tanz uncharged
					case 13197: // tanz charged
					case 13198: // magma uncharged
					case 13199: // magma charged
						if (player.getInventory().freeSlots() < 1) {
							player.sendMessage("You'll need an extra free inventory space to do this.");
							return;
						}
						Item restoredHelm = new Item((id == 13196 || id == 13198) ? 12929 : 12931);
						Item dye = new Item((id == 13196 || id == 13197) ? 13200 : 13201);
						player.getInventory().remove(item);
						player.getInventory().add(restoredHelm);
						player.getInventory().add(dye);
						break;

					case 12899: // trident of the swamp
						int charges = itemService.getCharges(player, item);

						if (charges > 0) {
							CacheItemDefinition def = CacheItemDefinition.get(itemService.getChargedItem(player, item));
							if (def != null) {
								Item chargedItem = new Item(def.getId(), charges);
								itemService.setChargesWithItem(player, item, chargedItem, -1);
								itemService.setCharges(player, item, -1);
								player.getInventory().add(chargedItem);
								player.getActionSender().sendMessage("You unload " + charges + " x " + def.name
										+ " from the " + item.getDefinition2().name);
							}
						}
						itemService.degradeItem(player, item);
						break;

					case 11907: // trident of the seas
						charges = itemService.getCharges(player, item);

						if (charges > 0) {
							itemService.setCharges(player, item, 0);
							player.getActionSender().sendMessage("You unload " + Misc.formatNumber(charges)
									+ " charges from your " + item.getDefinition2().name);
							Inventory.addDroppable(player, new Item(995, charges * 10));
							Inventory.addDroppable(player, new Item(554, charges * 5));
							Inventory.addDroppable(player, new Item(560, charges));
							Inventory.addDroppable(player, new Item(562, charges));
							charges = itemService.getCharges(player, item);
							if (charges == 0)
								player.getInventory().replace(11907, 11908);
						}
						break;

					case 13116: // bonecrusher
						charges = itemService.getCharges(player, item);

						if (charges < 1) {
							player.sendMessage("Your Bonecrusher has 0 remaining charges.");
							return;
						}
						charges = itemService.getCharges(player, new Item(13116));
						itemService.setCharges(player, new Item(13116), 0);
						Inventory.addDroppable(player, new Item(995, charges * 1000));
						player.sendMessage("You uncharged your Bonecrusher and received back "
								+ Misc.formatNumber(charges * 1000) + " coins.");
						break;

					case 11283:
						DragonfireShield.empty(player);
						break;
					case 4155: // slayer gem
						SlayerKillLog.handleSlayerLog(player);
						break;

					case 12006: // abyssal tentacle
						player.getActionSender().sendMessage("Your abyssal tentacle can perform "
								+ NumberFormat.getInstance(Locale.ENGLISH).format(itemService.getCharges(player, item))
								+ " more attacks.");
						break;
					case 12926:

						charges = itemService.getCharges(player, item);
						if (charges < 1) {
							player.getActionSender().sendMessage("Your blowpipe is empty; try loading some ammo.");
							return;
						}
						if (charges > 0) {
							CacheItemDefinition def = CacheItemDefinition.get(itemService.getChargedItem(player, item));
							if (def != null) {
								Item chargedItem = new Item(def.getId(), charges);
								if (player.getInventory().add(chargedItem)) {
									Item scales = new Item(12934, charges * 3);
									itemService.setChargesWithItem(player, item, chargedItem, -1);
									itemService.setCharges(player, item, -1);
									itemService.degradeItem(player, item);
									playerService.giveItem(player, scales, true);
									player.getActionSender()
											.sendMessage("You unload " + charges + " x " + def.name + " and "
													+ scales.getCount() + "x " + scales.getDefinition2().name
													+ " from the blowpipe.");
								}
							}
						}
						break;
					}
				}
			}
			break;
		}
	}

	/**
	 * Handles item option 5.
	 *
	 * @param player
	 *            The player.
	 * @param packet
	 *            The packet.
	 */
	@SuppressWarnings("unused")
	private void handleItemOption5(Player player, Packet packet) {
		int interfaceValue = packet.getInt();
		int slot = packet.getShort() & 0xFFFF;
		int id = packet.getShort() & 0xFFFF;
		int interfaceId = interfaceValue >> 16;
		int childId = interfaceValue & 0xFFFF;
		if (player.getCombatState().isDead()) {
			return;
		}
		player.getActionQueue().clearAllActions();
		player.getActionManager().stopAction();

		if (Constants.DEBUG)
			player.sendMessage("Item Option5: " + id + ", interfaceId: " + interfaceId);

		switch (interfaceId) {
		case Trade.TRADE_INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Trade.SIZE) {
				player.getInterfaceState().openEnterAmountInterface(interfaceId, slot, id);
			}
			break;
		case Trade.PLAYER_INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Trade.SIZE) {
				player.getInterfaceState().openEnterAmountInterface(interfaceId, slot, id);
			}
			break;
		case Bank.PLAYER_INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				player.getInterfaceState().openEnterAmountInterface(interfaceId, slot, id);
			}
			break;
		case Bank.BANK_INVENTORY_INTERFACE:
			if (slot >= 0 && slot < Bank.SIZE) {
				player.getInterfaceState().openEnterAmountInterface(interfaceId, slot, id);
			}
			break;
		default:
			logger.info("Unhandled item option 5 : " + id + " - " + slot + " - " + interfaceId + ".");
			break;
		}
	}

	/**
	 * Handles item option examine.
	 *
	 * @param player
	 *            The player.
	 * @param packet
	 *            The packet.
	 */
	private void handleItemOptionExamine(Player player, Packet packet) {
		int id = packet.getLEShort() & 0xFFFF;

		Item item = new Item(id);
		if (item.getDefinition() != null && item.getDefinition().getExamine() != null) {
			player.getActionSender().sendMessage(item.getDefinition().getExamine());
			return;
		}
		player.getActionSender().sendMessage("It's " + Misc.withPrefix(item.getDefinition().getName() + "."));
	}

	/**
	 * Handles item option 1.
	 *
	 * @param player
	 *            The player.
	 * @param packet
	 *            The packet.
	 */
	private void handleItemOptionClick1(final Player player, Packet packet) {
		int id = packet.getShortA() & 0xFFFF;
		int interfaceId = packet.getInt();
		int slot = packet.getShort() & 0xFFFF;
		if (player.getCombatState().isDead())
			return;
		player.getActionQueue().clearAllActions();
		player.getActionManager().stopAction();

		if (Constants.DEBUG)
			player.sendMessage("Item Option1: " + id + ", interfaceId: " + interfaceId);

		Item item = null;

		switch (interfaceId) {
		case Inventory.INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				item = player.getInventory().get(slot);
				if (item == null || id != item.getId())
					return;
				hookService.post(new GameItemInventoryActionEvent(player,
						GameItemInventoryActionEvent.ClickType.OPTION_1, item, slot));

				if (ClickEventManager.getEventManager().handleItemAction(player, player.getInventory().get(slot), slot,
						ClickOption.FIRST)) {
					return;
				}
				Herb herb = Herb.forId(id);
				if (herb != null) {
					if (player.getSkills().getLevelForExperience(Skills.HERBLORE) < herb.getRequiredLevel()) {
						player.getActionSender().sendMessage("You cannot clean this herb; you need a Herblore level of "
								+ herb.getRequiredLevel() + " to attempt this.");
						return;
					}
					player.getActionSender()
							.sendMessage("You clean the dirt from the " + org.rs2server.cache.format.CacheItemDefinition
									.get(herb.getReward()).getName().toLowerCase() + ".");
					player.getInventory().remove(new Item(herb.getId()), slot);
					player.getInventory().add(new Item(herb.getReward()), slot);
					player.getSkills().addExperience(Skills.HERBLORE, herb.getExperience());
					return;
				}
				if (player.getSkills().getPrayer().handleBury(new Item(item.getId()), slot))
					return;

				Teleporting.breakTablet(player, item);

				switch (item.getId()) {
				case 20724:
					if (player.getImbuedHeart() > System.currentTimeMillis()) {
						player.sendMessage("The heart is still drained of its power.");
						return;
					}
					player.setImbuedHeart(System.currentTimeMillis() + 420000);
					int modification = (int) ((player.getSkills().getLevelForExperience(Skills.MAGIC) * 0.1) + 1);
					// Only modify level if actual is higher or equal then current (to prevent
					// abuse)
					if (player.getSkills().getLevelForExperience(Skills.MAGIC) >= player.getSkills()
							.getLevel(Skills.MAGIC))
						player.getSkills().increaseLevel(Skills.MAGIC, modification);
					player.playGraphics(Graphic.create(1196));
					break;
				case 20703:
					SupplyCrate supplycrate = new SupplyCrate(player);
					supplycrate.handleReward(player, slot);
					break;
				case 13193:
					player.getInventory().remove(new Item(13193, 1));
					player.getInventory().add(new Item(8882, 100));
					break;
				case 11640:
					player.getInventory().remove(new Item(11640, 1));
					player.getInventory().add(new Item(995, 100000));
					player.sendMessage("You've redeemed your vote book for 100k coins.");
					break;
				case 12641:
					player.getInventory().remove(new Item(12641, 1));
					Inventory.addDroppable(player, new Item(12640, 100));
					break;
				case 21079:
					boolean augury = player.getDatabaseEntity().getStatistics().hasUnlockedAugury();
					if (augury) {
						player.sendMessage("You can make out some faded words on the ancient parchment. "
								+ "It appears to be an archaic invocation of the gods.");
						player.sendMessage("However there's nothing more for you to learn..");
						return;
					}
					player.getDatabaseEntity().getStatistics().unlockAugury(true);
					player.getInventory().remove(new Item(21079, 1));
					player.sendMessage("You can make out some faded words on the ancient parchment. "
							+ "It appears to be an archaic invocation of the gods!");
					player.sendMessage("<col=ff0000>Congratulations! You've unlocked the Augury prayer.");
					break;
				case 21034:
					boolean rigour = player.getDatabaseEntity().getStatistics().hasUnlockedRigour();
					if (rigour) {
						player.sendMessage("You can make out some faded words on the ancient parchment. "
								+ "It appears to be an archaic invocation of the gods.");
						player.sendMessage("However there's nothing more for you to learn..");
						return;
					}
					player.getDatabaseEntity().getStatistics().unlockRigour(true);
					player.getInventory().remove(new Item(21034, 1));
					player.sendMessage("You can make out some faded words on the ancient parchment. "
							+ "It appears to be an archaic invocation of the gods!");
					player.sendMessage("<col=ff0000>Congratulations! You've unlocked the Rigour prayer.");
					break;
				case 21047:
					boolean preserve = player.getDatabaseEntity().getStatistics().hasUnlockedPreserve();
					if (preserve) {
						player.sendMessage("You can make out some faded words on the ancient parchment. "
								+ "It appears to be an archaic invocation of the gods.");
						player.sendMessage("However there's nothing more for you to learn..");
						return;
					}
					player.getDatabaseEntity().getStatistics().unlockPreserve(true);
					player.getInventory().remove(new Item(21047, 1));
					player.sendMessage("You can make out some faded words on the ancient parchment. "
							+ "It appears to be an archaic invocation of the gods!");
					player.sendMessage("<col=ff0000>Congratulations! You've unlocked the Preserve prayer.");
					break;
				case 7509:
					player.playAnimation(Animation.create(829));
					World.getWorld().submit(new StoppingTick(1) {
						@Override
						public void executeAndStop() {
							if (player.getSkills().getLevel(Skills.HITPOINTS) > 2) {
								player.forceChat("Ow! I nearly broke a tooth!");
								player.inflictDamage(new Hit(2), player);
							}
						}
					});
					break;
				case 13190:
					DialogueManager.openDialogue(player, 13190);
					break;
				case 11879:
					player.getInventory().remove(item);
					player.getInventory().add(new Item(228, 100));
					break;
				case 6199:
					MysteryBox mysteryBox = new MysteryBox(player);
					mysteryBox.handleReward(slot);
					break;
				case 5070:
				case 5071:
				case 5072:
				case 5073:
				case 5074:
					BirdNests nest = new BirdNests(player, item);
					nest.handleReward(slot);
					break;
				case 405:
					Casket casket = new Casket(player);
					casket.open(slot);
					break;
				case 11738:
					HerbBox herbloreBox = new HerbBox(player);

					Optional<List<Item>> rewards = herbloreBox.getHerbRewards();

					player.getInventory().remove(item);
					rewards.get().forEach(i -> playerService.giveItem(player, i, true));
					player.sendMessage("You open your herb box to unveil 10 herbs.");
					break;
				case 7956: // rare item caskets
					player.getInventory().remove(slot, new Item(7956, 1));
					final LootGenerationService gen = Server.getInjector().getInstance(LootGenerationService.class);
					final PlayerService ps = Server.getInjector().getInstance(PlayerService.class);
					final Item loot = gen.generateCasketLoot();
					if (loot == null)
						player.sendMessage("The casket was empty!");
					else {
						ps.giveItem(player, loot, true);
						player.sendMessage("You found some treasure!");
					}
					break;
				case 4155:
					DialogueManager.openDialogue(player, 513);
					break;
				case 11941:
					lootingBagService.check(player);
					break;
				case 12728:
					player.getInventory().remove(item, slot);
					player.getInventory().add(new Item(556, 500));
					break;
				case 12730:
					player.getInventory().remove(item, slot);
					player.getInventory().add(new Item(555, 500));
					break;
				case 11883:
					player.getInventory().remove(item, slot);
					player.getInventory().add(new Item(313, 500));
					break;
				case 11881:
					player.getInventory().remove(item, slot);
					player.getInventory().add(new Item(314, 500));
					break;
				case 12732:
					player.getInventory().remove(item, slot);
					player.getInventory().add(new Item(557, 500));
					break;
				case 12734:
					player.getInventory().remove(item, slot);
					player.getInventory().add(new Item(554, 500));
					break;
				case 12736:
					player.getInventory().remove(item, slot);
					player.getInventory().add(new Item(558, 500));
					break;
				case 12738:
					player.getInventory().remove(item, slot);
					player.getInventory().add(new Item(562, 500));
					break;
				case 12859:
					player.getInventory().remove(item, slot);
					player.getInventory().add(new Item(222, 500));
					break;
				case 6:
					for (GameObject obj : player.getRegion().getGameObjects()) {
						if (obj != null && obj.getType() == 10 && obj.getLocation().equals(player.getLocation())) {
							player.getActionSender().sendMessage("You cannot set up a cannon here.");
							return;
						}
					}
					if (player.getY() > 5000 || player.getRFD().isStarted() || player.getFightCave().isStarted()
							|| WarriorsGuild.IN_GAME.contains(player)
							|| BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "PestControl")
							|| BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "Cerberus")
							|| BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "ClanWarsFFA")
							|| BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "ClanWarsFFAFull")
							|| BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "DuelArena")
							|| BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "PestControlBoat")
							|| BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "ResourceArena")
							|| BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "Barrows")
							|| BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "God Wars Entrance")
							|| BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "MultiCombat_Bandos")
							|| BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "MultiCombat_Armadyl")
							|| BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "MultiCombat_Sara")
							|| BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "MultiCombat_Zammy")
							|| BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "BarrowsUnderground")
							|| BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "Kraken")
							|| BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "Zulrah")
							|| player.getPlane() != 0) {
						player.getActionSender().sendMessage("You cannot set up a cannon here.");
						return;
					}
					player.playAnimation(Animation.create(827));
					player.setAttribute("cannon", new Cannon(player, player.getLocation()));
					break;
				default:
					Action action = new ConsumeItemAction(player, item, slot);
					action.execute();
					break;
				}
				break;
			}
		}
	}

	/**
	 * Handles item on item option.
	 *
	 * @param player
	 *            The player.
	 * @param packet
	 *            The packet.
	 */
	@SuppressWarnings("unused")
	private void handleItemOptionItem(final Player player, Packet packet) {
		int fromInterfaceHash = packet.getInt();
		int usedWithId = packet.getShort();
		int toInterfaceHash = packet.getInt1();
		int slot = packet.getLEShortA();
		int usedWith = packet.getShortA();
		int usedWithSlot = packet.getShortA();

		int interfaceId = fromInterfaceHash >> 16;
		int toInterfaceId = toInterfaceHash >> 16;

		if (player.getCombatState().isDead())
			return;

		player.getActionQueue().clearAllActions();
		player.getActionManager().stopAction();

		Item usedItem = null;
		Item withItem = null;
		Item withItem1 = null;
		Item withItem2 = null;
		Item withItem3 = null;

		switch (interfaceId) {
		case Inventory.INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				usedItem = player.getInventory().get(slot);
				withItem = player.getInventory().get(usedWithSlot);

				if (Constants.DEBUG)
					player.sendMessage("ItemOnItem: usedItem: " + usedItem.getId() + " on item: " + withItem.getId());

				if (usedItem == null || withItem == null)
					return;
				if (usedWith != usedItem.getId())
					return;
				if (usedWithId != withItem.getId())
					return;
				if (player.getCombatState().isDead())
					return;
				if (LeatherCrafting.handleItemOnItem(player, usedItem, withItem))
					return;
				if (SnakeskinCrafting.handleItemOnItem(player, usedItem, withItem))
					return;
				MaxCapeService maxCapeService = Server.getInjector().getInstance(MaxCapeService.class);
				if (maxCapeService.addToMaxCape(player, usedItem, withItem))
					return;
				if (maxCapeService.addToMaxCape(player, withItem, usedItem))
					return;
				for (int i = 0; i < GemCrafting.ITEMS.length; i++) {
					if (GemCrafting.ITEMS[i][0] == usedItem.getId() || GemCrafting.ITEMS[i][0] == withItem.getId()) {
						GemCrafting.combine(player, i);
						return;
					}
				}

				if (FarmingServiceImpl.handleItemOnItem(player, usedItem, withItem))
					return;

				if ((usedItem.getId() == 995 && withItem.getId() == 13116)
						|| (withItem.getId() == 995 && usedItem.getId() == 13116)) {

					if (player.getItemService().getCharges(player, new Item(13116)) >= 10000) {
						player.getActionSender().sendItemDialogue(13116,
								"Your Bonecrusher is already at its max capacity.");
						return;
					}
					player.setInterfaceAttribute("bonecrusher_charges", true);
					player.getActionSender().sendEnterAmountInterface();
					return;
				}

				if ((usedItem.getId() == 20714 && withItem.getId() == 20718)
						|| (withItem.getId() == 20714 && usedItem.getId() == 20718)) {
					DialogueManager.openDialogue(player, 20715);
					return;
				}

				if (usedItem.getId() == 11908 || withItem.getId() == 11908 || usedItem.getId() == 11907
						|| withItem.getId() == 11907) {
					if (usedItem.getId() == 12932 || withItem.getId() == 12932) {
						if (usedItem.getId() == 11907 || withItem.getId() == 11907) {
							player.sendMessage("Please uncharge your trident first before doing that!");
							return;
						}
						player.getInventory().remove(new Item(11908));
						player.getInventory().remove(new Item(12932));
						player.getInventory().add(new Item(12900));
						return;
					}
					if (usedItem.getId() == 995 || withItem.getId() == 995 || usedItem.getId() == 560
							|| withItem.getId() == 560 || usedItem.getId() == 562 || withItem.getId() == 562
							|| usedItem.getId() == 554 || withItem.getId() == 554) {
						if (player.getInventory().hasItem(new Item(995, 10))
								&& player.getInventory().hasItem(new Item(560))
								&& player.getInventory().hasItem(new Item(562))
								&& player.getInventory().hasItem(new Item(554, 5))) {
							int coins = player.getInventory().getCount(995);
							int fires = player.getInventory().getCount(554);
							int chaos = player.getInventory().getCount(562);
							int deaths = player.getInventory().getCount(560);

							// charge count based on the item we have the least
							int charges = 1;

							if ((coins * 10) > (fires * 5))
								charges = fires / 5; // divide by 5 cuz that's used for 1 charge
							if ((fires * 5) > chaos)
								charges = chaos;
							if (chaos > deaths)
								charges = deaths;
							if (deaths > (coins * 10))
								charges = coins / 10; // divide by 10 cuz that's used for 1 charge

							if (player.getInventory().getCount(560) < charges)
								charges = deaths;
							if (player.getInventory().getCount(562) < charges)
								charges = chaos;
							if (player.getInventory().getCount(554) < (charges * 5))
								charges = fires / 5;
							if (player.getInventory().getCount(995) < (charges * 10))
								charges = coins / 10;

							player.getInventory().removeItems(new Item(995, (charges * 10)), new Item(560, charges),
									new Item(554, (charges * 5)), new Item(562, charges));

							player.getActionSender().sendItemDialogue(11907, "You've added <col=ff0000>"
									+ Misc.formatNumber(charges) + "</col> charges to your trident.");

							player.getItemService().setCharges(player, new Item(11907),
									player.getItemService().getCharges(player, new Item(11907)) + charges);

							if (usedItem.getId() == 11908 || withItem.getId() == 11908)
								player.getInventory().replace(11908, 11907);

						} else
							player.sendMessage("You need 10 coins, 5 fire runes, 1 chaos rune and 1 death rune.");
					} else
						player.sendMessage("Nothing interesting happens.");
					return;
				}

				if (usedItem.getId() == 11941 || withItem.getId() == 11941) {
					Item otherItem;
					int otherIndex;
					if (usedItem.getId() == 11941) {
						otherItem = withItem;
						otherIndex = usedWithSlot;
					} else {
						otherItem = usedItem;
						otherIndex = slot;
					}
					if (player.getInventory().getCount(otherItem.getId()) == 1) {
						lootingBagService.deposit(player, player.getInventory().getSlotById(otherItem.getId()),
								otherItem.getId(), 1);
						return;
					}
					player.setInterfaceAttribute("lootingBagItem", otherItem);
					player.setInterfaceAttribute("lootingBagIndex", otherIndex);
					DialogueManager.openDialogue(player, 11941);
					return;
				}

				if ((usedItem.getId() == 13196 || withItem.getId() == 13196 && player.getInventory().contains(13196))
						|| (usedItem.getId() == 13197
								|| withItem.getId() == 13197 && player.getInventory().contains(13197))) {
					Item scales;
					Item helm;
					if (usedItem.getId() == 13196 || usedItem.getId() == 13197) {
						scales = withItem;
						helm = usedItem;
					} else {
						scales = usedItem;
						helm = withItem;
					}

					itemService.upgradeItem(player, helm, scales);
					return;
				}

				if ((usedItem.getId() == 13198 || withItem.getId() == 13198 && player.getInventory().contains(13198))
						|| (usedItem.getId() == 13199
								|| withItem.getId() == 13199 && player.getInventory().contains(13199))) {
					Item scales;
					Item helm;
					if (usedItem.getId() == 13198 || usedItem.getId() == 13199) {
						scales = withItem;
						helm = usedItem;
					} else {
						scales = usedItem;
						helm = withItem;
					}

					itemService.upgradeItem(player, helm, scales);
					return;
				}
				if ((usedItem.getId() == 12902 || withItem.getId() == 12902) && player.getInventory().contains(12902)
						|| (usedItem.getId() == 12904 || withItem.getId() == 12904)
								&& player.getInventory().contains(12904)) {
					Item with;
					Item staff;
					if (usedItem.getId() == 12902 || usedItem.getId() == 12904) {
						with = withItem;
						staff = usedItem;
					} else {
						with = usedItem;
						staff = withItem;
					}
					if (with.getId() == 12934) {
						itemService.upgradeItem(player, staff, with);
						return;
					}
				}
				if ((usedItem.getId() == 12900 || withItem.getId() == 12900) && player.getInventory().contains(12900)
						|| (usedItem.getId() == 12899 || withItem.getId() == 12899)
								&& player.getInventory().contains(12899)) {
					Item with;
					Item staff;
					if (usedItem.getId() == 12900 || usedItem.getId() == 12899) {
						with = withItem;
						staff = usedItem;
					} else {
						with = usedItem;
						staff = withItem;
					}
					if (with.getId() == 12934) {
						itemService.upgradeItem(player, staff, with);
						return;
					}
				}
				if ((usedItem.getId() == 13231 && withItem.getId() == 11840)
						|| (usedItem.getId() == 11840 && withItem.getId() == 13231)) {
					Item with;
					Item crystal;
					if (usedItem.getId() == 11840) {
						with = withItem;
						crystal = usedItem;
					} else {
						with = usedItem;
						crystal = withItem;
					}
					if (player.getSkills().getLevelForExperience(Skills.MAGIC) < 60
							|| player.getSkills().getLevelForExperience(Skills.RUNECRAFTING) < 60) {
						player.getActionSender().sendMessage("You need 60 magic and runecrafting to do this.");
						return;
					}
					itemService.upgradeItem(player, with, crystal);
					return;
				}

				if ((usedItem.getId() == 13265 && withItem.getId() == 187)
						|| (usedItem.getId() == 187 && withItem.getId() == 13265)) {
					Item with;
					Item crystal;
					if (usedItem.getId() == 187) {
						with = withItem;
						crystal = usedItem;
					} else {
						with = usedItem;
						crystal = withItem;
					}
					itemService.upgradeItem(player, with, crystal);
					return;
				}

				if ((usedItem.getId() == 13229 && withItem.getId() == 2577)
						|| (usedItem.getId() == 2577 && withItem.getId() == 13229)) {
					Item with;
					Item crystal;
					if (usedItem.getId() == 2577) {
						with = withItem;
						crystal = usedItem;
					} else {
						with = usedItem;
						crystal = withItem;
					}
					if (player.getSkills().getLevelForExperience(Skills.MAGIC) < 60
							|| player.getSkills().getLevelForExperience(Skills.RUNECRAFTING) < 60) {
						player.getActionSender().sendMessage("You need 60 Magic and Runecrafting to do this.");
						return;
					}
					itemService.upgradeItem(player, with, crystal);
					return;
				}

				if ((usedItem.getId() == 13227 && withItem.getId() == 6920)
						|| (usedItem.getId() == 6920 && withItem.getId() == 13227)) {
					Item with;
					Item crystal;
					if (usedItem.getId() == 6920) {
						with = withItem;
						crystal = usedItem;
					} else {
						with = usedItem;
						crystal = withItem;
					}
					if (player.getSkills().getLevelForExperience(Skills.MAGIC) < 60
							|| player.getSkills().getLevelForExperience(Skills.RUNECRAFTING) < 60) {
						player.getActionSender().sendMessage("You need 60 magic and runecrafting to do this.");
						return;
					}
					itemService.upgradeItem(player, with, crystal);
					return;
				}

				if ((usedItem.getId() == 13233 && withItem.getId() == 6739)
						|| (usedItem.getId() == 6739 && withItem.getId() == 13233)) {
					Item with;
					Item stone;
					if (usedItem.getId() == 6739) {
						with = withItem;
						stone = usedItem;
					} else {
						with = usedItem;
						stone = withItem;
					}
					if (player.getSkills().getLevelForExperience(Skills.FIREMAKING) < 85) {
						player.getActionSender().sendMessage("You need a Firemaking level of 85 to do this.");
						return;
					}
					itemService.upgradeItem(player, with, stone);
					return;
				}

				if ((usedItem.getId() == 11864 && withItem.getId() == 7979)
						|| (withItem.getId() == 11864 && usedItem.getId() == 7979)) {
					if (!player.getDatabaseEntity().getSlayerSkill().isUnlockedRedSlayerHelm()) {
						player.getActionSender().sendMessage(
								"You have not yet unlocked this feature, unlock it by speaking with your Slayer master.");
						return;
					}
					player.getInventory().remove(usedItem);
					player.getInventory().remove(withItem);
					player.getInventory().add(new Item(19647));
					player.sendMessage("You've combined the head with your helm and created a red slayer helmet.");
					return;
				}

				if ((usedItem.getId() == 1785 && withItem.getId() == 1775)
						|| (withItem.getId() == 1785 && usedItem.getId() == 1775))
					player.getActionSender().sendInterface(542, false);

				if ((usedItem.getId() == 11865 && withItem.getId() == 7979)
						|| (withItem.getId() == 11865 && usedItem.getId() == 7979)) {
					if (!player.getDatabaseEntity().getSlayerSkill().isUnlockedRedSlayerHelm()) {
						player.getActionSender().sendMessage(
								"You have not yet unlocked this feature, unlock it by speaking with your Slayer master.");
						return;
					}
					player.getInventory().remove(usedItem);
					player.getInventory().remove(withItem);
					player.getInventory().add(new Item(19649));
					player.sendMessage("You've combined the head with your helm and created a red slayer helmet (i).");
					return;
				}

				if ((usedItem.getId() == 13233 && withItem.getId() == 11920)
						|| (usedItem.getId() == 11920 && withItem.getId() == 13233)) {
					if (player.getSkills().getLevelForExperience(Skills.SMITHING) < 85) {
						player.getActionSender().sendMessage("You need 85 Smithing to do this.");
						return;
					}
					if (player.getInventory().contains(13233) && player.getInventory().contains(11920)) {
						player.getInventory().remove(new Item(13233, 1));
						player.getInventory().remove(new Item(11920, 1));
						player.getInventory().add(new Item(13243));
					}
					return;
				}

				if ((usedItem.getId() == 12932 && withItem.getId() == 11908)
						|| (withItem.getId() == 12932 && usedItem.getId() == 11908)) {
					if (player.getSkills().getLevel(Skills.CRAFTING) < 59) {
						player.sendMessage("You need a Fletching level of at least 59 in order to do this.");
						return;
					}
					player.getInventory().remove(usedItem);
					player.getInventory().remove(withItem);
					player.getInventory().add(new Item(12900));
					player.sendMessage("You craft an uncharged toxic trident.");
					return;
				}

				if ((usedItem.getId() == 6573 && withItem.getId() == 19529)
						|| (withItem.getId() == 6573 && usedItem.getId() == 19529)) {
					player.getInventory().remove(usedItem);
					player.getInventory().remove(withItem);
					player.getInventory().add(new Item(19496));
					player.sendMessage("You combine the onyx with a zenyte shard and create an uncut zenyte.");
					return;
				}

				if ((usedItem.getId() == 19592 && withItem.getId() == 19586)
						|| (withItem.getId() == 19592 && usedItem.getId() == 19586)) {
					if (player.getSkills().getLevel(Skills.FLETCHING) < 47) {
						player.sendMessage("You need a Fletching level of at least 47 in order to do this.");
						return;
					}
					player.getInventory().remove(usedItem);
					player.getInventory().remove(withItem);
					player.getInventory().add(new Item(19595));
					player.getSkills().addExperience(Skills.FLETCHING, 15);
					player.sendMessage("You attach the limbs onto the frame.");
					return;
				}

				if ((usedItem.getId() == 19595 && withItem.getId() == 19601)
						|| (withItem.getId() == 19595 && usedItem.getId() == 19601)) {
					if (player.getSkills().getLevel(Skills.FLETCHING) < 47) {
						player.sendMessage("You need a Fletching level of at least 47 in order to do this.");
						return;
					}
					player.getInventory().remove(usedItem);
					player.getInventory().remove(withItem);
					player.getInventory().add(new Item(19604));
					player.getSkills().addExperience(Skills.FLETCHING, 15);
					player.sendMessage("You attach the spring onto the incomplete ballista.");
					return;
				}

				if ((usedItem.getId() == 19604 && withItem.getId() == 19610)
						|| (withItem.getId() == 19604 && usedItem.getId() == 19610)) {
					if (player.getSkills().getLevel(Skills.FLETCHING) < 47) {
						player.sendMessage("You need a Fletching level of at least 47 in order to do this.");
						return;
					}
					player.getInventory().remove(usedItem);
					player.getInventory().remove(withItem);
					player.getInventory().add(new Item(19478));
					player.getSkills().addExperience(Skills.FLETCHING, 300);
					player.sendMessage("You attach the monkey tail and create a light ballista.");
					return;
				}

				if ((usedItem.getId() == 19592 && withItem.getId() == 19589)
						|| (withItem.getId() == 19592 && usedItem.getId() == 19589)) {
					if (player.getSkills().getLevel(Skills.FLETCHING) < 72) {
						player.sendMessage("You need a Fletching level of at least 72 in order to do this.");
						return;
					}
					player.getInventory().remove(usedItem);
					player.getInventory().remove(withItem);
					player.getInventory().add(new Item(19598));
					player.getSkills().addExperience(Skills.FLETCHING, 30);
					player.sendMessage("You attach the limbs onto the frame.");
					return;
				}

				if ((usedItem.getId() == 19598 && withItem.getId() == 19601)
						|| (withItem.getId() == 19598 && usedItem.getId() == 19601)) {
					if (player.getSkills().getLevel(Skills.FLETCHING) < 72) {
						player.sendMessage("You need a Fletching level of at least 72 in order to do this.");
						return;
					}
					player.getInventory().remove(usedItem);
					player.getInventory().remove(withItem);
					player.getInventory().add(new Item(19607));
					player.getSkills().addExperience(Skills.FLETCHING, 30);
					player.sendMessage("You attach the spring onto the incomplete ballista.");
					return;
				}

				if ((usedItem.getId() == 19607 && withItem.getId() == 19610)
						|| (withItem.getId() == 19607 && usedItem.getId() == 19610)) {
					if (player.getSkills().getLevel(Skills.FLETCHING) < 72) {
						player.sendMessage("You need a Fletching level of at least 72 in order to do this.");
						return;
					}
					player.getInventory().remove(usedItem);
					player.getInventory().remove(withItem);
					player.getInventory().add(new Item(19607));
					player.sendMessage("You attach the monkey tail and create a heavy ballista.");
					return;
				}

				if ((usedItem.getId() == 12929 || withItem.getId() == 12929 && player.getInventory().contains(12929))
						|| (usedItem.getId() == 12931
								|| withItem.getId() == 12931 && player.getInventory().contains(12931))) {
					Item with;
					Item helm;
					if (usedItem.getId() == 12927 || usedItem.getId() == 12929) {
						with = withItem;
						helm = usedItem;
					} else {
						with = usedItem;
						helm = withItem;
					}

					if (helm.getId() == 12929 && with.getId() == 13201 && player.getInventory().remove(helm) > 0) {
						player.getInventory().add(new Item(13198));
						player.getInventory().remove(new Item(13201));
					} else if (helm.getId() == 12929 && with.getId() == 13200
							&& player.getInventory().remove(helm) > 0) {
						player.getInventory().add(new Item(13196));
						player.getInventory().remove(new Item(13200));
					} else {
						if (helm.getId() == 12929 && player.getInventory().contains(12931)
								|| player.getBank().getCount(12931) > 0) {
							return;
						}
						itemService.upgradeItem(player, helm, with);
					}
					return;
				}
				if (usedItem.getId() == 11791 || withItem.getId() == 11791) {
					Item otherItem;
					if (usedItem.getId() == 11791) {
						otherItem = withItem;
					} else {
						otherItem = usedItem;
					}
					if (otherItem.getId() == 12932) {
						player.getInventory().remove(new Item(11791));
						player.getInventory().remove(new Item(12932));
						player.getInventory().add(new Item(12902));
					}
				}
				if (usedItem.getId() == 11335 && withItem.getId() == 12538
						|| usedItem.getId() == 12538 && withItem.getId() == 11335) {
					player.getInventory().remove(usedItem);
					player.getInventory().remove(withItem);
					player.getInventory().add(new Item(12417));
				}
				if (usedItem.getId() == 3140 && withItem.getId() == 12534
						|| usedItem.getId() == 12534 && withItem.getId() == 3140) {
					player.getInventory().remove(usedItem);
					player.getInventory().remove(withItem);
					player.getInventory().add(new Item(12414));
				}
				if (usedItem.getId() == 12337 && withItem.getId() == 1042
						|| usedItem.getId() == 1042 && withItem.getId() == 12337) {
					player.getInventory().remove(usedItem);
					player.getInventory().remove(withItem);
					player.getInventory().add(new Item(12399));
				}
				if (usedItem.getId() == 12432 && withItem.getId() == 12353
						|| usedItem.getId() == 12353 && withItem.getId() == 12432) {
					player.getInventory().remove(usedItem);
					player.getInventory().remove(withItem);
					player.getInventory().add(new Item(12434));
				}
				// spirit shield on holy exilir
				if (usedItem.getId() == 12833 && withItem.getId() == 12829
						|| usedItem.getId() == 12829 && withItem.getId() == 12833) {
					player.getInventory().remove(usedItem);
					player.getInventory().remove(withItem);
					player.getInventory().add(new Item(12831));
				}
				// spirit shield on ely sigil
				if (usedItem.getId() == 12819 && withItem.getId() == 12831
						|| usedItem.getId() == 12831 && withItem.getId() == 12819) {
					player.getInventory().remove(usedItem);
					player.getInventory().remove(withItem);
					player.getInventory().add(new Item(12817));
				}
				// spirit shield on spectral
				if (usedItem.getId() == 12831 && withItem.getId() == 12823
						|| usedItem.getId() == 12823 && withItem.getId() == 12831) {
					player.getInventory().remove(usedItem);
					player.getInventory().remove(withItem);
					player.getInventory().add(new Item(12821));
				}
			}
			// spirit shield on arcane
			if (usedItem.getId() == 12827 && withItem.getId() == 12831
					|| usedItem.getId() == 12831 && withItem.getId() == 12827) {
				player.getInventory().remove(usedItem);
				player.getInventory().remove(withItem);
				player.getInventory().add(new Item(12825));
			}
			if (usedItem.getId() == 1187 && withItem.getId() == 12532
					|| usedItem.getId() == 12532 && withItem.getId() == 1187) {
				player.getInventory().remove(usedItem);
				player.getInventory().remove(withItem);
				player.getInventory().add(new Item(12418));
			}
			if (usedItem.getId() == 12536 || withItem.getId() == 12536) {
				Item otherItem;
				if (usedItem.getId() == 12536) {
					otherItem = withItem;
				} else {
					otherItem = usedItem;
				}
				if (otherItem.getId() == 4087 || otherItem.getId() == 4585) {
					player.getInventory().remove(usedItem);
					player.getInventory().remove(withItem);
					player.getInventory().add(new Item(otherItem.getId() == 4087 ? 12415 : 12416));
				}
			}
			if (usedItem.getId() == 12530 || withItem.getId() == 12530) {
				Item otherItem;
				if (usedItem.getId() == 12530) {
					otherItem = withItem;
				} else {
					otherItem = usedItem;
				}
				if (otherItem.getId() == 6918 || otherItem.getId() == 6916 || otherItem.getId() == 6924) {
					if (player.getInventory().containsItems(6916, 6918, 6924, 12530)) {
						player.getInventory().removeItems(6916, 6918, 6924, 12530);
						player.getInventory().addItems(12419, 12420, 12421);
					}
				}
			}
			if (usedItem.getId() == 12528 || withItem.getId() == 12528) {
				Item otherItem;
				if (usedItem.getId() == 12528) {
					otherItem = withItem;
				} else {
					otherItem = usedItem;
				}
				if (otherItem.getId() == 6918 || otherItem.getId() == 6916 || otherItem.getId() == 6924) {
					if (player.getInventory().containsItems(6916, 6918, 6924, 12528)) {
						player.getInventory().removeItems(6916, 6918, 6924, 12528);
						player.getInventory().addItems(12457, 12458, 12459);
					}
				}
			}
			if (usedItem.getId() == 12526 && withItem.getId() == 6585
					|| usedItem.getId() == 6585 && withItem.getId() == 12526) {
				player.getInventory().remove(new Item(12526));
				player.getInventory().remove(new Item(6585));
				player.getInventory().add(new Item(12436));
			}
			if ((usedItem.getId() == 12924 || withItem.getId() == 12924 && player.getInventory().contains(12924))
					|| (usedItem.getId() == 12926
							|| withItem.getId() == 12926 && player.getInventory().contains(12926))) {

				Item darts;
				Item pipe;
				if (usedItem.getId() == 12924 || usedItem.getId() == 12926) {
					darts = withItem;
					pipe = usedItem;
				} else {
					darts = usedItem;
					pipe = withItem;
				}

				if (darts.getId() == 12934) {
					player.sendMessage("To charge your blowpipe use darts on it.");
					player.sendMessage("One charge is equivalent to 3 Zulrah's scales and 1 dart of any type.");
					return;
				}

				int chargedWith = itemService.getChargedItem(player, pipe);

				if (chargedWith != darts.getId() && chargedWith > 0) {
					CacheItemDefinition def = CacheItemDefinition.get(chargedWith);
					if (def != null && itemService.getCharges(player, pipe) > 0) {
						player.getActionSender().sendMessage("Your blowpipe is already charged with " + def.name + ".");
						return;
					}
				} else {

					int scaleAmount = player.getInventory().getCount(12934);
					int requiredScales = darts.getCount() * 3;

					if (scaleAmount >= requiredScales) {
						if (pipe.getId() == 12924 && player.getInventory().contains(12926)
								|| player.getBank().getCount(12926) > 0) {
							return;
						}
						itemService.upgradeItem(player, pipe, darts);
						player.getInventory().remove(new Item(12934, requiredScales));
					} else if (scaleAmount >= 3) {
						if (pipe.getId() == 12924 && player.getInventory().contains(12926)
								|| player.getBank().getCount(12926) > 0) {
							return;
						}
						int dartsNeeded = (int) Math.floor(scaleAmount / 3);
						Item requiredDarts = new Item(darts.getId(), dartsNeeded);
						itemService.upgradeItem(player, pipe, requiredDarts);
						player.getInventory().remove(new Item(12934, dartsNeeded * 3));
					} else {
						player.getActionSender().sendMessage("You do not have enough items to charge your blowpipe.");
						player.getActionSender()
								.sendMessage("One charge is equivalent to 3 Zulrah scales and 1 dart of any type.");
					}
				}
				return;
			}
			if (usedItem.getId() == 985 && withItem.getId() == 987
					|| usedItem.getId() == 987 && withItem.getId() == 985) {
				player.getInventory().remove(new Item(985, 1));
				player.getInventory().remove(new Item(987, 1));
				player.getInventory().add(new Item(989, 1));
				player.sendMessage("You join the two halves of the key together");
				return;
			}
			if (usedItem.getId() == 4151 || withItem.getId() == 4151) {
				Item otherItem = null;
				if (usedItem.getId() == 4151) {
					otherItem = withItem;
				} else {
					otherItem = usedItem;
				}
				if (otherItem.getId() == 12004) {
					DialogueManager.openDialogue(player, 4151);
				} else if (otherItem.getId() == 12769) {
					player.getInventory().remove(new Item(4151));
					player.getInventory().remove(new Item(12769));
					player.getInventory().add(new Item(12774));
				} else if (otherItem.getId() == 12771) {
					player.getInventory().remove(new Item(4151));
					player.getInventory().remove(new Item(12771));
					player.getInventory().add(new Item(12773));
				}
				return;
			}
			if ((usedItem.getId() == 12954 && withItem.getId() == 20143)
					|| (withItem.getId() == 12954 && usedItem.getId() == 20143)) {
				DialogueManager.openDialogue(player, 12954);
			}
			if ((usedItem.getId() == 11920 && withItem.getId() == 12800)
					|| (withItem.getId() == 11920 && usedItem.getId() == 12800)) {
				DialogueManager.openDialogue(player, 11920);
			}
			if ((usedItem.getId() == 11335 && withItem.getId() == 12538)
					|| (withItem.getId() == 11335 && usedItem.getId() == 12538)) {
				DialogueManager.openDialogue(player, 11335);
			}
			if ((usedItem.getId() == 1187 && withItem.getId() == 12532)
					|| (withItem.getId() == 1187 && usedItem.getId() == 12532)) {
				DialogueManager.openDialogue(player, 1187);
			}
			if ((usedItem.getId() == 11787 && withItem.getId() == 12798)
					|| (withItem.getId() == 11787 && usedItem.getId() == 12798)) {
				DialogueManager.openDialogue(player, 11787);
			}
			if ((usedItem.getId() == 11924 && withItem.getId() == 12802)
					|| (withItem.getId() == 11924 && usedItem.getId() == 12802)) {
				DialogueManager.openDialogue(player, 11924);
			}
			if ((usedItem.getId() == 4153 && withItem.getId() == 12849)
					|| (withItem.getId() == 4153 && usedItem.getId() == 12849)) {
				DialogueManager.openDialogue(player, 11930);
			}
			if ((usedItem.getId() == 11926 && withItem.getId() == 12802)
					|| (withItem.getId() == 11926 && usedItem.getId() == 12802)) {
				DialogueManager.openDialogue(player, 11927);
			}
			if ((usedItem.getId() == 4587 && withItem.getId() == 20002)
					|| (withItem.getId() == 4587 && usedItem.getId() == 20002)) {
				DialogueManager.openDialogue(player, 4587);
			}
			if ((usedItem.getId() == 19553 && withItem.getId() == 20062)
					|| (withItem.getId() == 19553 && usedItem.getId() == 20062)) {
				DialogueManager.openDialogue(player, 19553);
			}
			if ((usedItem.getId() == 12002 && withItem.getId() == 20065)
					|| (withItem.getId() == 12002 && usedItem.getId() == 20065)) {
				DialogueManager.openDialogue(player, 12002);
			}
			if ((usedItem.getId() == 11804 && withItem.getId() == 20071)
					|| (withItem.getId() == 11804 && usedItem.getId() == 20071)) {
				DialogueManager.openDialogue(player, 11804);
			}
			if ((usedItem.getId() == 11806 && withItem.getId() == 20074)
					|| (withItem.getId() == 11806 && usedItem.getId() == 20074)) {
				DialogueManager.openDialogue(player, 11807);
			}
			if ((usedItem.getId() == 11808 && withItem.getId() == 20077)
					|| (withItem.getId() == 11808 && usedItem.getId() == 20077)) {
				DialogueManager.openDialogue(player, 11813);
			}
			if ((usedItem.getId() == 11802 && withItem.getId() == 20068)
					|| (withItem.getId() == 11802 && usedItem.getId() == 20068)) {
				DialogueManager.openDialogue(player, 11816);
			}
			if ((usedItem.getId() == 11235 && withItem.getId() == 12757)
					|| (withItem.getId() == 11235 && usedItem.getId() == 12757)) {
				DialogueManager.openDialogue(player, 11819);
			}
			if ((usedItem.getId() == 11235 && withItem.getId() == 12759)
					|| (withItem.getId() == 11235 && usedItem.getId() == 12759)) {
				DialogueManager.openDialogue(player, 11822);
			}
			if ((usedItem.getId() == 11235 && withItem.getId() == 12761)
					|| (withItem.getId() == 11235 && usedItem.getId() == 12761)) {
				DialogueManager.openDialogue(player, 11825);
			}
			if ((usedItem.getId() == 11235 && withItem.getId() == 12763)
					|| (withItem.getId() == 11235 && usedItem.getId() == 12763)) {
				DialogueManager.openDialogue(player, 11828);
			}
			if (usedItem.getId() == 233 || withItem.getId() == 233) {
				Item otherItem = null;
				if (usedItem.getId() == 233) {
					otherItem = withItem;
				} else {
					otherItem = usedItem;
				}
				PestleAndMortar.Pestle pestle = PestleAndMortar.Pestle.forId(otherItem.getId());
				if (pestle != null) {
					player.getActionSender().sendItemOnInterface(309, 2, pestle.getNext(), 130);
					String itemName = CacheItemDefinition.get(pestle.getNext()).getName();
					player.getActionSender().sendString(309, 6, "<br><br><br><br>" + itemName);
					player.getActionSender().sendInterface(162, 546, 309, false);
					player.setInterfaceAttribute("pestle_type", pestle);
				}
			}
			if (usedItem.getId() == 11818 && withItem.getId() == 11820
					|| usedItem.getId() == 11818 && withItem.getId() == 11822
					|| usedItem.getId() == 11820 && withItem.getId() == 11818
					|| usedItem.getId() == 11820 && withItem.getId() == 11822
					|| usedItem.getId() == 11822 && withItem.getId() == 11818
					|| usedItem.getId() == 11822 && withItem.getId() == 11820) {
				if (player.getInventory().contains(11818) && player.getInventory().contains(11820)
						&& player.getInventory().contains(11822)) {
					if (player.getSkills().getLevel(Skills.SMITHING) < 80) {
						player.getActionSender().sendMessage("You need a Smithing level of 80 to combine these.");
						return;
					}
					player.getInventory().remove(new Item(11818, 1));
					player.getInventory().remove(new Item(11820, 1));
					player.getInventory().remove(new Item(11822, 1));
					player.getInventory().add(new Item(11798, 1));
					player.getActionSender().sendMessage("You combine the shards into a Godsword Blade.");
					player.getSkills().addExperience(Skills.SMITHING, 200);
					return;
				} else {
					player.getActionSender().sendMessage("You need all 3 shard pieces in order to do this.");
					return;
				}
			}
			if (SlayerHelmAction.handleItemOnItem(player, usedItem, withItem))
				return;
			if (usedItem.getId() == 590 || withItem.getId() == 590 && !player.isLighting()) {
				Item logItem = null;
				if (usedItem.getId() == 590) {
					logItem = withItem;
				} else {
					logItem = usedItem;
				}
				Firemaking firemaking = new Firemaking(player);
				firemaking.light(firemaking.findLog(logItem));
				return;
			}
			FletchingItem item = FletchingAction.getItemForId(usedItem.getId(), withItem.getId(), true);
			if (item != null) {
				Item[] materials = item.getMaterials();
				// System.out.println(item.getType());
				if (item.getType() == FletchingType.CUTTING) {
					FletchingGroup group = FletchingAction.groups.get(materials[0].getId());
					int iId = 305;
					if (group != FletchingGroup.LOGS) {
						iId = 304;
					}
					player.setInterfaceAttribute("fletch_group", group);
					for (int i = 0; i < group.getPossibleCreations().length; i++) {
						if (group == FletchingGroup.MAGIC_LOGS) {
							iId = 303;
							player.getActionSender().sendItemOnInterface(iId, 2 + i,
									group.getPossibleCreations()[i].getId(), 160);
							player.getActionSender().sendString(iId, (iId - 296) + (i * 4),
									"<br><br><br><br>" + group.getPossibleCreations()[i].getDefinition2().getName());
						} else {
							player.getActionSender().sendItemOnInterface(iId, 2 + i,
									group.getPossibleCreations()[i].getId(), 160);
							player.getActionSender().sendString(iId, (iId - 296) + (i * 4),
									"<br><br><br><br>" + group.getPossibleCreations()[i].getDefinition2().getName());
						}
					}
					player.getActionSender().sendChatboxInterface(iId);
				} else {
					int iId = item.getType() == FletchingType.STRINGING ? 309 : 582;
					player.setInterfaceAttribute("fletch_item", item);
					player.getActionSender().sendItemOnInterface(iId, 2, item.getProducedItem()[0].getId(), 150);
					player.getActionSender().sendString(iId, iId == 309 ? 6 : 5,
							"<br><br><br><br>" + CacheItemDefinition.get(item.getProducedItem()[0].getId()).getName());
					player.getActionSender().sendChatboxInterface(iId);
				}
			}
			if (usedItem.getId() == 11798 && withItem.getId() == 11810
					|| usedItem.getId() == 11810 && withItem.getId() == 11798) {// Armadyl
																				// Godsword
				player.getInventory().remove(usedItem);
				player.getInventory().remove(withItem);
				player.getInventory().add(new Item(11802));
				player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 11802, null,
						"You attach the hilt to the blade and make a Armadyl godsword.");
				// player.sendMessage("You attach the hilt to the blade and make
				// a Armadyl godsword.");
				return;
			}
			if (usedItem.getId() == 11798 && withItem.getId() == 11812
					|| usedItem.getId() == 11812 && withItem.getId() == 11798) {// Bandos
																				// Godsword
				player.getInventory().remove(usedItem);
				player.getInventory().remove(withItem);
				player.getInventory().add(new Item(11804));
				player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 11804, null,
						"You attach the hilt to the blade and make a Bandos godsword.");
				// player.sendMessage("You attach the hilt to the blade and make
				// a Bandos godsword.");
				return;
			}
			if (usedItem.getId() == 11798 && withItem.getId() == 11814
					|| usedItem.getId() == 11814 && withItem.getId() == 11798) {// Sara
																				// Godsword
				player.getInventory().remove(usedItem);
				player.getInventory().remove(withItem);
				player.getInventory().add(new Item(11806));
				player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 11806, null,
						"You attach the hilt to the blade and make a Saradomin godsword.");
				// player.sendMessage("You attach the hilt to the blade and make
				// a Saradomin godsword.");
				return;
			}
			if (usedItem.getId() == 11798 && withItem.getId() == 11816
					|| usedItem.getId() == 11816 && withItem.getId() == 11798) {// Zamorak
																				// Godsword
				player.getInventory().remove(usedItem);
				player.getInventory().remove(withItem);
				player.getInventory().add(new Item(11808));
				player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 11808, null,
						"You attach the hilt to the blade and make a Zamorak godsword.");
				// player.sendMessage("You attach the hilt to the blade and make
				// a Zamorak godsword.");
				return;
			}
			if (usedItem.getId() == 11818 && withItem.getId() == 11820
					|| usedItem.getId() == 11820 && withItem.getId() == 11818) {
				player.getInventory().remove(usedItem);
				player.getInventory().remove(withItem);
				player.getInventory().add(new Item(11794));
				player.sendMessage("You attach the shards together to make a shard 1 & 2");
				return;
			}
			if (usedItem.getId() == 13571 && withItem.getId() == 1931
					|| usedItem.getId() == 1931 && withItem.getId() == 13571) {
				player.getInventory().remove(usedItem);
				player.getInventory().remove(withItem);
				player.getInventory().removeItems(13421, 1931);
				player.getInventory().add(new Item(13572));
				player.sendMessage("You mix the items together to create a Dynamite Pot.");
				return;
			}
			if (usedItem.getId() == 1759 && withItem.getId() == 13572
					|| usedItem.getId() == 13572 && withItem.getId() == 1759) {
				player.getInventory().remove(usedItem);
				player.getInventory().remove(withItem);
				player.getInventory().add(new Item(13573));
				return;
			}
			if (usedItem.getId() == 11818 && withItem.getId() == 11822
					|| usedItem.getId() == 11822 && withItem.getId() == 11818) {
				player.getInventory().remove(usedItem);
				player.getInventory().remove(withItem);
				player.getInventory().add(new Item(11796));
				player.sendMessage("You attach the shards together to make a shard 1 & 3");
				return;
			}
			if (usedItem.getId() == 11820 && withItem.getId() == 11822
					|| usedItem.getId() == 11822 && withItem.getId() == 11820) {
				player.getInventory().remove(usedItem);
				player.getInventory().remove(withItem);
				player.getInventory().add(new Item(11800));
				player.sendMessage("You attach the shards together to make a shard 2 & 3");
				return;
			}
			if (usedItem.getId() == 11794 && withItem.getId() == 11822
					|| usedItem.getId() == 11822 && withItem.getId() == 11794) {
				player.getInventory().remove(usedItem);
				player.getInventory().remove(withItem);
				player.getInventory().add(new Item(11798));
				player.sendMessage("You attach the shards together to make a Godsword blade.");
				return;
			}
			if (usedItem.getId() == 21043 && withItem.getId() == 6914) {
				player.getInventory().remove(usedItem);
				player.getInventory().remove(withItem);
				player.getInventory().add(new Item(21006));
				player.sendMessage("You attach the insignia to the wand..");
				return;
			}
			if (usedItem.getId() == 11796 && withItem.getId() == 11820
					|| usedItem.getId() == 11820 && withItem.getId() == 11796) {
				player.getInventory().remove(usedItem);
				player.getInventory().remove(withItem);
				player.getInventory().add(new Item(11798));
				player.sendMessage("You attach the shards together to make a Godsword blade.");
				return;
			}
			if (usedItem.getId() == 11800 && withItem.getId() == 11818
					|| usedItem.getId() == 11818 && withItem.getId() == 11800) {
				player.getInventory().remove(usedItem);
				player.getInventory().remove(withItem);
				player.getInventory().add(new Item(11798));
				player.sendMessage("You attach the shards together to make a Godsword blade.");
				return;
			}
			if (usedItem.getId() == 1755 || withItem.getId() == 1755) {
				Item uncut = null;
				Item chisel = null;
				if (usedItem.getId() == 1755) {
					uncut = withItem;
					chisel = usedItem;
				} else {
					uncut = usedItem;
					chisel = withItem;
				}
				Gem gem = Gem.forId(uncut.getId());
				BoltTip tip = BoltTip.forId(uncut.getId());
				ZulrahCrafting.ZulrahItems zulrahItems = ZulrahCrafting.ZulrahItems.of(uncut.getId());
				if (gem != null) {
					if (player.getInventory().getCount(uncut.getId()) > 1) {
						player.getActionSender().sendItemOnInterface(309, 2, gem.getReward(), 130);
						String itemName = CacheItemDefinition.get(gem.getReward()).getName();
						player.getActionSender().sendString(309, 6, "<br><br><br><br>" + itemName);
						player.getActionSender().sendInterface(162, 546, 309, false);
						player.setInterfaceAttribute("gem_index", gem.getReward());
						player.setInterfaceAttribute("gem_type", gem);
					} else
						player.getActionQueue().addAction(new GemCutting(player, gem, 1));
				} else if (tip != null) {
					if (player.getInventory().getCount(uncut.getId()) > 1) {
						player.getActionSender().sendItemOnInterface(309, 2, tip.getReward().getId(), 130);
						String itemName = CacheItemDefinition.get(tip.getReward().getId()).getName();
						player.getActionSender().sendString(309, 6, "<br><br><br><br>" + itemName);
						player.getActionSender().sendInterface(162, 546, 309, false);
						player.setInterfaceAttribute("tip_index", tip.getReward().getId());
						player.setInterfaceAttribute("tip_type", tip);
					} else
						player.getActionQueue().addAction(new BoltCrafting(player, tip, 1));
				} else if (zulrahItems != null && zulrahItems.getRequiredItem() == chisel.getId())
					player.getActionQueue().addAction(new ZulrahCrafting(player, zulrahItems));
			}
			if (SuperCombatPotion.handleItemOnItem(player, usedItem, withItem))
				return;
			if (usedItem.getId() == 1391 || withItem.getId() == 1391) {
				Item other;
				if (usedItem.getId() == 1391) {
					other = withItem;
				} else {
					other = usedItem;
				}
				Optional<OrbChargingService.StaffType> typeOptional = OrbChargingService.StaffType.of(other.getId());
				if (typeOptional.isPresent()) {
					OrbChargingService.StaffType type = typeOptional.get();
					if (player.getInventory().getCount(1391) > 1
							&& player.getInventory().getCount(type.getOrbId()) > 1) {
						player.getActionSender().sendItemOnInterface(309, 2, type.getStaffId(), 130);
						String itemName = CacheItemDefinition.get(type.getStaffId()).getName();
						player.getActionSender().sendString(309, 6, "<br><br><br><br>" + itemName);
						player.getActionSender().sendInterface(162, 546, 309, false);
						player.setInterfaceAttribute("staff_type", type);
					} else {
						player.getActionQueue().addAction(new OrbChargingService.BattleStaffAction(player, type, 1));
					}
				}
			}
			if ((usedItem.getId() == 227 || usedItem.getId() == 5935)
					|| (withItem.getId() == 227 || withItem.getId() == 5935)) {
				Item primaryIngredient;
				Item vial;
				if (usedItem.getId() == 227 || usedItem.getId() == 5935) {
					primaryIngredient = withItem;
					vial = usedItem;
				} else {
					primaryIngredient = usedItem;
					vial = withItem;
				}
				PrimaryIngredient ingredient = PrimaryIngredient.forId(primaryIngredient.getId(), vial.getId());
				if (ingredient != null) {
					if (player.getInventory().getCount(ingredient.getVial().getId()) > 1
							&& player.getInventory().getCount(primaryIngredient.getId()) > 1) {
						player.getActionSender().sendItemOnInterface(309, 2, ingredient.getReward(), 130);
						String itemName = CacheItemDefinition.get(ingredient.getReward()).getName();
						if (vial.getId() == 227) {
							String leafName = CacheItemDefinition.get(ingredient.getId()).getName()
									.replaceAll(" leaf", "").replaceAll(" clean", "");
							itemName = leafName + " potion (unf)";
						}
						player.getActionSender().sendString(309, 6, "<br><br><br><br>" + itemName);
						player.getActionSender().sendInterface(162, 546, 309, false);
						player.setInterfaceAttribute("herblore_type", HerbloreType.PRIMARY_INGREDIENT);
						player.setInterfaceAttribute("herblore_index", ingredient.getId());
						player.setInterfaceAttribute("vial_index", vial.getId());
					} else {
						player.getActionQueue()
								.addAction(new Herblore(player, 1, ingredient, null, HerbloreType.PRIMARY_INGREDIENT));
					}
					return;
				}
			}
			SecondaryIngredient ingredient = null;
			for (SecondaryIngredient sIngredient : SecondaryIngredient.values()) {
				if (sIngredient.getId() == withItem.getId() && sIngredient.getRequiredItem().getId() == usedItem.getId()
						|| sIngredient.getId() == usedItem.getId()
								&& sIngredient.getRequiredItem().getId() == withItem.getId()) {
					ingredient = sIngredient;
				}
			}
			if (ingredient != null) {
				if (player.getInventory().getCount(ingredient.getId()) > 1
						&& player.getInventory().getCount(ingredient.getRequiredItem().getId()) > 1) {
					player.getActionSender().sendItemOnInterface(309, 2, ingredient.getReward(), 130);
					player.getActionSender().sendString(309, 6,
							"<br><br><br><br>" + CacheItemDefinition.get(ingredient.getReward()).getName());
					player.getActionSender().sendInterface(162, 546, 309, false);
					player.setInterfaceAttribute("herblore_type", HerbloreType.SECONDARY_INGREDIENT);
					player.setInterfaceAttribute("herblore_index", ingredient.getIndex());
				} else {
					player.getActionQueue()
							.addAction(new Herblore(player, 1, null, ingredient, HerbloreType.SECONDARY_INGREDIENT));
				}
				return;
			}

			Drink drink1 = Drink.forId(usedItem.getId());
			Drink drink2 = Drink.forId(withItem.getId());
			if (drink1 != null && drink2 != null) {
				if (drink1 != drink2) {
					player.getActionSender().sendMessage("You can't combine these two potions.");
					return;
				}
				int index1 = -1;
				int index2 = -1;
				for (int i = 0; i < drink1.getIds().length; i++) {
					if (drink1.getId(i) == usedItem.getId()) {
						index1 = i + 1;
						break;
					}
				}
				for (int i = 0; i < drink2.getIds().length; i++) {
					if (drink2.getId(i) == withItem.getId()) {
						index2 = i + 1;
						break;
					}
				}
				int doses = index1 + index2;
				int amount = 0;
				Item endPotion1 = null;
				Item endPotion2 = null;
				if (doses < 5) {
					endPotion1 = new Item(drink1.getId(doses - 1), 1);
					endPotion2 = new Item(229, 1);
					amount = doses;
				} else {
					endPotion1 = new Item(drink1.getId(3), 1);
					amount = 4;
					doses -= 4;
					endPotion2 = new Item(drink1.getId(doses - 1), 1);
				}
				player.getInventory().remove(usedItem);
				player.getInventory().remove(withItem);
				player.getInventory().add(endPotion1, usedWithSlot);
				player.getInventory().add(endPotion2, slot);
				player.getActionSender().sendMessage("You have combined the liquid into " + amount + " doses.");
				return;
			}
			break;
		}
	}

	@SuppressWarnings("unused")
	private void handleMagicOnItem(Player player, Packet packet) {
		int interfaceHash = packet.getBEInt();
		int interfaceId = interfaceHash >> 16;
		int childId = interfaceHash & 0xFFFF;
		int b = packet.getShort();
		int slot = packet.getLEShort();
		int itemId = packet.getShort();
		int spellHash = packet.getBEInt();
		int spellBook = spellHash >> 16;
		int spellId = spellHash & 0xFFFF;

		Item item = player.getInventory().get(slot);
		if (item == null)
			return;
		Magic magic = new Magic(player);
		magic.handleMagicOnItem(item, spellId - 3, slot);
	}

	/**
	 * The logger instance.
	 */
	private static final Logger logger = LoggerFactory.getLogger(ItemOptionPacketHandler.class);

	/**
	 * Option drop/destroy opcode.
	 */
	private static final int OPTION_DROP_DESTROY = 183;

	/**
	 * Option pickup opcode.
	 */
	private static final int OPTION_PICKUP = 5;

	/**
	 * Option examine opcode.
	 */
	private static final int OPTION_EXAMINE = 116;

	/**
	 * Option 1 opcode.
	 */
	private static final int OPTION_1 = 149;

	/**
	 * Option 2 opcode.
	 */
	private static final int OPTION_2 = 72;

	/**
	 * Option 3 opcode.
	 */
	private static final int OPTION_3 = 159;

	/**
	 * Option 4 opcode.
	 */
	private static final int OPTION_4 = 73;

	/**
	 * Option 5 opcode.
	 */
	private static final int OPTION_5 = 46;

	/**
	 * Click 1 opcode.
	 */
	private static final int CLICK_1 = 198;

	/**
	 * Item on item opcode.
	 */
	private static final int ITEM_ON_ITEM = 176;

	/**
	 * Magic on item opcode.
	 */
	private static final int MAGIC_ON_ITEM = 44;

	/**
	 * Magic on grund item
	 */
	private static final int MAGIC_ON_GROUND_ITEM = 74;

	private final SlayerService slayerService;
	private final PlayerService playerService;
	private final HookService hookService;
	private final ItemService itemService;
	private final PathfindingService pathfindingService;
	private final LootingBagService lootingBagService;
	private final GroundItemService groundItemService;

	public ItemOptionPacketHandler() {
		slayerService = Server.getInjector().getInstance(SlayerService.class);
		playerService = Server.getInjector().getInstance(PlayerService.class);
		hookService = Server.getInjector().getInstance(HookService.class);
		itemService = Server.getInjector().getInstance(ItemService.class);
		pathfindingService = Server.getInjector().getInstance(PathfindingService.class);
		lootingBagService = Server.getInjector().getInstance(LootingBagService.class);
		groundItemService = Server.getInjector().getInstance(GroundItemService.class);
	}

	@Override
	public void handle(Player player, Packet packet) {
		if (player.getAttribute("cutScene") != null || player.getAttribute("busy") != null)
			return;
		if (player.isLighting())
			return;

		boolean starter = player.getAttribute("starter");
		if (starter) {
			DialogueManager.openDialogue(player, 19000);
			return;
		}
		player.resetAfkTolerance();
		switch (packet.getOpcode()) {
		case OPTION_DROP_DESTROY:
			handleItemOptionDrop(player, packet);
			break;
		case OPTION_PICKUP:
			handleItemOptionPickup(player, packet);
			break;
		case OPTION_EXAMINE:
			handleItemOptionExamine(player, packet);
			break;
		case OPTION_1:
			handleItemOption1(player, packet);
			break;
		case OPTION_2:
			handleItemOption2(player, packet);
			break;
		case OPTION_3:
			handleItemOption3(player, packet);
			break;
		case OPTION_4:
			handleItemOption4(player, packet);
			break;
		case OPTION_5:
			handleItemOption5(player, packet);
			break;
		case CLICK_1:
			handleItemOptionClick1(player, packet);
			break;
		case ITEM_ON_ITEM:
			handleItemOptionItem(player, packet);
			break;
		case MAGIC_ON_ITEM:
			handleMagicOnItem(player, packet);
			break;
		case MAGIC_ON_GROUND_ITEM:
			handleMagicOnGroundItem(player, packet);
			break;
		}
	}

	private void handleMagicOnGroundItem(Player player, Packet packet) {
		packet.getByteC();
		int interfaceHash = packet.getInt();
		int interfaceId = interfaceHash >> 16;
		int child = interfaceHash & 0xFFFF;
		@SuppressWarnings("unused")
		int c = packet.getLEShort();
		int x = packet.getLEShort();
		int y = packet.getShort();
		int itemId = packet.getShortA();
		Location location = Location.create(x, y, player.getPlane());
		if (interfaceId == 218 && child == 20 && player.getCombatState()
				.getSpellBook() == MagicCombatAction.SpellBook.MODERN_MAGICS.getSpellBookId()) {
			Action action = new Action(player, 0) {
				@Override
				public CancelPolicy getCancelPolicy() {
					return CancelPolicy.ONLY_ON_WALK;
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
					Optional<GroundItemService.GroundItem> groundItemOptional = groundItemService.getGroundItem(itemId,
							location);
					if (!groundItemOptional.isPresent()) {
						this.stop();
						return;
					}
					GroundItemService.GroundItem groundItem = groundItemOptional.get();
					if (player.getLocation().distance(groundItem.getLocation()) > 6
							|| !ProjectilePathFinder.clearPath(player.getLocation(), groundItem.getLocation())) {
						pathfindingService.travel(player, groundItem.getLocation());
						return;
					}
					player.getWalkingQueue().reset();
					Magic magic = new Magic(player);
					magic.handleTeleGrab(groundItem);
					this.stop();
				}
			};
			player.getActionQueue().addAction(action);
		}
	}

	/**
	 * Handles item option drop.
	 *
	 * @param player
	 *            The player.
	 * @param packet
	 *            The packet.
	 */
	private void handleItemOptionDrop(Player player, Packet packet) {
		int id = packet.getLEShort();
		int slot = packet.getLEShort();
		int interfaceHash = packet.getInt();
		int interfaceId = interfaceHash >> 16;
		if (player.getCombatState().isDead()
				|| player.getInterfaceState().isInterfaceOpen(Bank.BANK_INVENTORY_INTERFACE))
			return;

		player.getActionQueue().clearAllActions();
		player.getActionManager().stopAction();

		if (Constants.DEBUG)
			player.sendMessage("ItemDrop: itemID: " + id);

		player.getActionSender().removeAllInterfaces().removeInterface2();
		switch (interfaceId) {
		case Inventory.INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				this.handleItemOptionDrop(player, id, slot);
				// player.getActionSender().playSound(Sound.DROP); crashes
				// client TODO fix sound system
			}
			break;
		default:
			logger.info("Unhandled item drop option : " + interfaceId + " - " + id + " - " + slot);
			break;
		}
	}

	/**
	 * Handles item option pickup.
	 *
	 * @param player
	 *            The player.
	 * @param packet
	 *            The packet.
	 */
	private void handleItemOptionPickup(final Player player, Packet packet) {
		int id = packet.getShortA();
		int x = packet.getLEShortA();
		packet.get();
		int y = packet.getShort();
		if (player.getCombatState().isDead()) {
			logger.info("Dead player {} cannot pickup ground item.", player.getName());
			return;
		}
		player.getActionQueue().clearAllActions();
		player.getActionManager().stopAction();

		final Location location = Location.create(x, y, player.getLocation().getPlane());
		player.resetInteractingEntity();
		Item item = new Item(id);
		if (!player.getLocation().equals(location))
			pathfindingService.travel(player, location);
		GroundItemService groundItemService = Server.getInjector().getInstance(GroundItemService.class);
		groundItemService.pickupGroundItem(player, item, location);
	}

	/**
	 * Handles item dropping.
	 * 
	 * @param player
	 *            the player
	 * @param id
	 *            the item id
	 * @param slot
	 *            the item slot
	 */
	private void handleItemOptionDrop(final Player player, int id, int slot) {
		Item item = player.getInventory().get(slot);
		if (item == null)
			return;
		if (item != null && item.getId() != id)
			return;
		if (player.getBountyHunter() != null) {
			if (item.getPrice() >= 1000) {
				player.getActionSender().sendMessage("You can't drop an item with a value of 1,000 coins or more.");
				return;
			}
		}

		// Clue scrolls should be destroyed, not dropped.
		for (ClueScrollType clueScroll : ClueScrollType.values()) {
			if (id == clueScroll.getClueScrollItemId()) {
				player.getActionSender().sendDestroyItem(item);
				return;
			}
		}

		if (item.getId() == 12926) {
			player.getActionSender().sendMessage("Please empty this item before dropping it.");
			return;
		}
		if (item.getId() == 12019 && player.getDatabaseEntity().getCoalBagAmount() > 0) {
			player.getActionSender().sendMessage("Please empty your Coal bag before dropping it.");
			return;
		}
		if (item.getId() == 12020 && player.getDatabaseEntity().getGemBag().size() > 0) {
			player.getActionSender().sendMessage("Please empty your Gem bag before dropping it.");
			return;
		}
		if (item.getId() == 12791 && player.getRunePouch().size() > 0) {
			player.getActionSender().sendMessage("Please empty your pouch before dropping this item.");
			return;
		}
		// Upgraded steam battlestaff
		if (item.getId() == 12795) {
			player.getInventory().remove(item);
			Inventory.addDroppable(player, new Item(11787));
			Inventory.addDroppable(player, new Item(12798));
			return;
		}
		// Upgraded dragon pickaxe
		if (item.getId() == 12797) {
			player.getInventory().remove(item);
			Inventory.addDroppable(player, new Item(11920));
			Inventory.addDroppable(player, new Item(12800));
			return;
		}
		// Upgraded odium ward
		if (item.getId() == 12807) {
			player.getInventory().remove(item);
			Inventory.addDroppable(player, new Item(11926));
			Inventory.addDroppable(player, new Item(12802));
			return;
		}
		// Upgraded malediction ward
		if (item.getId() == 12806) {
			player.getInventory().remove(item);
			Inventory.addDroppable(player, new Item(11924));
			Inventory.addDroppable(player, new Item(12802));
			return;
		}
		// Upgraded granite maul
		if (item.getId() == 12848) {
			player.getInventory().remove(item);
			Inventory.addDroppable(player, new Item(4153));
			Inventory.addDroppable(player, new Item(12802));
			return;
		}
		if (item.getId() == 13197 || item.getId() == 13199) {
			int charges = itemService.getCharges(player, item);
			if (charges < 1) {
				player.getActionSender().sendMessage("Your " + item.getDefinition2().name + " is empty.");
				return;
			}
			if (charges > 0) {
				CacheItemDefinition def = CacheItemDefinition.get(itemService.getChargedItem(player, item));
				if (def != null) {
					Item chargedItem = new Item(def.getId(), charges);
					if (player.getInventory().add(chargedItem)) {
						itemService.setChargesWithItem(player, item, chargedItem, -1);
						itemService.setCharges(player, item, -1);
						itemService.degradeItem(player, item);
						player.getActionSender().sendMessage(
								"You unload " + charges + " x " + def.name + " from the " + item.getDefinition2().name);
					}
				}
			}
			return;
		}

		for (int destroyableId : Constants.DESTROYABLE_ITEMS) {
			if (destroyableId == id) {
				player.getActionSender().sendDestroyItem(item);
				return;
			}
		}

		if (item.getId() == 13196) {
			DialogueManager.openDialogue(player, 12929);
			return;
		}
		if (item.getId() == 12006) { // abyssal tentacle
			DialogueManager.openDialogue(player, 6497);
			return;
		}
		if (item.getId() == 13198) {
			DialogueManager.openDialogue(player, 12932);
			return;
		}
		if (item.getId() == 12436) {
			if (player.getInventory().freeSlots() >= 3) {
				if (player.getInventory().add(new Item(6585)) && player.getInventory().add(new Item(12526)))
					player.getInventory().remove(new Item(12436));
			} else
				player.getActionSender().sendMessage("Not enough inventory space to do this.");
			return;
		}
		if (item.getId() == 11941 && player.getLootingBag().size() > 0) {
			player.getActionSender().sendMessage("Please empty the Looting bag before dropping it.");
			return;
		}

		MaxCapeService maxCapeService = Server.getInjector().getInstance(MaxCapeService.class);
		if (maxCapeService.destroyMaxCape(player, item))
			return;

		Pet.Pets petIds = Pet.Pets.from(item.getId());
		PlayerSettingsEntity settings = player.getDatabaseEntity().getPlayerSettings();
		if (petIds != null) {
			if (player.getPet() != null) {
				player.getActionSender().sendMessage("You already have a follower.");
				return;
			} else {
				player.playAnimation(Animation.create(827));
				Pet pet = new Pet(player, petIds.getNpc());
				settings.setPetId(petIds.getNpc());
				settings.setPetSpawned(true);

				player.setPet(pet);
				World.getWorld().register(pet);
				player.getInventory().remove(item);
				return;
			}
		}
		if (item.getId() == 12931 || item.getId() == 12904 || item.getId() == 12899) {
			int charges = itemService.getCharges(player, item);
			if (charges < 1) {
				player.getActionSender().sendMessage("Your " + item.getDefinition2().name + " is empty.");
				return;
			}
			if (charges > 0) {
				CacheItemDefinition def = CacheItemDefinition.get(itemService.getChargedItem(player, item));
				if (def != null) {
					Item chargedItem = new Item(def.getId(), charges);
					if (player.getInventory().add(chargedItem)) {
						itemService.setChargesWithItem(player, item, chargedItem, -1);
						itemService.setCharges(player, item, -1);
						itemService.degradeItem(player, item);
						player.getActionSender().sendMessage(
								"You unload " + charges + " x " + def.name + " from the " + item.getDefinition2().name);
					}
				}
			}
			return;
		}

		player.getInventory().remove(item, slot);
		GroundItemService groundItemService = Server.getInjector().getInstance(GroundItemService.class);
		GroundItemService.GroundItem groundItem = new GroundItemService.GroundItem(item, player.getLocation(), player,
				false);
		groundItemService.createGroundItem(player, groundItem);
		hookService.post(new GamePlayerItemDropEvent(player, groundItem));
	}

}
