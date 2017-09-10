package org.rs2server.rs2.model.combat;

import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.combat.CombatState.CombatStyle;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction;
import org.rs2server.rs2.model.combat.impl.MeleeCombatAction;
import org.rs2server.rs2.model.combat.impl.RangeCombatAction;
import org.rs2server.rs2.model.combat.impl.RangeCombatAction.BowType;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.equipment.EquipmentDefinition;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.util.NPCUtils;

/**
 * A utility class which contains combat-related formulae.
 * 
 * @author Scu11
 * @author Graham Edgecombe
 *
 */
public final class CombatFormula {

	/**
	 * Used in defence formulae.
	 */
	public static final double DEFENCE_MODIFIER = 0.895;

	/**
	 * Default private constructor.
	 */
	private CombatFormula() {

	}

	/**
	 * Calculates a mob's melee max hit.
	 */
	public static int calculateMeleeMaxHit(Mob mob, Mob victim, boolean special) {
		if (mob.isNPC()) {
			NPC npc = (NPC) mob;
			return npc.getCombatDefinition().getMaxHit();
		}
		int maxHit = 0;
		double specialMultiplier = 1;
		double prayerMultiplier = 1;
		double otherBonusMultiplier = 1; // TODO: salve amulet
											// = 1.15, salve amulet(e) = 1.2
		int strengthLevel = mob.getSkills().getLevel(Skills.STRENGTH);
		int combatStyleBonus = 0;

		if (mob.getCombatState().getPrayer(Prayers.BURST_OF_STRENGTH)) {
			prayerMultiplier = 1.05;
		} else if (mob.getCombatState().getPrayer(Prayers.SUPERHUMAN_STRENGTH)) {
			prayerMultiplier = 1.1;
		} else if (mob.getCombatState().getPrayer(Prayers.ULTIMATE_STRENGTH)) {
			prayerMultiplier = 1.15;
		} else if (mob.getCombatState().getPrayer(Prayers.CHIVALRY)) {
			prayerMultiplier = 1.18;
		} else if (mob.getCombatState().getPrayer(Prayers.PIETY)) {
			prayerMultiplier = 1.23;
		}

		switch (mob.getCombatState().getCombatStyle()) {
		case AGGRESSIVE_1:
		case AGGRESSIVE_2:
			combatStyleBonus = 3;
			break;
		case CONTROLLED_1:
		case CONTROLLED_2:
		case CONTROLLED_3:
			combatStyleBonus = 1;
			break;
		default:
			break;
		}

		if (fullVoidMelee(mob))
			otherBonusMultiplier = 1.1;

		if (fullEliteVoidMelee(mob))
			otherBonusMultiplier = 1.125;

		if (mob.isPlayer() && victim.isNPC()) {
			NPC npc = (NPC) victim;
			if (NPCUtils.isUndeadNPC(npc.getDefinition().getName())) {
				Player player = (Player) mob;
				if (player.getEquipment().contains(4081))
					otherBonusMultiplier *= 1.15;
				else if (player.getEquipment().contains(10588))
					otherBonusMultiplier *= 1.2;
			}
		}

		if (hasBerserkerNecklaceBonus(mob))
			otherBonusMultiplier = 1.2;

		if (mob.isPlayer() && victim.isNPC()) {
			final Player player = (Player) mob;
			final NPC npc = (NPC) victim;
			if (player.getSlayer().getSlayerTask() != null
					&& player.getSlayer().getSlayerTask().getName().contains(npc.getDefinition().getName())
					&& hasBlackMaskOrSlayerHelm(player)) {
				otherBonusMultiplier = 1.15;
			}
		}
		if (fullDharok(mob)) {
			double dharokMultiplier = ((1 - ((float) mob.getSkills().getLevel(Skills.HITPOINTS)
					/ (float) mob.getSkills().getLevelForExperience(Skills.HITPOINTS)) * 1.7)) + 1;
			otherBonusMultiplier *= dharokMultiplier;
		}
		int effectiveStrengthDamage = (int) ((strengthLevel * prayerMultiplier * otherBonusMultiplier)
				+ combatStyleBonus);
		double baseDamage = 1.3 + (effectiveStrengthDamage / 10) + (mob.getCombatState().getBonus(10) / 80)
				+ ((effectiveStrengthDamage * mob.getCombatState().getBonus(10)) / 640);

		if (special) {
			if (mob.getEquipment().get(Equipment.SLOT_WEAPON) != null) {
				switch (mob.getEquipment().get(Equipment.SLOT_WEAPON).getId()) {
				case 11802:
					specialMultiplier = 1.42375;
					break;
				case 19481:
					specialMultiplier = 1.42375;
					break;
				case 11804:
					specialMultiplier = 1.1825;
					break;
				case 11806:
				case 11808:
					specialMultiplier = 1.075;
					break;
				case 3101:
				case 3204:
				case 1215:
				case 1231:
				case 5680:
				case 5698:
					specialMultiplier = 1.25;
					break;
				case 21009:
					specialMultiplier = 1.20;
					break;
				case 1305:
					specialMultiplier = 1.15;
					break;
				case 1434:
					specialMultiplier = 1.45;
					break;
				case 13652:
					specialMultiplier = 0.75;
					break;
				}
			}
		}
		maxHit = (int) (baseDamage * specialMultiplier);
		return maxHit;
	}

