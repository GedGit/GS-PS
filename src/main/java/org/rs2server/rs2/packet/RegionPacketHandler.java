package org.rs2server.rs2.packet;

import org.rs2server.Server;
import org.rs2server.rs2.content.api.GamePlayerRegionEvent;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.sound.Music;
import org.rs2server.rs2.net.Packet;

/**
 * Player enters a new region.
 * 
 * @author Scu11
 *
 */
public class RegionPacketHandler implements PacketHandler {

	private final HookService hookService = Server.getInjector().getInstance(HookService.class);

	@Override
	public void handle(Player player, Packet packet) {
		hookService.post(new GamePlayerRegionEvent(player));
		Music.playMusic(player);

		player.getLocalNPCs().stream().filter(n -> n.getInteractingEntity() != null).forEach(n -> {
			Mob mob = n.getInteractingEntity();

			n.face(mob.getLocation());
			mob.face(n.getLocation());
		});

		player.getLocalPlayers().stream().filter(p -> p.getInteractingEntity() != null).forEach(p -> {
			Mob mob = p.getInteractingEntity();

			p.face(mob.getLocation());
			mob.face(p.getLocation());
		});
	}
}