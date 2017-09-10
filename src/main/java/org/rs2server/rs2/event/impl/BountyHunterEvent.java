package org.rs2server.rs2.event.impl;

import org.rs2server.Server;
import org.rs2server.rs2.domain.service.api.content.bounty.BountyHunterService;
import org.rs2server.rs2.domain.service.impl.content.bounty.BountyHunterServiceImpl;
import org.rs2server.rs2.event.Event;

/**
 * @author Clank1337
 */
public class BountyHunterEvent extends Event {

	private final BountyHunterService bountyHunterService;

	public BountyHunterEvent() {
		super(600);
		this.bountyHunterService = Server.getInjector().getInstance(BountyHunterService.class);
	}

	@Override
	public void execute() {
		BountyHunterServiceImpl.WILDERNESS_PLAYER_LIST.forEach(bountyHunterService::tick);
	}
}
