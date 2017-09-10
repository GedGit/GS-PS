package org.rs2server.rs2.domain.service.impl.skill;

import org.rs2server.rs2.domain.service.api.skill.MiningService;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.container.Container;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.skills.Mining;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Random;

/**
 * @author tommo
 */
public class MiningServiceImpl implements MiningService {

	private static final Random rand = new Random();

	@Override
	public boolean hasPickaxe(@Nonnull Player player) {
		return getPickaxe(player) != null;
	}

	@Nullable
	@Override
	public Mining.PickAxe getPickaxe(@Nonnull Player player) {
		Objects.requireNonNull(player, "player");

		for(final Mining.PickAxe pickaxe : Mining.PickAxe.values()) {
			if((player.getInventory().contains(pickaxe.getId()) || player.getEquipment().contains(pickaxe.getId()))) {
				return pickaxe;
			}
		}

		return null;
	}

	@Override
	public float getProspectorKitExperienceModifier(@Nonnull Player player) {
		float modifier = 1f;
		final Container eq = player.getEquipment();
		if (eq.contains(12013)) {//helmet
			modifier += 0.04f;
		}
		if (eq.contains(12014)) {//jacket
			modifier += 0.08f;
		}
		if (eq.contains(12015)) {//legs
			modifier += 0.06f;
		}
		if (eq.contains(12016)) {//boots
			modifier += 0.02f;
		}
		return modifier;
	}

	@Override
	public Item getRandomChanceGem() {
		float chance = rand.nextFloat();
		if (chance <= 0.03f) {
			int rnd = rand.nextInt(4);
			if (rnd == 0) {
				return new Item(1617, 1); // Uncut diamond
			} else if (rnd == 1) {
				return new Item(1619, 1); // Uncut ruby
			} else if (rnd == 2) {
				return new Item(1621, 1); // Uncut emerald
			} else if (rnd == 3) {
				return new Item(1623, 1); // Uncut sapphire
			}
		}

		return null;
	}

}
