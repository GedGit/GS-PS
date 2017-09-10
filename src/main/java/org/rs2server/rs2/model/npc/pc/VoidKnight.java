package org.rs2server.rs2.model.npc.pc;

import org.rs2server.rs2.model.CombatNPCDefinition;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Mob;
import org.rs2server.rs2.model.player.pc.PestControlInstance;
import org.rs2server.rs2.util.Misc;

/**
 * A void knight npc which is used in a {@link PestControlInstance}
 * @author twelve
 */
public class VoidKnight extends PestControlNpc {

	private static final Location VOID_KNIGHT_LOCATION = Location.create(2656, 2592);
	private static final Location VOID_KNIGHT_LOCATION_SOUTH = Location.create(2656, 2591);
	public static final int CHILD = 3;

	VoidKnight(PestControlInstance instance) {
		super(2950 + Misc.random(0, 3), VOID_KNIGHT_LOCATION, instance);
		setCombatDefinition(CombatNPCDefinition.of(getId()));
		getSkills().setLevel(3, 200);
		getCombatState().setCanMove(false);
		super.face(VOID_KNIGHT_LOCATION_SOUTH);
	}

	public static VoidKnight in(PestControlInstance instance) {
		return new VoidKnight(instance);
	}

	public int getHealthPercentage() {
		return (int) ((double) (getSkills().getLevel(3) / getCombatDefinition().getSkills().get(CombatNPCDefinition.Skill.HITPOINTS)) * 100);
	}

	@Override public boolean canHit(Mob victim, boolean messages) {
		return true;
	}

	@Override public void face(Location location) {}

	@Override public void dropLoot(Mob mob) {}

	@Override
	public void tick() {}
}
