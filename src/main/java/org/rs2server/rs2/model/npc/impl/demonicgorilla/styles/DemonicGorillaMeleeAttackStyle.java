package org.rs2server.rs2.model.npc.impl.demonicgorilla.styles;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Prayers;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.CombatState;
import org.rs2server.rs2.model.combat.impl.MeleeCombatAction;
import org.rs2server.rs2.model.npc.impl.NpcCombatState;
import org.rs2server.rs2.model.npc.impl.demonicgorilla.DemonicGorilla;
import org.rs2server.rs2.tickable.impl.StoppingTick;
import org.rs2server.rs2.util.Misc;

public class DemonicGorillaMeleeAttackStyle<T extends DemonicGorilla> extends NpcCombatState<T> {
	
	public static final Animation ATTACK_ANIMATION = Animation.create(7226);

    private static final int MAX_HIT = 30;
    public DemonicGorillaMeleeAttackStyle(T npc) {
        super(npc);
    }

    public void perform() {
		if (npc.isDestroyed() || npc.getSkills().getLevel(Skills.HITPOINTS) <= 0) {
			return;
		}
        npc.playAnimation(ATTACK_ANIMATION);
        Mob challenger = npc.getChallenger();
        CombatAction activeCombatAction = challenger.getActiveCombatAction();
        npc.getCombatState().setAttackDelay(8);
        World.getWorld().submit(new StoppingTick(1) {
            @Override
            public void executeAndStop() {
                int damage = Misc.random(MeleeCombatAction.getAction().damage(MAX_HIT, npc, challenger, CombatState.AttackType.SLASH, Skills.ATTACK, Prayers.PROTECT_FROM_MELEE, false, false));
                challenger.inflictDamage(new Hit(damage), npc);
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
        return DemonicGorilla.NPC_ID;
    }
}
