package org.rs2server.rs2.content;

import org.rs2server.Server;
import org.rs2server.rs2.domain.service.api.PermissionService;
import org.rs2server.rs2.domain.service.api.PlayerService;
import org.rs2server.rs2.model.DialogueManager;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.player.Player;

import java.util.List;

/**
 * @author Clank1337
 */
public final class ZulAreth {

	private final Player player;
	private final PlayerService playerService;

	public ZulAreth(Player player) {
		this.player = player;
		this.playerService = Server.getInjector().getInstance(PlayerService.class);
		Server.getInjector().getInstance(PermissionService.class);
	}

	public void appendDeath() {
		for (Item items : player.getEquipment().getItems()) {
			if (items != null) {
				player.getDatabaseEntity().getZulrahState().getItemsLostZulrah().add(items);
			}
		}
		for (Item items : player.getInventory().getItems()) {
			if (items != null && items.getId() != 11941) {
				player.getDatabaseEntity().getZulrahState().getItemsLostZulrah().add(items);
			}
		}
		for (Item items : player.getLootingBag().getItems()) {
			if (items != null) {
				player.getDatabaseEntity().getZulrahState().getItemsLostZulrah().add(items);
			}
		}
		player.getLootingBag().clear();
	}

	public void claimItems() {
		if (!player.isSilverMember() && player.getInventory().getCount(995) < 100000) {
			DialogueManager.openDialogue(player, 2045);
			return;
		}
		final List<Item> items = player.getDatabaseEntity().getZulrahState().getItemsLostZulrah();
		if (!items.isEmpty()) {
			items.forEach(i -> playerService.giveItem(player, i, true));
			player.getInventory().remove(new Item(995, 100000));
			items.clear();
			DialogueManager.openDialogue(player, 2046);
		}
	}
}
