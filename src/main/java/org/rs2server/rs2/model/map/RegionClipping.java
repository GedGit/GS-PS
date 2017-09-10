package org.rs2server.rs2.model.map;

import org.rs2server.cache.format.CacheObjectDefinition;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.ObjectManager;
import org.rs2server.rs2.model.gameobject.GameObjectType;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Lazaro
 * @author Palidino76
 * @author 'Mystic Flow
 */
@SuppressWarnings({"unchecked"})
public class RegionClipping {

    public static final int PLAYERS = 0, NPCS = 1;

    public static final int REGION_SIZE = 128;

    public static final int MAX_MAP_X = 16383, MAX_MAP_Y = 16383;

    private static RegionClipping[][] regions = new RegionClipping[(MAX_MAP_X + 1) / REGION_SIZE][(MAX_MAP_Y + 1) / REGION_SIZE];
    private static List<RegionClipping> loadedRegions = new ArrayList<RegionClipping>();

    public static int hash(int x, int y) {
        return x >> 7 << 8 | y >> 7;
    }

    public static void addClipping(int x, int y, int z, int shift) {
        if (x == 2543 && y == 10143 && z == 0) {
            return;
        }
        RegionClipping region = forCoords(x, y);
        int localX = x - ((x >> 7) << 7);
        int localY = y - ((y >> 7) << 7);
        if (region.clippingMasks[z] == null) {
            region.clippingMasks[z] = new int[region.size][region.size];
        }
        region.clipped = true;
        region.clippingMasks[z][localX][localY] |= shift;
    }

    public static void setClippingMask(int x, int y, int z, int shift) {
        RegionClipping region = forCoords(x, y);
        int localX = x - ((x >> 7) << 7);
        int localY = y - ((y >> 7) << 7);
        if (region.clippingMasks[z] == null) {
            region.clippingMasks[z] = new int[region.size][region.size];
        }
        region.clipped = true;
        region.clippingMasks[z][localX][localY] = shift;
    }

    public static void removeClipping(int x, int y, int z, int shift) {
        RegionClipping region = forCoords(x, y);
        int localX = x - ((x >> 7) << 7);
        int localY = y - ((y >> 7) << 7);
        if (region.clippingMasks[z] == null) {
            region.clippingMasks[z] = new int[region.size][region.size];
        }
        region.clippingMasks[z][localX][localY] &= ~shift;
    }

    public static RegionClipping forCoords(int x, int y) {
        int regionX = x >> 7, regionY = y >> 7;
        RegionClipping r = regions[regionX][regionY];
        if (r == null) {
            r = regions[regionX][regionY] = new RegionClipping(regionX, regionY, REGION_SIZE);
        }
        return r;
    }

    public static RegionClipping forLocation(Location other) {
        return forCoords(other.getX(), other.getY());
    }

    public static int getClippingMask(int x, int y, int z) {
        RegionClipping region = forCoords(x, y);

        if (region.clippingMasks[z] == null || !region.clipped) {
            return -1;
        }
        int localX = x - ((x >> 7) << 7);
        int localY = y - ((y >> 7) << 7);

        return region.clippingMasks[z][localX][localY];
    }

    public static RegionClipping getRegion(int x, int y) {
        return forCoords(x, y);
    }

    public int[][] getClipping(int plane) {
        return clippingMasks[plane];
    }

    private int[][][] clippingMasks = new int[4][][];
    private int size;
    private int x;
    private int y;

    public Set<Player>[] players = new HashSet[4];
    public Set<NPC>[] npcs = new HashSet[4];

    private boolean clipped;

    public RegionClipping(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
        for (int i = 0; i < 4; i++) {
            players[i] = new HashSet<Player>();
            npcs[i] = new HashSet<NPC>();
        }
        loadedRegions.add(this);
    }

