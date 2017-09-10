package org.rs2server.rs2.domain.service.api;

import org.rs2server.rs2.domain.model.player.PlayerEntity;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Provides persistence capabilities.
 *
 * @author tommo
 */
public interface PersistenceService {

	/**
	 * Retrieves or creates a database entity for the player by the account name.
	 * Also sets the <code>databaseEntity</code> field on the player to the retrieved/created entity.
	 * @param player The player.
	 * @return The persisted player entity.
	 */
	@Nonnull
	PlayerEntity getOrCreatePlayer(@Nonnull final Player player);

	/**
	 * Creates and persists a new player.
	 * @param player The player to persist.
	 * @return The persisted player.
	 */
	@Nonnull
	PlayerEntity createPlayer(@Nonnull final Player player);

	/**
	 * Retrieves a persisted player.
	 * @param accountName The account name.
	 * @return The persisted player, or null if no database entity exists for the given player.
	 */
	@Nullable
	PlayerEntity getPlayerByAccountName(@Nonnull final String accountName);

	/**
	 * Retrieves a persisted player.
	 * @param displayName The account name.
	 * @return The persisted player, or null if no database entity exists for the given player.
	 */
	@Nullable
	PlayerEntity getPlayerByDisplayName(@Nonnull final String displayName);

	/**
	 * Retrieves a persisted player.
	 * @param id The player database id.
	 * @return The persisted player, or null if no database entity exists for the given player.
	 */
	PlayerEntity getPlayerById(@Nonnull final String id);

	/**
	 * Updates a player.
	 * @param player The player.
	 * @return The updated player.
	 */
	PlayerEntity savePlayer(@Nonnull final Player player);

	/**
	 * Initialises a player.
	 * Expects the player's <code>databaseEntity</code> field to be set, and will fail if not.
	 * @param player The player to initialise.
	 */
	void initialisePlayer(@Nonnull final Player player);

}
