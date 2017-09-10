package org.rs2server.rs2.tickable.impl;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.container.Container;
import org.rs2server.rs2.tickable.Tickable;

public class EnergyRestoreTick extends Tickable {

	/**
	 * The mob whos energy we are restoring.
	 */
	private Mob mob;

	public EnergyRestoreTick(Mob mob) {
		super(1);
		this.mob = mob;
	}

	@Override
	public void execute() {
		if (mob.getWalkingQueue().getEnergy() < 100) {
			boolean graceful = wearingFullGraceful(mob);
			boolean staminaPotion = mob.hasAttribute("staminaPotion");
			double runRestore = mob.getAgilityRunRestore();
			if (graceful)
				runRestore -= runRestore * 0.4;
			else if (staminaPotion)
				runRestore -= runRestore * 0.7;
			if (System.currentTimeMillis() > runRestore + mob.getLastRunRecovery()) {
				mob.setLastRunRecovery(System.currentTimeMillis());
				mob.getWalkingQueue().setEnergy(mob.getWalkingQueue().getEnergy() + 1);
				if (mob.getActionSender() != null)
					mob.getActionSender().sendRunEnergy();
			}
		} else {
			mob.getEnergyRestoreTick().stop();
			mob.setEnergyRestoreTick(null);
		}
	}

	public boolean wearingFullGraceful(Mob mob) {
		Container equip = mob.getEquipment();
		return equip != null && (equip.containsItems(Constants.GRACEFUL)
				|| equip.containsItems(Constants.PURPLE_GRACEFUL) || equip.containsItems(Constants.GREEN_GRACEFUL)
				|| equip.containsItems(Constants.YELLOW_GRACEFUL) || equip.containsItems(Constants.RED_GRACEFUL)
				|| equip.containsItems(Constants.BLUE_GRACEFUL));
	}
}