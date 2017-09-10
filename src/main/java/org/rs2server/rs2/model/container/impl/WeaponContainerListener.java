package org.rs2server.rs2.model.container.impl;

import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.combat.CombatState.AttackType;
import org.rs2server.rs2.model.combat.CombatState.CombatStyle;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction;
import org.rs2server.rs2.model.container.Container;
import org.rs2server.rs2.model.container.ContainerListener;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.player.Player;

/**
 * A listener which updates the weapon tab.
 * 
 * @author Graham Edgecombe
 * 
 */
public class WeaponContainerListener implements ContainerListener {

	/**
	 * The player.
	 */
	private Player player;

	/**
	 * Creates the listener.
	 * 
	 * @param player
	 *            The player.
	 */
	public WeaponContainerListener(Player player) {
		this.player = player;
	}

	@Override
	public void itemChanged(Container container, int slot) {
		if (slot == Equipment.SLOT_WEAPON) {
			sendWeapon();
		}
	}

	@Override
	public void itemsChanged(Container container, int[] slots) {
		for (int slot : slots) {
			if (slot == Equipment.SLOT_WEAPON) {
				sendWeapon();
				return;
			}
		}
	}

	@Override
	public void itemsChanged(Container container) {
		sendWeapon();
	}

	/**
	 * Sends weapon information.
	 */
	private void sendWeapon() {
		Item weapon = player.getEquipment().get(Equipment.SLOT_WEAPON);
		if (weapon != null && weapon.getEquipmentDefinition() != null) {
			player.setStandAnimation(weapon.getEquipmentDefinition().getAnimation().getStand());
			player.setRunAnimation(weapon.getEquipmentDefinition().getAnimation().getRun());
			player.setWalkAnimation(weapon.getEquipmentDefinition().getAnimation().getWalk());
			player.setStandTurnAnimation(weapon.getEquipmentDefinition().getAnimation().getStandTurn());
			player.setTurn180Animation(weapon.getEquipmentDefinition().getAnimation().getTurn180());
			player.setTurn90ClockwiseAnimation(weapon.getEquipmentDefinition().getAnimation().getTurn90ClockWise());
			player.setTurn90CounterClockwiseAnimation(
					weapon.getEquipmentDefinition().getAnimation().getTurn90CounterClockWise());
		} else {
			player.setDefaultAnimations();
		}
		int id = -1;
		String name = null;
		if (weapon == null) {
			name = "Unarmed";
		} else {
			name = weapon.getDefinition2().name;
			id = weapon.getId();
		}
		String genericName = filterWeaponName(name).trim();
		sendWeapon(id, name, name.toLowerCase(), genericName.toLowerCase());
	}

