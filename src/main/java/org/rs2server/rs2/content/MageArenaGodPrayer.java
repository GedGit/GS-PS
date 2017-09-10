package org.rs2server.rs2.content;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.GroundItem;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;

import java.util.HashMap;
import java.util.Map;

public class MageArenaGodPrayer {

	static Map<Integer, Integer> contains = new HashMap<>();

	public static boolean godPrayer(Player player, GameObject obj) {
		if (!contains.containsKey(obj.getId()))
			return false;
		if (containsGodCape(player) || player.hasAttribute("droppedGodCape")) {
			player.getActionSender().sendMessage("You already own a God Cape.");
			return false;
		}
		player.setAttribute("busy", true);
		player.playAnimation(Animation.create(645));

		World.getWorld().submit(new Tickable(4) {

			@Override
			public void execute() {
				this.stop();
				player.removeAttribute("busy");
				World.getWorld().createGroundItem(new GroundItem(player.getName(), false,
						new Item(contains.get(obj.getId())), player.getLocation()), player);
			}

		});
		return true;
	}

	public static boolean containsGodCape(Player player) {
		boolean sara = player.getBank().contains(2412) || player.getInventory().contains(2412)
				|| player.getEquipment().contains(2412);
		boolean guthix = player.getBank().contains(2413) || player.getInventory().contains(2413)
				|| player.getEquipment().contains(2413);
		boolean zammy = player.getBank().contains(2414) || player.getInventory().contains(2414)
				|| player.getEquipment().contains(2414);
		return sara || guthix || zammy;
	}

	static {
		contains.put(2873, 2412);

		contains.put(2874, 2414);

		contains.put(2875, 2413);
	}
}
