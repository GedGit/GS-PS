package org.rs2server.rs2.domain.service.impl.content;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import org.rs2server.rs2.content.api.GameItemInventoryActionEvent;
import org.rs2server.rs2.content.api.GameMobDeathEvent;
import org.rs2server.rs2.domain.service.api.GroundItemService;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.domain.service.api.content.GemBagService;
import org.rs2server.rs2.model.DialogueManager;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.boundary.BoundaryManager;
import org.rs2server.rs2.model.minigame.fightcave.FightCave;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.ActionSender;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * @author Clank1337
 */
public class GemBagServiceImpl implements GemBagService {

	private static final int GEM_BAG = 12020;

	private static final int SIZE = 60;

	private final GroundItemService groundItemService;

	@Inject
	GemBagServiceImpl(HookService service, GroundItemService groundItemService) {
		service.register(this);
		this.groundItemService = groundItemService;
	}

	@SuppressWarnings("rawtypes")
	@Subscribe
	public final void onMobDeath(final GameMobDeathEvent event) {
		final Mob mob = event.getMob();
		final Mob k = event.getKiller();
		if (mob.isPlayer()) {
			Player player = (Player) mob;

			Map<Gems, Integer> gemBag = player.getDatabaseEntity().getGemBag();
			if (gemBag.size() <= 0 || BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "Zulrah")
					|| BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "ClanWarsFFAFull") || BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "PestControl") || player.getRFD().isStarted() || FightCave.IN_CAVES.contains(player)) {
				return;
			}
			for (Map.Entry pair : gemBag.entrySet()) {
				Gems key = (Gems) pair.getKey();
				int value = (int) pair.getValue();
				groundItemService.createGroundItem(k.isPlayer() ? (Player) k : player,
						new GroundItemService.GroundItem(new Item(key.getGemId() + 1, value), player.getLocation(), k.isPlayer() ? (Player) k : player, false, k.isPlayer() && k != player, k.isPlayer() && k == player));
			}
			gemBag.clear();
		}
	}

	@Override
	public void deposit(@Nonnull Player player) {
		Map<Gems, Integer> gemBag = player.getDatabaseEntity().getGemBag();
		Arrays.stream(Gems.values()).filter(i -> player.getInventory().contains(i.getGemId())).forEach(i -> {
			int amount = getAmount(player, i);
			if (amount >= SIZE) {
				player.getActionSender().sendMessage("You can't deposit more then 60 of each gem type.");
				return;
			}
			int transferAmount = player.getInventory().getCount(i.getGemId()) + amount;
			if (transferAmount > SIZE) {
				transferAmount = SIZE - amount;
			}

			player.getInventory().remove(new Item(i.getGemId(), transferAmount));
			int toAdd = amount + transferAmount > SIZE ? SIZE : amount + transferAmount;
			gemBag.put(i, toAdd);
		});
	}

	@Override
	public void withdraw(@Nonnull Player player, Gems type) {
		Map<Gems, Integer> gemBag = player.getDatabaseEntity().getGemBag();
		int freeSlots = player.getInventory().freeSlots();
		int amount = getAmount(player, type);
		if (amount <= 0 || freeSlots <= 0) {
			return;
		}
		int transferAmount = amount;
		if (transferAmount > freeSlots) {
			transferAmount = freeSlots;
		}
		if (player.getInventory().add(new Item(type.getGemId(), transferAmount))) {
			int gemsLeft = amount - transferAmount;
			if (gemsLeft <= 0) {
				gemBag.remove(type);
			} else {
				gemBag.put(type, gemsLeft);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void check(@Nonnull Player player) {
		StringBuilder sb = new StringBuilder();
		Map<Gems, Integer> gemBag = player.getDatabaseEntity().getGemBag();
		for (Map.Entry pair : gemBag.entrySet()) {
			Gems key = (Gems) pair.getKey();
			int value = (int) pair.getValue();
			sb.append(key.getName()).append(" (").append(value).append("), ");
		}
		player.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE_MODEL_LEFT, 1623, null, sb.toString());
	}


	@SuppressWarnings("incomplete-switch")
	@Subscribe
	public void onItemClick(GameItemInventoryActionEvent event) {
		Player player = event.getPlayer();
		Item item = event.getItem();
		if (item.getId() != GEM_BAG) {
			return;
		}
		switch (event.getClickType()) {
			case OPTION_1:
				deposit(player);
				break;
			case OPTION_2:
				check(player);
				break;
			case OPTION_4:
				player.getActionSender().removeChatboxInterface();
				DialogueManager.openDialogue(player, 12020);
				break;
		}
	}


	@Override
	public int getAmount(@Nonnull Player player, Gems type) {
		Map<Gems, Integer> gemBag = player.getDatabaseEntity().getGemBag();
		return gemBag.get(type) != null ? gemBag.get(type) : 0;
	}

	@Override
	public int getBagSize(@Nonnull Player player) {
		Optional<Integer> bagSizeOptional = player.getDatabaseEntity().getGemBag().values().stream().reduce(((a, b) -> a + b));
		return bagSizeOptional.isPresent() ? bagSizeOptional.get() : 0;
	}
}
