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
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

public class Tekton extends AbstractCombatAction {

	/**
	 * The singleton instance.
	 */
	private static final Tekton INSTANCE = new Tekton();
	
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
	public Tekton() {
		
	}
	
	private enum CombatStyle {
		MELEE,
		MELEE_1
	}
	
	@Override
	public void hit(final Mob attacker, final Mob victim) {
		super.hit(attacker, victim);
		
		if(!attacker.isNPC()) {
			return; //this should be an NPC!
		}
		
		NPC npc = (NPC) attacker;
		
		CombatStyle style = CombatStyle.MELEE;
		
		int maxHit;
		int randomHit;
		int hitDelay;
		boolean blockAnimation;
		final int hit;
		
		switch(Misc.random(3)) {
		case 0:
			style = CombatStyle.MELEE_1;
			break;
		default:
			style = CombatStyle.MELEE;
			break;
		}
		
		switch(style) {
		default:
		case MELEE:
			attacker.playAnimation(Animation.create(7483));
			maxHit = npc.getCombatDefinition().getMaxHit();
			hitDelay = 1;
			
			attacker.playAnimation(npc.getCombatDefinition().getAttack());
			blockAnimation = true;
			
			if(victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MELEE)) {
				maxHit = (int) (maxHit * 0.4);
			}
			randomHit = Misc.random(maxHit);
			if(randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			hit = randomHit;
			break;
		case MELEE_1:
			attacker.playAnimation(Animation.create(7492));
			maxHit = npc.getCombatDefinition().getMaxHit();
			hitDelay = 0;
			
			attacker.playAnimation(npc.getCombatDefinition().getAttack());
			blockAnimation = true;
			
			if(victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MELEE)) {
				maxHit = (int) (maxHit * 0.8);
			}
			randomHit = Misc.random(maxHit);
			if(randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			hit = randomHit;
			break;
		}		
		
		attacker.getCombatState().setAttackDelay(5);
		attacker.getCombatState().setSpellDelay(5);
		final CombatStyle preStyle = style;
		World.getWorld().submit(new Tickable(hitDelay) {
			@Override
			public void execute() {
				if(preStyle != CombatStyle.MELEE) {/*
					victim.playGraphics(Graphic.create(166, 0, 100));*/
				}
				victim.inflictDamage(new Hit(hit), attacker);
				smite(attacker, victim, hit);
				recoil(attacker, victim, hit);
				this.stop();
			}			
		});
		vengeance(attacker, victim, hit, 1);
		
		victim.getActiveCombatAction().defend(attacker, victim, blockAnimation);
	}
	
	@Override
	public int distance(Mob attacker) {
		return 9;
	}
}