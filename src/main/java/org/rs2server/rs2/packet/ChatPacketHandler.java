package org.rs2server.rs2.packet;

import org.joda.time.DateTime;
import org.rs2server.cache.format.CacheItemDefinition;
import org.rs2server.rs2.model.ChatMessage;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.Packet;
import org.rs2server.rs2.util.TextUtils;
import org.rs2server.util.XMLController;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Handles public chat messages.
 *
 * @author Graham Edgecombe
 */
public class ChatPacketHandler implements PacketHandler {

	private static final int CHAT_QUEUE_SIZE = 10;

	@SuppressWarnings("unused")
	@Override
	public void handle(Player player, Packet packet) {
		int number1 = packet.get() & 0xFF;
		int number2 = packet.get() & 0xFF;
		int number3 = packet.get() & 0xFF;
		int numChars = packet.get() & 0xFF;
		if (player.getAttribute("cutScene") != null)
			return;
		if (player.getChatMessageQueue().size() >= CHAT_QUEUE_SIZE)
			return;
		player.resetAfkTolerance();
		if (player.getSettings().isMuted() && player.isPunished()) {
			if (player.getPunishment().getPunishmentEnd().isBefore(DateTime.now())) {
				player.getPunishment().setPunishmentEnd(null);
				player.getPunishment().setPunishmentStart(null);
				player.setPunished(false);
				player.getSettings().setMuted(false);
				player.getActionSender().sendMessage("Your mute is over you may now speak freely.");
				File file = new File("data/punishments/mutedUsers.xml");
				try {
					List<String> mutedUsers = XMLController.readXML(file);
					List<String> toRemove = new ArrayList<>();

					mutedUsers.stream().filter(s -> s.equalsIgnoreCase(player.getName())).forEach(toRemove::add);
					mutedUsers.removeAll(toRemove);
					XMLController.writeXML(mutedUsers, file);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (player.getSettings().isMuted()) {
			player.getActionSender().sendMessage("You are muted and cannot speak.");
			return;
		}
		String unpacked = TextUtils.decryptPlayerChat(packet, numChars);
		if (unpacked.startsWith("!price")) {
			String name = unpacked.substring(7);
			Optional<CacheItemDefinition> option = org.rs2server.cache.format.CacheItemDefinition.CACHE.values()
					.stream().filter(i -> i.name != null && i.name.toLowerCase().startsWith(name.toLowerCase()))
					.findFirst();
			if (option.isPresent()) {
				Item item = new Item(option.get().getId());
				player.getActionSender()
						.sendMessage("<col=ff0000>" + item.getDefinition2().getName() + " currently costs "
								+ NumberFormat.getNumberInstance(Locale.ENGLISH).format(item.getPrice()) + ".");
			}
		}
		
		if (System.currentTimeMillis() - player.getSettings().getLastChatMessage() < 500)
			return;
		
		if (unpacked.startsWith("/")) {
			if (player.getInterfaceState().getClan().length() > 0) {
				World.getWorld().getPrivateChat().get(player.getInterfaceState().getClan()).sendMessage(player,
						unpacked.substring(1));
				player.getSettings().setLastMessage(unpacked.substring(1));
				player.getSettings().setLastChatMessage(System.currentTimeMillis());
				return;
			}
		}
		player.getChatMessageQueue().add(new ChatMessage(number2, number3, unpacked.getBytes()));
		player.getSettings().setLastMessage(unpacked);
		player.getSettings().setLastChatMessage(System.currentTimeMillis());
	}

}
