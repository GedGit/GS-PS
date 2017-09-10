package org.rs2server.rs2.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Consumable items (food/drink)
 * 
 * @author Michael Bull
 *
 */
public class Consumables {

	public static enum Food {

		JANGERBERRIES(247, 2), // Jangerberries

		SHRIMPS(315, 3), // Shrimps

		ANCHOVIES(319, 1), // Anchovies

		SARDINE(325, 4), // Sardine

		SALMON(329, 9), // Salmon

		TROUT(333, 7), // Trout

		GIANT_CARP(337, 6), // Giant carp

		COD(339, 7), // Cod

		HERRING(347, 5), // Herring

		PIKE(351, 8), // Pike

		MACKEREL(355, 6), // Mackerel

		TUNA(361, 10), // Tuna

		BASS(365, 13), // Bass

		SWORDFISH(373, 14), // Swordfish

		LOBSTER(379, 12), // Lobster

		MONKFISH(7946, 16), // Monkfish

		SHARK(385, 20), // Shark

		MANTA_RAY(391, 22), // Manta ray

		DARK_CRAB(11936, 22), // Dark Crab

		SEA_TURTLE(397, 21), // Sea turtle

		ANGLERFISH(13441, 22), // Anglerfish

		EDIBLE_SEAWEED(403, 4), // Edible seaweed

		UGTHANKI_MEAT(1861, 3), // Ugthanki meat

		CHOPPED_TOMATO(1869, 2), // Chopped tomato

		CAKE(1891, 4, 1893), // Cake

		KARAMBWAN(3144, 18), // Karambwans

		TWO_THIRDS_OF_CAKE(1893, 4, 1895), // 2/3 cake

		SLICE_OF_CAKE(1895, 4), // Slice of cake

		CHOCOLATE_CAKE(1897, 5, 1899), // Chocolate cake

		TWO_THIRDS_OF_CHOCOLATE_CAKE(1899, 5, 1901), // 2/3 chocolate cake

		CHOCOLATE_SLICE(1901, 5), // Chocolate slice

		POTATO(1942, 1), // Potato

		ONION(1957, 1), // Onion

		PUMPKIN(1959, 14), // Pumpkin

		EASTER_EGG(1961, 12), // Easter egg

		BANNANA(1963, 2), // Banana

		CABBAGE(1965, 1), // Cabbage

		CABBAGE_2(1967, 2), // Cabbage

		SPINACH_ROLL(1969, 2), // Spinach roll

		CHOCOLATE_BAR(1973, 3), // Chocolate bar

		TOMATO(1982, 2), // Tomato

		CHEESE(1985, 2), // Cheese

		STEW(2003, 11), // Stew

		CURRY(2011, 19), // Curry

		LEMON(2102, 2), // Lemon

		LEMON_CHUNKS(2104, 1), // Lemon chunks

		LEMON_SLICES(2106, 1), // Lemon slices

		ORANGE(2108, 2), // Orange

		ORANGE_CHUNKS(2110, 1), // Orange chunks

		ORANGE_SLICES(2112, 1), // Orange slices

		PINEAPPLE_CHUNKS(2116, 2), // Pineapple chunks

		PINEAPPLE_RING(2118, 2), // Pineapple ring

		LIME(2120, 2), // Lime

		LIME_CHUNKS(2122, 2), // Lime chunks

		LIME_SLICES(2124, 2), // Lime slices

		DWELLBERRIES(2126, 2), // Dwellberries

		EQUA_LEAVES(2128, 1), // Equa leaves

		COOKED_CHICKEN(2140, 3), // Cooked chicken

		COOKED_MEAT(2142, 3), // Cooked meat

		LAVA_EEL(2149, 11), // Lava eel

		TOADS_LEGS(2152, 3), // Toad's legs

		PLAIN_PIZZA(2289, 7, 2291), // Plain pizza

		HALF_OF_PLAIN_PIZZA(2291, 7), // 1/2 plain pizza

		MEAT_PIZZA(2293, 8, 2295), // Meat pizza

		HALF_OF_MEAT_PIZZA(2295, 8), // 1/2 meat pizza

		ANCHOVY_PIZZA(2297, 9, 2299), // Anchovy pizza

		HALF_OF_ANCHOVY_PIZZA(2299, 9), // 1/2 anchovy pizza

