package org.rs2server.rs2.domain.service.api;

import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.region.Region;

import javax.annotation.Nonnull;

/**
 * Service providing interaction with game Regions.
 *
 * @author tommo
 */
public interface RegionService {

	/**
	 * Replaces an existing game object with a given replacement.
	 * @param original The original game object to replace.
	 * @param replacement The new, replacing game object.
	 */
	void replaceObject(@Nonnull Region region, @Nonnull GameObject original, @Nonnull GameObject replacement);

	/**
	 * Temporarily replaces an existing game object with a given replacement.
	 * @param original The original game object to replace.
	 * @param replacement The new, replacing game object.
	 * @param cycles The number of cycles after which to remove the replacement and add the original object back to the region.
	 */
	void replaceObjectTemporary(@Nonnull Region region, @Nonnull GameObject original, @Nonnull GameObject replacement, int cycles);

	/**
	 * Adds a game object to a region.
	 * @param region The region to which to add the game object.
	 * @param object The game object to add.
	 */
	void addGameObject(@Nonnull Region region, @Nonnull GameObject object);

	/**
	 * Removes a game object from a region.
	 * @param region The region from which to remove the game object.
	 * @param object The game object to remove.
	 */
	void removeGameObject(@Nonnull Region region, @Nonnull GameObject object);

}
