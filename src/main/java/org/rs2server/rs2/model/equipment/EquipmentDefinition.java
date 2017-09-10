package org.rs2server.rs2.model.equipment;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.combat.impl.RangeCombatAction.ArrowType;
import org.rs2server.rs2.model.combat.impl.RangeCombatAction.BowType;
import org.rs2server.rs2.model.combat.impl.RangeCombatAction.RangeWeaponType;
import org.rs2server.rs2.model.container.Equipment.EquipmentType;
import org.rs2server.util.XMLController;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Defines the equipment information that is requested
 * 
 * @author Sir Sean
 *
 */
public class EquipmentDefinition {

	private static EquipmentAnimations DEFAULT_ANIMATIONS = new EquipmentAnimations(Animation.create(808),
			Animation.create(819), Animation.create(824),
			new Animation[] { Animation.create(422), Animation.create(423), Animation.create(422),
					Animation.create(422) },
			Animation.create(424), Animation.create(823), Animation.create(820), Animation.create(821),
			Animation.create(822));

	private static WeaponStyle[] DEFAULT_WEAPON_STYLES = new WeaponStyle[] { WeaponStyle.NONE };

	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(EquipmentDefinition.class.getName());

	/**
	 * We shall allocate a size for the HashMap!
	 */
	public static final int ALLOCATED_SIZE = 2384;

	/**
	 * The <code>EquipmentDefinition</code> map.
	 */
	private static Map<Integer, EquipmentDefinition> definitions;

	/**
	 * @return the definitions
	 */
	public static Map<Integer, EquipmentDefinition> getDefinitions() {
		return definitions;
	}

	/**
	 * @param definitions
	 *            the definitions to set
	 */
	public static void setDefinitions(Map<Integer, EquipmentDefinition> definitions) {
		EquipmentDefinition.definitions = definitions;
	}

