package org.rs2server.rs2.domain.service.api.content;

import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;

/**
 * @author Clank1337
 */
public interface PrivateChatService {

	enum PrivateChatStatus {
		ON, FRIENDS_ONLY, OFF
	}

	void addFriend(@Nonnull Player player, String name);

	void removeFriend(@Nonnull Player player, String name);

	void sendMessage(@Nonnull Player player, String name, String message);

	void updateFriendsList(@Nonnull Player player);

	void sendFriends(@Nonnull Player player);

}
