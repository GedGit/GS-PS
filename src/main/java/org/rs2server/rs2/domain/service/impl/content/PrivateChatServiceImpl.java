package org.rs2server.rs2.domain.service.impl.content;

import com.google.inject.Inject;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.domain.service.api.PlayerService;
import org.rs2server.rs2.domain.service.api.content.PrivateChatService;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.util.NameUtils;
import javax.annotation.Nonnull;
import java.util.Set;



/**
 * @author Clank1337
 */
public class PrivateChatServiceImpl implements PrivateChatService {

	private final PlayerService playerService;

	@Inject
	public PrivateChatServiceImpl(HookService service, PlayerService playerService) {
		service.register(this);
		this.playerService = playerService;
	}

	@Override
	public void addFriend(@Nonnull Player player, String name) {
		Set<String> friendsList = player.getDatabaseEntity().getPrivateChat().getFriendsList();
		if (friendsList.contains(name)) {
			player.getActionSender().sendMessage("This player is already on your friends list.");
			return;
		}
		friendsList.add(name);
		updateFriendsList(player);
	}

	@Override
	public void removeFriend(@Nonnull Player player, String name) {
		Set<String> friendsList = player.getDatabaseEntity().getPrivateChat().getFriendsList();
		if (friendsList.contains(name)) {
			friendsList.remove(name);
			updateFriendsList(player);
		}
	}

	@Override
	public void sendMessage(@Nonnull Player player, String name, String message) {
	}

	@Override
	public void updateFriendsList(@Nonnull Player player) {
		Set<String> friendsList = player.getDatabaseEntity().getPrivateChat().getFriendsList();
		friendsList.stream().forEach(i -> {
			Player friend = playerService.getPlayer(i);
			if (friend == null) {
				player.getActionSender().sendFriend(NameUtils.nameToLong(i), 0, 0);
			} else {
				PrivateChatStatus status = friend.getDatabaseEntity().getPrivateChat().getStatus();
				Set<String> otherFriendsList = friend.getDatabaseEntity().getPrivateChat().getFriendsList();
				if ((otherFriendsList.contains(player.getName()) && status == PrivateChatStatus.FRIENDS_ONLY) || status == PrivateChatStatus.ON) {
					player.getActionSender().sendFriend(NameUtils.nameToLong(i), 1, 0);
				} else {
					player.getActionSender().sendFriend(NameUtils.nameToLong(i), 0, 0);
				}
				otherFriendsList.stream().forEach(f -> {
					Player otherFriend = playerService.getPlayer(f);
					if (otherFriend == null) {
						friend.getActionSender().sendFriend(NameUtils.nameToLong(f), 0, 0);
					} else {
						Set<String> list = otherFriend.getDatabaseEntity().getPrivateChat().getFriendsList();
						PrivateChatStatus otherStatus = otherFriend.getDatabaseEntity().getPrivateChat().getStatus();
						if ((list.contains(player.getName()) && otherStatus == PrivateChatStatus.FRIENDS_ONLY) || otherStatus == PrivateChatStatus.ON) {
							friend.getActionSender().sendFriend(NameUtils.nameToLong(f), 1, 0);
						} else {
							friend.getActionSender().sendFriend(NameUtils.nameToLong(f), 0, 0);
						}
					}
				});
			}
		});
	}


	/*@Subscribe
	public void onLogin(GamePlayerLoginEvent event) {
		Player player = event.getPlayer();
		if (player != null) {
			updateFriendsList(player);
		}
	}*/

	@Override
	public void sendFriends(@Nonnull Player player) {

	}

}
