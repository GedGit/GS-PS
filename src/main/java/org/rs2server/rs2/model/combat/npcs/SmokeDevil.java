package org.rs2server.rs2.model.combat.npcs;

import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.CombatState;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.tickable.Tickable;

import java.util.Random;

/**
 * @author Clank1337
 */
public class SmokeDevil extends AbstractCombatAction {

	/**
	 * The singleton instance.
	 */
	private static final SmokeDevil INSTANCE = new SmokeDevil();

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
	public SmokeDevil() {

	}

	private enum CombatStyle {
		MELEE,

		RANGED
	}

	@Override
	public void hit(final Mob attacker, final Mob victim) {
		super.hit(attacker, victim);

		if (!attacker.isNPC()) {
			return; //this should be an NPC!
		}

		NPC npc = (NPC) attacker;

		CombatStyle style = attacker.getLocation().isWithinDistance(attacker, victim, 1) ?  CombatStyle.MELEE : CombatStyle.RANGED;

		int maxHit;
		int damage;
		int randomHit;
		int hitDelay;
		boolean blockAnimation;
		final int hit;
		maxHit = npc.getCombatDefinition().getMaxHit();
		switch (style) {
			case MELEE:
				attacker.playAnimation(attacker.getAttackAnimation());

				hitDelay = 1;
				blockAnimation = true;
				damage = damage(maxHit, attacker, victim, attacker.getCombatState().getAttackType(), Skills.ATTACK, Prayers.PROTECT_FROM_MELEE, false, false);
				randomHit = random.nextInt(damage < 1 ? 1 : damage + 1);
				if (randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
					randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
				}
				hit = randomHit;
				break;
			default:
				int clientSpeed;
				int gfxDelay;
				blockAnimation = false;
				hitDelay = 1;
				attacker.playAnimation(attacker.getAttackAnimation());
				if (attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
					clientSpeed = 50;
					gfxDelay = 60;
				} else if (attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
					clientSpeed = 70;
					gfxDelay = 80;
				} else if (attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
					clientSpeed = 90;
					gfxDelay = 100;
				} else {
					clientSpeed = 110;
					gfxDelay = 120;
				}
				hitDelay = (gfxDelay / 20) - 1;
				attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 643, 45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
				damage = damage(maxHit, attacker, victim, CombatState.AttackType.RANGE, Skills.RANGE , Prayers.PROTECT_FROM_MISSILES, false, false);
				randomHit = random.nextInt(damage < 1 ? 1 : damage + 1);
				if(randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
					randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
				}
				hit = randomHit;
				break;
		}
		attacker.getCombatState().setAttackDelay(3);
		attacker.getCombatState().setSpellDelay(4);
		World.getWorld().submit(new Tickable(hitDelay) {

			@Override
			public void execute() {
				victim.inflictDamage(new Hit(hit), attacker);
				smite(attacker, victim, hit);
				recoil(attacker, victim, hit);
				this.stop();
			}
		});
		vengeance(attacker, victim, hit, 1);

		victim.getActiveCombatAction().defend(attacker, victim, blockAnimation);
	}

	@Override
	public int distance(Mob attacker) {
		return 6;
	}
}
