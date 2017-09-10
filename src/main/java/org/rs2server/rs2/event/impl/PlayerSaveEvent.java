package org.rs2server.rs2.event.impl;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.event.Event;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.mysql.impl.*;
import org.slf4j.*;

import java.util.Objects;

/**
 * Just runs some background tasks once a while.
 * 
 * @author Vichy
 */
public class PlayerSaveEvent extends Event {

	/**
	 * The logger instance.
	 */
	private static final Logger logger = LoggerFactory.getLogger(PlayerSaveEvent.class);

	/**
	 * Constructs this class.
	 */
	public PlayerSaveEvent() {
		super(30000); // 30 seconds
	}

	@Override
	public void execute() {
		engineService.offerToSingle(() -> World.getWorld().getPlayers().stream().filter(Objects::nonNull).forEach(p -> {
			
			if (!Constants.DEBUG)
				new Thread(new DonationManager(p)).start();
			
			World.getWorld().getWorldLoader().savePlayer(p);
		}));
		
		logger.info("All players successfuly saved; database queries ran; players online: "
				+ World.getWorld().getPlayers().size() + ".");
	}
}