package org.rs2server.rs2.model.skills.slayer;

import java.util.Arrays;

import org.rs2server.rs2.model.Location;

public class SlayerTask {

	/**
	 * The slayer task groups. Each group has a unique id which is assigned by Jagex
	 * for the Slayer rewards interface, therefore these ids SHOULD NOT BE TOUCHED.
	 */
	public enum TaskGroup {

		GOBLINS(2),

		COW(6),

		BATS(8),

		ZOMBIES(10),

		SKELETONS(11),

		HILL_GIANTS(14),

		FIRE_GIANTS(16),

		MOSS_GIANTS(17),

		GREEN_DRAGONS(24),

		BLACK_DRAGONS(27),

		LESSER_DEMONS(28),

		GREATER_DEMONS(29),

		BLACK_DEMONS(30),

		HELLHOUNDS(31),

		TUROTHS(36),

		CAVE_CRAWLERS(37),

		CRAWLING_HANDS(39),

		ABERRANT_SPECTRE(41),

		ABYSSAL_DEMONS(42),

		BASILISKS(43),

		COCKATRICE(44),

		KURASKS(45),

		GARGOYLES(46),

		PYREFIENDS(47),

		BLOODVELDS(48),

		DUST_DEVILS(49),

		JELLIES(50),

		NECHRYAEL(52),

		BRONZE_DRAGONS(58),

		IRON_DRAGONS(59),

		STEEL_DRAGONS(60),

		CAVE_SLIMES(62),

		CAVE_BUG(63),

		DARK_BEASTS(66),

		DESERT_LIZARD(68),

		SKELETAL_WYVERN(72),

		MINOTAURS(76),

		ANKOUS(79),

		CAVE_HORRORS(80),

		SPIRITUAL_CREATURES(89),

		CAVE_KRAKEN(92),

		AVIANSES(94),

		SMOKE_DEVIL(95),

		TZHAARS(96),

		BOSSES(98),

		JAD(97),

		CREATURES(99),

		BLUE_DRAGON(25),

		BANSHEE(38);

		private int id;

		TaskGroup(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}

		public static TaskGroup forName(String taskGroup) {
			if (taskGroup == null)
				return null;
			return Arrays.stream(TaskGroup.values()).filter(g -> g.name().equals(taskGroup)).findFirst().get();
		}
	}

	public enum Master {

		TURAEL(401, 3, 2, // starter
				new Object[][] { { "Chicken", 1, 10, 15, 6.0, TaskGroup.CREATURES, Location.create(3234, 3292, 0) },
						{ "Rock Crab", 1, 10, 15, 10.0, TaskGroup.CREATURES, Location.create(2673, 3710, 0) },
						{ "Yak", 1, 10, 15, 10.0, TaskGroup.CREATURES, Location.create(2321, 3803, 0) },
						{ "Goblin", 1, 10, 15, 8.0, TaskGroup.GOBLINS, Location.create(3247, 3241, 0) },
						{ "Experiment", 1, 10, 15, 15.0, TaskGroup.CREATURES, Location.create(3247, 3241, 0) },
						{ "Cow", 1, 10, 15, 6.0, TaskGroup.COW, Location.create(3259, 3263, 0) } }),

		MAZCHNA(402, 3, 5, // easy
				new Object[][] { { "Chicken", 1, 25, 50, 6.0, TaskGroup.CREATURES, Location.create(3234, 3292, 0) },
						{ "Giant bat", 1, 25, 50, 8.0, TaskGroup.BATS, Location.create(2911, 9831, 0) },
						{ "Rock Crab", 1, 10, 45, 10.0, TaskGroup.CREATURES, Location.create(2673, 3710, 0) },
						{ "Goblin", 1, 10, 45, 6.0, TaskGroup.GOBLINS, Location.create(3247, 3241, 0) },
						{ "Bear", 1, 10, 40, 6.0, TaskGroup.CREATURES, Location.create(2701, 3331, 0) },
						{ "Cave Bug", 7, 10, 40, 10.0, TaskGroup.CAVE_BUG, Location.create(3169, 3169, 0) },
						{ "Scorpion", 1, 10, 40, 8.0, TaskGroup.CREATURES, Location.create(3300, 3273, 0) },
						{ "Skeleton", 1, 10, 40, 8.0, TaskGroup.SKELETONS, Location.create(2884, 9809, 0) },
						{ "Zombie", 1, 10, 40, 8.0, TaskGroup.ZOMBIES, Location.create(3146, 9901, 0) },
						{ "Cave crawler", 10, 35, 75, 11.0, TaskGroup.CAVE_CRAWLERS, Location.create(3169, 3169, 0) },
						// { "Cave slime", 17, 10, 40, 10.0, TaskGroup.CAVE_SLIMES,
						// Location.create(3169, 3169, 0) },
						{ "Cow", 1, 10, 40, 5.0, TaskGroup.COW, Location.create(3259, 3263, 0) },
						{ "Desert lizard", 22, 10, 40, 9.0, TaskGroup.DESERT_LIZARD, Location.create(3387, 3056, 0) },
						{ "Dwarf", 1, 10, 40, 7.0, TaskGroup.CREATURES, Location.create(3033, 3470, 0) } }),

