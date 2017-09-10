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

public class KetZek extends AbstractCombatAction {

	/**
	 * The singleton instance.
	 */
	private static final KetZek INSTANCE = new KetZek();

	/**
	 * Gets the singleton instance.
	 * @return The singleton instance.
	 */
	public static CombatAction getAction() {
		return INSTANCE;
	}

	public KetZek() {
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
		CombatStyle style = attacker.getLocation().distanceToEntity(attacker, victim) <= 1 ? CombatStyle.MELEE : CombatStyle.MAGIC;
		NPC npc = (NPC) attacker;

		int maxHit = style == CombatStyle.MAGIC ? 48 : 54;
		switch (style) {
		case MELEE:
			npc.playAnimation(npc.getAttackAnimation());
			
			World.getWorld().submit(new Tickable(1) {

				@Override
				public void execute() {
					this.stop();
					int randomHit;
					randomHit = Misc.random(damage(maxHit, attacker, victim, AttackType.CRUSH, Skills.ATTACK , Prayers.PROTECT_FROM_MELEE, false, false));
					victim.inflictDamage(new Hit(randomHit), attacker);
					smite(attacker, victim, randomHit);
					recoil(attacker, victim, randomHit);
					vengeance(attacker, victim, randomHit, 1);
				}
				
			});
			break;
		case MAGIC:
			npc.playAnimation(Animation.create(2647));
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
			npc.playProjectile(Projectile.create(npc.getCentreLocation(), victim, 445, 25, 5, clientSpeed, 43, 36, 10, 48));
			World.getWorld().submit(new Tickable(delay) {

				@Override
				public void execute() {
					this.stop();
					int randomHit;
					randomHit = Misc.random(damage(maxHit, attacker, victim, AttackType.MAGIC, Skills.MAGIC , Prayers.PROTECT_FROM_MAGIC, false, false));
					victim.inflictDamage(new Hit(randomHit), attacker);
					victim.playGraphics(Graphic.create(446));
					smite(attacker, victim, randomHit);
					recoil(attacker, victim, randomHit);
					vengeance(attacker, victim, randomHit, 1);
				}

			});
			break;
		}
		victim.getActiveCombatAction().defend(attacker, victim, true);
		npc.getCombatState().setAttackDelay(6);
		npc.getCombatState().setSpellDelay(7);
	}

	@Override
	public int distance(Mob attacker) {
		return 8;
	}

}
