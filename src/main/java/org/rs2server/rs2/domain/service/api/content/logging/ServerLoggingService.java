package org.rs2server.rs2.domain.service.api.content.logging;

import org.rs2server.rs2.model.player.Player;

/**
 * @author Vichy
 */
public interface ServerLoggingService {

	enum LogType {

		TRADE("data/logs/trades/"),

		PVP("data/logs/pvp/"),

		PVN("data/logs/pvn/"),

		DROP_ITEM("data/logs/dropped/"),

		PICKUP_ITEM("data/logs/pickup/"),

		PRIVATE_MESSAGE("data/logs/pm/");

		private final String path;

		LogType(String path) {
			this.path = path;
		}

		public String getPath() {
			return path;
		}
	}

	void write(Player player, String info, LogType type);

}
