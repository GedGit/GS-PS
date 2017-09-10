package org.rs2server.rs2.tickable.impl;

import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.Shop;
import org.rs2server.rs2.tickable.Tickable;

import java.util.Objects;

/**
 * Removes items sold by players
 * @author Vichy
 */
public class ShopItemRemoveTick extends Tickable {

	private final Item item;
	private final Shop shop;
	private static final int MAX_TICKS = 5;

	public ShopItemRemoveTick(Item item, Shop shop) {
		super(MAX_TICKS);
		this.item = item;
		this.shop = shop;
	}

	@Override
	public void execute() {
		// Removing player-sold items
		shop.getMainStock().stream().filter(Objects::nonNull).filter(i -> i.getId() == item.getId()).forEach(i -> {
			// Don't remove default stock items
			if (shop.getDefaultStock().getById(i.getId()) != null) {
				this.stop();
				return;
			}
			if (i.getCount() < 2) {
				shop.getMainStock().remove(item);
				shop.getMainStock().fireItemsChanged();
				this.stop();
				return;
			}
			i.setCount(i.getCount() - 1);
			shop.getMainStock().fireItemsChanged();
		});
	}
}
