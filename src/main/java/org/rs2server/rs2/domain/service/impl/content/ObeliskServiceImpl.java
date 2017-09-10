package org.rs2server.rs2.domain.service.impl.content;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import org.rs2server.rs2.content.api.GameObjectActionEvent;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.domain.service.api.content.ObeliskService;
import org.rs2server.rs2.event.Event;
import org.rs2server.rs2.model.Animation;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.impl.StoppingTick;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

/**
 * @author twelve
 */
public final class ObeliskServiceImpl implements ObeliskService {

	private static final int DEACTIVATED_OBELISK_1 = 14826;
	private static final int DEACTIVATED_OBELISK_2 = 14827;
	private static final int DEACTIVATED_OBELISK_3 = 14828;
	private static final int DEACTIVATED_OBELISK_4 = 14829;
	private static final int DEACTIVATED_OBELISK_5 = 14830;
	private static final int DEACTIVATED_OBELISK_6 = 14831;

	private static final Predicate<GameObject> OBELISK_PREDICATE = (g) -> g.getId() == DEACTIVATED_OBELISK_1
			|| g.getId() == DEACTIVATED_OBELISK_2 || g.getId() == DEACTIVATED_OBELISK_3 ||g.getId() == DEACTIVATED_OBELISK_4
			|| g.getId() == DEACTIVATED_OBELISK_5 || g.getId() == DEACTIVATED_OBELISK_6;

	private static final int ACTIVATED_OBELISK = 14825;
	private static final Animation RESET_ANIMATION = Animation.create(-1);

	@Inject
	ObeliskServiceImpl(HookService service) {
		service.register(this);
	}

	@Override
	@Subscribe
	public void onObeliskObjectClick(@Nonnull GameObjectActionEvent clickEvent) {

		if (clickEvent.getActionType() == GameObjectActionEvent.ActionType.OPTION_1 && OBELISK_PREDICATE.test(clickEvent.getGameObject())) {
			Player player = clickEvent.getPlayer();
			if (player.getDatabaseEntity().getPlayerSettings().isTeleBlocked()) {
				player.getActionSender().sendMessage("A magical force stops you from teleporting.");
				return;
			}
			activateObelisk(player, clickEvent.getGameObject());
		}
	}

	@Override
	public void activateObelisk(@Nonnull Player player, @Nonnull GameObject object) {
		Obelisks.of(object.getLocation()).ifPresent(o -> {
			if (o.isInUse()) {
				return;
			}
			getObeliskLocations(o).stream().map(l -> World.getWorld().getRegionManager().getGameObject(l, OBELISK_PREDICATE))
					.filter(Objects::nonNull)
					.forEach(gameObject -> World.getWorld().replaceObject(gameObject, new GameObject(gameObject.getLocation(), ACTIVATED_OBELISK, 10, 0, false), 10));
			World.getWorld().replaceObject(object, new GameObject(object.getLocation(), ACTIVATED_OBELISK, 10, 0, false), 10);
			player.getActionSender().sendMessage("You activate the obelisk.");
			o.setInUse(true);
			Location centerLocation = o.getLocation().transform(2, 2, 0);
			World.getWorld().submit(new Event(5000) {

				@Override
				public void execute() {
					stop();
					o.setInUse(false);
					List<Obelisks> targets = Arrays.asList(Obelisks.values()).stream().filter(t -> !t.equals(o)).collect(toList());
					Collections.shuffle(targets);

					List<Player> teleporting = player.getRegion().getPlayers().stream().filter(Objects::nonNull).filter(p -> p.getLocation().distance(centerLocation) < 2)
							.filter(p -> !p.getDatabaseEntity().getPlayerSettings().isTeleBlocked()).collect(toList());

					teleporting.forEach(p -> {
						p.playAnimation(Animation.create(1816));
					});

					World.getWorld().submit(new StoppingTick(1) {
						@Override
						public void executeAndStop() {
							teleporting.forEach(p -> {
								for (int x = -1; x <= 1; x++) {
									for (int y = -1; y <= 1; y++) {
										p.getActionSender().sendStillGFX(342, 0, centerLocation.transform(x, y, 0));
									}
								}
								p.setAttribute("busy", true);
								p.getWalkingQueue().reset();
								p.getCombatState().setCanMove(false);
							});
						}
					});

					World.getWorld().submit(new StoppingTick(4) {
						@Override
						public void executeAndStop() {
							Location targetBase = targets.get(0).getLocation();
							teleporting.forEach(p -> {
								p.setTeleportTarget(targetBase.transform(p.getLocation().getX() - o.getLocation().getX(), p.getLocation().getY() - o.getLocation().getY(), 0));
								p.playAnimation(RESET_ANIMATION);
								p.getCombatState().setCanMove(true);
								p.removeAttribute("busy");
								o.setInUse(false);
							});
						}
					});
				}
			});
			World.getWorld().submit(new StoppingTick(14) {
				@Override
				public void executeAndStop() {
					o.setInUse(false);
				}
			});
		});
	}

	@Override
	public Set<Location> getObeliskLocations(Obelisks obelisk) {
		Set<Location> locations = new HashSet<>();

		Location base = obelisk.getLocation();
		for (int x = 0; x <= 4; x += 4) {
			for (int y = 0; y <= 4; y += 4) {
				locations.add(base.transform(x, y, 0));
			}
		}
		return locations;
	}
}