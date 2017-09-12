package org.rs2server.rs2.model.event.impl.object;

import org.rs2server.rs2.model.DialogueManager;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.event.ClickEventManager;
import org.rs2server.rs2.model.event.EventListener;
import org.rs2server.rs2.model.minigame.impl.Barrows.BarrowsBrother;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;

/**
 *
 * @author 'Mystic Flow
 */
public class BarrowsCoffinObjectListener extends EventListener {

	@Override
	public void register(ClickEventManager manager) {
		for (BarrowsBrother brother : BarrowsBrother.values()) {
			manager.registerObjectListener(brother.getCoffinId(), this);
		}
	}

	@Override
	public boolean objectAction(final Player player, int objectId, GameObject gameObject, Location location,
			ClickOption option) {
		if (option != ClickOption.FIRST) {
			return false;
		}

		BarrowsBrother coffinBrother = null;
		for (BarrowsBrother brother : BarrowsBrother.values()) {
			if (brother.getCoffinId() == objectId) {
				coffinBrother = brother;
				break;
			}
		}
		if (player.getAttribute("currentlyFightingBrother") != null || coffinBrother == null)
			return true;
		if (player.getBarrowsKillCount() > 4) {
			DialogueManager.openDialogue(player, 48);
			return true;
		}
		if (player.getKilledBrothers().containsKey(coffinBrother.getNpcId())
				&& player.getKilledBrothers().get(coffinBrother.getNpcId())) {
			player.getActionSender().sendMessage("This brother has already been spawned.");
			return true;
		}
		NPC brother = new NPC(coffinBrother.getNpcId(), coffinBrother.getSpawn(), player.getLocation(),
				player.getLocation(), 6);
		brother.forceChat("You dare disturb my rest!");
		World.getWorld().register(brother);
		brother.setInstancedPlayer(player);
		player.setAttribute("currentlyFightingBrother", brother);
		return true;
	}

}
