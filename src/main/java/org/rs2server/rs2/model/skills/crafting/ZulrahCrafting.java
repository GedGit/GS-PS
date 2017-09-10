package org.rs2server.rs2.model.skills.crafting;

import org.rs2server.rs2.action.impl.ProductionAction;
import org.rs2server.rs2.model.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Clank1337
 */
public class ZulrahCrafting extends ProductionAction {

	private final ZulrahItems zulrahItems;

	public static final Animation CRAFT_ANIMATION = Animation.create(1249);

	/**
	 * Creates the production action for the specified mob.
	 *
	 * @param mob The mob to create the action for.
	 */
	public ZulrahCrafting(Mob mob, ZulrahItems zulrahItems) {
		super(mob);
		this.zulrahItems = zulrahItems;
	}


	public enum ZulrahItems {

		TANZANITE_FANG(1755, CRAFT_ANIMATION, 12922, 12924, 53, Skills.FLETCHING, 120),

		SERPENTINE_VISAGE(1755, CRAFT_ANIMATION, 12927, 12929, 52, Skills.CRAFTING, 120);

		private int requiredItem;
		private Animation animation;
		private int consumed;
		private int reward;
		private int levelReq;
		private int skill;
		private int xp;

		ZulrahItems(int requiredItem, Animation animation, int consumed, int reward, int levelReq, int skill, int xp) {
			this.requiredItem = requiredItem;
			this.animation = animation;
			this.consumed = consumed;
			this.reward = reward;
			this.levelReq = levelReq;
			this.skill = skill;
			this.xp = xp;
		}

		private static Map<Integer, ZulrahItems> zulrahItemsMap = new HashMap<>();

		public static ZulrahItems of(int id) { return zulrahItemsMap.get(id);}

		static {
			for (ZulrahItems zulrahItem : ZulrahItems.values()) {
				zulrahItemsMap.put(zulrahItem.getConsumed(), zulrahItem);
			}
		}

		public int getConsumed() {
			return consumed;
		}

		public int getReward() {
			return reward;
		}

		public int getLevelReq() {
			return levelReq;
		}

		public int getXp() {
			return xp;
		}

		public int getRequiredItem() {
			return requiredItem;
		}

		public int getSkill() {
			return skill;
		}

		public Animation getAnimation() {
			return animation;
		}
	}

	@Override
	public int getCycleCount() {
		return 4;
	}

	@Override
	public int getProductionCount() {
		return 1;
	}

	@Override
	public Item[] getRewards() {
		return new Item[] {new Item(zulrahItems.getReward())};
	}

	@Override
	public Item[] getConsumedItems() {
		return new Item[] {new Item(zulrahItems.getConsumed())};
	}

	@Override
	public int getSkill() {
		return zulrahItems.getSkill();
	}

	@Override
	public int getRequiredLevel() {
		return zulrahItems.getLevelReq();
	}

	@Override
	public double getExperience() {
		return zulrahItems.getXp();
	}

	@Override
	public String getLevelTooLowMessage() {
		if (zulrahItems.getConsumed() == 12922)
			return "You need a Fletching level of " + zulrahItems.getLevelReq() + " to create this item.";
		return "You need a Crafting level of " + zulrahItems.getLevelReq() + " to create this item.";
	}

	@Override
	public String getSuccessfulProductionMessage() {
		return "";
	}

	@Override
	public Animation getAnimation() {
		return zulrahItems.getAnimation();
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
}
