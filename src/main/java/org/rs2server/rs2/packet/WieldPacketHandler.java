package org.rs2server.rs2.packet;

import org.rs2server.Server;
import org.rs2server.rs2.Constants;
import org.rs2server.rs2.action.impl.WieldItemAction;
import org.rs2server.rs2.content.api.GameItemInventoryActionEvent;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.domain.service.api.SlayerService;
import org.rs2server.rs2.domain.service.impl.content.MaxCapeServiceImpl;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.container.Inventory;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.skills.runecrafting.Talisman;
import org.rs2server.rs2.net.Packet;

/**
 * Handles the 'wield' option on items.
 *
 * @author Graham Edgecombe
 */
public class WieldPacketHandler implements PacketHandler {

	private final SlayerService slayerService;
	private final HookService hookService;

	public WieldPacketHandler() {
		this.slayerService = Server.getInjector().getInstance(SlayerService.class);
		this.hookService = Server.getInjector().getInstance(HookService.class);
	}

	@Override
	public void handle(Player player, Packet packet) {// you closed it ... lol
		int interfaceId = packet.getInt() >> 16;
		int id = packet.getLEShortA() & 0xFFFF;
		int slot = packet.getLEShort() & 0xFFFF;// size maybe? no
		if (player.getAttribute("cutScene") != null)
			return;
		if (player.getInterfaceAttribute("fightPitOrbs") != null)
			return;
		player.resetAfkTolerance();
		switch (interfaceId) {
		case Inventory.INTERFACE:
			if (slot >= 0 && slot < Inventory.SIZE) {
				if (player.getCombatState().isDead())
					return;
				Item item = player.getInventory().get(slot);
				if (item != null && item.getId() == id) {
					if (item.getId() >= 5509 && item.getId() <= 5515) {
						hookService.post(new GameItemInventoryActionEvent(player,
								GameItemInventoryActionEvent.ClickType.WIELD_OPTION, item, slot));
						return;
					}
					Talisman talisman = Talisman.getTalismanByTiara(item.getId());
					if (talisman != null) {
						if (item.getId() == talisman.getTiaraId())
							player.getActionSender().sendConfig(491, talisman.getTiaraConfig());
					}
					if (item.getId() == 4155) {
						slayerService.sendCheckTaskMessage(player);
						return;
					}
					MaxCapeServiceImpl.MaxCapes maxCapes = MaxCapeServiceImpl.MaxCapes.forMaxCape(item.getId());
					MaxCapeServiceImpl.MaxCapes maxHoods = MaxCapeServiceImpl.MaxCapes.forMaxHood(item.getId());
					if ((maxCapes != null || maxHoods != null)
							&& player.getSkills().getTotalLevel() < Constants.MAX_LEVEL) {
						player.getActionSender()
								.sendMessage("You need a total level of " + Constants.MAX_LEVEL + " to equip this.");
						return;
					}
					if (player.hasQueuedSwitching())
						player.getWeaponSwitchQueue().add(slot);
					else
						new WieldItemAction(player, item.getId(), slot, 0).execute();
				}
			}
			break;
		}
	}
}