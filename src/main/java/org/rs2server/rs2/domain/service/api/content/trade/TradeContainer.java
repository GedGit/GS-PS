package org.rs2server.rs2.domain.service.api.content.trade;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.rs2server.rs2.domain.service.impl.content.trade.TradeServiceImpl;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.container.Container;
import org.rs2server.rs2.model.player.Player;

import java.util.*;

/**
 * @author twelve
 */
public final @Getter @Setter @ToString
class TradeContainer extends Container implements Iterable<Item> {
	private final Player player;
	private final List<Integer> modified;
	private boolean accepted;

	public TradeContainer(Player player) {
		super(Type.STANDARD, TradeServiceImpl.COMPONENT_CAPACITY);
		this.modified = new ArrayList<>();
		this.player = player;
	}

	@Override
	public final void set(int index, Item item) {
		if (getItems()[index] != null && !modified.contains(index)) {
			modified.add(index);
		}
		getItems()[index] = item;
		if (isFiringEvents()) {
			fireItemChanged(index);
		}
	}

	@Override
	public Iterator<Item> iterator() {
		return Arrays.asList(getItems()).stream().filter(Objects::nonNull).iterator();
	}

	public Player getPlayer() {
		return player;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}
}
