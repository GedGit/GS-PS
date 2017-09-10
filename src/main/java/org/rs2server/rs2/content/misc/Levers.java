package org.rs2server.rs2.content.misc;

import org.rs2server.rs2.event.Event;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Graphic;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.minigame.magearena.MageArena;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;

import java.util.HashMap;
import java.util.Map;

public class Levers {

    private static Map<Location, Lever> LEVERS = new HashMap<Location, Lever>();
    private static final Animation LEVER_ANIMATION = Animation.create(2140);


    public static boolean handle(final Player player, GameObject obj) {
        final Lever lever = LEVERS.get(obj.getLocation());
        if (lever == null)
            return false;
		if (player.hasAttribute("teleporting") || player.hasAttribute("busy"))
			return false;
        if (obj.getId() == 9707) {
            if (MageArena.IN_ARENA.contains(player)) {
                player.getMageArena().stop();
            }
        }
		if (!player.getLocation().isNextTo(obj.getLocation())) {
			return false;
		}
        /*
		 * Prevents mass clicking them.
		 */
        if (player.getSettings().getLastTeleport() < 3000 || player.getDatabaseEntity().getPlayerSettings().isTeleBlocked()) {
            return true;
        }
        player.faceObject(obj);
        player.playAnimation(LEVER_ANIMATION);
        player.setAttribute("busy", true);
        player.getSettings().setLastTeleport(System.currentTimeMillis());
        if (obj.getId() == 1817 || obj.getId() == 5959 || obj.getId() == 5960 || obj.getId() == 26761 || obj.getId() == 1815 || obj.getId() == 1814 || obj.getId() == 1816) {
            World.getWorld().submit(new Tickable(1) {

                @Override
                public void execute() {
                    this.stop();
                    World.getWorld().replaceObject(obj, new GameObject(obj.getLocation(), 5961, obj.getType(), obj.getDirection(), false), 7);
                }

            });
        }
        World.getWorld().submit(new Event(1500) {

            @Override
            public void execute() {
                player.playAnimation(Animation.create(714));
                player.playGraphics(Graphic.create(308, 48, 100));
                World.getWorld().submit(new Event(1800) {

                    @Override
                    public void execute() {
                        player.setTeleportTarget(lever.getTargetLocation());
                        player.playAnimation(Animation.create(-1));
                        player.removeAttribute("busy");
                        this.stop();
                    }

                });
                this.stop();
            }

        });
        return true;
    }

    private static class Lever {

        private final Location targetLocation;
        public Location getTargetLocation() {
            return targetLocation;
        }

        public Lever(Location target, int direction1, int direction2) {
            this.targetLocation = target;
        }
    }

    /**
     * This populates the map.
     */
    static {
		/*
		 * King Black Dragon levers.
		 */
        LEVERS.put(Location.create(3067, 10253, 0), new Lever(Location.create(2271, 4680, 0), 3, 3));
        LEVERS.put(Location.create(2271, 4680, 0), new Lever(Location.create(3067, 10253, 0), 3, 3));
		
		/*
		 * Edgeville -> Wild
		 */
        LEVERS.put(Location.create(3090, 3475, 0), new Lever(Location.create(3153, 3923, 0), 0, 0));
        /*
         * Ardougne -> Wild
         */
        LEVERS.put(Location.create(2561, 3311, 0), new Lever(Location.create(3153, 3923, 0), 0, 0));
		/*
		 * Wilderness -> Ardougne
		 */
        LEVERS.put(Location.create(3153, 3923, 0), new Lever(Location.create(2561, 3311, 0), 0, 0));
		
		/*
		 * Wild -> Magebank
		 */
        LEVERS.put(Location.create(3090, 3956, 0), new Lever(Location.create(2539, 4712, 0), 0, 0));
		
		/*
		 * Magebank -> Wild
		 */
        LEVERS.put(Location.create(2539, 4712, 0), new Lever(Location.create(3090, 3956, 0), 0, 0));
		
		/*
		 * Wilderness -> Mage Arena
		 */
        LEVERS.put(Location.create(3104, 3956, 0), new Lever(Location.create(3105, 3951, 0), 0, 0));
		
		/*
		 * Mage Arena -> Wilderness
		 */
        LEVERS.put(Location.create(3105, 3952, 0), new Lever(Location.create(3105, 3956, 0), 0, 0));
    }

}
