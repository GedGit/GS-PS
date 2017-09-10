package org.rs2server.rs2.model.npc.impl.cerberus.ghosts;

import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;

/**
 * @author Twelve
 */
public abstract class CerberusGhost extends NPC {
    private final Animation animation;

    public CerberusGhost(int id, Location location, Animation animation) {
        super(id, location);
        this.animation = animation;
    }

	@Override
	public Animation getDeathAnimation() {
		return Animation.create(-1);
	}

    public final DelayedGhostAttack createGhostAttack(Player target) {
        Location location = getLocation();
        int clientSpeed;
        int gfxDelay;
        double distance = location.getDistance(target.getLocation());
        if(distance <= 1) {
            clientSpeed = 70;
            gfxDelay = 80;
        } else if(distance <= 5) {
            clientSpeed = 90;
            gfxDelay = 100;
        } else if(distance <= 8) {
            clientSpeed = 110;
            gfxDelay = 120;
        } else {
            clientSpeed = 130;
            gfxDelay = 140;
        }
        int delay = (gfxDelay / 20) - 1;
        Projectile projectile = Projectile.create(getCentreLocation(), target.getCentreLocation(), getProjectileId(), 45, 50, clientSpeed, getProjectileStartHeight(), getProjectileEndHeight(), target.getProjectileLockonIndex(), 10, 48);
        return new DelayedGhostAttack(projectile, delay);
    }

    public abstract int getProjectileId();
    public abstract int getProjectileStartHeight();
    public abstract int getProjectileEndHeight();

    public final Animation getAnimation() {
        return animation;
    }
}
