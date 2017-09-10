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
import org.rs2server.rs2.model.combat.CombatState.AttackType;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.tickable.Tickable;

import java.util.Random;


/**
 * Blue Dragon combat instance
 * @author Michael Bull
 *
 */
public class SkeletonFremennik extends AbstractCombatAction {
	
	/**
	 * The singleton instance.
	 */
	private static final SkeletonFremennik INSTANCE = new SkeletonFremennik();
	
	/**
	 * Gets the singleton instance.
	 * @return The singleton instance.
	 */
	public static CombatAction getAction() {
		return INSTANCE;
	}
	
	/**
	 * The random number generator.
	 */
	private final Random random = new Random();
	
	/**
	 * Default private constructor.
	 */
	public SkeletonFremennik() {
		
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
		
		CombatStyle style = CombatStyle.MAGIC;
		
		int maxHit;
		int damage;
		int randomHit;
		int hitDelay;
		boolean blockAnimation;
		final int hit;

		if(attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
			switch(random.nextInt(2)) {
			case 0:
				style = CombatStyle.MELEE;	
				break;
			case 1:
				style = CombatStyle.MAGIC;
				break;
			}
		}
		
		switch(style) {
		case MELEE:
			attacker.playAnimation(attacker.getAttackAnimation());
			
			hitDelay = 1;
			blockAnimation = true;
			maxHit = npc.getCombatDefinition().getMaxHit();
			damage = damage(maxHit, attacker, victim, attacker.getCombatState().getAttackType(), Skills.ATTACK , Prayers.PROTECT_FROM_MELEE, false, false);
			randomHit = random.nextInt(damage < 1 ? 1 : damage + 1);
			if(randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			hit = randomHit;
			break;
		default:
		case MAGIC:
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
			
			attacker.playAnimation(Animation.create(1167));
			attacker.playGraphics(Graphic.create(164, 0, 100));
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 165, 45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
			victim.playGraphics(Graphic.create(166, gfxDelay, 100));

			blockAnimation = false;
			maxHit = (npc.getSkills().getCombatLevel() / 10) + 2;
			damage = damage(maxHit, attacker, victim, AttackType.MAGIC, Skills.MAGIC , Prayers.PROTECT_FROM_MAGIC, false, true); //these ignore prayers
			randomHit = random.nextInt(damage < 1 ? 1 : damage + 1);
			if(randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			hit = randomHit;
			break;
		}		
		
		attacker.getCombatState().setAttackDelay(4);
		attacker.getCombatState().setSpellDelay(4);
		
		World.getWorld().submit(new Tickable(hitDelay) {
			@Override
			public void execute() {
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
