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
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

public class InfernalMage extends AbstractCombatAction {

	/**
	 * The singleton instance.
	 */
	private static final InfernalMage INSTANCE = new InfernalMage();

	/**
	 * Gets the singleton instance.
	 * @return The singleton instance.
	 */
	public static CombatAction getAction() {
		return INSTANCE;
	}

	@Override
	public void hit(final Mob attacker, final Mob victim) {
		super.hit(attacker, victim);

		if(!attacker.isNPC()) {
			return; //this should be an NPC!
		}
		int damage;
		int clientSpeed;
		int gfxDelay;
		if(attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
			clientSpeed = 70;
			gfxDelay = 80;
		} else if(attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
			clientSpeed = 90;
			gfxDelay = 100;
		} else if(attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
			clientSpeed = 110;
			gfxDelay = 120;
		} else {
			clientSpeed = 130;
			gfxDelay = 140;
		}
		attacker.playAnimation(Animation.create(1167));
		attacker.playGraphics(Graphic.create(155, 0, 100));
		attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 156, 45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
		damage = Misc.random(damage(8, attacker, victim, attacker.getCombatState().getAttackType(), Skills.MAGIC , Prayers.PROTECT_FROM_MAGIC, false, false));
		int delay = (gfxDelay / 20) - 1;
		World.getWorld().submit(new Tickable(delay) {

			@Override
			public void execute() {
				this.stop();
                victim.playGraphics(damage <= 0 ? Graphic.create(85, gfxDelay, 100) : Graphic.create(157, gfxDelay, 100));
				victim.inflictDamage(new Hit(damage), attacker);
				smite(attacker, victim, damage);
				recoil(attacker, victim, damage);
			}

		});
		vengeance(attacker, victim, damage, 1);

		victim.getActiveCombatAction().defend(attacker, victim, false);

		attacker.getCombatState().setSpellDelay(5);
		attacker.getCombatState().setAttackDelay(4);
	}

	@Override
	public int distance(Mob attacker) {
		return 4;
	}

}
