package org.rs2server.rs2.model.player.pc;

import com.google.common.collect.Lists;
import org.rs2server.rs2.Constants;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.npc.pc.*;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.impl.StoppingTick;
import org.rs2server.rs2.util.Misc;
import org.rs2server.util.functional.Streamable;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Represents a single game of pest control.
 * 
 * @author twelve
 */
public final class PestControlInstance implements Streamable<Player> {

	public static final Location START_LOCATION_BASE = Location.create(2657, 2612);
	public static final long CAPACITY = 25;

	private final Set<Player> players;

	private final PestControlPortal bluePortal;
	private final PestControlPortal redPortal;
	private final PestControlPortal purplePortal;
	private final PestControlPortal yellowPortal;

	private final PestControlBoat boat;

	private final VoidKnight knight;
	private boolean destroyed;

	public PestControlInstance(PestControlBoat boat, Set<Player> players) {
		this.boat = boat;
		this.players = players;

		this.bluePortal = PestControlPortal.in(this, PortalCardinality.BLUE, 1744, 1748);
		this.redPortal = PestControlPortal.in(this, PortalCardinality.RED, 1746, 1750);
		this.purplePortal = PestControlPortal.in(this, PortalCardinality.PURPLE, 1743, 1747);
		this.yellowPortal = PestControlPortal.in(this, PortalCardinality.YELLOW, 1745, 1749);
		this.knight = VoidKnight.in(this);

		players.forEach(p -> p.setPestControlInstance(this));
	}

	public List<PestControlPortal> getPortals() {
		return Lists.newArrayList(bluePortal, redPortal, purplePortal, yellowPortal);
	}

	public Set<Player> getPlayers() {
		return players;
	}

	public void sendMessage(@Nonnull String message) {
		stream().map(Player::getActionSender).forEach(a -> a.sendMessage(message));
	}

	@Override
	public Stream<Player> stream() {
		return players.stream();
	}

	public void start() {
		List<PestControlPortal> portals = getPortals();

		players.forEach(p -> {
			p.setTeleportTarget(START_LOCATION_BASE.transform(Misc.random(0, 1), Misc.random(0, 1), 0));

			portals.stream().map(PestControlPortal::getCardinality)
					.forEach(c -> p.getActionSender().sendInterfaceConfig(408, c.getShieldChild(), false));
			p.getActionSender().sendWalkableInterface(408);
		});

		Collections.shuffle(portals);

		World.getWorld().submit(new ShieldDownTick(10, this, portals.get(0)));
		World.getWorld().submit(new ShieldDownTick(40, this, portals.get(1)));
		World.getWorld().submit(new ShieldDownTick(60, this, portals.get(2)));
		World.getWorld().submit(new ShieldDownTick(80, this, portals.get(3)));

		portals.forEach(PestControlPortal::register);
		knight.register();
	}

	public void endGame() {
		players.forEach(p -> {
			if (p.getCombatState().isDead()) {
				World.getWorld().submit(new StoppingTick(10) {
					@Override
					public void executeAndStop() {
						p.getActionSender().removeWalkableInterface();
						p.getActionQueue().clearAllActions();
						p.setTeleportTarget(boat.getExit());
						p.resetVariousInformation();
						if (p.hasAttribute("hits_dealt") && (int) p.getAttribute("hits_dealt") >= 50) {
							int toAdd = 2 * Constants.PEST_MODIFIER;
							p.getActionSender().sendMessage("You have been rewarded " + toAdd
									+ " Pest Control Points for your valiant effort.");
							int points = p.getDatabaseEntity().getStatistics().getPestControlPoints();
							p.getDatabaseEntity().getStatistics().setPestControlPoints(points + toAdd);
						} else
							p.getActionSender().sendMessage(
									"You have not fought hard enough and proven yourself to earn anything.");

						p.removeAttribute("hits_dealt");
					}
				});
			} else {
				p.getActionSender().removeWalkableInterface();
				p.getActionQueue().clearAllActions();
				p.setTeleportTarget(boat.getExit());
				p.resetVariousInformation();
				if (p.hasAttribute("hits_dealt") && (int) p.getAttribute("hits_dealt") >= 50) {
					int toAdd = 10 * Constants.PEST_MODIFIER;
					p.getActionSender().sendMessage(
							"You have been rewarded " + toAdd + " Pest Control Points for your valiant effort.");
					int points = p.getDatabaseEntity().getStatistics().getPestControlPoints();
					p.getDatabaseEntity().getStatistics().setPestControlPoints(points + toAdd);
				} else
					p.getActionSender()
							.sendMessage("You have not fought hard enough and proven yourself to earn anything.");
				
				p.removeAttribute("hits_dealt");
			}
		});
		getPortals().forEach(i -> i.getNpcs().forEach(PestControlNpc::unregister));
		getPortals().forEach(PestControlPortal::unregister);
		knight.unregister();
		this.destroyed = true;
	}

	public void destroyPlayer(@Nonnull Player p) {

	}

	public boolean isDestroyed() {
		return destroyed;
	}

	public void tick() {
		stream().forEach(a -> {
			a.getActionSender().sendString(408, VoidKnight.CHILD, "200");
			int damage = (a.getAttribute("hits_dealt") == null ? 0 : (int) a.getAttribute("hits_dealt"));
			a.getActionSender().sendString(408, 4, damage > 50 ? ("<col=00FF00>" + damage) : damage + "");
			a.getActionSender().sendString(408, 2,
					TimeUnit.MILLISECONDS.toMinutes((boat.getGameTimeRemaining() * 600)) + " mins");
			getPortals().stream().forEach(p -> a.getActionSender().sendString(408, p.getCardinality().getHealthChild(),
					"" + (p.isDestroyed() ? 0 : p.getSkills().getLevel(3))));
		});
		if (allPortalsDead()) {
			World.getWorld().submit(new StoppingTick(4) {
				@Override
				public void executeAndStop() {
					getBoat().endGame();
				}
			});
		}
	}

	public boolean allPortalsDead() {
		return (bluePortal.isDestroyed() && redPortal.isDestroyed() && yellowPortal.isDestroyed()
				&& purplePortal.isDestroyed());
	}

	public VoidKnight getKnight() {
		return knight;
	}

	public PestControlBoat getBoat() {
		return boat;
	}
}