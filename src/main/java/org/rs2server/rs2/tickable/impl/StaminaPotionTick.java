package org.rs2server.rs2.tickable.impl;

import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.tickable.Tickable;

/**
 * @author Clank1337
 */
public class StaminaPotionTick extends Tickable {

	private final Mob mob;
	private static final int TICKS = 200;

	public StaminaPotionTick(Mob mob) {
		super(TICKS);
		this.mob = mob;
	}

	@Override
	public void execute() {
		boolean attribute = mob.hasAttribute("staminaPotion");
		if (mob != null && attribute) {
			mob.getActionSender().sendMessage("Your stamina potion has expired.");
			mob.removeAttribute("staminaPotion");
		}
	}
}
