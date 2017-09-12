package org.rs2server.rs2.model.skills.smithing;

import org.rs2server.cache.format.CacheItemDefinition;
import org.rs2server.rs2.model.Item;

import java.util.*;

public class SmithingUtils {

	public static final Item HAMMER = new Item(2347);

	public enum ForgingBar {

		BRONZE(2349, 0,
				new int[] { 1205, 1277, 1321, 1291, 1307, 1351, 1422, 1337, 1375, -1, 1103, 1075, 1087, 1117, 4819,
						1139, 1155, 1173, 1189, -1, -1, 819, 39, 864, 1794, -1, 9375, 9420, 19570, -1, -1, -1 },
				12.5, new int[] { 66, 82, 210, 267 }),

		IRON(2351, 15,
				new int[] { 1203, 1279, 1323, 1293, 1309, 1349, 1420, 1335, 1363, -1, 1101, 1067, 1081, 1115, 4820,
						1137, 1153, 1175, 1191, 4540, -1, 820, 40, 863, 7225, -1, 9377, 9423, 19572 },
				25, new int[] { 66, 90, 162, 210, 267 }),

		STEEL(2353, 30,
				new int[] { 1207, 1281, 1325, 1295, 1311, 1353, 1424, 1339, 1365, -1, 1105, 1069, 1083, 1119, 1539,
						1141, 1157, 1177, 1193, 4544, -1, 821, 41, 865, 2370, -1, 9378, 9425, 19574 },
				37.5, new int[] { 66, 98, 162, 210, 267 }),

		MITHRIL(2359, 50,
				new int[] { 1209, 1285, 1329, 1299, 1315, 1355, 1428, 1343, 1369, -1, 1109, 1071, 1085, 1121, 4822,
						1143, 1159, 1181, 1197, -1, -1, 822, 42, 866, 9416, 9427, 9379, 9427, 9379, 19576 },
				50, new int[] { 66, 170, 210, 267 }),

		ADAMANT(2361, 70,
				new int[] { 1211, 1287, 1331, 1301, 1317, 1357, 1430, 1345, 1371, -1, 1111, 1073, 1091, 1123, 4823,
						1145, 1161, 1183, 1199, -1, -1, 823, 43, 867, -1, -1, 9380, 9429, 19578 },
				62.5, new int[] { 66, 210, 267 }),

		RUNE(2363, 85,
				new int[] { 1213, 1289, 1333, 1303, 1319, 1359, 1432, 1347, 1373, -1, 1113, 1079, 1093, 1127, 4824,
						1147, 1163, 1185, 1201, -1, -1, 824, 44, 868, -1, -1, 9381, 9431, 19580 },
				75, new int[] { 66, 210, 267 });

		private int barId;
		private int[] items;
		private double experience;
		private int baseLevel;

		private ForgingBar(int barId, int baseLevel, int[] items, double experience, int[] activatedChildren) {
			this.barId = barId;
			this.baseLevel = baseLevel;
			this.items = items;
			this.experience = experience;
		}

		public int[] getItems() {
			return items;
		}

		public int getBaseLevel() {
			return baseLevel;
		}

		public int getBarId() {
			return barId;
		}

		public double getExperience() {
			return experience;
		}

		private static Map<Integer, ForgingBar> smithingBars = new HashMap<Integer, ForgingBar>();

		static {
			for (ForgingBar bar : ForgingBar.values()) {
				smithingBars.put(bar.barId, bar);
			}
		}

		public static ForgingBar forId(int id) {
			return smithingBars.get(id);
		}
	}

	public enum SmeltingBar {

		// The primary ore MUST be the first element in the required items
		BRONZE(1, 6.2, new Item[] { new Item(436), new Item(438) }, new Item(2349)),

		BLURITE(8, 8.0, new Item[] { new Item(668) }, new Item(9467)),

		IRON(15, 12.5, new Item[] { new Item(440) }, new Item(2351)),

		SILVER(20, 13.7, new Item[] { new Item(442) }, new Item(2355)),

		STEEL(30, 17.5, new Item[] { new Item(440), new Item(453, 2) }, new Item(2353)),

		GOLD(40, 22.5, new Item[] { new Item(444) }, new Item(2357)),

		MITHRIL(50, 30, new Item[] { new Item(447), new Item(453, 4) }, new Item(2359)),

		ADAMANT(70, 37.5, new Item[] { new Item(449), new Item(453, 6) }, new Item(2361)),

		RUNE(85, 50, new Item[] { new Item(451), new Item(453, 8) }, new Item(2363));

		private int levelRequired;
		private double experience;
		private Item[] itemsRequired;
		private Item producedBar;

		private SmeltingBar(int levelRequired, double experience, Item[] itemsRequired, Item producedBar) {
			this.levelRequired = levelRequired;
			this.experience = experience;
			this.itemsRequired = itemsRequired;
			this.producedBar = producedBar;
		}

		public static SmeltingBar of(int ore) {
			for (SmeltingBar bar : SmeltingBar.values()) {
				for (Item item : bar.getItemsRequired()) {
					if (item.getId() == ore) {
						return bar;
					}
				}
			}
			return null;
		}

		public Item[] getItemsRequired() {
			return itemsRequired;
		}

