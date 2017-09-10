package org.rs2server.rs2.model.combat.npcs;

import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.CombatState;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

/**
 * Created by Tim on 12/19/2015.
 */
public class DarkBeast extends AbstractCombatAction {

    /**
     * The singleton instance.
     */
    private static final DarkBeast INSTANCE = new DarkBeast();

    /**
     * Gets the singleton instance.
     *
     * @return The singleton instance.
     */
    public static CombatAction getAction() {
        return INSTANCE;
    }

    private enum CombatStyle {
        MELEE, MAGIC
    }

    @Override
    public void hit(final Mob attacker, final Mob victim) {
        super.hit(attacker, victim);

        if (!attacker.isNPC()) {
            return; //this should be an NPC!
        }
        CombatStyle style = CombatStyle.MAGIC;
        if (attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
            style = CombatStyle.MELEE;
        }
        int damage;
        int gfxDelay;
        int clientSpeed;
        if (attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
            clientSpeed = 70;
            gfxDelay = 80;
        } else if (attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
            clientSpeed = 90;
            gfxDelay = 100;
        } else if (attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
            clientSpeed = 110;
            gfxDelay = 120;
        } else {
            clientSpeed = 130;
            gfxDelay = 140;
        }
        int delay = (gfxDelay / 20) - 1;
        switch (style) {
            case MELEE:
                attacker.playAnimation(attacker.getAttackAnimation());
                damage = Misc.random(damage(17, attacker, victim, CombatState.AttackType.CRUSH, Skills.ATTACK, Prayers.PROTECT_FROM_MELEE, false, false));
                delay = 1;
                break;
            default:
                attacker.playAnimation(attacker.getAttackAnimation());
                attacker.playGraphics(Graphic.create(155, 0, 100));
                attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 156, 45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
                damage = Misc.random(damage(8, attacker, victim, CombatState.AttackType.MAGIC, Skills.MAGIC, Prayers.PROTECT_FROM_MAGIC, false, false));
                break;
        }
        final CombatStyle endStyle = style;
        World.getWorld().submit(new Tickable(delay) {

            @Override
            public void execute() {
                this.stop();
                if (endStyle == CombatStyle.MAGIC) {
                    victim.playGraphics(damage <= 0 ? Graphic.create(85, gfxDelay, 100) : Graphic.create(157, gfxDelay, 100));
                }
                victim.inflictDamage(new Hit(damage), attacker);
                smite(attacker, victim, damage);
                recoil(attacker, victim, damage);
            }

        });
        vengeance(attacker, victim, damage, 1);

        victim.getActiveCombatAction().defend(attacker, victim, false);

        attacker.getCombatState().setSpellDelay(6);
        attacker.getCombatState().setAttackDelay(5);
    }


    @Override
    public int distance(Mob attacker) {
        return 4;
    }
}
