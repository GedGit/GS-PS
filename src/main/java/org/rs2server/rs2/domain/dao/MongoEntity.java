package org.rs2server.rs2.domain.dao;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.mongojack.Id;
import org.mongojack.ObjectId;

/**
 * Defines a MongoDB entity.
 */
public abstract class MongoEntity {

	@ObjectId
	@Id
	private String id;

	private long timestampCreated;

	private long timestampUpdated;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (!(o instanceof MongoEntity))
			return false;

		MongoEntity that = (MongoEntity) o;

		return EqualsBuilder.reflectionEquals(this, that, false);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(17, 37, this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@ObjectId
	@Id
	public String getId() {
		return id;
	}

	@ObjectId
	@Id
	public void setId(String id) {
		this.id = id;
	}

	public long getTimestampCreated() {
		return timestampCreated;
	}

	public void setTimestampCreated(long timestampCreated) {
		this.timestampCreated = timestampCreated;
	}

	public long getTimestampUpdated() {
		return timestampUpdated;
	}

	public void setTimestampUpdated(long timestampUpdated) {
		this.timestampUpdated = timestampUpdated;
	}

}
