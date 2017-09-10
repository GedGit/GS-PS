package org.rs2server.rs2.model.skills.crafting;

import org.rs2server.cache.format.CacheItemDefinition;
import org.rs2server.rs2.action.impl.ProductionAction;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Skills;

import java.util.HashMap;
import java.util.Map;

public class GemCutting extends ProductionAction {
	
	/**
	 * The amount of times to produce this item.
	 */
	private int productionCount;
	
	/**
	 * The type of gem we are cutting.
	 */
	private Gem gemType;
	
	public GemCutting(Mob mob, Gem gemType, int productionCount) {
		super(mob);
		this.gemType = gemType;
		this.productionCount = productionCount;
	}

	public static final Animation CLEAR_GEM_ANIMATION = Animation.create(886), RED_GEM_ANIMATIION = Animation.create(887), BLUE_GEM_ANIMATION = Animation.create(888), GREEN_GEM_ANIMATION = Animation.create(889);
	
	public enum Gem {
		OPAL(1625, 1609, 15.0, 1, Animation.create(890)),
		JADE(1627, 1611, 20, 13, Animation.create(890)),
		RED_TOPAZ(1629, 1613, 25, 16, Animation.create(892)),
		SAPPHIRE(1623, 1607, 50, 20, BLUE_GEM_ANIMATION),
		EMERALD(1621, 1605, 67, 27, GREEN_GEM_ANIMATION),
		RUBY(1619, 1603, 85, 34, RED_GEM_ANIMATIION),
		DIAMOND(1617, 1601, 107.5, 43, CLEAR_GEM_ANIMATION),
		DRAGONSTONE(1631, 1615, 137.5, 55, Animation.create(885)),
		ONYX(6571, 6573, 168, 67, Animation.create(2717)),
		SLAYER_RING(4155, 11866, 35, 75, BLUE_GEM_ANIMATION),
		CRAB_CLAW(7536, 7537, 32.5, 15, Animation.create(886)),
		CRAB_HELMET(7538, 7539, 32.5, 15, Animation.create(886)),
		DARK_ESSENCE(13446, 7938, 8, 38, Animation.create(886)),
		ZENYTE(19496, 19493, 200, 89, Animation.create(7185));
		
		private double experience;
		private int levelRequired;
		private int uncut, cut;

		private Animation cutAnimation;
		
		private String name;
		
		private Gem(int uncut, int cut, double experience, int levelRequired, Animation cutAnimation) {
			this.uncut = uncut;
			this.cut = cut;
			this.experience = experience;
			this.levelRequired = levelRequired;
			this.cutAnimation = cutAnimation;
			this.name = toString().toLowerCase().replaceAll("_", " ");
		}
		

		private static Map<Integer, Gem> gems = new HashMap<Integer, Gem>();


		public static Gem forId(int item) {
			return gems.get(item);
		}

		static {
			for (Gem gem : Gem.values()) {
				gems.put(gem.uncut, gem);
			}
		}

		public String getName() {
			return name;
		}
		
		public int getRequiredLevel() {
			return levelRequired;
		}
		
		public int getReward() {
			return cut;
		}
		
		public int getConsumed() {
			return uncut;
		}
		
		public double getExperience() {
			return experience * 2;
		}
		
		public Animation getAnimation() {
			return cutAnimation;
		}

	}

	public static final Item CHISEL = new Item(1755, 1);

	@Override
	public int getCycleCount() {
		return 3;
	}

	@Override
	public int getProductionCount() {
		return productionCount;
	}

	@Override
	public Item[] getRewards() {
		return new Item[] { new Item(gemType.getReward()) };
	}

	@Override
	public Item[] getConsumedItems() {
		return new Item[] { new Item(gemType.getConsumed()) };
	}

	@Override
	public int getSkill() {
		return Skills.CRAFTING;
	}

	@Override
	public int getRequiredLevel() {
		return gemType.getRequiredLevel();
	}

	@Override
	public double getExperience() {
		return gemType.getExperience();
	}

	@Override
	public String getLevelTooLowMessage() {
		return "You need a " + Skills.SKILL_NAME[getSkill()] + " level of " + getRequiredLevel() + " to cut this gem.";
	}

	@Override
	public String getSuccessfulProductionMessage() {
		return "You successfully cut the " + CacheItemDefinition.get(gemType.getReward()).getName() + ".";
	}

	@Override
	public Animation getAnimation() {
		return gemType.getAnimation();
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

