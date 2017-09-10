package org.rs2server.rs2.varp;

import org.rs2server.cache.format.PlayerVariableComposite;
import javax.annotation.concurrent.Immutable;

/**
 * Represents a {@link PlayerVariable}, which is simply a box around a Varp composite.
 *
 * @author twelve
 */
@Immutable
public final class PlayerVariable {

	private final PlayerVariableComposite composite;

	PlayerVariable(PlayerVariableComposite composite) {
		this.composite = composite;
	}

	public static PlayerVariable of(int id) {
		return new PlayerVariable(PlayerVariables.compositeOf(id));
	}

	public final PlayerVariableComposite getComposite() {
		return composite;
	}
}
