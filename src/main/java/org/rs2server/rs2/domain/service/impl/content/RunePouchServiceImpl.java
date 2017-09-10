package org.rs2server.rs2.domain.service.impl.content;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import org.rs2server.rs2.content.api.GameInterfaceButtonEvent;
import org.rs2server.rs2.content.api.GameItemInventoryActionEvent;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.domain.service.api.PlayerVariableService;
import org.rs2server.rs2.domain.service.api.content.RunePouchService;
import org.rs2server.rs2.domain.service.api.content.StaffService;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.container.impl.InterfaceContainerListener;
import org.rs2server.rs2.model.player.Player;
import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Clank1337
 */
public class RunePouchServiceImpl implements RunePouchService {

	public static final int SIZE = 3;

	enum RuneVarp {

		AIR(1, 556),

		WATER(2, 555),

		EARTH(3, 557),

		FIRE(4, 554),

		MIND(5, 558),

		CHAOS(6, 562),

		DEATH(7, 560),

		BLOOD(8, 565),

		COSMIC(9, 564),

		NATURE(10, 561),

		LAW(11, 563),

		BODY(12, 559),

		SOUL(13, 566),

		ASTRAL(14, 9075);

		private final int varp;
		private final int rune;

		RuneVarp(int varp, int rune) {
			this.varp = varp;
			this.rune = rune;
		}

		static Optional<RuneVarp> of(int rune) {
			return Arrays.stream(RuneVarp.values()).filter(i -> i.getRune() == rune).findAny();
		}

		public int getVarp() {
			return varp;
		}

		public int getRune() {
			return rune;
		}
	}

	private static final int RUNE_POUCH_WIDGET = 190;

	private final PlayerVariableService variableService;

	@Inject
	RunePouchServiceImpl(HookService hookService, PlayerVariableService variableService) {
		hookService.register(this);
		this.variableService = variableService;
	}

	@SuppressWarnings("incomplete-switch")
	@Subscribe
	public final void onItemClick(@Nonnull GameItemInventoryActionEvent event) {
		if (event.getClickType() != GameItemInventoryActionEvent.ClickType.OPTION_1
				&& event.getClickType() != GameItemInventoryActionEvent.ClickType.OPTION_4) {
			return;
		}

		Player player = event.getPlayer();
		Item item = event.getItem();
		if (player == null || item == null || item.getId() != 12791)
			return;
		switch (event.getClickType()) {
		case OPTION_1:
			openPouchInterface(player);
			break;
		case OPTION_4:
			emptyPouch(player);
			break;
		}
	}

	@Subscribe
	public final void onWidgetClick(@Nonnull GameInterfaceButtonEvent event) {
		if (event.getInterfaceId() != RUNE_POUCH_WIDGET) {
			return;
		}
		Player player = event.getPlayer();
		if (player != null) {
			switch (event.getButton()) {
			case 4:
				switch (event.getMenuIndex()) {
				case 0:
					withdraw(player, event.getChildButton(), event.getChildButton2(), 1);
					break;
				case 1:
					withdraw(player, event.getChildButton(), event.getChildButton2(), 5);
					break;
				case 2:
					withdraw(player, event.getChildButton(), event.getChildButton2(),
							player.getRunePouch().getCount(event.getChildButton2()));
					break;
				}
				break;
			default:
				Item item = player.getInventory().get(event.getChildButton());
				if (item != null) {
					switch (event.getMenuIndex()) {
					case 0:
						deposit(player, event.getChildButton(), event.getChildButton2(), 1);
						break;
					case 1:
						deposit(player, event.getChildButton(), event.getChildButton2(), 5);
						break;
					case 2:
						deposit(player, event.getChildButton(), event.getChildButton2(),
								player.getInventory().getCount(event.getChildButton2()));
						break;
					}

				}
				break;
			}
		}
	}

	@Override
	public void openPouchInterface(@Nonnull Player player) {
		player.getActionSender().sendInterface(190, false).sendInterfaceInventory(149)
				.sendAccessMask(0, 548, 48, -1, -1).sendAccessMask(1311774, 190, 4, 0, 2)
				.sendAccessMask(1311774, 190, 8, 0, 27).sendCS2Script(915, new Object[] { 3 }, "i");

		player.getInterfaceState().addListener(player.getInventory(),
				new InterfaceContainerListener(player, -1, 64209, 93));
	}

	@Override
	public void deposit(@Nonnull Player player, int slot, int id, int amount) {
		Item item = player.getInventory().get(slot);
		player.getActionSender().removeChatboxInterface();
		if (item == null || item.getId() != id || !item.getDefinition().isTradable()) {
			player.getActionSender().sendMessage("This item cannot be added to your rune pouch.");
			return; // invalid packet, or client out of sync
		}
		if (amount > 8000)
			amount = 8000;
		
		Optional<StaffService.Runes> runesOptional = StaffService.Runes.of(item.getId());
		if (runesOptional.isPresent()) {
			boolean inventoryFiringEvents = player.getInventory().isFiringEvents();
			player.getInventory().setFiringEvents(false);
			try {
				int transferAmount = player.getInventory().getCount(id);
				if (transferAmount >= amount)
					transferAmount = amount;
				else if (transferAmount == 0)
					return; // invalid packet, or client out of sync
				if (player.getRunePouch().getSlotById(item.getId()) != -1) {
					Item rune = player.getRunePouch().get(player.getRunePouch().getSlotById(item.getId()));
					if (rune != null && rune.getCount() + transferAmount > 8000)
						transferAmount = 8000 - rune.getCount();
				}
				if (player.getRunePouch().add(new Item(item.getId(), transferAmount), -1))
					player.getInventory().remove(new Item(item.getId(), transferAmount));
				player.getInventory().fireItemsChanged();
			} finally {
				player.getInventory().setFiringEvents(inventoryFiringEvents);
				player.getActionSender().removeChatboxInterface();
				updatePouchInterface(player);
			}
		} else
			player.sendMessage("Only runes can be deposited into the rune pouch.");
	}

