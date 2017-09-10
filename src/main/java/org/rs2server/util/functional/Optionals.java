package org.rs2server.util.functional;

import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.map.RegionClipping;

import java.util.Optional;

/**
 * Some utilities which utilize Optional.
 * @author twelve
 */
public final class Optionals {

    public static Optional<Location> nearbyFreeLocation(Location start) {
        Optional<Location> cardinal = nearbyFreeCardinal(start);

        if (cardinal.isPresent()) {
            return cardinal;
        }

        return nearbyFreeInterCardinal(start);
    }

    public static Optional<Location> nearbyFreeCardinal(Location start) {

        if (RegionClipping.isPassable(start.getX() - 1, start.getY(), start.getPlane())) {
            return Optional.of(Location.create(start.getX() - 1, start.getY(), start.getPlane()));
        }

        if (RegionClipping.isPassable(start.getX() - 1, start.getY(), start.getPlane())) {
            return Optional.of(Location.create(start.getX() - 1, start.getY(), start.getPlane()));
        }

        if (RegionClipping.isPassable(start.getX(), start.getY() + 1, start.getPlane())) {
            return Optional.of(Location.create(start.getX(), start.getY() + 1, start.getPlane()));
        }

        if (RegionClipping.isPassable(start.getX(), start.getY() - 1, start.getPlane())) {
            return Optional.of(Location.create(start.getX(), start.getY() - 1, start.getPlane()));
        }

        return Optional.empty();
    }

    public static Optional<Location> nearbyFreeInterCardinal(Location start) {
        if (RegionClipping.isPassable(start.getX() - 1, start.getY() - 1, start.getPlane())) {
            return Optional.of(Location.create(start.getX() - 1, start.getY() - 1, start.getPlane()));
        }

        if (RegionClipping.isPassable(start.getX() - 1, start.getY() + 1, start.getPlane())) {
            return Optional.of(Location.create(start.getX() - 1, start.getY() + 1, start.getPlane()));
        }

        if (RegionClipping.isPassable(start.getX() + 1, start.getY() + 1, start.getPlane())) {
            return Optional.of(Location.create(start.getX() + 1, start.getY() + 1, start.getPlane()));
        }

        if (RegionClipping.isPassable(start.getX() + 1, start.getY() - 1, start.getPlane())) {
            return Optional.of(Location.create(start.getX() + 1, start.getY() - 1, start.getPlane()));
        }
        return Optional.empty();
    }
}
