package org.rs2server.rs2.domain.service.api.content.trade;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.rs2server.Server;
import org.rs2server.rs2.content.api.GamePlayerTradeEvent;
import org.rs2server.rs2.domain.service.api.GroundItemService;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.player.Player;

import java.util.*;

/**
 * @author twelve
 */
public final @Getter @EqualsAndHashCode @ToString
class Transaction {

	private final TradeContainer traderContainer;
	private final TradeContainer partnerContainer;
	private final TradeService service;
	private final GroundItemService groundItemService;
	private final HookService hookService;

	public Transaction(Player player, Player other) {
		this.traderContainer = new TradeContainer(player);
		this.partnerContainer = new TradeContainer(other);
		this.service = Server.getInjector().getInstance(TradeService.class);
		this.groundItemService = Server.getInjector().getInstance(GroundItemService.class);
		this.hookService = Server.getInjector().getInstance(HookService.class);

		player.setTransaction(this);
		other.setTransaction(this);
	}

	public final Optional<TradeContainer> get(Player self) {
		if (traderContainer.getPlayer().equals(self)) {
			return Optional.of(traderContainer);
		}

		if (partnerContainer.getPlayer().equals(self)) {
			return Optional.of(partnerContainer);
		}
		return Optional.empty();
	}

	public final Optional<TradeContainer> getOther(Player self) {
		if (traderContainer.getPlayer().equals(self)) {
			return Optional.of(partnerContainer);
		}

		if (partnerContainer.getPlayer().equals(self)) {
			return Optional.of(traderContainer);
		}
		return Optional.empty();
	}

	public final boolean add(TradeContainer container, Item item) {
		int preferredAmount = container.getPlayer().getInventory().getCount(item.getId());
		if (item.getCount() > preferredAmount) {
			item.setCount(preferredAmount);
		}
		if (container.getPlayer().getInventory().remove(item) != 0 && container.add(item)) {
			traderContainer.setAccepted(false);
			partnerContainer.setAccepted(false);
			service.resetStrings(traderContainer.getPlayer());
			service.resetStrings(partnerContainer.getPlayer());
			service.setAvailableInventorySpace(this);
			return true;
		}
		return false;
	}

	public final boolean remove(TradeContainer container, IndexedTradeItem tradeItem) {
		Item item = tradeItem.getItem();
		int preferredAmount = container.getCount(item.getId());
		if (item.getCount() > preferredAmount) {
			item.setCount(preferredAmount);
		}
		if (container.remove(tradeItem.getIndex(), item) != 0 && container.getPlayer().getInventory().add(item)) {
			traderContainer.setAccepted(false);
			partnerContainer.setAccepted(false);
			service.resetStrings(traderContainer.getPlayer());
			service.resetStrings(partnerContainer.getPlayer());

			container.getPlayer().getActionSender().sendCS2Script(765, new Object[] {tradeItem.getIndex(), 0}, "1i");
			getOther(container.getPlayer()).get().getPlayer().getActionSender().sendCS2Script(765, new Object[] {tradeItem.getIndex(), 1}, "1i");
			service.setAvailableInventorySpace(this);
			return true;
		}
		return false;
	}

	public final void complete() {// TODO Capacity checks and implement accept button
		Player player = traderContainer.getPlayer();
		Player other = getOther(player).get().getPlayer();
		hookService.post(new GamePlayerTradeEvent(player, other, traderContainer, partnerContainer));
		hookService.post(new GamePlayerTradeEvent(other, player, partnerContainer, traderContainer));
		for (Item item : traderContainer) {
			if (other.getInventory().getCount(item.getId()) + item.getCount() < 0) {
				groundItemService.createGroundItem(other, new GroundItemService.GroundItem(item, other.getLocation(), other, false));
			}
			other.getInventory().add(item);
		}

		for (Item item : partnerContainer) {
			if (player.getInventory().getCount(item.getId()) + item.getCount() < 0) {
				groundItemService.createGroundItem(player, new GroundItemService.GroundItem(item, player.getLocation(), player, false));
			}
			player.getInventory().add(item);
		}
	}

	public final void cancel() {
		Player player = traderContainer.getPlayer();
		Player other = partnerContainer.getPlayer();

		for (Item item : traderContainer) {
			player.getInventory().add(item);
		}

		for (Item item : partnerContainer) {
			other.getInventory().add(item);
		}
	}

	public TradeContainer getPartnerContainer() {
		// TODO Auto-generated method stub
		return partnerContainer;
	}

	public TradeContainer getTraderContainer() {
		// TODO Auto-generated method stub
		return traderContainer;
	}
}
