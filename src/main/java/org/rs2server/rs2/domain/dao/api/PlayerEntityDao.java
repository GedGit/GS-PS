package org.rs2server.rs2.domain.dao.api;

import org.rs2server.rs2.domain.dao.MongoDao;
import org.rs2server.rs2.domain.model.player.PlayerEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author tommo
 */
public interface PlayerEntityDao extends MongoDao<PlayerEntity> {

	/**
	 * Attempts to retrieve a persisted player by their unique account name.
	 * @param accountName The account name.
	 * @return The found player, or null if no player exists with the given account name.
	 */
	@Nullable
	PlayerEntity findByAccountName(@Nonnull final String accountName);

	/**
	 * Attempts to retrieve a persisted player by their unique display name.
	 * @param displayName The display name.
	 * @return The found player, or null if no player exists with the given display name.
	 */
	@Nullable
	PlayerEntity findByDisplayName(@Nonnull final String displayName);

}
