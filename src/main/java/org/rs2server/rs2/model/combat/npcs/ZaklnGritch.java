package org.rs2server.rs2.model.combat.npcs;

import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.Projectile;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.CombatState.AttackType;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.tickable.Tickable;

import java.util.Random;

public class ZaklnGritch extends AbstractCombatAction {

	/**
	 * The singleton instance.
	 */
	private static final ZaklnGritch INSTANCE = new ZaklnGritch();

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
	public ZaklnGritch() {
	}

	@Override
	public void hit(final Mob attacker, final Mob victim) {
		super.hit(attacker, victim);

		if (!attacker.isNPC()) {
			return; // this should be an NPC!
		}
		
		

		int maxHit = 26;
		int randomHit;
		int hitDelay;
		boolean blockAnimation;
		final int hit;

		int rClientSpeed;
		int rGfxDelay;
		if (attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
			rClientSpeed = 70;
			rGfxDelay = 80;
		} else if (attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
			rClientSpeed = 90;
			rGfxDelay = 100;
		} else if (attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
			rClientSpeed = 110;
			rGfxDelay = 120;
		} else {
			rClientSpeed = 130;
			rGfxDelay = 140;
		}
		hitDelay = (rGfxDelay / 20) - 1;

		attacker.playAnimation(attacker.getAttackAnimation());
		attacker.playGraphics(Graphic.create(1222, 0, 100));
		attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 1223, 45,
				50, rClientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
		blockAnimation = true;
		randomHit = damage(maxHit, attacker, victim, AttackType.RANGE, Skills.RANGE, Prayers.PROTECT_FROM_MISSILES,
				false, false);
		randomHit = random.nextInt(randomHit < 1 ? 1 : randomHit + 1);
		if (randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
			randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
		}
		hit = randomHit;

		attacker.getCombatState().setAttackDelay(6);
		attacker.getCombatState().setSpellDelay(6);

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
		return 1;
	}
}