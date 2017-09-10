package org.rs2server.rs2.domain.service.api.content.abyss;

import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;

/**
 * @author Clank1337
 */
public interface AbyssService {

	void enterAbyss(@Nonnull Player player);

	void drainPlayer(@Nonnull Player player);
}
