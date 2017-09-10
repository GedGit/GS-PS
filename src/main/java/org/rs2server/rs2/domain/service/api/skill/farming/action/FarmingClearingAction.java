package org.rs2server.rs2.domain.service.api.skill.farming.action;

import org.rs2server.Server;
import org.rs2server.rs2.action.impl.AbstractStatefulAction;
import org.rs2server.rs2.domain.service.api.skill.farming.FarmingPatchState;
import org.rs2server.rs2.domain.service.api.skill.farming.FarmingService;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.player.Player;

/**
 * An action to clear a patch of dead crops. 
 *
 * @author tommo
 */
public class FarmingClearingAction extends AbstractStatefulAction<FarmingClearingAction.ClearPatchState> {

	private static final Animation ANIMATION_DIGGING = Animation.create(831);

	public enum ClearPatchState {
		START_CLEARING, CLEARING, FINISHED_CLEARING
	}

	private final FarmingService farmingService = Server.getInjector().getInstance(FarmingService.class);
	private final FarmingPatchState patch;

	public FarmingClearingAction(Mob mob, FarmingPatchState patch) {
		super(mob, 0, ClearPatchState.START_CLEARING);
		this.patch = patch;
	}

	@Override
	public ClearPatchState onState(ClearPatchState state) {
		if (state == ClearPatchState.START_CLEARING) {
			getMob().playAnimation(ANIMATION_DIGGING);
			getMob().getActionSender().sendMessage("You start digging the farming patch...");
			return ClearPatchState.CLEARING;
		} else if (state == ClearPatchState.CLEARING) {
			return ClearPatchState.FINISHED_CLEARING;
		} else if (state == ClearPatchState.FINISHED_CLEARING) {
			getMob().getActionSender().sendMessage("You have successfully cleared this patch for new crops.");
			farmingService.clearPatch(((Player) getMob()), patch);
			getMob().playAnimation(Animation.create(-1));
		}
		return null;
	}


	@Override
	public CancelPolicy getCancelPolicy() {
		return CancelPolicy.ALWAYS;
	}

	@Override
	public StackPolicy getStackPolicy() {
		return StackPolicy.NEVER;
	}

	@Override
	public AnimationPolicy getAnimationPolicy() {
		return AnimationPolicy.RESET_ALL;
	}

}
