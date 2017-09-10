package org.rs2server.rs2.task.impl;

import org.rs2server.Server;
import org.rs2server.rs2.domain.service.api.EngineService;
import org.rs2server.rs2.task.Task;

/**
 * A task which stops the game engine.
 * @author Graham Edgecombe
 *
 */
public class DeathTask implements Task {

	private final EngineService engineService;

	public DeathTask() {
		this.engineService = Server.getInjector().getInstance(EngineService.class);
	}
	@Override
	public void execute() {
		if (engineService.isRunning()) {
			engineService.stop();
		}
	}

}
