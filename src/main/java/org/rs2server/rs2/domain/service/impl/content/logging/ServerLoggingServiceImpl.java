package org.rs2server.rs2.domain.service.impl.content.logging;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import org.rs2server.rs2.content.api.*;
import org.rs2server.rs2.domain.service.api.GroundItemService;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.domain.service.api.content.logging.ServerLoggingService;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.container.Container;
import org.rs2server.rs2.model.player.Player;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Vichy
 */
public class ServerLoggingServiceImpl implements ServerLoggingService {

	private static final ExecutorService loggingExecutor = Executors.newSingleThreadExecutor();

	@Inject
	ServerLoggingServiceImpl(final HookService hookService) {
		hookService.register(this);
	}

	@Subscribe
	public void onTradeFinish(GamePlayerTradeEvent event) {
		Player player = event.getPlayer();
		Player partner = event.getPartner();
		if (player != null && partner != null) {
			Container giving = event.getPlayerContainer();
			Container receieving = event.getPartnerContainer();
			Optional<Integer> playerPriceOption = giving.stream().filter(i -> i != null && i.getDefinition() != null)
					.map(i -> i.getPrice() * i.getCount()).reduce((a, b) -> a + b);
			Optional<Integer> partnerPriceOption = receieving.stream()
					.filter(i -> i != null && i.getDefinition() != null)
					.map(i -> i.getPrice() * i.getCount()).reduce((a, b) -> a + b);

			int price = playerPriceOption.isPresent() ? playerPriceOption.get() : 0;
			int partnerPrice = partnerPriceOption.isPresent() ? partnerPriceOption.get() : 0;

			loggingExecutor.submit(() -> write(player,
					getDate(player.getDetails().getUUID()) + "[" + partner.getName() + " -> " + player.getName() + "] "
							+ (price > 25000000 || partnerPrice > 25000000 ? "EXTREME: Player->"
									+ NumberFormat.getNumberInstance(Locale.ENGLISH).format(price) + ", Partner->"
									+ NumberFormat.getNumberInstance(Locale.ENGLISH).format(partnerPrice) : "")
							+ partner.getName() + "'s Container: [" + getContainer(giving) + "], " + player.getName()
							+ "'s Container: [" + getContainer(receieving) + "]",
					LogType.TRADE));
		}
	}

	@Subscribe
	public void onPlayerMessage(GamePlayerMessageEvent event) {
		Player player = event.getPlayer();
		Player receiver = event.getReceiver();
		if (player != null && receiver != null) {
			loggingExecutor.submit(() -> write(player, getDate(player.getDetails().getUUID()) + "[" + player.getName()
					+ " -> " + receiver.getName() + "] " + event.getMessage(), LogType.PRIVATE_MESSAGE));
		}
	}

	@Subscribe
	public void onPlayerKill(GamePlayerKillEvent event) {
		Player player = event.getPlayer();
		Player killer = event.getKiller();
		if (player != null && killer != null) {
			Container items = event.getItems();
			Optional<Integer> playerPriceOption = items.stream().filter(i -> i != null && i.getDefinition() != null)
					.map(i -> i.getPrice() * i.getCount()).reduce((a, b) -> a + b);
			int price = playerPriceOption.isPresent() ? playerPriceOption.get() : 0;
			loggingExecutor.submit(() -> write(killer,
					getDate(player.getDetails().getUUID()) + "[" + killer.getName() + " killed " + player.getName()
							+ "] Items = " + (price > 25000000 ? "EXTREME" : "" + getContainer(items)),
					LogType.PVP));
		}
	}

	@Subscribe
	public void onNpcKill(GamePlayerNPCKillEvent event) {
		Player player = event.getPlayer();
		if (player != null) {
			String name = event.getNpcName();
			Item item = event.getItem();
			if (item.getDefinition2() == null) {
				return;
			}
			loggingExecutor.submit(() -> write(player,
					getDate(player.getDetails().getUUID()) + "[" + player.getName() + " killed " + name + "] "
							+ item.getCount() + "x " + (item.getDefinition2().isNoted()
									? item.getDefinition2().getNotedName() : item.getDefinition2().getName()),
					LogType.PVN));
		}
	}

	@Subscribe
	public void onItemDrop(GamePlayerItemDropEvent event) {
		Player player = event.getPlayer();
		GroundItemService.GroundItem groundItem = event.getGroundItem();
		if (player != null && groundItem != null) {
			Item item = groundItem.getItem();
			loggingExecutor.submit(() -> write(player,
					getDate(player.getDetails().getUUID()) + "[" + player.getName() + "] dropped " + item.getCount()
							+ "x "
							+ (item.getDefinition2().isNoted() ? item.getDefinition2().getNotedName()
									: item.getDefinition2().getName())
							+ " at " + groundItem.getLocation().toString(),
					LogType.DROP_ITEM));
		}
	}

	@Subscribe
	public void onItemPickup(GamePlayerItemPickupEvent event) {
		Player player = event.getPlayer();
		GroundItemService.GroundItem groundItem = event.getGroundItem();
		if (player != null && groundItem != null) {
			Item item = groundItem.getItem();
			loggingExecutor.submit(() -> write(player,
					getDate(player.getDetails().getUUID()) + "[" + player.getName() + "] picked up " + item.getCount()
							+ "x "
							+ (item.getDefinition2().isNoted() ? item.getDefinition2().getNotedName()
									: item.getDefinition2().getName())
							+ " from " + groundItem.getOwner() + " at " + groundItem.getLocation().toString(),
					LogType.PICKUP_ITEM));
		}
	}

	private static String getContainer(Container container) {
		StringBuilder sb = new StringBuilder();
		container.stream().filter(Objects::nonNull).filter(i -> i.getDefinition2() != null)
				.forEach(i -> sb.append(i.getCount()).append("x ").append(
						i.getDefinition2().isNoted() ? i.getDefinition2().getNotedName() : i.getDefinition2().getName())
						.append(", "));
		return sb.toString();
	}

	private static String getDate(String MAC) {
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date date = new Date();
		return "[" + dateFormat.format(date) + "]" + "[" + MAC + "]";
	}

	@Override
	public void write(Player player, String info, LogType type) {
		File file = new File(type.getPath() + player.getName() + ".log");
		try {
			if (!file.exists() && !file.isDirectory()) {
				file.createNewFile();
			}
			FileWriter writer = new FileWriter(file, true);
			BufferedWriter out = new BufferedWriter(writer);
			out.write(info);
			out.newLine();
			out.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
