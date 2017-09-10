package org.rs2server.rs2.model.combat.npcs;

import org.rs2server.rs2.content.misc.DragonfireShield;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.CombatFormula;
import org.rs2server.rs2.model.combat.CombatState.AttackType;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

import java.util.Random;


/**
 * Blue Dragon combat instance
 *
 * @author Michael Bull
 */
public class LavaDragon extends AbstractCombatAction {

    /**
     * The singleton instance.
     */
    private static final LavaDragon INSTANCE = new LavaDragon();

    /**
     * Gets the singleton instance.
     *
     * @return The singleton instance.
     */
    public static CombatAction getAction() {
        return INSTANCE;
    }

    /**
     * The random number generator.
     */
    private final Random random = new Random();

    /**
     * Default private constructor.
     */
    public LavaDragon() {

    }

    private enum CombatStyle {
        MELEE,

        MAGIC
    }

    @Override
    public void hit(final Mob attacker, final Mob victim) {
        super.hit(attacker, victim);

        if (!attacker.isNPC()) {
            return; //this should be an NPC!
        }

        NPC npc = (NPC) attacker;

        CombatStyle style = CombatStyle.MAGIC;

        int maxHit = 50;
        int damage;
        int randomHit;
        int hitDelay;
        boolean blockAnimation;
        final int hit;

        if (attacker.getLocation().isWithinDistance(attacker, victim, 2)) {
            switch (random.nextInt(3)) {
                case 0:
                case 1:
                case 2:
                    style = CombatStyle.MELEE;
                    break;
                case 3:
                    style = CombatStyle.MAGIC;
                    break;
            }
        }

        switch (style) {
            case MELEE:
                Animation anim = attacker.getAttackAnimation();
                if (random.nextInt(2) == 1) {
                    anim = Animation.create(91);
                }
                attacker.playAnimation(anim);
                blockAnimation = true;
                hitDelay = 1;
                maxHit = npc.getCombatDefinition().getMaxHit();
                damage = damage(maxHit, attacker, victim, attacker.getCombatState().getAttackType(), Skills.ATTACK, Prayers.PROTECT_FROM_MELEE, false, false);
                randomHit = random.nextInt(damage < 1 ? 1 : damage + 1);
                if (randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
                    randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
                }
                hit = randomHit;
                break;
            default:
            case MAGIC:
                attacker.playAnimation(Animation.create(81));
                attacker.playGraphics(Graphic.create(1, 0, 100));
                hitDelay = 2;
                blockAnimation = false;
                if (victim instanceof Player) {
                    if (((Player) victim).dfsCharges < 50) {
                        DragonfireShield.charge((Player) victim);
                    }
                }
                maxHit = 50;

                //System.out.println(dragonfireReduction + ", " + randomHit);
                break;
        }
        attacker.getCombatState().setAttackDelay(5);
        attacker.getCombatState().setSpellDelay(5);
        CombatStyle styleUsed = style;
        int finalMaxHit = maxHit;
        World.getWorld().submit(new Tickable(hitDelay) {
            @Override
            public void execute() {
                int damage = getDamage(attacker, victim, styleUsed, finalMaxHit);
                victim.inflictDamage(new Hit(damage), attacker);
                smite(attacker, victim, damage);
                recoil(attacker, victim, damage);

                vengeance(attacker, victim, damage, 1);
                this.stop();
            }
        });

        victim.getActiveCombatAction().defend(attacker, victim, blockAnimation);
    }

    public int getDamage(Mob attacker, Mob victim, CombatStyle style, int maxHit) {
        if (style == CombatStyle.MAGIC) {
            int finalRandom = damage(maxHit, attacker, victim, AttackType.MAGIC, Skills.MAGIC, Prayers.PROTECT_FROM_MAGIC, false, true);
            finalRandom = random.nextInt(finalRandom < 1 ? 1 : finalRandom + 1);
            if (finalRandom > victim.getSkills().getLevel(Skills.HITPOINTS)) {
                finalRandom = victim.getSkills().getLevel(Skills.HITPOINTS);
            }
            double dragonfireReduction = CombatFormula.dragonfireReduction(victim);
            if (dragonfireReduction > 0) {
                finalRandom -= (finalRandom * dragonfireReduction);
                if (finalRandom < 0) {
                    finalRandom = 0;
                }
            }
            return finalRandom;
        } else {
            return Misc.random(damage(maxHit, attacker, victim, AttackType.CRUSH, Skills.ATTACK, Prayers.PROTECT_FROM_MELEE, false, false));
        }
    }

    @Override
    public int distance(Mob attacker) {
        return 2;
    }
}
