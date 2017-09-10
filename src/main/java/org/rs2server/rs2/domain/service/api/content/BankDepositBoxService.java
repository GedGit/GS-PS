package org.rs2server.rs2.domain.service.api.content;

import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;

/**
 * Created by Tim on 11/18/2015.
 */
public interface BankDepositBoxService {

    void openDepositBox(@Nonnull Player player);


    void handleInterfaceActions(@Nonnull Player player, int button, int childButton, int childButton2, int menuIndex);
}
