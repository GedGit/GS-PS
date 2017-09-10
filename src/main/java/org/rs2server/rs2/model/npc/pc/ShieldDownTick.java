package org.rs2server.rs2.model.npc.pc;

import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.player.pc.PestControlInstance;
import org.rs2server.rs2.tickable.impl.StoppingTick;

/**
 * A tick that enables a {@link PestControlPortal} to be attacked.
 * Each player in the {@link PestControlInstance} also gets notified.
 * @author twelve
 */
public class ShieldDownTick extends StoppingTick {

	private final PestControlPortal portal;
	private final PestControlInstance instance;

	public ShieldDownTick(int ticks, PestControlInstance instance, PestControlPortal portal) {
		super(ticks);
		this.instance = instance;
		this.portal = portal;
	}

	@Override
	public void executeAndStop() {

		if (instance.isDestroyed()) {
			stop();
			return;
		}

		PortalCardinality cardinality = portal.getCardinality();
		portal.setAttackable(true);

		instance.stream().map(Player::getActionSender).forEach(a -> {
			a.sendMessage("The " + portal.getCardinality().getName() + ", " + cardinality.getDirection() + " portal shield has dropped!");
			a.sendInterfaceConfig(408, cardinality.getShieldChild(), true);
		});
	}
}