		CHAELDAR(404, 20, 15, // easy
				new Object[][] { { "Ankou", 1, 25, 50, 8.0, TaskGroup.ANKOUS, Location.create(2377, 9751, 0) },
						{ "Banshee", 15, 10, 40, 12.0, TaskGroup.BANSHEE, Location.create(3442, 3545, 0) },
						{ "Green dragon", 35, 15, 40, 25.0, TaskGroup.GREEN_DRAGONS, Location.create(2968, 3607, 0) },
						{ "Goblin", 1, 10, 45, 8.0, TaskGroup.GOBLINS, Location.create(3248, 3238, 0) },
						{ "Rock Crab", 1, 10, 45, 10.0, TaskGroup.CREATURES, Location.create(2673, 3711, 0) },
						{ "Crawling Hand", 5, 10, 45, 15.0, TaskGroup.CRAWLING_HANDS, Location.create(3414, 3540, 0) },
						{ "Hill Giant", 20, 15, 40, 17.0, TaskGroup.HILL_GIANTS, Location.create(3118, 9854, 0) },
						{ "Cave crawler", 10, 35, 75, 11.0, TaskGroup.CAVE_CRAWLERS, Location.create(2798, 9997, 0) },
						// { "Cave slime", 17, 10, 40, 9.0, TaskGroup.CAVE_SLIMES, Location.create(3163,
						// 9589, 0) },
						{ "Lesser demon", 30, 25, 45, 22.0, TaskGroup.LESSER_DEMONS, Location.create(2837, 9569, 0) },
						{ "Ice warrior", 30, 25, 60, 22.0, TaskGroup.CREATURES, Location.create(3033, 9582, 0) },
						{ "Ice giant", 30, 25, 60, 24.0, TaskGroup.CREATURES, Location.create(3033, 9582, 0) },
						{ "Cockatrice", 25, 50, 70, 19.0, TaskGroup.COCKATRICE, Location.create(2793, 10033, 0) },
						{ "Moss giant", 20, 30, 60, 23.0, TaskGroup.MOSS_GIANTS, Location.create(3165, 9892, 0) },
						{ "Fire giant", 20, 40, 85, 52.0, TaskGroup.FIRE_GIANTS, Location.create(2350, 9755, 0) } }),

		VANNAKA(403, 40, 10, // medium
				new Object[][] {
						{ "Hill Giant", 20, 15, 60, 20.0, TaskGroup.HILL_GIANTS, Location.create(3118, 9854, 0) },
						{ "Lizardman", 1, 25, 60, 35.0, TaskGroup.CREATURES, Location.create(1478, 3692, 0) },
						{ "Lesser demon", 30, 25, 110, 85.0, TaskGroup.LESSER_DEMONS, Location.create(2837, 9569, 0) },
						{ "Greater demon", 30, 25, 150, 90.0, TaskGroup.GREATER_DEMONS,
								Location.create(2640, 9512, 2) },
						{ "Cockatrice", 25, 30, 80, 37.0, TaskGroup.COCKATRICE, Location.create(2793, 10033, 0) },
						{ "Green dragon", 35, 35, 80, 40.0, TaskGroup.GREEN_DRAGONS, Location.create(2968, 3607, 0) },
						{ "Blue dragon", 35, 35, 75, 40.0, TaskGroup.BLUE_DRAGON, Location.create(2906, 9810, 0) },
						{ "Fire giant", 62, 40, 150, 111.0, TaskGroup.FIRE_GIANTS, Location.create(2350, 9755, 0) },
						{ "Moss giant", 35, 30, 100, 40.0, TaskGroup.MOSS_GIANTS, Location.create(3165, 9892, 0) },
						{ "Bronze dragon", 50, 20, 50, 125.0, TaskGroup.BRONZE_DRAGONS,
								Location.create(2726, 9487, 0) },
						{ "Iron dragon", 65, 18, 47, 173.2, TaskGroup.IRON_DRAGONS, Location.create(2709, 9470, 0) },
						{ "Steel dragon", 79, 16, 45, 220.4, TaskGroup.STEEL_DRAGONS, Location.create(2709, 9470, 0) },
						{ "Black dragon", 75, 20, 60, 119.4, TaskGroup.BLACK_DRAGONS, Location.create(2835, 9819, 0) },
						{ "Abyssal demon", 85, 60, 160, 150.0, TaskGroup.ABYSSAL_DEMONS,
								Location.create(3418, 3566, 2) },
						{ "Tzhaar", 40, 30, 150, 140.0, TaskGroup.TZHAARS, Location.create(2480, 5167, 0) },
						{ "Ice warrior", 30, 25, 60, 22.0, TaskGroup.CREATURES, Location.create(3033, 9582, 0) },
						{ "Ice giant", 30, 25, 60, 24.0, TaskGroup.CREATURES, Location.create(3033, 9582, 0) },
						{ "Banshee", 15, 30, 80, 45.0, TaskGroup.BANSHEE, Location.create(3442, 3545, 0) },
						{ "Bloodveld", 50, 60, 150, 120.0, TaskGroup.BLOODVELDS, Location.create(2424, 9787, 0) },
						{ "Gargoyle", 75, 50, 110, 105.0, TaskGroup.GARGOYLES, Location.create(3442, 3543, 2) },
						{ "Hellhound", 50, 50, 200, 116.0, TaskGroup.HELLHOUNDS, Location.create(2865, 9849, 0) },
						{ "Turoth", 55, 30, 100, 79.0, TaskGroup.TUROTHS, Location.create(2711, 10012, 0) },
						{ "Kurask", 70, 40, 100, 97.0, TaskGroup.KURASKS, Location.create(2705, 9994, 0) },
						{ "Jelly", 52, 30, 100, 75.0, TaskGroup.JELLIES, Location.create(2715, 10031, 0) },
						{ "Pyrefiend", 45, 30, 100, 45.0, TaskGroup.PYREFIENDS, Location.create(2766, 10017, 0) },
						{ "Basilisk", 40, 30, 100, 75.0, TaskGroup.BASILISKS, Location.create(2745, 10001, 0) } }),

