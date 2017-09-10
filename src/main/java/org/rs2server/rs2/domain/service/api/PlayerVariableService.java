package org.rs2server.rs2.domain.service.api;

import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.varp.PlayerVariable;

import javax.annotation.Nonnull;
/**
 * @author twelve
 */
public interface PlayerVariableService {
	void set(@Nonnull Player player, @Nonnull PlayerVariable playerVariable, int toSet);

	void send(@Nonnull Player player, @Nonnull PlayerVariable playerVariable);

	void send(@Nonnull Player player, int config);

	int getCurrentValue(@Nonnull Player player, int config);
}
