package org.rs2server.rs2.model.npc;

import org.rs2server.cache.format.CacheNPCDefinition;
import org.rs2server.io.FileUtilities;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.boundary.Boundary;
import org.rs2server.rs2.model.map.Directions.NormalDirection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class NPCSpawnLoader {

	private static final Logger logger = Logger.getLogger(NPCSpawnLoader.class.getName());
	private static final Map<String, Integer> DIRECTIONS = new HashMap<String, Integer>();
	static {
		for (NormalDirection dir : NormalDirection.values())
			DIRECTIONS.put(dir.name(), dir.npcIntValue());
	}

	public static void init() {
		logger.info("Loading default npc spawns...");
		int size = 0;
		boolean ignore = false;
		try {
			for (String string : FileUtilities.readFile("data/npcspawns.txt")) {
				if (string.startsWith("//") || string.equals(""))
					continue;
				if (string.contains("/*")) {
					ignore = true;
					continue;
				}
				if (ignore) {
					if (string.contains("*/"))
						ignore = false;
					continue;
				}
				String[] spawn = string.split(" ");
				int id = Integer.parseInt(spawn[0]), x = Integer.parseInt(spawn[1]), y = Integer.parseInt(spawn[2]),
						z = Integer.parseInt(spawn[3]);
				String dirS = spawn[4];
				int dir = 0;
				if (DIRECTIONS.containsKey(dirS))
					dir = DIRECTIONS.get(dirS);
				else
					dir = Integer.parseInt(dirS);
				boolean doesWalk = Boolean.parseBoolean(spawn[5]);
				Boundary boundary = null;
				Location spawnLoc = Location.create(x, y, z);
				Location minLoc = null, maxLoc = null;
				if (doesWalk && boundary == null) {
					minLoc = Location.create(x - 3, y - 3, z);
					maxLoc = Location.create(x + 3, y + 3, z);
				} else if (boundary != null) {
					minLoc = boundary.getBottomLeft();
					maxLoc = boundary.getTopRight();
				}
				NPC npc = new NPC(id, spawnLoc, minLoc, maxLoc, dir);
				//if (npc.getId() == 492)
				//	npc = new CaveKraken(npc.getId(), spawnLoc);
				npc.setHomeArea(boundary);
				World.getWorld().register(npc);
				size++;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("Loaded " + size + " default npc spawns.");
	}

	/**
	 * Defines the writer.
	 */
	private static BufferedWriter writer;

	/**
	 * Adds an NPC spawn to our custom spawns text file.
	 * 
	 * @param username
	 *            the player adding the npc
	 * @param id
	 *            the npc id to add
	 * @param tile
	 *            the world tile to add the npc spawn onto
	 * @return if successful
	 * @throws Throwable
	 *             if error :?
	 */
	public static boolean addSpawn(String username, int id, Location tile) throws Throwable {
		File file = new File("data/npcspawns.txt");
		writer = new BufferedWriter(new FileWriter(file, true));
		writer.write("// " + CacheNPCDefinition.get(id).getName() + ", " + CacheNPCDefinition.get(id).getCombatLevel()
				+ ", added by: " + username);
		writer.flush();
		writer.newLine();
		writer.write(id + " " + tile.getX() + " " + tile.getY() + " " + tile.getPlane()+" SOUTH true");
		writer.newLine();
		writer.flush();
		NPC npc = new NPC(id, Location.create(tile.getX(), tile.getY(), tile.getPlane()), tile, tile, 6);
		World.getWorld().register(npc);
		writer.close();
		return true;
	}
}