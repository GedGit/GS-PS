package org.rs2server.rs2.tickable.impl;

import org.joda.time.Duration;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;

/**
 * @author Clank1337
 */
public class VenomDrainTick extends Tickable {

	private static final int CYCLE_TIME = 34;
	public Mob mob;

	public VenomDrainTick(Mob mob) {
		super(CYCLE_TIME);
		this.mob = mob;
	}

	@Override
	public void execute() {
		long lastVenomSip = mob.hasAttribute("antiVenom+") ? mob.getAttribute("antiVenom+") : 0;
		boolean cured = Duration.millis(System.currentTimeMillis()).minus(lastVenomSip).getMillis() < 300000;
		boolean attribute = mob.hasAttribute("venom");
		boolean dead = mob.getCombatState().isDead();
		if (mob.isPlayer()) {
			Player player = (Player) mob;
			if (dead || cured || !attribute || player.getDatabaseEntity().getCombatEntity().getVenomDamage() >= 22) {
				stop();
				player.getDatabaseEntity().getCombatEntity().setVenomDamage(0);
				return;
			}
			int venomDamage = player.getDatabaseEntity().getCombatEntity().getVenomDamage();
			player.inflictDamage(new Hit(venomDamage, Hit.HitType.VENOM_HIT), null);
			player.getDatabaseEntity().getCombatEntity().setVenomDamage(venomDamage + 2);
		} else { //NPC SHIT
			if (dead || cured || !attribute || mob.venomDamage >= 22) {
				stop();
				mob.venomDamage = 0;
				mob.removeAttribute("venom");
				return;
			}

			mob.inflictDamage(new Hit(mob.venomDamage, Hit.HitType.VENOM_HIT), null);
			mob.venomDamage += 2;
		}
	}
}
