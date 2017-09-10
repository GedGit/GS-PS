package org.rs2server.rs2.model.combat.npcs;

import org.rs2server.rs2.model.Animation;
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

public class Karamel extends AbstractCombatAction {

	/**
	 * The singleton instance.
	 */
	private static final Karamel INSTANCE = new Karamel();

	/**
	 * Gets the singleton instance.
	 * 
	 * @return The singleton instance.
	 */
	public static CombatAction getAction() {
		return INSTANCE;
	}

	public Karamel() {

	}

	private enum CombatStyle {
		MELEE,

		MAGIC
	}

	public void hit(final Mob attacker, final Mob victim) {
		super.hit(attacker, victim);

		if (!attacker.isNPC()) {
			return; // this should be an NPC!
		}
		CombatStyle style = CombatStyle.MELEE;
		NPC npc = (NPC) attacker;
		int maxHit;
		int damage;
		int randHit;
		if (Misc.random(10) >= 7) {
			style = CombatStyle.MAGIC;
		}

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
			int gfxDelay;
			if (attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
				gfxDelay = 80;
			} else if (attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
				gfxDelay = 100;
			} else if (attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
				gfxDelay = 120;
			} else {
				gfxDelay = 140;
			}
			int delay = (gfxDelay / 20) - 1;
			attacker.playAnimation(Animation.create(1979));
			if (victim.getSprites().getPrimarySprite() != -1 || victim.getSprites().getSecondarySprite() != -1) {
				attacker.playProjectile(Projectile.create(victim.getCentreLocation(), victim.getCentreLocation(), 368,
						70, 50, 90, 0, 0, victim.getProjectileLockonIndex(), 0, 48));
			}
			attacker.forceChat("Semolina-Go!");
			// ((MagicCombatAction) this.hitEnemy(attacker, victim,
			// Spell.FIRE_WAVE, Graphic.create(157, gfxDelay, 100),
			// PoisonType.NONE, false, 20, delay, 0);
			attacker.getCombatState().setSpellDelay(4);
			attacker.getCombatState().setAttackDelay(3);
			int randomHit = damage(7, attacker, victim, AttackType.MAGIC, Skills.MAGIC, Prayers.PROTECT_FROM_MAGIC,
					false, false);
			if (randomHit > 0) {
				World.getWorld().submit(new Tickable(delay) {

					@Override
					public void execute() {
						this.stop();
						victim.playGraphics(Graphic.create(369, 60, 0));
						victim.inflictDamage(new Hit(randomHit), attacker);
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
		return 1;
	}

}
