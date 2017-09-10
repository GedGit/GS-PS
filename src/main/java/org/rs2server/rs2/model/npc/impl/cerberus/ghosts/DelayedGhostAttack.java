package org.rs2server.rs2.model.npc.impl.cerberus.ghosts;

import org.rs2server.rs2.model.Projectile;

/**
 * Created by shawn on 6/8/2016.
 */
public final class DelayedGhostAttack {
    private final Projectile projectile;
    private final int delay;

    public DelayedGhostAttack(Projectile projectile, int delay) {
        this.projectile = projectile;
        this.delay = delay;
    }

    public Projectile getProjectile() {
        return projectile;
    }

    public int getDelay() {
        return delay;
    }
}
