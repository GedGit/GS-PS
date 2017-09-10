package org.rs2server.tools;

import org.rs2server.GUI;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.player.Player;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

public final class KickActionListener implements ActionListener {

	private final GUI gui;

	public KickActionListener(GUI gui) {
		this.gui = gui;
	}
	@Override
	public void actionPerformed(ActionEvent e) {

		String selected = gui.getList().getSelectedValue();
		if (selected != null) {
			Optional<Player> playerOption = World.getWorld().getPlayers().stream().filter(p -> p.getName().equals(selected)).findAny();
			if (playerOption.isPresent()) {
				Player player = playerOption.get();
				player.getActionSender().sendLogout();
				gui.getListModel().remove(gui.getList().getSelectedIndex());
				gui.getList().setSelectedIndex(-1);
			}
		}
	}

}
