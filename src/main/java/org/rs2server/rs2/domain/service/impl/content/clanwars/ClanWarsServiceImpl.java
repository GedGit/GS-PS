package org.rs2server.rs2.domain.service.impl.content.clanwars;

import org.rs2server.rs2.domain.service.api.content.clanwars.ClanWarsService;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.player.PrivateChat;

import javax.annotation.Nonnull;

/**
 * @author Clank1337
 */
public class ClanWarsServiceImpl implements ClanWarsService {

	@Override
	public void openClanwarsInterface(@Nonnull Player player, Player challenger) {

	}

	@Override
	public void challengePlayer(@Nonnull Player player, @Nonnull Player other) {
		PrivateChat playerClan = World.getWorld().getPrivateChat().get(player.getInterfaceState().getClan());
		PrivateChat otherClan = World.getWorld().getPrivateChat().get(other.getInterfaceState().getClan());
		if (playerClan == null) {
			player.getActionSender().sendMessage("You aren't currently in a Clan chat.");
			return;
		}
		if (otherClan == null) {
			player.getActionSender().sendMessage("That player isn't current in a Clan chat.");
			return;
		}
//		other.getActionSender().sendClanRequest(player.getName() + " wishes ")
	}
}
