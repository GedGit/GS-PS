package org.rs2server.rs2.domain.service.api.content.cerberus;

import com.google.common.collect.ImmutableList;
import org.rs2server.rs2.model.npc.impl.cerberus.Cerberus;
import org.rs2server.rs2.model.npc.impl.cerberus.ghosts.CerberusGhost;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;


/**
 * Created by shawn on 6/8/2016.
 */
public interface CerberusService {
	
    ImmutableList<CerberusGhost> getRandomGhostOrder();

	void enterCave(@Nonnull Player player);

	void exitCave(@Nonnull Player player);

    void addCerberus(Player player, Cerberus cerberus);
}
