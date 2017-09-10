package org.rs2server.rs2.model.event;


import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.event.EventListener.ClickOption;
import org.rs2server.rs2.model.player.Player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


/**
 * @author 'Mystic Flow <Steven@rune-server.org>
 */
public class ClickEventManager {

	private static final ClickEventManager MANAGER = new ClickEventManager();

	public static ClickEventManager getEventManager() {
		return MANAGER;
	}

	/**
	 * The mapping for item action event listeners.
	 */
	private final Map<Integer, EventListener> itemListeners = new HashMap<Integer, EventListener>();

	private Map<Integer, EventListener> objectListeners = new HashMap<Integer, EventListener>();
	private Map<Integer, List<EventListener>> interfaceListeners = new HashMap<Integer, List<EventListener>>();
	private List<EventListener> listeners = new LinkedList<EventListener>();

	public void load() throws Exception {
		Logger logger = Logger.getAnonymousLogger();
		BufferedReader reader = new BufferedReader(new FileReader("data/eventListeners.txt"));
		String string;
		while ((string = reader.readLine()) != null) {
			if (!string.startsWith(">")) {
				continue;
			}
			string = string.substring(1);
			if (Class.forName(string) != null) {
				EventListener listener = (EventListener) Class.forName(string).newInstance();
				listener.register(this);

				listeners.add(listener); // we add it just in case
			}
		}
		reader.close();
		logger.info("Loaded " + interfaceListeners.size() + " interface listeners, " + itemListeners.size() + " item listeners and " + objectListeners.size() + " object listeners.");
	}

	public List<File> allFiles(File[] files) {
		List<File> list = new ArrayList<File>();
		addDirectoryFiles(list, files);
		return list;
	}

	public void addDirectoryFiles(List<File> list, File[] files) {
		for (File f : files) {
			if (f.isDirectory()) {
				addDirectoryFiles(list, f.listFiles());
			} else {
				list.add(f);
			}
		}
	}

	public boolean handleObjectOption(Player player, int objectId, GameObject gameObject, Location location, ClickOption option) {
		EventListener listener = objectListeners.get(objectId);
		return listener != null && listener.objectAction(player, objectId, gameObject, location, option);
	}

	public boolean handleInterfaceOption(Player player, int interfaceId, int buttonId, int slot, int itemId, int opcode) {
		List<EventListener> listeners = interfaceListeners.get(interfaceId);
		boolean handled = false;
		if (listeners != null) {
			for (EventListener listener : listeners) {
				if (listener.interfaceOption(player, interfaceId, buttonId, slot, itemId, opcode)) {
					handled = true;
				}
			}
		}
		return handled;
	}

	/**
	 * Handles an item action.
	 * @param player The player.
	 * @param item The item clicked.
	 * @param slot The item slot.
	 * @param option The option type.
	 * @return {@code True} if the action got handled.
	 */
	public boolean handleItemAction(Player player, Item item, int slot, ClickOption option) {
		if (item == null)
			return false;
		EventListener listener = itemListeners.get(item.getId());
		if (listener != null) {
			return listener.itemAction(player, item, slot, option);
		}
		return false;
	}

	/**
	 * Registers an item action event listener.
	 * @param itemId The item id.
	 * @param listener The event listener.
	 */
	public void registerItemListener(int itemId, EventListener listener) {
		if (itemListeners.containsKey(itemId)) {
			throw new IllegalStateException("There are already is a listener registered to this item id! [" + itemId + "]");
		}
		
		itemListeners.put(itemId, listener);
	}

	public void registerObjectListener(int objectId, EventListener listener) {
		if (objectListeners.get(objectId) != null) {
			throw new IllegalStateException("There are already is a listener to this id! id=" + objectId + ", user=" + objectListeners.get(objectId) + " attempt=" + listener);
		}
		objectListeners.put(objectId, listener);
	}

	public void registerInterfaceListener(int interfaceId, EventListener listener) {
		List<EventListener> listeners = interfaceListeners.get(interfaceId);
		if (listeners == null) {
			interfaceListeners.put(interfaceId, listeners = new ArrayList<EventListener>());
		}
		listeners.add(listener);
	}


}
