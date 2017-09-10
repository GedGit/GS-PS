package org.rs2server.rs2.model.combat.npcs;

import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.CombatState;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

/**
 * Created by Tim on 12/7/2015.
 */
public class Snakeling extends AbstractCombatAction {


    /**
     * The singleton instance.
     */
    private static final Snakeling INSTANCE = new Snakeling();

    /**
     * Gets the singleton instance.
     * @return The singleton instance.
     */
    public static CombatAction getAction() {
        return INSTANCE;
    }

    @Override
    public void hit(final Mob attacker, final Mob victim) {
        super.hit(attacker, victim);

        if (!attacker.isNPC()) {
            return; //this should be an NPC!
        }
        attacker.playAnimation(attacker.getAttackAnimation());
        int damage = 0;
        damage = Misc.random(damage(12, attacker, victim, CombatState.AttackType.CRUSH, Skills.ATTACK, Prayers.PROTECT_FROM_MELEE, false, false));

        final int hit = damage;
        World.getWorld().submit(new Tickable(1) {

            @Override
            public void execute() {
                this.stop();
                victim.inflictDamage(new Hit(hit), attacker);
                smite(attacker, victim, hit);
                recoil(attacker, victim, hit);
                vengeance(attacker, victim, hit, 1);
            }
        });

        victim.getActiveCombatAction().defend(attacker, victim, true);
    }

    @Override
    public int distance(Mob attacker) {
        return 1;
    }
}