		STEVE(6798, 85, 25, // hard
				new Object[][] {
						{ "Abyssal demon", 85, 50, 150, 75, TaskGroup.ABYSSAL_DEMONS, Location.create(3418, 3566, 2) },
						{ "Aberrant spectre", 60, 50, 150, 75, TaskGroup.ABERRANT_SPECTRE,
								Location.create(3440, 3548, 1) },
						{ "Ankou", 1, 50, 185, 75, TaskGroup.ANKOUS, Location.create(2377, 9751, 0) },
						{ "Black demon", 1, 50, 185, 75, TaskGroup.BLACK_DEMONS, Location.create(3103, 9955, 0) },
						{ "Black dragon", 1, 10, 40, 75, TaskGroup.BLACK_DRAGONS, Location.create(2835, 9819, 0) },
						{ "Bloodveld", 50, 50, 185, 75, TaskGroup.BLOODVELDS, Location.create(2424, 9787, 0) },
						{ "Blue dragon", 1, 50, 90, 75, TaskGroup.BLUE_DRAGON, Location.create(2906, 9810, 0) },
						{ "Cave horror", 55, 100, 185, 75, TaskGroup.CAVE_HORRORS, Location.create(3740, 9373, 0) },
						// { "Cave Kraken", 87, 100, 125, 75, TaskGroup.CAVE_KRAKEN,
						// Location.create(2458, 9811, 0) },
						{ "Dagannoth", 1, 50, 185, 75, TaskGroup.CREATURES, Location.create(2516, 10145, 0) },
						{ "Dark beast", 90, 50, 150, 75, TaskGroup.DARK_BEASTS, Location.create(2006, 4641, 0) },
						{ "Dust devil", 65, 100, 185, 75, TaskGroup.DUST_DEVILS, Location.create(3206, 9379, 0) },
						// { "Elve", 1, 60, 90, 75, TaskGroup.CREATURES, null },
						{ "Fire giant", 1, 100, 190, 75, TaskGroup.FIRE_GIANTS, Location.create(2350, 9755, 0) },
						{ "Gargoyle", 75, 100, 185, 75, TaskGroup.GARGOYLES, Location.create(3442, 3543, 2) },
						{ "Greator demon", 1, 100, 185, 75, TaskGroup.GREATER_DEMONS, Location.create(2640, 9512, 2) },
						{ "Hellhound", 1, 100, 185, 75, TaskGroup.HELLHOUNDS, Location.create(2865, 9849, 0) },
						{ "Iron dragon", 1, 20, 40, 75, TaskGroup.IRON_DRAGONS, Location.create(2709, 9470, 0) },
						// { "Kalphite", 1, 120, 185, 75, TaskGroup.CREATURES,
						// null },
						{ "Kurask", 70, 100, 185, 75, TaskGroup.KURASKS, Location.create(2705, 9994, 0) },
						// { "Mithril Dragon", 1, 4, 9, 75, TaskGroup.CREATURES,
						// null },
						{ "Nechryael", 80, 100, 185, 75, TaskGroup.NECHRYAEL, null },
						{ "Red dragon", 1, 15, 40, 75, TaskGroup.CREATURES, Location.create(2688, 9507, 0) },
						{ "Skeletal wyvern", 72, 20, 50, 75, TaskGroup.SKELETAL_WYVERN,
								Location.create(3056, 9564, 0) },
						{ "Smoke devil", 93, 100, 185, 75, TaskGroup.SMOKE_DEVIL, Location.create(3745, 5793, 0) },
						// { "Spiritual warrior", 63, 30, 100, 75,
						// TaskGroup.SPIRITUAL_CREATURES, null },
						// { "Spiritual ranger", 63, 30, 100, 75,
						// TaskGroup.SPIRITUAL_CREATURES, null },
						{ "Spiritual mage", 83, 30, 100, 75, TaskGroup.SPIRITUAL_CREATURES,
								Location.create(2880, 5310, 2) },
						{ "Steel dragon", 1, 15, 35, 75, TaskGroup.STEEL_DRAGONS, Location.create(2709, 9470, 0) },
						{ "Suqah", 1, 100, 185, 75, TaskGroup.CREATURES, Location.create(2101, 3865, 0) },
						// { "Troll", 1, 120, 185, 75, TaskGroup.CREATURES, null
						// },
						{ "Turoth", 55, 100, 185, 75, TaskGroup.TUROTHS, Location.create(2711, 10012, 0) },
						{ "TzHaar", 1, 100, 185, 75, TaskGroup.TZHAARS, Location.create(2480, 5167, 0) } }),

