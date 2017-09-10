package org.rs2server.rs2.domain.service.api.content;

import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.container.Container;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author Clank1337
 */
public interface ItemService {


	enum DegradeOnDeath {
		
		BERSERKER_RING_I(11773, 6737),
		WARRIOR_RING_I(11772, 6735),
		ARCHERS_RING_I(11771, 6733),
		SEERS_RING_I(11770, 6731),
		VOLCANIC_WHIP(12773, 4151),
		FROZEN_ICE_WHIP(12774, 4151),
		INFERNAL_PICK(13243, 13233),
		INFERNAL_AXE(13241, 13233);

		private final int itemId;
		private final int toId;

		DegradeOnDeath(int itemId, int toId) {
			this.itemId = itemId;
			this.toId = toId;
		}

		public static Optional<DegradeOnDeath> of(int itemId) {
			return Arrays.stream(DegradeOnDeath.values()).filter(i -> i.itemId == itemId).findAny();
		}

		public int getItemId() {
			return itemId;
		}

		public int getToId() {
			return toId;
		}
	}

	/**
	 * Checks if an item is fully degraded (last item in the degradation process)
	 * @param item The item.
	 * @return true is fully degraded, false if not.
	 */
	boolean isFullyDegraded(@Nonnull Item item);

	void degradeItem(@Nonnull Mob mob, Item item);

	void upgradeItem(@Nonnull Player player, Item item, Item parameter);

	/**
	 * Returns the amount of charges left for a given item.
	 * @param player The player.
	 * @param item The item.
	 * @return The amount of charges left.
	 */
	int getCharges(@Nonnull Player player, @Nonnull Item item);

	/**
	 * Sets the amount of charges left for a given item.
	 * @param player The player.
	 * @param item The item.
	 * @param amount The amount of charges left.
	 */
	void setCharges(@Nonnull Player player, @Nonnull Item item, int amount);

	void setChargesWithItem(@Nonnull Player player, @Nonnull Item item, @Nonnull Item with, int chargeAmount);

	int getChargedItem(@Nonnull Player player, @Nonnull Item item);

	int getNetWorth(@Nonnull Player player);

	void exchangeToNote(@Nonnull Player player, @Nonnull Item item);

	void exchangeToUnNote(@Nonnull Player player, @Nonnull Item item);

	Container[] getItemsKeptOnDeath(@Nonnull Player player);

	void gambleFireCapes(@Nonnull Player player, int ammount);

	boolean playerOwnsItem(@Nonnull Player player, int id);
}
