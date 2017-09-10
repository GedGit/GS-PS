package org.rs2server.rs2.domain.service.api.content;

import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;

/**
 * Created by Tim on 12/21/2015.
 */
public interface GrandExchangeService {

    public void openGrandExchange(@Nonnull Player player);
    
    public void sendBuyScreen(@Nonnull Player player, Item item);

    public void sendSellScreen(@Nonnull Player player, int slot);

    public void refresh(@Nonnull Player player);

    public void sendSellScreenItem(@Nonnull Player player, Item item);
}
