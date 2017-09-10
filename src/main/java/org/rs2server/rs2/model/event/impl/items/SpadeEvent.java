package org.rs2server.rs2.model.event.impl.items;

import org.rs2server.Server;
import org.rs2server.rs2.content.api.GameSpadeDigEvent;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.event.Event;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.event.ClickEventManager;
import org.rs2server.rs2.model.event.EventListener;
import org.rs2server.rs2.model.minigame.barrows.Barrows;
import org.rs2server.rs2.model.player.Player;

/**
 *
 * @author `Discardedx2
 */
public class SpadeEvent extends EventListener {

	@Override
	public void register(ClickEventManager manager) {
		manager.registerItemListener(952, this);
	}

	@Override
	public boolean itemAction(final Player player, Item item, int slot, ClickOption option) {
		player.playAnimation(Animation.create(831));
		World.getWorld().submit(new Event(600) {
			@Override
			public void execute() {
				stop();
				Server.getInjector().getInstance(HookService.class).post(new GameSpadeDigEvent(player, player.getLocation()));
				if (Barrows.enterCrypt(player)) {
					player.playAnimation(Animation.create(-1));
				} else {
					player.playAnimation(Animation.create(-1));
				}
			}
		});
		return false;
	}

}
