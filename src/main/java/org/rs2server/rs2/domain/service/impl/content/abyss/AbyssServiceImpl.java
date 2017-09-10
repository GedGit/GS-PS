package org.rs2server.rs2.domain.service.impl.content.abyss;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import org.rs2server.rs2.content.api.GameNpcActionEvent;
import org.rs2server.rs2.content.api.GameObjectActionEvent;
import org.rs2server.rs2.domain.service.api.HookService;
import org.rs2server.rs2.domain.service.api.content.abyss.AbyssService;
import org.rs2server.rs2.model.*;
import org.rs2server.rs2.model.npc.NPC;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.tickable.impl.StoppingTick;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author Clank1337
 */
public class AbyssServiceImpl implements AbyssService {

	private enum RIFTS {

		NATURE(24975, Location.create(2400, 4835)),

		COSMIC(24974, Location.create(2162, 4833)),

		FIRE(24971, Location.create(2576, 4849)),

		EARTH(24972, Location.create(2657, 4830)),

		BODY(24973, Location.create(2521, 4835)),

		MIND(25379, Location.create(2793, 4829)),

		AIR(25378, Location.create(2841, 4830)),

		WATER(25376, Location.create(2725, 4833)),

		LAW(25034, Location.create(2464, 4818)),

		DEATH(25035, Location.create(2208, 4830)),

		SOUL(25377, Location.create(1820, 3861)),

		BLOOD(25380, Location.create(1734, 3828)),

		CHAOS(24976, Location.create(2281, 4837));

		private final int id;
		private final Location location;

		RIFTS(int id, Location location) {
			this.id = id;
			this.location = location;
		}

		public static Optional<RIFTS> of(int id) {
			return Arrays.stream(RIFTS.values()).filter(i -> i.getId() == id).findAny();
		}

		public int getId() {
			return id;
		}

		public Location getLocation() {
			return location;
		}
	}

	private static final Location ENTER_LOCATION = Location.create(3039, 4834, 0);
	private static final int MAGE_OF_ZAMORAK = 2581;
	private static final int SKULL_TICKS = 1000;

	@Inject
	AbyssServiceImpl(HookService hookService) {
		hookService.register(this);
	}

	@Override
	public void enterAbyss(@Nonnull Player player) {
		player.teleport(ENTER_LOCATION, 1, 1, true);
		drainPlayer(player);
	}

	@Override
	public void drainPlayer(@Nonnull Player player) {
		player.getSkills().decreasePrayerPoints(99);
		if (player.getCombatState().getSkullTicks() <= 0) {
			player.getCombatState().setSkullTicks(SKULL_TICKS);
		}
	}

	@Subscribe
	public void onObjectClick(GameObjectActionEvent event) {
		Player player = event.getPlayer();
		GameObject object = event.getGameObject();
		if (player == null || object == null) {
			return;
		}
		Optional<RIFTS> riftOptional = RIFTS.of(object.getId());
		if (riftOptional.isPresent()) {
			RIFTS rift = riftOptional.get();
			player.setTeleportTarget(rift.getLocation());
		}
	}

	@SuppressWarnings("incomplete-switch")
	@Subscribe
	public void onNpcClick(final GameNpcActionEvent event) {
		if (event.getNpc().getId() != MAGE_OF_ZAMORAK)
			return;
		Player player = event.getPlayer();
		NPC npc = event.getNpc();
		if (player != null) {
			switch (event.getActionType()) {
			case OPTION_2:
				npc.setInteractingEntity(Mob.InteractionMode.TALK, player);
				npc.playAnimation(Animation.create(722));
				npc.playGraphics(Graphic.create(343, 0, 0));
				npc.forceChat("Veniens! Sallakar! Rinnesset!");
				player.setAttribute("busy", true);
				World.getWorld().submit(new StoppingTick(2) {
					@Override
					public void executeAndStop() {
						player.removeAttribute("busy");
						enterAbyss(player);
						npc.resetInteractingEntity();
						player.resetInteractingEntity();
					}
				});
				break;
			}
		}
	}
}