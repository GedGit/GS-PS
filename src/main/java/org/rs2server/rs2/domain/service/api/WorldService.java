package org.rs2server.rs2.domain.service.api;

import javax.annotation.Nonnull;

/**
 * Service providing interaction with the global world.
 *
 * @author tommo
 */
public interface WorldService {

	/**
	 * Sends messages to all players in the world.
	 * @param messages The messages to send, each message will be send individually.
	 */
	void sendGlobalMessage(@Nonnull final String ... messages);

}
