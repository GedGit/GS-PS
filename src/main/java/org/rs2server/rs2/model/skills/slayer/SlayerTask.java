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
				new Object[][] {
						{ new String[] { "Chicken", "Duck", "Seagull" }, 1, 10, 15, 6.0, TaskGroup.CREATURES,
								Location.create(3234, 3292, 0) },
						{ new String[] { "Rock Crab", "Sand crab" }, 1, 10, 15, 10.0, TaskGroup.CREATURES,
								Location.create(2673, 3710, 0) },
						{ new String[] { "Yak" }, 1, 10, 15, 10.0, TaskGroup.CREATURES,
								Location.create(2321, 3803, 0) },
						{ new String[] { "Goblin" }, 1, 10, 15, 8.0, TaskGroup.GOBLINS,
								Location.create(3247, 3241, 0) },
						{ new String[] { "Experiment" }, 1, 10, 15, 15.0, TaskGroup.CREATURES,
								Location.create(3247, 3241, 0) },
						{ new String[] { "Cow", "Cow calf" }, 1, 10, 15, 6.0, TaskGroup.COW,
								Location.create(3259, 3263, 0) } }),

		CHAELDAR(404, 20, 15, // easy
				new Object[][] {
						{ new String[] { "Ankou" }, 1, 25, 50, 8.0, TaskGroup.ANKOUS, Location.create(2377, 9751, 0) },
						{ new String[] { "Banshee" }, 15, 10, 40, 12.0, TaskGroup.BANSHEE,
								Location.create(3442, 3545, 0) },
						{ new String[] { "Green dragon" }, 35, 15, 40, 25.0, TaskGroup.GREEN_DRAGONS,
								Location.create(2968, 3607, 0) },
						{ new String[] { "Goblin" }, 1, 10, 45, 8.0, TaskGroup.GOBLINS,
								Location.create(3248, 3238, 0) },
						{ new String[] { "Rock Crab", "Sand crab" }, 1, 10, 45, 10.0, TaskGroup.CREATURES,
								Location.create(2673, 3711, 0) },
						{ new String[] { "Crawling Hand", "Crushing hand" }, 5, 10, 45, 15.0, TaskGroup.CRAWLING_HANDS,
								Location.create(3414, 3540, 0) },
						{ new String[] { "Hill Giant" }, 20, 15, 40, 17.0, TaskGroup.HILL_GIANTS,
								Location.create(3118, 9854, 0) },
						{ new String[] { "Cave crawler" }, 10, 35, 75, 11.0, TaskGroup.CAVE_CRAWLERS,
								Location.create(2798, 9997, 0) },
						{ new String[] { "Lesser demon" }, 30, 25, 45, 22.0, TaskGroup.LESSER_DEMONS,
								Location.create(2837, 9569, 0) },
						{ new String[] { "Ice warrior" }, 30, 25, 60, 22.0, TaskGroup.CREATURES,
								Location.create(3033, 9582, 0) },
						{ new String[] { "Ice giant" }, 30, 25, 60, 24.0, TaskGroup.CREATURES,
								Location.create(3033, 9582, 0) },
						{ new String[] { "Cockatrice", "Cockathrice" }, 25, 50, 70, 19.0, TaskGroup.COCKATRICE,
								Location.create(2793, 10033, 0) },
						{ new String[] { "Moss giant" }, 20, 30, 60, 23.0, TaskGroup.MOSS_GIANTS,
								Location.create(3165, 9892, 0) },
						{ new String[] { "Fire giant" }, 20, 40, 85, 52.0, TaskGroup.FIRE_GIANTS,
								Location.create(2350, 9755, 0) } }),

		VANNAKA(403, 40, 10, // medium
				new Object[][] {
						{ new String[] { "Hill Giant" }, 20, 15, 60, 20.0, TaskGroup.HILL_GIANTS,
								Location.create(3118, 9854, 0) },
						{ new String[] { "Lizardman" }, 1, 25, 60, 35.0, TaskGroup.CREATURES,
								Location.create(1478, 3692, 0) },
						{ new String[] { "Lesser demon" }, 30, 25, 110, 85.0, TaskGroup.LESSER_DEMONS,
								Location.create(2837, 9569, 0) },
						{ new String[] { "Greater demon" }, 30, 25, 150, 90.0, TaskGroup.GREATER_DEMONS,
								Location.create(2640, 9512, 2) },
						{ new String[] { "Cockatrice", "Cockathrice" }, 25, 30, 80, 37.0, TaskGroup.COCKATRICE,
								Location.create(2793, 10033, 0) },
						{ new String[] { "Green dragon" }, 35, 35, 80, 40.0, TaskGroup.GREEN_DRAGONS,
								Location.create(2968, 3607, 0) },
						{ new String[] { "Blue dragon", "Baby dragon" }, 35, 35, 75, 40.0, TaskGroup.BLUE_DRAGON,
								Location.create(2906, 9810, 0) },
						{ new String[] { "Fire giant" }, 62, 40, 150, 111.0, TaskGroup.FIRE_GIANTS,
								Location.create(2350, 9755, 0) },
						{ new String[] { "Moss giant" }, 35, 30, 100, 40.0, TaskGroup.MOSS_GIANTS,
								Location.create(3165, 9892, 0) },
						{ new String[] { "Bronze dragon" }, 50, 20, 50, 125.0, TaskGroup.BRONZE_DRAGONS,
								Location.create(2726, 9487, 0) },
						{ new String[] { "Iron dragon" }, 65, 18, 47, 173.2, TaskGroup.IRON_DRAGONS,
								Location.create(2709, 9470, 0) },
						{ new String[] { "Steel dragon" }, 79, 16, 45, 220.4, TaskGroup.STEEL_DRAGONS,
								Location.create(2709, 9470, 0) },
						{ new String[] { "Black dragon" }, 75, 20, 60, 119.4, TaskGroup.BLACK_DRAGONS,
								Location.create(2835, 9819, 0) },
						{ new String[] { "Abyssal demon" }, 85, 60, 160, 150.0, TaskGroup.ABYSSAL_DEMONS,
								Location.create(3418, 3566, 2) },
						{ new String[] { "Tzhaar" }, 40, 30, 150, 140.0, TaskGroup.TZHAARS,
								Location.create(2480, 5167, 0) },
						{ new String[] { "Ice warrior" }, 30, 25, 60, 22.0, TaskGroup.CREATURES,
								Location.create(3033, 9582, 0) },
						{ new String[] { "Ice giant" }, 30, 25, 60, 24.0, TaskGroup.CREATURES,
								Location.create(3033, 9582, 0) },
						{ new String[] { "Banshee" }, 15, 30, 80, 45.0, TaskGroup.BANSHEE,
								Location.create(3442, 3545, 0) },
						{ new String[] { "Bloodveld" }, 50, 60, 150, 120.0, TaskGroup.BLOODVELDS,
								Location.create(2424, 9787, 0) },
						{ new String[] { "Gargoyle" }, 75, 50, 110, 105.0, TaskGroup.GARGOYLES,
								Location.create(3442, 3543, 2) },
						{ new String[] { "Hellhound" }, 50, 50, 200, 116.0, TaskGroup.HELLHOUNDS,
								Location.create(2865, 9849, 0) },
						{ new String[] { "Turoth" }, 55, 30, 100, 79.0, TaskGroup.TUROTHS,
								Location.create(2711, 10012, 0) },
						{ new String[] { "Kurask" }, 70, 40, 100, 97.0, TaskGroup.KURASKS,
								Location.create(2705, 9994, 0) },
						{ new String[] { "Jelly" }, 52, 30, 100, 75.0, TaskGroup.JELLIES,
								Location.create(2715, 10031, 0) },
						{ new String[] { "Pyrefiend" }, 45, 30, 100, 45.0, TaskGroup.PYREFIENDS,
								Location.create(2766, 10017, 0) },
						{ new String[] { "Basilisk" }, 40, 30, 100, 75.0, TaskGroup.BASILISKS,
								Location.create(2745, 10001, 0) } }),

		STEVE(6798, 85, 25, // hard
				new Object[][] {
						{ new String[] { "Abyssal demon" }, 85, 50, 150, 75, TaskGroup.ABYSSAL_DEMONS,
								Location.create(3418, 3566, 2) },
						{ new String[] { "Aberrant spectre" }, 60, 50, 150, 75, TaskGroup.ABERRANT_SPECTRE,
								Location.create(3440, 3548, 1) },
						{ new String[] { "Ankou" }, 1, 50, 185, 75, TaskGroup.ANKOUS, Location.create(2377, 9751, 0) },
						{ new String[] { "Black Demon" }, 1, 50, 185, 75, TaskGroup.BLACK_DEMONS,
								Location.create(3103, 9955, 0) },
						{ new String[] { "Black Dragon" }, 1, 10, 40, 75, TaskGroup.BLACK_DRAGONS,
								Location.create(2835, 9819, 0) },
						{ new String[] { "Bloodveld" }, 50, 50, 185, 75, TaskGroup.BLOODVELDS,
								Location.create(2424, 9787, 0) },
						{ new String[] { "Blue Dragon", "Baby dragon" }, 1, 50, 90, 75, TaskGroup.BLUE_DRAGON,
								Location.create(2906, 9810, 0) },
						{ new String[] { "Cave Horror" }, 55, 100, 185, 75, TaskGroup.CAVE_HORRORS,
								Location.create(3740, 9373, 0) },
						{ new String[] { "Dagannoth" }, 1, 50, 185, 75, TaskGroup.CREATURES,
								Location.create(2516, 10145, 0) },
						{ new String[] { "Dark Beast" }, 90, 50, 150, 75, TaskGroup.DARK_BEASTS,
								Location.create(2006, 4641, 0) },
						{ new String[] { "Dust Devil" }, 65, 100, 185, 75, TaskGroup.DUST_DEVILS,
								Location.create(3206, 9379, 0) },
						{ new String[] { "Fire Giant" }, 1, 100, 190, 75, TaskGroup.FIRE_GIANTS,
								Location.create(2350, 9755, 0) },
						{ new String[] { "Gargoyle" }, 75, 100, 185, 75, TaskGroup.GARGOYLES,
								Location.create(3442, 3543, 2) },
						{ new String[] { "Greator Demon" }, 1, 100, 185, 75, TaskGroup.GREATER_DEMONS,
								Location.create(2640, 9512, 2) },
						{ new String[] { "Hellhound" }, 1, 100, 185, 75, TaskGroup.HELLHOUNDS,
								Location.create(2865, 9849, 0) },
						{ new String[] { "Iron Dragon" }, 1, 20, 40, 75, TaskGroup.IRON_DRAGONS,
								Location.create(2709, 9470, 0) },
						{ new String[] { "Kurask" }, 70, 100, 185, 75, TaskGroup.KURASKS,
								Location.create(2705, 9994, 0) },
						{ new String[] { "Nechryael" }, 80, 100, 185, 75, TaskGroup.NECHRYAEL, null },
						{ new String[] { "Red Dragon" }, 1, 15, 40, 75, TaskGroup.CREATURES,
								Location.create(2688, 9507, 0) },
						{ new String[] { "Skeletal Wyvern" }, 72, 20, 50, 75, TaskGroup.SKELETAL_WYVERN,
								Location.create(3056, 9564, 0) },
						{ new String[] { "Smoke Devil" }, 93, 100, 185, 75, TaskGroup.SMOKE_DEVIL,
								Location.create(3745, 5793, 0) },
						{ new String[] { "Spiritual mage" }, 83, 30, 100, 75, TaskGroup.SPIRITUAL_CREATURES,
								Location.create(2880, 5310, 2) },
						{ new String[] { "Steel Dragon" }, 1, 15, 35, 75, TaskGroup.STEEL_DRAGONS,
								Location.create(2709, 9470, 0) },
						{ new String[] { "Suqah" }, 1, 100, 185, 75, TaskGroup.CREATURES,
								Location.create(2101, 3865, 0) },
						{ new String[] { "Turoth" }, 55, 100, 185, 75, TaskGroup.TUROTHS,
								Location.create(2711, 10012, 0) },
						{ new String[] { "TzHaar" }, 1, 100, 185, 75, TaskGroup.TZHAARS,
								Location.create(2480, 5167, 0) } }),

		NIEVE(490, 115, 40, // boss
				new Object[][] { { new String[] { "General Graardor" }, 90, 20, 45, 225.4, TaskGroup.BOSSES, null },
						{ new String[] { "Chaos Fanatic" }, 90, 20, 45, 225.4, TaskGroup.BOSSES, null },
						{ new String[] { "Dagannoth Prime" }, 90, 20, 45, 225.4, TaskGroup.BOSSES, null },
						{ new String[] { "Dagannoth Rex" }, 90, 20, 45, 225.4, TaskGroup.BOSSES, null },
						{ new String[] { "Dagannoth Supreme" }, 90, 20, 45, 225.4, TaskGroup.BOSSES, null },
						{ new String[] { "K'ril Tsutsaroth" }, 90, 20, 45, 225.4, TaskGroup.BOSSES, null },
						{ new String[] { "Kraken" }, 87, 20, 45, 225.4, TaskGroup.BOSSES, null },
						{ new String[] { "Kree'arra" }, 90, 20, 45, 225.4, TaskGroup.BOSSES, null },
						{ new String[] { "Zulrah" }, 90, 20, 45, 225.4, TaskGroup.BOSSES, null },
						{ new String[] { "Commander Zilyana" }, 90, 20, 45, 225.4, TaskGroup.BOSSES, null },
						{ new String[] { "Thermonuclear smoke devil" }, 93, 20, 45, 225.4, TaskGroup.BOSSES, null },
						{ new String[] { "Chaos Elemental" }, 90, 20, 45, 225.4, TaskGroup.BOSSES, null },
						{ new String[] { "King Black Dragon" }, 90, 20, 45, 225.4, TaskGroup.BOSSES, null },
						{ new String[] { "TzTok-Jad" }, 90, 2, 5, 1250, TaskGroup.BOSSES, null },
						{ new String[] { "Cerberus" }, 91, 20, 45, 225.4, TaskGroup.BOSSES, null } });

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

	public String[] getName() {
		return (String[]) master.data[taskId][0];
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