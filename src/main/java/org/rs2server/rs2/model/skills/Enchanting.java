package org.rs2server.rs2.model.skills;

import org.rs2server.rs2.action.impl.ProductionAction;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Skills;

import java.util.HashMap;
import java.util.Map;

public class Enchanting extends ProductionAction {
	
	BoltType type;
	Animation ANIM = Animation.create(4462);

	public Enchanting(Mob mob, BoltType type) {
		super(mob);
		this.type = type;
	}
	
	
	public enum BoltType {
		OPAL_BOLT_E(2, 4, 30, new Item[] {new Item(879, 10), new Item(564, 1), new Item(556, 2)}, new Item(9236, 10)),
		
		SAPPHIRE_BOLT_E(3, 7, 50, new Item[] {new Item(9337, 10), new Item(564, 1), new Item(555, 1), new Item(558, 1)}, new Item(9240, 10)),
		
		JADE_BOLT_E(4, 14, 70, new Item[] {new Item(9235, 10), new Item(564, 1), new Item(557, 2)}, new Item(9237, 10)),
		
		PEARL_BOLT_E(5, 24, 85, new Item[] {new Item(880, 10), new Item(564, 1), new Item(555, 2)}, new Item(9238, 10)),
		
		EMERALD_BOLT_E(6, 27, 95, new Item[] {new Item(9338, 10), new Item(564, 1), new Item(556, 3), new Item(561, 1)}, new Item(9241, 10)),
		
		RED_TOPAZ(7, 29, 105, new Item[] {new Item(9336, 10), new Item(564, 1), new Item(554, 2)}, new Item(9239, 10)),
		
		RUBY_BOLT_E(8, 49, 140, new Item[] {new Item(9339, 10), new Item(564, 1), new Item(554, 5), new Item(565, 1)}, new Item(9242, 10)),
		
		DIAMOND_BOLT_E(9, 57, 160, new Item[] {new Item(9340, 10), new Item(564, 1), new Item(557, 10), new Item(563, 2)}, new Item(9243, 10)),
		
		DRAGON_BOLT_E(10, 68, 190, new Item[] {new Item(9341, 10), new Item(564, 1), new Item(557, 15), new Item(566, 1)}, new Item(9244, 10)),
		
		ONYX_BOLT_E(11, 87, 225, new Item[] {new Item(9342, 10), new Item(564, 1), new Item(554, 20), new Item(560, 1)}, new Item(9245, 10));
		
		
		private int buttonId;
		private int magicReq;
		private int xp;
		private Item[] consumed;
		private Item reward;
		
		BoltType(int buttonId, int magicReq, int xp, Item[] consumed, Item reward) {
			this.buttonId = buttonId;
			this.magicReq = magicReq;
			this.xp = xp;
			this.consumed = consumed;
			this.reward = reward;
		}
		
		private static Map<Integer, BoltType> items = new HashMap<Integer, BoltType>();


		public static BoltType forId(int button) {
			return items.get(button);
		}

		static {
			for (BoltType type : BoltType.values()) {
				items.put(type.getButton(), type);
			}
		}
		
		public int getButton() {
			return buttonId;
		}
		
		public int getLevelReq() {
			return magicReq;
		}
		
		public int getXP() {
			return xp * 2;
		}
		
		public Item[] getConsumed() {
			return consumed;
		}
		
		public Item getReward() {
			return reward;
		}
	}


	@Override
	public int getCycleCount() {
		return 2;
	}


	@Override
	public int getProductionCount() {
		return 1;
	}


	@Override
	public Item[] getRewards() {
		return new Item[] {type.getReward()};
	}


	@Override
	public Item[] getConsumedItems() {
		return type.getConsumed();
	}


	@Override
	public int getSkill() {
		return Skills.MAGIC;
	}


	@Override
	public int getRequiredLevel() {
		return type.getLevelReq();
	}


	@Override
	public double getExperience() {
		return type.getXP();
	}


	@Override
	public String getLevelTooLowMessage() {
		return "You need a Magic level of " + type.getLevelReq() + " to enchant this.";
	}


	@Override
	public String getSuccessfulProductionMessage() {
		return "The magic of the runes coaxes out the true nature of the gem tips.";
	}


	@Override
	public Animation getAnimation() {
		return ANIM;
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
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Item getFailItem() {
		// TODO Auto-generated method stub
		return null;
	}

}
