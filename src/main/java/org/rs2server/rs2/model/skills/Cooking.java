package org.rs2server.rs2.model.skills;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.action.impl.ProductionAction;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;

import java.util.HashMap;
import java.util.Random;

public class Cooking extends ProductionAction {

	public static HashMap<Integer, CookingItem> itemMap = new HashMap<Integer, CookingItem>();
	private static HashMap<Integer, CookingMethod> objectMap = new HashMap<Integer, CookingMethod>();

	static {
		for (CookingItem item : CookingItem.values())
			for (Item ingr : item.getIngredients())
				itemMap.put(ingr.getId(), item);

		for (CookingMethod method : CookingMethod.values())
			for (int id : method.getObjects())
				objectMap.put(id, method);
	}

	public enum CookingMethod {

		STOVE(Animation.create(883), new int[] { 114, 2728, 2729, 2730, 2731, 4488, 14919, 26181, 27724 }),

		FIRE(Animation.create(897), new int[] { 5249, 26185, 3769, 29300 }),

		FIRE_PLACE(Animation.create(899), new int[] { 2724, 2725, 2726 });

		private final Animation animation;
		private final int[] objIds;

		CookingMethod(Animation animation, int[] objIds) {
			this.animation = animation;
			this.objIds = objIds;
		}

		public Animation getAnimation() {
			return animation;
		}

		public int[] getObjects() {
			return objIds;
		}
	}

	public enum CookingItem {

		RAW_REDBERRY_PIE(10, new Item[] { new Item(2321) }, new Item(2329), 78, new Item(2325), false),

		RAW_MEAT_PIE(20, new Item[] { new Item(2319) }, new Item(2329), 110, new Item(2327), false),

		RAW_MUD_PIE(29, new Item[] { new Item(7168) }, new Item(2329), 128, new Item(7170), false),

		RAW_APPLE_PIE(30, new Item[] { new Item(2317) }, new Item(2329), 130, new Item(2323), false),

		RAW_GARDEN_PIE(34, new Item[] { new Item(7176) }, new Item(2329), 138, new Item(7178), false),

		RAW_FISH_PIE(47, new Item[] { new Item(7186) }, new Item(2329), 164, new Item(7188), false),

		RAW_ADMIRAL_PIE(70, new Item[] { new Item(7196) }, new Item(2329), 210, new Item(7198), false),

		RAW_WILD_PIE(85, new Item[] { new Item(7206) }, new Item(2329), 240, new Item(7208), false),

		RAW_SUMMER_PIE(95, new Item[] { new Item(7216) }, new Item(2329), 260, new Item(7218), false),

		RAW_FISHCAKE(31, new Item[] { new Item(7529) }, new Item(7531), 100, new Item(7530), false),

		RAW_POTATO(7, new Item[] { new Item(1942) }, new Item(6699), 15, new Item(6701), false),

		RAW_SHRIMPS(1, new Item[] { new Item(317) }, new Item(323), 30, new Item(315), true),

		RAW_SARDINE(1, new Item[] { new Item(327) }, new Item(323), 40, new Item(325), true),

		RAW_ANCHOVIES(1, new Item[] { new Item(321) }, new Item(323), 30, new Item(319), true),

		POISON_KARAMBWAN(1, new Item[] { new Item(3142) }, new Item(3148), 80, new Item(3151), true),

		RAW_HERRING(5, new Item[] { new Item(345) }, new Item(357), 50, new Item(347), true),

		RAW_MACKEREL(10, new Item[] { new Item(353) }, new Item(357), 60, new Item(355), true),

		RAW_TROUT(15, new Item[] { new Item(335) }, new Item(343), 70, new Item(333), true),

		RAW_COD(18, new Item[] { new Item(341) }, new Item(343), 75, new Item(339), true),

		RAW_PIKE(20, new Item[] { new Item(349) }, new Item(343), 80, new Item(351), true),

		RAW_SALMON(25, new Item[] { new Item(331) }, new Item(343), 90, new Item(329), true),

		RAW_SLIMY_EEL(28, new Item[] { new Item(3379) }, new Item(3383), 95, new Item(3381), true),

		RAW_TUNA(30, new Item[] { new Item(359) }, new Item(367), 100, new Item(361), true),

