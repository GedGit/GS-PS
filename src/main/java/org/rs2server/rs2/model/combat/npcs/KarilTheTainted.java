package org.rs2server.rs2.model.combat.npcs;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.Projectile;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

public class KarilTheTainted extends AbstractCombatAction {
	
	/**
	 * The singleton instance.
	 */
	private static final KarilTheTainted INSTANCE = new KarilTheTainted();
	
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
		int delay = (gfxDelay / 20) - 1;
		attacker.playAnimation(Animation.create(2075));
		attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 27, 45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
		int damage = 0;
		if (!victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MISSILES)) {
			damage = 20;
		}
		
		attacker.getCombatState().setAttackDelay(3);
		final int randomHit = Misc.random(damage);
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

	@Override
	public int distance(Mob attacker) {
		return 4;
	}

}
