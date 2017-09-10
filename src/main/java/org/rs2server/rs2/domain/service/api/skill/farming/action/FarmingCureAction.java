package org.rs2server.rs2.domain.service.api.skill.farming.action;

import org.rs2server.Server;
import org.rs2server.rs2.action.impl.SimpleAction;
import org.rs2server.rs2.domain.service.api.skill.farming.FarmingPatchState;
import org.rs2server.rs2.domain.service.api.skill.farming.FarmingService;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Skill;
import org.rs2server.rs2.model.player.Player;

/**
 * An action to cure a diseased crop.
 *
 * @author tommo
 */
public class FarmingCureAction extends SimpleAction { 

	public static final Item ITEM_PLANT_CURE = new Item(6036, 1);
	private static final Animation ANIMATION_POURING_PLANT_CURE = Animation.create(2288);

	private final FarmingService farmingService = Server.getInjector().getInstance(FarmingService.class);
	private final FarmingPatchState patch;

	public FarmingCureAction(Mob mob, FarmingPatchState patch) {
		super(mob, 0);
		this.patch = patch;
	}

	@Override
	public void execute() {
		getMob().getInventory().remove(ITEM_PLANT_CURE);
		getMob().getSkills().addExperience(Skill.FARMING.getId(), 26 * 2);
		getMob().playAnimation(ANIMATION_POURING_PLANT_CURE);
		patch.setDiseased(false);
		farmingService.updateAndSendPatches((Player) getMob(), patch);
		getMob().getActionSender().sendMessage("You cure the " + patch.getPatch().getType().toString() + " with " + ITEM_PLANT_CURE.getDefinition2().getName().toLowerCase() + ".");
		stop();
	}

}
