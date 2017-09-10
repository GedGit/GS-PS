package org.rs2server.rs2.domain.service.api.content;

import org.rs2server.rs2.model.player.pc.PestControlBoat;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a pest control service.
 * @author twelve
 */
public interface PestControlService {

	enum PestControlItem {

		ATTACK_1(86, 0, 1),
		ATTACK_10(101, 0, 10),
		ATTACK_100(108, 0, 100),

		STRENGTH_1(87, 2, 1),
		STRENGTH_10(102, 2, 10),
		STRENGTH_100(109, 2, 100),

		DEFENCE_1(88, 1, 1),
		DEFENCE_10(103, 1, 10),
		DEFENCE_100(110, 1, 100),

		RANGED_1(89, 4, 1),
		RANGED_10(104, 4, 10),
		RANGED_100(111, 4, 100),

		MAGIC_1(90, 6, 1),
		MAGIC_10(105, 6, 10),
		MAGIC_100(112, 6, 100),

		HITPOINTS_1(91, 3, 1),
		HITPOINTS_10(106, 3, 10),
		HITPOINTS_100(113, 3, 100),

		PRAYER_1(92, 5, 1),
		PRAYERS_10(107, 5, 10),
		PRAYER_100(114, 5, 100),
		
		VOID_TOP(94, 8839, 250),
		VOID_BOTTOM(95, 8840, 250),
		VOID_GLOVES(96, 8842, 150),
		VOID_MAGE_HELM(119, 11663, 200),
		VOID_RANGE_HELM(120, 11664, 200),
		VOID_MELEE_HELM(121, 11665, 200),
		HERB_PACK(97, 11738, 30),
		MINERAL_PACK(98, 453, 15),
		SEED_PACK(100, 5320, 15),
		VOID_KNIGHT_MACE(93, 8841, 250),
		VOID_KNIGHT_SEAL(122, 11666, 10);

		private final int buttonId;
		private final int itemId;
		private final int cost;

		PestControlItem(int buttonId, int itemId, int cost) {
			this.buttonId = buttonId;
			this.itemId = itemId;
			this.cost = cost;
		}

		private static Map<Integer, PestControlItem> shopButtons = new HashMap<>();
		private static Map<Integer, PestControlItem> shopItems = new HashMap<>();

		public static PestControlItem ofButton(int id) {
			return shopButtons.get(id);
		}

		public static PestControlItem ofItem(int id) {
			return shopItems.get(id);
		}

		static {
			for (PestControlItem shopItem : PestControlItem.values()) {
				shopButtons.put(shopItem.getButtonId(), shopItem);
			}
			for (PestControlItem shopItem : PestControlItem.values()) {
				shopItems.put(shopItem.getItemId(), shopItem);
			}
		}

		public int getButtonId() {
			return buttonId;
		}

		public int getItemId() {
			return itemId;
		}

		public int getCost() {
			return cost;
		}
	}

    void setPestControlPoints(@Nonnull Player player, int points);

    void openShop(@Nonnull Player player);

    List<PestControlBoat> getBoats();

    void addBoatMember(PestControlBoat boat, @Nonnull Player player);

    void removeBoatMember(PestControlBoat boat, @Nonnull Player player);

	boolean containsPlayer(@Nonnull Player player);

	void handleDeath(@Nonnull Player player);

}