	/**
	 * Sends weapon information.
	 * 
	 * @param id
	 *            The id.
	 * @param name
	 *            The name.
	 * @param genericName
	 *            The filtered name.
	 */
	@SuppressWarnings("incomplete-switch")
	private void sendWeapon(int id, String originalName, String name, String genericName) {
		if (name.equals("unarmed")) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				player.getActionSender().sendConfig(43, 0);
				break;
			case AGGRESSIVE_1:
			case CONTROLLED_2:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				player.getActionSender().sendConfig(43, 1);
				break;
			case AGGRESSIVE_2:
			case DEFENSIVE:
			case CONTROLLED_3:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				player.getActionSender().sendConfig(43, 3);
				break;
			}
			// player.getActionSender().sendSidebarInterface(92, 128);
			player.getActionSender().sendString(593, 1, originalName);
			player.getActionSender().sendConfig(843, 0);
		} else if (name.endsWith("whip") || name.contains("mouse") || name.endsWith("tentacle")) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				player.getActionSender().sendConfig(43, 0);
				break;
			case AGGRESSIVE_1:
			case CONTROLLED_2:
			case CONTROLLED_3:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.CONTROLLED_2);
				player.getActionSender().sendConfig(43, 1);
				break;
			case AGGRESSIVE_2:
			case DEFENSIVE:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				player.getActionSender().sendConfig(43, 3);
				break;
			}
			// player.getActionSender().sendSidebarInterface(93, 128);
			player.getActionSender().sendString(593, 1, originalName);
			// player.getActionSender().sendInterfaceConfig(93, 10, true);
			player.getActionSender().sendConfig(843, 20);
		} else if (name.endsWith("scythe")) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				player.getActionSender().sendConfig(43, 0);
				break;
			case AGGRESSIVE_1:
			case CONTROLLED_2:
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				player.getActionSender().sendConfig(43, 1);
				break;
			case AGGRESSIVE_2:
			case CONTROLLED_3:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_2);
				player.getActionSender().sendConfig(43, 2);
				break;
			case DEFENSIVE:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				player.getActionSender().sendConfig(43, 3);
				break;
			}
			// player.getActionSender().sendSidebarInterface(86, 128);
			player.getActionSender().sendString(593, 1, originalName);
		} else if ((name.contains("bow") || name.equals("seercull")) && !name.contains("karil")
				&& !name.contains("c'bow")) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
				player.getCombatState().setAttackType(AttackType.RANGE);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				player.getActionSender().sendConfig(43, 0);
				break;
			case AGGRESSIVE_1:
			case CONTROLLED_2:
				player.getCombatState().setAttackType(AttackType.RANGE);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				player.getActionSender().sendConfig(43, 1);
				break;
			case AGGRESSIVE_2:
			case DEFENSIVE:
			case CONTROLLED_3:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.RANGE);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				player.getActionSender().sendConfig(43, 3);
				break;
			}
			// player.getActionSender().sendSidebarInterface(77, 128);
			player.getActionSender().sendString(593, 1, originalName);
			player.getActionSender().sendConfig(843, 3);
			// player.getActionSender().sendInterfaceConfig(77, 10, true);
			player.getActionSender().updateSpecialConfig();
		} else if (name.contains("karil") || name.contains("c'bow") || name.contains("cross")
				|| genericName.endsWith("ballista")) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
				player.getCombatState().setAttackType(AttackType.RANGE);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				player.getActionSender().sendConfig(43, 0);
				break;
			case AGGRESSIVE_1:
			case CONTROLLED_2:
				player.getCombatState().setAttackType(AttackType.RANGE);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				player.getActionSender().sendConfig(43, 1);
				break;
			case AGGRESSIVE_2:
			case DEFENSIVE:
			case CONTROLLED_3:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.RANGE);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				player.getActionSender().sendConfig(43, 3);
				break;
			}
			// player.getActionSender().sendSidebarInterface(79, 128);
			player.getActionSender().sendString(593, 1, originalName);

			player.getActionSender().sendConfig(843, 5);
			// player.getActionSender().sendInterfaceConfig(79, 10, true);
		} else if (genericName.contains("blade") || genericName.contains("scimitar") || genericName.contains("butterfly net") || name.equals("excalibur")
				|| name.equals("katana") || name.endsWith("light") || (genericName.contains("sword")
						&& !name.contains("2h") && !name.contains("god") && !name.contains("saradomin"))) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				player.getActionSender().sendConfig(43, 0);
				break;
			case AGGRESSIVE_1:
			case CONTROLLED_2:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				player.getActionSender().sendConfig(43, 1);
				break;
			case AGGRESSIVE_2:
			case CONTROLLED_3:
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.CONTROLLED_1);
				player.getActionSender().sendConfig(43, 2);
				break;
			case DEFENSIVE:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				player.getActionSender().sendConfig(43, 3);
				break;
			}
			// player.getActionSender().sendSidebarInterface(81, 128);
			player.getActionSender().sendString(593, 1, originalName);
			player.getActionSender().sendConfig(843, 9);
			// player.getActionSender().sendInterfaceConfig(81, 12, true);
		} else if (name.contains("staff") || name.contains("wand")) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				player.getActionSender().sendConfig(43, 0);
				break;
			case AGGRESSIVE_1:
			case CONTROLLED_2:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				player.getActionSender().sendConfig(43, 1);
				break;
			case AGGRESSIVE_2:
			case DEFENSIVE:
			case CONTROLLED_3:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				player.getActionSender().sendConfig(43, 3);
				break;
			}
			// player.getActionSender().sendSidebarInterface(90, 128);
			player.getActionSender().sendString(593, 1, originalName);
			player.getActionSender().sendConfig(843, 18);
			player.getActionSender().sendString(593, 3, "Combat lvl: " + player.getSkills().getCombatLevel());
		} else if (genericName.startsWith("dart") || genericName.endsWith("knife") || genericName.endsWith("thrownaxe")
				|| name.equals("toktz-xil-ul") || name.equals("toxic blowpipe")) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
				player.getCombatState().setAttackType(AttackType.RANGE);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				player.getActionSender().sendConfig(43, 0);
				break;
			case AGGRESSIVE_1:
			case CONTROLLED_2:
				player.getCombatState().setAttackType(AttackType.RANGE);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				player.getActionSender().sendConfig(43, 1);
				break;
			case AGGRESSIVE_2:
			case DEFENSIVE:
			case CONTROLLED_3:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.RANGE);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				player.getActionSender().sendConfig(43, 3);
				break;
			}
			// player.getActionSender().sendSidebarInterface(91, 128);
			player.getActionSender().sendString(593, 1, originalName);
			player.getActionSender().sendConfig(843, 19);
		} else if (genericName.contains("trident of the")) {
			player.getCombatState().setAttackType(AttackType.MAGIC);
			player.getActionSender().sendString(593, 1, originalName);
			player.getActionSender().sendConfig(843, 19);
		} else if (genericName.contains("mace") || name.endsWith("flail") || name.endsWith("anchor")) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				player.getActionSender().sendConfig(43, 0);
				break;
			case AGGRESSIVE_1:
			case CONTROLLED_2:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				player.getActionSender().sendConfig(43, 1);
				break;
			case AGGRESSIVE_2:
			case CONTROLLED_3:
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_2);
				player.getActionSender().sendConfig(43, 2);
				break;
			case DEFENSIVE:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				player.getActionSender().sendConfig(43, 3);
				break;
			}
			// player.getActionSender().sendSidebarInterface(88, 128);
			player.getActionSender().sendConfig(843, 16);
			player.getActionSender().sendString(593, 1, originalName);
		} else if (genericName.startsWith("dagger") || name.contains("abyssal dagger")) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				player.getActionSender().sendConfig(43, 0);
				break;
			case AGGRESSIVE_1:
			case CONTROLLED_2:
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				player.getActionSender().sendConfig(43, 1);
				break;
			case AGGRESSIVE_2:
			case CONTROLLED_3:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_2);
				player.getActionSender().sendConfig(43, 2);
				break;
			case DEFENSIVE:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				player.getActionSender().sendConfig(43, 3);
				break;
			}
			// player.getActionSender().sendSidebarInterface(89, 128);
			player.getActionSender().sendString(593, 1, originalName);
			player.getActionSender().sendConfig(843, 17);
		} else if (genericName.startsWith("pickaxe")) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				player.getActionSender().sendConfig(43, 0);
				break;
			case AGGRESSIVE_1:
			case CONTROLLED_2:
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				player.getActionSender().sendConfig(43, 1);
				break;
			case AGGRESSIVE_2:
			case CONTROLLED_3:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_2);
				player.getActionSender().sendConfig(43, 2);
				break;
			case DEFENSIVE:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				player.getActionSender().sendConfig(43, 3);
				break;
			}
			// player.getActionSender().sendSidebarInterface(83, 128);
			player.getActionSender().sendString(593, 1, originalName);
			player.getActionSender().sendConfig(843, 11);
		} else if (genericName.startsWith("maul") || genericName.endsWith("warhammer") || name.endsWith("hammers")
				|| name.equalsIgnoreCase("tzhaar-ket-om") || name.equalsIgnoreCase("elder maul")) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				player.getActionSender().sendConfig(43, 0);
				break;
			case AGGRESSIVE_1:
			case CONTROLLED_2:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				player.getActionSender().sendConfig(43, 1);
				break;
			case AGGRESSIVE_2:
			case DEFENSIVE:
			case CONTROLLED_3:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				player.getActionSender().sendConfig(43, 3);
				break;
			}
			// player.getActionSender().sendSidebarInterface(76, 128);
			player.getActionSender().sendString(593, 1, originalName);
			player.getActionSender().sendConfig(843, 2);
		} else if (name.equals("abyssal bludgeon")) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				player.getActionSender().sendConfig(43, 0);
				break;
			case AGGRESSIVE_1:
			case CONTROLLED_2:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				player.getActionSender().sendConfig(43, 1);
				break;
			case AGGRESSIVE_2:
			case DEFENSIVE:
			case CONTROLLED_3:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				player.getActionSender().sendConfig(43, 3);
				break;
			}
			player.getActionSender().sendString(593, 1, originalName);
			player.getActionSender().sendConfig(843, 2);

		} else if (name.contains("2h") || name.contains("godsword") || name.equals("saradomin sword")) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				player.getActionSender().sendConfig(43, 0);
				break;
			case AGGRESSIVE_1:
			case CONTROLLED_2:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				player.getActionSender().sendConfig(43, 1);
				break;
			case AGGRESSIVE_2:
			case CONTROLLED_3:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_2);
				player.getActionSender().sendConfig(43, 2);
				break;
			case DEFENSIVE:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				player.getActionSender().sendConfig(43, 3);
				break;
			}
			// player.getActionSender().sendSidebarInterface(82, 128);
			player.getActionSender().sendString(593, 1, originalName);
			player.getActionSender().sendConfig(843, 10);
		} else if (genericName.contains("axe") || genericName.contains("battleaxe")) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				player.getActionSender().sendConfig(43, 0);
				break;
			case AGGRESSIVE_1:
			case CONTROLLED_2:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				player.getActionSender().sendConfig(43, 1);
				break;
			case AGGRESSIVE_2:
			case CONTROLLED_3:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_2);
				player.getActionSender().sendConfig(43, 2);
				break;
			case DEFENSIVE:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				player.getActionSender().sendConfig(43, 3);
				break;
			}
			// player.getActionSender().sendSidebarInterface(75, 128);
			player.getActionSender().sendString(593, 1, originalName);
			player.getActionSender().sendConfig(843, 1);
		} else if (genericName.contains("claws")) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				player.getActionSender().sendConfig(43, 0);
				break;
			case AGGRESSIVE_1:
			case CONTROLLED_2:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.AGGRESSIVE_1);
				player.getActionSender().sendConfig(43, 1);
				break;
			case AGGRESSIVE_2:
			case CONTROLLED_3:
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.CONTROLLED_2);
				player.getActionSender().sendConfig(43, 2);
				break;
			case DEFENSIVE:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				player.getActionSender().sendConfig(43, 3);
				break;
			}
			// player.getActionSender().sendSidebarInterface(78, 128);
			player.getActionSender().sendString(593, 1, originalName);
			player.getActionSender().sendConfig(843, 4);
		} else if (genericName.startsWith("halberd")) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.CONTROLLED_1);
				player.getActionSender().sendConfig(43, 0);
				break;
			case AGGRESSIVE_1:
			case CONTROLLED_2:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.CONTROLLED_2);
				player.getActionSender().sendConfig(43, 1);
				break;
			case AGGRESSIVE_2:
			case DEFENSIVE:
			case CONTROLLED_3:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.CONTROLLED_3);
				player.getActionSender().sendConfig(43, 3);
				break;
			}
			// player.getActionSender().sendSidebarInterface(84, 128);
			player.getActionSender().sendString(593, 1, originalName);
			player.getActionSender().sendConfig(843, 12);
		} else if (genericName.contains("spear") || genericName.contains("hasta") || genericName.contains("banner")) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.CONTROLLED_1);
				player.getActionSender().sendConfig(43, 0);
				break;
			case AGGRESSIVE_1:
			case CONTROLLED_2:
				player.getCombatState().setAttackType(AttackType.SLASH);
				player.getCombatState().setCombatStyle(CombatStyle.CONTROLLED_2);
				player.getActionSender().sendConfig(43, 1);
				break;
			case AGGRESSIVE_2:
			case CONTROLLED_3:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.CONTROLLED_3);
				player.getActionSender().sendConfig(43, 2);
				break;
			case DEFENSIVE:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.STAB);
				player.getCombatState().setCombatStyle(CombatStyle.DEFENSIVE);
				player.getActionSender().sendConfig(43, 3);
				break;
			}
			// player.getActionSender().sendSidebarInterface(87, 128);
			player.getActionSender().sendString(593, 1, originalName);
			player.getActionSender().sendConfig(843, 15);
		} else if (genericName.equals("bow-sword")) {
			switch (player.getCombatState().getCombatStyle()) {
			case ACCURATE:
			case CONTROLLED_1:
			case AGGRESSIVE_1:
			case CONTROLLED_2:
			case AGGRESSIVE_2:
			case CONTROLLED_3:
			case DEFENSIVE:
			case AUTOCAST:
			case DEFENSIVE_AUTOCAST:
				player.getCombatState().setAttackType(AttackType.CRUSH);
				player.getCombatState().setCombatStyle(CombatStyle.ACCURATE);
				player.getActionSender().sendConfig(43, 0);
				break;
			}
			// player.getActionSender().sendSidebarInterface(80, 128);
			player.getActionSender().sendString(593, 1, originalName);
		}
		player.getCombatState().setQueuedSpell(null);
		player.getCombatState().setSpecial(false);
		player.getActionSender().updateSpecialConfig();
		// setSpecials(id);
		if (player.getAutocastSpell() != null) {
			MagicCombatAction.setAutocast(player, null, -1, false);
			player.getActionSender().sendConfig(108, 0);
			return;
		}
	}

	/**
	 * Filters a weapon name.
	 * 
	 * @param name
	 *            The original name.
	 * @return The filtered name.
	 */
	private String filterWeaponName(String name) {
		final String[] filtered = new String[] { "Iron", "Steel", "Scythe", "Black", "Mithril", "Adamant", "Rune",
				"Granite", "Dragon", "Crystal", "Bronze", "Drag" };
		for (String filter : filtered) {
			name = name.replaceAll(filter, "");
		}
		return name;
	}
}
