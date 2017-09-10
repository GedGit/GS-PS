package org.rs2server.rs2.domain.service.api.content;

import org.rs2server.rs2.content.api.GameObjectActionEvent;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

/**
 * @author twelve
 */
public interface ObeliskService {

	enum Obelisks {
		THIRTEEN(Location.create(3154, 3618, 0)),
		NINETEEN(Location.create(3225,3665,0)),
		TWENTY_SEVEN(Location.create(3033,3730,0)),
		THIRTY_FIVE( Location.create(3104,3792,0)),
		FOURTY_FOUR(Location.create(2978,3864,0)),
		FIFTY(Location.create(3305,3914,0));

		private final Location location;

		private boolean inUse;

		Obelisks(Location location) {
			this.location = location;
		}

		public static Optional<Obelisks> of(Location location) {
			return Arrays.stream(values()).filter(o -> o.location.distance(location) <= 8).findAny();
		}

		public void setInUse(boolean used) {
			this.inUse = used;
		}

		public boolean isInUse() {
			return inUse;
		}

		public final Location getLocation() {
			return location;
		}
	}

	void onObeliskObjectClick(@Nonnull GameObjectActionEvent event);

	void activateObelisk(@Nonnull Player player, @Nonnull GameObject object);

	Set<Location> getObeliskLocations(Obelisks obelisk);
}
