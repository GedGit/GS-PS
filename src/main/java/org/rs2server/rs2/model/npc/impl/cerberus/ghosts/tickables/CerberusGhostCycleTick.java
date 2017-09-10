package org.rs2server.rs2.model.npc.impl.cerberus.ghosts.tickables;

import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.npc.impl.cerberus.Cerberus;
import org.rs2server.rs2.model.npc.impl.cerberus.ghosts.CerberusGhost;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.impl.StoppingTick;

import java.util.List;

/**
 * @author Twelve
 */
public final class CerberusGhostCycleTick extends StoppingTick {

    private final Cerberus cerberus;

    public CerberusGhostCycleTick(Cerberus cerberus) {
        super(1);
        this.cerberus = cerberus;
    }

    @Override
    public void executeAndStop() {
        Player challenger = cerberus.getChallenger();
		if (cerberus.getGhosts() == null || cerberus.getSkills().getLevel(Skills.HITPOINTS) > 400) {
			return;
		}
        World.getWorld().submit(new CerberusGhostUpdateTick(cerberus));

        List<CerberusGhost> ghosts = cerberus.getGhosts();
        for (int i = 0; i < ghosts.size(); i++) {
            final CerberusGhost ghost = ghosts.get(i);
            World.getWorld().submit(new CerberusGhostAttackTick(cerberus, 2 + (i * 3), ghost, challenger));
            World.getWorld().submit(new CerberusGhostUnregisterTick(cerberus, challenger, ghost, 2 + (i * 3) + 3));
        }
    }
}
