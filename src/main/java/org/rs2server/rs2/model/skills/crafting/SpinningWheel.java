package org.rs2server.rs2.model.skills.crafting;

import org.rs2server.cache.format.CacheItemDefinition;
import org.rs2server.rs2.action.impl.ProductionAction;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.player.Player;

import java.util.HashMap;
import java.util.Map;

public class SpinningWheel extends ProductionAction {

	private final Animation ANIMATION = Animation.create(894);

	public enum SpinItem {

		// used item, new item, LEVEL, XP
		BALL_OF_WOOL(1737, 1759, 1, 15),

		BOW_STRING(1779, 1777, 10, 25),

		MAGIC_AMULET_STRING(6051, 6038, 19, 30),

		CROSSBOW_STRING(9436, 9438, 10, 25),

		ROPE(10814, 954, 30, 50);

		private int remove;
		private int add;
		private int requiredLevel;
		private int xp;

		SpinItem(int remove, int add, int requiredLevel, int xp) {
			this.remove = remove;
			this.add = add;
			this.requiredLevel = requiredLevel;
			this.xp = xp;
		}

		private static Map<Integer, SpinItem> items = new HashMap<Integer, SpinItem>();

		public static SpinItem forId(int item) {
			return items.get(item);
		}

		static {
			for (SpinItem item : SpinItem.values()) {
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

		public int getXP() {
			return xp;
		}
	}

	private SpinItem item;
	private int amount;

	public SpinningWheel(Mob mob, SpinItem item, int amount) {
		super(mob);
		this.item = item;
		this.amount = amount;
	}

	@Override
	public int getCycleCount() {
		int speed = 2;
		if (getMob().isPlayer()) {
			Player player = (Player) getMob();
			if (player.getEquipment().containsOneItem(13138, 13139, 13140))
				speed = 1;
		}
		return speed;
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
		return "You need a Crafting level of " + item.getRequiredLevel() + " to craft this item.";
	}

	@Override
	public String getSuccessfulProductionMessage() {
		return CacheItemDefinition.get(item.getAdd()) != null
				? "You successfully craft a " + CacheItemDefinition.get(item.getAdd()).getName() : "";
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
		return child == 100 || child == 95 || child == 107 || child == 121 || child == 114 || child == 128 ? 1
				: child == 99 || child == 94 || child == 106 || child == 120 || child == 113 || child == 127 ? 5
						: child == 98 || child == 93 || child == 105 || child == 119 || child == 112 || child == 126
								? 10
								: child == 97 || child == 92 || child == 104 || child == 118 || child == 112
										|| child == 125 ? 28 : -1;
	}
}