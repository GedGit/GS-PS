package org.rs2server.rs2.model.combat.npcs;

import org.rs2server.rs2.content.misc.DragonfireShield;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.CombatFormula;
import org.rs2server.rs2.model.combat.CombatState;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

import java.util.Random;

/**
 * Created by Tim on 12/11/2015.
 */
public class SkeletalWyvern extends AbstractCombatAction {

    /**
     * The singleton instance.
     */
    private static final SkeletalWyvern INSTANCE = new SkeletalWyvern();

    /**
     * Gets the singleton instance.
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
    public SkeletalWyvern() {

    }

    private enum CombatStyle {
        MELEE,

        ICY_BREATH
    }

    @Override
    public void hit(final Mob attacker, final Mob victim) {
        super.hit(attacker, victim);

        if (!attacker.isNPC()) {
            return; //this should be an NPC!
        }

        NPC npc = (NPC) attacker;

        CombatStyle style = CombatStyle.ICY_BREATH;

        int maxHit;
        int damage;
        int randomHit;
        int hitDelay;
        boolean blockAnimation;
        final int hit;
        boolean projectile = attacker.getLocation().distance(victim.getLocation()) > 1;
        if(attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
            switch(random.nextInt(3)) {
                case 0:
                case 1:
                    style = CombatStyle.MELEE;
                    break;
                case 2:
                    style = CombatStyle.ICY_BREATH;
                    break;
            }
        }
        switch (style) {
            case MELEE:
                attacker.playAnimation(attacker.getAttackAnimation());

                hitDelay = 1;
                blockAnimation = true;
                maxHit = npc.getCombatDefinition().getMaxHit();
                damage = damage(maxHit, attacker, victim, attacker.getCombatState().getAttackType(), Skills.ATTACK , Prayers.PROTECT_FROM_MELEE, false, false);
                randomHit = random.nextInt(damage < 1 ? 1 : damage + 1);
                if(randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
                    randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
                }
                hit = randomHit;
                break;
            default:
            case ICY_BREATH:
                int clientSpeed;
                int gfxDelay;
                blockAnimation = false;
                hitDelay = 1;
                attacker.playAnimation(attacker.getAttackAnimation());
                attacker.playGraphics(Graphic.create(499, 0, 100));
                if (projectile) {
                    if (attacker.getLocation().isWithinDistance(attacker, victim, 1)) {
                        clientSpeed = 50;
                        gfxDelay = 60;
                    } else if (attacker.getLocation().isWithinDistance(attacker, victim, 5)) {
                        clientSpeed = 70;
                        gfxDelay = 80;
                    } else if (attacker.getLocation().isWithinDistance(attacker, victim, 8)) {
                        clientSpeed = 90;
                        gfxDelay = 100;
                    } else {
                        clientSpeed = 110;
                        gfxDelay = 120;
                    }
                    hitDelay = (gfxDelay / 20) - 1;
                    attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), 500, 45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
                }
                if(victim instanceof Player) {
                    if(((Player) victim).dfsCharges < 50) {
                        DragonfireShield.charge((Player) victim);
                    }
                }
                maxHit = 50;
                damage = damage(maxHit, attacker, victim, CombatState.AttackType.MAGIC, Skills.MAGIC , Prayers.PROTECT_FROM_MAGIC, false, true);
                randomHit = random.nextInt(damage < 1 ? 1 : damage + 1);
                if(randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
                    randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
                }
                double dragonfireReduction = CombatFormula.dragonfireReduction(victim);
                if(dragonfireReduction > 0) {
                    randomHit -= (randomHit * dragonfireReduction);
                    if(randomHit < 0) {
                        randomHit = 0;
                    }
                }
                hit = randomHit;
                break;
        }
        attacker.getCombatState().setAttackDelay(6);
        attacker.getCombatState().setSpellDelay(6);

        World.getWorld().submit(new Tickable(hitDelay) {
            @Override
            public void execute() {
                if (projectile) {
                    victim.playGraphics(Graphic.create(502, 0, 100));
                    if (victim.getCombatState().canMove() && hit > 0 && Misc.random(3) == 1) {
                        victim.getCombatState().setCanMove(false);
                        World.getWorld().submit(new Tickable(15) {

                            @Override
                            public void execute() {
                                this.stop();
                                victim.getCombatState().setCanMove(true);
                            }
                        });
                    }
                }
                victim.inflictDamage(new Hit(hit), attacker);
                smite(attacker, victim, hit);
                recoil(attacker, victim, hit);
                this.stop();
            }
        });
        vengeance(attacker, victim, hit, 1);

        victim.getActiveCombatAction().defend(attacker, victim, blockAnimation);
    }

    @Override
    public int distance(Mob attacker) {
        return 4;
    }
}
