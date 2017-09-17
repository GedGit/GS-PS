package org.rs2server.rs2.model.combat.npcs;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.CombatState.AttackType;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.tickable.Tickable;

public class SaradominPriest extends AbstractCombatAction {

	/**
	 * The singleton instance.
	 */
	private static final SaradominPriest INSTANCE = new SaradominPriest();

	/**
	 * Gets the singleton instance.
	 * 
	 * @return The singleton instance.
	 */
	public static CombatAction getAction() {
		return INSTANCE;
	}

	public SaradominPriest() {

	}

	private enum CombatStyle {
		MAGIC
	}

	public void hit(final Mob attacker, final Mob victim) {
		super.hit(attacker, victim);

		if (!attacker.isNPC())
			return; // this should be an NPC!
		
		CombatStyle style = CombatStyle.MAGIC;

		switch (style) {
		
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
			attacker.playAnimation(Animation.create(811));
			attacker.getCombatState().setSpellDelay(5);
			attacker.getCombatState().setAttackDelay(5);

			World.getWorld().submit(new Tickable(delay) {

				@Override
				public void execute() {
					int randomHit = damage(20, attacker, victim, AttackType.MAGIC, Skills.MAGIC,
							Prayers.PROTECT_FROM_MAGIC, false, false);
					victim.playGraphics(Graphic.create(76, delay, 100));
					victim.inflictDamage(new Hit(randomHit), attacker);
					smite(attacker, victim, randomHit);
					recoil(attacker, victim, randomHit);
					vengeance(attacker, victim, randomHit, 1);
					victim.getActiveCombatAction().defend(attacker, victim, true);
					this.stop();
				}

			});
			break;
		}
	}

	@Override
	public int distance(Mob attacker) {
		return 6;
	}
}