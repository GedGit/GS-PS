package org.rs2server.rs2.domain.service.api.content;

import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;

/**
 * Created by Tim on 12/7/2015.
 */
public interface FountainOfHeroesService {

    public void rechargeGlories(@Nonnull Player player);
}
