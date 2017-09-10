package org.rs2server.rs2.domain.service.api;

import org.rs2server.rs2.domain.service.impl.BankPinServiceImpl;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;

/**
 * A representation of a bank pin service.
 * @author twelve
 */
public interface BankPinService {

    /**
     * Opens the bank pin interface for a player.
     * @param player The player to open the interface for.
     */
    void openPinInterface(@Nonnull Player player, BankPinServiceImpl.PinType type);

    /**
     * Opens the bank pin settings interface for a player
     * @param player The player to open the interface for.
     */
    void openPinSettingsInterface(@Nonnull Player player, BankPinServiceImpl.SettingScreenType type);

    /**
     * Shows a failed message when a pin is incorrect for a player.
     * @param player The player to show the failed message for.
     */
    void pinFailed(@Nonnull Player player);

    void openConfirmationInterface(@Nonnull Player player, BankPinServiceImpl.ConfirmationType type);

    void onClose(@Nonnull Player player);

}
