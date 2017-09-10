package org.rs2server.rs2.tickable.impl;

import org.rs2server.Server;
import org.rs2server.rs2.domain.service.api.GroundItemService;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.tickable.Tickable;

public class ItemSpawnTick extends Tickable {

	/**
	 * Creates the tickable to run every 60 seconds.
	 */
	public ItemSpawnTick() {
		super(100);
	}

	@Override
	public void execute() {
		GroundItemService groundItemService = Server.getInjector().getInstance(GroundItemService.class);
		ItemSpawn.getSpawns().stream()
				.filter(i -> !groundItemService.getGroundItem(i.getItem().getId(), i.getLocation()).isPresent())
				.forEach(spawn -> groundItemService.createGroundItem(null,
						new GroundItemService.SpawnedGroundItem(spawn.getItem(), spawn.getLocation(), "", true)));
	}

}
