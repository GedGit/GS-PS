package org.rs2server.rs2.domain.service.api.content;

import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;

/**
 * Created by Tim on 12/1/2015.
 */
public interface RegionMusicService {

    public void updateRegionMusic(@Nonnull Player player, int regionId);

    public void updateTrackList(@Nonnull Player player);


}
