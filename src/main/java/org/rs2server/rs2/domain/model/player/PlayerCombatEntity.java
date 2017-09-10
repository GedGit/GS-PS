package org.rs2server.rs2.domain.model.player;

/**
 * @author Clank1337
 */

import lombok.Getter;
import lombok.Setter;

public  final
@Setter @Getter
class PlayerCombatEntity {

	private int venomDamage;

	public int getVenomDamage() {
		return venomDamage;
	}

	public void setVenomDamage(int venomDamage) {
		this.venomDamage = venomDamage;
	}

}
