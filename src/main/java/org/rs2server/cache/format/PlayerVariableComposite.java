package org.rs2server.cache.format;

import com.diffplug.common.base.Errors;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.rs2server.cache.CacheManager;

import java.nio.ByteBuffer;

/**
 * Represents data that composes a Varp cache file.
 *
 * @author twelve
 */
public final class PlayerVariableComposite {

	private int config;
	private int leastSignificant;
	private int mostSignificant;
	private final int id;
	public static final Multimap<Integer, PlayerVariableComposite> CONFIG_VARIABLE_MAP = ArrayListMultimap.create();

	public PlayerVariableComposite(int id) {
		this.id = id;
		ByteBuffer data = Errors.suppress().getWithDefault(() -> ByteBuffer.wrap(CacheManager.getData(2, 14, id)), null);
		if (data != null) {
			while (data.hasRemaining()) {
				int op = data.get() & 0xFF;
				if (op == 0)
					return;
				decode(data, op);
			}
		}
	}

	private void decode(ByteBuffer data, int op) {
		if (op == 1) {
			config = data.getShort() & 0xFFFF;
			leastSignificant = data.get() & 0xFF;
			mostSignificant = data.get() & 0xFF;
			CONFIG_VARIABLE_MAP.put(config, this);
		}
	}

	public int getId() {
		return id;
	}

	public int getLeastSignificant() {
		return leastSignificant;
	}

	public int getMostSignificant() {
		return mostSignificant;
	}

	public int getConfig() {
		return config;
	}
}
