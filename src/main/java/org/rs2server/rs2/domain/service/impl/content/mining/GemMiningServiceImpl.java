package org.rs2server.rs2.domain.service.impl.content.mining;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import org.rs2server.rs2.content.api.GameObjectActionEvent;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.domain.service.api.content.mining.GemMiningService;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.player.Player;

import java.util.*;

/**
 * @author Clank1337
 */
public class GemMiningServiceImpl implements GemMiningService {

	private static final Map<Item, Double> GEMS = new HashMap<>();

	private final Random random;


	@Inject
	GemMiningServiceImpl(HookService service) {
		service.register(this);
		this.random = new Random();
	}

	@SuppressWarnings("incomplete-switch")
	@Subscribe
	public void onObjectClick(GameObjectActionEvent event) {
		Player player = event.getPlayer();
		GameObject object = event.getGameObject();
		if (object.getId() != 7464 && object.getId() != 7463) {
			return;
		}
		switch (event.getActionType()) {
			case OPTION_1:
				player.getActionQueue().addAction(new GemMiningAction(player, object));
				break;
		}
	}


	@Override
	public Item getReward() {
		List<Item> shuffled = new ArrayList<>();

		shuffled.addAll(GEMS.keySet());

		Collections.shuffle(shuffled);


		List<Item> rewards = new ArrayList<>(10);

		outer:
		while(rewards.size() < 1) {
			for (Item i : shuffled) {
				if (rewards.size() >= 1) {
					break outer;
				}

				if (random.nextInt(100) <= GEMS.get(i)) {
					rewards.add(i);
				}
			}
		}
		return rewards.get(0);
	}

	static {
		GEMS.put(new Item(1625), 25.5);

		GEMS.put(new Item(1627), 13.5);

		GEMS.put(new Item(1629), 8.5);

		GEMS.put(new Item(1623), 4.5);

		GEMS.put(new Item(1621), 3.0);

		GEMS.put(new Item(1619), 2.5);

		GEMS.put(new Item(1617), 1.0);
	}
}