    public int getSize() {
        return size;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public int hashCode() {
        return x << 8 | y;
    }

    public void setClipped(boolean clipped) {
        this.clipped = clipped;
    }

    public boolean isClipped() {
        return clipped;
    }

    public static void addClipping(GameObject obj) {
        CacheObjectDefinition def = obj.getDefinition();
        int x = obj.getSpawnLocation().getX();
        int y = obj.getSpawnLocation().getY();
        int height = obj.getSpawnLocation().getPlane();
        int xLength;
        int yLength;
        if (obj.getDirection() != 1 && obj.getDirection() != 3) {
            xLength = def.sizeX;
            yLength = def.sizeY;
        } else {
            xLength = def.sizeY;
            yLength = def.sizeX;
        }

        GameObjectType type = GameObjectType.forType(obj.getType());
        
        if (ObjectManager.handleObjectRemoval(obj))
        	return;

        if (type == GameObjectType.WALL_OBJECT && def.clipType != 0) {
            addClippingForVariableObject(x, y, height, obj.getType(), obj.getDirection(), def.aBoolean3763);
        } else if (type == GameObjectType.INTERACTABLE_OBJECT && def.clipType != 0) {
            addClippingForSolidObject(x, y, height, xLength, yLength, def.aBoolean3763);
        } else if (type == GameObjectType.GROUND_DECORATION && def.clipType == 1) {
            addClipping(x, y, height, 0x40000);
        }
    }

    public static void removeClipping(GameObject obj) {
        CacheObjectDefinition def = obj.getDefinition();
        int xLength;
        int yLength;
        int x = obj.getLocation().getX();
        int y = obj.getLocation().getY();
        int height = obj.getLocation().getPlane();
        if (obj.getDirection() != 1 && obj.getDirection() != 3) {
            xLength = def.sizeX;
            yLength = def.sizeY;
        } else {
            xLength = def.sizeY;
            yLength = def.sizeX;
        }

        GameObjectType type = GameObjectType.forType(obj.getType());

        if (type == GameObjectType.WALL_OBJECT && def.clipType != 0) {
            removeClippingForVariableObject(x, y, height, obj.getType(), obj.getDirection(), def.aBoolean3763);
        } else if (type == GameObjectType.INTERACTABLE_OBJECT && def.clipType != 0) {
            removeClippingForSolidObject(x, y, height, xLength, yLength, def.aBoolean3763);
        } else if (type == GameObjectType.GROUND_DECORATION && def.clipType == 1) {
            removeClipping(x, y, height, 0x40000);
        }
    }

    private static void addClippingForSolidObject(int x, int y, int height, int xLength, int yLength, boolean flag) {
        int clipping = 256;
        if (flag) {
            clipping |= 0x20000;
        }
        for (int i = x; i < x + xLength; i++) {
            for (int i2 = y; i2 < y + yLength; i2++) {
                addClipping(i, i2, height, clipping);
            }
        }
    }

    public static void removeClippingForSolidObject(int x, int y, int height, int xLength, int yLength, boolean flag) {
        int clipping = 256;
        if (flag) {
            clipping |= 0x20000;
        }
        for (int i = x; i < x + xLength; i++) {
            for (int i2 = y; i2 < y + yLength; i2++) {
                removeClipping(i, i2, height, clipping);
            }
        }
    }

    private static void addClippingForVariableObject(int x, int y, int z, int type, int direction, boolean flag) {
        if (type == 0) {
            if (direction == 0) {
                addClipping(x, y, z, 128);
                addClipping(x - 1, y, z, 8);
            }
            if (direction == 1) {
                addClipping(x, y, z, 2);
                addClipping(x, y + 1, z, 32);
            }
            if (direction == 2) {
                addClipping(x, y, z, 8);
                addClipping(x + 1, y, z, 128);
            }
            if (direction == 3) {
                addClipping(x, y, z, 32);
                addClipping(x, y - 1, z, 2);
            }
        }
        if (type == 1 || type == 3) {
            if (direction == 0) {
                addClipping(x, y, z, 1);
                addClipping(x - 1, y + 1, z, 16);
            }
            if (direction == 1) {
                addClipping(x, y, z, 4);
                addClipping(x + 1, y + 1, z, 64);
            }
            if (direction == 2) {
                addClipping(x, y, z, 16);
                addClipping(x + 1, y - 1, z, 1);
            }
            if (direction == 3) {
                addClipping(x, y, z, 64);
                addClipping(x - 1, y - 1, z, 4);
            }
        }
        if (type == 2) {
            if (direction == 0) {
                addClipping(x, y, z, 130);
                addClipping(x - 1, y, z, 8);
                addClipping(x, y + 1, z, 32);
            }
            if (direction == 1) {
                addClipping(x, y, z, 10);
                addClipping(x, y + 1, z, 32);
                addClipping(x + 1, y, z, 128);
            }
            if (direction == 2) {
                addClipping(x, y, z, 40);
                addClipping(x + 1, y, z, 128);
                addClipping(x, y - 1, z, 2);
            }
            if (direction == 3) {
                addClipping(x, y, z, 160);
                addClipping(x, y - 1, z, 2);
                addClipping(x - 1, y, z, 8);
            }
        }
        if (flag) {
            if (type == 0) {
                if (direction == 0) {
                    addClipping(x, y, z, 0x10000);
                    addClipping(x - 1, y, z, 4096);
                }
                if (direction == 1) {
                    addClipping(x, y, z, 1024);
                    addClipping(x, y + 1, z, 16384);
                }
                if (direction == 2) {
                    addClipping(x, y, z, 4096);
                    addClipping(x + 1, y, z, 0x10000);
                }
                if (direction == 3) {
                    addClipping(x, y, z, 16384);
                    addClipping(x, y - 1, z, 1024);
                }
            }
            if (type == 1 || type == 3) {
                if (direction == 0) {
                    addClipping(x, y, z, 512);
                    addClipping(x - 1, y + 1, z, 8192);
                }
                if (direction == 1) {
                    addClipping(x, y, z, 2048);
                    addClipping(x + 1, y + 1, z, 32768);
                }
                if (direction == 2) {
                    addClipping(x, y, z, 8192);
                    addClipping(x + 1, y - 1, z, 512);
                }
                if (direction == 3) {
                    addClipping(x, y, z, 32768);
                    addClipping(x - 1, y - 1, z, 2048);
                }
            }
            if (type == 2) {
                if (direction == 0) {
                    addClipping(x, y, z, 0x10400);
                    addClipping(x - 1, y, z, 4096);
                    addClipping(x, y + 1, z, 16384);
                }
                if (direction == 1) {
                    addClipping(x, y, z, 5120);
                    addClipping(x, y + 1, z, 16384);
                    addClipping(x + 1, y, z, 0x10000);
                }
                if (direction == 2) {
                    addClipping(x, y, z, 20480);
                    addClipping(x + 1, y, z, 0x10000);
                    addClipping(x, y - 1, z, 1024);
                }
                if (direction == 3) {
                    addClipping(x, y, z, 0x14000);
                    addClipping(x, y - 1, z, 1024);
                    addClipping(x - 1, y, z, 4096);
                }
            }
        }
    }

    public static void removeClippingForVariableObject(int x, int y, int z, int type, int direction, boolean flag) {
        if (type == 0) {
            if (direction == 0) {
                removeClipping(x, y, z, 128);
                removeClipping(x - 1, y, z, 8);
            }
            if (direction == 1) {
                removeClipping(x, y, z, 2);
                removeClipping(x, 1 + y, z, 32);
            }
            if (direction == 2) {
                removeClipping(x, y, z, 8);
                removeClipping(1 + x, y, z, 128);
            }
            if (direction == 3) {
                removeClipping(x, y, z, 32);
                removeClipping(x, y - 1, z, 2);
            }
        }
        if (type == 1 || type == 3) {
            if (direction == 0) {
                removeClipping(x, y, z, 1);
                removeClipping(x - 1, 1 + y, z, 16);
            }
            if (direction == 1) {
                removeClipping(x, y, z, 4);
                removeClipping(1 + x, y + 1, z, 64);
            }
            if (direction == 2) {
                removeClipping(x, y, z, 16);
                removeClipping(x + 1, -1 + y, z, 1);
            }
            if (direction == 3) {
                removeClipping(x, y, z, 64);
                removeClipping(-1 + x, -1 + y, z, 4);
            }
        }
        if (type == 2) {
            if (direction == 0) {
                removeClipping(x, y, z, 130);
                removeClipping(x - 1, y, z, 8);
                removeClipping(x, 1 + y, z, 32);
            }
            if (direction == 1) {
                removeClipping(x, y, z, 10);
                removeClipping(x, 1 + y, z, 32);
                removeClipping(1 + x, y, z, 128);
            }
            if (direction == 2) {
                removeClipping(x, y, z, 40);
                removeClipping(x + 1, y, z, 128);
                removeClipping(x, -1 + y, z, 2);
            }
            if (direction == 3) {
                removeClipping(x, y, z, 160);
                removeClipping(x, y - 1, z, 2);
                removeClipping(-1 + x, y, z, 8);
            }
        }
        if (flag) {
            if (type == 0) {
                if (direction == 0) {
                    removeClipping(x, y, z, 0x10000);
                    removeClipping(-1 + x, y, z, 4096);
                }
                if (direction == 1) {
                    removeClipping(x, y, z, 1024);
                    removeClipping(x, 1 + y, z, 16384);
                }
                if (direction == 2) {
                    removeClipping(x, y, z, 4096);
                    removeClipping(x + 1, y, z, 0x10000);
                }
                if (direction == 3) {
                    removeClipping(x, y, z, 16384);
                    removeClipping(x, y - 1, z, 1024);
                }
            }
            if (type == 1 || type == 3) {
                if (direction == 0) {
                    removeClipping(x, y, z, 512);
                    removeClipping(-1 + x, 1 + y, z, 8192);
                }
                if (direction == 1) {
                    removeClipping(x, y, z, 2048);
                    removeClipping(1 + x, 1 + y, z, 32768);
                }
                if (direction == 2) {
                    removeClipping(x, y, z, 8192);
                    removeClipping(x + 1, -1 + y, z, 512);
                }
                if (direction == 3) {
                    removeClipping(x, y, z, 32768);
                    removeClipping(x - 1, -1 + y, z, 2048);
                }
            }
            if (type == 2) {
                if (direction == 0) {
                    removeClipping(x, y, z, 0x10400);
                    removeClipping(-1 + x, y, z, 4096);
                    removeClipping(x, y + 1, z, 16384);
                }
                if (direction == 1) {
                    removeClipping(x, y, z, 5120);
                    removeClipping(x, 1 + y, z, 16384);
                    removeClipping(x + 1, y, z, 0x10000);
                }
                if (direction == 2) {
                    removeClipping(x, y, z, 20480);
                    removeClipping(1 + x, y, z, 0x10000);
                    removeClipping(x, -1 + y, z, 1024);
                }
                if (direction == 3) {
                    removeClipping(x, y, z, 0x14000);
                    removeClipping(x, -1 + y, z, 1024);
                    removeClipping(-1 + x, y, z, 4096);
                }
            }
        }
    }

    public int getClippingFlag(int localX, int localY, int height) {
        return clippingMasks[height][localX][localY];
    }

    public static RegionClipping getRegion(int realRegionId) {

        return null;
    }

    public static boolean isPassable(Location l) {
        int clippingMask = getClippingMask(l.getX(), l.getY(), l.getPlane());

        if (clippingMask == -1) {
            return true; //?
        }
        return clippingMask < 1;
    }

    public static boolean passable(int x, int y, int z) {
        int clippingMask = getClippingMask(x, y, z);
        if (clippingMask == -1) {
            return true; //?
        }
        return (clippingMask & 0x1280180) == 0 && (clippingMask & 0x1280108) == 0
        && (clippingMask & 0x1280120) == 0 && (clippingMask & 0x1280102) == 0;
    }

    public static boolean isPassable(int x, int y, int z) {
        int clippingMask = getClippingMask(x, y, z);
        if (clippingMask == -1) {
            return true; //?
        }
        return clippingMask < 1;
    }

    public static List<RegionClipping> getRegions() {
        return loadedRegions;
    }

    // Isn't used anywhere
    public void setClipping(int x, int y, int z, int clipping) {
        if (clippingMasks[z] == null)
            clippingMasks[z] = new int[size][size];
        this.clippingMasks[z][x][y] = clipping;
    }
}