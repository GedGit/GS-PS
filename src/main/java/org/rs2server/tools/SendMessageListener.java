package org.rs2server.tools;

import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.ActionSender.DialogueType;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SendMessageListener implements ActionListener {
	
	private Player player;
	private JFrame frame;
	
	public SendMessageListener(Player player, JFrame frame) {
		this.player = player;
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String msg = JOptionPane.showInputDialog(frame,
                "Message:", null);
		int idfk = "1234567890 1234567890 1234567890 1234567890 1234567890".length();
		System.out.println(idfk);
		if (msg.length() >= idfk) {
			String add = msg.substring(idfk, msg.length());
			msg = msg.substring(0, idfk) + "<br>" + add;
		}
		
		player.getActionSender().sendDialogue("Server Message", DialogueType.MESSAGE, -1, null, msg);
	}

}
