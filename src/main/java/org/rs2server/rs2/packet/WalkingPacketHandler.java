package org.rs2server.rs2.packet;

import org.rs2server.Server;
import org.rs2server.rs2.domain.service.api.PathfindingService;
import org.rs2server.rs2.model.DialogueManager;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.container.PriceChecker;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.Packet;

/**
 * A packet which handles walking requests.
 * 
 * @author Graham Edgecombe
 *
 */
public class WalkingPacketHandler implements PacketHandler {

	private final PathfindingService pathfindingService = Server.getInjector().getInstance(PathfindingService.class);

	@Override
	public void handle(Player player, Packet packet) {

		boolean starter = player.getAttribute("starter");
		if (starter) {
			DialogueManager.openDialogue(player, 19000);
			return;
		}
		
		player.getActionSender().closeAll();
		
		if (player.getAttribute("cutScene") != null)
			return;
		
		if (player.getInterfaceAttribute("fightPitOrbs") != null)
			return;

		if (player.getAttribute("stunned") != null) {
			player.getActionSender().sendMessage("You're stunned!");
			return;
		}
		
		if (player.getAttribute("busy") != null)
			return;
		
		if (player.hasAttribute("questnpc"))
			player.removeAttribute("questnpc");
		
		player.removeAttribute("isStealing");
		int toY = packet.getShort();
		int toX = packet.getLEShortA();
		final Location destination = Location.create(toX, toY);
		@SuppressWarnings("unused")
		boolean running = packet.get() == 1;

		player.resetInteractingEntity();
		player.getActionQueue().clearAllActions();
		player.getActionManager().stopAction();
		player.resetAfkTolerance();

		if (player.getInterfaceState().isInterfaceOpen(125))
			player.getActionSender().removeInventoryInterface();

		if (player.getInterfaceState().isInterfaceOpen(PriceChecker.PRICE_INVENTORY_INTERFACE))
			PriceChecker.returnItems(player);

		if (player.getInterfaceState().isEnterAmountInterfaceOpen())
			player.getActionSender().removeEnterAmountInterface();

		if (player.getAttribute("bank_searching") != null)
			player.removeAttribute("bank_searching");

		player.getActionSender().removeAllInterfaces().removeInterface();

		if (player.isLighting())
			return;

		if (player.getAttribute("isStealing") != null && (boolean) player.getAttribute("isStealing"))
			player.setAttribute("isStealing", false);

		player.getWalkingQueue().reset();
		if (!player.getCombatState().canMove()) {
			if (player.getFrozenBy() == null
					|| (player.getFrozenBy().getLocation().distance(player.getLocation()) >= 12)) {
				player.getCombatState().setCanMove(true);
				player.setFrozenBy(null);
			} else {
				if (player.getAttribute("moveMessage") != null) {
					long l = player.getAttribute("moveMessage");
					if (System.currentTimeMillis() - l <= 3000)
						return;
				}
				player.setAttribute("moveMessage", System.currentTimeMillis());
				player.getActionSender().sendMessage("A magical force stops you from moving.");
				return;
			}
		}
		if (!player.canEmote())
			return; // stops walking during skillcape animations.

		pathfindingService.travel(player, destination);
	}
}