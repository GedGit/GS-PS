package org.rs2server.rs2.model.combat.npcs;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;


/**
 * Sea Troll Queen
 * @author Canownueasy
 *
 */
public class GiantMole extends AbstractCombatAction {
	
	/**
	 * The singleton instance.
	 */
	private static final GiantMole INSTANCE = new GiantMole();
	
	/**
	 * Gets the singleton instance.
	 * @return The singleton instance.
	 */
	public static CombatAction getAction() {
		return INSTANCE;
	}
	
	private enum CombatStyle {
		MELEE,
		DIG
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
		
		switch(Misc.random(4)) {
		default:
			style = CombatStyle.MELEE;
			break;
		case 4:
			style = CombatStyle.DIG;
			break;
		}
		
		if(attacker.getSkills().getLevel(Skills.HITPOINTS) > 100) {
			style = CombatStyle.MELEE;
		}
		
		switch(style) {
		default:
		case MELEE:
			maxHit = npc.getCombatDefinition().getMaxHit();
			blockAnimation = true;
			hitDelay = 1;
			attacker.playAnimation(npc.getCombatDefinition().getAttack());
			if(victim.getCombatState().getPrayer(Prayers.PROTECT_FROM_MELEE)) {
				maxHit = (int) (maxHit * 0.2);
			}
			randomHit = Misc.random(maxHit);
			if(randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
				randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
			}
			hit = randomHit;
			break;
		case DIG:
			hitDelay = 0;
			hit = 0;
			blockAnimation = false;
			attacker.setCanBeDamaged(false);
			break;
		}		
		
		attacker.getCombatState().setAttackDelay(5);
		attacker.getCombatState().setSpellDelay(5);
		
		if(style == CombatStyle.DIG) {
			for(Player ppl : victim.getRegion().getPlayers()) {
				if(ppl != null) {
					ppl.getActionSender().sendMessage("The mole digs underground!");	
				}
			}
			attacker.playAnimation(Animation.create(3314));
			World.getWorld().submit(new Tickable(2) {
				public void execute() {
					attacker.setTeleportTarget(random());
					attacker.playAnimation(Animation.create(3315));
					attacker.setCanBeDamaged(true);
					this.stop();
				}
			});
			return;
		}
		World.getWorld().submit(new Tickable(hitDelay) {
			@Override
			public void execute() {
				vengeance(attacker, victim, hit, 1);
				victim.inflictDamage(new Hit(hit), attacker);
				smite(attacker, victim, hit);
				recoil(attacker, victim, hit);
				this.stop();
			}			
		});
		
		victim.getActiveCombatAction().defend(attacker, victim, blockAnimation);
	}
	
	private Location random() {
		Location locs[] = { Location.create(1778, 5237), Location.create(1761, 5186),
				Location.create(1737, 5709), Location.create(1737, 5227)};
        return locs[(int)(Math.random()*locs.length)];
	}
	
	@Override
	public int distance(Mob attacker) {
		return 5;
	}
}