package org.rs2server.rs2.domain.service.api;

import javax.annotation.Nonnull;

/**
 * Provides async execution capabilities.
 *
 * @author tommo
 */
public interface AsyncExecutorService {

	/**
	 * Submits a task for asynchronous execution.
	 * @param runnable The task to execute asynchronously.
	 */
	void submit(@Nonnull final Runnable runnable);

}