		PINEAPPLE_PIZZA(2301, 11, 2303), // Pineapple pizza

		HALF_OF_PINEAPPLE_PIZZA(2303, 11), // 1/2 p'apple pizza

		BREAD(2309, 5), // Bread

		APPLE_PIE(2323, 7, 2335), // Apple pie

		REDBERRY_PIE(2325, 5, 2333), // Redberry pie

		MEAT_PIE(2327, 6, 2331), // Meat pie

		HALF_OF_MEAT_PIE(2331, 6), // Half a meat pie

		HALF_OF_REDBERRY_PIE(2333, 5), // Half a redberry pie

		HALF_OF_APPLE_PIE(2335, 7), // Half an apple pie

		COOKED_CHOMPY(2878, 10), // Cooked chompy

		POTATO_WITH_CHEESE(6705, 16), // Potato with cheese

		TUNA_POTATO(7060, 22), // Tuna potato

		CHILLI_POTATO(7054, 14),

		MUSHROOM_POTATO(7058, 20),

		PURPLE_SWEETS(4561, 1),;

		/**
		 * A map of food Ids.
		 */
		private static Map<Integer, Food> food = new HashMap<Integer, Food>();

		/**
		 * Gets a food by its item ID.
		 * 
		 * @param foodId
		 *            The food item id.
		 * @return The Food, or <code>null</code> if the id is not a food.
		 */
		public static Food forId(int foodId) {
			return food.get(foodId);
		}

		/**
		 * Populates the food map.
		 */
		static {
			for (Food foodE : Food.values()) {
				food.put(foodE.id, foodE);
			}
		}

		/**
		 * The food item id.
		 */
		private int id;

		/**
		 * The amount of health this food heals.
		 */
		private int heal;

		/**
		 * The new item id to add.
		 */
		private int newId;

		/**
		 * Creates the food.
		 * 
		 * @param id
		 *            The food item id.
		 * @param heal
		 *            The amount of health this food heals.
		 * @param newId
		 *            The item id of the new item to add.
		 */
		private Food(int id, int heal, int newId) {
			this.id = id;
			this.heal = heal;
			this.newId = newId;
		}

		/**
		 * Creates the food.
		 * 
		 * @param id
		 *            The food item id.
		 * @param heal
		 *            The amount of health this food heals.
		 */
		private Food(int id, int heal) {
			this.id = id;
			this.heal = heal;
			this.newId = -1;
		}

		/**
		 * Gets the food item id.
		 * 
		 * @return The food item id.
		 */
		public int getId() {
			return id;
		}

		/**
		 * Gets the amount of health this food heals.
		 * 
		 * @return The amount of health this food heals.
		 */
		public int getHeal() {
			return heal;
		}

		/**
		 * Gets the new item id.
		 * 
		 * @return The new item id.
		 */
		public int getNewId() {
			return newId;
		}
	}

	/**
	 * All drinkable items. NOTE: For item IDs, you must go potion dose 1, dose
	 * 2, dose 3, dose 4. EG: Attack_Potion(1)[125][2428], Attack_Potion(2),
	 * Attack_Potion(3)[121][123], Attack_Potion(4)
	 * 
	 * @author Michael Bull
	 *
	 */
	public static enum Drink {

		ATTACK_POTION(new int[] { 125, 123, 121, 2428 }, new int[] { Skills.ATTACK }, PotionType.NORMAL_POTION),

		STRENGTH_POTION(new int[] { 119, 117, 115, 113 }, new int[] { Skills.STRENGTH }, PotionType.NORMAL_POTION),

		DEFENCE_POTION(new int[] { 137, 135, 133, 2432 }, new int[] { Skills.DEFENCE }, PotionType.NORMAL_POTION),

		RANGE_POTION(new int[] { 173, 171, 169, 2444 }, new int[] { Skills.RANGE }, PotionType.NORMAL_POTION),

		MAGIC_POTION(new int[] { 3046, 3044, 3042, 3040 }, new int[] { Skills.MAGIC }, PotionType.PLUS_5),

		RESTORE_POTION(new int[] { 131, 129, 127, 2430 },
				new int[] { Skills.DEFENCE, Skills.ATTACK, Skills.STRENGTH, Skills.MAGIC, Skills.RANGE },
				PotionType.RESTORE),

