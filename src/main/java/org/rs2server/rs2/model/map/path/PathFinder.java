package org.rs2server.rs2.model.map.path;

import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;

import java.util.List;

/**
 * @author Graham
 */
public interface PathFinder {
    public static final int SOUTH_FLAG = 0x20, WEST_FLAG = 0x80, NORTH_FLAG = 0x2, EAST_FLAG = 0x8;

    public static final int SOUTH_WEST_FLAG = SOUTH_FLAG | WEST_FLAG;
    public static final int NORTH_WEST_FLAG = NORTH_FLAG | WEST_FLAG;
    public static final int SOUTH_EAST_FLAG = SOUTH_FLAG | EAST_FLAG;
    public static final int NORTH_EAST_FLAG = NORTH_FLAG | EAST_FLAG;

    public static final int SOLID_FLAG = 0x20000;
    public static final int UNKNOWN_FLAG = 0x40000000;

    public static final int DIRECTION_NORTHWEST = 0x1;
    public static final int DIRECTION_NORTH = 0x2;
    public static final int DIRECTION_NORTHEAST = 0x4;
    public static final int DIRECTION_EAST = 0x8;
    public static final int DIRECTION_SOUTHEAST = 0x10;
    public static final int DIRECTION_SOUTH = 0x20;
    public static final int DIRECTION_SOUTHWEST = 0x40;
    public static final int DIRECTION_WEST = 0x80;
    public static final int BLOCKED = 0x100;
    public static final int INVALID = 0x200000 | 0x40000;

    /**
     * Attempts to find a path from the source to the destination.
     * @param moveNear
     * @param trimLastNode
     * @param radius
     * @param source
     * @param destination
     * @return A list of locations in the path, or null if no path could be found.
     */
    List<Location> findPath(Location source, Location destination);

    TilePath findPath(Mob mob, Location base, int srcX, int srcY, int dstX, int dstY, int z, int radius, boolean running, boolean ignoreLastStep, boolean moveNear);


}
