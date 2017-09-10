package org.rs2server.rs2.model.npc.pc;

import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.player.pc.PestControlInstance;
import java.util.Optional;

/**
 * A Shifter teleports it's way towards the {@link VoidKnight}, unless it gets
 * attacked in which case it teleports around it's attacker. It's main goal is
 * to attack the {@link VoidKnight}.
 * 
 * @author Twelve
 */
public class Shifter extends PestControlNpc {

	public Shifter(int id, Location location, PestControlInstance instance, PestControlPortal portal) {
		super(id, location, instance, portal);
	}

	@Override
	public void tick() {
		if (getInteractingEntity() == null
				&& getCombatState().getLastHitTimer() < (System.currentTimeMillis() + 4000)) { // Not
																								// in
																								// combat
			Optional<Player> player = instance.getPlayers().stream()
					.filter(p -> p.getLocation().distance(this.getLocation()) <= 5).findAny();
			if (player.isPresent()) {
				getCombatState().startAttacking(player.get(), player.get().isAutoRetaliating());
			}
		}
	}
}
