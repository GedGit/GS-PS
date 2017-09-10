package org.rs2server.rs2.domain.dao;

import org.rs2server.rs2.Constants;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;

/**
 * MongoService implementation.
 */
public class MongoServiceImpl implements MongoService {

	private DB database;
	final MongoClient mongoClient;

	@SuppressWarnings("deprecation")
	@Inject
	public MongoServiceImpl() {
		mongoClient = new MongoClient(ImmutableList.of(new ServerAddress("127.0.0.1", 27017)));
		mongoClient.setWriteConcern(WriteConcern.ACKNOWLEDGED);
		database = mongoClient.getDB(Constants.DEBUG ? Constants.SERVER_NAME + "-debug" : "lostisle");
		if (Constants.DEBUG)
			System.out.println("[MongoServiceImpl] Starting on database: '" + database.getName() + "'!");
	}

	@Override
	public DB getDatabase() {
		return database;
	}
}