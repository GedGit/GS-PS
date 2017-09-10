package org.rs2server.rs2.model.event.impl.object;

import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.event.ClickEventManager;
import org.rs2server.rs2.model.event.EventListener;
import org.rs2server.rs2.model.player.Player;

public class SlayerTowerAgilityChain extends EventListener{

	@Override
	public void register(ClickEventManager manager) {
		manager.registerObjectListener(16537, this);
		manager.registerObjectListener(16538, this);
	}
	
	@Override
	public boolean objectAction(final Player player, int objectId, GameObject gameObject, Location location, ClickOption option) {
    	if (option != ClickOption.FIRST)
            return false;
		if (player.getSkills().getLevelForExperience(Skills.AGILITY) < 61) {
			player.getActionSender().sendMessage("You need a Agility level of 61 to use this shortcut.");
			return false;
		}
		switch (objectId) {
		case 16537:
			player.setTeleportTarget(Location.create(player.getLocation().getX(), player.getLocation().getY(), 1));
			break;
		case 16538:
			player.setTeleportTarget(Location.create(player.getLocation().getX(), player.getLocation().getY(), 0));
			break;
		}
		return true;
	}

}
