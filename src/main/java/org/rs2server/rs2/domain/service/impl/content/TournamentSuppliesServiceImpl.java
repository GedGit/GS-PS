package org.rs2server.rs2.domain.service.impl.content;

import java.util.Random;

import javax.annotation.Nonnull;

import org.rs2server.rs2.content.api.GameInterfaceButtonEvent;
import org.rs2server.rs2.domain.service.api.GroundItemService;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.domain.service.api.PlayerService;
import org.rs2server.rs2.domain.service.api.PlayerVariableService;
import org.rs2server.rs2.domain.service.api.content.ItemService;
import org.rs2server.rs2.domain.service.api.content.TournamentSuppliesService;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.bit.component.Access;
import org.rs2server.rs2.model.bit.component.AccessBits;
import org.rs2server.rs2.model.bit.component.NumberRange;
import org.rs2server.rs2.model.player.Player;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

public class TournamentSuppliesServiceImpl implements TournamentSuppliesService {

	@SuppressWarnings("unused")
	private final PlayerVariableService variableService;
	@SuppressWarnings("unused")
	private final ItemService itemService;
	@SuppressWarnings("unused")
	private final GroundItemService groundItemService;
	@SuppressWarnings("unused")
	private final PlayerService playerService;

	@SuppressWarnings("unused")
	private static final Random RANDOM = new Random();

	private static final int TOURNAMENT_WIDGET_ID = 100;

	private static final Access TOURNAMENT_SHOP_ACCESS = Access.of(TOURNAMENT_WIDGET_ID, 3, NumberRange.of(0, 293),
			AccessBits.optionBit(10));

	@Inject
	TournamentSuppliesServiceImpl(PlayerVariableService variableService, ItemService itemService,
			HookService hookService, GroundItemService groundItemService, PlayerService playerService) {
		this.variableService = variableService;
		this.itemService = itemService;
		this.groundItemService = groundItemService;
		this.playerService = playerService;
		hookService.register(this);
	}

	private static int getAmountForMenuIndex(int index) {
		switch (index) {
		case 0:
			return index;
		case 1:
			return 5;
		case 2:
			return 10;
		case 3:
			return 50;
		default:
			return 0;
		}
	}

	@Override
	@Subscribe
	public void onTournamentShopClick(@Nonnull GameInterfaceButtonEvent event) {
		if (event.getInterfaceId() == TOURNAMENT_WIDGET_ID) {
			Item clickedItem = new Item(event.getChildButton2(), getAmountForMenuIndex(event.getMenuIndex()));
			
			if (clickedItem.getCount() >= 1) {
				if (event.getPlayer().getInventory().add(new Item(clickedItem.getId(), clickedItem.getCount())))
					return;
				else
					event.getPlayer().getActionSender().sendMessage("Not enough space in your inventory.");
			}
		}
	}

	@Override
	public void openTournamentShop(@Nonnull Player player) {
		player.getActionSender().sendInterface(TOURNAMENT_WIDGET_ID, false);
		player.sendAccess(TOURNAMENT_SHOP_ACCESS);
	}
}