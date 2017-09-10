package org.rs2server.rs2.event;

import org.rs2server.Server;
import org.rs2server.rs2.domain.service.api.EngineService;

import java.util.concurrent.TimeUnit;

/**
 * A class that manages <code>Event</code>s for a specific
 * <code>GameEngine</code>.
 * 
 * @author Graham Edgecombe
 *
 */
public class EventManager {

	private final EngineService engineService;

	/**
	 * Creates an <code>EventManager</code> for the specified
	 * <code>GameEngine</code>.
	 * 
	 * @param engine
	 *            The game engine the manager is managing events for.
	 */
	public EventManager() {
		this.engineService = Server.getInjector().getInstance(EngineService.class);
	}

	/**
	 * Submits a new event to the <code>GameEngine</code>.
	 * 
	 * @param event
	 *            The event to submit.
	 */
	public void submit(final Event event) {
		submit(event, event.getDelay());
	}

	/**
	 * Schedules an event to run after the specified delay.
	 * 
	 * @param event
	 *            The event.
	 * @param delay
	 *            The delay.
	 */
	private void submit(final Event event, final long delay) {
		engineService.scheduleFuture(() -> {
			long start = System.currentTimeMillis();
			if (event.isRunning())
				event.execute();
			else
				return;
			long elapsed = System.currentTimeMillis() - start;
			long remaining = event.getDelay() - elapsed;
			if (remaining <= 0)
				remaining = 0;
			submit(event, remaining);
		}, TimeUnit.MILLISECONDS, delay);
	}
}