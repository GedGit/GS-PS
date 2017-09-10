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


/**
 * General Khazard
 * @author Canownueasy
 *
 */
public class GeneralKhazard extends AbstractCombatAction {
	
	/**
	 * The singleton instance.
	 */
	private static final GeneralKhazard INSTANCE = new GeneralKhazard();
	
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
	public GeneralKhazard() {
		
	}
	
	private enum CombatStyle {
		MELEE,
		MAGIC
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
			style = CombatStyle.MAGIC;
			break;
		default:
			style = CombatStyle.MELEE;
			break;
		}
		
		if(Misc.getDistance(attacker.getLocation(), victim.getLocation()) > 1) {
			style = CombatStyle.MAGIC;
		}
		
		switch(style) {
		default:
		case MELEE:
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
		case MAGIC:
			maxHit = 36;
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
			hitDelay = (gfxDelay / 20) - 1;
			
			attacker.playAnimation(Animation.create(2890));
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 165, 45, 50, clientSpeed, 70, 35, victim.getProjectileLockonIndex(), 10, 48));

			blockAnimation = false;
			if(victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MAGIC)) {
				maxHit = (int) (maxHit * 0.4);
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
				if(preStyle != CombatStyle.MELEE) {
					victim.playGraphics(Graphic.create(166, 0, 100));
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
		return 5;
	}
}