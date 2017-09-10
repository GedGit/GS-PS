package org.rs2server.rs2.domain.dao.api;

import org.rs2server.rs2.domain.dao.MongoDao;
import org.rs2server.rs2.domain.model.claim.ClaimType;
import org.rs2server.rs2.domain.model.claim.ClaimEntity;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author tommo
 */
public interface ClaimEntityDao extends MongoDao<ClaimEntity> {

	@Nonnull
	List<ClaimEntity> findByTypeAndClaimedBy(final ClaimType claimType, final String claimedBy);

	@Nonnull
	List<ClaimEntity> findByTypeAndIpAddress(final ClaimType claimType, final String ipAddress);

	@Nonnull
	List<ClaimEntity> findByTypeAndIpAddressAndClaimedBy(final ClaimType claimType, final String ipAddress, final String claimedBy);

}
