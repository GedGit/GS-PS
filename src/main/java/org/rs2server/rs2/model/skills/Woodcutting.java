package org.rs2server.rs2.model.skills;

import org.rs2server.Server;
import org.rs2server.cache.format.CacheItemDefinition;
import org.rs2server.rs2.Constants;
import org.rs2server.rs2.action.impl.HarvestingAction;
import org.rs2server.rs2.domain.service.api.content.ItemService;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.npc.Pet;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.ActionSender;
import org.rs2server.rs2.util.Misc;

import java.util.*;

public class Woodcutting extends HarvestingAction {

	/**
	 * The tree we are cutting down.
	 */
	private GameObject object;

	/**
	 * The hatchet we are using.
	 */
	private Hatchet hatchet;

	/**
	 * The tree we are cutting down.
	 */
	private Tree tree;

	public Woodcutting(Mob mob, GameObject object) {
		super(mob);
		this.object = object;
		this.tree = Tree.forId(object.getId());
		Server.getInjector().getInstance(ItemService.class);
	}

	/**
	 * Represents types of axe hatchets.
	 *
	 * @author Michael (Scu11)
	 */
	public static enum Hatchet {

		/**
		 * 3rd age axe.
		 */
		THIRD_AGE(20011, 61, Animation.create(7264)),

		/**
		 * Infernal axe.
		 */
		INFERNAL(13241, 61, Animation.create(2117)),

		/**
		 * Dragon axe.
		 */
		DRAGON(6739, 61, Animation.create(2846)),

		/**
		 * Rune axe.
		 */
		RUNE(1359, 41, Animation.create(867)),

		/**
		 * Adamant axe.
		 */
		ADAMANT(1357, 31, Animation.create(869)),

		/**
		 * Mithril axe.
		 */
		MITHRIL(1355, 21, Animation.create(871)),

		/**
		 * Black axe.
		 */
		BLACK(1361, 6, Animation.create(873)),

		/**
		 * Steel axe.
		 */
		STEEL(1353, 6, Animation.create(875)),

		/**
		 * Iron axe.
		 */
		IRON(1349, 1, Animation.create(877)),

		/**
		 * Bronze axe.
		 */
		BRONZE(1351, 1, Animation.create(879));

		/**
		 * The item id of this hatchet.
		 */
		private int id;

		/**
		 * The level required to use this hatchet.
		 */
		private int level;

		/**
		 * The animation performed when using this hatchet.
		 */
		private Animation animation;

		/**
		 * A list of hatchets.
		 */
		private static List<Hatchet> hatchets = new ArrayList<Hatchet>();

		/**
		 * Gets the list of hatchets.
		 *
		 * @return The list of hatchets.
		 */
		public static List<Hatchet> getHatchets() {
			return hatchets;
		}

		/**
		 * Populates the hatchet map.
		 */
		static {
			for (Hatchet hatchet : Hatchet.values()) {
				hatchets.add(hatchet);
			}
		}

		private Hatchet(int id, int level, Animation animation) {
			this.id = id;
			this.level = level;
			this.animation = animation;
		}

		/**
		 * @return the id
		 */
		public int getId() {
			return id;
		}

		/**
		 * @return the level
		 */
		public int getRequiredLevel() {
			return level;
		}

		/**
		 * @return the animation
		 */
		public Animation getAnimation() {
			return animation;
		}
	}

	/**
	 * Represents types of tree.
	 *
	 * @author Michael
	 */
	public enum Tree {

		/**
		 * Normal tree.
		 */
		NORMAL(1511, 1, 25, 15, 1,
				new int[] { 1276, 1277, 1278, 1279, 1280, 1282, 1283, 1284, 1285, 1286, 1289, 1290, 1291, 1315, 1316,
						1318, 1319, 1330, 1331, 1332, 1365, 1383, 1384, 3033, 3034, 3035, 3036, 3881, 3882, 3883, 5902,
						5903, 5904 },
				1342, 10000),

		/**
		 * Willow tree.
		 */
		WILLOW(1519, 30, 67.5, 22, 16, new int[] { 1750, 1756, 1758, 1760 }, 1342, 8000),

		/**
		 * Oak tree.
		 */
		OAK(1521, 15, 37.5, 22, 12, new int[] { 1751 }, 1342, 9000),

		/**
		 * Magic tree.
		 */
		MAGIC(1513, 75, 250, 150, 18, new int[] { 1761 }, 1342, 4500),

		/**
		 * Maple tree.
		 */
		MAPLE(1517, 45, 100, 60, 17, new int[] { 1759 }, 1342, 7000),

		HOLLOW(3239, 45, 82.5, 60, 5, new int[] { 1757, 1752 }, 1342, 8000),

		MATURE_JUNIPER(13355, 42, 180, 60, 7, new int[] { 27499 }, 1342, 8000),

		ARCTIC_PINE(10810, 40, 310, 60, 17, new int[] { 3037 }, 1342, 8000),

