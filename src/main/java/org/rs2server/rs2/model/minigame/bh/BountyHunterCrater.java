package org.rs2server.rs2.model.minigame.bh;

import org.rs2server.rs2.model.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author 'Mystic Flow
 */
public enum BountyHunterCrater {
	
	HIGH(95, 28121);
	
	private int requiredLevel;
	private int objectId;
	
	private List<Player> players = new ArrayList<Player>();
	
	BountyHunterCrater(int requiredLevel, int objectId) {
		this.requiredLevel = requiredLevel;
		this.objectId = objectId;
	}
	
	public int getRequiredLevel() {
		return requiredLevel;
	}
	
	public int getObjectId() {
		return objectId;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void remove(Player player) {
		players.remove(player);
	}

}
