package org.rs2server.rs2.domain.service.api.clojure;

import org.rs2server.rs2.content.api.GameWorldLoadedEvent;

import javax.annotation.Nonnull;

/**
 * @author twelve
 */
public interface ClojureService {

	void invokeScriptsOnStartup(@Nonnull GameWorldLoadedEvent event);

	void registerAndInvoke(@Nonnull String name, @Nonnull ClojureScript script);

	Object register(@Nonnull String name, @Nonnull ClojureScript script);

	Object invoke(@Nonnull ClojureScript script);

	Object invoke(@Nonnull String scriptName);

	Object invoke(@Nonnull String scriptName, @Nonnull Object... args);

	Object invoke(@Nonnull ClojureScript script, @Nonnull Object... args);
}