		/**
		 * Mahogany tree.
		 */
		MAHOGANY(6332, 50, 125, 22, 12, new int[] { 9034 }, 1342, 10000),

		/**
		 * Teak tree.
		 */
		TEAK(6333, 35, 85, 22, 10, new int[] { 9036 }, 1342, 10000),

		/**
		 * Achey tree.
		 */
		ACHEY(2862, 1, 25, 22, 4, new int[] { 2023 }, 1342, 10000),

		/**
		 * Medium tree.
		 */
		LIGHT_JUNGLE(9010, 10, 35, 22, 6, new int[] { 6281 }, 1342, 100000),

		MEDIUM_JUNGLE(9015, 20, 45, 22, 5, new int[] { 6283 }, 1342, 100000),

		DENSE_JUNGLE(9020, 35, 55, 22, 4, new int[] { 6285 }, 1342, 100000),
		/**
		 * Yew tree.
		 */
		YEW(1515, 60, 175, 120, 16, new int[] { 1753, 7419 }, 1342, 6000),

		/**
		 * Dramen tree
		 */
		DRAMEN(771, 36, 0, 22, 4, new int[] {}, 1342, 100000),

		/**
		 * Redwood tree
		 */
		REDWOOD(19669, 90, 380, 300, 50, new int[] { 28859 }, 28860, 6000),

		/**
		 * Bruma root
		 */
		BRUMA(20695, 1, 2, 30, 110, new int[] { 29311 }, -1, 20000),

		/**
		 * Brimhaven vines
		 */
		VINE(-1, 1, 0, 2, 1, new int[] { 21731, 21732, 21733, 21734, 21735 }, -1, 150000);

		/**
		 * The object ids of this tree.
		 */
		private int[] objects;

		/**
		 * The level required to cut this tree down.
		 */
		private int level;

		/**
		 * The logging rewarded for each cut of the tree.
		 */
		private int log;

		/**
		 * The time it takes for this tree to respawn.
		 */
		private int respawnTimer;

		/**
		 * The amount of logs this tree contains.
		 */
		private int logCount;

		/**
		 * The experience granted for cutting a logging.
		 */
		private double experience;

		/**
		 * The stump id of this tree.
		 */
		private int replacement;

		/**
		 * The chance of receiving a pet.
		 */
		private int petRate;

		/**
		 * A map of object ids to trees.
		 */
		private static Map<Integer, Tree> trees = new HashMap<Integer, Tree>();

		/**
		 * Gets a tree by an object id.
		 *
		 * @param object
		 *            The object id.
		 * @return The tree, or <code>null</code> if the object is not a tree.
		 */
		public static Tree forId(int object) {
			return trees.get(object);
		}

		static {
			for (Tree tree : Tree.values()) {
				for (int object : tree.objects) {
					trees.put(object, tree);
				}
			}
		}

		/**
		 * Creates the tree.
		 *
		 * @param log
		 *            The logging id.
		 * @param level
		 *            The required level.
		 * @param experience
		 *            The experience per logging.
		 * @param objects
		 *            The object ids.
		 */
		Tree(int log, int level, double experience, int respawnTimer, int logCount, int[] objects, int replacement,
				int petRate) {
			this.objects = objects;
			this.level = level;
			this.experience = experience;
			this.respawnTimer = respawnTimer;
			this.logCount = logCount;
			this.log = log;
			this.replacement = replacement;
			this.petRate = petRate;
		}

		/**
		 * Gets the logging id.
		 *
		 * @return The logging id.
		 */
		public int getLogId() {
			return log;
		}

		/**
		 * Gets the object ids.
		 *
		 * @return The object ids.
		 */
		public int[] getObjectIds() {
			return objects;
		}

		/**
		 * Gets the required level.
		 *
		 * @return The required level.
		 */
		public int getRequiredLevel() {
			return level;
		}

		/**
		 * Gets the experience.
		 *
		 * @return The experience.
		 */
		public double getExperience() {
			return experience;
		}

		/**
		 * @return the respawnTimer
		 */
		public int getRespawnTimer() {
			return respawnTimer;
		}

		/**
		 * @return the logCount
		 */
		public int getLogCount() {
			return logCount;
		}

		/**
		 * Gets the pet rate.
		 * 
		 * @return
		 */
		public int getPetRate() {
			return petRate;
		}

		/**
		 * Gets the stump id.
		 * 
		 * @return
		 */
		public int getReplacementId() {
			return replacement;
		}
	}

	@Override
	public Animation getAnimation() {
		return hatchet.getAnimation();
	}

	@Override
	public int getCycleCount() {
		int skill = getMob().getSkills().getLevel(Skills.WOODCUTTING);
		int level = tree.level;
		int modifier = hatchet.level;
		int randomAmt = Misc.random(3);
		double cycleCount = Math.ceil((level * 50 - skill * 10) / modifier * 0.25 - randomAmt * 4);
		if (cycleCount < 1)
			cycleCount = 1;
		return (int) cycleCount + 1;
	}

