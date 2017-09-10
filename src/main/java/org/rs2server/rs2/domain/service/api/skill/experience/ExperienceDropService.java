package org.rs2server.rs2.domain.service.api.skill.experience;

import org.rs2server.rs2.content.api.GameInterfaceButtonEvent;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;

/**
 * A service used to interact with a player's experience dropdown widget.
 * @author twelve
 */
public interface ExperienceDropService {

	void openExperienceDrop(@Nonnull Player player);

	void openExperienceOrb(@Nonnull Player player);

	void onWidgetClicked(@Nonnull GameInterfaceButtonEvent event);

	void onOrbClicked(@Nonnull GameInterfaceButtonEvent event);
}
