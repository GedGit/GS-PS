package org.rs2server.rs2.domain.service.impl.content.trade;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import org.rs2server.rs2.Constants;
import org.rs2server.rs2.content.api.GameInterfaceButtonEvent;
import org.rs2server.rs2.content.api.GamePlayerLogoutEvent;
import org.rs2server.rs2.content.api.GameTradeRequestEvent;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.domain.service.api.PermissionService;
import org.rs2server.rs2.domain.service.api.PlayerVariableService;
import org.rs2server.rs2.domain.service.api.content.trade.*;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.bit.component.Access;
import org.rs2server.rs2.model.bit.component.AccessBits;
import org.rs2server.rs2.model.bit.component.BitPackedValue;
import org.rs2server.rs2.model.bit.component.NumberRange;
import org.rs2server.rs2.model.container.Container;
import org.rs2server.rs2.model.container.impl.InterfaceContainerListener;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

/**
 * @author twelve
 */
public final class TradeServiceImpl implements TradeService {

	public static final int CONFIRMATION_WIDGET = 334;
	public static final int TRADE_WIDGET = 335;
	public static final int TRADE_INVENTORY_WIDGET = 336;

	private static final int LEFT_TRADE_COMPONENT = 25;
	private static final int RIGHT_TRADE_COMPONENT = 28;

	private static final int FREE_INVENTORY_SLOTS_COMPONENT = 9;
	private static final int TRADING_WITH_COMPONENT = 31;

	public static final int COMPONENT_CAPACITY = 28;

	private static final BitPackedValue OPTIONS_ACCESS = AccessBits.optionBit(10);
	private static final Access TRADE_ACCESS_SELF = Access.of(TRADE_WIDGET, LEFT_TRADE_COMPONENT,
			NumberRange.of(0, COMPONENT_CAPACITY), OPTIONS_ACCESS);
	private static final Access TRADE_ACCESS_OTHER = Access.of(TRADE_WIDGET, RIGHT_TRADE_COMPONENT,
			NumberRange.of(0, COMPONENT_CAPACITY), OPTIONS_ACCESS);

	private static final Access TRADE_INVENTORY_ACCESS = Access.of(TRADE_INVENTORY_WIDGET, 0,
			NumberRange.of(0, COMPONENT_CAPACITY), OPTIONS_ACCESS);

	private static final Object[] TRADE_PARAMETERS_2 = new Object[] { "", "", "", "", "Remove-X", "Remove-All",
			"Remove-10", "Remove-5", "Remove", -1, 0, 7, 4, 81, 335 << 16 | 24 };
	private static final Object[] TRADE_PARAMETERS_1 = new Object[] { -2, 0, 7, 4, 80, 335 << 16 | 27 };

	private static final int ACCEPT_BUTTON = 10;
	private static final int DECLINE_BUTTON = 13;
	private static final int WAITING_CHILD = 30;
	private static final int SECOND_TRADING_WITH = 30;
	private static final int SECOND_WAITING_CHILD = 4;
	private static final int SECOND_ACCEPT_BUTTON = 13;
	private static final int SECOND_DECLINE_BUTTON = 14;
	private static final int PLAYER_CONTAINER_PRICE = 24;
	private static final int PARTNER_CONTAINER_PRICE = 27;

	private final PermissionService permissionService;
	private final PlayerVariableService playerVariableService;

	@Inject
	TradeServiceImpl(HookService hookService, PermissionService permissionService,
			PlayerVariableService playerVariableService) {
		hookService.register(this);
		this.permissionService = permissionService;
		this.playerVariableService = playerVariableService;
	}

	@Override
	@Subscribe
	public final void onLogout(@Nonnull GamePlayerLogoutEvent event) {
		Player player = event.getPlayer();

		Transaction transaction = player.getTransaction();
		if (transaction != null) {
			endTransaction(player.getTransaction(), true);
		}
	}

