package org.rs2server.rs2.domain.service.api.clojure;

import clojure.lang.IFn;
import clojure.lang.RT;

import javax.annotation.concurrent.Immutable;

/**
 * @author twelve
 */
@Immutable
public final class ClojureScript {

	private static final String ROOT_PATH = "./src/main/clojure/";
	private final String path;
	private final IFn main;

	public ClojureScript(String path) {
		this.path = ROOT_PATH + path;
		this.main = RT.var("clojure.main", "main");
	}

	public final String getPath() {
		return path;
	}

	public final IFn getMain() {
		return main;
	}
}
