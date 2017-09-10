package org.rs2server.rs2.model.npc.impl.demonicgorilla.styles;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.CombatState;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction;
import org.rs2server.rs2.model.npc.impl.NpcCombatState;
import org.rs2server.rs2.model.npc.impl.demonicgorilla.DemonicGorilla;
import org.rs2server.rs2.tickable.impl.StoppingTick;
import org.rs2server.rs2.util.Misc;

public class DemonicGorillaMagicAttackStyle<T extends DemonicGorilla> extends NpcCombatState<T> {
	
	public static final Animation ATTACK_ANIMATION = Animation.create(7238);

    private static final int MAX_HIT = 30;
    private static final int MAGIC_PROJECTILE_ID = 1305;
    private static final int MAGIC_IMPACT_GRAPHIC = 1304;

    public DemonicGorillaMagicAttackStyle(T npc) {
        super(npc);
    }

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
			challenger.getActionSender().sendProjectile(npc.getCentreLocation(), challenger.getCentreLocation(), MAGIC_PROJECTILE_ID, 45, 50, clientSpeed, 43, 35, 10, 48, challenger.getProjectileLockonIndex());
		}
		npc.getCombatState().setAttackDelay(8);
		World.getWorld().submit(new StoppingTick(delay) {
            @Override
            public void executeAndStop() {
                int damage = MagicCombatAction.getAction().damage(MAX_HIT, npc, challenger, CombatState.AttackType.MAGIC, Skills.MAGIC, Prayers.PROTECT_FROM_MAGIC, false, false);
				if (damage < 0) {
					damage = 0;
				}
				damage = Misc.random(damage);
                challenger.inflictDamage(new Hit(damage), npc);
                challenger.playGraphics(Graphic.create(MAGIC_IMPACT_GRAPHIC));
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

    public int getId() {
        return DemonicGorilla.NPC_ID;
    }
}
