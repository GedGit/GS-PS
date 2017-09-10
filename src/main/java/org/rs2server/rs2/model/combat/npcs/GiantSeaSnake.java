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
 * For the giant sea snake
 * @author Canownueasy
 *
 */
public class GiantSeaSnake extends AbstractCombatAction {
	
	/**
	 * The singleton instance.
	 */
	private static final GiantSeaSnake INSTANCE = new GiantSeaSnake();
	
	/**
	 * Gets the singleton instance.
	 * @return The singleton instance.
	 */
	public static CombatAction getAction() {
		return INSTANCE;
	}
	
	private enum CombatStyle {
		MELEE,
		MAGIC,
		POISON
	}
	
	@Override
	public void hit(final Mob attacker, final Mob victim) {
		super.hit(attacker, victim);
		
		if(!attacker.isNPC()) {
			return; //this should be an NPC!
		}
		
		NPC npc = (NPC) attacker;
		
		CombatStyle style = CombatStyle.MAGIC;
		
		switch(Misc.random(2)) {
		case 0:
			style = CombatStyle.MELEE;
			break;
		case 1:
			style = CombatStyle.POISON;
			break;
		default:
			style = CombatStyle.MAGIC;
			break;
		}
		
		if(Misc.getDistance(attacker.getLocation(), victim.getLocation()) > 3) {
			style = Misc.random(1) == 1 ? CombatStyle.MAGIC : CombatStyle.POISON;
		}
		
		int maxHit;
		int randomHit;
		int hitDelay;
		int clientSpeed;
		int gfxDelay;
		boolean blockAnimation;
		final int hit;
		
		switch(style) {
		case POISON:
			maxHit = 26;
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
			
			attacker.playAnimation(npc.getCombatDefinition().getAttack());
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 270, 45, 50, clientSpeed, 0, 32, victim.getProjectileLockonIndex(), 10, 48));
			blockAnimation = false;
			if(victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MISSILES)) {
				maxHit = (int) (maxHit * 0.4);
			}
			randomHit = Misc.random(maxHit);
			if(randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			hit = randomHit;
			break;
		default:
		case MAGIC:
			maxHit = npc.getCombatDefinition().getMaxHit();
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
			
			attacker.playAnimation(npc.getCombatDefinition().getAttack());
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 268, 45, 50, clientSpeed, 0, 32, victim.getProjectileLockonIndex(), 10, 48));
			victim.playGraphics(Graphic.create(269, gfxDelay, 100));
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
		case MELEE:
			attacker.playAnimation(npc.getCombatDefinition().getAttack());
			blockAnimation = true;
			hitDelay = 1;
			maxHit = 14;
			if(victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MELEE)) {
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
		
		World.getWorld().submit(new Tickable(hitDelay) {
			@Override
			public void execute() {
				victim.inflictDamage(new Hit(hit), attacker);
				smite(attacker, victim, hit);
				recoil(attacker, victim, hit);
				vengeance(attacker, victim, hit, 1);
				this.stop();
			}			
		});
		
		victim.getActiveCombatAction().defend(attacker, victim, blockAnimation);
	}
	
	@Override
	public int distance(Mob attacker) {
		return 5;
	}
}