package org.rs2server.rs2.model.cm.impl;

import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.cm.Content;
import org.rs2server.rs2.model.npc.impl.zulrah.Zulrah;
import org.rs2server.rs2.model.npc.impl.zulrah.ZulrahBehavior;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.Tickable;

/**
 * @author Nine
 * @author Twelve
 */
public class ZulrahContent extends Content {

    private static final Location SPAWN_LOCATION = Location.create(2269, 3069);
    private Zulrah zulrah;
    private int height;
    private boolean started;
    private int respawnTimer = -1;

    public ZulrahContent(Player player) {
        super(player);
    }

    @Override
    public void start() {
        height = 0;
		if (!player.isMultiplayerDisabled()) {
			player.setMultiplayerDisabled(true);
		}
        if (!player.getLocation().isWithinDistance(SPAWN_LOCATION)) {
            World.getWorld().submit(new Tickable(2) {
                @Override
                public void execute() {
                    if (getTickDelay() == 1) {
                        started = true;
                        stop();
                        return;
                    }
                    player.sendMessage("You enter Zulrah's Shrine...");
                    player.setTeleportTarget(SPAWN_LOCATION.transform(-1, 0, height));
                    setTickDelay(1);
                }
            });
        }
        respawnTimer = 8;
    }

    @Override
    public void process() {
        if (respawnTimer > -1)
            respawnTimer--;
        if (started) {
            Location loc = ZulrahBehavior.CENTER;
            if (player.getLocation().distanceToPoint(loc) >= 20) {
                player.getContentManager().stop(this);
                return;
            }
        }
        if (zulrah != null && World.getWorld().getNPCs().contains(zulrah) && !zulrah.getCombatState().isDead()) {
            zulrah.doCombat();
        } else {
            if (respawnTimer == 0)
                spawnZulrah();
            if (respawnTimer == -1)
                respawnTimer = 30;
        }
    }

    @Override
    public void stop() {
        if (zulrah != null) {
            zulrah.destroySelf();
            zulrah = null;
        }
		if (player.isMultiplayerDisabled()) {
			player.setMultiplayerDisabled(false);
		}
    }

    @Override
    public boolean canStart() {
        return !player.getInstancedNPCs().contains(zulrah);
    }

    @Override
    public void onCannotStart() {

    }

    private void spawnZulrah() {
        Location loc = ZulrahBehavior.CENTER.transform(0, 0, height);
        zulrah = new Zulrah(player, loc);
        zulrah.playAnimation(Zulrah.RISE_ANIMATION);
        zulrah.getCombatState().setAttackDelay(3);
        player.addInstancedNpc(zulrah);
        World.getWorld().getNPCs().add(zulrah);
        zulrah.setTeleporting(false);
        zulrah.setLocation(zulrah.getSpawnLocation());
        //World.getWorld().register(zulrah);
        startRecording();
    }

}
