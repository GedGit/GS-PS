package org.rs2server.rs2.model.combat.npcs;

import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.CombatState;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

/** 
 * @author Script
 *	<Rune-Server> CamelCrusher
 */
public class Venenatis extends AbstractCombatAction {

    /**
     * The singleton instance.
     */
    private static final Venenatis INSTANCE = new Venenatis();

    private static final Animation MELEE_ANIMATION = Animation.create(5327);

    private static final Animation MAGIC_ANIMATION = Animation.create(5330);

    private static final Graphic EARTH_BLAST_START = Graphic.create(164, 0, 100);

    private static final Graphic ENEMY_HIT_GFX = Graphic.create(166, 0, 100);

    private static final Graphic ENTANGLE_START = Graphic.create(177, 0, 100);

    private static final Graphic ENTANGLE_END = Graphic.create(179, 0, 100);

    private static final Graphic PRAYER_DRAIN_END = Graphic.create(172, 0, 100);

    private static final int ENTANGLE_PROJECTILE = 178;

    private static final int EARTH_BLAST_PROJECTILE = 165;

    /**
     * Gets the singleton instance.
     *
     * @return The singleton instance.
     */
    public static CombatAction getAction() {
        return INSTANCE;
    }


    /**
     * Default private constructor.
     */
    public Venenatis() {

    }

    private enum CombatStyle {
        MAGIC, MELEE, DRAIN_PRAYER, ENTANGLE, WEB
    }


    @Override
    public void hit(final Mob attacker, final Mob victim) {
        super.hit(attacker, victim);

        if (!attacker.isNPC()) {
            return; //this should be an NPC!
        }
        CombatStyle style = CombatStyle.MELEE;


        int maxHit = 50;
        int randomHit;
        int hitDelay;
        boolean blockAnimation = false;
        final int hit;
        int clientSpeed;
        int gfxDelay;
        int preHit = 0;
        int attackDelay = 6;
        int spellDelay = 7;
        if (attacker.getLocation().isWithinDistance(attacker, victim, 2)) {
            switch (Misc.random(10)) {
                case 4:
                case 5:
                case 6:
                    style = CombatStyle.MAGIC;
                    break;
                case 7:
                    style = CombatStyle.DRAIN_PRAYER;
                    break;
                case 8:
                    style = CombatStyle.ENTANGLE;
                    break;
                case 9:
                    style = CombatStyle.WEB;
                    break;
            }
        } else {
            switch (Misc.random(10)) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 10:
                    style = CombatStyle.MAGIC;
                    break;
                case 7:
                    style = CombatStyle.DRAIN_PRAYER;
                    break;
                case 8:
                    style = CombatStyle.ENTANGLE;
                    break;
                case 9:
                    style = CombatStyle.WEB;
                    break;
            }
        }
        switch (style) {
            case MELEE:
                attacker.playAnimation(MELEE_ANIMATION);
                randomHit = damage(maxHit, attacker, victim, CombatState.AttackType.STAB, Skills.ATTACK, Prayers.PROTECT_FROM_MELEE, false, false);
                randomHit = random.nextInt(randomHit < 1 ? 1 : randomHit + 1);
                if(randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
                    randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
                }
                preHit = randomHit;
                hitDelay = 1;
                break;
            case MAGIC:
                attacker.playAnimation(MAGIC_ANIMATION);
                attacker.playGraphics(EARTH_BLAST_START);
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
                hitDelay = (gfxDelay / 20) - 1;
                attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), EARTH_BLAST_PROJECTILE, 45, 50, clientSpeed, 35, 35, victim.getProjectileLockonIndex(), 10, 48));
                randomHit = damage(maxHit, attacker, victim, CombatState.AttackType.MAGIC, Skills.MAGIC, Prayers.PROTECT_FROM_MAGIC, false, false);
                randomHit = random.nextInt(randomHit < 1 ? 1 : randomHit + 1);
                if(randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
                    randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
                }
                preHit = randomHit;
                break;
            case DRAIN_PRAYER:
                attacker.playAnimation(MELEE_ANIMATION);
                preHit = 0;
                attackDelay = 3;
                spellDelay = 4;
                hitDelay = 1;
                break;
            case ENTANGLE:
                attacker.playAnimation(MAGIC_ANIMATION);
                attacker.playGraphics(ENTANGLE_START);
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
                hitDelay = (gfxDelay / 20) - 1;
                randomHit = damage(1, attacker, victim, CombatState.AttackType.MAGIC, Skills.MAGIC, Prayers.PROTECT_FROM_MAGIC, false, false);
                randomHit = random.nextInt(randomHit < 1 ? 1 : randomHit + 1);
                if(randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
                    randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
                }
                preHit = randomHit;
                attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), ENTANGLE_PROJECTILE, 45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
                break;
            case WEB:
                attacker.playAnimation(MELEE_ANIMATION);
                preHit = Misc.random(maxHit);
                attackDelay = 3;
                spellDelay = 4;
                hitDelay = 1;
                break;
            default:
                preHit = 0;
                blockAnimation = true;
                hitDelay = 1;
                break;
        }

        attacker.getCombatState().setAttackDelay(attackDelay);
        attacker.getCombatState().setSpellDelay(spellDelay);
        final CombatStyle preStyle = style;
        hit = preHit;
        World.getWorld().submit(new Tickable(hitDelay) {

            @Override
            public void execute() {
                this.stop();
                if (preStyle != CombatStyle.MELEE && (preStyle == CombatStyle.ENTANGLE && hit > 0)) {
                    victim.playGraphics(getEnemyHitGfx(preStyle));
                }
                switch (preStyle) {
                    case ENTANGLE:
                        if (hit > 0 && victim.getCombatState().canMove()
                                && victim.getCombatState().canBeFrozen()) {
                            int finalTimer = 25;
                            victim.getCombatState().setCanMove(false);
                            victim.getCombatState().setCanBeFrozen(false);
                            victim.getWalkingQueue().reset();
                            if (victim.getActionSender() != null) {
                                victim.getActionSender().sendMessage(
                                        "You have been frozen!");
                            }
                            World.getWorld().submit(
                                    new Tickable(finalTimer) {
                                        public void execute() {
                                            victim.getCombatState().setCanMove(
                                                    true);
                                            this.stop();
                                        }
                                    });
                            World.getWorld().submit(
                                    new Tickable(finalTimer + 8) {
                                        public void execute() {
                                            victim.getCombatState()
                                                    .setCanBeFrozen(true);
                                            this.stop();
                                        }
                                    });
                        }
                        break;
                    case DRAIN_PRAYER:
                        victim.getActionSender().sendMessage("Your prayer was drained!");
                        victim.getSkills().decreasePrayerPoints(victim.getSkills().getLevel(Skills.PRAYER) * .3);
                        break;
                }
                victim.inflictDamage(new Hit(hit), attacker);
                smite(attacker, victim, hit);
                recoil(attacker, victim, hit);
                vengeance(attacker, victim, hit, 1);
            }
        });
        victim.getActiveCombatAction().defend(attacker, victim, blockAnimation);
    }

    private Graphic getEnemyHitGfx(CombatStyle style) {
        switch (style) {
            case MAGIC:
                return ENEMY_HIT_GFX;
            case ENTANGLE:
                return ENTANGLE_END;
            case DRAIN_PRAYER:
                return PRAYER_DRAIN_END;
            default:
                return null;
        }
    }

    @Override
    public int distance(Mob attacker) {
        return 6;
    }
}
