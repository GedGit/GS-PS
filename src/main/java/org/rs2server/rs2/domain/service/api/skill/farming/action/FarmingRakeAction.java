package org.rs2server.rs2.domain.service.api.skill.farming.action;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.rs2server.Server;
import org.rs2server.rs2.action.impl.InfiniteHarvestingAction;
import org.rs2server.rs2.domain.service.api.skill.farming.FarmingPatchState;
import org.rs2server.rs2.domain.service.api.skill.farming.FarmingService;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.Skill;
import org.rs2server.rs2.model.player.Player;

/**
 * A rake action.
 *
 * @author tommo
 */
public class FarmingRakeAction extends InfiniteHarvestingAction {

	private static final Animation ANIMATION_RAKE = Animation.create(2273);
	private static final Item ITEM_RAKE = new Item(5341, 1);

	private final FarmingService farmingService = Server.getInjector().getInstance(FarmingService.class);
	private final FarmingPatchState patchState;

	public FarmingRakeAction(Mob mob, FarmingPatchState patchState) {
		super(mob);
		this.patchState = patchState;
	}

	@Override
	public void onSuccessfulHarvest(Item item) {
		patchState.setLastGrowthTime(DateTime.now(DateTimeZone.UTC));
		patchState.setWeedLevel(patchState.getWeedLevel() + 1);

		farmingService.updateAndSendPatches((Player) getMob(), patchState);

		// There is no more weed. Call your dealer.
		if (patchState.getWeedLevel() == 3) {
			stop();
		}
	}

	@Override
	public int getCycleCount() {
		return 3;
	}

	@Override
	public Item getReward() {
		return new Item(6055, 1);
	}

	@Override
	public int getSkill() {
		return Skill.FARMING.getId();
	}

	@Override
	public int getRequiredLevel() {
		return 0;
	}

	@Override
	public double getExperience() {
		return 8;
	}

	@Override
	public String getLevelTooLowMessage() {
		return null;
	}

	@Override
	public String getHarvestStartedMessage() {
		return null;
	}

	@Override
	public String getSuccessfulHarvestMessage() {
		return null;
	}

	@Override
	public String getInventoryFullMessage() {
		return "Your inventory is full.";
	}

	@Override
	public Animation getAnimation() {
		return ANIMATION_RAKE;
	}

	@Override
	public boolean canHarvest() {
		if (!getMob().getInventory().contains(ITEM_RAKE.getId())) {
			getMob().getActionSender().sendMessage("You need a rake to weed a farming patch.");
			return false;
		}
		return true;
	}
}
