package org.rs2server.rs2.model;

import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.ActionSender;
import org.rs2server.rs2.tickable.impl.PrayerUpdateTick;
import java.util.HashMap;
import java.util.Map;

public class Prayers {

	/**
	 * Represents types of prayers.
	 * 
	 * @author Scu11
	 *
	 */
	public enum Prayer {

		/**
		 * Thick skin.
		 */
		THICK_SKIN(Prayers.THICK_SKIN, "Thick Skin", 1, 0, 1, 12), // d

		/**
		 * Burst of Strength.
		 */
		BURST_OF_STRENGTH(Prayers.BURST_OF_STRENGTH, "Burst of Strength", 4, 0, 2, 12), // d

		/**
		 * Clarity of Thought.
		 */
		CLARITY_OF_THOUGHT(Prayers.CLARITY_OF_THOUGHT, "Clarity of Thought", 7, 0, 4, 12), // d

		/**
		 * Rock Skin.
		 */
		ROCK_SKIN(Prayers.ROCK_SKIN, "Rock Skin", 10, 0, 8, 6), // d

		/**
		 * Superhuman Strength.
		 */
		SUPERHUMAN_STRENGTH(Prayers.SUPERHUMAN_STRENGTH, "Superhuman Strength", 13, 0, 16, 6), // d

		/**
		 * Improved Reflexes.
		 */
		IMPROVED_REFLEXES(Prayers.IMPROVED_REFLEXES, "Improved Reflexes", 16, 0, 32, 6), // d

		/**
		 * Rapid Restore.
		 */
		RAPID_RESTORE(Prayers.RAPID_RESTORE, "Rapid Restore", 19, 0, 64, 26), // d

		/**
		 * Rapid Heal.
		 */
		RAPID_HEAL(Prayers.RAPID_HEAL, "Rapid Heal", 22, 0, 128, 18), // d?

		/**
		 * Protect Items.
		 */
		PROTECT_ITEMS(Prayers.PROTECT_ITEM, "Protect Items", 25, 0, 256, 18), // d

		/**
		 * Steel Skin.
		 */
		STEEL_SKIN(Prayers.STEEL_SKIN, "Steel Skin", 28, 0, 512, 3), // d

		/**
		 * Ultimate Strength.
		 */
		ULTIMATE_STRENGTH(Prayers.ULTIMATE_STRENGTH, "Ultimate Strength", 31, 0, 1024, 3), // d

		/**
		 * Incredible Reflexes.
		 */
		INCREDIBLE_REFLEXES(Prayers.INCREDIBLE_REFLEXES, "Incredible Reflexes", 34, 0, 2048, 3), // d

		/**
		 * Protect from Magic.
		 */
		PROTECT_FROM_MAGIC(Prayers.PROTECT_FROM_MAGIC, "Protect from Magic", 37, 2, 4096, 3), // d

		/**
		 * Protect from Missiles.
		 */
		PROTECT_FROM_MISSILES(Prayers.PROTECT_FROM_MISSILES, "Protect from Missiles", 40, 1, 8192, 3), // d

		/**
		 * Protect from Melee.
		 */
		PROTECT_FROM_MELEE(Prayers.PROTECT_FROM_MELEE, "Protect from Melee", 43, 0, 16384, 3), // d

		/**
		 * Retribution.
		 */
		RETRIBUTION(Prayers.RETRIBUTION, "Retribution", 46, 3, 32768, 12), // d

		/**
		 * Redemption.
		 */
		REDEMPTION(Prayers.REDEMPTION, "Redemption", 49, 5, 65536, 6), // d

		/**
		 * Smite.
		 */
		SMITE(Prayers.SMITE, "Smite", 52, 4, 131072, 2), // d

		/**
		 * Sharp Eye
		 */
		SHARP_EYE(Prayers.SHARP_EYE, "Sharp Eye", 8, 0, 262144, 12), // d

		/**
		 * Mystic Will
		 */
		MYSTIC_WILL(Prayers.MYSTIC_WILL, "Mystic Will", 9, 0, 524288, 12), // d

		/**
		 * Hawk Eye
		 */
		HAWK_EYE(Prayers.HAWK_EYE, "Hawk Eye", 26, 0, 1048576, 6), // d

		/**
		 * Mystic Lore
		 */
		MYSTIC_LORE(Prayers.MYSTIC_LORE, "Mystic Lore", 27, 0, 2097152, 6), // d

		/**
		 * Eagle Eye
		 */
		EAGLE_EYE(Prayers.EAGLE_EYE, "Eagle Eye", 44, 0, 4194304, 3), // d

		/**
		 * Mystic Might
		 */
		MYSTIC_MIGHT(Prayers.MYSTIC_MIGHT, "Mystic Might", 45, 0, 8388608, 3), // d

		/**
		 * Chivalry
		 */
		CHIVALRY(Prayers.CHIVALRY, "Chivalry", 60, 0, 33554432, 1.5), // d

		/**
		 * Piety
		 */
		PIETY(Prayers.PIETY, "Piety", 70, 0, 67108864, 1.5), // d

		/**
		 * Rigour
		 */
		RIGOUR(Prayers.RIGOUR, "Rigour", 74, 0, 1073741824, 1.5), // Preserver
																	// config *
																	// 2 is not
																	// the one

		/**
		 * Augury
		 */
		AUGURY(Prayers.AUGURY, "Augury", 77, 0, 134217728, 1.5), // d

		/**
		 * Augury
		 */
		PRESERVE(Prayers.PRESERVE, "Preserve", 55, 0, 268435456, 3) // d
		;

