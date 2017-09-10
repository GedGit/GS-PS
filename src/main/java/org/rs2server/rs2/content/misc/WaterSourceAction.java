package org.rs2server.rs2.content.misc;

import org.rs2server.rs2.action.impl.ProductionAction;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tim on 11/29/2015.
 */
public class WaterSourceAction extends ProductionAction {

	private final Fillables fillables;

	public WaterSourceAction(Mob mob, Fillables fillables) {
		super(mob);
		this.fillables = fillables;
	}

	public enum Fillables {

		VIAL(229, 227),

		WATERING_CAN(5331, 5340),

		WATERING_CAN1(5333, 5340),

		WATERING_CAN2(5334, 5340),

		WATERING_CAN3(5335, 5340),

		WATERING_CAN4(5336, 5340),

		WATERING_CAN5(5337, 5340),

		WATERING_CAN6(5338, 5340),

		WATERING_CAN7(5339, 5340);

		private final int empty;
		private final int full;

		Fillables(int empty, int full) {
			this.empty = empty;
			this.full = full;
		}

		private static Map<Integer, Fillables> fillables = new HashMap<Integer, Fillables>();

		public static Fillables forId(int item) {
			return fillables.get(item);
		}

		static {
			for (Fillables fill : Fillables.values()) {
				fillables.put(fill.empty, fill);
			}
		}

		public int getEmpty() {
			return empty;
		}

		public int getFull() {
			return full;
		}
	}

	@Override
	public int getCycleCount() {
		return 1;
	}

	@Override
	public int getProductionCount() {
		return getMob().getInventory().getCount(fillables.getEmpty());
	}

	@Override
	public Item[] getRewards() {
		return new Item[] { new Item(fillables.getFull()) };
	}

	@Override
	public Item[] getConsumedItems() {
		return new Item[] { new Item(fillables.getEmpty()) };
	}

	@Override
	public int getSkill() {
		return 0;
	}

	@Override
	public int getRequiredLevel() {
		return 0;
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
		return Animation.create(832);
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
		return "";
	}

	@Override
	public Item getFailItem() {
		return null;
	}
}
