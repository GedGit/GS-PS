package org.rs2server.rs2.model.skills;

import org.rs2server.rs2.action.impl.ProductionAction;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.util.Misc;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Clank1337
 */
public class PestleAndMortar extends ProductionAction {

	private Pestle pestle;
	private int amount;

	public PestleAndMortar(Mob mob, Pestle pestle, int amount) {
		super(mob);
		this.pestle = pestle;
		this.amount = amount;
	}

	public enum Pestle {

		UNICORN_HORN(237, 235, 1, 1),

		CHOCOLATE_BAR(1973, 1975, 1, 1),

		BLUE_DRAGON_SCALE(243, 241, 1, 1),

		LAVA_SCALE(11992, 11994, 3, 6),

		MARK_OF_GRACE(11849, 12640, 10, 10);

		private int prev;
		private int next;
		private int minAmount;
		private int maxAmount;

		Pestle(int prev, int next, int minAmount, int maxAmount) {
			this.prev = prev;
			this.next = next;
			this.minAmount = minAmount;
			this.maxAmount = maxAmount;
		}

		private static Map<Integer, Pestle> items = new HashMap<Integer, Pestle>();

		public static Pestle forId(int item) {
			return items.get(item);
		}

		static {
			for (Pestle pestle : Pestle.values()) {
				items.put(pestle.getPrev(), pestle);
			}
		}

		public int getPrev() {
			return prev;
		}

		public int getNext() {
			return next;
		}

		public int getMinAmount() {
			return minAmount;
		}

		public int getMaxAmount() {
			return maxAmount;
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
		return new Item[] { new Item(pestle.getNext(), Misc.random(pestle.getMinAmount(), pestle.getMaxAmount())) };
	}

	@Override
	public Item[] getConsumedItems() {
		return new Item[] { new Item(pestle.getPrev()) };
	}

	@Override
	public int getSkill() {
		return Skills.HERBLORE;
	}

	@Override
	public int getRequiredLevel() {
		return 1;
	}

	@Override
	public double getExperience() {
		return 0;
	}

	@Override
	public String getLevelTooLowMessage() {
		return "";
	}

	@Override
	public String getSuccessfulProductionMessage() {
		return "";
	}

	@Override
	public Animation getAnimation() {
		return Animation.create(364);
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
