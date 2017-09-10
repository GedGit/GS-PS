package org.rs2server.rs2.domain.service.impl;

import org.rs2server.rs2.domain.dao.api.ClaimEntityDao;
import org.rs2server.rs2.domain.model.claim.ClaimEntity;
import org.rs2server.rs2.domain.model.claim.ClaimType;
import org.rs2server.rs2.domain.service.api.ClaimService;
import org.rs2server.rs2.domain.service.api.PlayerService;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Objects;

/**
 * @author tommo
 */
public class ClaimServiceImpl implements ClaimService {

	private final PlayerService playerService;
	private final ClaimEntityDao claimDao;

	@Inject
	ClaimServiceImpl(final ClaimEntityDao claimDao, final PlayerService playerService) {
		this.claimDao = claimDao;
		this.playerService = playerService;
	}

	@Override
	public void claim(@Nonnull final Player player, @Nonnull final ClaimType claimType) {
		final ClaimEntity claim = new ClaimEntity();
		claim.setClaimedBy(player.getName());
		claim.setIpAddress(playerService.getIpAddress(player));
		claim.setClaimType(claimType);

		claimDao.save(claim);
	}

	@Override
	public boolean hasClaimed(@Nonnull Player player, @Nonnull ClaimType claimType) {
		Objects.requireNonNull(player, "player");
		Objects.requireNonNull(claimType, "claimType");

		return claimDao.findByTypeAndClaimedBy(claimType, player.getName()).size() > 0;
	}

	@Override
	public boolean hasClaimedByIpAddress(@Nonnull final Player player, @Nonnull final ClaimType claimType) {
		Objects.requireNonNull(player, "player");
		Objects.requireNonNull(claimType, "claimType");

		return claimDao.findByTypeAndIpAddressAndClaimedBy(claimType, playerService.getIpAddress(player), player.getName()).size() > 0;
	}

}
