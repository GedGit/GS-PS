package org.rs2server.rs2.domain.service.api.content.bounty;


import org.rs2server.rs2.content.api.GameInterfaceButtonEvent;
import org.rs2server.rs2.content.api.GamePlayerLogoutEvent;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author Clank1337
 * @author twelve
 */
public interface BountyHunterService {

	enum BountyShopRewards {
		
		DRAGON_SCIMITAR(4587, 300000),
		DRAGON_LONGSWORD(1305, 300000),
		DRAGON_DAGGER(1215, 90000),
		DRAGON_BATTLEAXE(1377, 600000),
		DRAGON_MACE(1434, 150000),
		HELM_OF_NEITIZNOT(10828, 150000),
		BERSERKER_HELM(3751, 234000),
		WARRIOR_HELM(3753, 234000),
		ARCHER_HELM(3749, 234000),
		FARSEER_HELM(3755, 234000),
		MYSTIC_ROBE_TOP(4091, 360000),
		MYSTIC_ROBE_BOTTOM(4093, 240000),
		MYSTIC_HAT(4089, 45000),
		MYSTIC_GLOVES(4095, 30000),
		MYSTIC_BOOTS(4097, 30000),
		RUNE_PLATEBODY(1127, 255000),
		RUNE_PLATELEGS(1079, 192000),
		RUNE_PLATESKIRT(1093, 192000),
		BOLT_RACK(4740, 360),
		RUNE_ARROW(892, 600),
		ADAMANT_ARROW(890, 240),
		CLIMBING_BOOTS(3105, 5400),
		FROZEN_WHIP_MIX(12769, 500000),
		VOLCANIC_WHIP_MIX(12771, 500000),
		RUNE_POUCH(12791, 1200000),
		LOOTING_BAG(11941, 150000);


		private final int id;
		private final int cost;

		BountyShopRewards(int id, int cost) {
			this.id = id;
			this.cost = cost;
		}

		public static Optional<Integer> cost(Item item) {
			return Arrays.stream(values()).filter(r -> r.id == item.getId()).map(r -> r.cost).findFirst();
		}

		public final int getId() {
			return id;
		}

		public final int getCost() {
			return cost;
		}
	}
	enum Emblems {
		TIER_ONE(12746, 50000), 
		TIER_THREE(12748, 100000),
		TIER_FOUR(12749, 200000),
		TIER_FIVE(12750, 400000),
		TIER_SIX(12751, 750000),
		TIER_SEVEN(12752, 1200000),
		TIER_EIGHT(12753, 1750000),
		TIER_NINE(12754, 2500000),
		TIER_TEN(12755, 3500000),
		TIER_ELEVEN(12756, 5000000);

		private final int id;
		private final int cost;

		Emblems(int id, int cost) {
			this.id = id;
			this.cost = cost;
		}

		public final int getCost() {
			return cost;
		}

		public final int getId() {
			return id;
		}

		public static Optional<Emblems> of(int id) {
			return Arrays.stream(values()).filter(e -> e.getId() == id).findAny();
		}
	}

	enum Wealth {
		NONE, VERY_LOW, LOW, MEDIUM, HIGH, VERY_HIGH;

		public static Wealth of(int netWorth) {

			if (netWorth <= 150000) {
				return VERY_LOW;
			}

			if (netWorth <= 400000) {
				return LOW;
			}

			if (netWorth <= 800000) {
				return MEDIUM;
			}

			if (netWorth <= 1500000) {
				return HIGH;
			}

			return VERY_HIGH;
		}
	}

	void increaseKillCount(@Nonnull Player player);

	void increaseDeathCount(@Nonnull Player player);

	void onPlayerLogout(@Nonnull GamePlayerLogoutEvent event);

	void onBountyShopClick(@Nonnull GameInterfaceButtonEvent event);

	void openWidget(@Nonnull Player player);

	void updateWidget(@Nonnull Player player);

	void resetWidget(@Nonnull Player player);

	void assignTarget(@Nonnull Player player);

	void sendHintIcons(@Nonnull Player player);

	void resetHintIcons(@Nonnull Player player);

	void setWealth(@Nonnull Player player, @Nonnull Wealth wealth);

	void openBountyShop(@Nonnull Player player);

	void tick(@Nonnull Player player);
}
