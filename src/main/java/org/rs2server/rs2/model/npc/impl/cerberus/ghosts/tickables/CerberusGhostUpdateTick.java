package org.rs2server.rs2.model.npc.impl.cerberus.ghosts.tickables;

import org.rs2server.Server;
import org.rs2server.rs2.domain.service.api.content.cerberus.CerberusService;
import org.rs2server.rs2.model.npc.impl.cerberus.Cerberus;
import org.rs2server.rs2.tickable.impl.StoppingTick;

/**
 * @author Twelve
 */
public final class CerberusGhostUpdateTick extends StoppingTick {

    private final Cerberus cerberus;
    private final CerberusService cerberusService;

    public CerberusGhostUpdateTick(Cerberus cerberus) {
        super(19);
        this.cerberus = cerberus;
        this.cerberusService = Server.getInjector().getInstance(CerberusService.class);
    }

    @Override
    public void executeAndStop() {
        cerberus.setGhosts(cerberusService.getRandomGhostOrder());
    }
}
