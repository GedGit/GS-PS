package org.rs2server.rs2.domain.dao.impl;

import com.google.inject.Inject;
import org.mongojack.DBQuery;
import org.rs2server.rs2.domain.dao.AbstractMongoDao;
import org.rs2server.rs2.domain.dao.MongoService;
import org.rs2server.rs2.domain.dao.api.PlayerEntityDao;
import org.rs2server.rs2.domain.model.player.PlayerEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author tommo
 */
public class PlayerEntityDaoImpl extends AbstractMongoDao<PlayerEntity> implements PlayerEntityDao {

	@Inject
	PlayerEntityDaoImpl(final MongoService mongoService) {
		super(mongoService, "Player", PlayerEntity.class);
	}

	@Override
	protected void ensureIndices() {
		ensureUniqueIndex("accountName");
	}

	@Nullable
	@Override
	public PlayerEntity findByAccountName(@Nonnull String accountName) {
		return findSingleByQuery(DBQuery.is("accountName", accountName));
	}

	@Nullable
	@Override
	public PlayerEntity findByDisplayName(@Nonnull String displayName) {
		return findSingleByQuery(DBQuery.is("displayName", displayName));
	}
}
