package org.rs2server.rs2.content;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.UpdateFlags.UpdateFlag;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.player.Player;

public class StarterMap {

	private static StarterMap INSTANCE = new StarterMap();

	public static StarterMap getSingleton() {
		return INSTANCE;
	}

	public List<String> starters = new ArrayList<String>();
	private final String path = "./data/starters.ini";
	private File map = new File(path);

	private Logger logger = Logger.getAnonymousLogger();

	public void init() {
		try {
			logger.info("Loading Starters");
			@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new FileReader(map));
			String s;
			while ((s = reader.readLine()) != null) {
				starters.add(s);
			}
			logger.info("Loaded Starter map, There are " + starters.size() + " IP's in Configuration");
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private void save() {
		BufferedWriter bf;
		try {
			clearMapFile();
			bf = new BufferedWriter(new FileWriter(path, true));
			for (String ip : starters) {
				bf.write(ip);
				bf.newLine();
			}
			bf.flush();
			bf.close();
		} catch (IOException e) {
			System.err.println("Error saving starter map!");
		}
	}

	private void clearMapFile() {
		PrintWriter writer;
		try {
			writer = new PrintWriter(map);
			writer.print("");
			writer.close();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void addIP(String ip) {
		starters.add(ip);
		save();
	}

	public int getCount(String ip) {
		int count = 0;
		for (String i : starters) {
			if (i.equals(ip))
				count++;
		}
		return count;
	}

	/**
	 * Handles the starter package.
	 * 
	 * @param player
	 *            the player
	 */
	public static void handleStarter(Player player, int gamemode) {
		boolean starter = player.getAttribute("starter");
		if (starter) {
			String IP = player.getSession().getRemoteAddress().toString().split(":")[0].replaceFirst("/", "");
			int count = getSingleton().getCount(IP);
			if (count > Constants.MAX_STARTER_COUNT && !Constants.DEBUG)
				player.sendMessage(
						"You have already received your " + Constants.MAX_STARTER_COUNT + " starter packages!");
			else {
				for (Item startItems : Constants.STARTER_ITEMS)
					player.getInventory().add(startItems);

				player.getEquipment().set(Equipment.SLOT_BOOTS, new Item(3105));
				player.getEquipment().set(Equipment.SLOT_GLOVES, new Item(7453));
				player.getEquipment().set(Equipment.SLOT_CAPE, new Item(1052));
				player.getEquipment().set(Equipment.SLOT_AMULET, new Item(1712));
				player.getEquipment().set(Equipment.SLOT_RING, new Item(2552));
				player.getEquipment().set(Equipment.SLOT_ARROWS, new Item(882, 100));

				player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);

				getSingleton().addIP(IP);
				World.getWorld().sendWorldMessage("<col=ff0000><img=50>Player: " + player.getName()
						+ " has just joined " + Constants.SERVER_NAME + player.gameModeName());
			}
			player.setQueuedSwitching(false);
			
			if (player.getAttribute("starter") != null)
				player.setAttribute("starter", false);
		}
	}
}
