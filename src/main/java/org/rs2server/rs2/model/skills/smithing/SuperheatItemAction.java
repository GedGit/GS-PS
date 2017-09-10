package org.rs2server.rs2.model.skills.smithing;

import org.rs2server.rs2.action.impl.ProductionAction;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.container.Inventory;
import org.rs2server.rs2.model.skills.smithing.SmithingUtils.SmeltingBar;
import org.rs2server.rs2.net.ActionSender;

public class SuperheatItemAction extends ProductionAction {

	private SmeltingBar bar;
	private static final Animation ANIMATION_ID = Animation.create(723);
	private static final Graphic GRAPHIC_ID = Graphic.create(148);
	private int amount;

	/**
	 * These will be set to true after the first time they are played.
	 */
	private boolean playedAnimation = false;
	private boolean playedGraphic = false;

	public SuperheatItemAction(Mob mob, SmeltingBar bar, int amount) {
		super(mob);
		this.bar = bar;
		this.amount = amount;
		for (int i = 0; i < getConsumedItems().length; i++) {
			if (Inventory.getCount(mob, getConsumedItems()[i].getId()) < getConsumedItems()[i].getCount()) {
				mob.getActionSender().sendDialogue("", ActionSender.DialogueType.MESSAGE, -1, null, "You don't have the required ore to smelt this bar.");
				mob.getActionSender().sendMessage("You don't have the required ore to smelt this bar.");
				return;
			}
		}
	}

	@Override
	public void onSuccess() {
		Inventory.removeRune(getMob(), new Item(561));
		Inventory.removeRune(getMob(), new Item(554, 4));
		getMob().getSkills().addExperience(Skills.MAGIC, 7 * 53);
		getMob().getActionSender().switchTab(4);
	}

	@Override
	public int getCycleCount() {
		return 1;
	}

	@Override
	public int getProductionCount() {
		return amount;
	}

	@Override
	public Item[] getRewards() {
		return new Item[] {bar.getProducedBar()};
	}

	@Override
	public Item[] getConsumedItems() {
		return bar.getItemsRequired();
	}

	@Override
	public int getSkill() {
		return Skills.SMITHING;
	}

	@Override
	public int getRequiredLevel() {
		return bar.getLevelRequired();
	}

	@Override
	public double getExperience() {
		return bar.getExperience();
	}

	@Override
	public String getLevelTooLowMessage() {
		return "You need a Smithing level of " + bar.getLevelRequired() + " to make this bar.";
	}

	@Override
	public String getSuccessfulProductionMessage() {
		return "You retrieve a bar of " + bar.toString().toLowerCase() + ".";
	}

	@Override
	public Animation getAnimation() {
		if (playedAnimation) return null;

		playedAnimation = true;
		return ANIMATION_ID;
	}

	@Override
	public Graphic getGraphic() {
		if (playedGraphic) return null;

		playedGraphic = true;
		return GRAPHIC_ID;
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
		return "The ore is too impure and you fail to refine it.";
	}

	@Override
	public Item getFailItem() {
		return null;
	}

}
