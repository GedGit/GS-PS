package org.rs2server.rs2.domain.service.api.content;

import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;

/**
 * @author Clank1337
 */
public interface MonsterExamineService {

	void openMonsterExamine(@Nonnull Player player, int npcId);
}
