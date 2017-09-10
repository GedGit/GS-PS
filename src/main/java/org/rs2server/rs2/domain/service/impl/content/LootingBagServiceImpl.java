package org.rs2server.rs2.domain.service.impl.content;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import org.rs2server.rs2.content.api.GameMobDeathEvent;
import org.rs2server.rs2.domain.service.api.GroundItemService;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.domain.service.api.PlayerService;
import org.rs2server.rs2.domain.service.api.content.ItemService;
import org.rs2server.rs2.domain.service.api.content.LootingBagService;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.boundary.BoundaryManager;
import org.rs2server.rs2.model.container.Bank;
import org.rs2server.rs2.model.container.Inventory;
import org.rs2server.rs2.model.container.impl.InterfaceContainerListener;
import org.rs2server.rs2.model.minigame.fightcave.FightCave;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.ActionSender;
import org.rs2server.rs2.util.Misc;

import javax.annotation.Nonnull;
import java.text.NumberFormat;
import java.util.*;

/**
 * @author Clank1337
 */
public class LootingBagServiceImpl implements LootingBagService {

	private final GroundItemService groundItemService;
	private final ItemService itemService;
	private final PlayerService playerService;

	@Inject
	public LootingBagServiceImpl(HookService hookService, GroundItemService groundItemService, ItemService itemService,
			PlayerService playerService) {
		this.groundItemService = groundItemService;
		this.itemService = itemService;
		this.playerService = playerService;
		hookService.register(this);
	}