		RAW_RAINBOW_FISH(35, new Item[] { new Item(10138) }, new Item(10140), 110, new Item(10136), true),

		RAW_CAVE_EEL(38, new Item[] { new Item(5001) }, new Item(5006), 115, new Item(5003), true),

		RAW_LOBSTER(40, new Item[] { new Item(377) }, new Item(381), 120, new Item(379), true),

		RAW_BASS(43, new Item[] { new Item(363) }, new Item(367), 130, new Item(365), true),

		RAW_SWORDFISH(45, new Item[] { new Item(371) }, new Item(375), 140, new Item(373), true),

		RAW_LAVA_EEL(53, new Item[] { new Item(2148) }, new Item(-1), 140, new Item(2149), true),

		RAW_MONKFISH(62, new Item[] { new Item(7944) }, new Item(7948), 150, new Item(7946), true),

		RAW_SHARK(80, new Item[] { new Item(383) }, new Item(387), 210, new Item(385), true),

		RAW_SEA_TURTLE(82, new Item[] { new Item(395) }, new Item(399), 212, new Item(397), true),

		RAW_CAVEFISH(88, new Item[] { new Item(15264) }, new Item(15268), 214, new Item(15266), true),

		RAW_YAK_MEAT(1, new Item[] { new Item(15264) }, new Item(15268), 214, new Item(15266), true),

		RAW_MANTA_RAY(91, new Item[] { new Item(389) }, new Item(393), 200, new Item(391), true),

		RAW_ANGLERFISH(84, new Item[] { new Item(13439) }, new Item(13443), 230, new Item(13441), true),

		RAW_KARAMBWAN(30, new Item[] { new Item(3142) }, new Item(3148), 90, new Item(3144), true),

		RAW_DARK_CRAB(90, new Item[] { new Item(11934) }, new Item(11938), 225, new Item(11936), true),

		RAW_RAT_MEAT(1, new Item[] { new Item(2134) }, new Item(2146), 30, new Item(2124), true),

		RAW_BEAR_MEAT(1, new Item[] { new Item(2134) }, new Item(2146), 30, new Item(2124), true),

		RAW_BEEF(1, new Item[] { new Item(2132) }, new Item(2146), 30, new Item(2124), true),

		RAW_CHICKEN(1, new Item[] { new Item(1) }, new Item(1), 30, new Item(1), true),

		STEW(25, new Item[] { new Item(1997) }, new Item(2005), 117, new Item(2003), true),

		SPICY_STEW(25, new Item[] { new Item(1999) }, new Item(2005), 117, new Item(7479), true),

		CURRY(60, new Item[] { new Item(2009) }, new Item(2013), 280, new Item(2011), true);

		private final Item[] ingredients;
		private final Item product;
		private final int level;
		private final int endXp;
		private final Item burntProduct;
		private boolean canCookOnFire;

		CookingItem(int level, Item[] ingredients, Item burntProduct, int endXp, Item product, boolean canCookOnFire) {
			this.level = level;
			this.ingredients = ingredients;
			this.product = product;
			this.endXp = endXp;
			this.burntProduct = burntProduct;
			this.canCookOnFire = canCookOnFire;
		}

		public Item getProduct() {
			return product;
		}

		public Item[] getIngredients() {
			return ingredients;
		}

		public int getLevel() {
			return level;
		}

		public int getEndXp() {
			return (int) endXp;
		}

		public Item getBurntProduct() {
			return burntProduct;
		}

		public boolean canCookOnFire() {
			return canCookOnFire;
		}

	}

	private static final String[] MESSAGES = new String[] { "You have run out of ingredients!",
			"You successfully cook the ", "You accidentally burn the ",
			"You do not have the required level to cook this!" };

	private CookingItem item;
	private CookingMethod method;

	private int cycles;
	private int productionAmt;

	public Cooking(Mob mob, int ticks, int productionAmount, CookingItem item, CookingMethod method) {
		super(mob);
		this.cycles = ticks;
		this.productionAmt = productionAmount;
		this.method = method;
		this.item = item;
		if (method == CookingMethod.FIRE)
			cycles = 4;
		else
			cycles = 3;
	}

