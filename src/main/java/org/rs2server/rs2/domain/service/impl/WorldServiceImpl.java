package org.rs2server.rs2.domain.service.impl;

import org.rs2server.rs2.domain.service.api.WorldService;
import org.rs2server.rs2.model.World;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Arrays;

/**
 * @author tommo
 */
public class WorldServiceImpl implements WorldService {

	@Inject
	WorldServiceImpl() {
	}

	/**
	 * Sends messages to all players in the world.
	 * @param messages The messages to send, each message will be send individually.
	 */
	public void sendGlobalMessage(@Nonnull final String ... messages) {
		World.getWorld().getPlayers().stream()
				.filter(p -> p != null && p.getActionSender() != null)
				.forEach(p -> Arrays.stream(messages).forEach(m -> p.getActionSender().sendMessage(m)));
	}

}
