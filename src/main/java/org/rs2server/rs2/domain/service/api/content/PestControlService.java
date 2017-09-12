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

		ATTACK_X1(86, 0, 1, 1),
		ATTACK_X10(101, 0, 10, 2),
		ATTACK_X100(108, 0, 100, 3),

		STRENGTH_X1(87, 2, 1, 4),
		STRENGTH_X10(102, 2, 10, 5),
		STRENGTH_X100(109, 2, 100, 6),

		DEFENCE_X1(88, 1, 1, 7),
		DEFENCE_X10(103, 1, 10, 8),
		DEFENCE_X100(110, 1, 100, 9),

		RANGED_X1(89, 4, 1, 10),
		RANGED_X10(104, 4, 10, 11),
		RANGED_X100(111, 4, 100, 12),

		MAGIC_X1(90, 6, 1, 13),
		MAGIC_X10(105, 6, 10, 14),
		MAGIC_X100(112, 6, 100, 15),

		HITPOINTS_X1(91, 3, 1, 16),
		HITPOINTS_X10(106, 3, 10, 17),
		HITPOINTS_X100(113, 3, 100, 18),

		PRAYER_X1(92, 5, 1, 19),
		PRAYER_X10(107, 5, 10, 20),
		PRAYER_X100(114, 5, 100, 21),
		
		VOID_TOP(94, 8839, 250, 0),
		VOID_BOTTOM(95, 8840, 250, 0),
		VOID_GLOVES(96, 8842, 150, 0),
		VOID_MAGE_HELM(119, 11663, 200, 0),
		VOID_RANGE_HELM(120, 11664, 200, 0),
		VOID_MELEE_HELM(121, 11665, 200, 0),
		HERB_PACK(97, 11738, 30, 0),
		MINERAL_PACK(98, 453, 15, 0),
		SEED_PACK(100, 5320, 15, 0),
		VOID_KNIGHT_MACE(93, 8841, 250, 0),
		VOID_KNIGHT_SEAL(122, 11666, 10, 0);

		private final int buttonId;
		private final int itemId;
		private final int cost;
		private final int configId;

		PestControlItem(int buttonId, int itemId, int cost, int configId) {
			this.buttonId = buttonId;
			this.itemId = itemId;
			this.cost = cost;
			this.configId = configId;
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

		public int getConfigId() { 
			return configId;
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
