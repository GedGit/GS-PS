package org.rs2server.rs2.domain.service.impl.content.duel;

import org.rs2server.rs2.model.container.Container;
import org.rs2server.rs2.model.player.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Clank1337
 */
public class Duel {

	public enum IngameDuelRule {
		NO_RANGED,
		NO_MELEE
	}

	private static final int DUEL_SIZE = 28;
	private final Player player;
	private final Player opponent;
	private boolean[] equipmentRules;
	private Map<IngameDuelRule, Boolean> rules;
	private final Container playerStakes;
	private final Container partnerStakes;

	public Duel(Player player, Player opponent) {
		this.player = player;
		this.opponent = opponent;
		this.equipmentRules = new boolean[11];
		this.rules = new HashMap<>();
		this.playerStakes = new Container(Container.Type.STANDARD, DUEL_SIZE);
		this.partnerStakes = new Container(Container.Type.STANDARD, DUEL_SIZE);
	}

	public boolean setIngameRule(IngameDuelRule rule, boolean value) {
		return rules.put(rule, value);
	}

	public boolean getIngameRule(IngameDuelRule rule) {
		return rules.getOrDefault(rule, false);
	}

	public Player getPlayer() {
		return player;
	}

	public Player getOpponent() {
		return opponent;
	}

	public boolean[] getEquipmentRules() {
		return equipmentRules;
	}

	public boolean getEquipmentRule(int index) {
		return equipmentRules[index];
	}

	public void setEquipmentRule(int index, boolean bool) {
		equipmentRules[index] = bool;
	}

	public Container getPlayerStakes() {
		return playerStakes;
	}

	public Container getPartnerStakes() {
		return partnerStakes;
	}

}
