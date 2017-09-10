package org.rs2server.rs2.model.npc;

import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.combat.CombatAction;
import org.rs2server.rs2.model.combat.impl.*;
import org.rs2server.rs2.model.player.Player;

public class MetalArmour extends NPC {
	
	/**
	 * The minimum location this NPC can walk into.
	 */
	private static final Location minLocation = Location.create(2849,3534,0);
	/**
	 * The maximum location this NPC can walk into.
	 */
	private static final Location maxLocation = Location.create(2861,3545,0);
	
	private static final Animation RISE = Animation.create(4166);
	
	private Player owner;

	public MetalArmour(NPCDefinition def, Location location, Player owner) {
		super(def.getId(), location, minLocation, maxLocation, 1);
		this.playAnimation(RISE);
		this.forceChat("I'm ALIVE!");
		this.setAggressiveDistance(15);
		this.owner = owner;
	}
	
	@Override
	public boolean canHit(Mob victim, boolean messages) {
		final CombatAction action = victim.getActiveCombatAction();
		if (action == MagicCombatAction.getAction()) {
			victim.getActionSender().sendMessage("You can not use Magic in the guild.");
			return false;
		}
		if (action == RangeCombatAction.getAction()) {
			victim.getActionSender().sendMessage("You can not use Ranged in the guild.");
			return false;
		}
		return super.canHit(victim, messages) && victim == this.owner;
	}
}