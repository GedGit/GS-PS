package org.rs2server.rs2.model.combat.npcs;

import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.CombatState;
import org.rs2server.rs2.model.combat.impl.AbstractCombatAction;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.region.Region;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tim on 12/6/2015.
 */
public class ChaosFanatic extends AbstractCombatAction {

    /**
     * The singleton instance.
     */
    private static final ChaosFanatic INSTANCE = new ChaosFanatic();

    private static final Animation ATTACK_ANIMATION = Animation.create(811);

    private static final int RED_GFX = 554;

    private static final String[] MESSAGES = {
            "BURN!",
            "WEUGH!",
            "Develish Oxen Roll!",
            "All your wilderness are belong to them!",
            "AhehHeheuhHhahueHuUEehEahAH",
            "I shall call him squidgy and he shall be my squidgy!"};

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
    public ChaosFanatic() {

    }

    private enum CombatStyle {
        MAGIC,
        GREEN_BOMB
    }

    @Override
    public void hit(final Mob attacker, final Mob victim) {
        super.hit(attacker, victim);

        if (!attacker.isNPC()) {
            return; //this should be an NPC!
        }

        int maxHit = 31;
        int randomHit;
        int hitDelay;
        boolean blockAnimation = false;
        final int hit;
        int clientSpeed;
        int gfxDelay;
        int preHit = 0;
        ((NPC) attacker).forceChat(MESSAGES[random.nextInt(MESSAGES.length)]);
        CombatStyle style = CombatStyle.MAGIC;

        switch (Misc.random(3)) {
            case 3:
                style = CombatStyle.GREEN_BOMB;
                break;
        }
        Location firstLocation = victim.getLocation();
        Location secondLocation = victim.getLocation().transform(1, 1, 0);
        Location thirdLocation = victim.getLocation().transform(-1, -1, 0);
        switch (style) {
            case MAGIC:
                attacker.playAnimation(ATTACK_ANIMATION);
                //victim.playGraphics(Graphic.create(550));
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
                attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), victim.getCentreLocation(), RED_GFX, 45, 50, clientSpeed, 43, 35, victim.getProjectileLockonIndex(), 10, 48));
                randomHit = damage(31, attacker, victim, CombatState.AttackType.MAGIC, Skills.MAGIC, Prayers.PROTECT_FROM_MAGIC, false, false);
                if (randomHit > victim.getSkills().getLevel(Skills.HITPOINTS)) {
                    randomHit = victim.getSkills().getLevel(Skills.HITPOINTS);
                }
                preHit = randomHit;
                break;
            case GREEN_BOMB:
                attacker.playAnimation(ATTACK_ANIMATION);
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
                attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), firstLocation, 551, 45, 50, clientSpeed, 43, 35, 0, 10, 48));
                attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), secondLocation, 551, 45, 50, clientSpeed, 43, 35, 0, 10, 48));
                attacker.playProjectile(Projectile.create(attacker.getCentreLocation(), thirdLocation, 551, 45, 50, clientSpeed, 43, 35, 0, 10, 48));
                break;
            default:
                gfxDelay = 0;
                break;
        }
        attacker.getCombatState().setAttackDelay(style == CombatStyle.MAGIC ? 2 : 4);
        attacker.getCombatState().setSpellBook(style == CombatStyle.MAGIC ? 3 : 5);

        final CombatStyle preStyle = style;
        hit = preHit;
        World.getWorld().submit(new Tickable((gfxDelay / 20) - 1) {

            @Override
            public void execute() {
                this.stop();
                List<Player> enemies = new ArrayList<>();
                switch (preStyle) {
                    case GREEN_BOMB:
                        for (Player p : victim.getLocalPlayers()) {
                            if (p.getLocation().equals(firstLocation) || p.getLocation().equals(secondLocation) || p.getLocation().equals(thirdLocation)) {
                                if (p == victim) {
                                    continue;
                                }
                                enemies.add(p);
                            }
                        }
                        victim.getActionSender().sendStillGFX(157, 100, firstLocation);
                        victim.getActionSender().sendStillGFX(157, 100, secondLocation);
                        victim.getActionSender().sendStillGFX(157, 100, thirdLocation);
                        break;
                }
				int finalHit = preStyle == CombatStyle.GREEN_BOMB ? Misc.random(31) : hit;
				boolean doDamage = true;
				switch (preStyle) {
					case GREEN_BOMB:
						if (!victim.getLocation().equals(firstLocation) && !victim.getLocation().equals(secondLocation) && !victim.getLocation().equals(thirdLocation)) {
							doDamage = false;
						}
						break;
				}
				if (doDamage) {
					victim.inflictDamage(new Hit(finalHit), attacker);
					smite(attacker, victim, finalHit);
					recoil(attacker, victim, finalHit);
					vengeance(attacker, victim, finalHit, 1);
				}
				for (Player p : enemies) {
					p.inflictDamage(new Hit(finalHit), attacker);
					smite(attacker, p, finalHit);
					recoil(attacker, p, finalHit);
					vengeance(attacker, p, finalHit, 1);
				}
				enemies.clear();
            }
        });
    }

    @Override
    public int distance(Mob attacker) {
        return 6;
    }
}
