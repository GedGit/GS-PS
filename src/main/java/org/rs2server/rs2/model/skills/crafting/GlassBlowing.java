package org.rs2server.rs2.model.skills.crafting;

import org.rs2server.cache.format.CacheItemDefinition;
import org.rs2server.rs2.action.impl.ProductionAction;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.util.Misc;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles everything related to glassblowing.
 * 
 * @author Vichy
 */
public class GlassBlowing extends ProductionAction {

	/**
	 * The animation
	 */
	private final Animation ANIMATION = Animation.create(884);

	public enum GlassItem {

		// used item, new item, LEVEL, XP
		BEER_GLASS(1775, 1919, 1, 17),

		CANDLE_LANTERN(1775, 4529, 4, 19),

		OIL_LAMP(1775, 4522, 12, 25),

		VIAL(1775, 229, 33, 35),

		FISH_BOWL(1775, 6668, 42, 42.5),

		ORB(1775, 567, 46, 52.5),

		LANTERN_LENS(1775, 4542, 49, 55),

		LIGHT_ORB(1775, 10973, 87, 70);

		private int remove;
		private int add;
		private int requiredLevel;
		private double xp;

		GlassItem(int remove, int add, int requiredLevel, double xp) {
			this.remove = remove;
			this.add = add;
			this.requiredLevel = requiredLevel;
			this.xp = xp;
		}

		private static Map<Integer, GlassItem> items = new HashMap<Integer, GlassItem>();

		public static GlassItem forId(int item) {
			return items.get(item);
		}

		static {
			for (GlassItem item : GlassItem.values()) {
				items.put(item.remove, item);
			}
		}

		public int getRemove() {
			return remove;
		}

		public int getAdd() {
			return add;
		}

		public int getRequiredLevel() {
			return requiredLevel;
		}

		public double getXP() {
			return xp;
		}
	}

	private GlassItem item;
	private int amount;

	public GlassBlowing(Mob mob, GlassItem item, int amount) {
		super(mob);
		this.item = item;
		this.amount = amount;
	}

	@Override
	public int getCycleCount() {
		return 2;
	}

	@Override
	public int getProductionCount() {
		return amount;
	}

	@Override
	public Item[] getRewards() {
		return new Item[] { new Item(item.getAdd()) };
	}

	@Override
	public Item[] getConsumedItems() {
		return new Item[] { new Item(item.getRemove()) };
	}

	@Override
	public int getSkill() {
		return Skills.CRAFTING;
	}

	@Override
	public int getRequiredLevel() {
		return item.getRequiredLevel();
	}

	@Override
	public double getExperience() {
		return item.getXP();
	}

	@Override
	public String getLevelTooLowMessage() {
		return "You need a Crafting skill of " + item.getRequiredLevel() + " to make "
				+ Misc.withPrefix(CacheItemDefinition.get(item.getAdd()).getName()) + ".";
	}

	@Override
	public String getSuccessfulProductionMessage() {
		return CacheItemDefinition.get(item.getAdd()) != null
				? "You successfully craft a " + CacheItemDefinition.get(item.getAdd()).getName()
				: "";
	}

	@Override
	public Animation getAnimation() {
		return ANIMATION;
	}

	@Override
	public Graphic getGraphic() {
		return null;
	}

	@Override
	public boolean canProduce() {
		return true;
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

	public static int getAmount(int child) {

		if (child == 120 || child == 136 || child == 124 || child == 116 || child == 128 || child == 132 || child == 112
				|| child == 144)
			return 1;

		if (child == 119 || child == 123 || child == 127 || child == 111 || child == 135 || child == 115 || child == 131
				|| child == 143)
			return 5;

		if (child == 118 || child == 122 || child == 126 || child == 110 || child == 134 || child == 114 || child == 130
				|| child == 142)
			return 10;

		if (child == 117 || child == 121 || child == 125 || child == 109 || child == 133 || child == 113 || child == 129
				|| child == 141)
			return 27;

		return -1;
	}
}