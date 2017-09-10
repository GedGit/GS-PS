package org.rs2server.rs2.model.combat.npcs;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.Projectile;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.combat.CombatState.AttackType;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

public class TzTokJad extends AbstractCombatAction {

	/**
	 * The ranged animation.
	 */
	private static final Animation RANGE_ANIMATION = Animation.create(2652);

	/**
	 * The range gfx.
	 */
	private static final Graphic RANGE_GFX = Graphic.create(451);

	/**
	 * The range end gfx.
	 */
	private static final Graphic RANGE_END_GFX = Graphic.create(157);

	/**
	 * The melee animation.
	 */
	private static final Animation MELEE_ANIMATION = Animation.create(2655);

	/**
	 * The magic animation.
	 */
	private static final Animation MAGIC_ANIMATION = Animation.create(2656);

	/**
	 * The magic gfx.
	 */
	private static final Graphic MAGIC_GFX = Graphic.create(448);

	private enum CombatStyle {
		MELEE,

		MAGIC,

		RANGE
	}

	@Override
	public void hit(final Mob attacker, final Mob victim) {
		super.hit(attacker, victim);

		if(!attacker.isNPC()) {
			return; //this should be an NPC!
		}
		CombatStyle style = CombatStyle.MAGIC;
        CombatStyle[] styles = {CombatStyle.MAGIC, CombatStyle.RANGE, CombatStyle.MELEE};
		NPC npc = (NPC) attacker;
		int maxHit;
        if (attacker.getLocation().distance(victim.getLocation()) <= 2) {
            style = styles[Misc.random(styles.length)];
        } else {
            style = styles[Misc.random(1)];
        }
		style = attacker.getLocation().distance(victim.getLocation()) <= 2 ? CombatStyle.MELEE : Misc.random(9) <= 4 ? CombatStyle.MAGIC : CombatStyle.RANGE;
		int speed = (int) (46 + (attacker.getLocation().distance(victim.getLocation()) * 10));
		switch (style) {
		case MELEE:
			npc.playAnimation(MELEE_ANIMATION);
			maxHit = 98;
			break;
		case RANGE:
			npc.playAnimation(RANGE_ANIMATION);
			World.getWorld().submit(new Tickable(2) {

				@Override
				public void execute() {
					if (getTickDelay() == 3) {
						this.stop();
					}
					if (getTickDelay() == 2) {
						victim.playGraphics(RANGE_GFX);
					}
					this.setTickDelay(getTickDelay() + 1);
				}
				
			});
			maxHit = 97;
			break;
		default:
			npc.playAnimation(MAGIC_ANIMATION);
			maxHit = 97;
			World.getWorld().submit(new Tickable(1) {

				@Override
				public void execute() {
					this.stop();
					npc.playProjectile(Projectile.create(npc.getCentreLocation(), victim, 448, 25, 5, speed, 43, 36, 10, 48));
				}
				
			});
			break;
		}
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
		CombatStyle sty = style;
		if (style == CombatStyle.MELEE) {
			delay = 1;
		}
		World.getWorld().submit(new Tickable(delay) {

			@Override
			public void execute() {
				this.stop();
				victim.getActiveCombatAction().defend(attacker, victim, true);
				int randomHit;
				
				if (sty == CombatStyle.RANGE) {
					victim.playGraphics(RANGE_END_GFX);
				}
				if (sty == CombatStyle.MELEE) {
					randomHit = Misc.random(damage(maxHit, attacker, victim, AttackType.CRUSH, Skills.ATTACK , Prayers.PROTECT_FROM_MELEE, false, false));
				} else if (sty == CombatStyle.RANGE) {
					randomHit = Misc.random(damage(maxHit, attacker, victim, AttackType.RANGE, Skills.RANGE , Prayers.PROTECT_FROM_MISSILES, false, false));
				} else {
					randomHit = Misc.random(damage(maxHit, attacker, victim, AttackType.MAGIC, Skills.MAGIC , Prayers.PROTECT_FROM_MAGIC, false, false));
				}
				victim.inflictDamage(new Hit(randomHit), attacker);
				smite(attacker, victim, randomHit);
				recoil(attacker, victim, randomHit);
				vengeance(attacker, victim, randomHit, 1);
			}
			
		});
		
		npc.getCombatState().setAttackDelay(8);
		npc.getCombatState().setSpellDelay(9);
	}

	@Override
	public int distance(Mob attacker) {
		return 8;
	}

}
