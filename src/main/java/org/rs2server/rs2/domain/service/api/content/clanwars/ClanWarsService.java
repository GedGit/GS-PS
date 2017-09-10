package org.rs2server.rs2.domain.service.api.content.clanwars;

import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;

/**
 * @author Clank1337
 */
public interface ClanWarsService {

	void openClanwarsInterface(@Nonnull Player player, Player challenger);


	void challengePlayer(@Nonnull Player player, @Nonnull Player other);
}
