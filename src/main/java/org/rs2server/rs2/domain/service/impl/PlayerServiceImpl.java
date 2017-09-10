package org.rs2server.rs2.domain.service.impl;

import org.rs2server.rs2.domain.service.api.PlayerService;
import org.rs2server.rs2.model.GroundItem;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * @author tommo
 */
public class PlayerServiceImpl implements PlayerService {

	@Override
	public boolean hasItemInInventoryOrBank(@Nonnull Player player, @Nonnull Item item) {
		Objects.requireNonNull(player, "player");
		Objects.requireNonNull(item, "item");

		return player.getInventory().contains(item.getId()) || player.getBank().contains(item.getId());
	}

	/**
	 * Gives a player item if the player has space in their inventory, otherwise drops
	 * a ground item at the player's location if specified.
	 * @param player The player
	 * @param item The item to give
	 * @param fallbackToGround true if the item should be dropped as a ground item if the player has no space in their inventory
	 */
	public void giveItem(@Nonnull final Player player, @Nonnull final Item item, boolean fallbackToGround) {
		Objects.requireNonNull(player, "player");
		Objects.requireNonNull(item, "item");

		if (player.getInventory().add(item)) {//i cant vote again so ur testing go vote
			//success
		} else if (fallbackToGround) {
			World.getWorld().createGroundItem(new GroundItem(player.getName(), item, player.getLocation()), player);
		}
	}

	public String getIpAddress(@Nonnull final Player player) {
		Objects.requireNonNull(player, "player");
		return player.getSession().getRemoteAddress().toString().split(":")[0].replaceFirst("/", "");
	}

	@Nullable
	@Override
	public Player getPlayer(@Nonnull String name) {
		Objects.requireNonNull(name, "name");
		return World.getWorld().getPlayers().stream().filter(p -> p != null && p.getName().equalsIgnoreCase(name))
				.findFirst().orElse(null);
	}

}
