package org.rs2server.rs2.domain.service.api.content.trade;

import org.rs2server.rs2.content.api.GameInterfaceButtonEvent;
import org.rs2server.rs2.content.api.GamePlayerLogoutEvent;
import org.rs2server.rs2.content.api.GameTradeRequestEvent;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;

/**
 * @author twelve
 */
public interface TradeService {

	void onLogout(@Nonnull GamePlayerLogoutEvent event);

	void onTradeRequest(@Nonnull GameTradeRequestEvent tradeRequest);

	void onWidgetClick(@Nonnull GameInterfaceButtonEvent offerEvent);

	boolean openConfirmationWidget(@Nonnull Transaction transaction);

	void setAvailableInventorySpace(@Nonnull Transaction transaction);

	void openSharedScreen(@Nonnull Transaction transaction);

	void setDefaults(@Nonnull Player player, @Nonnull Player partner);

	void addTransactionListeners(@Nonnull Player player);

	void addWidgetListeners(@Nonnull Player player);

	void endTransaction(@Nonnull Transaction transaction, boolean cancel);

	void resetStrings(@Nonnull Player player);

}
