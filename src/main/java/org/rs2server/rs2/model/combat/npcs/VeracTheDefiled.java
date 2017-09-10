package org.rs2server.rs2.model.combat.npcs;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.util.Misc;

public class VeracTheDefiled extends AbstractCombatAction {
	
	/**
	 * The singleton instance.
	 */
	private static final VeracTheDefiled INSTANCE = new VeracTheDefiled();
	
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
		
		attacker.playAnimation(Animation.create(2062));
		
		int damage = 0;
		int randomHit;
		if (Misc.random(3) == 3) {
			if (!victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MELEE)
					&& Misc.random(10) > 6) {
				randomHit = Misc.random(25);
				if (randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
					randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
				}
				damage = randomHit;
			} else if (victim.getCombatState().getPrayer(
					Prayers.PROTECT_FROM_MELEE)
					&& Misc.random(10) > 3) {
				randomHit = Misc.random(25);
				if (randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
					randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
				}
				damage = randomHit;
			}
		}
		attacker.getCombatState().setAttackDelay(4);
		
		victim.inflictDamage(new Hit(damage), attacker);
		smite(attacker, victim, damage);
		recoil(attacker, victim, damage);
		vengeance(attacker, victim, damage, 1);
	}

	@Override
	public int distance(Mob attacker) {
		return 1;
	}

}
