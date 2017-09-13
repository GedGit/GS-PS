package org.rs2server.rs2.domain.service.impl.content;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import org.rs2server.rs2.content.api.GameItemInventoryActionEvent;
import org.rs2server.rs2.domain.service.api.*;
import org.rs2server.rs2.domain.service.api.content.HerbSackService;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.ActionSender;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * @author Vichy
 */
public class HerbSackServiceImpl implements HerbSackService {

	@Inject
	HerbSackServiceImpl(HookService hookService, PlayerVariableService variableService) {
		hookService.register(this);
	}

	// How many of each herb can be stored in the sack.
	private final int SIZE = 30;

	@Override
	public void deposit(Player player) {
		Map<HERBS, Integer> herbSack = player.getDatabaseEntity().getHerbSack();
		Arrays.stream(HERBS.values()).filter(i -> player.getInventory().contains(i.getHerbId())).forEach(i -> {
			int amount = getAmount(player, i);
			if (amount >= SIZE) {
				player.getActionSender().sendMessage("You can't deposit more then " + SIZE + " of each herb type.");
				return;
			}
			int transferAmount = player.getInventory().getCount(i.getHerbId()) + amount;
			if (transferAmount > SIZE)
				transferAmount = SIZE - amount;

			player.getInventory().remove(new Item(i.getHerbId(), transferAmount));
			herbSack.put(i, transferAmount);
		});
	}

	@Override
	public void withdraw(Player player, HERBS herb) {
		Map<HERBS, Integer> herbSack = player.getDatabaseEntity().getHerbSack();
		int freeSlots = player.getInventory().freeSlots();
		int amount = getAmount(player, herb);
		if (amount <= 0 || freeSlots <= 0)
			return;
		int transferAmount = amount;
		if (transferAmount > freeSlots)
			transferAmount = freeSlots;
		if (player.getInventory().add(new Item(herb.getHerbId(), transferAmount))) {
			int herbsLeft = amount - transferAmount;
			if (herbsLeft <= 0)
				herbSack.remove(herb);
			else
				herbSack.put(herb, herbsLeft);
		}
	}

	@Override
	public void check(Player player) {
		StringBuilder sb = new StringBuilder();
		Map<HERBS, Integer> herbSack = player.getDatabaseEntity().getHerbSack();
		for (@SuppressWarnings("rawtypes")
		Map.Entry pair : herbSack.entrySet()) {
			HERBS key = (HERBS) pair.getKey();
			int value = (int) pair.getValue();
			sb.append(key.getName()).append(" (").append(value).append("), ");
		}
		if (sb.toString().isEmpty())
			sb.append("Your herb sack is empty.");
		player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 203, null,
				sb.toString());
	}

	@Override
	public int getAmount(@Nonnull Player player, HERBS herb) {
		Map<HERBS, Integer> herbSack = player.getDatabaseEntity().getHerbSack();
		return herbSack.get(herb) != null ? herbSack.get(herb) : 0;
	}

	@Override
	public int getBagSize(@Nonnull Player player) {
		Optional<Integer> bagSizeOptional = player.getDatabaseEntity().getHerbSack().values().stream()
				.reduce(((a, b) -> a + b));
		return bagSizeOptional.isPresent() ? bagSizeOptional.get() : 0;
	}

	@Subscribe
	public final void onItemClick(@Nonnull GameItemInventoryActionEvent event) {
		if (event.getClickType() != GameItemInventoryActionEvent.ClickType.OPTION_1
				&& event.getClickType() != GameItemInventoryActionEvent.ClickType.OPTION_2
				&& event.getClickType() != GameItemInventoryActionEvent.ClickType.OPTION_4) {
			return;
		}

		Player player = event.getPlayer();
		Item item = event.getItem();
		if (item.getId() != 13226)
			return;
		switch (event.getClickType()) {
		case OPTION_1:
			deposit(player);
			break;
		case OPTION_2:
			for (HERBS type : HERBS.values())
			withdraw(player, type);
			break;
		case OPTION_4:
			check(player);
			break;
		default:
			break;
		}
	}
}
