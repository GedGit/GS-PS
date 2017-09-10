package org.rs2server.rs2.model.req;

import org.rs2server.Server;
import org.rs2server.rs2.domain.service.api.content.duel.DuelArenaService;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.player.Player;

/**
 * @author Clank1337
 */
public class DuelRequestListener implements RequestListener {

	private final DuelArenaService duelService;



	public DuelRequestListener() {
		this.duelService = Server.getInjector().getInstance(DuelArenaService.class);
	}

	@Override
	public void requestAccepted(Player player, Player partner) {
		duelService.openRulesInterface(player, partner);
	}

	@Override
	public void requestCancelled(Player player, Player partner) {
		if(player.getDuelContainer().size() > 0) {
			for(Item item : player.getTrade().toArray()) {
				if(item != null) {
					player.getInventory().add(item);
				}
			}
			player.getDuelContainer().clear();
		}
		if(partner.getDuelContainer().size() > 0) {
			for(Item item : partner.getTrade().toArray()) {
				if(item != null) {
					partner.getInventory().add(item);
				}
			}
			partner.getDuelContainer().clear();
		}
		partner.getActionSender().removeInterfaces(partner.getAttribute("tabmode"), (int) partner.getAttributes().get("tabmode") == 548 ? 19 : 6);
		player.getActionSender().removeInterfaces(player.getAttribute("tabmode"), (int) player.getAttributes().get("tabmode") == 548 ? 19 : 6);
		player.getActionSender().removeAllInterfaces().removeInterface().removeInterfaces(player.getAttribute("tabmode"), ((int) player.getAttribute("tabmode") == 548 ? 61 : 57)).sendMessage("You declined the trade.");
		partner.getActionSender().removeAllInterfaces().removeInterface().removeInterfaces(partner.getAttribute("tabmode"), ((int) partner.getAttribute("tabmode") == 548 ? 61 : 57)).sendMessage("Other player declined the trade.");
		partner.resetInteractingEntity();
		player.resetInteractingEntity();
		player.getInterfaceState().interfaceClosed();
		partner.getInterfaceState().interfaceClosed();
	}

	@Override
	public void requestFinished(Player player, Player partner) {

	}
}