	/**
	 * Calculates a mob's range max hit.
	 */
	public static int calculateRangeMaxHit(Mob mob, Mob victim, boolean special) {
		if (mob.isNPC()) {
			NPC npc = (NPC) mob;
			return npc.getCombatDefinition().getMaxHit();
		}
		int maxHit = 0;
		double specialMultiplier = 1;
		double prayerMultiplier = 1;
		double otherBonusMultiplier = 1;
		int rangedStrength = mob.getCombatState().getBonus(12);
		Item weapon = mob.getEquipment().get(Equipment.SLOT_WEAPON);
		BowType bow = weapon.getEquipmentDefinition().getBowType();

		if (bow == BowType.CRYSTAL_BOW) {
			/**
			 * Crystal Bow does not use arrows, so we don't use the arrows range strength
			 * bonus.
			 */
			rangedStrength = mob.getEquipment().get(Equipment.SLOT_WEAPON).getEquipmentDefinition().getBonus(12);
		}

		if (mob.isPlayer() && victim.isNPC()) {
			final Player player = (Player) mob;
			final NPC npc = (NPC) victim;
			if (player.getSlayer().getSlayerTask() != null
					&& player.getSlayer().getSlayerTask().getName().contains(npc.getDefinition().getName())
					&& hasImbuedSlayerHelm(player)) {
				otherBonusMultiplier = 1.15;
			}
		}

		int rangeLevel = mob.getSkills().getLevel(Skills.RANGE) + 2;
		int combatStyleBonus = 0;

		switch (mob.getCombatState().getCombatStyle()) {
		case ACCURATE:
			combatStyleBonus = 3;
			break;
		case DEFENSIVE:
			combatStyleBonus = 2;
			break;
		default:
			combatStyleBonus = 1;
			break;
		}

		Player player = (Player) mob;
		if (player.getCombatState().getPrayer(Prayers.SHARP_EYE))
			prayerMultiplier *= 1.05;
		if (player.getCombatState().getPrayer(Prayers.HAWK_EYE))
			prayerMultiplier *= 1.1;
		if (player.getCombatState().getPrayer(Prayers.EAGLE_EYE))
			prayerMultiplier *= 1.15;
		if (player.getCombatState().getPrayer(Prayers.RIGOUR))
			prayerMultiplier *= 1.23;

		if (fullVoidRange(mob))
			otherBonusMultiplier = 1.1;

		if (fullEliteVoidRange(mob))
			otherBonusMultiplier = 1.125;

		int effectiveRangeDamage = (int) ((rangeLevel * prayerMultiplier * otherBonusMultiplier) + combatStyleBonus);
		double baseDamage = 1.3 + (effectiveRangeDamage / 10) + (rangedStrength / 40)
				+ ((effectiveRangeDamage * rangedStrength) / 640);

		if (special) {
			if (mob.getEquipment().get(Equipment.SLOT_ARROWS) != null) {
				switch (mob.getEquipment().get(Equipment.SLOT_ARROWS).getId()) {
				case 9243:
					specialMultiplier = 1.15;
					break;
				case 9244:
					specialMultiplier = 1.45;
					break;
				case 9245:
					specialMultiplier = 1.15;
					break;
				case 9236:
					specialMultiplier = 1.25;
					break;
				case 882:
				case 884:
				case 886:
				case 888:
				case 890:
				case 892:
				case 11212:
					if (mob.getEquipment().get(Equipment.SLOT_WEAPON) != null
							&& mob.getEquipment().get(Equipment.SLOT_WEAPON).getId() == 11235) {
						specialMultiplier = 1.3;
						if (mob.getEquipment().get(Equipment.SLOT_ARROWS).getId() == 11212)
							specialMultiplier += .5;
					}
					if (mob.getEquipment().get(Equipment.SLOT_ARROWS).getId() == 11212)
						specialMultiplier = 1.5;
					break;
				}
			}
		}

		maxHit = (int) (baseDamage * specialMultiplier);
		return maxHit;
	}

	public static boolean hasAmuletOfTheDamned(Mob mob) {
		return mob.getEquipment().contains(12851);
	}

