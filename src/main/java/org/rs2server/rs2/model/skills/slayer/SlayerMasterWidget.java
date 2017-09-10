package org.rs2server.rs2.model.skills.slayer;

import org.rs2server.rs2.domain.model.player.PlayerStatisticsEntity;
import org.rs2server.rs2.model.bit.BitConfigBuilder;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;

/**
 * @author Twelve
 */
public class SlayerMasterWidget {

    private static final int TASK_WIDGET = 426;

    @SuppressWarnings("unused")
	private static final int TASK_CONFIG = 1096;

    private static final int REWARD_POINT_CONFIG = 661;
    private static final int REWARD_BIT = 6;

    private static final int FIFTH_SLOT_CONFIG = 1191;
    private static final int FIFTH_SLOT = 7;


    private final Player player;

    public SlayerMasterWidget(@Nonnull Player player) {
        this.player = player;
    }

    public final BitConfigBuilder rewardPointBuilder() {
        PlayerStatisticsEntity statistics = player.getDatabaseEntity().getStatistics();
        return BitConfigBuilder.of(REWARD_POINT_CONFIG).set(statistics.getSlayerRewardPoints(), REWARD_BIT);
    }

    public final BitConfigBuilder fifthSlotBuilder() {
        return BitConfigBuilder.of(FIFTH_SLOT_CONFIG).set(FIFTH_SLOT, FIFTH_SLOT);
    }

    public final void open() {
        player.sendBitConfig(rewardPointBuilder().build());
        player.sendBitConfig(fifthSlotBuilder().build());
        player.getActionSender().sendInterface(TASK_WIDGET, false);
    }

}
