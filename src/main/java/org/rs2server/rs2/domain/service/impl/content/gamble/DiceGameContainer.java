package org.rs2server.rs2.domain.service.impl.content.gamble;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.rs2server.rs2.model.Item;
import org.rs2server.rs2.model.ItemDefinition;
import org.rs2server.rs2.model.container.Container;
import org.rs2server.rs2.model.player.Player;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

/**
 * @author Clank1337
 */
public final @Getter @Setter @ToString
class DiceGameContainer extends Container implements Iterable<Item>{

	private Player player;
	private boolean accepted;

	public DiceGameContainer(Player player) {
		super(Type.STANDARD, DiceGameServiceImpl.COMPONENT_CAPACITY);
		this.player = player;
	}

	@Override
	public final void set(int index, Item item) {
		getItems()[index] = item;
		if (isFiringEvents()) {
			fireItemChanged(index);
		}
	}

	@Override
	public Iterator<Item> iterator() {
		return Arrays.asList(getItems()).stream().filter(Objects::nonNull).iterator();
	}

	public Object setAccepted(boolean b) {
		// TODO Auto-generated method stub
		return accepted;
	}

	public Player getPlayer() {
		// TODO Auto-generated method stub
		return player;
	}

	public ItemDefinition getDefinition() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isAccepted() {
		// TODO Auto-generated method stub
		return false;
	}

}
