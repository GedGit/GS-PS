package org.rs2server.rs2.model.container.impl;

import org.rs2server.rs2.model.container.Container;
import org.rs2server.rs2.model.container.ContainerListener;
import org.rs2server.rs2.model.player.Player;

/**
 * A ContainerListener which updates a client-side interface to match the
 * server-side copy of the container.
 * @author Graham Edgecombe
 *
 */
public class InterfaceContainerListener implements ContainerListener {
	
	/**
	 * The player.
	 */
	private Player player;
	
	/**
	 * The interface id.
	 */
	private int interfaceId;
	
	/**
	 * The child id.
	 */
	private int childId;
	
	/**
	 * The interface type.
	 */
	private int type;
	
	/**
	 * Creates the container listener.
	 * @param player The player.
	 * @param interfaceId The interface id.
	 */
	public InterfaceContainerListener(Player player, int interfaceId, int childId, int type) {
		this.player = player;
		this.interfaceId = interfaceId;
		this.childId = childId;
		this.type = type;
	}

	@Override
	public void itemChanged(Container container, int slot) {
		player.getActionSender().sendUpdateItems(interfaceId, childId, type, container.toArray());
	}

	@Override
	public void itemsChanged(Container container) {
		player.getActionSender().sendUpdateItems(interfaceId, childId, type, container.toArray());
	}

	@Override
	public void itemsChanged(Container container, int[] slots) {
		player.getActionSender().sendUpdateItems(interfaceId, childId, type, container.toArray());
	}

}
