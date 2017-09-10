package org.rs2server.rs2.domain.service.api;

import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Player service.
 *
 * @author tommo
 */
public interface PlayerService {

	/**
	 * Checks if the player has a given item in their inventory or bank.
	 * @param player The player.
	 * @param item The item to check for.
	 * @return true if so, false if not.
	 */
	boolean hasItemInInventoryOrBank(@Nonnull Player player, @Nonnull Item item);

	/**
	 * Gives a player item if the player has space in their inventory, otherwise drops
	 * a ground item at the player's location if specified.
	 * @param player The player
	 * @param item The item to give
	 * @param fallbackToGround true if the item should be dropped as a ground item if the player has no space in their inventory
	 */
	void giveItem(@Nonnull final Player player, @Nonnull final Item item, boolean fallbackToGround);

	String getIpAddress(@Nonnull final Player player);

	/**
	 * Attempts to retrieve a player his their name.
	 * @param name The player name.
	 * @return The found player, or null if not found.
	 */
	@Nullable
	Player getPlayer(@Nonnull final String name);

}
