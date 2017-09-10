package org.rs2server.rs2.domain.service.impl;

import org.rs2server.cache.format.PlayerVariableComposite;
import org.rs2server.rs2.domain.model.player.PlayerSettingsEntity;
import org.rs2server.rs2.domain.service.api.PlayerVariableService;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.varp.PlayerVariable;
import org.rs2server.rs2.varp.PlayerVariables;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * A service dedicated to working with player variables.
 * @author twelve
 */
public final class PlayerVariableServiceImpl implements PlayerVariableService {

	@Override
	public void set(@Nonnull Player player, @Nonnull PlayerVariable playerVariable, int toSet) {
		PlayerSettingsEntity settings = player.getDatabaseEntity().getPlayerSettings();

		Map<Integer, Integer> variables = settings.getPlayerVariables();
		PlayerVariableComposite composite = playerVariable.getComposite();

		final int currentValue = variables.containsKey(composite.getConfig()) ? variables.get(composite.getConfig()) : 0;
		final int least = playerVariable.getComposite().getLeastSignificant();
		int mask = PlayerVariables.BIT_MAX_VALUES[composite.getMostSignificant() - least];

		if (toSet < 0 || toSet > mask) {
			toSet = 0;
		}

		mask <<= least;
		variables.put(composite.getConfig(), (currentValue & ~mask) | ((toSet << least) & mask));
	}

	@Override
	public void send(@Nonnull Player player, @Nonnull PlayerVariable playerVariable) {
		send(player, playerVariable.getComposite().getConfig());
	}

	@Override
	public void send(@Nonnull Player player, int config) {
		PlayerSettingsEntity settings = player.getDatabaseEntity().getPlayerSettings();
		Map<Integer, Integer> variables = settings.getPlayerVariables();
		if (variables.containsKey(config)) {
			player.getActionSender().sendConfig(config, variables.get(config));
		}
	}

	@Override
	public int getCurrentValue(@Nonnull Player player, int config) {
		return player.getDatabaseEntity().getPlayerSettings().getPlayerVariables().getOrDefault(config, 0);
	}
}