		SUPER_RESTORE_POTION(new int[] { 3030, 3028, 3026, 3024 },
				new int[] { Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE, Skills.MAGIC, Skills.RANGE, Skills.PRAYER,
						Skills.AGILITY, Skills.COOKING, Skills.CRAFTING, Skills.FARMING, Skills.FIREMAKING,
						Skills.FISHING, Skills.FLETCHING, Skills.HERBLORE, Skills.MINING, Skills.RUNECRAFTING,
						Skills.SLAYER, Skills.SMITHING, Skills.THIEVING, Skills.WOODCUTTING },
				PotionType.SUPER_RESTORE),

		PRAYER_POTION(new int[] { 143, 141, 139, 2434 }, new int[] { Skills.PRAYER }, PotionType.PRAYER_POTION),

		SUPER_ATTACK_POTION(new int[] { 149, 147, 145, 2436 }, new int[] { Skills.ATTACK }, PotionType.SUPER_POTION),

		SUPER_STRENGTH_POTION(new int[] { 161, 159, 157, 2440 }, new int[] { Skills.STRENGTH },
				PotionType.SUPER_POTION),

		SUPER_DEFENCE_POTION(new int[] { 167, 165, 163, 2442 }, new int[] { Skills.DEFENCE }, PotionType.SUPER_POTION),

		SUPER_MAGIC_POTION(new int[] { 11729, 11728, 11727, 11726 }, new int[] { Skills.MAGIC }, PotionType.PLUS_10),

		SUPER_COMBAT_POTION(new int[] { 12701, 12699, 12697, 12695 },
				new int[] { Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE }, PotionType.SUPER_POTION),

		SARADOMIN_BREW(new int[] { 6691, 6689, 6687, 6685 }, new int[] { Skills.ATTACK, Skills.DEFENCE, Skills.STRENGTH,
				Skills.MAGIC, Skills.RANGE, Skills.HITPOINTS }, PotionType.SARADOMIN_BREW),

		ZAMORAK_BREW(new int[] { 193, 191, 189, 2450 },
				new int[] { Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE, Skills.HITPOINTS, Skills.PRAYER },
				PotionType.ZAMORAK_BREW),

		ANTIPOISON(new int[] { 179, 177, 175, 2446 }, new int[] {}, PotionType.ANTIPOISON),

		SUPER_ANTIPOISON(new int[] { 185, 183, 181, 2448 }, new int[] {}, PotionType.SUPER_ANTIPOISON),

		ANTIDOTE_PLUS(new int[] { 5949, 5947, 5945, 5943 }, new int[] {}, PotionType.ANTIDOTE_PLUS),

		ANTIDOTE_PLUS_PLUS(new int[] { 5958, 5956, 5954, 5952 }, new int[] {}, PotionType.ANTIDOTE_PLUS_PLUS),

		ANTIFIRE(new int[] { 2458, 2456, 2454, 2452 }, new int[] {}, PotionType.ANTIFIRE),

		EXTENDED_ANTIFIRE(new int[] { 11957, 11955, 11953, 11951 }, new int[] {}, PotionType.EXTENDED_ANTIFIRE),

		ENERGY(new int[] { 3014, 3012, 3010, 3008 }, new int[] {}, PotionType.ENERGY),

		SUPER_ENERGY(new int[] { 3022, 3020, 3018, 3016 }, new int[] {}, PotionType.SUPER_ENERGY),

		BEER(new int[] { 1919, 1917 }, new int[] { Skills.ATTACK, Skills.STRENGTH }, PotionType.BEER),

		JUG(new int[] { 1935, 1993 }, new int[] { Skills.ATTACK, Skills.HITPOINTS }, PotionType.WINE),

		CUP_OF_TEA(new int[] { 1980, 712 }, new int[] {}, PotionType.DEFAULT),

		ANTI_VENOM(new int[] { 12911, 12909, 12907, 12905 }, new int[] {}, PotionType.ANTI_VENOM),

		ANTI_VENOM_PLUS(new int[] { 12919, 12917, 12915, 12913 }, new int[] {}, PotionType.ANTI_VENOM_PLUS),

		STAMINA_POTION(new int[] { 12631, 12629, 12627, 12625 }, new int[] {}, PotionType.STAMINA_POTION),