	@Override
	@Subscribe
	public final void onTradeRequest(@Nonnull GameTradeRequestEvent tradeRequest) {

		Player trader = tradeRequest.getPlayer();
		Player partner = tradeRequest.getPartner();

		if ((permissionService.is(trader, PermissionService.PlayerPermissions.IRON_MAN)
				|| permissionService.is(partner, PermissionService.PlayerPermissions.IRON_MAN))
				|| (permissionService.is(trader, PermissionService.PlayerPermissions.HARDCORE_IRON_MAN)
						|| permissionService.is(partner, PermissionService.PlayerPermissions.HARDCORE_IRON_MAN)
						|| (permissionService.is(trader, PermissionService.PlayerPermissions.ULTIMATE_IRON_MAN)
								|| permissionService.is(partner,
										PermissionService.PlayerPermissions.ULTIMATE_IRON_MAN)))) {
			trader.sendMessage("Unable to trade due to the fact that either you or your partner is an ironman.");
			return;
		}

		partner.getActionQueue().clearAllActions();
		partner.getActionManager().stopAction();
		partner.getWalkingQueue().reset();

		Transaction transaction = new Transaction(tradeRequest.getPlayer(), tradeRequest.getPartner());
		openSharedScreen(transaction);
	}

	@Override
	@Subscribe
	public final void onWidgetClick(@Nonnull GameInterfaceButtonEvent offerEvent) {
		if (offerEvent.getInterfaceId() == CONFIRMATION_WIDGET
				&& offerEvent.getPlayer().getInterfaceState().isInterfaceOpen(CONFIRMATION_WIDGET)) {
			Player player = offerEvent.getPlayer();
			Transaction transaction = player.getTransaction();

			if (transaction == null)
				return;

			switch (offerEvent.getButton()) {
			case SECOND_ACCEPT_BUTTON:
				transaction.get(player).ifPresent(p -> p.setAccepted(true));
				openConfirmationWidget(transaction);
				transaction.get(player).get().getPlayer().getActionSender().sendString(CONFIRMATION_WIDGET,
						SECOND_WAITING_CHILD, "Waiting for other player...");
				transaction.getOther(player).get().getPlayer().getActionSender().sendString(CONFIRMATION_WIDGET,
						SECOND_WAITING_CHILD, "Other player has accepted");
				break;
			case SECOND_DECLINE_BUTTON:
				endTransaction(transaction, true);
				break;
			}
		} else if (offerEvent.getInterfaceId() == TRADE_INVENTORY_WIDGET
				&& offerEvent.getPlayer().getInterfaceState().isInterfaceOpen(TRADE_WIDGET)) {

			Player player = offerEvent.getPlayer();
			Transaction transaction = player.getTransaction();

			if (transaction == null)
				return;

			Optional<TradeContainer> tradeOption = transaction.get(player);
			Optional<TradeContainer> partnerOption = transaction.getOther(player);

			if (tradeOption.isPresent() && partnerOption.isPresent()) {
				Container inventory = player.getInventory();

				Item item = player.getInventory().get(offerEvent.getChildButton());

				if (item == null)
					return;

				if (!item.getDefinition().isTradable()) {
					if (permissionService.is(player, PermissionService.PlayerPermissions.DEV))
						player.sendMessage("<col=ff0000>[Warning]</col> The item [ " + item.getDefinition2().getName()
								+ " ] isn't tradable!");
					else
						player.getActionSender().sendMessage("You cannot trade this item.");
					return;
				}

				int menuIndex = offerEvent.getMenuIndex();
				int offerAmount = menuIndex == 3 ? inventory.getCount(item.getId()) : getAmountForMenuIndex(menuIndex);

				if (menuIndex == 10) {
					player.getInterfaceState().openEnterAmountInterface(TRADE_INVENTORY_WIDGET,
							offerEvent.getChildButton(), item.getId());
					return;
				}

				transaction.add(tradeOption.get(), new Item(item.getId(), offerAmount));
			}
		} else if (offerEvent.getInterfaceId() == TRADE_WIDGET
				&& offerEvent.getPlayer().getInterfaceState().isInterfaceOpen(TRADE_WIDGET)) {

			Player player = offerEvent.getPlayer();
			Transaction transaction = player.getTransaction();

			if (transaction == null) {
				return;
			}

			Optional<TradeContainer> tradeOption = transaction.get(player);
			if (tradeOption.isPresent()) {
				TradeContainer container = tradeOption.get();

				if (offerEvent.getButton() == ACCEPT_BUTTON) {
					container.setAccepted(true);
					openConfirmationWidget(transaction);
					container.getPlayer().getActionSender().sendString(TRADE_WIDGET, WAITING_CHILD,
							"Waiting for other player...");
					transaction.getOther(container.getPlayer()).get().getPlayer().getActionSender()
							.sendString(TRADE_WIDGET, WAITING_CHILD, "Other player has accepted");
					return;
				}

				if (offerEvent.getButton() == DECLINE_BUTTON) {
					endTransaction(transaction, true);
					return;
				}

				Item item = container.get(offerEvent.getChildButton());

				if (item == null) {
					return;
				}

				int menuIndex = offerEvent.getMenuIndex();
				int withdrawAmount = getAmountForMenuIndex(menuIndex);
				int available = container.getCount(item.getId());

				if (withdrawAmount > available) {
					withdrawAmount = available;
				}

				int takeAmount = menuIndex == 3 ? available : withdrawAmount;
				if (menuIndex == 10) {
					player.getInterfaceState().openEnterAmountInterface(TRADE_WIDGET, offerEvent.getChildButton(),
							item.getId());
					return;
				}
				transaction.remove(tradeOption.get(),
						new IndexedTradeItem(new Item(item.getId(), takeAmount), offerEvent.getChildButton()));
			}
		}
	}

