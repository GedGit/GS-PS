package org.rs2server.rs2.model.npc.impl.zulrah;

import org.rs2server.rs2.model.npc.impl.NpcCombatState;



public class ZulrahMageState extends NpcCombatState<Zulrah> {
	
	public ZulrahMageState(Zulrah npc) {
		super(npc);
	}

	@Override
	public void perform() {
	}

	@Override
	public int getId() {
		return 2044;
	}

}
