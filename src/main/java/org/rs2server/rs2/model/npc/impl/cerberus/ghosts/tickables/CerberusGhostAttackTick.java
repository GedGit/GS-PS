package org.rs2server.rs2.model.npc.impl.cerberus.ghosts.tickables;

import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.combat.CombatState;
import org.rs2server.rs2.model.npc.impl.cerberus.Cerberus;
import org.rs2server.rs2.model.npc.impl.cerberus.ghosts.CerberusGhost;
import org.rs2server.rs2.model.npc.impl.cerberus.ghosts.DelayedGhostAttack;
import org.rs2server.rs2.model.npc.impl.cerberus.ghosts.impl.MagicCerberusGhost;
import org.rs2server.rs2.model.npc.impl.cerberus.ghosts.impl.MeleeCerberusGhost;
import org.rs2server.rs2.model.npc.impl.cerberus.ghosts.impl.RangedCerberusGhost;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.impl.StoppingTick;

/**
 * @author Twelve
 */
public final class CerberusGhostAttackTick extends StoppingTick {

	private final Cerberus cerberus;
    private final CerberusGhost ghost;
    private final Player target;

    public CerberusGhostAttackTick(Cerberus cerberus, int ticks, CerberusGhost ghost, Player target) {
        super(ticks);
		this.cerberus = cerberus;
        this.ghost = ghost;
        this.target = target;
    }

    @Override
    public void executeAndStop() {
		if (cerberus.getGhosts() == null) {
			this.stop();
			return;
		}
        DelayedGhostAttack ghostAttack = ghost.createGhostAttack(target);
        ghost.face(target.getLocation());
		if (target.getActionSender() != null) {
			target.getActionSender().sendProjectile(ghostAttack.getProjectile());
		}
        ghost.playAnimation(ghost.getAnimation());
		boolean dealsDamage = true;
		CombatState combatState = target.getCombatState();
		if (ghost instanceof MeleeCerberusGhost) {
			dealsDamage = !combatState.getPrayer(Prayers.PROTECT_FROM_MELEE);
		} else if (ghost instanceof MagicCerberusGhost) {
			dealsDamage = !combatState.getPrayer(Prayers.PROTECT_FROM_MAGIC);
		} else if (ghost instanceof RangedCerberusGhost) {
			dealsDamage = !combatState.getPrayer(Prayers.PROTECT_FROM_MISSILES);
		}

		final boolean finalDealsDamage = dealsDamage;
		World.getWorld().submit(new StoppingTick(ghostAttack.getDelay()) {//i think this needs changed tbh.
            @Override
            public void executeAndStop() {
                if (finalDealsDamage) {
                    target.inflictDamage(new Hit(30), ghost);
                } else {
                    target.getSkills().decreasePrayerPoints(30);
                }
            }
        });
    }
}