		/**
		 * A map of prayer IDs.
		 */
		private static Map<Integer, Prayer> prayers = new HashMap<Integer, Prayer>();

		/**
		 * Gets a prayer by its ID.
		 * 
		 * @param prayer
		 *            The prayer id.
		 * @return The prayer, or <code>null</code> if the id is not a prayer.
		 */
		public static Prayer forId(int prayer) {
			return prayers.get(prayer);
		}

		/**
		 * Populates the prayer map.
		 */
		static {
			for (Prayer prayer : Prayer.values()) {
				prayers.put(prayer.id, prayer);
			}
		}

		/**
		 * The id of this prayer.
		 */
		private int id;

		/**
		 * The name of this prayer.
		 */
		private String name;

		/**
		 * The required level for this prayer.
		 */
		private int level;

		/**
		 * The client configuration for this prayer.
		 */
		private int config;

		/**
		 * The head icon for this prayer.
		 */
		private int icon;

		/**
		 * The amount of seconds it takes to drain one prayer point.
		 */
		private double drain;

		/**
		 * Creates the prayer.
		 * 
		 * @param prayer
		 *            The prayer id.
		 * @return
		 */
		Prayer(int id, String name, int level, int icon, int config, double drain) {
			this.id = id;
			this.name = name;
			this.level = level;
			this.config = config;
			this.icon = icon;
			this.drain = drain;
		}

		/**
		 * Gets the prayer id.
		 * 
		 * @return The prayer id.
		 */
		public int getPrayerId() {
			return id;
		}

		/**
		 * Gets the prayer name.
		 * 
		 * @return The prayer name.
		 */
		public String getPrayerName() {
			return name;
		}

		/**
		 * Gets the level required for this prayer.
		 * 
		 * @return The level required for this prayer.
		 */
		public int getLevelRequired() {
			return level;
		}

		/**
		 * Gets the client configuration for this prayer.
		 * 
		 * @return The client configuration for this prayer.
		 */
		public int getConfig() {
			return config;
		}

		/**
		 * Gets the head icon for this prayer.
		 * 
		 * @return The head icon for this prayer.
		 */
		public int getHeadIcon() {
			return icon;
		}

		/**
		 * Gets the amount of prayer points this prayer drains every tick.
		 * 
		 * @return The amount of prayer points this prayer drains every tick.
		 */
		public double getDrain() {
			return drain;
		}
	}

