package org.rs2server.tools;

import org.rs2server.cache.format.CacheItemDefinition;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.player.Player;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GiveItemActionListener implements ActionListener{
	
	
	private Player player;
	private JFrame frame;
	
	public GiveItemActionListener(Player player, JFrame frame) {
		this.player = player;
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String itemName = JOptionPane.showInputDialog(frame,
                "Item", null);
		String[] itemString = itemName.split(",");
		int id = Integer.parseInt(itemString[0]);
		int amount = Integer.parseInt(itemString[1]);
		Item item = new Item(id, amount);
		if (player.getInventory().add(item)) {
			player.getActionSender().sendMessage("You have been given; " + item.getCount() + "x " + CacheItemDefinition.get(item.getId()).getName() + " from SERVER" );
		}
	}

}
