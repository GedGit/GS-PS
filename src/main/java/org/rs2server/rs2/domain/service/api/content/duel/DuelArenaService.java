package org.rs2server.rs2.domain.service.api.content.duel;

import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author Clank1337
 * @author Twelve
 */
public interface DuelArenaService {

	enum VarpForButton {
		
		HELM(Equipment.SLOT_HELM, 0),

		CAPE(Equipment.SLOT_CAPE, 1),

		AMULET(Equipment.SLOT_AMULET, 2),

		ARROWS(Equipment.SLOT_ARROWS, 3),

		WEAPON(Equipment.SLOT_WEAPON, 4),

		PLATE(Equipment.SLOT_CHEST, 5),

		SHIELD(Equipment.SLOT_SHIELD, 6),

		BOTTOMS(Equipment.SLOT_BOTTOMS, 7),

		RING(Equipment.SLOT_RING, 8),

		BOOTS(Equipment.SLOT_BOOTS, 9),

		GLOVES(Equipment.SLOT_GLOVES, 10);

		private final int equipmentIndex;
		private final int button;

		VarpForButton(int equipmentIndex, int button) {
			this.equipmentIndex = equipmentIndex;
			this.button = button;
		}

		public static Optional<VarpForButton> of(int button) {
			return Arrays.stream(VarpForButton.values()).filter(v -> v.getButton() == button).findAny();
		}


		public int getEquipmentIndex() {
			return equipmentIndex;
		}

		public int getButton() {
			return button;
		}
	}

	void openRulesInterface(@Nonnull Player player, @Nonnull Player partner);

	void openConfirmationInterface(@Nonnull Player player, @Nonnull Player partner);

	void handleEquipmentRules(@Nonnull Player player, int button);

	void updateRulesInterface(@Nonnull Player player);
}