	@Override
	public Item[] getRewards() {
		return new Item[] { item.getProduct() };
	}

	@Override
	public Item[] getConsumedItems() {
		return item.getIngredients();
	}

	@Override
	public int getRequiredLevel() {
		return item.getLevel();
	}

	@Override
	public int getSkill() {
		return Skills.COOKING;
	}

	@Override
	public double getExperience() {
		GameObject method = getMob().getInterfaceAttribute("cookObject");
		if (World.getWorld().getRegionManager().getGameObject(method.getLocation(), method.getId()) == null)
			this.stop();
		return item.getEndXp();
	}

	@Override
	public String getLevelTooLowMessage() {
		return MESSAGES[3] + ".";
	}

	@Override
	public String getSuccessfulProductionMessage() {
		return MESSAGES[1] + item.getProduct().getDefinition2().getName() + ".";
	}

	@Override
	public String getFailProductionMessage() {
		return MESSAGES[2] + item.getProduct().getDefinition2().getName() + ".";
	}

	@Override
	public Animation getAnimation() {
		return method.getAnimation();
	}

	@Override
	public Graphic getGraphic() {
		return null;
	}

	/**
	 * Gets the approximate level to endGame burning a specific cooking item.
	 *
	 * @return The approximate level to endGame burning the cooking item provided.
	 */
	private int getStopBurningLevel(Item reward) {
		switch (reward.getId()) {
		case 391:
		case 11936:
		case 13441:
			return 110;
		case 385:
			return 99;
		case 373:
		case 3144:
			return 70;
		case 365:
			return 64;
		case 379:
			return 61;
		case 361:
			return 52;
		case 329:
			return 47;
		case 351:
			return 42;
		case 339:
			return 40;
		case 333:
			return 37;
		case 355:
			return 32;
		case 347:
			return 30;
		case 325:
		case 315:
		case 319:
			return 25;
		}
		return item.getLevel() + 24;
	}

	@Override
	public boolean isSuccessfull() {
		int playerLevel = getMob().getSkills().getLevel(Skills.COOKING);
		int noBurnLevel = getStopBurningLevel(item.getProduct());
		boolean shouldBurn = (playerLevel < noBurnLevel) && shouldBurn(playerLevel, noBurnLevel);
		if (Constants.hasMaxCape(getMob()) || getMob().getEquipment().containsOneItem(9801, 9802) || !shouldBurn)
			return true;
		return false;
	}

	public static CookingItem getCookingItem(int itemId) {
		return itemMap.get(itemId);
	}

	public static boolean canCook(CookingMethod method, CookingItem item) {
		if (method.equals(CookingMethod.FIRE) && !item.canCookOnFire()) {
			return false;
		} else {
			return true;
		}
	}

	public static CookingMethod getCookingMethod(GameObject obj) {
		if (objectMap.containsKey(obj.getId()))
			return objectMap.get(obj.getId());
		else {
			if (obj.getDefinition().getName().equals("Stove"))
				return CookingMethod.STOVE;
		}
		return null;
	}

	public static CookingMethod getCookingMethod(int objId) {
		return objectMap.get(objId);
	}

	/**
	 * Calculates the chance of burning any item.
	 *
	 * @param playerLevel
	 *            The current cooking level of the Player.
	 * @param noBurnLevel
	 *            The level to endGame burning the food, the player is currently
	 *            cooking.
	 * @return True if the food should burn, false if not.
	 */
	public static boolean shouldBurn(int playerLevel, int noBurnLevel) {
		int levelsToStopBurn = noBurnLevel - playerLevel;
		if (levelsToStopBurn > 20) {
			levelsToStopBurn = 20; // Makes the chance of burning approximatly
									// 60%.
		}
		Random r = new Random();
		return r.nextInt(38) <= levelsToStopBurn;
	}

	public boolean wearingGauntlets() {
		return getMob().getEquipment().getById(775) != null;
	}

	@Override
	public Item getFailItem() {
		return item.getBurntProduct();
	}

	@Override
	public int getCycleCount() {
		return cycles;
	}

	@Override
	public int getProductionCount() {
		return productionAmt;
	}

	@Override
	public boolean canProduce() {
		return canCook(method, item);
	}

}
