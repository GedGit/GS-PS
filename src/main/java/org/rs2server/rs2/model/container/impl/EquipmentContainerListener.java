package org.rs2server.rs2.model.container.impl;

import org.rs2server.rs2.model.UpdateFlags.UpdateFlag;
import org.rs2server.rs2.model.container.Container;
import org.rs2server.rs2.model.container.ContainerListener;
import org.rs2server.rs2.model.player.Player;

/**
 * A ContainerListener which flags for an appearance update when the player
 * equips or removes an item.
 * @author Graham Edgecombe
 *
 */
public class EquipmentContainerListener implements ContainerListener {
	
	/**
	 * The player.
	 */
	private Player player;
	
	/**
	 * Creates the container listener.
	 * @param player The player.
	 */
	public EquipmentContainerListener(Player player) {
		this.player = player;
	}

	@Override
	public void itemChanged(Container container, int slot) {
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

	@Override
	public void itemsChanged(Container container) {
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

	@Override
	public void itemsChanged(Container container, int[] slots) {
		player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

}
