package org.rs2server.rs2.domain.service.api.content;

import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;

/**
 * @author Clank1337
 */
public interface MaxCapeService {

	public boolean addToMaxCape(@Nonnull Player player, Item used, Item with);

	public boolean destroyMaxCape(@Nonnull Player player, Item item);

}
