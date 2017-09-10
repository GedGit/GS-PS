package org.rs2server.util;

/**
 * Holds all NPC junk.
 * 
 * @author Vichy
 */
public final class NPCUtils {

	/**
	 * Checks if the given NPC name counts as undead.
	 * 
	 * @param name
	 *            the NPC name to check.
	 * @return if is undead.
	 */
	public static boolean isUndeadNPC(String name) {
		if (name.contains("spectre") || name.contains("ankou") || name.contains("banshee") || name.contains("crawling")
				|| name.contains("ghast") || name.contains("ghost") || name.contains("mummy") || name.contains("zombie")
				|| name.contains("shade") || name.contains("skeleton") || name.contains("tortured")
				|| name.contains("undead") || name.contains("vet'ion") || name.contains("zogre"))
			return true;
		return false;
	}
}