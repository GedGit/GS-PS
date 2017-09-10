package org.rs2server.rs2.model.combat.npcs;

import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.Projectile;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

/**
 * Spinolyp
 * 
 * @author Canownueasy
 *
 */
public class Spinolyp extends AbstractCombatAction {

	/**
	 * The singleton instance.
	 */
	private static final Spinolyp INSTANCE = new Spinolyp();

	/**
	 * Gets the singleton instance.
	 * 
	 * @return The singleton instance.
	 */
	public static CombatAction getAction() {
		return INSTANCE;
	}

	private enum CombatStyle {
		MAGIC
	}

	@Override
	public void hit(final Mob attacker, final Mob victim) {
		super.hit(attacker, victim);
		if (!attacker.isNPC())
			return;
		NPC npc = (NPC) attacker;
		CombatStyle style = CombatStyle.MAGIC;
		int maxHit;
		int randomHit;
		int hitDelay;
		boolean blockAnimation;
		final int hit;

		switch (style) {
		default:
		case MAGIC:
			maxHit = 0;
			if (Misc.random(3) == 3) {
				maxHit = npc.getCombatDefinition().getMaxHit();

				if (victim.getCombatState().getPoisonDamage() < 1 && random.nextInt(10) < 3
						&& victim.getCombatState().canBePoisoned()) {
					victim.getCombatState().setPoisonDamage(2, attacker);
					if (victim.getActionSender() != null)
						victim.getActionSender().sendMessage("You have been poisoned!");
				}

			}
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
			hitDelay = (gfxDelay / 20) - 1;

			attacker.playAnimation(npc.getCombatDefinition().getAttack());
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 94, 45,
					50, clientSpeed, 10, 35, victim.getProjectileLockonIndex(), 10, 48));

			blockAnimation = false;
			if (victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MAGIC))
				maxHit = (int) (maxHit * 0.4);
			randomHit = Misc.random(maxHit);
			if (randomHit > victim.getSkills().getLevel(Skills.HITPOINTS))
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			hit = randomHit;
			if (hit > 0)
				victim.getSkills().decreasePrayerPoints(1);
			break;
		}

		attacker.getCombatState().setAttackDelay(5);
		attacker.getCombatState().setSpellDelay(5);

		World.getWorld().submit(new Tickable(hitDelay) {
			@Override
			public void execute() {
				victim.playGraphics(Graphic.create(hit > 0 ? 95 : 85, 0, 100));
				if (hit > 0) {
					victim.inflictDamage(new Hit(hit), attacker);
					smite(attacker, victim, hit);
					recoil(attacker, victim, hit);
					vengeance(attacker, victim, hit, 1);
				}
				this.stop();
			}
		});

		victim.getActiveCombatAction().defend(attacker, victim, blockAnimation);
	}

	@Override
	public int distance(Mob attacker) {
		return 20;
	}
}