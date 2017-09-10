package org.rs2server.rs2.action.impl;

import org.rs2server.rs2.action.Action;
import org.rs2server.rs2.domain.service.api.PermissionService.PlayerPermissions;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.container.Equipment.EquipmentType;
import org.rs2server.rs2.model.equipment.EquipmentDefinition.Skill;
import org.rs2server.rs2.model.player.Player;

public class WieldItemAction extends Action {

	/**
	 * The item's id.
	 */
	private int id;

	/**
	 * The item's slot.
	 */
	private int slot;

	public WieldItemAction(Mob mob, int id, int slot, int ticks) {
		super(mob, ticks);
		this.id = id;
		this.slot = slot;
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

	@Override
	public void execute() {
		this.stop();
		Item item = getMob().getInventory() != null ? getMob().getInventory().get(slot) : null;
		if (item == null || item.getId() != id)
			return;
		if (!getMob().canEmote())
			return;
		if (item.getEquipmentDefinition() == null || item.getEquipmentDefinition().getType() == null) {
			if (getMob().getActionSender() != null)
				getMob().getActionSender()
						.sendMessage("You can't wear this item ["+item.getId()+"]; please report it to an administrator!");
			return;
		}
		EquipmentType type = Equipment.getType(item);
		if (item.getEquipmentDefinition() != null && item.getEquipmentDefinition().getSkillRequirements() != null) {
			for (Skill skill : item.getEquipmentDefinition().getSkillRequirements().keySet()) {
				if (getMob().getSkills().getLevelForExperience(skill.getId()) < item.getEquipmentDefinition()
						.getSkillRequirement(skill.getId())) {
					if (getMob().getActionSender() != null) {
						String level = "a ";
						if (Skills.SKILL_NAME[skill.getId()].toLowerCase().startsWith("a")) {
							level = "an ";
						}
						level += Skills.SKILL_NAME[skill.getId()].toLowerCase();
						getMob().getActionSender().sendMessage("You need to have " + level + " level of "
								+ item.getEquipmentDefinition().getSkillRequirement(skill.getId()) + ".");
					}
					return;
				}
			}
		}
		if (getMob().isPlayer()) {
			Player player = null;
			player = (Player) getMob();
			if (player != null) {
				if (!player.getPermissionService().is(player, PlayerPermissions.IRON_MAN)) {
					if (item.getId() >= 12810 && item.getId() <= 12812) {
						player.sendMessage("You can not equip this item as you're not an ironman.");
						return;
					}
				}
				if (!player.getPermissionService().is(player, PlayerPermissions.ULTIMATE_IRON_MAN)) {
					if (item.getId() >= 12813 && item.getId() <= 12815) {
						player.sendMessage("You can not equip this item as you're not an ultimate ironman.");
						return;
					}
				}
				if (!player.getPermissionService().is(player, PlayerPermissions.HARDCORE_IRON_MAN)) {
					if (item.getId() >= 20792 && item.getId() <= 20796) {
						player.sendMessage("You can not equip this item as you're not a hardcore ironman.");
						return;
					}
				}
				if (item.getId() >= 9810 && item.getId() <= 9812) {
					if (player.getSkills().getLevelForExperience(Skills.FARMING) < 99) {
						player.sendMessage("You need a level of 99 Farming to equip this.");
						return;
					}
				}
			}
		}
		long itemCount = item.getCount();
		long equipCount = getMob().getEquipment().getCount(item.getId());
		long totalCount = (itemCount + equipCount);
		if (totalCount > Integer.MAX_VALUE) {
			getMob().getActionSender().sendMessage("Not enough equipment space.");
			return;
		}
		boolean inventoryFiringEvents = getMob().getInventory().isFiringEvents();
		getMob().getInventory().setFiringEvents(false);
		try {
			if (type.getSlot() == 3 || type.getSlot() == 5) {
				if (type == EquipmentType.WEAPON_2H) {
					if (getMob().getEquipment().get(Equipment.SLOT_WEAPON) != null
							&& getMob().getEquipment().get(Equipment.SLOT_SHIELD) != null) {
						if (getMob().getInventory().freeSlots() < 1) {
							if (getMob().getActionSender() != null) {
								getMob().getActionSender().sendMessage("Not enough space in your inventory.");
							}
							return;
						}
						getMob().getInventory().remove(item, slot);
						if (getMob().getEquipment().get(Equipment.SLOT_WEAPON) != null) {
							getMob().getInventory().add(getMob().getEquipment().get(Equipment.SLOT_WEAPON), slot);
							getMob().getEquipment().set(Equipment.SLOT_WEAPON, null);
						}
						if (getMob().getEquipment().get(Equipment.SLOT_SHIELD) != null) {
							getMob().getInventory().add(getMob().getEquipment().get(Equipment.SLOT_SHIELD), slot - 1);
							getMob().getEquipment().set(Equipment.SLOT_SHIELD, null);
						}
						getMob().getEquipment().set(type.getSlot(), item);
						getMob().getCombatState().calculateBonuses();
						if (getMob().getActionSender() != null) {
							getMob().getActionSender().sendBonuses();
						}
						getMob().getInventory().fireItemsChanged();
						return;
					} else if (getMob().getEquipment().get(Equipment.SLOT_SHIELD) != null
							&& getMob().getEquipment().get(Equipment.SLOT_WEAPON) == null) {
						getMob().getInventory().remove(item, slot);
						getMob().getEquipment().set(Equipment.SLOT_WEAPON, item);
						getMob().getInventory().add(getMob().getEquipment().get(Equipment.SLOT_SHIELD), slot);
						getMob().getEquipment().set(Equipment.SLOT_SHIELD, null);
						getMob().getCombatState().calculateBonuses();
						if (getMob().getActionSender() != null) {
							getMob().getActionSender().sendBonuses();
						}
						getMob().getInventory().fireItemsChanged();
						return;
					}
				}
				if (type.getSlot() == Equipment.SLOT_SHIELD
						&& getMob().getEquipment().get(Equipment.SLOT_WEAPON) != null
						&& getMob().getEquipment().get(Equipment.SLOT_WEAPON).getEquipmentDefinition() != null
						&& getMob().getEquipment().get(Equipment.SLOT_WEAPON).getEquipmentDefinition()
								.getType() == EquipmentType.WEAPON_2H) {
					getMob().getInventory().remove(item, slot);
					getMob().getInventory().add(getMob().getEquipment().get(Equipment.SLOT_WEAPON), slot);
					getMob().getEquipment().set(Equipment.SLOT_WEAPON, null);
					getMob().getEquipment().set(Equipment.SLOT_SHIELD, item);
					getMob().getCombatState().calculateBonuses();
					if (getMob().getActionSender() != null) {
						getMob().getActionSender().sendBonuses();
					}
					getMob().getInventory().fireItemsChanged();
					return;
				}
			}
			getMob().getInventory().remove(item, slot);
			if (getMob().getEquipment().get(type.getSlot()) != null) {
				if (getMob().getEquipment().get(type.getSlot()).getId() == item.getId()
						&& item.getDefinition().isStackable()) {
					item = new Item(item.getId(),
							getMob().getEquipment().get(type.getSlot()).getCount() + item.getCount());
				} else {
					if (getMob().getEquipment().get(type.getSlot()).getEquipmentDefinition() != null) {
						for (int i = 0; i < getMob().getEquipment().get(type.getSlot()).getEquipmentDefinition()
								.getBonuses().length; i++) {
							getMob().getCombatState().setBonus(i, getMob().getCombatState().getBonus(i)
									- getMob().getEquipment().get(type.getSlot()).getEquipmentDefinition().getBonus(i));
						}
					}
					getMob().getInventory().add(getMob().getEquipment().get(type.getSlot()), slot);
				}
			}
			getMob().getEquipment().set(type.getSlot(), item);
			if (item.getEquipmentDefinition() != null) {
				for (int i = 0; i < item.getEquipmentDefinition().getBonuses().length; i++) {
					getMob().getCombatState().setBonus(i,
							getMob().getCombatState().getBonus(i) + item.getEquipmentDefinition().getBonus(i));
				}
			}
			if (getMob().getActionSender() != null)
				getMob().getActionSender().sendBonuses();

			// if (!((Player)getMob()).hasQueuedSwitching()) {
			getMob().getInventory().fireItemsChanged();
			// }
			if (item.getId() != 4153 && item.getId() != 12848) // Reset granite maul
				getMob().resetInteractingEntity();
		} finally {
			getMob().getInventory().setFiringEvents(inventoryFiringEvents);
		}
	}
}