package org.rs2server.rs2.domain.service.api.content.trade;

import lombok.Value;
import org.rs2server.rs2.model.Item;

/**
 * @author Clank1337
 */
public final @Value
class IndexedTradeItem {
	private final Item item;
	private final int index;
	
	public int getIndex() {
		return index;
	}
	public Item getItem() {
		return item;
	}
	
	public IndexedTradeItem (final Item item, int index) {
		this.item = item;
		this.index = index;
	}
}