	/**
	 * Gets a definition for the specified id.
	 * 
	 * @param id
	 *            The id.
	 * @return The definition.
	 */
	public static EquipmentDefinition forId(int id) {
		return definitions.get(id);
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
		logger.info("Loading equipment definitions...");
		try {
			/**
			 * Load equipment definitions.
			 */
			definitions = new HashMap<Integer, EquipmentDefinition>(ALLOCATED_SIZE);
			File file = new File("data/items/equipmentDefinition.xml");
			if (file.exists()) {
				definitions = XMLController.readXML(file);
				logger.info("Loaded " + definitions.size() + " equipment definitions.");
			} else {
				logger.info("Equipment definitions not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// EquipmentDefinition def = new EquipmentDefinition();
		// def.bonuses = new int[] {-5, -5, -5, -5, +10, +6, +8, +10, +10, +8,
		// +12, 0, 0, 0};
		// def.type = EquipmentType.FULL_HELM;
		// def.skillRequirements = new HashMap<Skill, Integer>();
		// def.skillRequirements.put(Skill.DEFENCE, 70);
		// def.skillRequirements.put(Skill.RANGE, 70);
		// definitions.put(11718, def);
		// System.out.println("Dumped ");
		// XMLController.writeXML(definitions, new
		// File("data/equipmentDefinition.xml"));

		/* please leave incase I need to redump any invalid values, thanks. */

	}

	/**
	 * The weapon speed
	 */
	private int speed;

	/**
	 * The item bonuses
	 */
	private int[] bonuses;

	/**
	 * The weapon type
	 */
	private WeaponStyle[] weaponStyles;

	/**
	 * The equipment types
	 */
	private EquipmentType type;

	/**
	 * The equipment animations
	 */
	private EquipmentAnimations animations;

	/**
	 * The equipment's poison type.
	 */
	private PoisonType poisonType;

	/**
	 * The amount of special energy this item uses.
	 */
	private int specialConsumption;

	/**
	 * The amount of hits this special deals.
	 */
	private int specialHits;

	/**
	 * The npc's skill levels.
	 */
	private Map<Skill, Integer> skillRequirements;

	/**
	 * The bow type of this item.
	 */
	private BowType bowType;

	/**
	 * The arrow type of this item.
	 */
	private ArrowType arrowType;

	/**
	 * The range weapon type of this item.
	 */
	private RangeWeaponType rangeWeaponType;

	private boolean degradable;

	/**
	 * The weapon speed
	 * 
	 * @return the weapon speed
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * Gets a bonus by its index.
	 * 
	 * @param index
	 *            The bonus index.
	 * @return The bonus amount.
	 */
	public int getBonus(int index) {
		return bonuses[index];
	}

	/**
	 * The item bonuses
	 * 
	 * @return the item bonuses
	 */
	public int[] getBonuses() {
		return bonuses;
	}

	/**
	 * Gets the weapon type
	 * 
	 * @return The weapon type
	 */
	public WeaponStyle[] getWeaponStyles() {
		if (weaponStyles == null) {
			return DEFAULT_WEAPON_STYLES;
		}
		return weaponStyles;
	}

	/**
	 * Gets the weapon type by its index.
	 * 
	 * @return The weapon type by its index.
	 */
	public WeaponStyle getWeaponStyle(int index) {
		if (weaponStyles == null) {
			if (index > DEFAULT_WEAPON_STYLES.length) {
				index = DEFAULT_WEAPON_STYLES.length;
			}
			return DEFAULT_WEAPON_STYLES[index];
		}
		return weaponStyles[index];
	}

	/**
	 * Gets the equipment types
	 * 
	 * @return The equipment types
	 */
	public EquipmentType getType() {
		return type;
	}

	/**
	 * Gets the equipment animation instance
	 * 
	 * @return the equipmentAnimation
	 */
	public EquipmentAnimations getAnimation() {
		if (animations == null) {
			return DEFAULT_ANIMATIONS;
		}
		return animations;
	}

	/**
	 * Gets the weapons poison type
	 * 
	 * @return The weapons poison type
	 */
	public PoisonType getPoisonType() {
		if (poisonType == null) {
			return PoisonType.NONE;
		}
		return poisonType;
	}

	/**
	 * Gets the weapons skill requirements.
	 * 
	 * @param index
	 *            The skill index.
	 * @return The weapons skill requirements.
	 */
	public int getSkillRequirement(int index) {
		return skillRequirements.get(Skill.skillForId(index));
	}

	/**
	 * Gets the weapons skill requirements.
	 * 
	 * @param index
	 *            The skill index.
	 * @return The weapons skill requirements.
	 */
	public Map<Skill, Integer> getSkillRequirements() {
		return skillRequirements;
	}

	/**
	 * Gets the amount of special energy this item consumes.
	 * 
	 * @return The amount of special energy this item consumes.
	 */
	public int getSpecialConsumption() {
		return specialConsumption;
	}

	/**
	 * Gets the amount of hits this special attack deals.
	 * 
	 * @return The amount of hits this special attack deals.
	 */
	public int getSpecialHits() {
		return specialHits;
	}

	/**
	 * @return the bowType
	 */
	public BowType getBowType() {
		return bowType;
	}

	/**
	 * @return the arrowType
	 */
	public ArrowType getArrowType() {
		return arrowType;
	}

	/**
	 * @return the rangeWeaponType
	 */
	public RangeWeaponType getRangeWeaponType() {
		return rangeWeaponType;
	}

	public boolean isDegradable() {
		return degradable;
	}

	public void setSpecialHits(int specialHits) {
		this.specialHits = specialHits;
	}

	/**
	 * The skill statistic enum.
	 * 
	 * @author Michael
	 */
	public enum Skill {

		ATTACK(Skills.ATTACK),

		AGILITY(Skills.AGILITY),

		COOKING(Skills.COOKING),

		SMITHING(Skills.SMITHING),

		HERBLORE(Skills.HERBLORE),

		HUNTER(Skills.HUNTER),

		THIEVING(Skills.THIEVING),

		FLETCHING(Skills.FLETCHING),

		DEFENCE(Skills.DEFENCE),

		HITPOINTS(Skills.HITPOINTS),

		RANGE(Skills.RANGE),

		MAGIC(Skills.MAGIC),

		PRAYER(Skills.PRAYER),

		STRENGTH(Skills.STRENGTH),

		FISHING(Skills.FISHING),

		FARMING(Skills.FARMING),

		SLAYER(Skills.SLAYER);

		/**
		 * The list of skills.
		 */
		private static Map<Integer, Skill> skills = new HashMap<Integer, Skill>();

		public static Skill skillForId(int skill) {
			return skills.get(skill);
		}

		/**
		 * Populates the skill list.
		 */
		static {
			for (Skill skill : Skill.values()) {
				skills.put(skill.getId(), skill);
			}
		}

		/**
		 * The id of the skill.
		 */
		private int id;

		private Skill(int id) {
			this.id = id;
		}

		/**
		 * @return the id
		 */
		public int getId() {
			return id;
		}
	}

}