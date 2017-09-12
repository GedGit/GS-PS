package org.rs2server.rs2.model.combat.impl;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.CombatFormula;
import org.rs2server.rs2.model.combat.CombatState.AttackType;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.equipment.PoisonType;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.tickable.Tickable;

import java.util.Random;

/**
 * Normal melee combat action.
 * 
 * @author Graham Edgecombe
 *
 */
public class MeleeCombatAction extends AbstractCombatAction {

	/**
	 * The singleton instance.
	 */
	private static final MeleeCombatAction INSTANCE = new MeleeCombatAction();

	/**
	 * Gets the singleton instance.
	 * 
	 * @return The singleton instance.
	 */
	public static CombatAction getAction() {
		return INSTANCE;
	}

	/**
	 * The random number generator.
	 */
	private final Random random = new Random();

	/**
	 * Default private constructor.
	 */
	public MeleeCombatAction() {

	}

	@Override
	public boolean canHit(Mob attacker, Mob victim, boolean messages, boolean cannon) {
		if (!super.canHit(attacker, victim, messages, cannon)) {
			return false;
		}
		if (victim.isNPC()) {
			NPC npc = (NPC) victim;
			if (npc.getId() == 3162 || npc.getId() == 3163 || npc.getId() == 3164 || npc.getId() == 3165) {
				if (messages && attacker.getActionSender() != null) {
					attacker.getActionSender().sendMessage("That NPC is flying too high to be attaked!");
				}
				return false;
			}
		}
		return true; // TODO implement!
	}

	@Override
	public void hit(final Mob attacker, final Mob victim) {
		super.hit(attacker, victim);

		boolean special = attacker.getCombatState().isSpecialOn() ? canSpecial(attacker, victim) : false;

		final int maxHit = CombatFormula.calculateMeleeMaxHit(attacker, victim, special);
		int damage = damage(maxHit, attacker, victim, attacker.getCombatState().getAttackType(), Skills.ATTACK,
				Prayers.PROTECT_FROM_MELEE, special, false);
		int randomHit = random.nextInt(damage < 1 ? 1 : damage + 1);
		if (randomHit > victim.getSkills().getLevel(Skills.HITPOINTS))
			randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
		final int hit = randomHit; // +1 as its exclusive
		if (special)
			special(attacker, victim, hit, false);

		if (!special) {
			int attackAnimationIndex = attacker.getCombatState().getCombatStyle().getId();
			if (attackAnimationIndex > 3) {
				attackAnimationIndex -= 4;
			}
			if (attacker.getEquipment().get(Equipment.SLOT_WEAPON) != null
					&& attacker.getEquipment().get(Equipment.SLOT_WEAPON).getEquipmentDefinition() != null) {
				int length = attacker.getEquipment().get(Equipment.SLOT_WEAPON).getEquipmentDefinition().getAnimation()
						.getAttacks().length;
				if (attackAnimationIndex >= length) {
					attackAnimationIndex = length - 1;
				}
			}
			attacker.playAnimation((attacker.getEquipment().get(Equipment.SLOT_WEAPON) != null
					&& attacker.getEquipment().get(Equipment.SLOT_WEAPON).getEquipmentDefinition() != null)
							? attacker.getEquipment().get(Equipment.SLOT_WEAPON).getEquipmentDefinition().getAnimation()
									.getAttack(attackAnimationIndex)
							: attacker.getAttackAnimation());
		}

		if (victim.getCombatState().getPoisonDamage() < 1 && random.nextInt(11) == 3
				&& victim.getCombatState().canBePoisoned()) {
			if (attacker.getEquipment().get(Equipment.SLOT_WEAPON) != null
					&& attacker.getEquipment().get(Equipment.SLOT_WEAPON).getEquipmentDefinition() != null) {
				if (attacker.getEquipment().get(Equipment.SLOT_WEAPON).getEquipmentDefinition()
						.getPoisonType() != PoisonType.NONE) {
					victim.getCombatState().setPoisonDamage(attacker.getEquipment().get(Equipment.SLOT_WEAPON)
							.getEquipmentDefinition().getPoisonType().getMeleeDamage(), attacker);
					if (victim.getActionSender() != null) {
						victim.getActionSender().sendMessage("You have been poisoned!");
						// player.getActionSender().sendConfig(102, 1);
					}
				}
			}
		}

		if (CombatFormula.fullGuthan(attacker)) {
			int guthan = random.nextInt(4);
			if (guthan == 1) {
				attacker.getSkills().increaseLevelToMaximum(Skills.HITPOINTS, hit);
				victim.playGraphics(Graphic.create(398, 0, 0));
			}
		}

		if (CombatFormula.fullTorag(attacker)) {
			int torag = random.nextInt(4);
			if (torag == 1) {
				if (victim.getWalkingQueue().getEnergy() <= 4)
					victim.getWalkingQueue().setEnergy(0);
				else
					victim.getWalkingQueue().setEnergy(victim.getWalkingQueue().getEnergy() - 4);
				victim.playGraphics(Graphic.create(399, 0, 0));
				if (victim.getActionSender() != null)
					victim.getActionSender().updateRunningConfig();
			}
		}

		World.getWorld().submit(new Tickable(1) {
			@Override
			public void execute() {
				victim.inflictDamage(new Hit(hit), attacker);
				victim.getActiveCombatAction().defend(attacker, victim, true);
				smite(attacker, victim, hit);
				recoil(attacker, victim, hit);
				this.stop();
			}
		});
		vengeance(attacker, victim, hit, 1);
		addExperience(attacker, hit);

		if (special) {
			final Item weapon = attacker.getEquipment().get(Equipment.SLOT_WEAPON);
			if (weapon != null && weapon.getEquipmentDefinition() != null) {
				if (weapon.getId() == 3204 && victim.getWidth() > 1 && !victim.isPlayer())
					weapon.getEquipmentDefinition().setSpecialHits(2);
				World.getWorld().submit(new Tickable(1) {
					@Override
					public void execute() {
						for (int i = 1; i < weapon.getEquipmentDefinition().getSpecialHits(); i++) {
							int dmg = damage(maxHit, attacker, victim, attacker.getCombatState().getAttackType(),
									Skills.ATTACK, Prayers.PROTECT_FROM_MELEE, true, false);
							int specialHit = random.nextInt(dmg < 1 ? 1 : dmg + 1);
							if (specialHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
								specialHit = victim.getSkills().getLevel(Skills.HITPOINTS);
							}
							victim.inflictDamage(new Hit(specialHit), attacker);
							victim.getActiveCombatAction().defend(attacker, victim, true);
							smite(attacker, victim, specialHit);
							addExperience(attacker, specialHit);
							recoil(attacker, victim, specialHit);
						}
						this.stop();
					}
				});
			}
		}
	}

