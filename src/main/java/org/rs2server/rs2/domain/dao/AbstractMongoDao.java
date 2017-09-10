package org.rs2server.rs2.domain.dao;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.DuplicateKeyException;
import org.mongojack.DBCursor;
import org.mongojack.DBQuery;
import org.mongojack.DBSort;
import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;
import org.mongojack.internal.MongoJackModule;

/**
 * Abstract DAO implementation.
 */
public abstract class AbstractMongoDao<T extends MongoEntity> implements MongoDao<T> {

	private static final Logger logger = LoggerFactory.getLogger(AbstractMongoDao.class);

	/**
	 * Custom configured ObjectMapper instance to use the JodaModule in the MongoDB jackson mapper.
	 */
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private final MongoService mongoService;

	private final String collectionName;

	private final Class<T> tClass;

	private JacksonDBCollection<T, String> jacksonDBCollection;

	private AtomicBoolean dbReady = new AtomicBoolean(false);

	protected AbstractMongoDao(final MongoService mongoService, final String collectionName, final Class<T> tClass) {
		this.mongoService = mongoService;
		this.collectionName = collectionName;
		this.tClass = tClass;
		OBJECT_MAPPER.registerModule(new JodaModule());
		MongoJackModule.configure(OBJECT_MAPPER);
	}

	protected JacksonDBCollection<T, String> getJacksonDBCollection() {
		initMongoDatabase();
		return jacksonDBCollection;
	}

	@SuppressWarnings("deprecation")
	protected void ensureUniqueIndex(String idToIndex) {
		BasicDBObject query = new BasicDBObject(idToIndex, 1);
		getJacksonDBCollection().ensureIndex(query, idToIndex + "IndexUnique", true);
	}

	@SuppressWarnings("deprecation")
	protected void ensureNonUniqueIndex(String idToIndex) {
		BasicDBObject query = new BasicDBObject(idToIndex, 1);
		getJacksonDBCollection().ensureIndex(query, idToIndex + "Index", false);
	}

	/**
	 * Implement your indices here. This method will be called only once before the first access of any DB method.
	 */
	protected abstract void ensureIndices();

	/**
	 * Prepares the MongoDB if not already initialised.
	 * <p>
	 * This method is thread-safe for the rare case where the db is not yet available and
	 * multiple threads attempt to initialise it simultaneously.
	 */
	private void initMongoDatabase() {
		try {
			if (dbReady.compareAndSet(false, true)) {
				final DBCollection dbCollection = mongoService.getDatabase().getCollection(collectionName);
				jacksonDBCollection = JacksonDBCollection.wrap(dbCollection, tClass, String.class, OBJECT_MAPPER);

				ensureIndices();
			}
		} catch (Exception e) {
			logger.error("Encountered MongoDB error.", e);
		}
	}

	@Override
	public T create(@Nonnull final T t) throws DuplicateKeyException {
		Objects.requireNonNull(t);
		updateTimestamps(t);

		final WriteResult<T, String> insert = getJacksonDBCollection().insert(t);
		return insert.getSavedObject();
	}

	@Override
	public T save(@Nonnull final T t) {
		Objects.requireNonNull(t);
		updateTimestamps(t);

		// This performs an atomic upsert in MongoDB.
		final WriteResult<T, String> result = getJacksonDBCollection().save(t);
		return result.getSavedObject();
	}

	private void updateTimestamps(@Nonnull final T t) {
		final long now = DateTime.now(DateTimeZone.UTC).getMillis();
		if (t.getTimestampCreated() == 0) {
			t.setTimestampCreated(now);
		}

		t.setTimestampUpdated(now);
	}

	@Override
	public long countAll() {
		return getJacksonDBCollection().count();
	}

	@Override
	public void deleteAll() {
		getJacksonDBCollection().drop();
		ensureIndices();
	}

	@Override
	public void drop() {
		getJacksonDBCollection().drop();
	}

	@Override
	@Nonnull
	public List<T> findAll() {
		return toList(getJacksonDBCollection().find());
	}

	@Override
	@Nullable
	public T find(@Nonnull String id) {
		Objects.requireNonNull(id, "id");

		try {
			return getJacksonDBCollection().findOneById(id);
		} catch (IllegalArgumentException e) {
			// This exception is thrown by the mongo driver if the id is an invalid ObjectId (@see ObjectId.isValid)
			// although some documents use custom id's so we cannot simply verify the id ourselves
			return null;
		}
	}

	/**
	 * Converts the input cursor to a list.
	 *
	 * @param cursor
	 *            if null, an empty list will be returned.
	 * @return the list, all elements in the cursor are added to it.
	 */
	protected List<T> toList(@Nullable final DBCursor<T> cursor) {
		if (cursor == null) {
			return new ArrayList<>();
		}

		final List<T> result = new ArrayList<>();
		final Iterator<T> iterator = toIterator(cursor);
		while (iterator.hasNext()) {
			result.add(iterator.next());
		}
		return result;
	}

	protected Iterator<T> toIterator(final DBCursor<T> cursor) {
		if (cursor == null || cursor.getCursor() == null) {
			return new Iterator<T>() {
				@Override
				public boolean hasNext() {
					return false;
				}

				@Override
				public T next() {
					return null;
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}

		return new Iterator<T>() {
			@Override
			public boolean hasNext() {
				return cursor.hasNext();
			}

			@Override
			public T next() {
				return cursor.next();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * Retrieves a list of documents matching field=value.
	 */
	@Nonnull
	protected List<T> findListByField(@Nonnull String field, @Nonnull Object value) {
		Objects.requireNonNull(field, "field");
		Objects.requireNonNull(value, "value");
		return findListByQuery(DBQuery.is(field, value));
	}

	protected List<T> findListByQuery(final DBQuery.Query query) {
		Objects.requireNonNull(query, "query");
		final DBCursor<T> cursor = getJacksonDBCollection().find(query);
		return toList(cursor);
	}

	protected List<T> findListByQuery(final DBObject query) {
		Objects.requireNonNull(query, "query");
		final DBCursor<T> cursor = getJacksonDBCollection().find(query);
		return toList(cursor);
	}

	protected List<T> findListByQuerySortedByField(final DBQuery.Query query, String sortingField) {
		Objects.requireNonNull(query, "query");
		Objects.requireNonNull(sortingField, "query");
		final DBCursor<T> cursor = getJacksonDBCollection().find(query).sort(DBSort.asc(sortingField));
		return toList(cursor);
	}

	protected T findSingleByQuery(DBQuery.Query query) {
		return getJacksonDBCollection().findOne(query);
	}
}
