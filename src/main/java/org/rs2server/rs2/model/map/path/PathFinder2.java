package org.rs2server.rs2.model.map.path;

import org.rs2server.rs2.model.Location;

import java.util.List;

public interface PathFinder2 {
    public List<Location> findPath(Location base, int srcX, int srcY, int dstX, int dstY, int z, int radius, boolean running, boolean ignoreLastStep, boolean moveNear);
}
