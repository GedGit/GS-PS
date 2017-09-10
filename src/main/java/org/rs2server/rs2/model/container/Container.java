package org.rs2server.rs2.model.container;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.util.functional.Streamable;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 * A container holds a group of items.
 * 
 * @author Graham Edgecombe
 * 
 */
@SuppressWarnings("rawtypes")
public class Container implements Cloneable, Streamable {

	/**
	 * The type of container.
	 * 
	 * @author Graham Edgecombe
	 * 
	 */
	public enum Type {

		/**
		 * A standard container such as inventory.
		 */
		STANDARD,

		/**
		 * A container which always stacks, e.g. the bank, regardless of the
		 * item.
		 */
		ALWAYS_STACK,

		/**
		 * A container which never stacks, e.g. items on death, regardless of
		 * the item.
		 */
		NEVER_STACK;

	}

	/**
	 * The capacity of this container.
	 */
	private int capacity;

	/**
	 * The items in this container.
	 */
	private Item[] items;

	/**
	 * A list of listeners.
	 */
	private List<ContainerListener> listeners = new LinkedList<ContainerListener>();

	/**
	 * The container type.
	 */
	private Type type;

	/**
	 * Firing events flag.
	 */
	private boolean firingEvents = true;

	/**
	 * Creates the container with the specified capacity.
	 * 
	 * @param type
	 *            The type of this container.
	 * @param capacity
	 *            The capacity of this container.
	 */
	public Container(Type type, int capacity) {
		this.type = type;
		this.capacity = capacity;
		this.items = new Item[capacity];
	}

	/**
	 * Sets the firing events flag.
	 * 
	 * @param firingEvents
	 *            The flag.
	 */
	public void setFiringEvents(boolean firingEvents) {
		this.firingEvents = firingEvents;
	}

	/**
	 * Checks the firing events flag.
	 * 
	 * @return <code>true</code> if events are fired, <code>false</code> if not.
	 */
	public boolean isFiringEvents() {
		return firingEvents;
	}

	/**
	 * Gets the listeners of this container.
	 * 
	 * @return The listeners of this container.
	 */
	public Collection<ContainerListener> getListeners() {
		return Collections.unmodifiableCollection(listeners);
	}

	/**
	 * Adds a listener.
	 * 
	 * @param listener
	 *            The listener to add.
	 */
	public void addListener(ContainerListener listener) {
		listeners.add(listener);
		listener.itemsChanged(this);
	}

