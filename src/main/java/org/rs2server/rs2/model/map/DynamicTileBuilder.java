package org.rs2server.rs2.model.map;

import org.rs2server.util.MapXTEA;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author twelve
 */
public final class DynamicTileBuilder {

    public static final int HEIGHT_MAP_SIZE = 4;
    public static final int SECTOR_SIZE = 8;
    public static final int PALETTE_SIZE = 13;

    @Nonnull private final DynamicTile[][][] tiles;
    @Nonnull private final int[] xtea;

    private static final RectanglePacker<Integer> PACKER = new RectanglePacker<>(2048, 2048, 10);
    private final int region;

    public DynamicTileBuilder(@Nonnull int[] xtea, int region) {
        this.tiles = new DynamicTile[HEIGHT_MAP_SIZE][PALETTE_SIZE][PALETTE_SIZE];
        this.xtea = xtea;
        this.region = region;
    }

    @Nonnull
    public static DynamicTileBuilder of(int region) {
        return new DynamicTileBuilder(MapXTEA.getKey(region), region);
    }

    @Nonnull
    public static DynamicTileBuilder of(int rX, int rY) {
        return of((rX >> 6) << 8 | (rY >> 6));
    }

    @Nonnull
    public static DynamicTileBuilder copyOf(int region) {
        return of(region).copy(0, 0, 0, SECTOR_SIZE, SECTOR_SIZE, HEIGHT_MAP_SIZE);
    }

    @Nonnull
    public static DynamicTileBuilder copyOf(int rX, int rY) {
        return of(rX, rY).copy(0, 0, 0, SECTOR_SIZE, SECTOR_SIZE, HEIGHT_MAP_SIZE);
    }

    public DynamicTile get(int x, int y) {
        return get(x, y, 0);
    }

    public DynamicTile get(int x, int y, int z) {
        return tiles[z][x][y];
    }

    @Nonnull
    public DynamicTileBuilder copy(int srcX, int srcY, int srcZ, int dstX, int dstY, int dstZ) {
        final int rX = (region >> 8) << 6;
        final int rY = (region & 0xFF) << 6;

        DynamicTileBuilder builder = of(rX, rY);

        for (int z = srcZ; z < dstZ; z++) {
            for (int x = srcX; x < dstX; x++) {
                for (int y = srcY; y < dstY; y++) {
                    builder.set(x, y, z, new DynamicTile(rX | x << 3, rY | y << 3, z, x, y, z, builder.xtea));
                }
            }
        }
        return builder;
    }


    @Nonnull
    public final DynamicTileBuilder merge(DynamicTileBuilder other) {
        DynamicTile[][][] otherTiles = other.getTiles();
        for (int z = 0; z < otherTiles.length; z++) {
            for (int x = 0; x < otherTiles[z].length / 2; x++) {
                for (int y = 0; y < otherTiles[z][x].length / 2; y++) {
                    DynamicTile otherTile = otherTiles[z][x][y];
                    if (otherTile != null) {
                        set(x, y, z, otherTile);
                    }
                }
            }
        }
        return this;
    }

    @Nonnull
    public final DynamicTileBuilder reverseMerge(DynamicTileBuilder other) {
        DynamicTile[][][] otherTiles = other.getTiles();
        for (int z = 0; z < otherTiles.length; z++) {
            for (int x = otherTiles[z].length - 1; x >= otherTiles[z].length / 2; x--) {
                for (int y = otherTiles[z][x].length - 1; y >= otherTiles[z][x].length / 2; y--) {
                    DynamicTile otherTile = otherTiles[z][x][y];
                    if (otherTile != null) {
                        set(x, y, z, otherTile);
                    }
                }
            }
        }
        return this;
    }

    @Nonnull
    public DynamicTileBuilder insert() {
        PACKER.insert(64, 64, region);
        RectanglePacker.Rectangle rect = PACKER.findRectangle(25700);
//        IMAGE.getGraphics().fillRect(rect.x, rect.y, rect.width, rect.height);
        System.out.println("REGION INSERTED: " + region + ", " + rect);
//        Errors.logging().run(() -> ImageIO.write(IMAGE, "png", new File("./testimg.png")));
        return this;
    }

    @Nonnull
    public final DynamicTileBuilder attach(DynamicTile tile, NeighborDirection direction) {
        return set(tile.getSectorX() + direction.getDeltaX(), tile.getSectorY() + direction.getDeltaY(), tile.getZ(), tile);
    }

    @Nonnull
    public final DynamicTileBuilder attachAll(DynamicTile[] tiles, NeighborDirection direction) {
        Arrays.stream(tiles).filter(Objects::nonNull).forEach(t -> attach(t, direction));
        return this;
    }

    @Nonnull
    public final DynamicTileBuilder attachAll(DynamicTile[][] tiles, NeighborDirection direction) {
        Arrays.stream(tiles).forEach(t -> attachAll(t, direction));
        return this;
    }

    @Nonnull
    public final DynamicTileBuilder attachAll(DynamicTile[][][] tiles, NeighborDirection direction) {
        Arrays.stream(tiles).forEach(t -> attachAll(t, direction));
        return this;
    }
    @Nonnull
    public final DynamicTile[][][] getTiles() {
        return tiles;
    }

    @Nonnull
    public final DynamicTileBuilder remove(int x, int y) {
        return remove(x, y, 0);
    }

    @Nonnull
    public final DynamicTileBuilder remove(int x, int y, int z) {
        return set(x, y, z, null);
    }

    @Nonnull
    public final DynamicTileBuilder set(int x, int y, DynamicTile tile) {
        return set(x, y, 0, tile);
    }

    @Nonnull
    public final DynamicTileBuilder set(int x, int y, int z, DynamicTile tile) {
        if (x < 0 || y < 0 || z < 0) {
            return this;
        }
        this.tiles[z][x][y] = tile;
        return this;
    }

    public enum NeighborDirection {
        CENTER(0, 0),
        NORTH(0, 1),
        SOUTH(0, -1),
        EAST(1, 0),
        WEST(-1, 0),
        SOUTH_WEST(-1, -1),
        SOUTH_EAST(-1, 1),
        NORTH_EAST(1, 1),
        NORTH_WEST(-1, 1);

        private final int dx;
        private final int dy;

        NeighborDirection(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }

        public final int getDeltaX() {
            return dx;
        }

        public final int getDeltaY() {
            return dy;
        }
    }
}
