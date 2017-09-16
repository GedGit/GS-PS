package org.rs2server.rs2.domain.service.impl.content.cerberus;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import org.rs2server.rs2.action.impl.CrawlingAction;
import org.rs2server.rs2.content.api.GameObjectActionEvent;
import org.rs2server.rs2.content.api.GamePlayerLoginEvent;
import org.rs2server.rs2.content.api.GamePlayerLogoutEvent;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.domain.service.api.content.cerberus.CerberusService;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.boundary.BoundaryManager;
import org.rs2server.rs2.model.cm.Content;
import org.rs2server.rs2.model.cm.impl.CerberusContent;
import org.rs2server.rs2.model.npc.impl.cerberus.Cerberus;
import org.rs2server.rs2.model.npc.impl.cerberus.ghosts.CerberusGhost;
import org.rs2server.rs2.model.npc.impl.cerberus.ghosts.impl.MagicCerberusGhost;
import org.rs2server.rs2.model.npc.impl.cerberus.ghosts.impl.MeleeCerberusGhost;
import org.rs2server.rs2.model.npc.impl.cerberus.ghosts.impl.RangedCerberusGhost;
import org.rs2server.rs2.model.player.Player;
import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Twelve
 */
public final class CerberusServiceImpl implements CerberusService {
	private static final ImmutableList<Location> GHOST_SPAWN_LOCATIONS = ImmutableList.of(Location.create(1239, 1266),
			Location.create(1240, 1266), Location.create(1241, 1266));

	private static final Location SPAWN_LOCATION = Location.create(1240, 1226, 0);
	private static final Location TUNNEL_LOCATION_EXIT = Location.create(2873, 9847, 0);

	@Inject
	CerberusServiceImpl(HookService service) {
		service.register(this);
	}

	@Subscribe
	public void onObjectClick(final GameObjectActionEvent clickEvent) {
		if (clickEvent.getActionType() == GameObjectActionEvent.ActionType.OPTION_1) {
			final Player player = clickEvent.getPlayer();
			final GameObject object = clickEvent.getGameObject();
			switch (object.getId()) {
			case 26567:
			case 26568:
			case 26569:
				player.setAttribute("busy", true);
				enterCave(player);
				break;
			case 21772:
				exitCave(player);
				break;
			}
		}
	}

	@Override
	public ImmutableList<CerberusGhost> getRandomGhostOrder() {
		List<Integer> locationOrder = Arrays.asList(0, 1, 2);
		Collections.shuffle(locationOrder);

		CerberusGhost meleeCerberusGhost = new MeleeCerberusGhost(GHOST_SPAWN_LOCATIONS.get(locationOrder.get(0)));
		CerberusGhost rangedCerberusGhost = new RangedCerberusGhost(GHOST_SPAWN_LOCATIONS.get(locationOrder.get(1)));
		CerberusGhost magicCerberusGhost = new MagicCerberusGhost(GHOST_SPAWN_LOCATIONS.get(locationOrder.get(2)));

		List<CerberusGhost> ghosts = Arrays.asList(meleeCerberusGhost, rangedCerberusGhost, magicCerberusGhost);

		Collections.sort(ghosts, (o1, o2) -> {
			int firstX = o1.getLocation().getX();
			int secondX = o2.getLocation().getX();
			return firstX > secondX ? 1 : firstX == secondX ? 0 : -1;
		});
		return ImmutableList.copyOf(ghosts);
	}

	@Override
	public void enterCave(@Nonnull Player player) {
		if (player.getSkills().getLevelForExperience(Skills.SLAYER) < 91) {
			player.getActionSender().sendMessage("You need a Slayer level of 91 to enter this cave.");
			return;
		}
		player.getActionQueue().addAction(new CrawlingAction(player, SPAWN_LOCATION));
		player.getContentManager().start(Content.CERBERUS);
	}

	@Override
	public void exitCave(@Nonnull Player player) {
		player.setTeleportTarget(TUNNEL_LOCATION_EXIT);
		Content cerberusContent = player.getContentManager().getActiveContent(Content.CERBERUS);
		if (cerberusContent != null) {
			cerberusContent.stop();
		}
	}

	@Subscribe
	public void onPlayerLogin(final GamePlayerLoginEvent event) {
		final Player player = event.getPlayer();
		if (BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "Cerberus")) {
			player.setTeleportTarget(Location.create(2873, 9847, 0));
		}

	}

	@Subscribe
	public void onPlayerLogout(final GamePlayerLogoutEvent event) {
		final Player player = event.getPlayer();
		if (BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "Cerberus")) {
			Content cerberusContent = player.getContentManager().getActiveContent(Content.CERBERUS);
			if (cerberusContent != null) {
				cerberusContent.stop();
			}
		}
	}

	@Override
	public void addCerberus(Player player, Cerberus cerberus) {
		cerberus.setGhosts(getRandomGhostOrder());
		player.addInstancedNpc(cerberus);
		World.getWorld().getNPCs().add(cerberus);
		cerberus.setTeleporting(false);
		cerberus.setLocation(CerberusContent.SPAWN_LOCATION);
		player.resetFace();
	}
}
