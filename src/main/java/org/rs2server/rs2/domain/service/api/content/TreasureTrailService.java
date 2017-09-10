package org.rs2server.rs2.domain.service.api.content;

import org.rs2server.rs2.domain.model.player.treasuretrail.PlayerTreasureTrail;
import org.rs2server.rs2.domain.model.player.treasuretrail.clue.ClueScrollType;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author tommo
 */
public interface TreasureTrailService {

	void showRewardsInterface(@Nonnull Player player, List<Item> rewards);

	PlayerTreasureTrail generateTreasureTrail(@Nonnull ClueScrollType clueScrollType);

	void finishTreasureTrail(@Nonnull Player player, @Nonnull PlayerTreasureTrail trail);

}
