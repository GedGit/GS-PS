package org.rs2server.rs2.domain.service.api.content;

import java.util.Arrays;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.rs2server.rs2.content.api.GameInterfaceButtonEvent;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.player.Player;

public interface TournamentSuppliesService {
	
	enum TournamentRewards {
		Ahrims_robetop(4712, 1000),
		Ahrims_hood(4708, 1000),
		Ahrims_robeskirt(4714, 1000),
		Ahrims_staff(4710, 1000),
		Dharoks_platebody(4720, 1000),
		Dharoks_helm(4716, 1000),
		Dharoks_platelegs(4722, 1000),
		Dharoks_greateaxe(4718, 1000);


		private final int id;
		private final int cost;

		TournamentRewards(int id, int cost) {
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
	
	void onTournamentShopClick(@Nonnull GameInterfaceButtonEvent event);


	void openTournamentShop(@Nonnull Player player);

}