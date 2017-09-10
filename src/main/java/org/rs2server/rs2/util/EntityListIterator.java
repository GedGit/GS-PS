package org.rs2server.rs2.util;

import org.rs2server.rs2.model.Mob;

import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * An implementation of an iterator for an entity list.
 * @author Graham Edgecombe
 *
 * @param <E> The type of entity.
 */
public class EntityListIterator<E extends Mob> implements Iterator<E> {
	
	/**
	 * The entities.
	 */
	private Mob[] mobs;
	
	/**
	 * The entity list.
	 */
	private EntityList<E> entityList;
	
	/**
	 * The previous index.
	 */
	private int lastIndex = -1;
	
	/**
	 * The current index.
	 */
	private int cursor = 0;
	
	/**
	 * The size of the list.
	 */
	private int size;

	/**
	 * Creates an entity list iterator.
	 * @param entityList The entity list.
	 */
	public EntityListIterator(EntityList<E> entityList) {
		this.entityList = entityList;
		mobs = entityList.toArray(new Mob[0]);
		size = mobs.length;
	}

	@Override
	public boolean hasNext() {
		return cursor < size;
	}

	@SuppressWarnings("unchecked")
	@Override
	public E next() {
		if(!hasNext()) {
			throw new NoSuchElementException();
		}
		lastIndex = cursor++;
		return (E) mobs[lastIndex];
	}

	@Override
	public void remove() {
		if(lastIndex == -1) {
			throw new IllegalStateException();
		}
		entityList.remove(mobs[lastIndex]);
	}

}
