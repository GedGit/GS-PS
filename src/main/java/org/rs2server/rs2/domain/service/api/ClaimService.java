package org.rs2server.rs2.domain.service.api;

import org.rs2server.rs2.domain.model.claim.ClaimType;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;

/**
 * Claim service.
 *
 * @author tommo
 */
public interface ClaimService {

	void claim(@Nonnull final Player player, @Nonnull final ClaimType claimType);

	boolean hasClaimed(@Nonnull final Player player, @Nonnull final ClaimType claimType);

	boolean hasClaimedByIpAddress(@Nonnull final Player player, @Nonnull final ClaimType claimType);

}
