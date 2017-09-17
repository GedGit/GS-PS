package org.rs2server.rs2.model.cm.impl;

import org.rs2server.Server;
import org.rs2server.rs2.domain.service.api.content.KrakenService;
import org.rs2server.rs2.domain.service.impl.content.KrakenServiceImpl;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.boundary.BoundaryManager;
import org.rs2server.rs2.model.cm.Content;
import org.rs2server.rs2.model.npc.impl.kraken.Kraken;
import org.rs2server.rs2.model.player.Player;

/**
 * @author Clank1337
 */
public class KrakenContent extends Content {

	private Kraken kraken;
	private boolean started;
	private int respawnTimer = -1;

	private KrakenService krakenService;

	public KrakenContent(Player player) {
		super(player);
		this.krakenService = Server.getInjector().getInstance(KrakenService.class);
	}

	@Override
	public void start() {
		player.setMultiplayerDisabled(true);
		if (BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "Kraken")) {
			started = true;
			System.out.println("started kraken");
			return;
		}
		respawnTimer = 5;
	}

	@Override
	public void process() {
		if (respawnTimer > -1)
			respawnTimer--;
		if (started) {
			Location loc = KrakenServiceImpl.SPAWN_LOCATION;
			if (player.getLocation().distanceToPoint(loc) >= 40) {
				player.getContentManager().stop(this);
				respawnTimer = -1;
				return;
			}
		}
		if (kraken != null && World.getWorld().getNPCs().contains(kraken) && !kraken.getCombatState().isDead()) {
			// Hello
		} else {
			if (respawnTimer == 0)
				spawnKraken();
			if (respawnTimer == -1)
				respawnTimer = 15;
		}

	}

	@Override
	public void stop() {
		if (kraken != null) {
			kraken.destroySelf();
			started = false;
			kraken = null;
		}
		if (player.isMultiplayerDisabled())
			player.setMultiplayerDisabled(false);
		player.resetFace();
		respawnTimer = 5;
	}

	@Override
	public boolean canStart() {
		return !player.getInstancedNPCs().contains(kraken);
	}

	@Override
	public void onCannotStart() {

	}

	public void spawnKraken() {
		kraken = new Kraken(player, KrakenServiceImpl.KRAKEN, KrakenServiceImpl.SPAWN_LOCATION);
		krakenService.addKraken(player, kraken);
	}
}
