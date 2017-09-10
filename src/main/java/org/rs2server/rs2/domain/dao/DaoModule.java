package org.rs2server.rs2.domain.dao;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import org.rs2server.rs2.domain.dao.api.ClaimEntityDao;
import org.rs2server.rs2.domain.dao.api.PlayerEntityDao;
import org.rs2server.rs2.domain.dao.impl.ClaimEntityDaoImpl;
import org.rs2server.rs2.domain.dao.impl.PlayerEntityDaoImpl;

/**
 * Module for binding DAOs.
 *
 * @author tommo
 */
public class DaoModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(PlayerEntityDao.class).to(PlayerEntityDaoImpl.class).in(Singleton.class);
		bind(ClaimEntityDao.class).to(ClaimEntityDaoImpl.class).in(Singleton.class);
	}
}