		public int getLevelRequired() {
			return levelRequired;
		}

		public Item getProducedBar() {
			return producedBar;
		}

		public double getExperience() {
			return experience;
		}

		public int getAmount(int childId) {
			switch (childId) {
			case 16:
			case 20:
			case 24:
			case 28:
			case 32:
			case 36:
			case 40:
			case 44:
			case 48:
				return 1;
			case 15:
			case 19:
			case 23:
			case 27:
			case 31:
			case 35:
			case 39:
			case 43:
			case 47:
				return 5;
			case 14:
			case 18:
			case 22:
			case 26:
			case 30:
			case 34:
			case 38:
			case 42:
			case 46:
				return 10;
			case 13:
			case 17:
			case 21:
			case 25:
			case 29:
			case 33:
			case 37:
			case 41:
			case 45:
				return -1;
			}
			return 0;
		}
	}

	public static final int[] CHILD_IDS = new int[29]; 

	public static final int[] CLICK_OPTIONS = { 1, 5, 10, 32767, 9 };

	private static Map<String, Integer> levelThreshold = new HashMap<String, Integer>();

	static {
		int counter = 2;
		for (int i = 0; i < CHILD_IDS.length; i++) {
			CHILD_IDS[i] = counter;
			counter += 1;
		}
		levelThreshold.put("dagger", 0);
		levelThreshold.put("sword", 4);
		levelThreshold.put("scimitar", 5);
		levelThreshold.put("longsword", 6);
		levelThreshold.put("2h sword", 14);

		levelThreshold.put("axe", 2);
		levelThreshold.put("mace", 2);
		levelThreshold.put("warhammer", 9);
		levelThreshold.put("battleaxe", 10);

		levelThreshold.put("chainbody", 11);
		levelThreshold.put("platelegs", 16);
		levelThreshold.put("plateskirt", 16);
		levelThreshold.put("platebody", 18);
		levelThreshold.put("nails", 4);

		levelThreshold.put("med helm", 3);
		levelThreshold.put("full helm", 7);
		levelThreshold.put("sq shield", 8);
		levelThreshold.put("kiteshield", 12);
		levelThreshold.put("Oil lantern frame", 11);
		levelThreshold.put("Bullseye lantern (unf)", 19);

		levelThreshold.put("dart tip", 4);
		levelThreshold.put("arrowtips", 5);
		levelThreshold.put("knife", 7);
		levelThreshold.put("javelin heads", 4);
		levelThreshold.put("Iron spit", 2);
		levelThreshold.put("wire", 4);

		levelThreshold.put("bolts", 3);
		levelThreshold.put("limbs", 6);

		levelThreshold.put("grapple tip", 9);
		levelThreshold.put("studs", 6);
	}

	public static int[] barToIntArray(List<SmeltingBar> bar) {
		int[] newArray = new int[bar.size()];
		for (int i = 0; i < bar.size(); i++) {
			newArray[i] = bar.get(i).ordinal();
		}
		return newArray;
	}

	public static int getLevelIncrement(ForgingBar bar, int id) {
		if (id == -1) {
			return 1;
		}
		String name = CacheItemDefinition.get(id).getName();
		for (Map.Entry<String, Integer> entry : levelThreshold.entrySet()) {
			if (name.contains(entry.getKey())) {
				int increment = entry.getValue();
				if (name.contains("dagger") && bar != ForgingBar.BRONZE) {
					increment--;
				} else if (name.contains("hatchet") && bar == ForgingBar.BRONZE || bar == ForgingBar.RUNE) {
					increment--;
				}
				if (bar == ForgingBar.RUNE && increment > 14) {
					increment -= 4;
				}
				return increment;
			}
		}
		System.out.println(name + " hasn't been added to the level increment map!");
		return 1;
	}

	public static int getItemAmount(int id) {
		String name = CacheItemDefinition.get(id).getName();
		if (name.contains("knife") || name.contains("javelin heads")) {
			return 5;
		} else if (name.contains("bolts") || name.contains("dart tip")) {
			return 10;
		} else if (name.contains("arrowtips") || name.contains("nails")) {
			return 15;
		}
		return 1;
	}

	public static int getBarAmount(int levelRequired, ForgingBar bar, int id) {
		if (levelRequired >= 99)
			levelRequired = 99;
		int level = levelRequired - bar.baseLevel;
		String name = CacheItemDefinition.get(id).getName().toLowerCase();
		if (level >= 0 && level <= 4) {
			if (name.contains("battleaxe") || name.contains("2h sword")) {
				return 3;
			} else if (name.contains("longsword")) {
				return 2;
			}
			return 1;
		} else if (level > 4 && level <= 8) {
			if (name.contains("knife") || name.contains("arrowtips") || name.contains("dart tip")
					|| name.contains("limb") || name.contains("studs")) {
				return 1;
			}
			return 2;
		} else if (level >= 9 && level <= 16) {
			if (name.contains("grapple")) {
				return 1;
			} else if (name.contains("claws")) {
				return 2;
			} else if (name.contains("platebody")) {
				return 5;
			}
			return 3;
		} else if (level >= 17) {
			if (name.contains("bullseye")) {
				return 1;
			}
			return 5;
		}
		return 1;
	}

}
