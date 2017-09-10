package org.rs2server.rs2.model.combat.npcs;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.util.Misc;

public class GuthanTheInfested extends AbstractCombatAction {
	
	/**
	 * The singleton instance.
	 */
	private static final GuthanTheInfested INSTANCE = new GuthanTheInfested();
	
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
		
		attacker.playAnimation(Animation.create(2080));
		
		int damage = 0;
		
		if (victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MELEE)) {
			damage = 0;
		} else {
			damage = Misc.random(26);
		}
		
		if (!victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MELEE) && Misc.random(7) == 3) {
			victim.playGraphics(Graphic.create(398, 0, 0));
			victim.getActionSender().sendMessage("Guthans heals himself...");
			attacker.getSkills().increaseLevel(Skills.HITPOINTS, damage);
		}
		
		attacker.getCombatState().setAttackDelay(5);
		
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
