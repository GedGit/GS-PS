package org.rs2server.rs2.model.combat.npcs;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.CombatState.AttackType;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.tickable.Tickable;

import java.util.Random;


/**
 * Monk of Zamorak combat instance
 * @author Michael Bull
 *
 */
public class MonkOfZamorak extends AbstractCombatAction {
	
	/**
	 * The singleton instance.
	 */
	private static final MonkOfZamorak INSTANCE = new MonkOfZamorak();
	
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
	public MonkOfZamorak() {
		
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
			Animation anim = attacker.getAttackAnimation();
			attacker.playAnimation(anim);
			
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
			attacker.playAnimation(Animation.create(811));
			victim.playGraphics(Graphic.create(78, 60));

			hitDelay = 2;
			blockAnimation = false;
			maxHit = 8;
			damage = damage(maxHit, attacker, victim, AttackType.MAGIC, Skills.MAGIC , Prayers.PROTECT_FROM_MAGIC, false, false);
			randomHit = random.nextInt(damage < 1 ? 1 : damage + 1);
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
