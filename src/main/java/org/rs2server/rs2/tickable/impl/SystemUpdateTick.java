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
		if (World.SYSTEM_UPDATE) {
			if (World.UPDATE_TIMER < 5 && World.UPDATE_TIMER > -1)
				Server.sendDiscordMessage("[SERVER] System Update in: " + World.UPDATE_TIMER + "..");
			World.UPDATE_TIMER -= 1;
			for (Player player : World.getWorld().getPlayers()) {
				if (player == null)
					continue;
				if (!player.isActive())
					continue;
				player.getActionSender().sendSystemUpdate(World.UPDATE_TIMER * 2);
				if (World.UPDATE_TIMER == -1) {
					player.getActionSender().sendLogout();
					new Thread(new PlayersOnlineManager(player, true)).start();
				}
			}
		}
		if (World.UPDATE_TIMER == -1 && World.SYSTEM_UPDATE) {
			Server.sendDiscordMessage("[SERVER] Our servers are restarting now.. we will be back in ~5 minutes!");
			System.exit(1);
		}
	}
}