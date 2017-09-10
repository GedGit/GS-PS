package org.rs2server.rs2.domain.service.api.content;

import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;

/**
 * @author Clank1337
 */
public interface LootingBagService {

	void check(@Nonnull Player player);

	void open(@Nonnull Player player);

	void deposit(@Nonnull Player player, int slot, int id, int amount);

	void depositBank(@Nonnull Player player, int slot, int id, int amount);

	void updateCS2(@Nonnull Player player);

	int getBagValue(@Nonnull Player player);

	void redeemBag(@Nonnull Player player);
}
