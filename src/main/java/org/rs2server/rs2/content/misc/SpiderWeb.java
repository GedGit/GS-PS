package org.rs2server.rs2.content.misc;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.util.Misc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 
 * @author Tim
 *
 */
public class SpiderWeb {

	private static Map<Location, Webs> webList = new HashMap<>();

	public static boolean slash(Player player, GameObject obj) {
		if (obj == null)
			return false;
		final Webs web = webList.get(obj.getLocation());
		if (web == null)
			return false;
		if (player.getSettings().getLastTeleport() < 3000)
			return false;
		boolean success = Misc.random(4) < 2;
		Item weapon = player.getEquipment().get(Equipment.SLOT_WEAPON);
		if (weapon == null)
			return false;
		String name = weapon.getDefinition2().getName().toLowerCase();
		boolean requiredWep = name.contains("sword") || name.contains("axe") || name.contains("scimitar")
				|| name.contains("dagger");
		
		Optional<Item> sharpItem = Arrays.stream(player.getInventory().getItems()).filter(Objects::nonNull)
				.filter(i -> containsSharpObject(i.getDefinition2().getName())).findAny();

		if (!requiredWep && sharpItem.isPresent())
			requiredWep = true;
		
		if (!requiredWep) {
			player.sendMessage("You'll need something sharp in order to slash the web!");
			return false;
		}

		player.getActionSender().sendMessage("You attempt to cut the web.");
		player.getSettings().setLastTeleport(System.currentTimeMillis());
		player.playAnimation(Animation.create(451));
		if (success) {
			World.getWorld().submit(new Tickable(1) {

				@Override
				public void execute() {
					this.stop();
					World.getWorld().replaceObject(obj, null, 60);
					player.getActionSender().sendMessage("You successfully cut through the web.");
				}
			});
		} else
			player.sendMessage("You failed to cut through the web..");
		return true;
	}

	private static boolean containsSharpObject(String name) {
		if (name == null)
			return false;
		name = name.toLowerCase();
		return name.contains("knife") || name.contains("sword") || name.contains("axe") || name.contains("scimitar")
				|| name.contains("dagger");
	}

	private static class Webs {
		@SuppressWarnings("unused")
		private int id;
		@SuppressWarnings("unused")
		private int face;

		Webs(int id, int face) {
			this.id = id;
			this.face = face;
		}
	}

	static {
		webList.put(Location.create(3158, 3951), new Webs(733, 1));

		webList.put(Location.create(3095, 3957), new Webs(733, 1));

		webList.put(Location.create(3092, 3957), new Webs(733, 1));

		webList.put(Location.create(3105, 3958), new Webs(733, 3));

		webList.put(Location.create(3106, 3958), new Webs(733, 3));

		webList.put(Location.create(3210, 9898), new Webs(733, 1));
	}
}