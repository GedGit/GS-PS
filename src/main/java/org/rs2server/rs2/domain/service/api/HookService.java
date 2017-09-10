package org.rs2server.rs2.domain.service.api;

import javax.annotation.Nonnull;

/**
 * @author tommo
 */
public interface HookService {

	void register(@Nonnull Object subscriber);

	void post(@Nonnull Object event);

	void unregister(@Nonnull Object subscriber);

}
