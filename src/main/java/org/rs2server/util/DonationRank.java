package org.rs2server.util;

import java.io.*;
import java.text.DateFormat;
import java.util.Date;

import org.rs2server.cache.format.CacheItemDefinition;
import org.rs2server.rs2.domain.service.api.PermissionService.PlayerPermissions;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.player.Player;

public final class DonationRank {

	/**
	 * Handles a donation made.
	 * 
	 * @param price
	 *            The donation price.
	 */
	public static void handleDonation(Player player, Item item, int price) {

		try {
			BufferedWriter bf = new BufferedWriter(new FileWriter("data/logs/donationLogs.txt", true));
			bf.newLine();
			bf.write("[Player: " + player.getName() + ", on " + DateFormat.getDateTimeInstance().format(new Date())
					+ "]: has donated: " + price + "$ for " + item.getCount() + " x "
					+ CacheItemDefinition.get(item.getId()).getName() + ".");
			bf.newLine();
			bf.flush();
			bf.close();
		} catch (IOException ignored) {
			ignored.printStackTrace();
		}
		
		player.getDatabaseEntity().incrementAmountDonated(price);
		
		int totalDonated = player.getDatabaseEntity().getAmountDonated();
		
		if (!player.getPermissionService().is(player, PlayerPermissions.BRONZE_MEMBER) && totalDonated > 0) {
			player.getPermissionService().give(player, PlayerPermissions.BRONZE_MEMBER);
			player.sendMessage("<col=ff0000>Congratulations; You've been promoted to <img=40> Bronze Member!");
		}
		
		if (!player.getPermissionService().is(player, PlayerPermissions.SILVER_MEMBER) && totalDonated >= 25) {
			player.getPermissionService().give(player, PlayerPermissions.SILVER_MEMBER);
			player.sendMessage("<col=ff0000>Congratulations; You've been promoted to <img=39> Silver Member!");
		}
		
		if (!player.getPermissionService().is(player, PlayerPermissions.GOLD_MEMBER) && totalDonated >= 100) {
			player.getPermissionService().give(player, PlayerPermissions.GOLD_MEMBER);
			player.sendMessage("<col=ff0000>Congratulations; You've been promoted to <img=38> Gold Member!");
		}
		
		if (!player.getPermissionService().is(player, PlayerPermissions.PLATINUM_MEMBER) && totalDonated >= 250) {
			player.getPermissionService().give(player, PlayerPermissions.PLATINUM_MEMBER);
			player.sendMessage("<col=ff0000>Congratulations; You've been promoted to <img=37> Platinum Member!");
		}
		
		if (!player.getPermissionService().is(player, PlayerPermissions.DIAMOND_MEMBER) && totalDonated >= 500) {
			player.getPermissionService().give(player, PlayerPermissions.DIAMOND_MEMBER);
			player.sendMessage("<col=ff0000>Congratulations; You've been promoted to <img=36> Diamond Member!");
		}
	}
}
