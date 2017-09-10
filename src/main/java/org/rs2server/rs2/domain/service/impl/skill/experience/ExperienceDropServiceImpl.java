package org.rs2server.rs2.domain.service.impl.skill.experience;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import org.rs2server.Server;
import org.rs2server.rs2.content.api.GameInterfaceButtonEvent;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.domain.service.api.PlayerVariableService;
import org.rs2server.rs2.domain.service.api.skill.experience.ExperienceDropService;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;

/**
 * An implementation of a {@link ExperienceDropService}
 * 
 * @author twelve
 */
public final class ExperienceDropServiceImpl implements ExperienceDropService {

	private static final int EXPERIENCE_DROP_WIDGET = 137;

	private static final int POSITION_CHILD = 50;
	private static final int SIZE_CHILD = 51;
	private static final int DURATION_CHILD = 52;
	private static final int SELECTED_SKILL_CHILD = 53;
	private static final int PROGRESS_BAR_CHILD = 54;
	private static final int COLOR_CHILD = 55;
	private static final int GROUP_CHILD = 56;
	private static final int SPEED_CHILD = 57;

	private static final int EXPERIENCE_DROP_ORB_WIDGET = 160;
	private static final int EXPERIENCE_DROP_ORB_CHILD = 1;

	@Inject
	ExperienceDropServiceImpl(HookService hookService) {
		hookService.register(this);
	}

	@Override
	public void openExperienceDrop(@Nonnull Player player) {
		final PlayerVariableService playerVariableService = Server.getInjector()
				.getInstance(PlayerVariableService.class);
		final int config = ExperienceVariables.COLOR_VARIABLE.getComposite().getConfig();

		playerVariableService.send(player, config);

		int pane = player.getAttribute("tabmode");
		int tabId = pane == 548 ? 15 : 7;

		player.getActionSender().sendInterface(pane, tabId, 122, true);
	}

	@Override
	public void openExperienceOrb(@Nonnull Player player) {

		// This method opens the experience counter

		final PlayerVariableService playerVariableService = Server.getInjector()
				.getInstance(PlayerVariableService.class);
		final int config = ExperienceVariables.COLOR_VARIABLE.getComposite().getConfig();

		int pane = player.getAttribute("tabmode");
		int tabId = pane == 548 ? 15 : 7;

		player.getActionSender().sendInterface(pane, tabId, 122, true);
		playerVariableService.send(player, config);
		player.getSettings().setExpCounterOpen(true);
	}

	@Override
	@Subscribe
	public void onWidgetClicked(@Nonnull GameInterfaceButtonEvent event) {
		if (event.getInterfaceId() == EXPERIENCE_DROP_WIDGET) {
			final Player player = event.getPlayer();
			final PlayerVariableService playerVariableService = Server.getInjector()
					.getInstance(PlayerVariableService.class);
			final int config = ExperienceVariables.COLOR_VARIABLE.getComposite().getConfig();

			int initialValue = playerVariableService.getCurrentValue(player, config);
			switch (event.getButton()) {
			case POSITION_CHILD:
				playerVariableService.set(player, ExperienceVariables.POSITION_VARIABLE, event.getChildButton() - 1);
				break;
			case COLOR_CHILD:
				playerVariableService.set(player, ExperienceVariables.COLOR_VARIABLE, event.getChildButton() - 1);
				break;
			case DURATION_CHILD:
				playerVariableService.set(player, ExperienceVariables.DURATION_VARIABLE, event.getChildButton() - 1);
				break;
			case SIZE_CHILD:
				playerVariableService.set(player, ExperienceVariables.SIZE_VARIABLE, event.getChildButton() - 1);
				break;
			case GROUP_CHILD:
				playerVariableService.set(player, ExperienceVariables.GROUP_VARIABLE, event.getChildButton() - 1);
				break;
			case SELECTED_SKILL_CHILD:
				playerVariableService.set(player, ExperienceVariables.SELECTED_SKILL_VARIABLE,
						event.getChildButton() - 1);
				break;
			case PROGRESS_BAR_CHILD:
				playerVariableService.set(player, ExperienceVariables.PROGRESS_BAR_VARIABLE,
						event.getChildButton() - 1);
				break;
			case SPEED_CHILD:
				playerVariableService.set(player, ExperienceVariables.SPEED_VARIABLE, event.getChildButton() - 1);
				break;
			}

			if (initialValue != playerVariableService.getCurrentValue(player, config)) {
				playerVariableService.send(player, config);
			}
		}
	}

	@Override
	@Subscribe
	public void onOrbClicked(@Nonnull GameInterfaceButtonEvent event) {
		if (event.getInterfaceId() == EXPERIENCE_DROP_ORB_WIDGET && event.getButton() == EXPERIENCE_DROP_ORB_CHILD) {

			if (event.getMenuIndex() == 1)
				openExperienceDrop(event.getPlayer());
			else {
				Player player = event.getPlayer();

				if (!player.getSettings().isExpCounterOpen())
					openExperienceOrb(player);
				else {
					int pane = player.getAttribute("tabmode");
					int tabId = pane == 548 ? 15 : 7;
					player.getSettings().setExpCounterOpen(false);
					player.getActionSender().removeInterfaces(pane, tabId);
				}
			}
		}
	}
}
