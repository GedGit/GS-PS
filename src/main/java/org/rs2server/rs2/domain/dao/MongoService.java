package org.rs2server.rs2.domain.dao;

import com.mongodb.DB;

/**
 * MongoDB service.
 */
public interface MongoService {

	DB getDatabase();

}