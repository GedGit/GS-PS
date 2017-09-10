package org.rs2server.rs2.model.npc.impl.zulrah;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.Hit;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.npc.impl.NpcCombatState;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.util.Misc;


public class ZulrahMeleeState extends NpcCombatState<Zulrah> {

    private static final Animation MELEE_ANIMATION = Animation.create(5806);
    private Location target;

    public ZulrahMeleeState(Zulrah npc) {
        super(npc);
    }

    @Override
    public void perform() {
        if (target != null) {
            if (npc.getCombatState().getAttackDelay() == 7) {
                npc.playAnimation(MELEE_ANIMATION);
            } else if (npc.getCombatState().getAttackDelay() == 1) {
                if (npc.getChallenger().getLocation().getDistance(target) < 2) {
                    Player challenger = npc.getChallenger();
                    if (challenger.getCombatState().getPrayer(18)) {
                        challenger.inflictDamage(new Hit(0), npc);
                    } else {
                        challenger.stun(6, "You have been stunned.", true);
                        challenger.inflictDamage(new Hit(Misc.random(41)), npc);
                    }
                }
                target = null;
            }
        }
    }

    public void setTarget(Location target) {
        this.target = target;
    }

    @Override
    public int getId() {
        return 2043;
    }

}
