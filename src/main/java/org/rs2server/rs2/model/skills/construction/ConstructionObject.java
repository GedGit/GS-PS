package org.rs2server.rs2.model.skills.construction;

import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.GameObjectDefinition;
import org.rs2server.rs2.model.Location;


public class ConstructionObject extends GameObject {

	public ConstructionObject(GameObjectDefinition definition, Location location, int type, int rotation, boolean buildingModeObject) {
		super(location, definition.getId(), type, rotation, false);
		this.buildingModeObject = buildingModeObject;
	}
	
	public boolean isBuildingModeObject() {
		return buildingModeObject;
	}

	private final boolean buildingModeObject;
	
}
