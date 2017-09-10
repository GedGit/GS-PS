package org.rs2server.rs2.domain.service.impl.content;

import org.rs2server.rs2.domain.service.api.content.LoggingService;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Clank1337
 */
public class LoggingServiceImpl implements LoggingService {

	private static final String TRADE_DIRECTORY = "./data/logs/trades/";

	@Override
	public void logTrade(@Nonnull Player player, @Nonnull Player partner) {
		File file = new File(TRADE_DIRECTORY + player.getName() + ".logging");
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			PrintWriter writer = new PrintWriter(file);
			List<Item> list = new ArrayList<>();
			for (Item item : player.getTrade().toArray()) {
				if (item == null) {
					continue;
				}
				list.add(item);
			}
			writer.println("[" + partner.getName() + "] to [" + player.getName() + "] " + Arrays.toString(list.toArray()));
			writer.println();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		file = new File(TRADE_DIRECTORY + partner.getName() + ".logging");
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			PrintWriter writer = new PrintWriter(file);
			List<Item> list = new ArrayList<>();
			for (Item item : player.getTrade().toArray()) {
				if (item == null) {
					continue;
				}
				list.add(item);
			}
			writer.println("[" + player.getName() + "] to [" + partner.getName() + "] " + Arrays.toString(list.toArray()));
			writer.println();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
