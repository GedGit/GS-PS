package org.rs2server.rs2.model.map;

import javax.annotation.concurrent.Immutable;

/**
 * @author twelve
 */
@Immutable
public final class DynamicTile {

    /**
     * Rotation direction clockwise by 0 degrees.
     */
    public static final int DIRECTION_CW_0 = 0;

    /**
     * Rotation direction clockwise by 90 degrees.
     */
    public static final int DIRECTION_CW_90 = 1;

    /**
     * Rotation direction clockwise by 180 degrees.
     */
    public static final int DIRECTION_CW_180 = 2;

    /**
     * Rotation direction clockwise by 270 degrees.
     */
    public static final int DIRECTION_CW_270 = 3;

    private final int x;
    private final int y;
    private final int z;
    private final int rotation;

    private final int[] xtea;
    private final int sectorX;
    private final int sectorY;
    private final int sectorZ;

    public DynamicTile(int x, int y, int z, int sectorX, int sectorY, int sectorZ, int rotation, int[] xtea) {
        this.x = x >> 3;
        this.y = y >> 3;
        this.z = z;
        this.rotation = rotation % 4;
        this.xtea = xtea;
        this.sectorX = sectorX;
        this.sectorY = sectorY;
        this.sectorZ = sectorZ;
    }

    public DynamicTile(int x, int y, int z, int sectorX, int sectorY, int sectorZ, int[] xtea) {
        this(x, y, z, sectorX, sectorY, sectorZ, DIRECTION_CW_0, xtea);
    }

    @Override
    public final int hashCode() {
        return rotation << 1 | z << 24 | x << 14 | y << 3;
    }

    public final int[] getXTEA() {
        return xtea;
    }


    public final int getZ() {
        return z;
    }

    public final int getY() {
        return y;
    }

    public final int getX() {
        return x;
    }

    public final int getSectorX() {
        return sectorX;
    }

    public final int getSectorY() {
        return sectorY;
    }

    public final int getSectorZ() {
        return sectorZ;
    }
}