	@Override
	public void withdraw(@Nonnull Player player, int slot, int id, int amount) {
		boolean inventoryFiringEvents = player.getInventory().isFiringEvents();
		player.getInventory().setFiringEvents(false);
		try {
			Item item = player.getRunePouch().get(slot);
			if (item == null || item.getId() != id)
				return; // invalid packet, or client out of sync
			int transferAmount = player.getRunePouch().getCount(id);
			if (transferAmount >= amount)
				transferAmount = amount;
			else if (transferAmount == 0)
				return; // invalid packet, or client out of sync

			if (player.getInventory().add(new Item(item.getId(), transferAmount), -1))
				player.getRunePouch().remove(new Item(item.getId(), transferAmount));
			player.getInventory().fireItemsChanged();
		} finally {
			player.getInventory().setFiringEvents(inventoryFiringEvents);
			player.getActionSender().removeChatboxInterface();
			updatePouchInterface(player);
		}
	}

	@Override
	public void emptyPouch(@Nonnull Player player) {
		player.getRunePouch().stream().filter(Objects::nonNull).forEach(i -> {
			if (player.getInventory().add(new Item(i.getId(), player.getRunePouch().getCount(i.getId()))))
				player.getRunePouch().remove(new Item(i.getId(), player.getRunePouch().getCount(i.getId())));
		});
		updatePouchInterface(player);
	}

	@Override
	public void updatePouchInterface(@Nonnull Player player) {
		Item slotOne = player.getRunePouch().get(0);
		Item slotTwo = player.getRunePouch().get(1);
		Item slotThree = player.getRunePouch().get(2);
		if (slotOne != null) {
			Optional<RuneVarp> varpOptional = RuneVarp.of(slotOne.getId());
			if (varpOptional.isPresent()) {
				RuneVarp varp = varpOptional.get();
				variableService.set(player, RunePouchVariables.FIRST_SLOT_RUNE, varp.getVarp());
				variableService.set(player, RunePouchVariables.FIRST_SLOT_AMOUNT, slotOne.getCount());
				variableService.send(player, RunePouchVariables.FIRST_SLOT_RUNE);
				variableService.send(player, RunePouchVariables.FIRST_SLOT_AMOUNT);
			}
		} else {
			variableService.set(player, RunePouchVariables.FIRST_SLOT_RUNE, 0);
			variableService.set(player, RunePouchVariables.FIRST_SLOT_AMOUNT, 0);
			variableService.send(player, RunePouchVariables.FIRST_SLOT_RUNE);
			variableService.send(player, RunePouchVariables.FIRST_SLOT_AMOUNT);
		}
		if (slotTwo != null) {
			Optional<RuneVarp> varpOptional = RuneVarp.of(slotTwo.getId());
			if (varpOptional.isPresent()) {
				RuneVarp varp = varpOptional.get();
				variableService.set(player, RunePouchVariables.SECOND_SLOT_RUNE, varp.getVarp());
				variableService.set(player, RunePouchVariables.SECOND_SLOT_AMOUNT, slotTwo.getCount());
				variableService.send(player, RunePouchVariables.SECOND_SLOT_RUNE);
				variableService.send(player, RunePouchVariables.SECOND_SLOT_AMOUNT);
			}
		} else {
			variableService.set(player, RunePouchVariables.SECOND_SLOT_RUNE, 0);
			variableService.set(player, RunePouchVariables.SECOND_SLOT_AMOUNT, 0);
			variableService.send(player, RunePouchVariables.SECOND_SLOT_RUNE);
			variableService.send(player, RunePouchVariables.SECOND_SLOT_AMOUNT);
		}
		if (slotThree != null) {
			Optional<RuneVarp> varpOptional = RuneVarp.of(slotThree.getId());
			if (varpOptional.isPresent()) {
				RuneVarp varp = varpOptional.get();
				variableService.set(player, RunePouchVariables.THIRD_SLOT_RUNE, varp.getVarp());
				variableService.set(player, RunePouchVariables.THIRD_SLOT_AMOUNT, slotThree.getCount());
				variableService.send(player, RunePouchVariables.THIRD_SLOT_RUNE);
				variableService.send(player, RunePouchVariables.THIRD_SLOT_AMOUNT);
			}
		} else {
			variableService.set(player, RunePouchVariables.THIRD_SLOT_RUNE, 0);
			variableService.set(player, RunePouchVariables.THIRD_SLOT_AMOUNT, 0);
			variableService.send(player, RunePouchVariables.THIRD_SLOT_RUNE);
			variableService.send(player, RunePouchVariables.THIRD_SLOT_AMOUNT);
		}
	}
}
