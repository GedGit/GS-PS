package org.rs2server.rs2.model.npc.impl.cerberus;

import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.map.RegionClipping;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.tickable.impl.StoppingTick;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by shawn on 6/9/2016.
 */
public final class LavaPoolTickable extends StoppingTick {

    private static final Graphic START_TILE_GRAPHIC = Graphic.create(1246);
    private static final Graphic END_TILE_GRAPHIC = Graphic.create(1247);
    private final Cerberus cerberus;
	private final Location initialPlayerLocation;

    public LavaPoolTickable(Cerberus cerberus, Location initialPlayerLocation) {
        super(4);
		this.initialPlayerLocation = initialPlayerLocation;
        this.cerberus = cerberus;
    }

    @Override
    public void executeAndStop() {
		if (!cerberus.canAttackPlayer()) {
			return;
		}
        Player challenger = cerberus.getChallenger();
		if (cerberus.isDestroyed() || cerberus.getCombatState().isDead() || !challenger.getInstancedNPCs().contains(cerberus)) {
			return;
		}
        challenger.getActionSender().sendStillGFX(START_TILE_GRAPHIC.getId(), 0, initialPlayerLocation);

        List<Location> clippedLocations = new ArrayList<>();
        for (int x = -5; x <= 5; x++) {
            for (int y = -5; y <= 5; y++) {
                if (x == 0 && y == 0) {
                    continue;
                }
                Location transformed = initialPlayerLocation.transform(x, y, 0);
                if (RegionClipping.isPassable(transformed)) {
                    clippedLocations.add(transformed);
                }
            }
        }

        Collections.shuffle(clippedLocations);
        Location one = clippedLocations.get(0);
        Location two = clippedLocations.get(1);

        challenger.getActionSender().sendStillGFX(START_TILE_GRAPHIC.getId(), 0, one);
        challenger.getActionSender().sendStillGFX(START_TILE_GRAPHIC.getId(), 0, two);

        World.getWorld().submit(new StoppingTick(6) {
            @Override
            public void executeAndStop() {
				if (cerberus.isDestroyed() || cerberus.getCombatState().isDead() || !challenger.getInstancedNPCs().contains(cerberus)) {
					return;
				}
                challenger.getActionSender().sendStillGFX(END_TILE_GRAPHIC.getId(), 0, one);
                challenger.getActionSender().sendStillGFX(END_TILE_GRAPHIC.getId(), 0, two);
                challenger.getActionSender().sendStillGFX(END_TILE_GRAPHIC.getId(), 0, initialPlayerLocation);
            }
        });

        World.getWorld().submit(new Tickable(2) {
            int ticks = 0;
            @Override
            public void execute() {
				if (cerberus.isDestroyed() || cerberus.getCombatState().isDead() || !challenger.getInstancedNPCs().contains(cerberus)) {
					this.stop();
					return;
				}
                if (ticks == 5 || cerberus.getSkills().getLevel(Skills.HITPOINTS) <= 0) {
                    stop();
                    return;
                }

                Location currentPlayerLocation = challenger.getLocation();

                if (currentPlayerLocation.distance(initialPlayerLocation) <= 1) {
                    challenger.inflictDamage(new Hit(10), cerberus);
                }

                if (currentPlayerLocation.distance(one) <= 1) {
                    challenger.inflictDamage(new Hit(7), cerberus);
                }

                if (currentPlayerLocation.distance(two) <= 1) {
                    challenger.inflictDamage(new Hit(7), cerberus);
                }
                ticks++;
            }
        });

        //todo delayed tick + check for damage on initialPlayerLocation, one, two

        World.getWorld().submit(new StoppingTick(12) {
            @Override
            public void executeAndStop() {
				if (cerberus.isDestroyed() || cerberus.getCombatState().isDead() || !challenger.getInstancedNPCs().contains(cerberus)) {
					this.stop();
					return;
				}
                cerberus.setCanSpawnLavaPools(true);
            }
        });
    }
}
