package org.rs2server.rs2.action;

import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.World;

/**
 * @author 'Mystic Flow <Steven@rune-server.org>
 */
public class ActionManager {

    private Action currentAction;
    private Mob mob;

    public ActionManager(Mob mob) {
        this.mob = mob;
    }

    public void appendAction(Action action) {
        if (currentAction != null)
            stopAction();
        currentAction = action;
        World.getWorld().submit(action);
    }

    public void stopAction() { 
        if (currentAction != null) {
            currentAction.stop();
            currentAction = null;
        }
        mob.removeTick("skill_action_tick");
        mob.removeTick("area_event");
    }

    public void stopNonWalkableActions() {
        if (currentAction != null) {
            currentAction.stop();
            currentAction = null;
        }
        mob.removeTick("skill_action_tick");
        mob.removeTick("area_event");
    }
}

