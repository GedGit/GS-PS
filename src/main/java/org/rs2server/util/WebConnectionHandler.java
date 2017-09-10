package org.rs2server.util;

import org.rs2server.Server;
import org.rs2server.rs2.domain.service.api.PlayerService;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.player.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Clank1337
 */
public class WebConnectionHandler {

	private final PlayerService playerService;

	public WebConnectionHandler() {
		this.playerService = Server.getInjector().getInstance(PlayerService.class);
	}

	@SuppressWarnings("unused")
	public void handleCommand(String command, String extra) {
		if (command.equals("update")) {
			int time = Integer.parseInt(extra);
		}
		if (command.equals("yell")) {
			World.getWorld().sendWorldMessage("[SERVER] " + extra);
		}
		if (command.equals("kick")) {
			Player player = playerService.getPlayer(extra);
			if (player != null) {
				player.getActionSender().sendLogout();
			}
		}
		if (command.equals("ban")) {
			Player player = playerService.getPlayer(extra);
			if (player != null) {
				player.getActionSender().sendLogout();
			}
			try {
				File file = new File("data/bannedUsers.xml");
				List<String> bannedUsers = XMLController.readXML(file);
				bannedUsers.add(extra);
				XMLController.writeXML(bannedUsers, file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
