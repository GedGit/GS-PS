package org.rs2server.tools;

import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.player.Player;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LocationActionListener implements ActionListener {
	
	private JFrame frame;
	private Player player;
	
	public LocationActionListener(Player player, JFrame frame) {
		this.player = player;
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String location = JOptionPane.showInputDialog(frame,
                "Location:", null);
		String[] absolute = location.split(",");
		

 		int x = Integer.parseInt(absolute[0]);
		int y = Integer.parseInt(absolute[1]);
		int z = player.getPlane();
		if (absolute.length >= 3) {
			z = Integer.parseInt(absolute[2]);
		}
		Location loc = Location.create(x, y, z);// thats already guarenteed by here as if it wasnt at least 3 it would throw exception
		player.setTeleportTarget(loc);
	}

}