	public static void setQuickPrayer(Player player, int childButton) {
		if (player.getCombatState().isDead()) {
			refresh(player);
			return;
		}
		ActionSender action = player.getActionSender();
		
		if (childButton == 29)
			childButton = CHIVALRY;
		if (childButton == 30)
			childButton = PIETY;
		
		// TODO FIX RIGOUR .-.

		if (childButton == Prayers.RIGOUR) {
			if (!player.getDatabaseEntity().getStatistics().hasUnlockedRigour()) {
				action.sendMessage("You need to unlock this prayer with a dexterous prayer scroll.");
				refreshQuickPrayers(player);
				refresh(player);
				return;
			}
			if (player.getSkills().getLevelForExperience(Skills.DEFENCE) < 70) {
				action.sendMessage("You need a defence level of 70 to use that.");
				refreshQuickPrayers(player);
				return;
			}
		}
		if (childButton == Prayers.PRESERVE && !player.getDatabaseEntity().getStatistics().hasUnlockedPreserve()) {
			action.sendMessage("You need to unlock this prayer with a torn prayer scroll.");
			refreshQuickPrayers(player);
			refresh(player);
			return;
		}
		if (childButton == Prayers.AUGURY) {
			if (!player.getDatabaseEntity().getStatistics().hasUnlockedAugury()) {
				action.sendMessage("You need to unlock this prayer with an arcane prayer scroll.");
				refreshQuickPrayers(player);
				refresh(player);
				return;
			}
			if (player.getSkills().getLevelForExperience(Skills.DEFENCE) < 70) {
				action.sendMessage("You need a defence level of 70 to use that.");
				refreshQuickPrayers(player);
				return;
			}
		}
		if (childButton == Prayers.PIETY && player.getSkills().getLevelForExperience(Skills.DEFENCE) < 70) {
			action.sendMessage("You need a defence level of 70 to use that.");
			refreshQuickPrayers(player);
			return;
		}
		if (childButton == Prayers.CHIVALRY && player.getSkills().getLevelForExperience(Skills.DEFENCE) < 60) {
			action.sendMessage("You need a defence level of 60 to use that.");
			refreshQuickPrayers(player);
			return;
		}
		player.getCombatState().setQuickPrayer(childButton, !player.getCombatState().getQuickPrayer(childButton));
		int[] deactivatePrayers = new int[0];
		if (player.getCombatState().getQuickPrayer(childButton)) {
			switch (childButton) {
			case Prayers.THICK_SKIN:
				deactivatePrayers = new int[] { Prayers.ROCK_SKIN, Prayers.STEEL_SKIN, Prayers.CHIVALRY, Prayers.PIETY,
						Prayers.AUGURY, Prayers.RIGOUR };
				break;
			case Prayers.BURST_OF_STRENGTH:
				deactivatePrayers = new int[] { Prayers.SHARP_EYE, Prayers.MYSTIC_WILL, Prayers.SUPERHUMAN_STRENGTH,
						Prayers.HAWK_EYE, Prayers.MYSTIC_LORE, Prayers.ULTIMATE_STRENGTH, Prayers.EAGLE_EYE,
						Prayers.MYSTIC_MIGHT, Prayers.CHIVALRY, Prayers.PIETY, Prayers.AUGURY, Prayers.RIGOUR };
				break;
			case Prayers.CLARITY_OF_THOUGHT:
				deactivatePrayers = new int[] { Prayers.SHARP_EYE, Prayers.MYSTIC_WILL, Prayers.IMPROVED_REFLEXES,
						Prayers.HAWK_EYE, Prayers.MYSTIC_LORE, Prayers.INCREDIBLE_REFLEXES, Prayers.EAGLE_EYE,
						Prayers.MYSTIC_MIGHT, Prayers.CHIVALRY, Prayers.PIETY, Prayers.AUGURY, Prayers.RIGOUR };
				break;
			case Prayers.SHARP_EYE:
				deactivatePrayers = new int[] { Prayers.BURST_OF_STRENGTH, Prayers.CLARITY_OF_THOUGHT,
						Prayers.MYSTIC_WILL, Prayers.SUPERHUMAN_STRENGTH, Prayers.IMPROVED_REFLEXES, Prayers.HAWK_EYE,
						Prayers.MYSTIC_LORE, Prayers.ULTIMATE_STRENGTH, Prayers.INCREDIBLE_REFLEXES, Prayers.EAGLE_EYE,
						Prayers.MYSTIC_MIGHT, Prayers.CHIVALRY, Prayers.PIETY, Prayers.AUGURY, Prayers.RIGOUR };
				break;
			case Prayers.MYSTIC_WILL:
				deactivatePrayers = new int[] { Prayers.BURST_OF_STRENGTH, Prayers.CLARITY_OF_THOUGHT,
						Prayers.SHARP_EYE, Prayers.SUPERHUMAN_STRENGTH, Prayers.IMPROVED_REFLEXES, Prayers.HAWK_EYE,
						Prayers.MYSTIC_LORE, Prayers.ULTIMATE_STRENGTH, Prayers.INCREDIBLE_REFLEXES, Prayers.EAGLE_EYE,
						Prayers.MYSTIC_MIGHT, Prayers.CHIVALRY, Prayers.PIETY, Prayers.AUGURY, Prayers.RIGOUR };
				break;
			case Prayers.ROCK_SKIN:
				deactivatePrayers = new int[] { Prayers.THICK_SKIN, Prayers.STEEL_SKIN, Prayers.CHIVALRY, Prayers.PIETY,
						Prayers.AUGURY, Prayers.RIGOUR };
				break;
			case Prayers.SUPERHUMAN_STRENGTH:
				deactivatePrayers = new int[] { Prayers.SHARP_EYE, Prayers.MYSTIC_WILL, Prayers.BURST_OF_STRENGTH,
						Prayers.HAWK_EYE, Prayers.MYSTIC_LORE, Prayers.ULTIMATE_STRENGTH, Prayers.EAGLE_EYE,
						Prayers.MYSTIC_MIGHT, Prayers.CHIVALRY, Prayers.PIETY, Prayers.AUGURY, Prayers.RIGOUR };
				break;
			case Prayers.IMPROVED_REFLEXES:
				deactivatePrayers = new int[] { Prayers.SHARP_EYE, Prayers.MYSTIC_WILL, Prayers.CLARITY_OF_THOUGHT,
						Prayers.HAWK_EYE, Prayers.MYSTIC_LORE, Prayers.INCREDIBLE_REFLEXES, Prayers.EAGLE_EYE,
						Prayers.MYSTIC_MIGHT, Prayers.CHIVALRY, Prayers.PIETY, Prayers.AUGURY, Prayers.RIGOUR };
				break;
			case Prayers.HAWK_EYE:
				deactivatePrayers = new int[] { Prayers.BURST_OF_STRENGTH, Prayers.CLARITY_OF_THOUGHT,
						Prayers.MYSTIC_WILL, Prayers.SUPERHUMAN_STRENGTH, Prayers.IMPROVED_REFLEXES, Prayers.SHARP_EYE,
						Prayers.MYSTIC_LORE, Prayers.ULTIMATE_STRENGTH, Prayers.INCREDIBLE_REFLEXES, Prayers.EAGLE_EYE,
						Prayers.MYSTIC_MIGHT, Prayers.CHIVALRY, Prayers.PIETY, Prayers.AUGURY, Prayers.RIGOUR };
				break;
			case Prayers.MYSTIC_LORE:
				deactivatePrayers = new int[] { Prayers.BURST_OF_STRENGTH, Prayers.CLARITY_OF_THOUGHT,
						Prayers.SHARP_EYE, Prayers.SUPERHUMAN_STRENGTH, Prayers.IMPROVED_REFLEXES, Prayers.HAWK_EYE,
						Prayers.MYSTIC_WILL, Prayers.ULTIMATE_STRENGTH, Prayers.INCREDIBLE_REFLEXES, Prayers.EAGLE_EYE,
						Prayers.MYSTIC_MIGHT, Prayers.CHIVALRY, Prayers.PIETY, Prayers.AUGURY, Prayers.RIGOUR };
				break;
			case Prayers.STEEL_SKIN:
				deactivatePrayers = new int[] { Prayers.THICK_SKIN, Prayers.ROCK_SKIN, Prayers.CHIVALRY, Prayers.PIETY,
						Prayers.AUGURY, Prayers.RIGOUR };
				break;
			case Prayers.ULTIMATE_STRENGTH:
				deactivatePrayers = new int[] { Prayers.SHARP_EYE, Prayers.MYSTIC_WILL, Prayers.BURST_OF_STRENGTH,
						Prayers.HAWK_EYE, Prayers.MYSTIC_LORE, Prayers.SUPERHUMAN_STRENGTH, Prayers.EAGLE_EYE,
						Prayers.MYSTIC_MIGHT, Prayers.CHIVALRY, Prayers.PIETY, Prayers.AUGURY, Prayers.RIGOUR };
				break;
			case Prayers.INCREDIBLE_REFLEXES:
				deactivatePrayers = new int[] { Prayers.SHARP_EYE, Prayers.MYSTIC_WILL, Prayers.CLARITY_OF_THOUGHT,
						Prayers.HAWK_EYE, Prayers.MYSTIC_LORE, Prayers.IMPROVED_REFLEXES, Prayers.EAGLE_EYE,
						Prayers.MYSTIC_MIGHT, Prayers.CHIVALRY, Prayers.PIETY, Prayers.AUGURY, Prayers.RIGOUR };
				break;
			case Prayers.EAGLE_EYE:
				deactivatePrayers = new int[] { Prayers.BURST_OF_STRENGTH, Prayers.CLARITY_OF_THOUGHT,
						Prayers.MYSTIC_WILL, Prayers.SUPERHUMAN_STRENGTH, Prayers.IMPROVED_REFLEXES, Prayers.SHARP_EYE,
						Prayers.MYSTIC_LORE, Prayers.ULTIMATE_STRENGTH, Prayers.INCREDIBLE_REFLEXES, Prayers.HAWK_EYE,
						Prayers.MYSTIC_MIGHT, Prayers.CHIVALRY, Prayers.PIETY, Prayers.AUGURY, Prayers.RIGOUR };
				break;
			case Prayers.MYSTIC_MIGHT:
				deactivatePrayers = new int[] { Prayers.BURST_OF_STRENGTH, Prayers.CLARITY_OF_THOUGHT,
						Prayers.SHARP_EYE, Prayers.SUPERHUMAN_STRENGTH, Prayers.IMPROVED_REFLEXES, Prayers.HAWK_EYE,
						Prayers.MYSTIC_WILL, Prayers.ULTIMATE_STRENGTH, Prayers.INCREDIBLE_REFLEXES, Prayers.EAGLE_EYE,
						Prayers.MYSTIC_LORE, Prayers.CHIVALRY, Prayers.PIETY, Prayers.AUGURY, Prayers.RIGOUR };
				break;
			case Prayers.CHIVALRY:
				deactivatePrayers = new int[] { Prayers.THICK_SKIN, Prayers.BURST_OF_STRENGTH,
						Prayers.CLARITY_OF_THOUGHT, Prayers.SHARP_EYE, Prayers.MYSTIC_WILL, Prayers.ROCK_SKIN,
						Prayers.SUPERHUMAN_STRENGTH, Prayers.IMPROVED_REFLEXES, Prayers.HAWK_EYE, Prayers.MYSTIC_LORE,
						Prayers.STEEL_SKIN, Prayers.ULTIMATE_STRENGTH, Prayers.INCREDIBLE_REFLEXES, Prayers.EAGLE_EYE,
						Prayers.MYSTIC_MIGHT, Prayers.PIETY, Prayers.AUGURY, Prayers.RIGOUR };
				break;
			case Prayers.PIETY:
				deactivatePrayers = new int[] { Prayers.THICK_SKIN, Prayers.BURST_OF_STRENGTH,
						Prayers.CLARITY_OF_THOUGHT, Prayers.SHARP_EYE, Prayers.MYSTIC_WILL, Prayers.ROCK_SKIN,
						Prayers.SUPERHUMAN_STRENGTH, Prayers.IMPROVED_REFLEXES, Prayers.HAWK_EYE, Prayers.MYSTIC_LORE,
						Prayers.STEEL_SKIN, Prayers.ULTIMATE_STRENGTH, Prayers.INCREDIBLE_REFLEXES, Prayers.EAGLE_EYE,
						Prayers.MYSTIC_MIGHT, Prayers.CHIVALRY, Prayers.RIGOUR, Prayers.AUGURY };
				break;
			case Prayers.RIGOUR:
				deactivatePrayers = new int[] { Prayers.THICK_SKIN, Prayers.BURST_OF_STRENGTH,
						Prayers.CLARITY_OF_THOUGHT, Prayers.SHARP_EYE, Prayers.MYSTIC_WILL, Prayers.ROCK_SKIN,
						Prayers.SUPERHUMAN_STRENGTH, Prayers.IMPROVED_REFLEXES, Prayers.HAWK_EYE, Prayers.MYSTIC_LORE,
						Prayers.STEEL_SKIN, Prayers.ULTIMATE_STRENGTH, Prayers.INCREDIBLE_REFLEXES, Prayers.EAGLE_EYE,
						Prayers.MYSTIC_MIGHT, Prayers.CHIVALRY, Prayers.PIETY, Prayers.AUGURY };
				break;
			case Prayers.AUGURY:
				deactivatePrayers = new int[] { Prayers.THICK_SKIN, Prayers.BURST_OF_STRENGTH,
						Prayers.CLARITY_OF_THOUGHT, Prayers.SHARP_EYE, Prayers.MYSTIC_WILL, Prayers.ROCK_SKIN,
						Prayers.SUPERHUMAN_STRENGTH, Prayers.IMPROVED_REFLEXES, Prayers.HAWK_EYE, Prayers.MYSTIC_LORE,
						Prayers.STEEL_SKIN, Prayers.ULTIMATE_STRENGTH, Prayers.INCREDIBLE_REFLEXES, Prayers.EAGLE_EYE,
						Prayers.MYSTIC_MIGHT, Prayers.CHIVALRY, Prayers.PIETY, Prayers.RIGOUR };
				break;
			case Prayers.PROTECT_FROM_MAGIC:
				deactivatePrayers = new int[] { Prayers.PROTECT_FROM_MISSILES, Prayers.PROTECT_FROM_MELEE,
						Prayers.RETRIBUTION, Prayers.REDEMPTION, Prayers.SMITE };
				break;
			case Prayers.PROTECT_FROM_MISSILES:
				deactivatePrayers = new int[] { Prayers.PROTECT_FROM_MAGIC, Prayers.PROTECT_FROM_MELEE,
						Prayers.RETRIBUTION, Prayers.REDEMPTION, Prayers.SMITE };
				break;
			case Prayers.PROTECT_FROM_MELEE:
				deactivatePrayers = new int[] { Prayers.PROTECT_FROM_MAGIC, Prayers.PROTECT_FROM_MISSILES,
						Prayers.RETRIBUTION, Prayers.REDEMPTION, Prayers.SMITE };
				break;
			case Prayers.RETRIBUTION:
				deactivatePrayers = new int[] { Prayers.PROTECT_FROM_MAGIC, Prayers.PROTECT_FROM_MISSILES,
						Prayers.PROTECT_FROM_MELEE, Prayers.REDEMPTION, Prayers.SMITE };
				break;
			case Prayers.REDEMPTION:
				deactivatePrayers = new int[] { Prayers.PROTECT_FROM_MAGIC, Prayers.PROTECT_FROM_MISSILES,
						Prayers.PROTECT_FROM_MELEE, Prayers.RETRIBUTION, Prayers.SMITE };
				break;
			case Prayers.SMITE:
				deactivatePrayers = new int[] { Prayers.PROTECT_FROM_MAGIC, Prayers.PROTECT_FROM_MISSILES,
						Prayers.PROTECT_FROM_MELEE, Prayers.RETRIBUTION, Prayers.REDEMPTION };
				break;
			}
			for (int i : deactivatePrayers) {
				if (i != childButton)
					deActivateQuickPrayer(player, i);
			}
			refreshQuickPrayers(player);
		} 
	}

