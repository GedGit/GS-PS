package org.rs2server.rs2.domain.service.api.content.gamble;

import org.rs2server.rs2.domain.service.impl.content.gamble.DiceGameTransaction;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;

/**
 * @author Clank1337
 */
public interface DiceGameService {

	void openGameInterface(@Nonnull DiceGameTransaction transaction);

	void rollDice(@Nonnull DiceGameTransaction transaction);

	void resetStrings(@Nonnull Player player);

	void setAvailableInventorySpace(@Nonnull DiceGameTransaction transaction);

	void setDefaults(@Nonnull Player player, @Nonnull Player partner);

	void addWidgetListeners(@Nonnull Player player);

	void addTransactionListeners(@Nonnull Player player);

	void endTransaction(DiceGameTransaction transaction, boolean cancel);

	boolean openConfirmationWidget(@Nonnull DiceGameTransaction transaction);

	void openDiceGame(@Nonnull DiceGameTransaction transaction);

}
