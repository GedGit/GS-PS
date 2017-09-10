package org.rs2server.rs2.domain.service.api.content;

import org.rs2server.rs2.model.Song;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;

/**
 * A service used to play music.
 * @author twelve
 */
public interface MusicService {

	void play(@Nonnull Player player, @Nonnull Song song);
}
