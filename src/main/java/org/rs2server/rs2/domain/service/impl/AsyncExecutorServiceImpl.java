package org.rs2server.rs2.domain.service.impl;


import org.rs2server.rs2.domain.service.api.AsyncExecutorService;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author tommo
 */
public class AsyncExecutorServiceImpl implements AsyncExecutorService {

	private final ExecutorService executorService;
	private final int processors;

	@Inject
	AsyncExecutorServiceImpl() {
		processors = Runtime.getRuntime().availableProcessors();
		executorService = Executors.newFixedThreadPool(processors);
	}

	/**
	 * Submits a task for asynchronous execution.
	 * @param runnable The task to execute asynchronously.
	 */
	public void submit(@Nonnull final Runnable runnable) {
		executorService.submit(runnable);
	}

}
