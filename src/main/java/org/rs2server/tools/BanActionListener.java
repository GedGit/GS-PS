package org.rs2server.tools;

import org.rs2server.GUI;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.util.XMLController;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Optional;

public final class BanActionListener implements ActionListener {

	private final GUI gui;

	public BanActionListener(GUI gui) {
		this.gui = gui;
	}
	@Override
	public void actionPerformed(ActionEvent e) {

		String selected = gui.getList().getSelectedValue();
		if (selected != null) {
			Optional<Player> playerOption = World.getWorld().getPlayers().stream().filter(p -> p.getName().equals(selected)).findAny();
			if (playerOption.isPresent()) {
				try {
					Player player = playerOption.get();
					File file = new File("data/bannedUsers.xml");
					List<String> bannedUsers = null;
					bannedUsers = XMLController.readXML(file);
					bannedUsers.add(player.getName());
					XMLController.writeXML(bannedUsers, file);
					player.getActionSender().sendLogout();
					player.getActionSender().sendMessage(
							"Successfully banned " + player.getName() + ".");
					gui.getListModel().remove(gui.getList().getSelectedIndex());
					gui.getList().setSelectedIndex(-1);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}

}
