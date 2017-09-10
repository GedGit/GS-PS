package org.rs2server.rs2.event.impl;

import org.rs2server.rs2.event.Event;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.minigame.warriorsguild.WarriorsGuild;
import org.rs2server.rs2.model.player.Player;

public class WarriorsGuildEvent extends Event{

	public WarriorsGuildEvent() {
		super(60000);
	}

	/**
	 * Representing the warrior guild token item
	 */
	private static final Item tokens = new Item(WarriorsGuild.TOKENS);
	
	@Override
	public void execute() {
		for (Player p : WarriorsGuild.IN_GAME) {
			if (p.getInventory().getCount(tokens.getId()) < 10) {
				p.getWarriorsGuild().outOfTokens();
				continue;
			}
			p.getInventory().remove(new Item(tokens.getId(), 10));
			p.getActionSender().sendMessage("Ten of your warrior guild tokens crumble away.");
		}
	}
}