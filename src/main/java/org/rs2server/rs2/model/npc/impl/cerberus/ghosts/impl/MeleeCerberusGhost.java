package org.rs2server.rs2.model.npc.impl.cerberus.ghosts.impl;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.npc.impl.cerberus.ghosts.CerberusGhost;

/**
 * Created by shawn on 6/8/2016.
 */
public final class MeleeCerberusGhost extends CerberusGhost {

    private static final int NPC_ID = 5869;

    public MeleeCerberusGhost(Location location) {
        super(NPC_ID, location, Animation.create(-1));//CAN U get me the gfx shit i need
    }

    @Override
    public int getProjectileId() {
        return 1248;
    }

    @Override
    public int getProjectileStartHeight() {
        return 43;
    }

    @Override
    public int getProjectileEndHeight() {
        return 35;
    }

    @Override
    public String toString() {
        return "Melee ghost: "+getLocation();
    }
}
