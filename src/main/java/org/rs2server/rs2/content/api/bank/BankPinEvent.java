package org.rs2server.rs2.content.api.bank;

import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Represents a bank pin submission event.
 * @author twelve
 */
@Immutable
public final class BankPinEvent {

    /**
     * The player that is attached to this event.
     */
    @Nonnull private final Player player;
    /**
     * The pin that the player entered on the bank pin interface.
     */
    private final int pin;

    public BankPinEvent(@Nonnull Player player, int pin) {
        this.player = player;
        this.pin = pin;
    }

    /**
     * Gets the attached player.
     * @return the player.
     */
    @Nonnull
    public final Player getPlayer() {
        return player;
    }

    /**
     * Gets the attached player input.
     * @return the input pin.
     */
    public final int getPin() {
        return pin;
    }
}
