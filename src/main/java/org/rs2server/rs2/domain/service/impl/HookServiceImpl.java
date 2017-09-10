package org.rs2server.rs2.domain.service.impl;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import com.google.inject.Inject;
import org.rs2server.rs2.domain.service.api.HookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * @author tommo
 */
public class HookServiceImpl implements HookService, SubscriberExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(HookServiceImpl.class);

	private final EventBus eventBus = new EventBus(this);

	@Inject
	HookServiceImpl() {

	}

	@Override
	public void register(@Nonnull Object subscriber) {
		eventBus.register(subscriber);
	}

	@Override
	public void post(@Nonnull Object event) {
		eventBus.post(event);
	}

	@Override
	public void unregister(@Nonnull Object subscriber) {
		eventBus.unregister(subscriber);
	}

	@Override
	public void handleException(Throwable exception, SubscriberExceptionContext context) {
		logger.error("Exception occurred in hook service at "
				+ context.getSubscriber().getClass().getSimpleName() + "#" + context.getSubscriberMethod().getName(),
				exception);
	}
}
