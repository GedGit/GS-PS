package org.rs2server.rs2.domain.service.api.content;

import org.rs2server.rs2.model.player.Player;

import java.util.Arrays;
import java.util.Optional;

import javax.annotation.Nonnull;

/**
 * @author Vichy
 */
public interface HerbSackService {

	/**
	 * Enum containing all possible herbs that are possible to be added to the sack.
	 * 
	 * @author Vichy
	 *
	 */
	public enum HERBS {

		GRIMY_GUAM("Guam", 199), GRIMY_MARRENTILL("Marrentill", 201), GRIMY_TARROMIN("Tarromin",
				203), GRIMY_HARRALANDER("Harralander", 205), GRIMY_RANARR_WEED("Ranarr", 207), GRIMY_IRIT_LEAF("Irit",
						209), GRIMY_AVANTOE("Avantoe", 211), GRIMY_KWUARM("Kwuarm", 213), GRIMY_CADANTINE("Cadantine",
								215), GRIMY_DWARF_WEED("Dwarf weed", 217), GRIMY_TORSTOL("Torstol",
										219), GRIMY_LANTADYME("Lantadyme", 2485), GRIMY_TOADFLAX("Toadflax",
												3049), GRIMY_SNAPDRAGON("Snapdragon", 3051);

		private final String name;
		private final int herbId;

		HERBS(String name, int herbId) {
			this.name = name;
			this.herbId = herbId;
		}

		public static Optional<HERBS> of(int herbId) {
			return Arrays.stream(HERBS.values()).filter(i -> i.herbId == herbId).findAny();
		}

		public String getName() {
			return name;
		}

		public int getHerbId() {
			return herbId;
		}
	}

	void deposit(@Nonnull Player player);

	void withdraw(@Nonnull Player player, HERBS herb);

	void check(@Nonnull Player player);

	int getAmount(@Nonnull Player player, HERBS herb);

	int getBagSize(@Nonnull Player player);
}
