package org.rs2server.rs2.tickable.impl;

import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Shop;
import org.rs2server.rs2.tickable.Tickable;

import java.util.Objects;

/**
 * @author Clank1337
 */
public class ShopItemRestoreTick extends Tickable {

	private final Item item;
	private final Shop shop;
	private final int slot;

	private static final int MAX_TICKS = 5;

	public ShopItemRestoreTick(Item item, Shop shop, int slot) {
		super(MAX_TICKS);
		this.item = item;
		this.shop = shop;
		this.slot = slot;
	}

	@Override
	public void execute() {
		// Resetting default store items back to original values
		shop.getMainStock().stream().filter(Objects::nonNull).filter(i -> i.getId() == item.getId()).forEach(i -> {
			if (shop.getMainItems()[slot].getId() == item.getId()) {
				if (i.getCount() >= item.getCount()) {
					this.stop();
					return;
				}
				i.setCount(i.getCount() + 1);
				shop.getMainStock().fireItemsChanged();
			}
		});
	}
}
