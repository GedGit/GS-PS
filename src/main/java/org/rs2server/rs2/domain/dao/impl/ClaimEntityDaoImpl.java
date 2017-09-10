package org.rs2server.rs2.domain.dao.impl;

import com.google.inject.Inject;
import org.mongojack.DBQuery;
import org.rs2server.rs2.domain.dao.AbstractMongoDao;
import org.rs2server.rs2.domain.dao.MongoService;
import org.rs2server.rs2.domain.dao.api.ClaimEntityDao;
import org.rs2server.rs2.domain.model.claim.ClaimType;
import org.rs2server.rs2.domain.model.claim.ClaimEntity;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author tommo
 */
public class ClaimEntityDaoImpl extends AbstractMongoDao<ClaimEntity> implements ClaimEntityDao {

	@Inject
	ClaimEntityDaoImpl(final MongoService mongoService) {
		super(mongoService, "Claim", ClaimEntity.class);
	}

	@Override
	protected void ensureIndices() {

	}

	@Nonnull
	@Override
	public List<ClaimEntity> findByTypeAndClaimedBy(ClaimType claimType, String claimedBy) {
		return findListByQuery(DBQuery.is("claimType", claimType).is("claimedBy", claimedBy));
	}

	@Nonnull
	@Override
	public List<ClaimEntity> findByTypeAndIpAddress(ClaimType claimType, String ipAddress) {
		return findListByQuery(DBQuery.is("claimType", claimType).is("ipAddress", ipAddress));
	}

	@Nonnull
	@Override
	public List<ClaimEntity> findByTypeAndIpAddressAndClaimedBy(ClaimType claimType, String ipAddress, String claimedBy) {
		return findListByQuery(DBQuery.is("claimType", claimType).is("ipAddress", ipAddress).is("claimedBy", claimedBy));
	}
}
