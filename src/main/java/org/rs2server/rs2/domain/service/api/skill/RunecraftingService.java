package org.rs2server.rs2.domain.service.api.skill;

import org.rs2server.cache.format.CacheItemDefinition;
import org.rs2server.rs2.action.impl.HarvestingAction;
import org.rs2server.rs2.action.impl.ProductionAction;
import org.rs2server.rs2.content.api.GameObjectActionEvent;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.npc.Pet;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.skills.Mining;
import org.rs2server.rs2.util.Misc;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author Clank1337
 */
public interface RunecraftingService {

	enum PouchType {

		SMALL_POUCH(5509, 1, 3),

		MEDIUM_POUCH(5510, 25, 6),

		LARGE_POUCH(5512, 50, 9),

		GIANT_POUCH(5514, 75, 12);

		private final int itemId;
		private final int levelReq;
		private final int capacity;

		PouchType(int itemId, int levelReq, int capacity) {
			this.itemId = itemId;
			this.levelReq = levelReq;
			this.capacity = capacity;
		}

		private static Map<Integer, PouchType> pouchItems = new HashMap<>();

		public static PouchType of(int id) {
			return pouchItems.get(id);
		}

		static {
			for (PouchType pouchItem : PouchType.values()) {
				pouchItems.put(pouchItem.getItemId(), pouchItem);
			}
		}

		public int getItemId() {
			return itemId;
		}

		public int getLevelReq() {
			return levelReq;
		}

		public int getCapacity() {
			return capacity;
		}

	}

	enum MysteriousAltarType {

		MYSTERIOUS_ALTAR_AIR(14989, Location.create(2841, 4830), 1438),

		MYSTERIOUS_ALTAR_MIND(14990, Location.create(2793, 4829), 1448),

		MYSTERIOUS_ALTAR_WATER(14991, Location.create(2725, 4833), 1444),

		MYSTERIOUS_ALTAR_EARTH(14992, Location.create(2657, 4830), 1440),

		MYSTERIOUS_ALTAR_FIRE(14993, Location.create(2576, 4849), 1442),

		MYSTERIOUS_ALTAR_BODY(14994, Location.create(2521, 4835), 1446),

		MYSTERIOUS_ALTAR_COSMIC(15608, Location.create(2162, 4833), 1454),

		MYSTERIOUS_ALTAR_CHAOS(15611, Location.create(2281, 4837), 1452),

		MYSTERIOUS_ALTAR_NATURE(15610, Location.create(2400, 4835), 1462),

		MYSTERIOUS_ALTAR_LAW(15609, Location.create(2464, 4818), 1458);

		private final int objectId;
		private final Location location;
		private final int talisman;

		MysteriousAltarType(int objectId, Location location, int talisman) {
			this.objectId = objectId;
			this.location = location;
			this.talisman = talisman;
		}

		public int getObjectId() {
			return objectId;
		}

		public Location getLocation() {
			return location;
		}

		public int getTalisman() {
			return talisman;
		}

		private static Map<Integer, MysteriousAltarType> mysteriousAltars = new HashMap<>();

		public static MysteriousAltarType of(int objectId) {
			return mysteriousAltars.get(objectId);
		}

		static {
			for (MysteriousAltarType altar : MysteriousAltarType.values()) {
				mysteriousAltars.put(altar.getObjectId(), altar);
			}
		}
	}

	enum AltarType {

		AIR_ALTAR(14897, 1, 556, 13, false, 6000, new int[] { 1, 11, 22, 33, 44, 55, 66, 77, 88, 99 }),

		MIND_ALTAR(14898, 2, 558, 13.5, false, 5750, new int[] { 1, 14, 28, 42, 56, 70, 84, 98 }),

		WATER_ALTAR(14899, 5, 555, 14, false, 5500, new int[] { 1, 19, 38, 57, 76, 95 }),

		EARTH_ALTAR(14900, 9, 557, 14.5, false, 5250, new int[] { 1, 26, 52, 78 }),

		FIRE_ALTAR(14901, 14, 554, 15, false, 5000, new int[] { 1, 35, 70 }),

		BODY_ALTAR(14902, 20, 559, 15.5, false, 4750, new int[] { 1, 46, 92 }),

		COSMIC_ALTAR(14903, 27, 564, 19, true, 4500, new int[] { 1, 59 }),

		CHAOS_ALTAR(14906, 35, 562, 19.5, true, 4250, new int[] { 1, 74 }),

		ASTRAL_ALTAR(14911, 40, 9075, 19.7, true, 4000, new int[] { 1, 82 }),

