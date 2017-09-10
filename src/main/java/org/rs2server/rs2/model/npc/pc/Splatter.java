package org.rs2server.rs2.model.npc.pc;

import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.model.player.pc.PestControlInstance;
import org.rs2server.rs2.tickable.impl.StoppingTick;
import org.rs2server.rs2.util.Misc;

import java.util.Optional;

/**
 * @author Clank1337
 */
public class Splatter extends PestControlNpc {


	public Splatter(int id, Location location, PestControlInstance instance, PestControlPortal portal) {
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

	public void handleDeath() {
		playAnimation(Animation.create(3888));
		playGraphics(Graphic.create(649));
		World.getWorld().submit(new StoppingTick(1) {
			@Override
			public void executeAndStop() {
				explode();
			}

		});
	}

	public void explode() {
		World.getWorld().getPlayers().stream().filter(p -> p.getLocation().distance(getLocation()) <= 1).forEach(i -> {
			int maximum = i.getSkills().getCombatLevel() / 3;
			int minimum = maximum / 2;
			i.inflictDamage(new Hit(Misc.random(minimum, maximum), Hit.HitType.NORMAL_HIT), null);
		});
		World.getWorld().getNPCs().stream().filter(n -> n.getLocation().distance(getLocation()) <= 1).forEach(i -> {
			int maximum = i.getSkills().getCombatLevel() / 3;
			int minimum = maximum / 2;
			i.inflictDamage(new Hit(Misc.random(minimum, maximum), Hit.HitType.NORMAL_HIT), null);
		});
		World.getWorld().unregister(this);
	}

}
