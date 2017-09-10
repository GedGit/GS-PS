package org.rs2server.rs2.tickable.impl;

import org.rs2server.Server;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.mysql.impl.PlayersOnlineManager;
import org.rs2server.rs2.tickable.Tickable;

public class SystemUpdateTick extends Tickable {

	public SystemUpdateTick() {
		super(2);
	}

	@Override
	public void execute() {
		if (World.systemUpdate) {
			if (World.updateTimer < 5 && World.updateTimer > -1)
				Server.sendDiscordMessage("[SERVER] System Update in: " + World.updateTimer + "..");
			World.updateTimer -= 1;
			for (Player player : World.getWorld().getPlayers()) {
				if (player == null)
					continue;
				if (!player.isActive())
					continue;
				player.getActionSender().sendSystemUpdate(World.updateTimer * 2);
				if (World.updateTimer == -1) {
					player.getActionSender().sendLogout();
					new Thread(new PlayersOnlineManager(player, true)).start();
				}
			}
		}
		if (World.updateTimer == -1 && World.systemUpdate) {
			Server.sendDiscordMessage("[SERVER] Our servers are restarting now.. we will be back in ~5 minutes!");
			System.exit(1);
		}
	}
}