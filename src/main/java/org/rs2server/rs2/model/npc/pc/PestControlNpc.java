package org.rs2server.rs2.model.npc.pc;

import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.pc.PestControlInstance;

/**
 * @author twelve
 */
public class PestControlNpc extends NPC {

	protected final PestControlInstance instance;
	protected final PestControlPortal portal;

	public PestControlNpc(int id, Location location, PestControlInstance instance, PestControlPortal portal) {
		/*
		minLoc = Location.create(x - 3, y - 3, z);
					maxLoc = Location.create(x + 3, y + 3, z);
		 */
		super(id, location, Location.create(location.getX() - 3, location.getY() - 3), Location.create(location.getX() + 3, location.getY() + 3), 0);
		this.instance = instance;
		this.portal = portal;
	}

	public PestControlNpc(int id, Location location, PestControlInstance instance) {
		super(id, location, null);
		this.instance = instance;
		this.portal = null;
	}

	public final PestControlInstance getInstance() {
		return instance;
	}

	public final PestControlPortal getPortal() { return portal;} 
}