	@Override
	public boolean openConfirmationWidget(@Nonnull Transaction transaction) {
		TradeContainer traderContainer = transaction.getTraderContainer();
		TradeContainer partnerContainer = transaction.getPartnerContainer();

		Player trader = traderContainer.getPlayer();
		Player partner = partnerContainer.getPlayer();
		if (trader.getInterfaceState().isInterfaceOpen(CONFIRMATION_WIDGET) && traderContainer.isAccepted()
				&& partnerContainer.isAccepted()) {
			endTransaction(transaction, false);
			return true;
		}

		if (trader.getInterfaceState().isInterfaceOpen(TRADE_WIDGET) && traderContainer.isAccepted()
				&& partnerContainer.isAccepted()) {

			if (partner.getInventory().freeSlots() < traderContainer.size()) {
				partner.getActionSender()
						.sendMessage("You don't have enough inventory space to complete this transaction.");
				trader.getActionSender()
						.sendMessage("Your partner have enough inventory space to complete this transaction.");
				endTransaction(transaction, true);
				return false;
			}

			if (trader.getInventory().freeSlots() < partnerContainer.size()) {
				trader.getActionSender()
						.sendMessage("You don't have enough inventory space to complete this transaction.");
				partner.getActionSender()
						.sendMessage("Your partner doesn't have enough inventory space to complete this transaction.");
				endTransaction(transaction, true);
				return false;
			}
			Optional<Integer> priceOption = transaction.getTraderContainer().stream()
					.filter(i -> i != null && i.getDefinition() != null).map(i -> i.getPrice())
					.reduce((a, b) -> a + b);

			Optional<Integer> partnerPriceOption = transaction.getPartnerContainer().stream()
					.filter(i -> i != null && i.getDefinition() != null).map(i -> i.getPrice())
					.reduce((a, b) -> a + b);

			int price = priceOption.isPresent() ? priceOption.get() : 0;
			int partnerPrice = partnerPriceOption.isPresent() ? partnerPriceOption.get() : 0;
			trader.getActionSender()
					.sendString(CONFIRMATION_WIDGET, SECOND_TRADING_WITH,
							"<col=00FFFF>Trading with:<br><col=00FFFF>" + partner.getName())
					.sendString(CONFIRMATION_WIDGET, 24, "In return you will receive:<br>(Value: <col=ffffff>"
							+ NumberFormat.getNumberInstance(Locale.ENGLISH).format(partnerPrice) + "</col> coins)")
					.sendString(CONFIRMATION_WIDGET, 23,
							"You are about to give:<br>(Value: <col=ffffff>"
									+ NumberFormat.getNumberInstance(Locale.ENGLISH).format(price) + "</col coins")
					.sendInterface(CONFIRMATION_WIDGET, false);
			partner.getActionSender()
					.sendString(CONFIRMATION_WIDGET, SECOND_TRADING_WITH,
							"<col=00FFFF>Trading with:<br><col=00FFFF>" + trader.getName())
					.sendString(CONFIRMATION_WIDGET, 24,
							"In return you will receive:<br>(Value: <col=ffffff>"
									+ NumberFormat.getNumberInstance(Locale.ENGLISH).format(price) + "</col> coins)")
					.sendString(CONFIRMATION_WIDGET, 23, "You are about to give:<br>(Value: <col=ffffff>"
							+ NumberFormat.getNumberInstance(Locale.ENGLISH).format(partnerPrice) + "</col coins")
					.sendInterface(CONFIRMATION_WIDGET, false);

			// Send String: Interface = 334, Child = 24, String = In return you will
			// receive:<br>(Value: <col=ffffff>2,283</col> coins)
			// Send String: Interface = 334, Child = 23, String = You are about to
			// give:<br>(Value: <col=ffffff>10</col> coins)

			traderContainer.setAccepted(false);
			partnerContainer.setAccepted(false);
			return true;
		}
		return false;
	}