		SANFREW_SERUM(new int[] { 10931, 10929, 10927, 10925 },
				new int[] { Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE, Skills.MAGIC, Skills.RANGE, Skills.PRAYER,
						Skills.AGILITY, Skills.COOKING, Skills.CRAFTING, Skills.FARMING, Skills.FIREMAKING,
						Skills.FISHING, Skills.FLETCHING, Skills.HERBLORE, Skills.MINING, Skills.RUNECRAFTING,
						Skills.SLAYER, Skills.SMITHING, Skills.THIEVING, Skills.WOODCUTTING },
				PotionType.SANFEW_SERUM);

		/**
		 * A map of drink Ids.
		 */
		private static Map<Integer, Drink> drinks = new HashMap<Integer, Drink>();

		/**
		 * Gets a drink by its ID.
		 * 
		 * @param drink
		 *            The drink id.
		 * @return The Drink, or <code>null</code> if the id is not a drink.
		 */
		public static Drink forId(int drink) {
			return drinks.get(drink);
		}

		/**
		 * Populates the drink map.
		 */
		static {
			for (Drink drink : Drink.values()) {
				for (int i = 0; i < drink.id.length; i++) {
					drinks.put(drink.id[i], drink);
				}
			}
		}

		/**
		 * The drink item id.
		 */
		private int[] id;

		/**
		 * The skill to boost.
		 */
		private int skill[];

		/**
		 * The potion type.
		 */
		private PotionType potionType;

		/**
		 * Creates the drink.
		 * 
		 * @param id
		 *            The drink item id.
		 */
		private Drink(int id[], int[] skill, PotionType potionType) {
			this.id = id;
			this.skill = skill;
			this.potionType = potionType;
		}

		/**
		 * Gets the drink item id.
		 * 
		 * @return The drink item id.
		 */
		public int getId(int index) {
			return id[index];
		}

		/**
		 * Gets the drink item id.
		 * 
		 * @return The drink item id.
		 */
		public int[] getIds() {
			return id;
		}

		/**
		 * Gets the boosted skill.
		 * 
		 * @return The boosted skill.
		 */
		public int[] getSkills() {
			return skill;
		}

		/**
		 * Gets the boosted skill.
		 * 
		 * @return The boosted skill.
		 */
		public int getSkill(int index) {
			return skill[index];
		}

		/**
		 * Gets the potion type.
		 * 
		 * @return The potion type.
		 */
		public PotionType getPotionType() {
			return potionType;
		}
	}

	public static enum PotionType {

		NORMAL_POTION(0),

		SUPER_POTION(1),

		SARADOMIN_BREW(2),

		ZAMORAK_BREW(3),

		PLUS_5(4),

		RESTORE(5),

		SUPER_RESTORE(6),

		PRAYER_POTION(7),

		ANTIPOISON(8),

		SUPER_ANTIPOISON(9),

		BEER(10), WINE(11),

		ANTIFIRE(12),

		ENERGY(13),

		SUPER_ENERGY(14),

		ANTI_VENOM(15),

		EXTENDED_ANTIFIRE(16),

		ANTI_VENOM_PLUS(17),

		STAMINA_POTION(18),

		SANFEW_SERUM(19),

		DEFAULT(20),

		PLUS_10(21),

		ANTIDOTE_PLUS(22),

		ANTIDOTE_PLUS_PLUS(23);

		/**
		 * A map of PotionType Ids.
		 */
		private static Map<Integer, PotionType> potionTypes = new HashMap<Integer, PotionType>();

		/**
		 * Gets a PotionType by its ID.
		 * 
		 * @param potionType
		 *            The PotionType id.
		 * @return The PotionType, or <code>null</code> if the id is not a
		 *         PotionType.
		 */
		public static PotionType forId(int potionType) {
			return potionTypes.get(potionType);
		}

		/**
		 * Populates the potion type map.
		 */
		static {
			for (PotionType potionType : PotionType.values()) {
				potionTypes.put(potionType.id, potionType);
			}
		}

		/**
		 * The potion type id.
		 */
		private int id;

		/**
		 * Creates the potion type.
		 * 
		 * @param id
		 *            The potion type id.
		 */
		private PotionType(int id) {
			this.id = id;
		}

		/**
		 * Gets the potion type id.
		 * 
		 * @return The potion type id.
		 */
		public int getId() {
			return id;
		}
	}

}
