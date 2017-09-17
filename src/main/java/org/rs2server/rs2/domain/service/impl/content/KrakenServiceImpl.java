package org.rs2server.rs2.domain.service.impl.content;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import org.rs2server.rs2.content.api.GameObjectActionEvent;
import org.rs2server.rs2.content.api.GamePlayerLoginEvent;
import org.rs2server.rs2.content.api.GamePlayerLogoutEvent;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.domain.service.api.content.KrakenService;
import org.rs2server.rs2.model.GameObject;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.Animation.FacialAnimation;
import org.rs2server.rs2.model.boundary.BoundaryManager;
import org.rs2server.rs2.model.cm.Content;
import org.rs2server.rs2.model.npc.impl.kraken.Kraken;
import org.rs2server.rs2.model.npc.impl.kraken.TentacleCombatState;
import org.rs2server.rs2.model.npc.impl.kraken.Whirlpool;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.net.ActionSender.DialogueType;
import org.rs2server.rs2.tickable.Tickable;

import javax.annotation.Nonnull;

/**
 * @author Clank1337
 */
public class KrakenServiceImpl implements KrakenService {

	public static final int KRAKEN = 494;
	public static final int TENTACLE = 5535;
	public static final int WHIRPOOL = 5534;
	public static final int WHIRLPOOL_LARGE = 496;

	private static final Location CAVE_SPAWN_LOCATION = Location.create(3696, 5798);
	private static final Location CAVE_ENTRANCE = Location.create(2486, 9797);
	public static final Location SPAWN_LOCATION = Location.create(3695, 5811);

	public static final Location[] TENTACLE_LOCATIONS = new Location[] { Location.create(3692, 5810),
			Location.create(3692, 5814), Location.create(3700, 5810), Location.create(3700, 5814) };

	@Inject
	public KrakenServiceImpl(final HookService hookService) {
		hookService.register(this);
	}

	@Subscribe
	public void onObjectClick(final GameObjectActionEvent clickEvent) {
		if (clickEvent.getActionType() == GameObjectActionEvent.ActionType.OPTION_1) {
			final Player player = clickEvent.getPlayer();
			final GameObject object = clickEvent.getGameObject();
			switch (object.getId()) {
			case 537:
				enterCave(player);
				break;
			case 538:
				exitCave(player);
				break;
			}
		}
	}

	@Subscribe
	public void onPlayerLogin(final GamePlayerLoginEvent event) {
		final Player player = event.getPlayer();
		if (BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "Kraken")) {
			player.getContentManager().start(Content.KRAKEN);
		}
	}

	@Subscribe
	public void onPlayerLogout(final GamePlayerLogoutEvent event) {
		final Player player = event.getPlayer();
		if (BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "Kraken")) {
			Content krakenContent = player.getContentManager().getActiveContent(Content.KRAKEN);
			if (krakenContent != null)
				krakenContent.stop();
		}
	}

	@Override
	public void enterCave(@Nonnull Player player) {
		if (player.getSkills().getLevelForExperience(Skills.SLAYER) < 87) {
			player.getActionSender().sendMessage("You need a Slayer level of 87 to enter this cave.");
			return;
		}
		if (player.getInstancedTimer() > System.currentTimeMillis()) {
			int seconds = (int) ((player.getInstancedTimer() - System.currentTimeMillis()) / 1000);
			player.getActionSender().sendDialogue("", DialogueType.MESSAGE, 0, FacialAnimation.DEFAULT,
					"You must wait another "+seconds+" seconds before entering the crevice again.");
			return;
		}
		player.setAttribute("busy", true);
		player.setTeleportTarget(CAVE_SPAWN_LOCATION);
		player.getContentManager().start(Content.KRAKEN);

		World.getWorld().submit(new Tickable(2) {

			@Override
			public void execute() {
				player.removeAttribute("busy");
				player.getActionQueue().clearAllActions();
				this.stop();
			}
		});
	}

	@Override
	public void exitCave(@Nonnull Player player) {
		player.setAttribute("busy", true);
		player.setTeleportTarget(CAVE_ENTRANCE);
		player.sendMessage("You will be logged out in 2 seconds!");
		World.getWorld().submit(new Tickable(3) {

			@Override
			public void execute() {
				player.removeAttribute("busy");
				player.getActionQueue().clearAllActions();
				Content krakenContent = player.getContentManager().getActiveContent(Content.KRAKEN);
				if (krakenContent != null)
					krakenContent.stop();
				player.setInstancedTimer(System.currentTimeMillis() + 15000);
				player.getActionSender().sendLogout();
				this.stop();
			}
		});
	}

	@Override
	public void addKraken(@Nonnull Player player, Kraken kraken) {
		player.addInstancedNpc(kraken);
		World.getWorld().getNPCs().add(kraken);
		kraken.setTeleporting(false);
		kraken.setLocation(SPAWN_LOCATION);
	}

	@Override
	public void destroyKraken(@Nonnull Player player) {
		player.getInstancedNPCs().forEach(World.getWorld()::unregister);
		player.getInstancedNPCs().clear();
	}

	@Override
	public void disturbWhirlpool(@Nonnull Player player, @Nonnull Whirlpool whirlpool) {
		whirlpool.transformNPC(TENTACLE);
		whirlpool.getKraken().getDisturbedWhirlpools().add(whirlpool);
		whirlpool.transition(new TentacleCombatState<>(whirlpool));

		if (whirlpool.getKraken().getDisturbedWhirlpools().size() == 4)
			whirlpool.getKraken().setAttackable(true);
	}
}
