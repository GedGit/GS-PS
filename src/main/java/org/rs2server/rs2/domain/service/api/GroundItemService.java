package org.rs2server.rs2.domain.service.api;

import org.rs2server.rs2.domain.service.impl.GroundItemServiceImpl;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.region.Region;
import org.rs2server.rs2.tickable.Tickable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Clank1337
 */
public interface GroundItemService {

	class GroundItem {
		private Item item;
		private Location location;
		private Player player;
		private boolean global;
		private String owner;
		private boolean hour;

		public GroundItem(Item item, Location location, Player player, boolean global) {
			this(item, location, player.getName(), global);
		}

		public GroundItem(Item item, Location location, Player player, boolean global, boolean pvpDrop, boolean hour) {
			this (item, location, player, global, pvpDrop);
			this.setHour(hour);
		}

		public GroundItem(Location location, Item item, String player, boolean global, boolean pvpDrop, boolean hour) {
			this (item, location, player, global);
			item.setPvpDrop(pvpDrop);
			this.setHour(hour);
		}

		public GroundItem(Item item, Location location, String owner, boolean global) {
			this.item = item;
			this.location = location;
			this.owner = owner;
			this.global = global;
		}

		public GroundItem(Item item, Location location, Player player, boolean global, boolean pvpDrop) {
			this(item, location, player.getName(), global);
			item.setPvpDrop(pvpDrop);
		}

		@Override
		public String toString() {
			return "Item: " + item + ", " + location + ", " + owner + ", " + global;
		}

		public Item getItem() {
			return item;
		}

		public Location getLocation() {
			return location;
		}

		public Player getPlayer() {
			return player;
		}

		public boolean isGlobal() {
			return global;
		}

		public void setGlobal(boolean global) {
			this.global = global;
		}

		public String getOwner() {
			return owner;
		}

		public boolean isHour() { return hour;}

		public void setHour(boolean hour) {
			this.hour = hour;
		}
	}

	class SpawnedGroundItem extends GroundItem {

		public SpawnedGroundItem(Item item, Location location, String owner, boolean global) {
			super(item, location, owner, global);
		}
	}

	class GroundItemTick extends Tickable {

		private int ticks = 0;
		private int maxTicks = 400;
		private GroundItem item;

		public GroundItemTick(GroundItem item) {
			super(1);
			this.item = item;
			if (item.isHour()) {
				maxTicks = 6000;
			}
		}

		@Override
		public void execute() {
			if (ticks == maxTicks - 200) {
				Optional<GroundItem> groundItemOptional = GroundItemServiceImpl.GROUND_ITEMS.keySet().stream().
						filter(g -> g.getItem().getId() == item.getItem().getId()).filter(i -> i.getLocation().equals(item.getLocation())).findAny();
				if (groundItemOptional.isPresent()) {
					GroundItem groundItem = groundItemOptional.get();
					if (groundItem.getItem().getDefinition() != null && groundItem.getItem().getDefinition().isTradable()) {
						Region region = World.getWorld().getRegionManager().getRegionByLocation(groundItem.getLocation());
						region.getPlayers().stream().filter(p -> !p.getName().equals(groundItem.getOwner())).forEach(i -> i.getActionSender().sendGroundItem(groundItem));
						groundItem.setGlobal(true);
					}
				}
			}
			if (ticks == maxTicks) {
				Region region = World.getWorld().getRegionManager().getRegionByLocation(item.getLocation());
				region.getPlayers().stream().filter(Objects::nonNull).forEach(i -> i.getActionSender().removeGroundItem(item));

				GroundItemServiceImpl.GROUND_ITEMS.remove(item);
				this.stop();
			}
			ticks++;
		}
	}

	void createGroundItem(@Nullable Player player, @Nonnull GroundItem groundItem);

	void removeGroundItem(@Nonnull GroundItem groundItem);

	void pickupGroundItem(@Nonnull Player player, Item item, Location location);

	void teleGrabItem(@Nonnull Player player, @Nonnull GroundItem item);

	void refresh(@Nonnull Player player);

	void addItemForRegionalPlayers(@Nonnull Player player, GroundItem item);

	void removeItemForRegionalPlayers(@Nonnull Player player, GroundItem item);

	void registerGroundItemEvent(GroundItem item);

	Optional<GroundItem> getGroundItem(int id, Location location);

	List<GroundItem> getPlayerDroppedItems(String name);
}
