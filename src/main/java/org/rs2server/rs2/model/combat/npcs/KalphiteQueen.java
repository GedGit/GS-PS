package org.rs2server.rs2.model.combat.npcs;

import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.CombatState.AttackType;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.tickable.Tickable;

/**
 * Handles Kalphite queen's combat
 * 
 * @author Vichy
 */
public class KalphiteQueen extends AbstractCombatAction {

	/**
	 * The singleton instance.
	 */
	private static final KalphiteQueen INSTANCE = new KalphiteQueen();

	/**
	 * Gets the singleton instance.
	 * 
	 * @return The singleton instance.
	 */
	public static CombatAction getAction() {
		return INSTANCE;
	}

	@Override
	public void hit(final Mob attacker, final Mob victim) {
		super.hit(attacker, victim);
		if (!attacker.isNPC())
			return; // this should be an NPC!

		NPC npc = (NPC) attacker;
		
		int maxHit, damage, randomHit, attackType = 1;
		int clientSpeed, gfxDelay = 0;

		if (npc.getLocation().isWithinDistance(npc, victim, 1)) {
			clientSpeed = 20;
			gfxDelay = 30;
		} else if (npc.getLocation().isWithinDistance(npc, victim, 5)) {
			clientSpeed = 70;
			gfxDelay = 80;
		} else if (npc.getLocation().isWithinDistance(npc, victim, 8)) {
			clientSpeed = 80;
			gfxDelay = 90;
		} else {
			clientSpeed = 100;
			gfxDelay = 110;
		}
		
		final int hit;

		if (npc.getRandom().nextInt(10) > 7)
			attackType = 0; // Melee

		else if (npc.getRandom().nextInt(10) < 4)
			attackType = 1; // Range

		else
			attackType = 2; // Mage

		if (npc.getLocation().getDistance(victim.getLocation()) < 2) {
			if (npc.getRandom().nextInt(10) < 4)
				attackType = 0; // Melee
		}

		switch (attackType) {
		default:
		case 0: // Melee attack
			maxHit = npc.getCombatDefinition().getMaxHit();
			damage = damage(maxHit, npc, victim, npc.getCombatState().getAttackType(), Skills.ATTACK,
					Prayers.PROTECT_FROM_MELEE, false, false);
			npc.playAnimation(Animation.create(npc.getTransformId() == 6501 ? 6235 : 6241));
			randomHit = random.nextInt(damage < 1 ? 1 : damage + 1);
			if (randomHit > victim.getSkills().getLevel(Skills.HITPOINTS))
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			hit = randomHit;
			break;
		case 1: // Magic attack

			npc.playAnimation(Animation.create(npc.getTransformId() == 6501 ? 6234 : 6240));
			npc.playProjectile(Projectile.create(npc.getCentreLocation(), victim.getCentreLocation(), 280,
					gfxDelay, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
			randomHit = damage(31, npc, victim, AttackType.MAGIC, Skills.MAGIC, Prayers.PROTECT_FROM_MAGIC, false,
					false);
			victim.playGraphics(Graphic.create(281, gfxDelay, 0));

			randomHit = random.nextInt(randomHit < 1 ? 1 : randomHit + 1);
			if (randomHit > victim.getSkills().getLevel(Skills.HITPOINTS))
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			hit = randomHit;
			break;
		case 2: // Range attack

			npc.playAnimation(Animation.create(npc.getTransformId() == 6501 ? 6234 : 6240));
			npc.playProjectile(Projectile.create(npc.getCentreLocation(), victim.getCentreLocation(), 473,
					gfxDelay, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
			randomHit = damage(31, attacker, victim, AttackType.RANGE, Skills.RANGE, Prayers.PROTECT_FROM_MISSILES,
					false, false);
			// TODO victim.playGraphics(Graphic.create(278, gfxDelay, 100));

			randomHit = random.nextInt(randomHit < 1 ? 1 : randomHit + 1);
			if (randomHit > victim.getSkills().getLevel(Skills.HITPOINTS))
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			hit = randomHit;
			break;
		}

		World.getWorld().submit(new Tickable(gfxDelay / 20) {
			@Override
			public void execute() {
				victim.inflictDamage(new Hit(hit), attacker);
				smite(attacker, victim, hit);
				recoil(attacker, victim, hit);
				vengeance(attacker, victim, hit, 1);
				victim.getActiveCombatAction().defend(attacker, victim, true);
				this.stop();
			}
		});

		npc.getCombatState().setAttackDelay(6);
		npc.getCombatState().setSpellDelay(6);
	}

	@Override
	public int distance(Mob attacker) {
		return 8;
	}
}