	/**
	 * Removes a listener.
	 * 
	 * @param listener
	 *            The listener to remove.
	 */
	public void removeListener(ContainerListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Removes all listeners.
	 */
	public void removeAllListeners() {
		listeners.clear();
	}

	/**
	 * Shifts all items to the top left of the container leaving no gaps.
	 */
	public void shift() {
		Item[] old = items;
		items = new Item[capacity];
		int newIndex = 0;
		for (int i = 0; i < items.length; i++) {
			if (old[i] != null) {
				items[newIndex] = old[i];
				newIndex++;
			}
		}
		if (firingEvents) {
			fireItemsChanged();
		}
	}

	public Item[] getItems() {
		return items;
	}

	/**
	 * Gets the next free slot.
	 * 
	 * @return The slot, or <code>-1</code> if there are no available slots.
	 */
	public int freeSlot() {
		for (int i = 0; i < items.length; i++) {
			if (items[i] == null) {
				return i;
			}
		}
		return -1;
	}

	public void addItems(int... items) {
		for (int item : items) {
			add(new Item(item), -1);
		}
	}

	/**
	 * Attempts to add an item into the next free slot.
	 * 
	 * @param item
	 *            The item.
	 * @return <code>true</code> if the item was added, <code>false</code> if
	 *         not.
	 */
	public boolean add(Item item) {
		return add(item, -1);
	}

	/**
	 * Attemps to add a item into the next free slot ignoring stackable issues
	 * with the original method
	 *
	 * @param item
	 * @return <code>true</code> if the item was added, <code>false</code> if
	 *         not.
	 */
	public boolean addItemIgnoreStackPolicy(Item item) {
		if (item.getDefinition().isStackable() || type.equals(Type.ALWAYS_STACK)) {
			for (int i = 0; i < items.length; i++) {
				if (items[i] != null && items[i].getId() == item.getId()) {
					int totalCount = item.getCount() + items[i].getCount();
					if (totalCount >= Constants.MAX_ITEMS || totalCount < 1) {
						return false;
					}
					set(i, new Item(items[i].getId(), items[i].getCount() + item.getCount()));
					return true;
				}
			}
			int slot = freeSlot();
			if (slot == -1) {
				return false;
			} else {
				set(slot, item);
				return true;
			}
		} else {
			int slots = freeSlots();
			if (slots >= item.getCount()) {
				boolean b = firingEvents;
				firingEvents = false;
				try {
					for (int i = 0; i < item.getCount(); i++) {
						set(freeSlot(), new Item(item.getId()));
					}
					if (b) {
						fireItemsChanged();
					}
					return true;
				} finally {
					firingEvents = b;
				}
			} else {
				return false;
			}
		}
	}

	/**
	 * Attempts to add a specific slot.
	 * 
	 * @param item
	 *            The item.
	 * @param slot
	 *            The desired slot.
	 * @return <code>true</code> if the item was added, <code>false</code> if
	 *         not.
	 */
	public boolean add(Item item, int slot) {
		if (item == null) {
			return false;
		}
		int newSlot = (slot > -1) ? slot : freeSlot();
		if ((item.getDefinition().isStackable() || type.equals(Type.ALWAYS_STACK)) && !type.equals(Type.NEVER_STACK)) {
			if (getCount(item.getId()) > 0) {
				newSlot = getSlotById(item.getId());
			}
		}
		if (newSlot == -1) {
			// the free slot is -1
			return false;
		}
		if (get(newSlot) != null) {
			newSlot = freeSlot();
		}
		if ((item.getDefinition().isStackable() || type.equals(Type.ALWAYS_STACK)) && !type.equals(Type.NEVER_STACK)) {
			for (int i = 0; i < items.length; i++) {
				if (items[i] != null && items[i].getId() == item.getId()) {
					int totalCount = item.getCount() + items[i].getCount();
					if (totalCount >= Constants.MAX_ITEMS || totalCount < 1) {
						return false;
					}
					set(i, new Item(items[i].getId(), items[i].getCount() + item.getCount()));
					return true;
				}
			}
			if (newSlot == -1) {
				return false;
			} else {
				set(slot > -1 ? newSlot : freeSlot(), item);
				return true;
			}
		} else {
			int slots = freeSlots();
			if (slots >= item.getCount()) {
				boolean b = firingEvents;
				firingEvents = false;
				try {
					for (int i = 0; i < item.getCount(); i++) {
						set(slot > -1 ? newSlot : freeSlot(), new Item(item.getId()));
					}
					if (b) {
						fireItemsChanged();
					}
					return true;
				} finally {
					firingEvents = b;
				}
			} else {
				return false;
			}
		}
	}

	public boolean deposit(Player player, Item item, int slot) {
		if (item == null) {
			return false;
		}
		int newSlot = (slot > -1) ? slot : freeSlot();
		if ((item.getDefinition().isStackable() || type.equals(Type.ALWAYS_STACK)) && !type.equals(Type.NEVER_STACK)) {
			if (getCount(item.getId()) > 0) {
				newSlot = getSlotById(item.getId());
			}
		}
		if (newSlot == -1) {
			// the free slot is -1
			return false;
		}
		if (get(newSlot) != null) {
			newSlot = freeSlot();
		}
		int currentTab = player.getBanking().getCurrentTab();
		if (currentTab != 0) {
			newSlot = player.getBanking().getTabStartSlot()[currentTab] + player.getBanking().getItemsInTab(currentTab);
		}
		if ((item.getDefinition().isStackable() || type.equals(Type.ALWAYS_STACK)) && !type.equals(Type.NEVER_STACK)) {
			for (int i = 0; i < items.length; i++) {
				if (items[i] != null && items[i].getId() == item.getId()) {
					int totalCount = item.getCount() + items[i].getCount();
					if (totalCount >= Constants.MAX_ITEMS || totalCount < 1) {
						return false;
					}
					set(i, new Item(items[i].getId(), items[i].getCount() + item.getCount()));
					return true;
				}
			}
			if (newSlot == -1) {
				return false;
			} else {
				set(slot > -1 ? newSlot : freeSlot(), item);
				return true;
			}
		} else {
			int slots = freeSlots();
			if (slots >= item.getCount()) {
				boolean b = firingEvents;
				firingEvents = false;
				try {
					for (int i = 0; i < item.getCount(); i++) {
						set(slot > -1 ? newSlot : freeSlot(), new Item(item.getId()));
					}
					if (b) {
						fireItemsChanged();
					}
					return true;
				} finally {
					firingEvents = b;
				}
			} else {
				return false;
			}
		}
	}

	/**
	 * Gets the number of free slots.
	 * 
	 * @return The number of free slots.
	 */
	public int freeSlots() {
		return capacity - size();
	}

	/**
	 * Gets an item.
	 * 
	 * @param index
	 *            The position in the container.
	 * @return The item.
	 */
	public Item get(int index) {
		return items[index];
	}

	public Item getByButton(int button) {
		int index = 0;
		switch (button) {
		case 14:
			index = 10;
			break;
		case 16:
			index = 13;
			break;
		case 15:
			index = 12;
			break;
		case 13:
			index = 9;
			break;
		case 12:
			index = 7;
			break;
		case 11:
			index = 5;
			break;
		}
		return items[index];
	}

	/**
	 * Gets the amount of an item.
	 * 
	 * @param item
	 *            The item.
	 * @return The amount of this item in this container.
	 */
	public int getAmount(Item item) {
		if (item == null) {
			return 0;
		}
		int count = 0;
		for (Item i : items) {
			if (i != null && i.getId() == item.getId()) {
				count += i.getCount();
			}
		}
		return count;
	}

	/**
	 * Gets the amount.
	 * 
	 * @param id
	 *            the id.
	 * @return the amount.
	 */
	public int getAmount(int id) {
		return getAmount(new Item(id));
	}

	/**
	 * Gets an item by id.
	 * 
	 * @param id
	 *            The id.
	 * @return The item, or <code>null</code> if it could not be found.
	 */
	public Item getById(int id) {
		for (int i = 0; i < items.length; i++) {
			if (items[i] == null) {
				continue;
			}
			if (items[i].getId() == id) {
				return items[i];
			}
		}
		return null;
	}

	/**
	 * Gets a slot by id.
	 * 
	 * @param id
	 *            The id.
	 * @return The slot, or <code>-1</code> if it could not be found.
	 */
	public int getSlotById(int id) {
		for (int i = 0; i < items.length; i++) {
			if (items[i] == null) {
				continue;
			}
			if (items[i].getId() == id) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Sets an item.
	 * 
	 * @param index
	 *            The position in the container.
	 * @param item
	 *            The item.
	 */
	public void set(int index, Item item) {
		items[index] = item;
		if (firingEvents) {
			fireItemChanged(index);
		}
	}

	/**
	 * Gets the capacity of this container.
	 * 
	 * @return The capacity of this container.
	 */
	public int capacity() {
		return capacity;
	}

	/**
	 * Gets the size of this container.
	 * 
	 * @return The size of this container.
	 */
	public int size() {
		int size = 0;
		for (int i = 0; i < items.length; i++) {
			if (items[i] != null) {
				size++;
			}
		}
		return size;
	}

	/**
	 * Clears this container.
	 */
	public void clear() {
		items = new Item[items.length];
		if (firingEvents) {
			fireItemsChanged();
		}
	}

	/**
	 * Returns an array representing this container.
	 * 
	 * @return The array.
	 */
	public Item[] toArray() {
		return items;
	}

	/**
	 * Checks if a slot is used.
	 * 
	 * @param slot
	 *            The slot.
	 * @return <code>true</code> if an item is present, <code>false</code>
	 *         otherwise.
	 */
	public boolean isSlotUsed(int slot) {
		return items[slot] != null;
	}

	/**
	 * Checks if a slot is free.
	 * 
	 * @param slot
	 *            The slot.
	 * @return <code>true</code> if an item is not present, <code>false</code>
	 *         otherwise.
	 */
	public boolean isSlotFree(int slot) {
		return items[slot] == null;
	}

	public void removeItems(int... items) {
		for (int item : items) {
			remove(new Item(item), -1, false);
		}
	}

	public void removeItems(Item... items) {
		for (Item item : items) {
			remove(item, -1, false);
		}
	}

	/**
	 * Removes an item.
	 * 
	 * @param item
	 *            The item to remove.
	 * @return The number of items removed.
	 */
	public int remove(Item item) {
		return remove(item, -1, false);
	}

	public int remove(int preferredSlot, Item item) {
		int removed = 0;
		if (item.getDefinition().isStackable() || type.equals(Type.ALWAYS_STACK)) {
			int slot = getSlotById(item.getId());
			if (slot == -1) {
				return 0;
			}
			Item stack = get(slot);
			if (stack.getCount() > item.getCount()) {
				removed = item.getCount();
				set(slot, new Item(stack.getId(), stack.getCount() - item.getCount()));
			} else {
				removed = stack.getCount();
				set(slot, null);
			}
		} else {
			for (int i = 0; i < item.getCount(); i++) {
				int slot = getSlotById(item.getId());
				if (i == 0 && preferredSlot != -1) {
					Item inSlot = get(preferredSlot);
					if (inSlot != null && inSlot.getId() == item.getId()) {
						slot = preferredSlot;
					}
				}
				if (slot != -1) {
					removed++;
					set(slot, null);
				} else {
					break;
				}
			}
		}
		return removed;
	}

	/**
	 * Removes an item, allowing zeros.
	 * 
	 * @param item
	 *            The item.
	 * @return The number of items removed.
	 */
	public int removeOrZero(Item item) {
		return remove(item, -1, true);
	}

	/**
	 * Removes an item.
	 * 
	 * @param item
	 *            The item.
	 * @param preferredSlot
	 *            The preferred slot.
	 * @return The number of items removed.
	 */
	public int remove(Item item, int preferredSlot) {
		return remove(item, preferredSlot, false);
	}

	/**
	 * Removes an item.
	 * 
	 * @param itemToRemove
	 *            The item to remove.
	 * @param preferredSlot
	 *            The preferred slot.
	 * @param allowZero
	 *            If a zero amount item should be allowed.
	 * @return The number of items removed.
	 */
	public int remove(Item itemToRemove, int preferredSlot, boolean allowZero) {
		int removed = 0;
		if ((itemToRemove.getDefinition().isStackable() || type.equals(Type.ALWAYS_STACK))
				&& !type.equals(Type.NEVER_STACK)) {
			int slot = getSlotById(itemToRemove.getId());
			Item itemInSlot = get(slot);
			if (itemInSlot.getCount() > itemToRemove.getCount()) {
				removed = itemToRemove.getCount();
				set(slot, new Item(itemInSlot.getId(), itemInSlot.getCount() - itemToRemove.getCount()));
			} else {
				removed = itemInSlot.getCount();
				set(slot, allowZero ? new Item(itemInSlot.getId(), 0) : null);
			}
		} else {
			for (int i = 0; i < itemToRemove.getCount(); i++) {
				int slot = getSlotById(itemToRemove.getId());
				if (i == 0 && preferredSlot != -1) {
					Item inSlot = get(preferredSlot);
					if (inSlot.getId() == itemToRemove.getId()) {
						slot = preferredSlot;
					}
				}
				if (slot != -1) {
					removed++;
					set(slot, null);
				} else {
					break;
				}
			}
		}
		return removed;
	}

	/**
	 * Transfers an item from one container to another.
	 * 
	 * @param from
	 *            The container to transfer from.
	 * @param to
	 *            The container to transfer to.
	 * @param fromSlot
	 *            The slot in the original container.
	 * @param id
	 *            The item id.
	 * @return A flag indicating if the transfer was successful.
	 */
	public static boolean transfer(Container from, Container to, int fromSlot, int id) {
		Item fromItem = from.get(fromSlot);
		if (fromItem == null || fromItem.getId() != id) {
			return false;
		}
		if (to.add(fromItem)) {
			from.set(fromSlot, null);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Swaps two items.
	 * 
	 * @param fromSlot
	 *            From slot.
	 * @param toSlot
	 *            To slot.
	 */
	public void swap(int fromSlot, int toSlot) {
		Item temp = get(fromSlot);
		boolean b = firingEvents;
		firingEvents = false;
		try {
			set(fromSlot, get(toSlot));
			set(toSlot, temp);
			if (b) {
				fireItemsChanged(new int[] { fromSlot, toSlot });
			}
		} finally {
			firingEvents = b;
		}
	}

	/**
	 * Gets the total amount of an item, including the items in stacks.
	 * 
	 * @param id
	 *            The id.
	 * @return The amount.
	 */
	public int getCount(int id) {
		int total = 0;
		for (int i = 0; i < items.length; i++) {
			if (items[i] != null) {
				if (items[i].getId() == id) {
					total += items[i].getCount();
				}
			}
		}
		return total;
	}

	/**
	 * Inserts an item.
	 * 
	 * @param fromSlot
	 *            The old slot.
	 * @param toSlot
	 *            The new slot.
	 */
	public void insert(int fromSlot, int toSlot) {
		// we reset the item in the from slot
		Item from = items[fromSlot];
		if (from == null) {
			return;
		}
		items[fromSlot] = null;
		// find which direction to shift in
		if (fromSlot > toSlot) {
			int shiftFrom = toSlot;
			int shiftTo = fromSlot;
			for (int i = (toSlot + 1); i < fromSlot; i++) {
				if (items[i] == null) {
					shiftTo = i;
					break;
				}
			}
			Item[] slice = new Item[shiftTo - shiftFrom];
			System.arraycopy(items, shiftFrom, slice, 0, slice.length);
			System.arraycopy(slice, 0, items, shiftFrom + 1, slice.length);
		} else {
			int sliceStart = fromSlot + 1;
			int sliceEnd = toSlot;
			for (int i = (sliceEnd - 1); i >= sliceStart; i--) {
				if (items[i] == null) {
					sliceStart = i;
					break;
				}
			}
			Item[] slice = new Item[sliceEnd - sliceStart + 1];
			System.arraycopy(items, sliceStart, slice, 0, slice.length);
			System.arraycopy(slice, 0, items, sliceStart - 1, slice.length);
		}
		// now fill in the target slot
		items[toSlot] = from;
		if (firingEvents) {
			fireItemsChanged();
		}
	}

	/**
	 * Fires an item changed event.
	 * 
	 * @param slot
	 *            The slot that changed.
	 */
	public void fireItemChanged(int slot) {
		for (ContainerListener listener : listeners) {
			listener.itemChanged(this, slot);
		}
	}

	/**
	 * Fires an items changed event.
	 */
	public void fireItemsChanged() {
		for (ContainerListener listener : listeners) {
			listener.itemsChanged(this);
		}
	}

	/**
	 * Fires an items changed event.
	 * 
	 * @param slots
	 *            The slots that changed.
	 */
	public void fireItemsChanged(int[] slots) {
		for (ContainerListener listener : listeners) {
			listener.itemsChanged(this, slots);
		}
	}

	/**
	 * Checks if the container contains the specified item.
	 * 
	 * @param id
	 *            The item id.
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean contains(int id) {
		return getSlotById(id) != -1;
	}

	public boolean containsItems(int... ids) {
		for (int id : ids) {
			if (getSlotById(id) == -1)
				return false;
		}
		return true;
	}

	public boolean containsOneItem(int... itemIds) {
		for (int itemId : itemIds) {
			if (getSlotById(itemId) != -1)
				return true;
		}
		return false;
	}

	public boolean containsItems(Item... items) {
		for (Item item : items) {
			if (getSlotById(item.getId()) == -1)
				return false;
		}
		return true;
	}

	/**
	 * Checks if there is room in the inventory for an item.
	 * 
	 * @param item
	 *            The item.
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean hasRoomFor(Item item) {
		if ((item.getDefinition2().stackable || type.equals(Type.ALWAYS_STACK)) && !type.equals(Type.NEVER_STACK)) {
			for (int i = 0; i < items.length; i++) {
				if (items[i] != null && items[i].getId() == item.getId()) {
					int totalCount = item.getCount() + items[i].getCount();
					if (totalCount >= Constants.MAX_ITEMS || totalCount < 1) {
						return false;
					}
					return true;
				}
			}
			int slot = freeSlot();
			return slot != -1;
		} else {
			int slots = freeSlots();
			return slots >= item.getCount();
		}

	}

	/**
	 * Sets the containers contents.
	 * 
	 * @param items
	 *            The contents to set.
	 */
	public void setItems(Item[] items) {
		clear();
		for (int i = 0; i < items.length; i++) {
			this.items[i] = items[i];
		}
	}

	public int getSlot(int slot) {
		Item i = get(slot);
		return i == null ? -1 : i.getId();
	}

	public boolean hasItem(Item item) {
		if (getCount(item.getId()) >= 1)
			return true;
		return false;
	}

	public void removeAll(Item item) {
		for (int i = 0; i < items.length; i++) {
			if (items[i] != null) {
				if (items[i].getId() == item.getId()) {
					items[i] = null;
				}
			}
		}
	}

	public Item[] getContents() {
		return items;
	}

	public boolean replace(int original, int newItem) { 
		int slot = getSlotById(original);
		if (slot == -1)
			return false;
		items[slot] = newItem == -1 ? null : new Item(newItem);
		fireItemsChanged();
		return true;
	}

	@Override
	public Container clone() {
		Container container = new Container(type, items.length);
		for (int i = 0; i < items.length; i++) {
			container.set(i, items[i]);
		}
		return container;
	}

	@Override
	public Stream<Item> stream() {
		return Stream.of(this.items);
	}

	public Player getPlayer() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean remove(int preferredSlot, int i) {
		// TODO Auto-generated method stub
		return false;
	}

}
