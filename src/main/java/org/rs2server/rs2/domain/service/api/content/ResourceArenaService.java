package org.rs2server.rs2.domain.service.api.content;

import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;

/**
 * Created by Tim on 12/1/2015.
 */
public interface ResourceArenaService {


    public void handleDoorInteraction(@Nonnull Player player, @Nonnull GameObject obj);

    public void handlePilesInteraction(@Nonnull Player player);

    public void handleItemOnNPC(@Nonnull Player player, @Nonnull NPC n, @Nonnull Item item);

}
