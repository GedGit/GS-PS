
package org.rs2server.cache.format.loaders;

import org.rs2server.cache.CacheContainer;
import org.rs2server.cache.CacheManager;
import org.rs2server.cache.stream.ByteInputStream;
import org.rs2server.cache.stream.RSInputStream;
import org.rs2server.rs2.Constants;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.map.RegionClipping;
import org.rs2server.rs2.util.XTEA;
import org.rs2server.util.MapXTEA;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class WorldMapObjectsLoader {

	public static Logger log = Logger.getAnonymousLogger();
	private static ExecutorService mapService = Executors.newFixedThreadPool(1);

	public static void loadMaps(Location loc) {
		for (int xCalc = (loc.getRegionX() - 6) / 8; xCalc <= ((loc.getRegionX() + 6) / 8); xCalc++) {
			for (int yCalc = (loc.getRegionY() - 6) / 8; yCalc <= ((loc.getRegionY() + 6) / 8); yCalc++) {
				int region = yCalc + (xCalc << 8);
				submitRegionRequest(region);
			}
		}
	}

	public static void submitRegionRequest(int region) {
		mapService.submit(() -> {
			try {
				loadRegionMap(region);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private static Map<Integer, Boolean> activeMaps = new HashMap<Integer, Boolean>();

	public static void loadRegionMap(int regionId) throws Exception {
		if (activeMaps.get(regionId) != null)
			return;
		activeMaps.put(regionId, Boolean.TRUE);
		int regionX = regionId >> 8;
		int regionY = regionId & 0xFF;
		int[] keys = MapXTEA.getKey(regionId);

		byte[] landscapeMap = CacheManager.getByName(5, "m" + regionX + "_" + regionY);
		byte[] objectMap = CacheManager.getByName(5, "l" + regionX + "_" + regionY);
		if (landscapeMap == null && objectMap == null) {
			if (Constants.DEBUG)
				System.out.println("Map [" + (regionX << 6) + ", " + (regionY << 6) + "] was not found in the cache!");
			return;
		}
		RSInputStream str2 = null;
		ByteInputStream str1 = null;
		if (landscapeMap != null)
			str2 = new RSInputStream(new ByteArrayInputStream(new CacheContainer(landscapeMap).decompress()));
		
		if (objectMap != null) {
			if (keys != null)
				objectMap = XTEA.decrypt(keys, objectMap, 5, objectMap.length);
			
			str1 = new ByteInputStream(new CacheContainer(objectMap).decompress());
		}
		int baseX = regionX << 6;
		int baseY = regionY << 6;
		byte[][][] mapSettings = new byte[4][64][64];
		if (str2 != null) {
			for (int plane = 0; plane < 4; plane++) {
				for (int x = 0; x < 64; x++) {
					for (int y = 0; y < 64; y++) {
						while (true) {
							int v = str2.readByte() & 0xff;
							if (v == 0) {
								break;
							} else if (v == 1) {
								str2.readByte();
								break;
							} else if (v <= 49) {
								str2.readByte();
							} else if (v <= 81) {
								mapSettings[plane][x][y] = (byte) (v - 49);
							}
						}
					}
				}
			}

		}
		for (int plane = 0; plane < 4; plane++) {
			for (int x = 0; x < 64; x++) {
				for (int y = 0; y < 64; y++) {
					if ((mapSettings[plane][x][y] & 1) == 1) {
						int height = plane;
						if ((mapSettings[1][x][y] & 2) == 2) {
							height--;
						}
						int absX = x + baseX;
						int absY = y + baseY;
						if (height >= 0 && height <= 4)
							RegionClipping.addClipping(absX, absY, plane, 0x200000);
					}
				}
			}
		}
		if (str1 != null) {
			int objectId = -1;
			int incr;

			// This causes a null-pointer exception.
			while ((incr = str1.readSmart2()) != 0) {
				objectId += incr;
				int location = 0;
				int incr2;
				while ((incr2 = str1.readSmart()) != 0) {
					location += incr2 - 1;
					int localX = (location >> 6 & 0x3f);
					int localY = (location & 0x3f);
					int plane = location >> 12;
					int objectData = str1.readUByte();
					int type = objectData >> 2;
					int rotation = objectData & 0x3;
					if (localX < 0 || localX >= 64 || localY < 0 || localY >= 64)
						continue;
					if ((mapSettings[1][localX][localY] & 2) == 2)
						plane--;
					if (plane >= 0 && plane <= 3) {
						Location loc = Location.create(baseX + localX, baseY + localY, plane);
						GameObject obj = new GameObject(loc, objectId, type, rotation, true);
						World.getWorld().register(obj);
						RegionClipping.addClipping(obj);
					}
				}
			}
		}
	}
}