	public static boolean hasBerserkerNecklaceBonus(Mob mob) {
		return mob.getEquipment().contains(11128)
				&& (mob.getEquipment().contains(6523) || mob.getEquipment().contains(6528)
						|| mob.getEquipment().contains(6527) || mob.getEquipment().contains(6525));
	}

	public static boolean hasBlackMaskOrSlayerHelm(Mob mob) {
		return mob.getEquipment() != null && (mob.getEquipment().contains(8901) || mob.getEquipment().contains(11864)
				|| mob.getEquipment().contains(11865) || mob.getEquipment().contains(19639)
				|| mob.getEquipment().contains(19643) || mob.getEquipment().contains(19647)
				|| mob.getEquipment().contains(8903) || mob.getEquipment().contains(8905)
				|| mob.getEquipment().contains(8907) || mob.getEquipment().contains(8909)
				|| mob.getEquipment().contains(8911) || mob.getEquipment().contains(8913)
				|| mob.getEquipment().contains(8915) || mob.getEquipment().contains(8917)
				|| mob.getEquipment().contains(8919) || mob.getEquipment().contains(8921));
	}

	public static boolean hasImbuedSlayerHelm(Mob mob) {
		return mob.getEquipment() != null && (mob.getEquipment().contains(11865) || mob.getEquipment().contains(19641)
				|| mob.getEquipment().contains(19645) || mob.getEquipment().contains(19649));
	}

	public static boolean fullVoidMelee(Mob mob) {
		return mob.getEquipment() != null && mob.getEquipment().contains(8839) && mob.getEquipment().contains(8840)
				&& mob.getEquipment().contains(8842) && mob.getEquipment().contains(11665);
	}

	public static boolean fullEliteVoidMelee(Mob mob) {
		return mob.getEquipment() != null && (mob.getEquipment().contains(8839) || mob.getEquipment().contains(13072))
				&& (mob.getEquipment().contains(8840) || mob.getEquipment().contains(13073))
				&& mob.getEquipment().contains(8842) && mob.getEquipment().contains(11665);
	}

	public static boolean fullVoidRange(Mob mob) {
		return mob.getEquipment() != null && mob.getEquipment().contains(8839) && mob.getEquipment().contains(8840)
				&& mob.getEquipment().contains(8842) && mob.getEquipment().contains(11664);
	}

	public static boolean fullEliteVoidRange(Mob mob) {
		return mob.getEquipment() != null && mob.getEquipment().contains(13072) && mob.getEquipment().contains(13073)
				&& mob.getEquipment().contains(8842) && mob.getEquipment().contains(11664);
	}

	public static boolean fullVoidMage(Mob mob) {
		return mob.getEquipment() != null && mob.getEquipment().contains(8839) && mob.getEquipment().contains(8840)
				&& mob.getEquipment().contains(8842) && mob.getEquipment().contains(11663);
	}

	public static boolean fullEliteVoidMage(Mob mob) {
		return mob.getEquipment() != null && mob.getEquipment().contains(13072) && mob.getEquipment().contains(13073)
				&& mob.getEquipment().contains(8842) && mob.getEquipment().contains(11663);
	}

	public static boolean fullGuthan(Mob mob) {
		return mob.getEquipment() != null && mob.getEquipment().contains(4724) && mob.getEquipment().contains(4726)
				&& mob.getEquipment().contains(4728) && mob.getEquipment().contains(4730);
	}

	public static boolean fullTorag(Mob mob) {
		return mob.getEquipment() != null && mob.getEquipment().contains(4745) && mob.getEquipment().contains(4747)
				&& mob.getEquipment().contains(4749) && mob.getEquipment().contains(4751);
	}

	public static boolean fullKaril(Mob mob) {
		return mob.getEquipment() != null && mob.getEquipment().contains(4732) && mob.getEquipment().contains(4734)
				&& mob.getEquipment().contains(4736) && mob.getEquipment().contains(4738);
	}

	public static boolean fullAhrim(Mob mob) {
		return mob.getEquipment() != null && mob.getEquipment().contains(4708) && mob.getEquipment().contains(4710)
				&& mob.getEquipment().contains(4712) && mob.getEquipment().contains(4714);
	}

	public static boolean fullAhrimDamned(Mob mob) {
		return mob.getEquipment() != null && mob.getEquipment().contains(4708) && mob.getEquipment().contains(4710)
				&& mob.getEquipment().contains(4712) && mob.getEquipment().contains(4714)
				&& (mob.getEquipment().contains(12851) || mob.getEquipment().contains(12853));
	}

	public static boolean fullDharok(Mob mob) {
		return mob.getEquipment() != null && mob.getEquipment().contains(4716) && mob.getEquipment().contains(4718)
				&& mob.getEquipment().contains(4720) && mob.getEquipment().contains(4722);
	}