	@Override
	public double getExperience() {
		int random = Misc.random(tree.getPetRate());
		double exp = tree.getExperience();

		if (random == 1) {
			Pet.Pets pets = Pet.Pets.BEAVER;
			Pet.givePet((Player) getMob(), new Item(pets.getItem()));
			return tree.getExperience();
		}
		if (getMob().isPlayer() && tree.getLogId() == 1517) {
			Player player = (Player) getMob();
			if (player.getEquipment().containsOneItem(13138, 13139, 13140))
				exp *= 1.1;
		}
		return exp;
	}

	@Override
	public GameObject getGameObject() {
		return object;
	}

	@Override
	public int getGameObjectMaxHealth() {
		return Misc.random(2, tree.getLogCount());
	}

	@Override
	public String getHarvestStartedMessage() {
		return "You swing your axe at the " + object.getDefinition().getName() + ".";
	}

	@Override
	public String getLevelTooLowMessage() {
		return "You need a " + Skills.SKILL_NAME[getSkill()] + " level of " + tree.getRequiredLevel()
				+ " to cut this tree.";
	}

	@Override
	public int getObjectRespawnTimer() {
		return tree.getRespawnTimer();
	}

	@Override
	public GameObject getReplacementObject() {

		// Special exception for brimhaven dungeon .-.
		if (object.getDefinition().getName().contains("Vine")) {
			if (getMob().isPlayer() && getMob().getWalkingQueue().isEmpty()) {
				Player player = (Player) getMob();

				int destinationX = player.getX() + (getGameObject().getX() > player.getX() ? +2
						: (getGameObject().getX() < player.getX() ? -2 : 0));

				int destinationY = player.getY() + (getGameObject().getY() > player.getY() ? +2
						: (getGameObject().getY() < player.getY() ? -2 : 0));

				// player.getWalkingQueue().addStep(destinationX, destinationY);
				Agility.forceWalkingQueue(player, player.getWalkAnimation(), destinationX, destinationY, 0, 2, true);
				this.stop();
			}
			return null;
		}
		if (tree.getReplacementId() == -1)
			return null;
		return new GameObject(getGameObject().getLocation(), 1342, tree.getReplacementId(), object.getDirection(), false);
	}

	@Override
	public int getRequiredLevel() {
		return tree.getRequiredLevel();
	}

	@Override
	public Item getReward() {
		if (object.getDefinition().getName().contains("Vine"))
			return null;
		if (hatchet == Hatchet.INFERNAL && Misc.random(8) == 0 && tree.getLogId() != 20695) {
			getMob().getSkills().addExperience(Skills.FIREMAKING, tree.getExperience() / 2);
			getMob().playGraphics(Graphic.create(86));
			return null;
		}
		int amount = 1;
		if (getMob().isPlayer() && tree.getLogId() == 1511) {
			Player player = (Player) getMob();
			if (player.getEquipment().containsOneItem(13137, 13138, 13139, 13140)) {
				if (Misc.random(5) < 4)
					amount = 2;
			}
		}
		return new Item(tree.getLogId(), amount);
	}

	@Override
	public int getSkill() {
		return Skills.WOODCUTTING;
	}

	@Override
	public String getSuccessfulHarvestMessage() {
		if (object.getDefinition().getName().contains("Vine"))
			return null;
		int birdsNest = 1;
		if (getMob().getEquipment().containsOneItem(9808, 9808) || Constants.hasMaxCape(getMob()))
			birdsNest = 3;
		Item birdNest = new Item(Misc.random(10) == 0 ? 5070
				: Misc.random(10) == 0 ? 5071 : Misc.random(10) == 0 ? 5072 : Misc.random(8) == 0 ? 5073 : 5074);
		if (Misc.random(266) <= birdsNest && tree.getLogId() != 20695) {
			World.getWorld().register(new GroundItem(((Player) getMob()).getName(), birdNest, getMob().getLocation()),
					getMob());
			getMob().getActionSender().sendMessage("<col=ff0000>A bird's nest falls out of the tree.");
		}
		if (tree.getLogId() == 20695)
			return null;
		return "You get some " + CacheItemDefinition.get(tree.getLogId()).getName().toLowerCase() + ".";
	}

	@Override
	public boolean canHarvest() {
		for (Hatchet hatchet : Hatchet.values()) {
			if ((getMob().getInventory().contains(hatchet.getId()) || getMob().getEquipment().contains(hatchet.getId()))
					&& getMob().getSkills().getLevelForExperience(getSkill()) >= hatchet.getRequiredLevel()) {
				this.hatchet = hatchet;
				break;
			}
		}
		if (hatchet == null) {
			getMob().getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE, -1, null,
					"You do not have an axe that you can use.");
			getMob().getActionSender().sendMessage("You do not have an axe that you can use.");
			return false;
		}
		return true;
	}

	@Override
	public String getInventoryFullMessage() {
		return "Your inventory is too full to hold any more "
				+ CacheItemDefinition.get(tree.getLogId()).getName().toLowerCase() + ".";
	}
}