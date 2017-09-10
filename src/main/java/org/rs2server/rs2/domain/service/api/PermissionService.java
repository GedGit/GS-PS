package org.rs2server.rs2.domain.service.api;

import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;
import java.util.EnumSet;

/**
 * Represents a permission service.
 * 
 * @author Twelve
 */
public interface PermissionService {

	enum PlayerPermissions {

		// It's based on ordinal, sequence is MANDATORY for these to function correctly

		PLAYER, MODERATOR, ADMINISTRATOR, IRON_MAN, ULTIMATE_IRON_MAN, HARDCORE_IRON_MAN, SHIT_1, SHIT_2, SHIT_3, SHIT_4, PVP, HELPER, YOUTUBER, BRONZE_MEMBER, SILVER_MEMBER, GOLD_MEMBER, PLATINUM_MEMBER, DIAMOND_MEMBER, COM, DEV;

	}

	PlayerPermissions getHighestPermission(@Nonnull Player player);

	void give(@Nonnull Player player, @Nonnull PlayerPermissions permission);

	void remove(@Nonnull Player player, @Nonnull PlayerPermissions permission);

	boolean is(@Nonnull Player player, @Nonnull PlayerPermissions permission);

	boolean isAny(@Nonnull Player player, @Nonnull PlayerPermissions... permissions);

	boolean isAny(@Nonnull Player player, @Nonnull EnumSet<PlayerPermissions> permissions);

	boolean isNot(@Nonnull Player player, @Nonnull PlayerPermissions permissions);

	boolean isNotAny(@Nonnull Player player, @Nonnull PlayerPermissions... permissions);

	boolean isNotAny(@Nonnull Player player, @Nonnull EnumSet<PlayerPermissions> permissions);
}
