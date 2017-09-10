package org.rs2server.rs2.content.api;

import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.concurrent.Immutable;

/**
 * An event which is fired when a player performs an emote.
 *
 * @author tommo
 */
@Immutable
public class GamePlayerEmoteEvent {

	private final Player player;

	private final Animation.Emote emote;

	public GamePlayerEmoteEvent(final Player player, final Animation.Emote emote) {
		this.player = player;
		this.emote = emote;
	}

	public Player getPlayer() {
		return player;
	}

	public Animation.Emote getEmote() {
		return emote;
	}
}
