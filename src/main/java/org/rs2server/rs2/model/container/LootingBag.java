package org.rs2server.rs2.model.container;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import org.rs2server.Server;
import org.rs2server.rs2.content.api.GameMobDeathEvent;
import org.rs2server.rs2.domain.service.api.GroundItemService;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.container.impl.InterfaceContainerListener;
import org.rs2server.rs2.model.player.Player;

import java.util.Objects;

public class LootingBag {

	private final GroundItemService itemService;

	@Inject
	public LootingBag(HookService service) {
		this.itemService = Server.getInjector().getInstance(GroundItemService.class);
		service.register(this);
	}

	@Subscribe
	public final void onMobDeath(final GameMobDeathEvent event) {
		final Mob mob = event.getMob();
		final Mob k = event.getKiller();
		if (mob.isPlayer()) {
			Player player = (Player) mob;
			if (player.getLootingBag().size() <= 0) {
				return;
			}
			player.getLootingBag().stream().filter(Objects::nonNull).forEach(i ->
					itemService.createGroundItem(k.isPlayer() ? (Player)k : player,
							new GroundItemService.GroundItem(i, player.getLocation(), k.isPlayer() ? (Player) k : player, false)));
		}
	}


	public static final int SIZE = 27;

	public static void check(Player player) {
		player.getActionSender().sendInterfaceInventory(81);
		player.getActionSender().sendAccessMask(1024, 81, 7, 0, 27)
		.sendCS2Script(495, new Object[]{0, "Looting bag"}, "s1");

		player.getInterfaceState().addListener(player.getLootingBag(), new InterfaceContainerListener(player, -1, 63786, 516));
	}
	
	public static void open(Player player) {
		player.getActionSender().sendInterfaceInventory(81)
				.sendAccessMask(542, 81, 5, 0, 27)
				.sendCS2Script(495, new Object[]{1, "Add to bag"}, "s1");

		//Access Mask: Set = 542, Interface = 81, Child = 5, Offset = 0, Length = 27
		
		player.getInterfaceState().addListener(player.getInventory(),
				new InterfaceContainerListener(player, -1, 64209, 93));
	}
	
	public static void deposit(Player player, int slot, int id, int amount) {
		player.getActionSender().removeChatboxInterface();
		Item item = player.getInventory().get(slot);
		if(item == null) {
			return; // invalid packet, or client out of sync
		}
		if(item.getId() != id) {
			return; // invalid packet, or client out of sync
		}
		if(!item.getDefinition().isTradable()) {
			player.getActionSender().sendMessage("You cannot trade this item.");
			return;
		}
		boolean inventoryFiringEvents = player.getInventory().isFiringEvents();
		player.getInventory().setFiringEvents(false);
		try {
			int transferAmount = player.getInventory().getCount(id);
			if(transferAmount >= amount) {
				transferAmount = amount;
			} else if(transferAmount == 0) {
				return; // invalid packet, or client out of sync
			}

			if(player.getLootingBag().add(new Item(item.getId(), transferAmount), -1)) {
				player.getInventory().remove(new Item(item.getId(), transferAmount));
			}
			player.getInventory().fireItemsChanged();
		} finally {
			player.getInventory().setFiringEvents(inventoryFiringEvents);
			player.getActionSender().removeChatboxInterface();
		}
	}

	public static void depositBank(Player player, int slot, int id, int amount) {
		player.getActionSender().removeChatboxInterface();
		Item item = player.getLootingBag().get(slot);
		if(item == null) {
			return; // invalid packet, or client out of sync
		}
		if(item.getId() != id) {
			return; // invalid packet, or client out of sync
		}
		if(!item.getDefinition().isTradable()) {
			player.getActionSender().sendMessage("You cannot trade this item.");
			return;
		}
		if (player.getBank().size() >= Bank.SIZE) {
			player.getActionSender().sendMessage("You don't have enough space in your bank account.");
			return;
		}
		boolean inventoryFiringEvents = player.getLootingBag().isFiringEvents();
		player.getLootingBag().setFiringEvents(false);
		try {
			int transferAmount = player.getLootingBag().getCount(id);
			if(transferAmount >= amount) {
				transferAmount = amount;
			} else if(transferAmount == 0) {
				return; // invalid packet, or client out of sync
			}

			if(player.getBank().add(new Item(item.getId(), transferAmount), -1)) {
				player.getLootingBag().remove(new Item(item.getId(), transferAmount));
			}
			player.getLootingBag().fireItemsChanged();
		} finally {
			player.getLootingBag().setFiringEvents(inventoryFiringEvents);
			player.getActionSender().sendConfig(115, 2);
			player.getActionSender().removeChatboxInterface();
		}
	}
}
