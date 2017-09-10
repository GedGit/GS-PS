package org.rs2server.tools;

import org.rs2server.GUI;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InformationActionListener implements ActionListener{

	@SuppressWarnings("unused")
	private GUI gui;

	public InformationActionListener(GUI gui) {
		this.gui = gui;
	}//pretty cool thing to create stuff xd just back
	
	

	@Override
	public void actionPerformed(ActionEvent e) {
/*
		String selected = gui.getList().getSelectedValue();
		if (selected != null) {
			Optional<Player> playerOption = World.getWorld().getPlayers().stream().filter(p -> p.getName().equals(selected)).findAny();
			if (playerOption.isPresent()) {
				Player player = playerOption.get();
				JFrame frame = new JFrame("Player: "+selected);

				JPanel panel = new JPanel();
				panel.setLayout(null);

				JLabel lblPlayerInformation = new JLabel(TextUtils.optimizeText(selected) + "'s Information");
				lblPlayerInformation.setBounds(164, 11, 160, 14);
				panel.add(lblPlayerInformation);// open another client..

				JLabel lblStatistics = new JLabel("Statistics");
				lblStatistics.setBounds(29, 35, 65, 14);
				panel.add(lblStatistics);

				int y = 60;
				int x = 29;
				int i = 0;
				for (String skill : Skills.SKILL_NAME) {// why not just liek this? overlaps where
					
					JLabel label = new JLabel(skill + ": " + player.getSkills().getLevelForExperience(i));
					JButton btnNewButton = new JButton("Edit");
					
					if (y >= 255) {
						y = 60;
						x += 100;
					}
					
					label.setFont(new Font("Tahoma", Font.PLAIN, 10));
					label.setBounds(x, y, 85, 14);

					btnNewButton.setBounds(x - 19, y, 16, 9);
					
					btnNewButton.addActionListener(new SkillActionListener(player, frame, i++));
					
					panel.add(label);
					panel.add(btnNewButton);
					
					
					y += 15;
				}
				
				JLabel lblCoordinates = new JLabel("Misc");
				lblCoordinates.setBounds(334, 35, 71, 14);
				panel.add(lblCoordinates);
				
				JLabel lblLocation = new JLabel("Location");
				lblLocation.setBounds(300, 60, 71, 14);
				panel.add(lblLocation);
				
				JLabel lblNewLabel_1 = new JLabel("Rank");
				lblNewLabel_1.setBounds(300, 85, 71, 14);
				panel.add(lblNewLabel_1);
				
				JLabel lblLoc = new JLabel(player.getLocation().toString());
				lblLoc.setFont(new Font("Tahoma", Font.PLAIN, 9));
				lblLoc.setBounds(377, 60, 71, 14);
				panel.add(lblLoc);
				
				
				JLabel lblRank = new JLabel(player.getRights().toString());
				lblRank.setFont(new Font("Tahoma", Font.PLAIN, 9));
				lblRank.setBounds(377, 85, 91, 14);
				panel.add(lblRank);
				
				JButton button = new JButton("Edit");
				JButton button_1 = new JButton("Edit");

				

				button.setFont(new Font("Tahoma", Font.PLAIN, 6));
				button_1.setFont(new Font("Tahoma", Font.PLAIN, 6));
				
				button.setBounds(281, 64, 16, 9);
				button_1.setBounds(281, 90, 16, 9);
				
				button.addActionListener(new LocationActionListener(player, frame));
				
				
				JButton btnSendMessage = new JButton("Message");
				
				btnSendMessage.setFont(new Font("Tahoma", Font.PLAIN, 9));
				
				btnSendMessage.setBounds(150, 225, 85, 33);
				
				btnSendMessage.addActionListener(new SendMessageListener(player, frame));
				
				panel.add(btnSendMessage);
				
				JButton btnKill = new JButton("DESTROY");
				
				btnKill.setFont(new Font("Tahoma", Font.PLAIN, 9));
				
				btnKill.setBounds(225, 225, 85, 33);
				
				btnKill.addActionListener(new DestroyActionListener(player, frame));
				
				panel.add(btnKill);
				
				
				JButton btnGive = new JButton("Give Item");
				
				btnGive.setFont(new Font("Tahoma", Font.PLAIN, 9));
				
				btnGive.setBounds(300, 225, 85, 33);
				
				btnGive.addActionListener(new GiveItemActionListener(player, frame));
				
				panel.add(btnGive);
				
				panel.add(button);
				panel.add(button_1);


				frame.setSize(500, 300);
				frame.add(panel);

				frame.setVisible(true);
			}
		}*/
	}
}
