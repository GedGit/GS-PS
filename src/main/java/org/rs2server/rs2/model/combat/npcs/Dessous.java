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
import org.rs2server.rs2.util.Misc;

import java.util.Random;

public class Dessous extends AbstractCombatAction {
	
	/**
	 * The singleton instance.
	 */
	private static final Dessous INSTANCE = new Dessous();
	
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
	public Dessous() {
		
	}
	
	private enum CombatStyle {
		MELEE, MAGIC;
	}
	
	@Override
	public void hit(final Mob attacker, final Mob victim) {
		super.hit(attacker, victim);
		
		if(!attacker.isNPC()) {
			return; //this should be an NPC!
		}
		
		NPC npc = (NPC) attacker;
		Random random = new Random();
		CombatStyle style = random.nextInt(9) <= 4 ? CombatStyle.MELEE : CombatStyle.MAGIC;
		
		switch (style) {
		case MELEE:
			npc.playAnimation(npc.getAttackAnimation());
			int randomHit = Misc.random(damage(19, attacker, victim, AttackType.CRUSH, Skills.ATTACK , Prayers.PROTECT_FROM_MELEE, false, false));
			attacker.getCombatState().setSpellDelay(5);
			attacker.getCombatState().setAttackDelay(4);
			victim.inflictDamage(new Hit(randomHit), attacker);
			smite(attacker, victim, randomHit);
			recoil(attacker, victim, randomHit);
			vengeance(attacker, victim, randomHit, 1);
			victim.getActiveCombatAction().defend(attacker, victim, true);
			break;
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
			int delay = (gfxDelay / 20) - 1;
			attacker.playAnimation(Animation.create(727));
			attacker.playGraphics(Graphic.create(155, 0, 0));
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 156, 45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
			//((MagicCombatAction) this.hitEnemy(attacker, victim, Spell.FIRE_WAVE, Graphic.create(157, gfxDelay, 100), PoisonType.NONE, false, 20, delay, 0);
			attacker.getCombatState().setSpellDelay(5);
			attacker.getCombatState().setAttackDelay(4);
			randomHit = Misc.random(damage(25, attacker, victim, AttackType.MAGIC, Skills.MAGIC , Prayers.PROTECT_FROM_MAGIC, false, false));
			victim.playGraphics(randomHit <= 0 ? Graphic.create(85, gfxDelay, 100) : Graphic.create(157, gfxDelay, 100));
			if (randomHit > 0) {
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
			victim.getActiveCombatAction().defend(attacker, victim, true);
			break;
		}
	}

	@Override
	public int distance(Mob attacker) {
		return 1;
	}

}
