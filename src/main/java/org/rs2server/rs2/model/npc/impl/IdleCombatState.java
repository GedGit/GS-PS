package org.rs2server.rs2.model.npc.impl;

/**
 * A combat state that does nothing.
 * @author Clank1337
 */
public class IdleCombatState<T extends CombatNpc<?>> extends NpcCombatState<T> {

	public IdleCombatState(T npc) {
		super(npc);
	}

	@Override
	public void perform() {
	}

	@Override
	public int getId() {
		return npc.getId();
	}
}
