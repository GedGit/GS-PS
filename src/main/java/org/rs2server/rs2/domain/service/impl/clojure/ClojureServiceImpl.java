package org.rs2server.rs2.domain.service.impl.clojure;

import clojure.lang.AFn;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;
import com.diffplug.common.base.Errors;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import org.rs2server.rs2.content.api.GameWorldLoadedEvent;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.domain.service.api.clojure.ClojureScript;
import org.rs2server.rs2.domain.service.api.clojure.ClojureService;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

/**
 * A service for interacting with the Clojure language.
 * @author twelve
 */
public final class ClojureServiceImpl implements ClojureService {

	private static final Logger LOGGER = Logger.getLogger(ClojureService.class.getName());
	private final LinkedHashMap<String, ClojureScript> scripts;

	private static final Symbol CLOJURE_MAIN = Symbol.intern("clojure.main");
	private static final Var REQUIRE = RT.var("clojure.core", "require");

	@Inject
	ClojureServiceImpl(HookService hookService) {
		hookService.register(this);
		REQUIRE.invoke(CLOJURE_MAIN);
		this.scripts = new LinkedHashMap<>();

		//DO NOT MODIFY ORDER, THESE ARE ORDERED BY DEPENDENCIES IN CERTAIN CASES
		//TODO recursive script loading
		register("services.clj", new ClojureScript("services.clj"));
		register("players.clj", new ClojureScript("players.clj"));
		register("commands.clj", new ClojureScript("commands.clj"));
	}

	@Subscribe
	public final void invokeScriptsOnStartup(@Nonnull GameWorldLoadedEvent event) {
		LOGGER.info("Invoking scripts in order to increase execution speed.");

		scripts.keySet().forEach(k -> Errors.suppress().run(() -> invoke(k)));
		LOGGER.info("Finished invoking " + scripts.size() + " clojure scripts.");
	}

	@Override
	public final void registerAndInvoke(@Nonnull String name, @Nonnull ClojureScript script) {
		register(name, script);
		invoke(script);
	}

	@Override
	public final ClojureScript register(@Nonnull String name, @Nonnull ClojureScript script) {
		return scripts.put(name, script);
	}

	@Override
	public final Object invoke(@Nonnull ClojureScript script) {
		return invoke(script, "");
	}

	@Override
	public final Object invoke(@Nonnull String scriptName) {
		return invoke(scripts.get(scriptName));
	}

	@Override
	public final Object invoke(@Nonnull String scriptName, @Nonnull Object... args) {
		return invoke(scripts.get(scriptName), args);
	}

	@Override
	public final Object invoke(@Nonnull ClojureScript script, @Nonnull Object... args) {
		return AFn.applyToHelper(script.getMain(), RT.seq(Lists.asList(script.getPath(), args)));
	}

}
