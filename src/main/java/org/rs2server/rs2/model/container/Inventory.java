package org.rs2server.rs2.model.container;

import org.rs2server.Server;
import org.rs2server.rs2.domain.service.api.content.StaffService;
import org.rs2server.rs2.model.GroundItem;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.player.Player;

import java.util.Optional;

/**
 * A utility class for the player's inventory.
 * 
 * @author Graham Edgecombe
 *
 */
public class Inventory {

	/**
	 * The size of the inventory container.
	 */
	public static final int SIZE = 28;

	@SuppressWarnings("unused")
	private final StaffService staffService;

	public Inventory() {
		this.staffService = Server.getInjector().getInstance(StaffService.class);
	}

	/**
	 * Inventory interface id.
	 */
	public static final int INTERFACE = 149;

	public static int removeRune(Mob mob, Item item) {
		StaffService service = Server.getInjector().getInstance(StaffService.class);
		if (item.getId() == 453 && mob.isPlayer()) {
			Player player = (Player) mob;
			if (player.getInventory().getCount(item.getId()) <= 0
					&& player.getDatabaseEntity().getCoalBagAmount() >= item.getCount()) {
				player.getDatabaseEntity()
						.setCoalBagAmount(player.getDatabaseEntity().getCoalBagAmount() - item.getCount());
				return 0;
			}
		}
		return service.removeRune((Player) mob, item);
	}

	public static boolean hasItem(Mob player, Item item) {
		if (player.isPlayer()) {
			Player p = (Player) player;
			if (p.getRunePouch().getCount(item.getId()) >= item.getCount()) {
				return true;
			}
		}
		if (Inventory.getCount(player, item.getId()) >= item.getCount()) {
			return true;
		}
		return false;
	}

	public static boolean hasItem(Mob player, Item[] items) {
		for (Item item : items) {
			if (Inventory.getCount(player, item.getId()) < item.getCount()) {
				return false;
			}
		}
		return true;
	}

	public static boolean containsRune(Mob player, Item item) { 
		StaffService service = Server.getInjector().getInstance(StaffService.class);
		return service.containsRune((Player) player, item);
	}

	public static boolean hasStaff(Mob player, Item item) {
		if (player.getEquipment().get(Equipment.SLOT_WEAPON) == null)
			return false;
		Item weapon = player.getEquipment().get(Equipment.SLOT_WEAPON);
		Optional<StaffService.Staff> staffOptional = StaffService.Staff.of(weapon.getId());
		if (staffOptional.isPresent()) {
			StaffService.Staff staff = staffOptional.get();
			for (StaffService.Runes runes : staff.getRunes()) {
				if (runes.getItemId() == item.getId())
					return true;
			}
		}
		return false;
	}

	public static int getCount(Mob mob, int id) {
		StaffService service = Server.getInjector().getInstance(StaffService.class);
		if (id == 453 && mob.isPlayer()) {
			Player player = (Player) mob;
			if (player.getInventory().getCount(453) <= 0)
				return player.getDatabaseEntity().getCoalBagAmount();
		}
		return service.getCount((Player) mob, new Item(id));
	}

	public static void addDroppable(Player player, Item item) {
		if (!player.getInventory().add(item))
			World.getWorld().register(new GroundItem(player.getName(), item, player.getLocation()), player);
	}

	public static boolean ownsItem(Player player, int i) {
		return player.getInventory().getCount(i) > 0 || player.getBank().getCount(i) > 0
				|| player.getEquipment().getCount(i) > 0;
	}
}