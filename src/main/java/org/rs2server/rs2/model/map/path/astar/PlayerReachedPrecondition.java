package org.rs2server.rs2.model.map.path.astar;

import org.rs2server.rs2.model.map.RegionClipping;
import org.rs2server.rs2.model.map.path.ClippingFlag;
import org.rs2server.rs2.model.map.path.PathPrecondition;
import org.rs2server.rs2.model.player.Player;

/**
 * A set of preconditions for when trying to interact with a player. Currently only used for trade option
 * in both chat and on the right click menu.
 * @author Twelve
 */
public class PlayerReachedPrecondition implements PathPrecondition {

    private final Player player;

    public PlayerReachedPrecondition(Player player) {
        this.player = player;
    }

    @Override
    public boolean targetReached(int currentX, int currentY, int destinationX, int destinationY) {
        int flags = RegionClipping.getClippingMask(currentX, currentY, player.getPlane());
        if (currentX == destinationX && currentY == destinationY + 1 && ClippingFlag.BLOCK_SOUTH.and(flags) != 0) {
            return false;
        }
        if (currentX == destinationX && currentY == destinationY - 1 && ClippingFlag.BLOCK_NORTH.and(flags) != 0) {
            return false;
        }
        if (currentX == destinationX + 1 && currentY == destinationY && ClippingFlag.BLOCK_WEST.and(flags) != 0) {
            return false;
        }
        if (currentX == destinationX - 1 && currentY == destinationY && ClippingFlag.BLOCK_EAST.and(flags) != 0) {
            return false;
        }
        if (currentX == destinationX - 1 && currentY == destinationY + 1 && ClippingFlag.BLOCK_SOUTH_EAST.and(flags) != 0) {
            return false;
        }
        if (currentX == destinationX + 1 && currentY == destinationY + 1 && ClippingFlag.BLOCK_SOUTH_WEST.and(flags) != 0) {
            return false;
        }
        if (currentX == destinationX + 1 && currentY == destinationY - 1 && ClippingFlag.BLOCK_NORTH_WEST.and(flags) != 0) {
            return false;
        }
        if (currentX == destinationX - 1 && currentY == destinationY - 1 && ClippingFlag.BLOCK_NORTH_EAST.and(flags) != 0) {
            return false;
        }
        return distance(currentX, currentY, destinationX, destinationY) == 1;
    }

    private static int distance(int sx, int sy, int dx, int dy) {
        int deltaX = sx - dx;
        int deltaY = sy - dy;
        return Math.abs(deltaX) + Math.abs(deltaY);
    }
}
