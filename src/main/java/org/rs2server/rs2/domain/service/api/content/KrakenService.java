package org.rs2server.rs2.domain.service.api.content;

import org.rs2server.rs2.model.npc.impl.kraken.Kraken;
import org.rs2server.rs2.model.npc.impl.kraken.Whirlpool;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;

/**
 * @author Clank1337
 * @author Twelve
 */
public interface KrakenService {

	void enterCave(@Nonnull Player player);

	void exitCave(@Nonnull Player player);

	void addKraken(@Nonnull Player player, Kraken kraken);

	void destroyKraken(@Nonnull Player player);

	void disturbWhirlpool(@Nonnull Player player, @Nonnull Whirlpool whirlpool);


}
