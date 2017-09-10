package org.rs2server.rs2.domain.service.impl;

import org.rs2server.rs2.domain.service.api.RegionService;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.map.RegionClipping;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.region.Region;
import org.rs2server.rs2.tickable.Tickable;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * @author tommo
 */
public class RegionServiceImpl implements RegionService {


	@Override
	public void replaceObject(@Nonnull Region region, @Nonnull GameObject original, @Nonnull GameObject replacement) {
		Objects.requireNonNull(original, "original");
		Objects.requireNonNull(replacement, "replacement");

		removeGameObject(region, original);
		addGameObject(region, replacement);
	}

	@Override
	public void replaceObjectTemporary(@Nonnull Region region, @Nonnull GameObject original, @Nonnull GameObject replacement, int cycles) {
		replaceObject(region, original, replacement);

		World.getWorld().submit(new Tickable(cycles) {
			@Override
			public void execute() {
				replaceObject(region, replacement, original);
				stop();
			}
		});
	}

	@Override
	public void addGameObject(@Nonnull Region region, @Nonnull GameObject object) {
		object.setLocation(object.getSpawnLocation() != null ? object.getSpawnLocation() : object.getLocation());
		if (!object.isLoadedInLandscape()) {
			for (Region r : region.getSurroundingRegions()) {
				if (r == null) continue;

				for (Player p : r.getPlayers()) {
					p.getActionSender().sendObject(object);
				}
			}
		}
		region.addObject(object);
		RegionClipping.addClipping(object);
	}

	@Override
	public void removeGameObject(@Nonnull Region region, @Nonnull GameObject object) {
		final Region[] regions = region.getSurroundingRegions();
		for (Region r : regions) {
			if (r == null) continue;

			for (Player p : r.getPlayers()) {
				p.getActionSender().removeObject(object);
			}
		}

		object.setRegion(null);
		region.removeObject(object);
		RegionClipping.removeClipping(object);
	}
}
