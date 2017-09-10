package org.rs2server.rs2.task.impl;

import org.rs2server.Server;
import org.rs2server.rs2.domain.service.api.EngineService;
import org.rs2server.rs2.task.Task;

/**
 * Performs garbage collection and finalization.
 * @author Graham Edgecombe
 *
 */
public class CleanupTask implements Task {

	private final EngineService engineService;

	public CleanupTask() {
		this.engineService = Server.getInjector().getInstance(EngineService.class);
	}
	@Override
	public void execute() {
		engineService.offerToSingle(() -> {
			System.gc();
			System.runFinalization();
		});
	}

}