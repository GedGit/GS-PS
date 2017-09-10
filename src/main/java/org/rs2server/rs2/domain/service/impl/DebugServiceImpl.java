package org.rs2server.rs2.domain.service.impl;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import org.rs2server.rs2.content.api.GameObjectActionEvent;
import org.rs2server.rs2.domain.service.api.DebugService;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.gameobject.GameObjectType;
import org.rs2server.rs2.model.player.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tommo
 */
public class DebugServiceImpl implements DebugService {

	private Map<Player, Boolean> debugToggles = new HashMap<>();

	@Inject
	DebugServiceImpl(final HookService hookService) {
		hookService.register(this);
	}

	@Override
	public void toggleDebug(Player player, boolean debug) {
		debugToggles.put(player, debug);
	}

	@Subscribe
	public void onObjectClick(GameObjectActionEvent clickEvent) {
		final Player player = clickEvent.getPlayer();
		final GameObject obj = clickEvent.getGameObject();

		if (isDebugToggled(clickEvent.getPlayer())) {
			player.getActionSender().sendMessage("[DEBUG]: Option=" + clickEvent.getActionType().name()
					+ ", Location=" + obj.getLocation() + ", Face=" + obj.getDirection() + ", Type=" + obj.getType() + " (" + GameObjectType.forType(obj.getType())  + ")");
		}
	}

	private boolean isDebugToggled(final Player player) {
		return debugToggles.containsKey(player) && debugToggles.get(player);
	}

}
