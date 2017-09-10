package org.rs2server.rs2.content.api.bank;

import org.rs2server.rs2.model.player.Player;

import javax.annotation.concurrent.Immutable;

/**
 * Represents a click on the bank settings widget.
 * @author twelve
 */
@Immutable
public final class BankSettingsClickEvent {

    /**
     * The player who initiated the click event.
     */
    private final Player player;
    /**
     * The child id of the button clicked.
     */
    private final int child;

    public BankSettingsClickEvent(Player player, int child) {
        this.player = player;
        this.child = child;
    }

    public final Player getPlayer() {
        return player;
    }

    public final int getChild() {
        return child;
    }
}
