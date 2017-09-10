package org.rs2server.rs2.model.skills.hunter;

import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.util.Misc;

public class TrapExecution {

	/**
	 * Handles Trap's with a state of 'set'
	 *
	 * @param trap
	 */
	public static void setTrapProcess(Trap trap) {
		for (final NPC npc : Hunter.HUNTER_NPC_LIST) {
			if (npc == null) {
				continue;
			}
			if (trap instanceof BoxTrap && npc.getId() != 5079 && npc.getId() != 5080)
				continue;
			if (trap instanceof SnareTrap && (npc.getId() == 5079 || npc.getId() == 5080))
				continue;
			if (npc.getLocation().isWithinDistance(trap.getGameObject().getLocation(), 1)) {
				if (Misc.random(100) < successFormula(trap, npc)) {
					Hunter.catchNPC(trap, npc);
					return;
				}
			}
		}
	}

	public static int successFormula(Trap trap, NPC npc) {
		if (trap.getOwner() == null)
			return 0;
		int chance = 70;
		if (Hunter.hasLarupia(trap.getOwner()))
			chance = chance + 10;
		chance = chance + (int) (trap.getOwner().getSkills().getLevel(22) / 1.5) + 10;

		if (trap.getOwner().getSkills().getLevel(22) < 25)
			chance = (int) (chance * 1.5) + 8;
		if (trap.getOwner().getSkills().getLevel(22) < 40)
			chance = (int) (chance * 1.4) + 3;
		if (trap.getOwner().getSkills().getLevel(22) < 50)
			chance = (int) (chance * 1.3) + 1;
		if (trap.getOwner().getSkills().getLevel(22) < 55)
			chance = (int) (chance * 1.2);
		if (trap.getOwner().getSkills().getLevel(22) < 60)
			chance = (int) (chance * 1.1);
		if (trap.getOwner().getSkills().getLevel(22) < 65)
			chance = (int) (chance * 1.05) + 3;

		return chance;
	}

	/**
	 * Handles the cycle management of each traps timer
	 *
	 * @param trap
	 *            is the given trap we are managing
	 * @return false if the trap is too new to have caught
	 */
	public static boolean trapTimerManagement(Trap trap) {
		if (trap.getTicks() > 0)
			trap.setTicks(trap.getTicks() - 1);
		if (trap.getTicks() <= 0) {
			if (trap.getOwner() != null)
				trap.getOwner().getActionSender().sendMessage("You left your trap for too long, and it collapsed.");
		}
		return true;
	}
}
