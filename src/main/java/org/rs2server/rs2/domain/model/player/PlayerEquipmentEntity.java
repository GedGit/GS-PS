package org.rs2server.rs2.domain.model.player;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Player equipment state.
 *
 * Currently, this only exists to persist item states (degrading items, charges, etc) and whether they're
 * equipped or banked does not matter.
 *
 * In the future, the player's actual equipment setup will be persisted here too.
 *
 * @author tommo
 */
public final
@Setter @Getter
class PlayerEquipmentEntity {

	private Map<Integer, Integer> itemCharges;
	private Map<Integer, Integer> itemChargedWith;
	public Map<Integer, Integer> getItemCharges() {
		return itemCharges;
	}
	public void setItemCharges(Map<Integer, Integer> itemCharges) {
		this.itemCharges = itemCharges;
	}
	public Map<Integer, Integer> getItemChargedWith() {
		return itemChargedWith;
	}
	public void setItemChargedWith(Map<Integer, Integer> itemChargedWith) {
		this.itemChargedWith = itemChargedWith;
	}
}
