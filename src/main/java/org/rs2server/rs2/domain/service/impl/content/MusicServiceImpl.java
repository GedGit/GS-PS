package org.rs2server.rs2.domain.service.impl.content;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import org.rs2server.rs2.content.api.GameInterfaceButtonEvent;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.domain.service.api.content.MusicService;
import org.rs2server.rs2.model.Song;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;

/**
 * @author twelve
 */
public final class MusicServiceImpl implements MusicService {

	@Inject
	public MusicServiceImpl(HookService service) {
		service.register(this);
	}

	@Override
	public void play(@Nonnull Player player, @Nonnull Song song) {
		player.getActionSender().sendSong(song.getId());
	}

	@Subscribe
	public void onMusicPlayRequest(GameInterfaceButtonEvent event) {
		if (event.getInterfaceId() == 239) {
			Song song = Song.of(event);

			if (song != null) {
				play(event.getPlayer(), song);
			}
		}
	}
}
