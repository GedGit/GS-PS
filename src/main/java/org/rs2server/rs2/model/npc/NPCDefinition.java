package org.rs2server.rs2.model.npc;

import org.rs2server.cache.Cache;
import org.rs2server.cache.format.CacheNPCDefinition;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * <p>
 * Represents a type of NPC.
 * </p>
 * 
 * @author Graham Edgecombe
 *
 */
public class NPCDefinition {

	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(NPCDefinition.class.getName());

	/**
	 * The definitions array.
	 */
	private static NPCDefinition[] definitions;

	/**
	 * @return the definitions
	 */
	public static NPCDefinition[] getDefinitions() {
		return definitions;
	}

	/**
	 * @param definitions
	 *            the definitions to set
	 */
	public static void setDefinitions(NPCDefinition[] definitions) {
		NPCDefinition.definitions = definitions;
	}

	/**
	 * Adds a definition. TODO better way?
	 * 
	 * @param def
	 *            The definition.
	 */
	static void addDefinition(NPCDefinition def) {
		definitions[def.getId()] = def;
	}

	/**
	 * Gets an npc definition by its id.
	 * 
	 * @param id
	 *            The id.
	 * @return The definition.
	 */
	public static NPCDefinition forId(int id) {
		return definitions[id];
	}

	/**
	 * Gets an npc definition by its id.
	 * 
	 * @param id
	 *            The id.
	 * @return The definition.
	 */
	public static CacheNPCDefinition get(int id) {
		return CacheNPCDefinition.get(id);
	}

	/**
	 * Loads the item definitions.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs.
	 * @throws IllegalStateException
	 *             if the definitions have been loaded already.
	 */
	public static void init() throws IOException {
		if (definitions != null)
			throw new IllegalStateException("NPC definitions already loaded.");
		
		logger.info("Loading NPC definitions...");

		definitions = new NPCDefinition[Cache.getAmountOfNpcs()];// just print how many real quick onur pc
		for (int i = 0; i < definitions.length; i++) {
			// CacheNPCDefinition def = CacheNPCDefinition.forID(i);
			CacheNPCDefinition def = CacheNPCDefinition.get(i);
			if (def == null) {
				continue;
			}
			int id = i;
			if (id == -1) {
				continue;
			}
			String name = def.name;
			String examine = "It's a " + name;
			byte size = (byte) def.occupiedTiles;
			int combatLevel = def.combatLevel;
			String[] interactionMenu = def.options;

			definitions[id] = new NPCDefinition(id, name, examine, combatLevel, size, interactionMenu);
		}
		logger.info("Loaded " + definitions.length + " NPC definitions.");
	}

	/**
	 * The npc's id.
	 */
	private int id;

	/**
	 * The npc's name.
	 */
	private String name;

	/**
	 * The npc's description.
	 */
	private String description;

	/**
	 * The npc's size.
	 */
	private int size;

	/**
	 * The npc's combat level.
	 */
	private int combatLevel;

	/**
	 * The npc's right click options.
	 */
	private String[] interactionMenu;

	/**
	 * Creates the definition.
	 * 
	 * @param id
	 *            The id.
	 */
	public NPCDefinition(int id, String name, String description, int combatLevel, int size, String[] interactionMenu) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.combatLevel = combatLevel;
		this.size = size;
		this.interactionMenu = interactionMenu;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the combatLevel
	 */
	public int getCombatLevel() {
		return combatLevel;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	public static int forName(String text) {
		for (NPCDefinition d : definitions)
			if (d.name.equalsIgnoreCase(text))
				return d.id;
		return -1;
	}

	/**
	 * @return The interactionMenu.
	 */
	public String[] getInteractionMenu() {
		return interactionMenu;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public static NPCDefinition quickDef(int id) {
		String[] options = null;
		return new NPCDefinition(id, "", "", 1, 1, options);
	}

}
