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

public class BoltCrafting extends ProductionAction{
	
	BoltTip tip;
	int amount;
	Animation ANIM = Animation.create(4462);

	public BoltCrafting(Mob mob, BoltTip tip, int amount) {
		super(mob);
		this.tip = tip;
		this.amount = amount;
	}
	
	
	public enum BoltTip {
		
		OPAL_TIP(11, 4, 1609, new Item(45, 12)),
		
		JADE_TIP(26, 5, 1611, new Item(9187, 12)),
		
		PEARL_TIP(41, 6, 411, new Item(46, 6)),
		
		RED_TOPAZ_TIP(48, 8, 1613, new Item(9188, 12)),
		
		SAPPHIRE_TIP(56, 10, 1607, new Item(9189, 12)),
		
		EMERALD_TIP(58, 15, 1605, new Item(9190, 12)),
		
		RUBY_TIP(63, 18, 1603, new Item(9191, 12)),
		
		DIAMOND_TIP(65, 20, 1601, new Item(9192, 12)),
		
		DRAGONSTONE_TIP(71, 25, 1615, new Item(9193, 12)),
		
		ONYX_TIP(73, 45, 6573, new Item(9194, 24));
		
		
		private int levelReq;
		private int xp;
		private int gem;
		private Item reward;
		
		BoltTip(int levelReq, int xp, int gem, Item reward) {
			this.levelReq = levelReq;
			this.xp = xp;
			this.gem = gem;
			this.reward = reward;
		}
		
		private static Map<Integer, BoltTip> gems = new HashMap<Integer, BoltTip>();


		public static BoltTip forId(int gem) {
			return gems.get(gem);
		}
 
		static {
			for (BoltTip gem : BoltTip.values()) {
				gems.put(gem.gem, gem);
			}
		}

		
		public int getLevelReq() {
			return levelReq;
		}
		
		public int getXP() {
			return xp * 2;
		}
		
		public int getGem() {
			return gem;
		}
		
		public Item getReward() {
			return reward;
		}
	}


	@Override
	public int getCycleCount() {
		return 3;
	}


	@Override
	public int getProductionCount() {
		return amount;
	}


	@Override
	public Item[] getRewards() {
		return new Item[] {tip.getReward()};
	}


	@Override
	public Item[] getConsumedItems() {
		return new Item[] {new Item(tip.getGem(), 1)};
	}


	@Override
	public int getSkill() {
		return Skills.FLETCHING;
	}


	@Override
	public int getRequiredLevel() {
		return tip.getLevelReq();
	}


	@Override
	public double getExperience() {
		return tip.getXP();
	}


	@Override
	public String getLevelTooLowMessage() {
		return "You need a Fletching level of " + tip.getLevelReq() + " to fletch this.";
	}


	@Override
	public String getSuccessfulProductionMessage() {
		return "You succesfully craft " + tip.getReward().getCount() + " " + CacheItemDefinition.get(tip.getReward().getId()).getName() + ".";
	}


	@Override
	public Animation getAnimation() {
		return ANIM;
	}


	@Override
	public Graphic getGraphic() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Item getFailItem() {
		// TODO Auto-generated method stub
		return null;
	}

}