	/**
	 * Prayer tab child buttons
	 */
	public static final int THICK_SKIN = 4, BURST_OF_STRENGTH = 5, CLARITY_OF_THOUGHT = 6, SHARP_EYE = 22,
			MYSTIC_WILL = 23, ROCK_SKIN = 7, SUPERHUMAN_STRENGTH = 8, IMPROVED_REFLEXES = 9, RAPID_RESTORE = 10,
			RAPID_HEAL = 11, PROTECT_ITEM = 12, HAWK_EYE = 24, MYSTIC_LORE = 25, STEEL_SKIN = 13,
			ULTIMATE_STRENGTH = 14, INCREDIBLE_REFLEXES = 15, PROTECT_FROM_MAGIC = 16, PROTECT_FROM_MISSILES = 17,
			PROTECT_FROM_MELEE = 18, EAGLE_EYE = 26, MYSTIC_MIGHT = 27, RETRIBUTION = 19, REDEMPTION = 20, SMITE = 21,
			PRESERVE = 32, CHIVALRY = 28, PIETY = 29, RIGOUR = 30, AUGURY = 31;

	public static void activatePrayer(Player player, int id) {
		if (player.getCombatState().isDead() || player.getSkills().getPrayerPoints() < 1
				|| player.hasAttribute("noProtectionPrayer")) {
			refresh(player);
			return;
		}
		if (player.getRFD().isStarted()) {
			player.getActionSender().sendMessage("You can't use prayers in this fight.");
			refresh(player);
			return;
		}
		ActionSender action = player.getActionSender();
		Prayer prayer = Prayer.forId(id);
		action.removeAllInterfaces().removeInterface2().removeInventoryInterface();
		if (player.getSkills().getLevelForExperience(Skills.PRAYER) < prayer.getLevelRequired()) {
			action.sendMessage("You need a Prayer level of " + prayer.getLevelRequired() + " to use that.");
			return;
		}
		if (id == Prayers.PIETY && player.getSkills().getLevelForExperience(Skills.DEFENCE) < 70) {
			action.sendMessage("You need a defence level of 70 to use that.");
			return;
		}

		if (id == Prayers.RIGOUR) {
			if (!player.getDatabaseEntity().getStatistics().hasUnlockedRigour()) {
				action.sendMessage("You need to unlock this prayer with a dexterous prayer scroll.");
				refresh(player);
				return;
			}
			if (player.getSkills().getLevelForExperience(Skills.DEFENCE) < 70) {
				action.sendMessage("You need a defence level of 70 to use that.");
				return;
			}
		}
		if (id == Prayers.PRESERVE && !player.getDatabaseEntity().getStatistics().hasUnlockedPreserve()) {
			action.sendMessage("You need to unlock this prayer with a torn prayer scroll.");
			refresh(player);
			return;
		}
		if (id == Prayers.AUGURY) {
			if (!player.getDatabaseEntity().getStatistics().hasUnlockedAugury()) {
				action.sendMessage("You need to unlock this prayer with an arcane prayer scroll.");
				refresh(player);
				return;
			}
			if (player.getSkills().getLevelForExperience(Skills.DEFENCE) < 70) {
				action.sendMessage("You need a defence level of 70 to use that.");
				return;
			}
		}
		if (id == Prayers.CHIVALRY && player.getSkills().getLevelForExperience(Skills.DEFENCE) < 60) {
			action.sendMessage("You need a defence level of 60 to use that.");
			return;
		}
		player.getCombatState().setPrayer(id, !player.getCombatState().getPrayer(id));
		int[] deactivatePrayers = new int[0];
		if (player.getCombatState().getPrayer(id)) {
			if (player.getPrayerUpdateTick() == null) {
				player.setPrayerUpdateTick(new PrayerUpdateTick(player));
				World.getWorld().submit(player.getPrayerUpdateTick());
			}
			switch (id) {
			case Prayers.THICK_SKIN:
				deactivatePrayers = new int[] { Prayers.ROCK_SKIN, Prayers.STEEL_SKIN, Prayers.CHIVALRY, Prayers.PIETY,
						Prayers.AUGURY, Prayers.RIGOUR };
				break;
			case Prayers.BURST_OF_STRENGTH:
				deactivatePrayers = new int[] { Prayers.SHARP_EYE, Prayers.MYSTIC_WILL, Prayers.SUPERHUMAN_STRENGTH,
						Prayers.HAWK_EYE, Prayers.MYSTIC_LORE, Prayers.ULTIMATE_STRENGTH, Prayers.EAGLE_EYE,
						Prayers.MYSTIC_MIGHT, Prayers.CHIVALRY, Prayers.PIETY, Prayers.AUGURY, Prayers.RIGOUR };
				break;
			case Prayers.CLARITY_OF_THOUGHT:
				deactivatePrayers = new int[] { Prayers.SHARP_EYE, Prayers.MYSTIC_WILL, Prayers.IMPROVED_REFLEXES,
						Prayers.HAWK_EYE, Prayers.MYSTIC_LORE, Prayers.INCREDIBLE_REFLEXES, Prayers.EAGLE_EYE,
						Prayers.MYSTIC_MIGHT, Prayers.CHIVALRY, Prayers.PIETY, Prayers.AUGURY, Prayers.RIGOUR };
				break;
			case Prayers.SHARP_EYE:
				deactivatePrayers = new int[] { Prayers.BURST_OF_STRENGTH, Prayers.CLARITY_OF_THOUGHT,
						Prayers.MYSTIC_WILL, Prayers.SUPERHUMAN_STRENGTH, Prayers.IMPROVED_REFLEXES, Prayers.HAWK_EYE,
						Prayers.MYSTIC_LORE, Prayers.ULTIMATE_STRENGTH, Prayers.INCREDIBLE_REFLEXES, Prayers.EAGLE_EYE,
						Prayers.MYSTIC_MIGHT, Prayers.CHIVALRY, Prayers.PIETY, Prayers.AUGURY, Prayers.RIGOUR };
				break;
			case Prayers.MYSTIC_WILL:
				deactivatePrayers = new int[] { Prayers.BURST_OF_STRENGTH, Prayers.CLARITY_OF_THOUGHT,
						Prayers.SHARP_EYE, Prayers.SUPERHUMAN_STRENGTH, Prayers.IMPROVED_REFLEXES, Prayers.HAWK_EYE,
						Prayers.MYSTIC_LORE, Prayers.ULTIMATE_STRENGTH, Prayers.INCREDIBLE_REFLEXES, Prayers.EAGLE_EYE,
						Prayers.MYSTIC_MIGHT, Prayers.CHIVALRY, Prayers.PIETY, Prayers.AUGURY, Prayers.RIGOUR };
				break;
			case Prayers.ROCK_SKIN:
				deactivatePrayers = new int[] { Prayers.THICK_SKIN, Prayers.STEEL_SKIN, Prayers.CHIVALRY, Prayers.PIETY,
						Prayers.AUGURY, Prayers.RIGOUR };
				break;
			case Prayers.SUPERHUMAN_STRENGTH:
				deactivatePrayers = new int[] { Prayers.SHARP_EYE, Prayers.MYSTIC_WILL, Prayers.BURST_OF_STRENGTH,
						Prayers.HAWK_EYE, Prayers.MYSTIC_LORE, Prayers.ULTIMATE_STRENGTH, Prayers.EAGLE_EYE,
						Prayers.MYSTIC_MIGHT, Prayers.CHIVALRY, Prayers.PIETY, Prayers.AUGURY, Prayers.RIGOUR };
				break;
			case Prayers.IMPROVED_REFLEXES:
				deactivatePrayers = new int[] { Prayers.SHARP_EYE, Prayers.MYSTIC_WILL, Prayers.CLARITY_OF_THOUGHT,
						Prayers.HAWK_EYE, Prayers.MYSTIC_LORE, Prayers.INCREDIBLE_REFLEXES, Prayers.EAGLE_EYE,
						Prayers.MYSTIC_MIGHT, Prayers.CHIVALRY, Prayers.PIETY, Prayers.AUGURY, Prayers.RIGOUR };
				break;
			case Prayers.HAWK_EYE:
				deactivatePrayers = new int[] { Prayers.BURST_OF_STRENGTH, Prayers.CLARITY_OF_THOUGHT,
						Prayers.MYSTIC_WILL, Prayers.SUPERHUMAN_STRENGTH, Prayers.IMPROVED_REFLEXES, Prayers.SHARP_EYE,
						Prayers.MYSTIC_LORE, Prayers.ULTIMATE_STRENGTH, Prayers.INCREDIBLE_REFLEXES, Prayers.EAGLE_EYE,
						Prayers.MYSTIC_MIGHT, Prayers.CHIVALRY, Prayers.PIETY, Prayers.AUGURY, Prayers.RIGOUR };
				break;
			case Prayers.MYSTIC_LORE:
				deactivatePrayers = new int[] { Prayers.BURST_OF_STRENGTH, Prayers.CLARITY_OF_THOUGHT,
						Prayers.SHARP_EYE, Prayers.SUPERHUMAN_STRENGTH, Prayers.IMPROVED_REFLEXES, Prayers.HAWK_EYE,
						Prayers.MYSTIC_WILL, Prayers.ULTIMATE_STRENGTH, Prayers.INCREDIBLE_REFLEXES, Prayers.EAGLE_EYE,
						Prayers.MYSTIC_MIGHT, Prayers.CHIVALRY, Prayers.PIETY, Prayers.AUGURY, Prayers.RIGOUR };
				break;
			case Prayers.STEEL_SKIN:
				deactivatePrayers = new int[] { Prayers.THICK_SKIN, Prayers.ROCK_SKIN, Prayers.CHIVALRY, Prayers.PIETY,
						Prayers.AUGURY, Prayers.RIGOUR };
				break;
			case Prayers.ULTIMATE_STRENGTH:
				deactivatePrayers = new int[] { Prayers.SHARP_EYE, Prayers.MYSTIC_WILL, Prayers.BURST_OF_STRENGTH,
						Prayers.HAWK_EYE, Prayers.MYSTIC_LORE, Prayers.SUPERHUMAN_STRENGTH, Prayers.EAGLE_EYE,
						Prayers.MYSTIC_MIGHT, Prayers.CHIVALRY, Prayers.PIETY, Prayers.AUGURY, Prayers.RIGOUR };
				break;
			case Prayers.INCREDIBLE_REFLEXES:
				deactivatePrayers = new int[] { Prayers.SHARP_EYE, Prayers.MYSTIC_WILL, Prayers.CLARITY_OF_THOUGHT,
						Prayers.HAWK_EYE, Prayers.MYSTIC_LORE, Prayers.IMPROVED_REFLEXES, Prayers.EAGLE_EYE,
						Prayers.MYSTIC_MIGHT, Prayers.CHIVALRY, Prayers.PIETY, Prayers.AUGURY, Prayers.RIGOUR };
				break;
			case Prayers.EAGLE_EYE:
				deactivatePrayers = new int[] { Prayers.BURST_OF_STRENGTH, Prayers.CLARITY_OF_THOUGHT,
						Prayers.MYSTIC_WILL, Prayers.SUPERHUMAN_STRENGTH, Prayers.IMPROVED_REFLEXES, Prayers.SHARP_EYE,
						Prayers.MYSTIC_LORE, Prayers.ULTIMATE_STRENGTH, Prayers.INCREDIBLE_REFLEXES, Prayers.HAWK_EYE,
						Prayers.MYSTIC_MIGHT, Prayers.CHIVALRY, Prayers.PIETY, Prayers.AUGURY, Prayers.RIGOUR };
				break;
			case Prayers.MYSTIC_MIGHT:
				deactivatePrayers = new int[] { Prayers.BURST_OF_STRENGTH, Prayers.CLARITY_OF_THOUGHT,
						Prayers.SHARP_EYE, Prayers.SUPERHUMAN_STRENGTH, Prayers.IMPROVED_REFLEXES, Prayers.HAWK_EYE,
						Prayers.MYSTIC_WILL, Prayers.ULTIMATE_STRENGTH, Prayers.INCREDIBLE_REFLEXES, Prayers.EAGLE_EYE,
						Prayers.MYSTIC_LORE, Prayers.CHIVALRY, Prayers.PIETY, Prayers.AUGURY, Prayers.RIGOUR };
				break;
			case Prayers.CHIVALRY:
				deactivatePrayers = new int[] { Prayers.THICK_SKIN, Prayers.BURST_OF_STRENGTH,
						Prayers.CLARITY_OF_THOUGHT, Prayers.SHARP_EYE, Prayers.MYSTIC_WILL, Prayers.ROCK_SKIN,
						Prayers.SUPERHUMAN_STRENGTH, Prayers.IMPROVED_REFLEXES, Prayers.HAWK_EYE, Prayers.MYSTIC_LORE,
						Prayers.STEEL_SKIN, Prayers.ULTIMATE_STRENGTH, Prayers.INCREDIBLE_REFLEXES, Prayers.EAGLE_EYE,
						Prayers.MYSTIC_MIGHT, Prayers.PIETY, Prayers.AUGURY, Prayers.RIGOUR };
				break;
			case Prayers.PIETY:
				deactivatePrayers = new int[] { Prayers.THICK_SKIN, Prayers.BURST_OF_STRENGTH,
						Prayers.CLARITY_OF_THOUGHT, Prayers.SHARP_EYE, Prayers.MYSTIC_WILL, Prayers.ROCK_SKIN,
						Prayers.SUPERHUMAN_STRENGTH, Prayers.IMPROVED_REFLEXES, Prayers.HAWK_EYE, Prayers.MYSTIC_LORE,
						Prayers.STEEL_SKIN, Prayers.ULTIMATE_STRENGTH, Prayers.INCREDIBLE_REFLEXES, Prayers.EAGLE_EYE,
						Prayers.MYSTIC_MIGHT, Prayers.CHIVALRY, Prayers.RIGOUR, Prayers.AUGURY };
				break;
			case Prayers.RIGOUR:
				deactivatePrayers = new int[] { Prayers.THICK_SKIN, Prayers.BURST_OF_STRENGTH,
						Prayers.CLARITY_OF_THOUGHT, Prayers.SHARP_EYE, Prayers.MYSTIC_WILL, Prayers.ROCK_SKIN,
						Prayers.SUPERHUMAN_STRENGTH, Prayers.IMPROVED_REFLEXES, Prayers.HAWK_EYE, Prayers.MYSTIC_LORE,
						Prayers.STEEL_SKIN, Prayers.ULTIMATE_STRENGTH, Prayers.INCREDIBLE_REFLEXES, Prayers.EAGLE_EYE,
						Prayers.MYSTIC_MIGHT, Prayers.CHIVALRY, Prayers.PIETY, Prayers.AUGURY };
				break;
			case Prayers.AUGURY:
				deactivatePrayers = new int[] { Prayers.THICK_SKIN, Prayers.BURST_OF_STRENGTH,
						Prayers.CLARITY_OF_THOUGHT, Prayers.SHARP_EYE, Prayers.MYSTIC_WILL, Prayers.ROCK_SKIN,
						Prayers.SUPERHUMAN_STRENGTH, Prayers.IMPROVED_REFLEXES, Prayers.HAWK_EYE, Prayers.MYSTIC_LORE,
						Prayers.STEEL_SKIN, Prayers.ULTIMATE_STRENGTH, Prayers.INCREDIBLE_REFLEXES, Prayers.EAGLE_EYE,
						Prayers.MYSTIC_MIGHT, Prayers.CHIVALRY, Prayers.PIETY, Prayers.RIGOUR };
				break;
			case Prayers.PROTECT_FROM_MAGIC:
				player.getCombatState().setPrayerHeadIcon(prayer.getHeadIcon());
				deactivatePrayers = new int[] { Prayers.PROTECT_FROM_MISSILES, Prayers.PROTECT_FROM_MELEE,
						Prayers.RETRIBUTION, Prayers.REDEMPTION, Prayers.SMITE };
				break;
			case Prayers.PROTECT_FROM_MISSILES:
				player.getCombatState().setPrayerHeadIcon(prayer.getHeadIcon());
				deactivatePrayers = new int[] { Prayers.PROTECT_FROM_MAGIC, Prayers.PROTECT_FROM_MELEE,
						Prayers.RETRIBUTION, Prayers.REDEMPTION, Prayers.SMITE };
				break;
			case Prayers.PROTECT_FROM_MELEE:
				player.getCombatState().setPrayerHeadIcon(prayer.getHeadIcon());
				deactivatePrayers = new int[] { Prayers.PROTECT_FROM_MAGIC, Prayers.PROTECT_FROM_MISSILES,
						Prayers.RETRIBUTION, Prayers.REDEMPTION, Prayers.SMITE };
				break;
			case Prayers.RETRIBUTION:
				player.getCombatState().setPrayerHeadIcon(prayer.getHeadIcon());
				deactivatePrayers = new int[] { Prayers.PROTECT_FROM_MAGIC, Prayers.PROTECT_FROM_MISSILES,
						Prayers.PROTECT_FROM_MELEE, Prayers.REDEMPTION, Prayers.SMITE };
				break;
			case Prayers.REDEMPTION:
				player.getCombatState().setPrayerHeadIcon(prayer.getHeadIcon());
				deactivatePrayers = new int[] { Prayers.PROTECT_FROM_MAGIC, Prayers.PROTECT_FROM_MISSILES,
						Prayers.PROTECT_FROM_MELEE, Prayers.RETRIBUTION, Prayers.SMITE };
				break;
			case Prayers.SMITE:
				player.getCombatState().setPrayerHeadIcon(prayer.getHeadIcon());
				deactivatePrayers = new int[] { Prayers.PROTECT_FROM_MAGIC, Prayers.PROTECT_FROM_MISSILES,
						Prayers.PROTECT_FROM_MELEE, Prayers.RETRIBUTION, Prayers.REDEMPTION };
				break;
			}
			for (int i : deactivatePrayers) {
				if (i != id)
					deActivatePrayer(player, i);
			}
			refresh(player);
		} else {
			switch (id) {
			case Prayers.PROTECT_FROM_MAGIC:
			case Prayers.PROTECT_FROM_MISSILES:
			case Prayers.PROTECT_FROM_MELEE:
			case Prayers.RETRIBUTION:
			case Prayers.REDEMPTION:
			case Prayers.SMITE:
				player.getCombatState().setPrayerHeadIcon(-1);
				break;
			}
			boolean prayersFound = false;
			for (int i = 0; i < player.getCombatState().getPrayers().length; i++) {
				if (player.getCombatState().getPrayer(i)) {
					prayersFound = true;
					break;
				}
			}
			if (!prayersFound) {
				if (player.getPrayerUpdateTick() != null) {
					player.getPrayerUpdateTick().stop();
					player.setPrayerUpdateTick(null);
				}
			}
			refresh(player);
		}
	}

	public static void refresh(Player player) {
		int config = 0;
		for (int i = 0; i < player.getCombatState().getPrayers().length; i++) {
			if (player.getCombatState().getPrayers()[i]) {
				Prayer prayer = Prayer.forId(i);
				config |= prayer.getConfig();
			}
		}
		player.getActionSender().sendConfig(83, config);
	}

	public static void refreshQuickPrayers(Player player) {
		int config = 0;
		for (int i = 0; i < player.getCombatState().getQuickPrayers().length; i++) {
			if (player.getCombatState().getQuickPrayers()[i]) {
				Prayer prayer = Prayer.forId(i);
				config |= prayer.getConfig();
			}
		}
		player.getActionSender().sendConfig(84, config);
	}

	public static void deActivatePrayer(Player player, int id) {
		player.getCombatState().setPrayer(id, false);
	}

	public static void deActivateQuickPrayer(Player player, int id) {
		player.getCombatState().setQuickPrayer(id, false);
	}
}
