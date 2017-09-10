package org.rs2server.rs2.model.npc.impl.cerberus.ghosts.tickables;

import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.npc.impl.cerberus.Cerberus;
import org.rs2server.rs2.model.npc.impl.cerberus.ghosts.CerberusGhost;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.impl.StoppingTick;

/**
 * @author Twelve
 */
public final class CerberusGhostUnregisterTick extends StoppingTick {

    private final CerberusGhost ghost;
    private final Player challenger;
    private final Cerberus cerberus;

    public CerberusGhostUnregisterTick(Cerberus cerberus, Player challenger, CerberusGhost ghost, int ticks) {
        super(ticks);
        this.cerberus = cerberus;
        this.challenger = challenger;
        this.ghost = ghost;
    }

    @Override
    public void executeAndStop() {
		if (cerberus.getGhosts() == null) {
			this.stop();
			return;
		}
        Location targetLocation = ghost.getLocation().transform(0, 10, 0);
        ghost.getWalkingQueue().addStep(targetLocation.getX(), targetLocation.getY());

        World.getWorld().submit(new StoppingTick(25) {
            @Override
            public void executeAndStop() {
                ghost.unregister();
                challenger.getInstancedNPCs().remove(ghost);
                cerberus.setCanSpawnGhosts(true);
            }
        });

    }
}
