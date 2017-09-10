package org.rs2server.rs2.model.container.impl;

import org.rs2server.rs2.model.container.Container;
import org.rs2server.rs2.model.container.ContainerListener;
import org.rs2server.rs2.model.player.Player;

public class BankContainerListener implements ContainerListener {
	
	/**
	 * The player.
	 */
	private Player player;
	
	public BankContainerListener(Player player) {
		this.player = player;
	}

	@Override
	public void itemChanged(Container container, int slot) {
		player.getBanking().sendTabConfig();
	}

	@Override
	public void itemsChanged(Container container) {
		player.getBanking().sendTabConfig();
	}

	@Override
	public void itemsChanged(Container container, int[] slots) {
		player.getBanking().sendTabConfig();
	}

}