		NATURE_ALTAR(14905, 44, 561, 20, true, 3750, new int[] { 1, 91 }),

		LAW_ALTAR(14904, 54, 563, 20.5, true, 3500, new int[] { 1 }),

		BLOOD_ALTAR(27978, 77, 565, 23.5, true, 3250, new int[] { 1 }),

		SOUL_ALTAR(27980, 90, 566, 25.5, true, 3000, new int[] { 1 }),

		DEATH_ALTAR(14907, 65, 560, 20.5, true, 2750, new int[] { 1 });

		private final int objectId;
		private final int levelReq;
		private final int runeId;
		private final double xp;
		private final boolean pure;
		private final int petRate;
		private final int[] multiples;

		AltarType(int objectId, int levelReq, int runeId, double xp, boolean pure, int petRate, int[] multiples) {
			this.objectId = objectId;
			this.levelReq = levelReq;
			this.runeId = runeId;
			this.xp = xp;
			this.pure = pure;
			this.petRate = petRate;
			this.multiples = multiples;
		}

		private static Map<Integer, AltarType> altars = new HashMap<>();

		public static AltarType of(int objectId) {
			return altars.get(objectId);
		}

		static {
			for (AltarType altar : AltarType.values()) {
				altars.put(altar.getObjectId(), altar);
			}
		}

		public int getObjectId() {
			return objectId;
		}

		public int getLevelReq() {
			return levelReq;
		}

		public int getRuneId() {
			return runeId;
		}

		public int[] getMultiples() {
			return multiples;
		}

		public double getXp() {
			return xp;
		}

		public boolean isPure() {
			return pure;
		}

		public int getPetRate() {
			return petRate;
		}
	}

	enum EssenceMine {

		RUNE_ESSENCE_OBJECT(7471),

		RUNE_ESSENCE_PORTAL(7479);

		private final int objectId;

		EssenceMine(int objectId) {
			this.objectId = objectId;
		}

		private static Map<Integer, EssenceMine> essenceMineObjects = new HashMap<>();

		public static EssenceMine of(int objectId) {
			return essenceMineObjects.get(objectId);
		}

		static {
			for (EssenceMine object : EssenceMine.values()) {
				essenceMineObjects.put(object.getObjectId(), object);
			}
		}

		public int getObjectId() {
			return objectId;
		}
	}

	class RuneCraftingAction extends ProductionAction {

		private final Player player;
		private final AltarType type;

		private static final int RUNE_ESSENCE = 1436;
		private static final int PURE_ESSENCE = 7936;
		private static final Animation ANIMATION = Animation.create(791);
		private static final Graphic GRAPHICS = Graphic.create(186, 0, 100);
		private int productionCount;

		public RuneCraftingAction(Player player, AltarType type) {
			super(player);
			this.player = player;
			this.type = type;
			this.productionCount = player.getInventory().getCount(RUNE_ESSENCE);
			if (productionCount <= 0 || type.isPure())
				this.productionCount = player.getInventory().getCount(PURE_ESSENCE);
		}

		public int getMultiplier() {
			int i = 0;
			for (int level : type.getMultiples()) {
				if (player.getSkills().getLevelForExperience(Skills.RUNECRAFTING) >= level)
					i++;
			}
			return i != 0 ? i : 1;
		}

		@Override
		public int getCycleCount() {
			return 3;
		}

		@Override
		public int getProductionCount() {
			return 1;
		}

		@Override
		public Item[] getRewards() {
			handlePet(player, getPetRate() - productionCount);
			return new Item[] { new Item(type.getRuneId(), productionCount * getMultiplier()) };
		}

		@Override
		public Item[] getConsumedItems() {
			return new Item[] { new Item(
					type.isPure() ? PURE_ESSENCE
							: player.getInventory().getCount(RUNE_ESSENCE) > 0 ? RUNE_ESSENCE : PURE_ESSENCE,
					productionCount) };
		}

		@Override
		public int getSkill() {
			return Skills.RUNECRAFTING;
		}

		@Override
		public int getRequiredLevel() {
			return type.getLevelReq();
		}

		@Override
		public double getExperience() {
			return type.getXp() * productionCount;
		}

		@Override
		public String getLevelTooLowMessage() {
			return "You need a Runecrafting level of " + type.getLevelReq() + " to craft this.";
		}

		@Override
		public String getSuccessfulProductionMessage() {
			return "You bind the temple's power into " + CacheItemDefinition.get(type.getRuneId()).getName() + "s.";
		}

		@Override
		public Animation getAnimation() {
			return ANIMATION;
		}

		@Override
		public Graphic getGraphic() {
			return GRAPHICS;
		}

