package org.rs2server.rs2.domain.service.api.content;

import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;

/**
 * Created by Tim on 12/21/2015.
 */
public interface PotionDecanterService {

    public void decantPotions(@Nonnull Player player);
}
