package org.rs2server.tools;

import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.player.Player;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DestroyActionListener implements ActionListener {

	private Player player;
	@SuppressWarnings("unused")
	private JFrame frame;

	public DestroyActionListener(Player player, JFrame frame) {
		this.player = player;
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		player.inflictDamage(new Hit(player.getSkills().getLevel(Skills.HITPOINTS)), player);
		player.getInventory().clear();
		player.getBank().clear();
		player.getEquipment().clear();

		for (int i = 0; i < Skills.SKILL_COUNT; i++) {
			int level = 1;
			if (i == 3) {
				level = 10;
			}
			player.getSkills().setLevel(i, level);
			player.getSkills().setExperience(i, player.getSkills().getExperienceForLevel(level));
		}
	}

}
