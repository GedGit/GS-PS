package org.rs2server.rs2.model.skills.smithing;

import org.rs2server.rs2.action.impl.ProductionAction;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.ActionSender.DialogueType;

public class DragonfireShieldAction extends ProductionAction {
	
	
	public static boolean handleItemOnObject(Player player, Item item, GameObject obj) {
		if (!player.getInventory().contains(2347) || !player.getInventory().contains(1540) || !player.getInventory().contains(11286)) {
			return false;
		}
		if (item.getId() == 2347 || item.getId() == 11286) {
			player.getActionSender().sendDialogue("", DialogueType.MESSAGE_MODEL_LEFT, 11286, null, "You set to work, trying to attach the ancient draconic<br>visage to your anti-dragonbreath shield. It's not easy to<br>work with the ancient artifact and ti takes all your<br>skill as a master smith.");
			player.setAttribute("forging_dfs", true);
			return true;
		}
		return false;
	}

	public DragonfireShieldAction(Mob mob) {
		super(mob);
	}

	@Override
	public int getCycleCount() {
		return 6;
	}

	@Override
	public int getProductionCount() {
		return 1;
	}

	@Override
	public Item[] getRewards() {
		return new Item[] {new Item(11283, 1)};
	}

	@Override
	public Item[] getConsumedItems() {
		return new Item[] {new Item(1540, 1), new Item(11286, 1)};
	}

	@Override
	public int getSkill() {
		return Skills.SMITHING;
	}

	@Override
	public int getRequiredLevel() {
		return 90;
	}

	@Override
	public double getExperience() {
		return 4000;
	}

	@Override
	public String getLevelTooLowMessage() {
		return "You need a Smithing level of 90 to smith this.";
	}

	@Override
	public String getSuccessfulProductionMessage() {
		return "You sucessfully attach the visage to the shield.";
	}

	@Override
	public Animation getAnimation() {
		return Animation.create(898);
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
