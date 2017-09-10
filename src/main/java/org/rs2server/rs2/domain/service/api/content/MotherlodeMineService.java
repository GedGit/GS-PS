package org.rs2server.rs2.domain.service.api.content;

import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;

/**
 * @author tommo
 */
public interface MotherlodeMineService {

	/**
	 * Attempts to claim any available ore in the sack for the player.
	 * @param player The player.
	 */
	void claimOreInSack(@Nonnull Player player);

	/**
	 * Adds the given amount of pay dirt into the sack for the player.
	 * @param player The player.
	 * @param amount The amount of pay dirt to add.
	 */
	void addPayDirtToSack(@Nonnull Player player, final int amount);

	/**
	 * Gets the amount of pay dirt the player has deposited into the hopper.
	 * @param player The player.
	 * @return The amount of pay dirt the player has deposited.
	 */
	int getPayDirtInSack(@Nonnull Player player);

	/**
	 * Attempts to deposit all pay-dirt in the player's inventory into the hopper.
	 * @param player The player.
	 */
	void depositPayDirt(@Nonnull Player player);

}
