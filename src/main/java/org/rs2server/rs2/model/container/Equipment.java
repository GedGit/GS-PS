package org.rs2server.rs2.model.container;

import org.rs2server.rs2.model.Item;

/**
 * Contains equipment utility methods.
 * 
 * @author Graham Edgecombe
 * @author Lothy
 *
 */
public class Equipment {

	/**
	 * The size of the equipment container.
	 */
	public static final int SIZE = 14;

	/**
	 * The helmet slot.
	 */
	public static final int SLOT_HELM = 0;

	/**
	 * The cape slot.
	 */
	public static final int SLOT_CAPE = 1;

	/**
	 * The amulet slot.
	 */
	public static final int SLOT_AMULET = 2;

	/**
	 * The weapon slot.
	 */
	public static final int SLOT_WEAPON = 3;

	/**
	 * The chest slot.
	 */
	public static final int SLOT_CHEST = 4;

	/**
	 * The shield slot.
	 */
	public static final int SLOT_SHIELD = 5;

	/**
	 * The bottoms slot.
	 */
	public static final int SLOT_BOTTOMS = 7;

	/**
	 * The gloves slot.
	 */
	public static final int SLOT_GLOVES = 9;

	/**
	 * The boots slot.
	 */
	public static final int SLOT_BOOTS = 10;

	/**
	 * The rings slot.
	 */
	public static final int SLOT_RING = 12;

	/**
	 * The arrows slot.
	 */
	public static final int SLOT_ARROWS = 13;

	/**
	 * Equipment interface id.
	 */
	public static final int INTERFACE = 387;

	/**
	 * Equipment interface screen.
	 */
	public static final int SCREEN = 84;

	/**
	 * Equipment type enum.
	 * 
	 * @author Lothy
	 * @author Miss Silabsoft
	 *
	 */
	public enum EquipmentType {
		/**
		 * Item is a cape
		 */
		CAPE("Cape", Equipment.SLOT_CAPE),

		/**
		 * Item is a pair of boots
		 */
		BOOTS("Boots", Equipment.SLOT_BOOTS),

		/**
		 * Item is a pair of gloves
		 */
		GLOVES("Gloves", Equipment.SLOT_GLOVES),

		/**
		 * Item is a shield
		 */
		SHIELD("Shield", Equipment.SLOT_SHIELD),

		/**
		 * Item is a hat
		 */
		HAT("Hat", Equipment.SLOT_HELM),

		/**
		 * Item is an amulet
		 */
		AMULET("Amulet", Equipment.SLOT_AMULET),

		/**
		 * Item is a set of arrows
		 */
		ARROWS("Arrows", Equipment.SLOT_ARROWS),

		/**
		 * Item is a ring
		 */
		RING("Ring", Equipment.SLOT_RING),

		/**
		 * Item is a normal body with no sleeves
		 */
		BODY("Body", Equipment.SLOT_CHEST),

		/**
		 * Item is legs
		 */
		LEGS("Legs", Equipment.SLOT_BOTTOMS),

		/**
		 * Item is a platebody
		 */
		PLATEBODY("Platebody", Equipment.SLOT_CHEST),

		/**
		 * Item covers over hair
		 */
		FULL_HELM("Full helm", Equipment.SLOT_HELM),

		/**
		 * Item covers over head fully
		 */
		FULL_MASK("Full mask", Equipment.SLOT_HELM),

		/**
		 * Item is a weapon
		 */
		WEAPON("Weapon", Equipment.SLOT_WEAPON),

		/**
		 * Item is a weapon 2 handed
		 */
		WEAPON_2H("Two-handed weapon", Equipment.SLOT_WEAPON);

		/**
		 * The description.
		 */
		private String description;

		/**
		 * The slot.
		 */
		private int slot;

		/**
		 * Creates the equipment type.
		 * 
		 * @param description
		 *            The description.
		 * @param slot
		 *            The slot.
		 */
		private EquipmentType(String description, int slot) {
			this.description = description;
			this.slot = slot;
		}

		/**
		 * Gets the description.
		 * 
		 * @return The description.
		 */
		public String getDescription() {
			return description;
		}

		/**
		 * Gets the slot.
		 * 
		 * @return The slot.
		 */
		public int getSlot() {
			return slot;
		}

	}

	/**
	 * Gets an equipment type.
	 * 
	 * @param item
	 *            The item.
	 * @return The equipment type.
	 */
	public static EquipmentType getType(Item item) {
		if (item.getEquipmentDefinition() != null)
			return item.getEquipmentDefinition().getType();
		return null;
	}

	/**
	 * Checks if an item is of a specific type.
	 * 
	 * @param type
	 *            The type.
	 * @param item
	 *            The item.
	 * @return <code>true</code> if the types are the same, <code>false</code>
	 *         if not.
	 */
	public static boolean is(EquipmentType type, Item item) {
		final EquipmentType t = getType(item);
		return t != null && t.equals(type);
	}
}