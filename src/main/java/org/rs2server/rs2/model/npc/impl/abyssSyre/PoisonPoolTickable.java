package org.rs2server.rs2.model.npc.impl.abyssSyre;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.map.RegionClipping;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;
import org.rs2server.rs2.tickable.impl.StoppingTick;

public class PoisonPoolTickable {

    private static final Graphic START_TILE_GRAPHIC = Graphic.create(1246);
    private static final Graphic END_TILE_GRAPHIC = Graphic.create(1247);
    private final AbyssalSire abyssalsire;
	private final Location initialPlayerLocation;

    public PoisonPoolTickable(AbyssalSire abyssalsire, Location initialPlayerLocation) {
        super();
		this.initialPlayerLocation = initialPlayerLocation;
        this.abyssalsire = abyssalsire;
    }

    public void executeAndStop() {
		if (!abyssalsire.canAttackPlayer()) {
			return;
		}
        Player challenger = abyssalsire.getChallenger();
		if (abyssalsire.isDestroyed() || abyssalsire.getCombatState().isDead() || !challenger.getInstancedNPCs().contains(abyssalsire)) {
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
				if (abyssalsire.isDestroyed() || abyssalsire.getCombatState().isDead() || !challenger.getInstancedNPCs().contains(abyssalsire)) {
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
				if (abyssalsire.isDestroyed() || abyssalsire.getCombatState().isDead() || !challenger.getInstancedNPCs().contains(abyssalsire)) {
					this.stop();
					return;
				}
                if (ticks == 5 || abyssalsire.getSkills().getLevel(Skills.HITPOINTS) <= 0) {
                    stop();
                    return;
                }

                Location currentPlayerLocation = challenger.getLocation();

                if (currentPlayerLocation.distance(initialPlayerLocation) <= 1) {
                    challenger.inflictDamage(new Hit(10), abyssalsire);
                }

                if (currentPlayerLocation.distance(one) <= 1) {
                    challenger.inflictDamage(new Hit(7), abyssalsire);
                }

                if (currentPlayerLocation.distance(two) <= 1) {
                    challenger.inflictDamage(new Hit(7), abyssalsire);
                }
                ticks++;
            }
        });

        //todo delayed tick + check for damage on initialPlayerLocation, one, two

        World.getWorld().submit(new StoppingTick(12) {
            @Override
            public void executeAndStop() {
				if (abyssalsire.isDestroyed() || abyssalsire.getCombatState().isDead() || !challenger.getInstancedNPCs().contains(abyssalsire)) {
					this.stop();
					return;
				}
                abyssalsire.CanSpawnPoisonPool(true);
            }
        });
    }
}