	@Override
	public void defend(Mob attacker, Mob victim, boolean blockAnimation) {
		super.defend(attacker, victim, blockAnimation);
	}

	@Override
	public boolean canSpecial(Mob attacker, Mob victim) {
		return super.canSpecial(attacker, victim);
	}

	@Override
	public void special(Mob attacker, Mob victim, int damage, boolean boltSpecial) { 
		super.special(attacker, victim, damage, false);
	}

	@Override
	public void special(Mob attacker, final Item item) {
		super.special(attacker, item);
	}

	@Override
	public int distance(Mob attacker) {
		return 1;
	}

	@Override
	public int damage(int maxHit, Mob attacker, Mob victim, AttackType attackType, int skill, int prayer,
			boolean special, boolean ignorePrayers) {
		return super.damage(maxHit, attacker, victim, attackType, skill, prayer, special, ignorePrayers);
	}

	@Override
	public void addExperience(Mob attacker, int damage) {
		super.addExperience(attacker, damage);
		for (int i = 0; i < attacker.getCombatState().getCombatStyle().getSkills().length; i++) {
			attacker.getSkills().addExperience(attacker.getCombatState().getCombatStyle().getSkill(i),
					(attacker.getCombatState().getCombatStyle().getExperience(i) * damage) * Constants.COMBAT_EXP);
		}
	}

	@Override
	public void recoil(Mob attacker, Mob victim, int damage) {
		super.recoil(attacker, victim, damage);
	}

	@Override
	public void smite(Mob attacker, Mob victim, int damage) {
		super.smite(attacker, victim, damage);
	}

	@Override
	public void vengeance(Mob attacker, Mob victim, int damage, int delay) {
		super.vengeance(attacker, victim, damage, delay);
	}
}
