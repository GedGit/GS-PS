package org.rs2server.rs2.model.combat.npcs;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.Projectile;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;


/**
 * For the Barrelchest monster
 * @author Canownueasy
 *
 */
public class Barrelchest extends AbstractCombatAction {
	
	/**
	 * The singleton instance.
	 */
	private static final Barrelchest INSTANCE = new Barrelchest();
	
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
		ZIP
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
			style = CombatStyle.ZIP;
			break;
		default:
			style = CombatStyle.MAGIC;
			break;
		}
		
		if(Misc.getDistance(attacker.getLocation(), victim.getLocation()) > 3) {
			style = Misc.random(1) == 1 ? CombatStyle.MAGIC : CombatStyle.ZIP;
		}
		
		int maxHit;
		int randomHit;
		int hitDelay;
		int clientSpeed;
		int gfxDelay;
		boolean blockAnimation;
		final int hit;
		
		switch(style) {
		case ZIP:
			maxHit = 24;
			attacker.playAnimation(Animation.create(5896));
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
			for(final Player pz : World.getWorld().getBarrelchest().players) {
				if(pz != null && pz.getSkills().getLevel(Skills.HITPOINTS) > 0 && pz != victim) {
					int cs = 0;
					int gd = 0;
					if(attacker.getLocation().isWithinDistance(attacker, pz, 1)) {
						cs = 70;
						gd = 80;
					} else if(attacker.getLocation().isWithinDistance(attacker, pz, 5)) {
						cs = 90;
						gfxDelay = 100;
					} else if(attacker.getLocation().isWithinDistance(attacker, pz, 8)) {
						cs = 110;
						gd = 120;
					} else {
						cs = 130;
						gd = 140;
					}
					final int maxHitzz = maxHit;
					attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), pz.getCentreLocation(), 258, 45, 50, cs, 54, 32, pz.getProjectileLockonIndex(), 10, 48));
					World.getWorld().submit(new Tickable(((gd / 20) - 1)) {
						public void execute() {
							if(pz == null || pz.getSkills().getLevel(Skills.HITPOINTS) < 1) {
								this.stop(); return;
							}
							int max = maxHitzz;
							if(pz.getCombatState().getPrayer(Prayers.PROTECT_FROM_MISSILES)) {
								max = (int) (max * 0.4);
							}
							int hitz = Misc.random(max);
							pz.inflictDamage(new Hit(hitz), attacker);
							this.stop();
						}
					});
				}
			}
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
			attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 258, 45, 50, clientSpeed, 54, 32, victim.getProjectileLockonIndex(), 10, 48));
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
			attacker.playAnimation(Animation.create(5895));
			for(final Player pz : World.getWorld().getBarrelchest().players) {
				if(pz != null && pz.getSkills().getLevel(Skills.HITPOINTS) > 0 && pz != victim) {
					final int maxHitz = maxHit;
					pz.getActionSender().sendMessage("Barrelchest's pounding attack shook the whole room!");
					World.getWorld().submit(new Tickable(3) {
						public void execute() {
							if(pz == null || pz.getSkills().getLevel(Skills.HITPOINTS) < 1) {
								this.stop(); return;
							}
							int max = maxHitz;
							if(pz.getCombatState().getPrayer(Prayers.PROTECT_FROM_MAGIC)) {
								max = (int) (max * 0.4);
							}
							int hitz = Misc.random(max);
							pz.inflictDamage(new Hit(hitz), attacker);
							this.stop();
						}
					});
				}
			}
			victim.getActionSender().sendMessage("Barrelchest's pounding attack shook the whole room!");
			hitDelay = 3;
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
			maxHit = 32;
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