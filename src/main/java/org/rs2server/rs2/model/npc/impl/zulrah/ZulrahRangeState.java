package org.rs2server.rs2.model.npc.impl.zulrah;

import org.rs2server.rs2.model.npc.impl.NpcCombatState;



/**
 * 
 * @author Nine
 * @author Twelve
 *
 */
public class ZulrahRangeState extends NpcCombatState<Zulrah> {

	public ZulrahRangeState(Zulrah npc) {
		super(npc);
	}

	@Override
	public void perform() {

	}

	@Override
	public int getId() {
		return 2042;
	}

}