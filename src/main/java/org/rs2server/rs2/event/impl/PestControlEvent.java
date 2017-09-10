package org.rs2server.rs2.event.impl;

import org.rs2server.Server;
import org.rs2server.rs2.domain.service.api.content.PestControlService;
import org.rs2server.rs2.event.Event;
import org.rs2server.rs2.model.player.pc.PestControlBoat;
import org.rs2server.rs2.model.player.pc.PestControlInstance;
import org.rs2server.rs2.model.player.Player;

/**
 * An event that processes all pest control boats and instances.
 * @author Tim
 * @author Twelve
 */
public class PestControlEvent extends Event {

	private final PestControlService service;

	public PestControlEvent() {
		super(600);
		this.service = Server.getInjector().getInstance(PestControlService.class);
	}

	@Override
	public void execute() {
		for (PestControlBoat boat : service.getBoats()) {
			int boatRemaining = boat.decrementBoatTime();
			int gameRemaining = boat.decrementGameTime();


			boat.stream()
					.map(Player::getActionSender)
					.forEach(a -> a.sendString(407, 12, "Next Departure: " + Integer.toString((boatRemaining + 100) / 100) + " min" + (boatRemaining > 100 ? "s" : ""))
							.sendString(407, 13, "Players Ready: " + boat.getPlayers().size()).sendString(407, 14, "(Need 3 to 25 players)"));
			if (gameRemaining <= 0) {
				boat.endGame();
			}

			if (boatRemaining <= 0 && boat.getInstance() == null) {
				boat.startGame();
			} else if (boat.getInstance() != null && boatRemaining <= 0) {
				boat.setBoatTimeRemaining(100);
			}

			PestControlInstance instance = boat.getInstance();
			if (instance != null) {
				instance.tick();
			}
		}
	}
}
