package org.rs2server.rs2.domain.dao;

import com.mongodb.DuplicateKeyException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Base interface for all data access objects.
 */
public interface MongoDao<T extends MongoEntity> {

	/**
	 * Persists and returns the given entity.
	 *
	 * @param t
	 * 		the entity to persist
	 * @throws DuplicateKeyException
	 * @return the persisted entity
	 */
	T create(T t) throws DuplicateKeyException;

	/**
	 * Upserts an entity.
	 * @param t The entity to upsert.
	 * @return The persisted entity.
	 */
	T save(T t);

	/**
	 * Counts all objects of the collection managed by the concrete implementation.
	 *
	 * @return number of elements in the collection.
	 */
	long countAll();

	/**
	 * Removes all elements from the collection.
	 */
	void deleteAll();

	/**
	 * Removes all elements from the collection and deletes its indices.
	 */
	void drop();

	/**
	 * Searches for a single element in the collection that has the input id.
	 *
	 * @param id
	 *            the id to search for - cannot be null.
	 * @return the item or null.
	 */
	@Nullable
	T find(@Nonnull String id);

	/**
	 * Returns all elements in the collection.
	 */
	@Nonnull
	List<T> findAll();

}
