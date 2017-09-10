package org.rs2server.rs2.model.combat.npcs;

import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.CombatState.AttackType;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.util.Misc;

import java.util.Random;

public class Damis extends AbstractCombatAction {
	
	/**
	 * The singleton instance.
	 */
	private static final Damis INSTANCE = new Damis();
	
	/**
	 * Gets the singleton instance.
	 * @return The singleton instance.
	 */
	public static CombatAction getAction() {
		return INSTANCE;
	}
	
	/**
	 * Default private constructor.
	 */
	public Damis() {
	}
	
	@Override
	public void hit(final Mob attacker, final Mob victim) {
		super.hit(attacker, victim);
		
		if(!attacker.isNPC()) {
			return; //this should be an NPC!
		}
		
		NPC npc = (NPC) attacker;
		Random random = new Random();
		
		npc.playAnimation(npc.getAttackAnimation());
		
		boolean drainPrayer = random.nextInt(10) >= 4;
		
		if (drainPrayer) {
			victim.getSkills().decreaseLevel(Skills.PRAYER, Misc.random(3, 6));
		}
		int randomHit = Misc.random(damage(29, attacker, victim, AttackType.CRUSH, Skills.ATTACK , Prayers.PROTECT_FROM_MELEE, false, false));
		attacker.getCombatState().setSpellDelay(5);
		attacker.getCombatState().setAttackDelay(4);
		victim.inflictDamage(new Hit(randomHit), attacker);
		smite(attacker, victim, randomHit);
		recoil(attacker, victim, randomHit);
		vengeance(attacker, victim, randomHit, 1);
		victim.getActiveCombatAction().defend(attacker, victim, true);
	}

	@Override
	public int distance(Mob attacker) {
		return 1;
	}
}