	@Override
	public void setAvailableInventorySpace(@Nonnull Transaction transaction) {
		Player trader = transaction.getTraderContainer().getPlayer();
		Player partner = transaction.getPartnerContainer().getPlayer();
		trader.getActionSender().sendString(TRADE_WIDGET, FREE_INVENTORY_SLOTS_COMPONENT,
				partner.getName() + " has " + partner.getInventory().freeSlots() + "<br> free inventory slots.");
		partner.getActionSender().sendString(TRADE_WIDGET, FREE_INVENTORY_SLOTS_COMPONENT,
				trader.getName() + " has " + trader.getInventory().freeSlots() + "<br> free inventory slots.");
		Optional<Integer> priceOption = transaction.getTraderContainer().stream()
				.filter(i -> i != null && i.getDefinition() != null)
				.map(i -> i.getPrice() * i.getCount()).reduce((a, b) -> a + b);

		Optional<Integer> partnerPriceOption = transaction.getPartnerContainer().stream()
				.filter(i -> i != null && i.getDefinition() != null)
				.map(i -> i.getPrice() * i.getCount()).reduce((a, b) -> a + b);

		int price = priceOption.isPresent() ? priceOption.get() : 0;
		int partnerPrice = partnerPriceOption.isPresent() ? partnerPriceOption.get() : 0;
		String formattedPrice = price < 0 ? "A lot of" : NumberFormat.getNumberInstance(Locale.ENGLISH).format(price);
		String otherFormattedPrice = partnerPrice < 0 ? "A lot of"
				: NumberFormat.getNumberInstance(Locale.ENGLISH).format(partnerPrice);
		trader.getActionSender().sendString(TRADE_WIDGET, PLAYER_CONTAINER_PRICE,
				"You offer:<br>(Value: <col=ffffff>" + formattedPrice + "</col> coins)");
		trader.getActionSender().sendString(TRADE_WIDGET, PARTNER_CONTAINER_PRICE,
				partner.getName() + " offers:<br>(Value: <col=ffffff>" + otherFormattedPrice + "</col> coins)");

		partner.getActionSender().sendString(TRADE_WIDGET, PLAYER_CONTAINER_PRICE,
				"You offer:<br>(Value: <col=ffffff>" + otherFormattedPrice + "</col> coins)");
		partner.getActionSender().sendString(TRADE_WIDGET, PARTNER_CONTAINER_PRICE,
				trader.getName() + " offers:<br>(Value: <col=ffffff>" + formattedPrice + "</col> coins)");

	}

	private static int getAmountForMenuIndex(int index) {
		switch (index) {
		case 0:
			return 1;
		case 1:
			return 5;
		case 2:
			return 10;
		default:
			return 0;
		}
	}

	@Override
	public final void openSharedScreen(@Nonnull Transaction transaction) {
		Player trader = transaction.getTraderContainer().getPlayer();
		Player partner = transaction.getPartnerContainer().getPlayer();

		if (!trader.isEnteredPinOnce() && trader.getDatabaseEntity().getPlayerSettings().isBankSecured()) {
			trader.getActionSender().sendMessage("Please enter your Bank pin before trading at any nearest bank!");
			return;
		}
		if (!partner.isEnteredPinOnce() && partner.getDatabaseEntity().getPlayerSettings().isBankSecured()) {
			trader.getActionSender().sendMessage("Please enter your Bank pin before trading at any nearest bank!");
			return;
		}

		setDefaults(trader, partner);
		setDefaults(partner, trader);

		setAvailableInventorySpace(transaction);

		addWidgetListeners(trader);
		addWidgetListeners(partner);

		addTransactionListeners(trader);
		addTransactionListeners(partner);

		trader.getActionSender().sendInterfaceInventory(TRADE_INVENTORY_WIDGET);
		partner.getActionSender().sendInterfaceInventory(TRADE_INVENTORY_WIDGET);

		trader.getActionSender().sendInterfaceConfig(TRADE_WIDGET, 24, false).sendInterface(TRADE_WIDGET, false);
		partner.getActionSender().sendInterfaceConfig(TRADE_WIDGET, 24, false).sendInterface(TRADE_WIDGET, false);
	}

