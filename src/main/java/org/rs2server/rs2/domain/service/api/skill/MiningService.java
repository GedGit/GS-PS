package org.rs2server.rs2.domain.service.api.skill;

import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.skills.Mining;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author tommo
 */
public interface MiningService {

	boolean hasPickaxe(@Nonnull Player player);

	@Nullable
	Mining.PickAxe getPickaxe(@Nonnull Player player);

	/**
	 * Calculates the mining experience modifier based on which pieces of
	 * the prospector kit the player is wearing.
	 * @param player The player who may or may not be wearing any prospector's kit items.
	 * @return The experience modified. If the player no has no prospector's items, returns 1f.
	 */
	float getProspectorKitExperienceModifier(@Nonnull Player player);

	/**
	 * Usually returns null or a gem item, occasionally.
	 * @return Null, or a gem.
	 */
	Item getRandomChanceGem();

}
