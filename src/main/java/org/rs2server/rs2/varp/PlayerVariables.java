package org.rs2server.rs2.varp;

import org.rs2server.cache.format.PlayerVariableComposite;

import java.util.HashMap;
import java.util.Map;

/**
 * Miscellaneous code related to {@link PlayerVariable}'s.
 * @author Twelve
 */
public final class PlayerVariables {

	public static final int[] BIT_MAX_VALUES = new int[32];
	public static final Map<Integer, PlayerVariableComposite> VARP_COMPOSITE_MAP = new HashMap<>();

	/**
	 * Finds or constructs a {@link PlayerVariableComposite} by a specified id.
	 * @param id The id of the composite to lookup.
	 * @return A {@link PlayerVariableComposite}
	 */
	public static PlayerVariableComposite compositeOf(int id) {
		PlayerVariableComposite composite = PlayerVariables.VARP_COMPOSITE_MAP.get(id);

		if (composite != null) {
			return composite;
		}

		composite = new PlayerVariableComposite(id);
		PlayerVariables.VARP_COMPOSITE_MAP.put(id, composite);
		return composite;
	}

	static {
		int i = 2;
		for (int i_0_ = 0; i_0_ < 32; i_0_++) {
			BIT_MAX_VALUES[i_0_] = i - 1;
			i += i;
		}
	}

}
