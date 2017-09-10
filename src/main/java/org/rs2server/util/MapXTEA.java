package org.rs2server.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.rs2server.rs2.Constants;

public class MapXTEA {

	private static Map<Integer, int[]> mapKeys = new HashMap<Integer, int[]>();

	public static void init() {
		try {
			loadUnpacked();
			Logger.getAnonymousLogger().info("Loaded " + mapKeys.size() + " map XTEA key(s)");
		} catch (Exception e) {
			System.err.println("Failed to load map xtea(s)!");
			e.printStackTrace();
		}
	}

	public static void loadUnpacked() throws IOException {
		File directory = new File(Constants.XTEA_PATH);
		if (directory.isDirectory()) {
			for (File file : directory.listFiles()) {
				if (file.isFile()) {
					BufferedReader input = new BufferedReader(new FileReader(file));
					int id = Integer.parseInt(file.getName().substring(0, file.getName().indexOf(".")));
					int[] keys = new int[4];
					for (int i = 0; i < 4; i++) {
						String line = input.readLine();
						if (line != null)
							keys[i] = Integer.parseInt(line);
						else {
							System.err.println("Corrupted XTEA file : " + id + "; line: " + line);
							keys[i] = 0;
						}
					}
					input.close();
					mapKeys.put(id, keys);
				}
			}
		}
	}

	public static int[] getKey(int region) {
		int[] keys = mapKeys.get(region);
		if (keys == null)
			return new int[4];
		return keys;
	}

	public static Map<Integer, int[]> getMapKeys() {
		return mapKeys;
	}
}
