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
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

public class AgrithNaNa extends AbstractCombatAction {

	/**
	 * The singleton instance.
	 */
	private static final AgrithNaNa INSTANCE = new AgrithNaNa();

	/**
	 * Gets the singleton instance.
	 * 
	 * @return The singleton instance.
	 */
	public static CombatAction getAction() {
		return INSTANCE;
	}

	public AgrithNaNa() {

	}

	private enum CombatStyle {
		MELEE,

		MAGIC
	}

	@Override
	public void hit(final Mob attacker, final Mob victim) {
		super.hit(attacker, victim);

		if (!attacker.isNPC()) {
			return; // this should be an NPC!
		}
		CombatStyle style = attacker.getLocation().isWithinDistance(attacker, victim, 1) ? CombatStyle.MELEE
				: CombatStyle.MAGIC;
		NPC npc = (NPC) attacker;
		int maxHit;
		int damage;
		int randHit;
		switch (style) {
		case MELEE:
			npc.playAnimation(npc.getAttackAnimation());
			maxHit = npc.getCombatDefinition().getMaxHit();
			damage = damage(maxHit, attacker, victim, attacker.getCombatState().getAttackType(), Skills.ATTACK,
					Prayers.PROTECT_FROM_MELEE, false, false);
			randHit = random.nextInt(damage < 1 ? 1 : damage + 1);
			if (randHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				randHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			victim.inflictDamage(new Hit(randHit), attacker);
			smite(attacker, victim, randHit);
			recoil(attacker, victim, randHit);
			vengeance(attacker, victim, randHit, 1);
			victim.getActiveCombatAction().defend(attacker, victim, true);
			break;
		case MAGIC:
			int clientSpeed;
			int gfxDelay;
			if (attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
				clientSpeed = 70;
				gfxDelay = 80;
			} else if (attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
				clientSpeed = 90;
				gfxDelay = 100;
			} else if (attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
				clientSpeed = 110;
				gfxDelay = 120;
			} else {
				clientSpeed = 130;
				gfxDelay = 140;
			}
			int delay = (gfxDelay / 20) - 1;
			attacker.playAnimation(npc.getAttackAnimation());
			attacker.playGraphics(Graphic.create(155, 0, 100));
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 156, 45,
					50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
			// ((MagicCombatAction) this.hitEnemy(attacker, victim,
			// Spell.FIRE_WAVE, Graphic.create(157, gfxDelay, 100),
			// PoisonType.NONE, false, 20, delay, 0);
			attacker.getCombatState().setSpellDelay(5);
			attacker.getCombatState().setAttackDelay(4);
			int randomHit = Misc.random(damage(20, attacker, victim, AttackType.MAGIC, Skills.MAGIC,
					Prayers.PROTECT_FROM_MAGIC, false, false));
			victim.playGraphics(
					randomHit <= 0 ? Graphic.create(85, gfxDelay, 100) : Graphic.create(157, gfxDelay, 100));
			if (randomHit > 0) {
				World.getWorld().submit(new Tickable(delay) {

					@Override
					public void execute() {
						this.stop();
						victim.inflictDamage(new Hit(randomHit), attacker);
						smite(attacker, victim, randomHit);
						recoil(attacker, victim, randomHit);
						vengeance(attacker, victim, randomHit, 1);
					}

				});
			}
			victim.getActiveCombatAction().defend(attacker, victim, true);
			break;
		}
	}

	@Override
	public int distance(Mob attacker) {
		return 4;
	}
}