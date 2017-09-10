package org.rs2server.rs2.model.skills.smithing;

import org.rs2server.cache.format.CacheItemDefinition;
import org.rs2server.rs2.action.impl.ProductionAction;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.skills.smithing.SmithingUtils.ForgingBar;
import org.rs2server.rs2.util.Misc;

public class Forging extends ProductionAction {

	public static final Animation FORGING_ANIMATION = Animation.create(898);

	private ForgingBar bar;
	private int index;
	private int productionCount;

	public Forging(Mob mob, ForgingBar bar, int productionCount, int index) {
		super(mob);
		this.bar = bar;
		this.productionCount = productionCount;
		this.index = index;
	}

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
		return new Item[] {new Item(bar.getItems()[index], SmithingUtils.getItemAmount(bar.getItems()[index]))};
	}

	@Override
	public Item[] getConsumedItems() {
		return new Item[]{new Item(bar.getBarId(), SmithingUtils.getBarAmount(getRequiredLevel(), bar, bar.getItems()[index]))};
	}

	@Override
	public int getSkill() {
		return Skills.SMITHING;
	}

	@Override
	public int getRequiredLevel() {
		return bar.getBaseLevel() + SmithingUtils.getLevelIncrement(bar, bar.getItems()[index]);
	}

	@Override
	public double getExperience() {
		int barAmount = SmithingUtils.getBarAmount(getRequiredLevel(), bar, bar.getItems()[index]);
		return bar.getExperience() * barAmount;
	}

	@Override
	public String getLevelTooLowMessage() {
		return "You need a Smithing level of at least "  + bar.getBaseLevel() + " to use this.";
	}

	@Override
	public String getSuccessfulProductionMessage() {
		 return "You make " + Misc.withPrefix(CacheItemDefinition.get(bar.getItems()[index]).getName().toLowerCase()) + ".";
	}
	
	@Override
	public Animation getAnimation() {
		return FORGING_ANIMATION;
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