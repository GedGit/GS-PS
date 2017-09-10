package org.rs2server.rs2.domain.service.api.content;

import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;

/**
 * @author Clank1337
 */
public interface RunePouchService {

	void openPouchInterface(@Nonnull Player player);

	void deposit(@Nonnull Player player, int slot, int id, int amount);

	void withdraw(@Nonnull Player player, int slot, int id, int amount);

	void emptyPouch(@Nonnull Player player);

	void updatePouchInterface(@Nonnull Player player);
}
