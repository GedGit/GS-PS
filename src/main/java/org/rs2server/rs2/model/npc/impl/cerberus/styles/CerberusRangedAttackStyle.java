package org.rs2server.rs2.model.npc.impl.cerberus.styles;

import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.CombatState;
import org.rs2server.rs2.model.combat.impl.RangeCombatAction;
import org.rs2server.rs2.model.npc.impl.NpcCombatState;
import org.rs2server.rs2.model.npc.impl.cerberus.Cerberus;
import org.rs2server.rs2.tickable.impl.StoppingTick;
import org.rs2server.rs2.util.Misc;

/**
 * @author twelve
 */
public class CerberusRangedAttackStyle<T extends Cerberus> extends NpcCombatState<T> {

    public static final Animation ATTACK_ANIMATION = Animation.create(4490);

    private static final int MAX_HIT = 23;
    private static final int RANGED_PROJECTILE_ID = 1245;
    private static final int RANGED_IMPACT_GRAPHIC = 1244;

    public CerberusRangedAttackStyle(T npc) {
        super(npc);
    }

    @Override
    public void perform() {
		if (npc.isDestroyed() || npc.getSkills().getLevel(Skills.HITPOINTS) <= 0) {
			return;
		}
        npc.playAnimation(ATTACK_ANIMATION);
        Mob challenger = npc.getChallenger();
        CombatAction activeCombatAction = challenger.getActiveCombatAction();

        int clientSpeed;
        int gfxDelay;
        double distance = npc.getLocation().getDistance(challenger.getLocation());
		if (distance <= 1) {
			clientSpeed = 50;
			gfxDelay = 60;
		} else if (distance <= 5) {
			clientSpeed = 70;
			gfxDelay = 80;
		} else if (distance <= 8) {
			clientSpeed = 90;
			gfxDelay = 100;
		} else {
			clientSpeed = 110;
			gfxDelay = 120;
		}
        int delay = (gfxDelay / 20) - 1;
		if (challenger.getActionSender() != null) {
			challenger.getActionSender().sendProjectile(npc.getCentreLocation(), challenger.getCentreLocation(), RANGED_PROJECTILE_ID, 45, 50, clientSpeed, 43, 35, 10, 48, challenger.getProjectileLockonIndex());
		}
		npc.getCombatState().setAttackDelay(6);
		World.getWorld().submit(new StoppingTick(delay) {
            @Override
            public void executeAndStop() {
                int damage = Misc.random(RangeCombatAction.getAction().damage(MAX_HIT, npc, challenger, CombatState.AttackType.RANGE, Skills.RANGE, Prayers.PROTECT_FROM_MISSILES, false, false));
				challenger.inflictDamage(new Hit(damage), npc);
                challenger.playGraphics(Graphic.create(RANGED_IMPACT_GRAPHIC));
				challenger.getCombatState().setLastHitTimer(10000);
				challenger.getCombatState().setLastHitBy(npc);
				challenger.getCombatState().updateCombatWith(npc);
				challenger.getActionQueue().clearRemovableActions();
                activeCombatAction.smite(npc, challenger, damage);
                activeCombatAction.smite(npc, challenger, damage);
                activeCombatAction.vengeance(npc, challenger, damage, 1);
				npc.incrementPerformedAttacks();
				activeCombatAction.defend(npc, challenger, true);
            }
        });
    }

    @Override
    public int getId() {
        return Cerberus.NPC_ID;
    }
}
