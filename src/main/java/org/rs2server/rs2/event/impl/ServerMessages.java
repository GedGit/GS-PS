package org.rs2server.rs2.event.impl;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.event.Event;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.util.Misc;

public class ServerMessages extends Event {

	public ServerMessages() {
		super(300000);
	}

	@Override
	public void execute() {
		World.getWorld().sendWorldMessage("<img=51><shad=000000><col=" + colors[Misc.random(colors.length - 1)] + ">"
				+ "Server: " + messageString[Misc.random(messageString.length - 1)]);
	}

	/**
	 * A String[] containing all World random messages.
	 */
	private String[] messageString = { "Please report all bugs; glitches & missing content on our ::forums.",
			"Join our ::discord channel to quickly chat with the staff members.",
			"Don't forget to ::vote for us daily and receive awesome rewards.",
			"Did you know you can imbue your Slayer helmets at vannaka?",
			"Enjoying the server? Please consider to ::donate to us.",
			"To teleport around the server talk to the grey magician at home.",
			"If you'd like to suggest something - please visit our ::forums.",
			"Training in the Donator zone provides +10% extra experience.",
			"Training in the Wilderness provides +10% extra experience.",
			"Did you know claiming vote rewards also give you double experience?",
			"Did you know you can imbue rings at the Pest Control rewards trader?",
			"You can spend your Loyalty points at Salve-PS Advisor NPC (at home).",
			"Did you know you can upgrade void at the Pest Control rewards trader?",
			"You can see a list of all available commands by typing ::commands.",
			"Did you know " + Constants.SERVER_NAME + " also has Runescape shops scattered around the world?" };

	/**
	 * A String[] containing all Colors used for Server messages.
	 */
	private String[] colors = { "3399FF", "00CC99", "003399", "0099CC", "993300", "993333", "9900FF", "7D1616",
			"CC3300", "00FF00", "3399FF" };
}
