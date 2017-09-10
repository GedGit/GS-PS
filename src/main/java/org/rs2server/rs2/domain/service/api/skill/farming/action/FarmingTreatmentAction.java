package org.rs2server.rs2.domain.service.api.skill.farming.action;

import org.rs2server.Server;
import org.rs2server.rs2.action.impl.SimpleAction;
import org.rs2server.rs2.domain.service.api.skill.farming.FarmingPatchState;
import org.rs2server.rs2.domain.service.api.skill.farming.FarmingPatchTreatment;
import org.rs2server.rs2.domain.service.api.skill.farming.FarmingService;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Skill;
import org.rs2server.rs2.model.player.Player;

/**
 * Treating soil on a patch.
 *
 * @author tommo
 */
public class FarmingTreatmentAction extends SimpleAction {

	private static final Animation ANIMATION_POURING_TREATMENT = Animation.create(2283);

	private final FarmingService farmingService = Server.getInjector().getInstance(FarmingService.class);
	private final FarmingPatchState patch;
	private final FarmingPatchTreatment treatment;

	public FarmingTreatmentAction(Mob mob, FarmingPatchState patch, FarmingPatchTreatment treatment) {
		super(mob, 0);
		this.patch = patch;
		this.treatment = treatment;
	}

	@Override
	public void execute() {
		getMob().getInventory().remove(new Item(treatment.getItemId(), 1));
		getMob().getSkills().addExperience(Skill.FARMING.getId(), 26 * 2);
		getMob().playAnimation(ANIMATION_POURING_TREATMENT);
		patch.setTreatment(treatment);
		farmingService.updateAndSendPatches((Player) getMob(), patch);
		getMob().getActionSender().sendMessage("You treat the " + patch.getPatch().getType().toString() + " with " + treatment.name().toLowerCase() + ".");
		stop();
	}
}
