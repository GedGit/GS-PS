package org.rs2server.rs2.domain.service.api;

import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;

/**
 * Service for deadman mode.
 *
 * @author tommo
 */
public interface DeadmanService {

	boolean canAttack(@Nonnull Player attacker, @Nonnull Player victim);

	boolean inSafeZone(@Nonnull Player player);

	/**
	 * Reduces a player's skills according to deadman mode rules.
	 * Called when a player has died.
	 * @param player The player who's stats to lower.
	 */
	void reducePlayerSkills(@Nonnull Player player);

	void onPlayerKill(@Nonnull Player killer, @Nonnull Player victim);

}