	@Override
	public void addTransactionListeners(@Nonnull Player player) {

		Transaction transaction = player.getTransaction();

		Optional<TradeContainer> containerOption = transaction.get(player);
		Optional<TradeContainer> partnerContainerOption = transaction.getOther(player);

		if (containerOption.isPresent() && partnerContainerOption.isPresent()) {
			TradeContainer container = containerOption.get();
			TradeContainer partnerContainer = partnerContainerOption.get();

			player.getInterfaceState().addListener(container, new InterfaceContainerListener(player, -1, 2, 90));
			player.getInterfaceState().addListener(partnerContainer,
					new InterfaceContainerListener(player, -2, 60981, 90));
			player.getInterfaceState().addListener(partnerContainer,
					new InterfaceContainerListener(player, -1, 50, 80));
			player.getInterfaceState().addListener(container, new InterfaceContainerListener(player, -1, 1, 81));
		}
	}

	@Override
	public final void setDefaults(@Nonnull Player player, @Nonnull Player partner) {

		player.sendAccess(TRADE_ACCESS_SELF);
		player.sendAccess(TRADE_ACCESS_OTHER);
		player.sendAccess(TRADE_INVENTORY_ACCESS);

		player.getActionSender().sendCS2Script(150, TRADE_PARAMETERS_2, Constants.TRADE_TYPE_STRING)
				.sendCS2Script(150, Constants.OFFER_PARAMETERS, Constants.TRADE_TYPE_STRING)
				.sendCS2Script(150, TRADE_PARAMETERS_1, "iiiiii").sendCS2Script(147, new Object[] { 1 }, "i");

		player.getActionSender().sendString(TRADE_WIDGET, TRADING_WITH_COMPONENT, "Trading with: " + partner.getName());
		player.getActionSender().sendString(TRADE_WIDGET, WAITING_CHILD, "");
		player.getActionSender().sendString(CONFIRMATION_WIDGET, SECOND_WAITING_CHILD, "");
	}

	@Override
	public final void resetStrings(@Nonnull Player player) {
		player.getActionSender().sendString(TRADE_WIDGET, WAITING_CHILD, "")
				.sendString(CONFIRMATION_WIDGET, SECOND_WAITING_CHILD, "")
				.sendString(TRADE_WIDGET, PARTNER_CONTAINER_PRICE, "")
				.sendString(TRADE_WIDGET, PLAYER_CONTAINER_PRICE, "");
	}

	@Override
	public final void addWidgetListeners(@Nonnull Player player) {
		player.getInterfaceState().addListener(player.getInventory(),
				new InterfaceContainerListener(player, 149, 0, 93));
		player.getInterfaceState().addListener(player.getInventory(),
				new InterfaceContainerListener(player, -1, 1, 82));
	}

	@Override
	public void endTransaction(@Nonnull Transaction transaction, boolean cancel) {

		Player trader = transaction.getTraderContainer().getPlayer();
		Player partner = transaction.getPartnerContainer().getPlayer();
		trader.getRequestManager().finishRequest();
		trader.getActionSender().removeInventoryInterface();
		trader.resetInteractingEntity();
		trader.getActionSender().removeAllInterfaces().removeInterface();
		resetStrings(trader);

		partner.getActionSender().removeInventoryInterface();
		partner.resetInteractingEntity();
		partner.getActionSender().removeAllInterfaces().removeInterface();
		resetStrings(partner);

		if (cancel) {
			transaction.cancel();
		} else {
			transaction.complete();
		}

		playerVariableService.set(trader, TradeVariables.TRADE_MODIFIED, 0);
		playerVariableService.set(partner, TradeVariables.TRADE_MODIFIED, 0);

		trader.setTransaction(null);
		partner.setTransaction(null);
		trader.getRequestManager().setRequestType(null);
		trader.getRequestManager().setAcquaintance(null);
		partner.getRequestManager().setRequestType(null);
		partner.getRequestManager().setAcquaintance(null);
	}
}