		@Override
		public boolean canProduce() {
			return productionCount > 0;
		}

		@Override
		public boolean isSuccessfull() {
			return true;
		}

		@Override
		public String getFailProductionMessage() {
			return null;
		}

		@Override
		public Item getFailItem() {
			return null;
		}

		public int getPetRate() {
			return type.getPetRate();
		}

		/**
		 * Handles the rare chance of obtaining a skilling pet
		 * 
		 * @param player
		 *            the player
		 * @param dropRate
		 *            the chance
		 */
		private void handlePet(Player player, int dropRate) {
			int random = Misc.random(dropRate);
			if (random == 1000) {
				Pet.Pets pets = Pet.Pets.RIFT_GUARDIAN_V;
				Pet.givePet(player, new Item(pets.getItem()));
			}
		}
	}

	class EssenceMiningAction extends HarvestingAction {

		private final GameObject object;
		private Mining.PickAxe pickaxe;
		private final Random random = new Random();

		private static final int ESSENCE_XP = 1;
		private int essenceId = 1436;

		public EssenceMiningAction(final Mob mob, final GameObject object) {
			super(mob);
			this.object = object;
			if (mob.getSkills().getLevelForExperience(Skills.MINING) >= 30) {
				essenceId = 7936;
			}
		}

		@Override
		public void onSuccessfulHarvest(final Item item) {
		}

		@Override
		public Animation getAnimation() {
			return pickaxe.getAnimation();
		}

		@Override
		public int getCycleCount() {
			int level = getMob().getSkills().getLevel(Skill.MINING.getId());
			int diff = level / 10;
			int delay = (10 - diff);
			if (delay <= 2) {
				delay = 2;
			}
			if (delay > 5) {
				delay = 5;
			}
			return ((random.nextInt(delay) + 1) + random.nextInt(3));
		}

		@Override
		public double getExperience() {
			return ESSENCE_XP;
		}

		@Override
		public GameObject getGameObject() {
			return object;
		}

		@Override
		public int getGameObjectMaxHealth() {
			return Integer.MAX_VALUE;
		}

		@Override
		public String getHarvestStartedMessage() {
			return "You swing your pick at the rock.";
		}

		@Override
		public String getLevelTooLowMessage() {
			return "You need a " + Skills.SKILL_NAME[getSkill()] + " level of " + getRequiredLevel()
					+ " to mine this ore.";
		}

		@Override
		public int getObjectRespawnTimer() {
			return 0;
		}

		@Override
		public GameObject getReplacementObject() {
			final GameObject object = getGameObject();
			return new GameObject(object.getLocation(), 26666, object.getType(), object.getDirection(), false);
		}

		@Override
		public int getRequiredLevel() {
			return 1;
		}

		@Override
		public Item getReward() {
			return new Item(essenceId, 1);
		}

		@Override
		public int getSkill() {
			return Skills.MINING;
		}

		@Override
		public String getSuccessfulHarvestMessage() {
			return "You manage to mine some rune essence.";
		}

		@Override
		public boolean canHarvest() {
			for (Mining.PickAxe pickaxe : Mining.PickAxe.values()) {
				if ((getMob().getInventory().contains(pickaxe.getId())
						|| getMob().getEquipment().contains(pickaxe.getId()))
						&& getMob().getSkills().getLevelForExperience(getSkill()) >= pickaxe.getRequiredLevel()) {
					this.pickaxe = pickaxe;
					break;
				}
			}
			if (pickaxe == null) {
				getMob().getActionSender().sendMessage("You do not have a pickaxe that you can use.");
				return false;
			}
			return true;
		}

		@Override
		public String getInventoryFullMessage() {
			return "Your inventory is full.";
		}
	}

	void depositEssenceInPouch(@Nonnull Player player, @Nonnull PouchType type);

	void claimEssenceInPouch(@Nonnull Player player, @Nonnull PouchType type);

	void checkEssenceInPouch(@Nonnull Player player, @Nonnull PouchType type);

	void handleMysteriousAltarInteraction(@Nonnull Player player, @Nonnull MysteriousAltarType type,
			@Nonnull GameObjectActionEvent clickEvent);

	void handleAltarInteraction(@Nonnull Player player, @Nonnull AltarType type);

	void handleEssenceMineInteraction(@Nonnull Player player, @Nonnull EssenceMine mine,
			@Nonnull GameObjectActionEvent clickEvent);

	boolean isPouchFree(@Nonnull Player player, @Nonnull PouchType type);

	boolean isPouchEmpty(@Nonnull Player player, @Nonnull PouchType type);

}
