package org.rs2server.rs2.domain.service.api.content;

import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author Clank1337
 */
public interface GemBagService {

	enum Gems {
		SAPPHIRE("Sapphire", 1623),
		EMERALD("Emerald", 1621),
		RUBY("Ruby", 1619),
		DIAMOND("Diamond", 1617),
		DRAGONSTONE("Dragonstone", 1631);

		private final String name;
		private final int gemId;

		Gems(String name, int gemId) {
			this.name = name;
			this.gemId = gemId;
		}

		public static Optional<Gems> of(int gemId) {
			return Arrays.stream(Gems.values()).filter(i -> i.gemId == gemId).findAny();
		}

		public String getName() {
			return name;
		}

		public int getGemId() {
			return gemId;
		}

	}

	void deposit(@Nonnull Player player);

	void withdraw(@Nonnull Player player, Gems type);

	void check(@Nonnull Player player);

	int getAmount(@Nonnull Player player, Gems type);

	int getBagSize(@Nonnull Player player);
}
