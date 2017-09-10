package org.rs2server.rs2.model;

import org.rs2server.rs2.content.api.GameInterfaceButtonEvent;

import com.mongodb.annotations.Immutable;

/**
 * A song to be played.
 * @author twelve
 */
@Immutable
public final class Song {

	private final int id;

	public Song(int id) {
		this.id = id;
	}

	public static Song of(int id) {
		return new Song(id);
	}

	public static Song of(GameInterfaceButtonEvent event) {
		switch(event.getChildButton()) {
			case 2:
				return of(0);
			case 6:
				return of(76);
		}
		return null;
	}

	public int getId() {
		return id;
	}
}
