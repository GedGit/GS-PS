package org.rs2server.tools;

import org.rs2server.rs2.model.player.Player;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SkillActionListener implements ActionListener {

	private final Player player;
	private final JFrame frame;
	private final int id;
	
	public SkillActionListener(Player player, JFrame frame, int id) {// use jframe or w/e idk where ur code is rofl as in, get jframe and pass it as first arg of showinputdialog
		this.player = player;
		this.frame = frame;
		this.id = id;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		int level = Integer.parseInt(JOptionPane.showInputDialog(frame,
                "Skill:", null));
		if (level >= 0 && level <= 99) {
			player.getSkills().setLevel(id, level);
			player.getSkills().setExperience(id, player.getSkills().getExperienceForLevel(level));
		}
		
	}

}