	@Subscribe
	public final void onMobDeath(final GameMobDeathEvent event) {
		final Mob mob = event.getMob();
		final Mob k = event.getKiller();
		if (mob.isPlayer()) {
			Player player = (Player) mob;
			if (player.getLootingBag().size() <= 0
					|| BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "Zulrah")
					|| BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "ClanWarsFFAFull")
					|| BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "PestControl")
					|| player.getRFD().isStarted() || FightCave.IN_CAVES.contains(player)) {
				return;
			}
			player.getLootingBag().stream().filter(Objects::nonNull)
					.forEach(i -> groundItemService.createGroundItem(k.isPlayer() ? (Player) k : player,
							new GroundItemService.GroundItem(i, player.getLocation(),
									k.isPlayer() ? (Player) k : player, false, k.isPlayer() && k != player,
									k.isPlayer() && k == player)));
			player.getLootingBag().clear();
		} else if (mob.isNPC() && k.isPlayer()) {
			NPC npc = (NPC) mob;
			Player killer = (Player) k;
			if (!npc.isInWilderness())
				return;
			if (Misc.random(29) == 0) {
				if (!itemService.playerOwnsItem(killer, 11941)) {
					groundItemService.createGroundItem(killer,
							new GroundItemService.GroundItem(new Item(11941), npc.getLocation(), killer, false));
					killer.getActionSender().sendMessage("<col=7f00ff>You've received a Looting bag drop.");
				}
			}
		}
	}

	public static final int SIZE = 27;

	@Override
	public void check(@Nonnull Player player) {
		player.getActionSender().sendInterfaceInventory(81);
		player.getActionSender().sendAccessMask(1024, 81, 5, 0, 27)
				.sendString(81, 6,
						"Bag Value: " + NumberFormat.getNumberInstance(Locale.ENGLISH).format(getBagValue(player)))
				.sendCS2Script(495, new Object[] { 0, "Looting bag" }, "s1");
		updateCS2(player);

		player.getInterfaceState().addListener(player.getLootingBag(),
				new InterfaceContainerListener(player, -1, 63786, 516));
	}

	@Override
	public void open(@Nonnull Player player) {
		if (!player.isInWilderness() && !player.isAdministrator()) {
			player.getActionSender().sendMessage("You must be in the Wilderness to do this.");
			return;
		}
		player.getActionSender().sendInterfaceConfig(81, 6, false)
				.sendString(81, 6,
						"Bag Value: " + NumberFormat.getNumberInstance(Locale.ENGLISH).format(getBagValue(player)))
				.sendInterfaceInventory(81).sendAccessMask(542, 81, 5, 0, 27)
				.sendCS2Script(495, new Object[] { 1, "Add to bag" }, "s1");

		updateCS2(player);
		player.getInterfaceState().addListener(player.getInventory(),
				new InterfaceContainerListener(player, -1, 64209, 93));
	}

	@Override
	public void deposit(@Nonnull Player player, int slot, int id, int amount) {
		player.getActionSender().removeChatboxInterface();
		Item item = player.getInventory().get(slot);
		if (item == null) {
			return; // invalid packet, or client out of sync
		}
		if (item.getId() != id) {
			return; // invalid packet, or client out of sync
		}
		if (item.getId() == 11941) {
			player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE, -1, null,
					"You may be surprised to learn that bagception is not permitted.");
			return;
		}
		if (!player.isInWilderness() && !player.isAdministrator()) {
			player.getActionSender()
					.sendMessage("You can't deposit items into the Looting bag unless you are in the Wilderness.");
			return;
		}
		if (!item.getDefinition().isTradable()) {
			player.getActionSender().sendMessage("You cannot trade this item.");
			return;
		}
		boolean inventoryFiringEvents = player.getInventory().isFiringEvents();
		player.getInventory().setFiringEvents(false);
		try {
			int transferAmount = player.getInventory().getCount(id);
			if (transferAmount >= amount) {
				transferAmount = amount;
			} else if (transferAmount == 0) {
				return; // invalid packet, or client out of sync
			}

			if (player.getLootingBag().add(new Item(item.getId(), transferAmount), -1)) {
				player.getInventory().remove(new Item(item.getId(), transferAmount));
			}
			player.getInventory().fireItemsChanged();
		} finally {
			updateCS2(player);
			player.getActionSender().sendString(81, 6,
					"Bag Value: " + NumberFormat.getNumberInstance(Locale.ENGLISH).format(getBagValue(player)));
			player.getInventory().setFiringEvents(inventoryFiringEvents);
			player.getActionSender().removeChatboxInterface();
		}
	}

	@Override
	public void depositBank(@Nonnull Player player, int slot, int id, int amount) {
		player.getActionSender().removeChatboxInterface();
		Item item = player.getLootingBag().get(slot);
		if (item == null) {
			return; // invalid packet, or client out of sync
		}
		if (item.getId() != id) {
			return; // invalid packet, or client out of sync
		}
		if (!item.getDefinition().isTradable()) {
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
			if (transferAmount >= amount) {
				transferAmount = amount;
			} else if (transferAmount == 0) {
				return; // invalid packet, or client out of sync
			}

			if (player.getBank().add(new Item(item.getId(), transferAmount), -1)) {
				player.getLootingBag().remove(new Item(item.getId(), transferAmount));
			}
			player.getLootingBag().fireItemsChanged();
		} finally {
			updateCS2(player);
			player.getActionSender().sendString(81, 6,
					"Bag Value: " + NumberFormat.getNumberInstance(Locale.ENGLISH).format(getBagValue(player)));
			player.getLootingBag().setFiringEvents(inventoryFiringEvents);
			player.getActionSender().sendConfig(115, 2);
			player.getActionSender().removeChatboxInterface();
		}
	}

	@Override
	public void updateCS2(@Nonnull Player player) {
		List<Object> priceList = new ArrayList<>();

		priceList.add(93);
		for (int i = 0; i < Inventory.SIZE; i++) {
			Item item = player.getInventory().get(i);
			if (item != null && item.getId() != 11941 && item.getDefinition() != null) {
				priceList.add(item.getPrice());
			} else {
				priceList.add(-1);
			}
		}
		Collections.reverse(priceList);
		player.getActionSender().sendCS2Script(1235, priceList.toArray(), "viiiiiiiiiiiiiiiiiiiiiiiiiiii");
	}

	@Override
	public int getBagValue(@Nonnull Player player) {
		Optional<Integer> priceOption = player.getLootingBag().stream()
				.filter(i -> i != null && i.getDefinition() != null).map(i -> i.getPrice())
				.reduce((a, b) -> a + b);
		return priceOption.isPresent() ? priceOption.get() : 0;
	}

	@Override
	public void redeemBag(@Nonnull Player player) {
		if (player.getLootingBag().size() <= 0)
			return;
		player.getLootingBag().stream().filter(Objects::nonNull).forEach(i -> playerService.giveItem(player, i, true));
		player.getLootingBag().clear();
	}
}
