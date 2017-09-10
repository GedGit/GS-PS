package org.rs2server.rs2.model.combat.npcs;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.util.Misc;

public class DharokTheWretched extends AbstractCombatAction {
	
	/**
	 * The singleton instance.
	 */
	private static final DharokTheWretched INSTANCE = new DharokTheWretched();
	
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
		
		int startHp = 100;
		attacker.playAnimation(Animation.create(2066));
		double dharokMultiplier = (startHp - attacker.getSkills().getLevel(Skills.HITPOINTS)) / 2;
		double base = 0;
		base += 1.05D + (double) (attacker.getCombatState().getBonus(10) * 121.77) * 0.00175D;
		base += (double) 121.77 * 0.09D;
		base += dharokMultiplier;
		int finalDamage = victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MELEE) ? 0 : Misc.random((int) base);
		attacker.getCombatState().setAttackDelay(5);
		
		victim.inflictDamage(new Hit(finalDamage), attacker);
		smite(attacker, victim, finalDamage);
		recoil(attacker, victim, finalDamage);
		vengeance(attacker, victim, finalDamage, 1);
	}

	@Override
	public int distance(Mob attacker) {
		return 1;
	}

}
