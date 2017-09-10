package org.rs2server.rs2.packet;

import org.rs2server.Server;
import org.rs2server.rs2.domain.service.api.BankPinService;
import org.rs2server.rs2.domain.service.impl.BankPinServiceImpl;
import org.rs2server.rs2.model.Shop;
import org.rs2server.rs2.model.container.Bank;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.container.PriceChecker;
import org.rs2server.rs2.model.container.Trade;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.Packet;

/**
 * A packet handler that is called when an interface is closed.
 * 
 * @author Graham Edgecombe
 *
 */
public class CloseInterfacePacketHandler implements PacketHandler {

	private final BankPinService bankPinService;

	public CloseInterfacePacketHandler() {
		this.bankPinService = Server.getInjector().getInstance(BankPinService.class);
	}

	@Override
	public void handle(Player player, Packet packet) {
		player.getActionSender().removeChatboxInterface();

		if (player.getInterfaceState().isInterfaceOpen(125))
			player.getActionSender().removeInventoryInterface();

		if (player.getInterfaceState().isInterfaceOpen(BankPinServiceImpl.BANK_PIN_WIDGET)
				|| player.getInterfaceState().isInterfaceOpen(BankPinServiceImpl.BANK_SETTINGS_WIDGET))
			bankPinService.onClose(player);

		if (player.getInterfaceState().isInterfaceOpen(PriceChecker.PRICE_INVENTORY_INTERFACE))
			PriceChecker.returnItems(player);

		if (player.getInterfaceState().isEnterAmountInterfaceOpen())
			player.getActionSender().removeEnterAmountInterface();

		if (player.getAttribute("bank_searching") != null) {
			player.getActionSender().removeEnterAmountInterface();
			player.removeAttribute("bank_searching");
		}

		if (player.getInterfaceState().isInterfaceOpen(Equipment.SCREEN)
				|| player.getInterfaceState().isInterfaceOpen(Bank.BANK_INVENTORY_INTERFACE)
				|| player.getInterfaceState().isInterfaceOpen(Trade.TRADE_INVENTORY_INTERFACE)
				|| player.getInterfaceState().isInterfaceOpen(Shop.SHOP_INVENTORY_INTERFACE)
				|| player.getInterfaceState().isInterfaceOpen(PriceChecker.PRICE_INVENTORY_INTERFACE)) {
			player.getActionSender().removeInventoryInterface();
			player.resetInteractingEntity();
		}

		player.getInterfaceState().setOpenShop(-1);
		player.getActionSender().removeAllInterfaces().removeInterface();
	}
}