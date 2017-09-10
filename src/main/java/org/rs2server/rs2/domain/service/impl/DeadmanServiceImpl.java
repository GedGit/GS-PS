package org.rs2server.rs2.domain.service.impl;

import com.google.common.collect.ImmutableList;
import org.rs2server.rs2.domain.service.api.DeadmanService;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.Skill;
import org.rs2server.rs2.model.boundary.Boundary;
import org.rs2server.rs2.model.boundary.BoundaryManager;
import org.rs2server.rs2.model.player.Player;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author tommo
 */
public class DeadmanServiceImpl implements DeadmanService {

	private static final List<Boundary> SAFE_ZONES = ImmutableList.of(
			Boundary.create("Falador west bank", Location.create(2943, 3368), Location.create(2947, 3373)),
			Boundary.create("Falador west bank", Location.create(2948, 3368), Location.create(2949, 3368)),
			Boundary.create("Varrock west bank", Location.create(3190, 3447), Location.create(3180, 3433))
	);

	@Override
	public boolean canAttack(@Nonnull Player attacker, @Nonnull Player victim) {
		return !inSafeZone(victim);
	}

	@Override
	public boolean inSafeZone(@Nonnull Player player) {
		final Location l = player.getLocation();

		for (final Boundary boundary : SAFE_ZONES) {
			if (BoundaryManager.isWithinBoundary(l, boundary)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void reducePlayerSkills(@Nonnull Player player) {
		for (final Skill skill : Skill.values()) {
			if (player.getDatabaseEntity().getDeadmanState().getProtectedCombatSkills().contains(skill)
					|| player.getDatabaseEntity().getDeadmanState().getProtectedOtherSkills().contains(skill)) {
				continue;
			}

			player.getSkills().setExperience(skill.getId(), player.getSkills().getExperience(skill.getId())/2.5);
		}
	}

	@Override
	public void onPlayerKill(@Nonnull Player killer, @Nonnull Player victim) {
		reducePlayerSkills(victim);

	}

	/*
	[23:00:50] Shawn: interface config on (90, 23): hidden=false
interface config on (90, 25): hidden=false
[23:00:58] Shawn: guarded, 3-126
[23:01:39] Shawn: interface config on (90, 23): hidden=true
interface config on (90, 25): hidden=false
[23:03:28] Shawn: Welcome to Deadman mode. On these worlds, you die.

	 */
}
