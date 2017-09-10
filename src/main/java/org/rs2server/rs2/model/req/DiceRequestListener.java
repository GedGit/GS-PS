package org.rs2server.rs2.model.req;

import org.rs2server.Server;
import org.rs2server.rs2.content.api.GameDiceRequestEvent;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.domain.service.api.content.gamble.DiceGameService;
import org.rs2server.rs2.domain.service.impl.content.gamble.DiceGameServiceImpl;
import org.rs2server.rs2.model.player.Player;

/**
 * @author Clank1337
 */
public class DiceRequestListener implements RequestListener {

	private final HookService hookService;
	private final DiceGameService diceGameService;

	public DiceRequestListener() {
		this.hookService = Server.getInjector().getInstance(HookService.class);
		this.diceGameService = Server.getInjector().getInstance(DiceGameService.class);
	}

	@Override
	public void requestAccepted(Player player, Player partner) {
		hookService.post(new GameDiceRequestEvent(player, partner));
	}

	@Override
	public void requestCancelled(Player player, Player partner) {
		if (player.getTransaction() == null || partner.getTransaction() == null || player.getInterfaceState().isInterfaceOpen(DiceGameServiceImpl.DICE_WIDGET) || partner.getInterfaceState().isInterfaceOpen(DiceGameServiceImpl.DICE_WIDGET)) {
			return;
		}
		diceGameService.endTransaction(player.getDiceGameTransaction(), true);
	}

	@Override
	public void requestFinished(Player player, Player partner) {
	}

}

