package org.rs2server.rs2.domain.model.player;

import lombok.Getter;
import lombok.Setter;

/**
 * An entity containing information about a player's time in bounty hunter.
 * @author twelve.
 */
public final
@Setter @Getter
class PlayerBountyHunterEntity {
	private int kills;
	private int deaths;
	private int bountyShopPoints;
	private String lastKilled;
	private String lastKilledUUID;

	public void incrementBountyPoints(int i) {
		setBountyShopPoints(getBountyShopPoints() + i);
	}

	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	public int getDeaths() {
		return deaths;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}

	public int getBountyShopPoints() {
		return bountyShopPoints;
	}

	public void setBountyShopPoints(int bountyShopPoints) {
		this.bountyShopPoints = bountyShopPoints;
	}

	public String getLastKilled() {
		return lastKilled;
	}

	public void setLastKilled(String lastKilled) {
		this.lastKilled = lastKilled;
	}

	public String getLastKilledUUID() {
		return lastKilledUUID;
	}

	public void setLastKilledUUID(String lastKilledUUID) {
		this.lastKilledUUID = lastKilledUUID;
	}
}
