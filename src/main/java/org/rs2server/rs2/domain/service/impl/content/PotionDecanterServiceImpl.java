package org.rs2server.rs2.domain.service.impl.content;

import com.google.inject.Inject;
import org.rs2server.rs2.content.Potion;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.domain.service.api.content.PotionDecanterService;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;

/**
 * Created by Tim on 12/21/2015.
 */
public class PotionDecanterServiceImpl implements PotionDecanterService {

	@Inject
	PotionDecanterServiceImpl(final HookService hookService) {
		hookService.register(this);
	}

	@Override
	public void decantPotions(@Nonnull Player player) {
		for (Potion p : Potion.values()) {
			int full = p.getFullId();
			int half = p.getHalfId();
			int quarter = p.getQuarterId();
			int threeQuarters = p.getThreeQuartersId();
			int totalDoses = 0;
			int remainder = 0;
			int totalEmptyPots = 0;

			if (player.getInventory().contains(threeQuarters)) {
				totalDoses += (3 * player.getInventory().getCount(threeQuarters));
				totalEmptyPots += player.getInventory().getCount(threeQuarters);
				player.getInventory().remove(new Item(threeQuarters, player.getInventory().getCount(threeQuarters)));
			}

			if (player.getInventory().contains(half)) {
				totalDoses += (2 * player.getInventory().getCount(half));
				totalEmptyPots += player.getInventory().getCount(half);
				player.getInventory().remove(new Item(half, player.getInventory().getCount(half)));
			}

			if (player.getInventory().contains(quarter)) {
				totalDoses += (player.getInventory().getCount(quarter));
				totalEmptyPots += player.getInventory().getCount(quarter);
				player.getInventory().remove(new Item(quarter, player.getInventory().getCount(quarter)));
			}

			if (totalDoses > 0) {
				if (totalDoses >= 4) {
					player.getInventory().add(new Item(full, totalDoses / 4));
				} else if (totalDoses == 3) {
					player.getInventory().add(new Item(threeQuarters, 1));
				} else if (totalDoses == 2) {
					player.getInventory().add(new Item(half, 1));
				} else if (totalDoses == 1) {
					player.getInventory().add(new Item(quarter, 1));
				}
				if ((totalDoses % 4) != 0) {
					totalEmptyPots -= 1;
					remainder = totalDoses % 4;
					if (remainder == 3)
						player.getInventory().add(new Item(threeQuarters, 1));
					else if (remainder == 2)
						player.getInventory().add(new Item(half, 1));
					else if (remainder == 1)
						player.getInventory().add(new Item(quarter, 1));
				}
				totalEmptyPots -= (totalDoses / 4);
				player.getInventory().add(new Item(230, totalEmptyPots));
			}
		}
	}
}