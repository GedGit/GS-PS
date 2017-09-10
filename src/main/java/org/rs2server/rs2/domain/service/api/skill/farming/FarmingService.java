package org.rs2server.rs2.domain.service.api.skill.farming;

import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;

/**
 * @author tommo
 */
public interface FarmingService {

	/**
	 * Convenience method to call {@link #updatePatch(FarmingPatchState)} and {@link #sendPatches(Player)}.
	 * @param player The player.
	 * @param patch The farming patch.
	 */
	void updateAndSendPatches(@Nonnull Player player, @Nonnull FarmingPatchState patch);

	/**
	 * Updates and sends the state of a given farming patch to the client.
	 * @param patch The farming patch.
	 */
	void updatePatch(@Nonnull FarmingPatchState patch);

	/**
	 * Constructs and sends all of the bit configs for farming patches to the client.
	 * @param player The player for whom to send the farming patch configs.
	 */
	void sendPatches(@Nonnull Player player);

	/**
	 * Opens the tool store interface, usually from the tool leprechaun.
	 * @param player The player to open it for.
	 */
	void openToolInterface(@Nonnull Player player);

	/**
	 * Clears a patch.
	 * @param player The player.
	 * @param patch The farming patch.
	 */
	void clearPatch(@Nonnull Player player, @Nonnull FarmingPatchState patch);

}
