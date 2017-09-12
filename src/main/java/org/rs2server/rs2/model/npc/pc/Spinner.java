package org.rs2server.rs2.model.npc.pc;

import org.rs2server.Server;
import org.rs2server.rs2.domain.service.api.PathfindingService;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.player.pc.PestControlInstance;
import org.rs2server.rs2.tickable.impl.StoppingTick;

/**
 * A spinner is an npc that heals a {@link PestControlPortal} and poisons players.
 * @author twelve
 */
public final class Spinner extends PestControlNpc {

	private long lastHeal;
	private final PathfindingService pathfindingService;

	public Spinner(int id, Location location, PestControlInstance instance, PestControlPortal portal) {
		super(id, location, instance, portal);
		this.pathfindingService = Server.getInjector().getInstance(PathfindingService.class);
	}

	@Override
	public void tick() {
		if (portal.getCombatState().isDead()) {
			World.getWorld().submit(new StoppingTick(2) {
				@Override
				public void executeAndStop() {
					unregister();
				}
			});
			return;
		}
		if (!instance.isDestroyed()) { //Not in combat
			double distance = getLocation().distance(portal.getLocation());
			if (distance <= 5 && System.currentTimeMillis() - lastHeal > 5000) {
				heal();
				lastHeal = System.currentTimeMillis();
				return;
			}
			if (distance > 5 && getWalkingQueue().isEmpty()) {
				pathfindingService.travel(this, portal.getLocation());
			}
		}
	}

	public void heal() {
		if (portal.getCombatState().isDead()) {
			return;
		}
		face(portal.getLocation());
		playAnimation(Animation.create(3911));
		playGraphics(Graphic.create(658));
		portal.getSkills().increaseLevelToMaximum(Skills.HITPOINTS, portal.getSkills().getLevelForExperience(Skills.HITPOINTS) / 10);
	}

	@Override
	public void dropLoot(Mob killer) {
		// Override it to do nothing since pest control mobs don't drop items.
	}
}