	public static boolean fullVerac(Mob mob) {
		return mob.getEquipment() != null && mob.getEquipment().contains(4753) && mob.getEquipment().contains(4755)
				&& mob.getEquipment().contains(4757) && mob.getEquipment().contains(4759);
	}

	/**
	 * The percentage of the hit reducted by antifire.
	 */
	public static double dragonfireReduction(Mob mob) {
		boolean dragonfireShield = mob.getEquipment() != null && (mob.getEquipment().contains(1540)
				|| mob.getEquipment().contains(11283) || mob.getEquipment().contains(11284)
				|| mob.getEquipment().contains(20714) || mob.getEquipment().contains(11285));
		boolean dragonfirePotion = false;
		if (mob.hasAttribute("antiFire")) {
			dragonfirePotion = System.currentTimeMillis() - (long) mob.getAttribute("antiFire", 0L) < 360000;
		} else if (mob.hasAttribute("extended_antiFire")) {
			dragonfirePotion = System.currentTimeMillis() - (long) mob.getAttribute("extended_antiFire", 0L) < 720000;
		}
		boolean protectPrayer = mob.getCombatState().getPrayer(Prayers.PROTECT_FROM_MAGIC);
		if (dragonfireShield && dragonfirePotion) {
			if (mob.getActionSender() != null) {
				mob.getActionSender().sendMessage("You shield absorbs most of the dragon fire!");
				mob.getActionSender().sendMessage("Your potion protects you from the heat of the dragon's breath!");
			}
			return 1;
		} else if (dragonfireShield) {
			if (mob.getActionSender() != null) {
				mob.getActionSender().sendMessage("You shield absorbs most of the dragon fire!");
			}
			return 0.8; // 80%
		} else if (dragonfirePotion) {
			if (mob.getActionSender() != null) {
				mob.getActionSender().sendMessage("Your potion protects you from the heat of the dragon's breath!");
			}
			return 0.8; // 80%
		} else if (protectPrayer) {
			if (mob.getActionSender() != null) {
				mob.getActionSender().sendMessage("Your prayers resist some of the dragon fire.");
			}
			return 0.6; // 60%
		}
		return /* mob.getEquipment() != null */0;
	}

	/**
	 * Get the attackers' weapon speed.
	 * 
	 * @param player
	 *            The player for whose weapon we are getting the speed value.
	 * @return A <code>long</code>-type value of the weapon speed.
	 */
	public static int getCombatCooldownDelay(Mob mob) {
		double extra = 0;
		if (getActiveCombatAction(mob) == RangeCombatAction.getAction()) {
			if (mob.getCombatState().getCombatStyle() != CombatStyle.AGGRESSIVE_1 || (mob.getEquipment().contains(12926)
					&& (mob.getInteractingEntity() != null && mob.getInteractingEntity() instanceof Player))) {
				/**
				 * If we are ranging and are not on rapid, combat speed is increased by 1 cycle
				 */
				extra = 1;
			}
		}
		return (int) ((mob.getEquipment() != null && mob.getEquipment().get(3) != null)
				? mob.getEquipment().get(3).getEquipmentDefinition().getSpeed() + extra
				: 4);
	}

	public static CombatAction getActiveCombatAction(Mob mob) {
		if (mob.getDefaultCombatAction() != null) {
			return mob.getDefaultCombatAction();
		}
		if (mob.getCombatState().getQueuedSpell() != null
				|| (mob.getAutocastSpell() != null && (mob.getCombatState().getCombatStyle() == CombatStyle.AUTOCAST
						|| mob.getCombatState().getCombatStyle() == CombatStyle.DEFENSIVE_AUTOCAST))) {
			return MagicCombatAction.getAction();
		}
		Item weapon = mob.getEquipment().get(Equipment.SLOT_WEAPON);
		if (weapon != null) {
			EquipmentDefinition weaponEquipDef = weapon.getEquipmentDefinition();
			if (weaponEquipDef.getBowType() != null || weaponEquipDef.getRangeWeaponType() != null) {
				return RangeCombatAction.getAction();
			}
		}
		return MeleeCombatAction.getAction();
	}

	public static boolean hasAccumulator(Mob attacker) {
		return attacker.getEquipment().get(Equipment.SLOT_CAPE) != null
				&& (attacker.getEquipment().get(Equipment.SLOT_CAPE).getId() == 10499
						|| attacker.getEquipment().get(Equipment.SLOT_CAPE).getId() == 13337
						|| attacker.getEquipment().get(Equipment.SLOT_CAPE).getId() == 9757
						|| attacker.getEquipment().get(Equipment.SLOT_CAPE).getId() == 9756
						|| attacker.getEquipment().get(Equipment.SLOT_CAPE).getId() == 10498);
	}
}
