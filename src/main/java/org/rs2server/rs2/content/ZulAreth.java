package org.rs2server.rs2.content;

import org.rs2server.rs2.model.DialogueManager;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.container.Inventory;
import org.rs2server.rs2.model.player.Player;

import java.util.List;

/**
 * @author Clank1337
 */
public final class ZulAreth {

	private final Player player;

	public ZulAreth(Player player) {
		this.player = player;
	}

	public void appendDeath() {
		for (Item items : player.getEquipment().getItems()) {
			if (items != null) {
				player.getDatabaseEntity().getZulrahState().getItemsLostZulrah().add(items);
			}
		}
		for (Item items : player.getInventory().getItems()) {
			// Exclude looting bag, we'll handle it seperately
			if (items != null && items.getId() != 11941) {
				player.getDatabaseEntity().getZulrahState().getItemsLostZulrah().add(items);
			}
		}
		// If we have the looting bag then empty it
		if (player.getInventory().contains(11941)) {
			for (Item items : player.getLootingBag().getItems()) {
				if (items != null)
					player.getDatabaseEntity().getZulrahState().getItemsLostZulrah().add(items);
			}
			player.getLootingBag().clear();
		}
	}

	public void claimItems() {
		if (!player.isSilverMember() && player.getInventory().getCount(995) < 100000) {
			DialogueManager.openDialogue(player, 2045);
			return;
		}
		final List<Item> items = player.getDatabaseEntity().getZulrahState().getItemsLostZulrah();
		if (!items.isEmpty()) {
			items.forEach(i -> Inventory.addDroppable(player, i));
			if (!player.isSilverMember())
				player.getInventory().remove(new Item(995, 100000));
			items.clear();
			DialogueManager.openDialogue(player, 2046);
		}
	}
}
