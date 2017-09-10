package org.rs2server.rs2.model.npc.pc;

import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.player.pc.PestControlInstance;

import java.util.Optional;

/**
 * @author Clank1337
 */
public class Torcher extends PestControlNpc {

    public Torcher(int id, Location location, PestControlInstance instance, PestControlPortal portal) {
        super(id, location, instance, portal);
    }

    @Override
    public void tick() {
        if (getInteractingEntity() == null && getCombatState().getLastHitTimer() < (System.currentTimeMillis() + 4000)) { //Not in combat
			Optional<Player> player = instance.getPlayers().stream().filter(p -> p.getLocation().distance(this.getLocation()) <= 5).findAny();
			if (player.isPresent()) {
				getCombatState().startAttacking(player.get(), player.get().isAutoRetaliating());
			}
        }
    }
}
