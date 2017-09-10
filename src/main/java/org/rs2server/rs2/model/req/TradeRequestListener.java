package org.rs2server.rs2.model.req;

import org.rs2server.Server;
import org.rs2server.rs2.content.api.GameTradeRequestEvent;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.domain.service.api.content.trade.TradeService;
import org.rs2server.rs2.model.player.Player;

/**
 * A class which listens to trade requests.
 * @author Graham Edgecombe
 *
 */
public class TradeRequestListener implements RequestListener {

	private final HookService hookService;
	private final TradeService tradeService;

	public TradeRequestListener() {
		this.hookService = Server.getInjector().getInstance(HookService.class);
		this.tradeService = Server.getInjector().getInstance(TradeService.class);
	}

	@Override
	public void requestAccepted(Player player, Player partner) {
		hookService.post(new GameTradeRequestEvent(player, partner));
	}

	@Override
	public void requestCancelled(Player player, Player partner) {
		if (player.getTransaction() == null || partner.getTransaction() == null)
			return;
		tradeService.endTransaction(player.getTransaction(), true);
	}

	@Override
	public void requestFinished(Player player, Player partner) {
		/*if(partner.getTrade().size() > 0) {
			for(Item item : partner.getTrade().toArray()) {
				if(item != null) {
					player.getInventory().add(item);
				}
			}
			partner.getTrade().clear();
		}
		if(player.getTrade().size() > 0) {
			for(Item item : player.getTrade().toArray()) {
				if(item != null) {
					partner.getInventory().add(item);
				}
			}
			player.getTrade().clear();
		}
		player.resetInteractingEntity();
		partner.resetInteractingEntity();
		//player.getActionSender().sendAccessMask(1278, -1, -1, -1, -1);
		partner.getActionSender().removeInterfaces(partner.getAttribute("tabmode"), (int) partner.getAttributes().get("tabmode") == 548 ? 19 : 6);
		player.getActionSender().removeInterfaces(player.getAttribute("tabmode"), (int) player.getAttributes().get("tabmode") == 548 ? 19 : 6);
		partner.getActionSender().removeAllInterfaces().removeInterface().removeInterfaces(partner.getAttribute("tabmode"), ((int) partner.getAttribute("tabmode") == 548 ? 61 : 57)).sendMessage("You accepted the trade.");
		player.getActionSender().removeAllInterfaces().removeInterface().removeInterfaces(player.getAttribute("tabmode"), ((int) player.getAttribute("tabmode") == 548 ? 61 : 57)).sendMessage("You accepted the trade.");
		player.getInterfaceState().interfaceClosed();
		partner.getInterfaceState().interfaceClosed();
		player.getRequestManager().setRequestType(null);
		player.getRequestManager().setAcquaintance(null);		
		partner.getRequestManager().setRequestType(null);
		partner.getRequestManager().setAcquaintance(null);*/
	}

}
