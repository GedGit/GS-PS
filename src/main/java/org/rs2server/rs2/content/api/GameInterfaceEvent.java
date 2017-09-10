package org.rs2server.rs2.content.api;

import javax.annotation.concurrent.Immutable;

/**
 * An event which is fired when an interface state has changed.
 *
 * @author tommo
 */
@Immutable
public class GameInterfaceEvent {

	public enum EventType {
		INTERFACE_OPENED, INTERFACE_CLOSED
	}

	private int interfaceId;
	private EventType eventType;

	public GameInterfaceEvent(int interfaceId, EventType eventType) {
		this.interfaceId = interfaceId;
		this.eventType = eventType;
	}

	public int getInterfaceId() {
		return interfaceId;
	}

	public EventType getEventType() {
		return eventType;
	}
}
