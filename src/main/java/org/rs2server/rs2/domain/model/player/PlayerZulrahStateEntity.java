package org.rs2server.rs2.domain.model.player;

import lombok.Getter;
import lombok.Setter;
import org.rs2server.rs2.model.Item;

import java.util.List;

/**
 * @author tommo
 */
public final
@Setter @Getter
class PlayerZulrahStateEntity {

	private List<Item> itemsLostZulrah;

	public List<Item> getItemsLostZulrah() {
		return itemsLostZulrah;
	}

	public void setItemsLostZulrah(List<Item> itemsLostZulrah) {
		this.itemsLostZulrah = itemsLostZulrah;
	}

}
