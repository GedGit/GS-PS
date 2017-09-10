package org.rs2server.rs2.model.container.impl;

import org.rs2server.rs2.model.container.Container;
import org.rs2server.rs2.model.container.ContainerListener;
import org.rs2server.rs2.model.player.Player;

/**
 * @author Clank1337
 */
public class ShopContainerListener implements ContainerListener {

	/**
	 * Represents the player using the container
	 */
	private final Player player;
	/**
	 * Represents the shop being used
	 */
	private final int shopId;
	/**
	 * Repressents the interface being updated
	 */
	private final int interfaceId;
	/**
	 * Represents the child being updated
	 */
	private final int childId;
	/**
	 * Represents the type of interface being updated
	 */
	private final int type;


	public ShopContainerListener(Player player, int shopId, int interfaceId, int childId, int type) {
		this.player = player;
		this.shopId = shopId;
		this.interfaceId = interfaceId;
		this.childId = childId;
		this.type = type;
	}

	@Override
	public void itemChanged(Container container, int slot) {
		if (player.getInterfaceState().getOpenShop() != shopId) {
			return;
		}
		player.getActionSender().sendUpdateItems(interfaceId, childId, type, container.toArray());
	}

	@Override
	public void itemsChanged(Container container) {
		if (player.getInterfaceState().getOpenShop() != shopId) {
			return;
		}
		player.getActionSender().sendUpdateItems(interfaceId, childId, type, container.toArray());
	}

	@Override
	public void itemsChanged(Container container, int[] slots) {
		if (player.getInterfaceState().getOpenShop() != shopId) {
			return;
		}
		player.getActionSender().sendUpdateItems(interfaceId, childId, type, container.toArray());
	}
}
