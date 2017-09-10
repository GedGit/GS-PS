package org.rs2server.rs2.domain.service.impl.content.gamble;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.rs2server.Server;
import org.rs2server.rs2.content.api.GamePlayerTradeEvent;
import org.rs2server.rs2.domain.service.api.GroundItemService;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.domain.service.api.PlayerService;
import org.rs2server.rs2.domain.service.api.content.gamble.DiceGameService;
import org.rs2server.rs2.domain.service.api.content.gamble.IndexedDiceItem;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.player.Player;

import java.util.Optional;

/**
 * @author Clank1337
 */
public final @Getter @EqualsAndHashCode @ToString class DiceGameTransaction {

	private final DiceGameContainer playerContainer;
	private final DiceGameContainer partnerContainer;
	private final DiceGameService service;
	private final GroundItemService groundItemService;
	private final HookService hookService;
	private boolean dueling;

	public DiceGameTransaction(Player player, Player other) {
		this.playerContainer = new DiceGameContainer(player);
		this.partnerContainer = new DiceGameContainer(other);
		this.service = Server.getInjector().getInstance(DiceGameService.class);
		this.groundItemService = Server.getInjector().getInstance(GroundItemService.class);
		this.hookService = Server.getInjector().getInstance(HookService.class);
		Server.getInjector().getInstance(PlayerService.class);

		player.setDiceTransaction(this);
		other.setDiceTransaction(this);
	}

	public final Optional<DiceGameContainer> get(Player self) {
		if (playerContainer.getPlayer().equals(self)) {
			return Optional.of(playerContainer);
		}

		if (partnerContainer.getPlayer().equals(self)) {
			return Optional.of(partnerContainer);
		}
		return Optional.empty();
	}

	public final Optional<DiceGameContainer> getOther(Player self) {
		if (playerContainer.getPlayer().equals(self)) {
			return Optional.of(partnerContainer);
		}

		if (partnerContainer.getPlayer().equals(self)) {
			return Optional.of(playerContainer);
		}
		return Optional.empty();
	}

	public final boolean add(DiceGameContainer container, Item item) {
		int preferredAmount = container.getPlayer().getInventory().getCount(item.getId());
		if (item.getCount() > preferredAmount) {
			item.setCount(preferredAmount);
		}
		if (container.getPlayer().getInventory().remove(item) != 0 && container.add(item)) {
			playerContainer.setAccepted(false);
			partnerContainer.setAccepted(false);
			service.resetStrings(playerContainer.getPlayer());
			service.resetStrings(partnerContainer.getPlayer());
			service.setAvailableInventorySpace(this);
			return true;
		}
		return false;
	}

	public final boolean remove(DiceGameContainer container, IndexedDiceItem tradeItem) {
		Item item = tradeItem.getItem();
		int preferredAmount = container.getCount(item.getId());
		if (item.getCount() > preferredAmount) {
			item.setCount(preferredAmount);
		}
		if (container.remove(tradeItem.getIndex(), item) != 0 && container.getPlayer().getInventory().add(item)) {
			playerContainer.setAccepted(false);
			partnerContainer.setAccepted(false);
			service.resetStrings(playerContainer.getPlayer());
			service.resetStrings(partnerContainer.getPlayer());

			container.getPlayer().getActionSender().sendCS2Script(765, new Object[] { tradeItem.getIndex(), 0 }, "1i");
			getOther(container.getPlayer()).get().getPlayer().getActionSender().sendCS2Script(765,
					new Object[] { tradeItem.getIndex(), 1 }, "1i");
			service.setAvailableInventorySpace(this);
			return true;
		}
		return false;
	}

	public final void complete() {// TODO Capacity checks and implement accept
									// button
		Player player = playerContainer.getPlayer();
		Player other = getOther(player).get().getPlayer();
		hookService.post(new GamePlayerTradeEvent(player, other, playerContainer, partnerContainer));
		hookService.post(new GamePlayerTradeEvent(other, player, partnerContainer, playerContainer));
		for (Item item : playerContainer) {
			if (other.getInventory().getCount(item.getId()) + item.getCount() < 0) {
				groundItemService.createGroundItem(other,
						new GroundItemService.GroundItem(item, other.getLocation(), other, false));
			}
			other.getInventory().add(item);
		}

		for (Item item : partnerContainer) {
			if (player.getInventory().getCount(item.getId()) + item.getCount() < 0) {
				groundItemService.createGroundItem(player,
						new GroundItemService.GroundItem(item, player.getLocation(), player, false));
			}
			player.getInventory().add(item);
		}
	}

	public final void complete(Player winner) {// TODO Capacity checks and
												// implement accept button
		for (Item item : playerContainer) {
			if (winner.getInventory().getCount(item.getId()) + item.getCount() < 0) {
				groundItemService.createGroundItem(winner,
						new GroundItemService.GroundItem(item, winner.getLocation(), winner, false));
			}
			winner.getInventory().add(item);
		}

		for (Item item : partnerContainer) {
			if (winner.getInventory().getCount(item.getId()) + item.getCount() < 0) {
				groundItemService.createGroundItem(winner,
						new GroundItemService.GroundItem(item, winner.getLocation(), winner, false));
			}
			winner.getInventory().add(item);
		}
	}

	public final void cancel() {
		Player player = playerContainer.getPlayer();
		Player other = partnerContainer.getPlayer();

		for (Item item : playerContainer) {
			player.getInventory().add(item);
		}

		for (Item item : partnerContainer) {
			other.getInventory().add(item);
		}
	}

	public void setDueling(boolean dueling) {
		this.dueling = dueling;
	}

	public DiceGameContainer getPlayerContainer() {
		return playerContainer;
	}

	public DiceGameContainer getPartnerContainer() {
		return partnerContainer;
	}

	public boolean isDueling() {
		return dueling;
	}
}
