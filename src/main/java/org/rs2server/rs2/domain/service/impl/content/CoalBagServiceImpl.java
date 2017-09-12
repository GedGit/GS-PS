package org.rs2server.rs2.domain.service.impl.content;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import org.rs2server.rs2.content.api.GameInterfaceButtonEvent;
import org.rs2server.rs2.content.api.GameItemInventoryActionEvent;
import org.rs2server.rs2.content.api.GameMobDeathEvent;
import org.rs2server.rs2.domain.service.api.GroundItemService;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.domain.service.api.content.CoalBagService;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.boundary.BoundaryManager;
import org.rs2server.rs2.model.minigame.impl.fightcave.FightCave;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;

/**
 * @author Clank1337
 */
public class CoalBagServiceImpl implements CoalBagService {

	private static final int SIZE = 27;
	private static final int COAL = 453;
	private static final int COAL_BAG = 12019;

	private final GroundItemService groundItemService;

	@Inject
	CoalBagServiceImpl(HookService service, GroundItemService groundItemService) {
		service.register(this);
		this.groundItemService = groundItemService;
	}

	@Override
	public void deposit(@Nonnull Player player) {
		int coalAmount = player.getDatabaseEntity().getCoalBagAmount();
		int amount = player.getInventory().getCount(COAL);
		if (amount <= 0 || coalAmount >= SIZE) {
			return;
		}
		int transferAmount = amount + coalAmount;
		if (transferAmount > SIZE) {
			transferAmount = SIZE - coalAmount;
		}
		player.getInventory().remove(new Item(COAL, transferAmount));
		player.getDatabaseEntity().setCoalBagAmount(transferAmount);
	}

	@Override
	public void withdraw(@Nonnull Player player) {
		int coalAmount = player.getDatabaseEntity().getCoalBagAmount();
		if (coalAmount <= 0) {
			return;
		}
		if (coalAmount > player.getInventory().freeSlots()) {
			coalAmount = player.getInventory().freeSlots();
		}
		if (player.getInventory().add(new Item(COAL, coalAmount))) {
			player.getDatabaseEntity().setCoalBagAmount(player.getDatabaseEntity().getCoalBagAmount() - coalAmount);
		}
	}

	@Override
	public void check(@Nonnull Player player) {
		int coalAmount = player.getDatabaseEntity().getCoalBagAmount();
		player.getActionSender().sendMessage("Your Coal bag currently contains " + coalAmount + " coal.");
	}

	@Subscribe
	public final void onMobDeath(final GameMobDeathEvent event) {
		final Mob mob = event.getMob();
		final Mob k = event.getKiller();
		if (mob.isPlayer()) {
			Player player = (Player) mob;
			if (player.getLootingBag().size() <= 0 || BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "Zulrah")
					|| BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "PestControl") || player.getRFD().isStarted() || FightCave.IN_CAVES.contains(player)) {
				return;
			}
			int coalAmount = player.getDatabaseEntity().getCoalBagAmount();
			if (coalAmount > 0) {
				groundItemService.createGroundItem(k.isPlayer() ? (Player) k : player,
						new GroundItemService.GroundItem(new Item(453, coalAmount), player.getLocation(), k.isPlayer() ? (Player) k : player, false, k.isPlayer() && k != player, k.isPlayer() && k == player));
				player.getDatabaseEntity().setCoalBagAmount(0);
			}
		}
	}

	@SuppressWarnings("incomplete-switch")
	@Subscribe
	public void onItemClick(GameItemInventoryActionEvent event) {
		Player player = event.getPlayer();
		Item item = event.getItem();
		if (item.getId() != COAL_BAG) {
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
				withdraw(player);
				break;
		}
	}

	@Subscribe
	public void onButtonClick(GameInterfaceButtonEvent event) {
		Player player = event.getPlayer();
		if (event.getChildButton2() != COAL_BAG || !player.getInterfaceState().isInterfaceOpen(12)) {
			return;
		}
		switch (event.getMenuIndex()) {
			case 8:
				withdraw(player);
				break;
		}
	}
}
