package org.rs2server.rs2.domain.service.api.skill.farming.action;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.rs2server.Server;
import org.rs2server.rs2.action.impl.SimpleAction;
import org.rs2server.rs2.domain.service.api.skill.farming.FarmingPatchState;
import org.rs2server.rs2.domain.service.api.skill.farming.FarmingPlantable;
import org.rs2server.rs2.domain.service.api.skill.farming.FarmingService;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Skill;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.util.Misc;

/**
 * An action where a player plants a seed in the patch.
 *
 * @author tommo
 */
public class FarmingPlantingAction extends SimpleAction {

	private static final Animation ANIMATION_PLANTING = Animation.create(2291);

	private final FarmingService farmingService = Server.getInjector().getInstance(FarmingService.class);
	private final FarmingPatchState patch;
	private final FarmingPlantable plantable;

	public FarmingPlantingAction(Mob mob, FarmingPatchState patch, FarmingPlantable plantable) {
		super(mob, 0);
		this.patch = patch;
		this.plantable = plantable;
	}

	@Override
	public void execute() {
		final Item seed = new Item(plantable.getSeedItemId(), 1);
		double exp = plantable.getExperience();
		if (seed.getDefinition2().getName().contains("sapling"))
			exp = exp / 100;
		getMob().getSkills().addExperience(Skill.FARMING.getId(), exp);
		getMob().playAnimation(ANIMATION_PLANTING);
		getMob().getInventory().remove(seed);
		if (seed.getDefinition2().getName().contains("sapling"))
			getMob().getInventory().add(new Item(5350, 1));
		getMob().getActionSender().sendMessage("You plant " + Misc.withPrefix(seed.getDefinition2().getName())
				+ " in the " + patch.getPatch().getType().toString());
		patch.setLastGrowthTime(DateTime.now(DateTimeZone.UTC));
		patch.setGrowth(plantable.getMinGrowth());
		patch.setPlanted(plantable);
		farmingService.updateAndSendPatches((Player) getMob(), patch);
		stop();
	}
}
