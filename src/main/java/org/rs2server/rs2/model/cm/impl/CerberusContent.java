package org.rs2server.rs2.model.cm.impl;

import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.boundary.BoundaryManager;
import org.rs2server.rs2.model.cm.Content;
import org.rs2server.rs2.model.npc.impl.cerberus.Cerberus;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;

/**
 * Created by shawn on 6/8/2016.
 */
public class CerberusContent extends Content {
	
	public static final Location SPAWN_LOCATION = Location.create(1237, 1251);
	private Cerberus cerberus;

	private int respawnTimer = -1;
	private boolean started;

	public CerberusContent(Player player) {
		super(player);
	}

	public void spawnCerberus() {
		cerberus = new Cerberus(player, SPAWN_LOCATION);
		World.getWorld().createNPC(cerberus);
	}

	@Override
	public void start() {
		player.setMultiplayerDisabled(true);
		if (!BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "Cerberus")) {
			World.getWorld().submit(new Tickable(2) {
				@Override
				public void execute() {
					if (getTickDelay() == 1) {
						started = true;
						stop();
						return;
					}
					setTickDelay(1);
				}
			});
		}
		respawnTimer = 1;
	}

	@Override
	public void process() {
		if (respawnTimer > -1)
			respawnTimer--;
		if (started) {
			if (!BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "Cerberus")) {
				player.getContentManager().stop(this);
				return;
			}
		}
		if (cerberus != null && cerberus.getSkills().getLevel(Skills.HITPOINTS) > 0
				&& World.getWorld().getNPCs().contains(cerberus) && !cerberus.getCombatState().isDead()
				&& BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "Cerberus")) {
			cerberus.doCombat();
			System.out.println("processing combat, we're at "+cerberus.getLocation().toString());
		} else {
			// Only spawn if in cerberus lair
			if (respawnTimer == 0 && BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "Cerberus"))
				spawnCerberus();
			if (respawnTimer == -1)
				respawnTimer = 30;
		}
	}

	@Override
	public void stop() {
		if (cerberus != null) {
			cerberus.destroySelf();
			cerberus = null;
		}
		if (player.isMultiplayerDisabled())
			player.setMultiplayerDisabled(false);
	}

	@Override
	public boolean canStart() {
		return true;
	}

	@Override
	public void onCannotStart() {

	}
}
