package org.rs2server.rs2.domain.service.api.skill.farming.action;

import org.rs2server.Server;
import org.rs2server.rs2.action.impl.SimpleAction;
import org.rs2server.rs2.domain.service.api.skill.farming.FarmingPatchState;
import org.rs2server.rs2.domain.service.api.skill.farming.FarmingService;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Skill;
import org.rs2server.rs2.model.player.Player;

/**
 * Watering a crop.
 *
 * @author tommo
 */
public class FarmingWateringAction extends SimpleAction {

	private static final Animation ANIMATION_WATERING = Animation.create(2293);

	private final FarmingService farmingService = Server.getInjector().getInstance(FarmingService.class);
	private final FarmingPatchState patch;

	public FarmingWateringAction(Mob mob, FarmingPatchState patch) {
		super(mob, 0);
		this.patch = patch;
	}

	@Override
	public void execute() {
		getMob().getSkills().addExperience(Skill.FARMING.getId(), 26 * 2);
		getMob().playAnimation(ANIMATION_WATERING);
		patch.setWatered(true);
		farmingService.updateAndSendPatches((Player) getMob(), patch);
		stop();
	}
}