		NIEVE(490, 115, 40, // boss
				new Object[][] { { "General Graardor", 90, 20, 45, 225.4, TaskGroup.BOSSES, null },
						{ "Chaos Fanatic", 90, 20, 45, 225.4, TaskGroup.BOSSES, null },
						{ "Dagannoth Prime", 90, 20, 45, 225.4, TaskGroup.BOSSES, null },
						{ "Dagannoth Rex", 90, 20, 45, 225.4, TaskGroup.BOSSES, null },
						{ "Dagannoth Supreme", 90, 20, 45, 225.4, TaskGroup.BOSSES, null },
						{ "K'ril Tsutsaroth", 90, 20, 45, 225.4, TaskGroup.BOSSES, null },
						{ "Kraken", 87, 20, 45, 225.4, TaskGroup.BOSSES, null },
						{ "Kree'arra", 90, 20, 45, 225.4, TaskGroup.BOSSES, null },
						{ "Zulrah", 90, 20, 45, 225.4, TaskGroup.BOSSES, null },
						{ "Commander Zilyana", 90, 20, 45, 225.4, TaskGroup.BOSSES, null },
						{ "Thermonuclear smoke devil", 93, 20, 45, 225.4, TaskGroup.BOSSES, null },
						{ "Chaos Elemental", 90, 20, 45, 225.4, TaskGroup.BOSSES, null },
						{ "King Black Dragon", 90, 20, 45, 225.4, TaskGroup.BOSSES, null },
						{ "TzTok-Jad", 90, 2, 5, 1250, TaskGroup.BOSSES, null },
						{ "Cerberus", 91, 20, 45, 225.4, TaskGroup.BOSSES, null }, });

		private int id;
		private int combatRequired;
		private int taskRewardPoints;
		private Object[][] data;

		@SuppressWarnings("unused")
		private TaskGroup group;

		Master(int id, int combatRequired, int taskRewardPoints, Object[][] data) {
			this.id = id;
			this.combatRequired = combatRequired;
			this.taskRewardPoints = taskRewardPoints;
			this.data = data;
		}

		public static Master forId(int id) {
			for (Master master : Master.values()) {
				if (master.id == id)
					return master;
			}
			return null;
		}

		public int getId() {
			return id;
		}

		public int getCombatRequired() {
			return combatRequired;
		}

		public int getTaskRewardPoints() {
			return taskRewardPoints;
		}

		public Object[][] getData() {
			return data;
		}

	}

	private Master master;
	private int taskId;
	private int taskAmount;
	private int initialAmount;

	public SlayerTask(Master master, int taskId, int taskAmount) {
		this.master = master;
		this.taskId = taskId;
		this.initialAmount = taskAmount;
		this.taskAmount = taskAmount;
	}

	public String getName() {
		return (String) master.data[taskId][0];
	}

	public int getTaskId() {
		return taskId;
	}

	public int getTaskAmount() {
		return taskAmount;
	}

	public void decreaseAmount() {
		taskAmount--;
	}

	public double getXPAmount() {
		return Double.parseDouble(master.data[taskId][4].toString());
	}

	public int getInitialAmount() {
		return initialAmount;
	}

	public Master getMaster() {
		return master;
	}
}