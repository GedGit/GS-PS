package org.rs2server.rs2.domain.service.impl;

import com.google.inject.Inject;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.domain.dao.api.PlayerEntityDao;
import org.rs2server.rs2.domain.model.player.*;
import org.rs2server.rs2.domain.service.api.PermissionService;
import org.rs2server.rs2.domain.service.api.PersistenceService;
import org.rs2server.rs2.domain.service.api.content.PrivateChatService;
import org.rs2server.rs2.model.Entity;
import org.rs2server.rs2.model.Location;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.util.Helpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * @author tommo
 */
public class PersistenceServiceImpl implements PersistenceService {

	private static final Logger logger = LoggerFactory.getLogger(PersistenceServiceImpl.class);

	private final PlayerEntityDao playerEntityDao;
	private final PermissionService permissionService;

	@Inject
	PersistenceServiceImpl(final PlayerEntityDao playerEntityDao, final PermissionService permissionService) {
		this.playerEntityDao = playerEntityDao;
		this.permissionService = permissionService;
	}

	@Nonnull
	@Override
	public PlayerEntity getOrCreatePlayer(@Nonnull Player player) {
		PlayerEntity entity = getPlayerByAccountName(player.getName());
		if (entity == null)
			entity = createPlayer(player);
		player.setDatabaseEntity(entity);

		return entity;
	}

	@Nonnull
	@Override
	public PlayerEntity createPlayer(@Nonnull Player player) {
		final PlayerEntity playerEntity = new PlayerEntity();
		playerEntity.setAccountName(player.getName());
		playerEntity.setDisplayName(player.getName());
		playerEntity.setPreviousDisplayName(null);

		playerEntity.setLocationX(player.getLocation().getX());
		playerEntity.setLocationY(player.getLocation().getY());
		playerEntity.setLocationZ(player.getLocation().getPlane());

		playerEntity.setPermissions(
				EnumSet.of(PermissionService.PlayerPermissions.PLAYER, PermissionService.PlayerPermissions.PLAYER));

		return playerEntityDao.save(playerEntity);
	}

	@Nullable
	@Override
	public PlayerEntity getPlayerByAccountName(@Nonnull String accountName) {
		return playerEntityDao.findByAccountName(accountName);
	}

	@Nullable
	@Override
	public PlayerEntity getPlayerByDisplayName(@Nonnull String displayName) {
		return playerEntityDao.findByDisplayName(displayName);
	}

	@Override
	public PlayerEntity getPlayerById(@Nonnull final String id) {
		Objects.requireNonNull(id, "id");
		return playerEntityDao.find(id);
	}

	@Override
	public PlayerEntity savePlayer(@Nonnull final Player player) {
		Objects.requireNonNull(player, "player");

		final PlayerEntity entity = player.getDatabaseEntity();
		if (entity == null) {
			logger.error("Player {} has no database entity!", player.getName());
			return null;
		}

		entity.setDisplayName(player.getName());
		entity.setPreviousDisplayName(player.getPreviousName());

		entity.setLocationX(player.getLocation().getX());
		entity.setLocationY(player.getLocation().getY());
		entity.setLocationZ(player.getLocation().getPlane());

		playerEntityDao.save(entity);
		if (Constants.DEBUG)
			logger.info("Saved player {} with display name {}", entity.getAccountName(), entity.getDisplayName());
		return entity;
	}

	@Override
	public void initialisePlayer(@Nonnull Player player) {
		final PlayerEntity playerEntity = player.getDatabaseEntity();
		if (playerEntity == null) {
			logger.error("Cannot initialise player {}. No database entity exists!", player.getName());
			return;
		}

		// -------------- Backwards compatibility
		if (playerEntity.getPermissions() == null)
			playerEntity.setPermissions(
					EnumSet.of(PermissionService.PlayerPermissions.PLAYER, PermissionService.PlayerPermissions.PLAYER));
		if (playerEntity.getRunePouches() == null)
			playerEntity.setRunePouches(new HashMap<>());
		if (playerEntity.getGemBag() == null)
			playerEntity.setGemBag(new HashMap<>());
		if (playerEntity.getHerbSack() == null)
			playerEntity.setHerbSack(new HashMap<>());
		if (playerEntity.getCombatEntity() == null)
			playerEntity.setCombatEntity(new PlayerCombatEntity());
		if (playerEntity.getAchievements() == null)
			playerEntity.setAchievementEntity(new PlayerAchievementEntity());

		if (player.isIronMan() && !permissionService.is(player, PermissionService.PlayerPermissions.IRON_MAN))
			permissionService.give(player, PermissionService.PlayerPermissions.IRON_MAN);
		if (player.isUltimateIronMan()
				&& !permissionService.is(player, PermissionService.PlayerPermissions.ULTIMATE_IRON_MAN))
			permissionService.give(player, PermissionService.PlayerPermissions.ULTIMATE_IRON_MAN);
		if (player.isHardcoreIronMan()
				&& !permissionService.is(player, PermissionService.PlayerPermissions.HARDCORE_IRON_MAN))
			permissionService.give(player, PermissionService.PlayerPermissions.HARDCORE_IRON_MAN);

		PlayerStatisticsEntity statistics = Helpers.fallback(playerEntity.getStatistics(),
				new PlayerStatisticsEntity());
		if (statistics.getBossKillCount() == null)
			statistics.setBossKillCount(new HashMap<>());
		if (statistics.getSlayerMonsterKillCount() == null)
			statistics.setSlayerMonsterKillCount(new HashMap<>());
		if (statistics.getBossKillTimes() == null)
			statistics.setBossKillTimes(new HashMap<>());
		if (statistics.getTreasureTrailCount() == null)
			statistics.setTreasureTrailCount(new HashMap<>());
		playerEntity.setStatistics(statistics);

		PlayerPrivateChatEntity playerPrivateChatEntity = Helpers.fallback(playerEntity.getPrivateChat(),
				new PlayerPrivateChatEntity());
		if (playerPrivateChatEntity.getFriendsList() == null)
			playerPrivateChatEntity.setFriendsList(new HashSet<>());
		if (playerPrivateChatEntity.getIgnoreList() == null)
			playerPrivateChatEntity.setIgnoreList(new HashSet<>());
		if (playerPrivateChatEntity.getStatus() == null)
			playerPrivateChatEntity.setStatus(PrivateChatService.PrivateChatStatus.ON);
		playerEntity.setPrivateChat(playerPrivateChatEntity);

		PlayerSkillsEntity skills = Helpers.fallback(playerEntity.getSkills(), new PlayerSkillsEntity());
		if (skills.getLevels() == null)
			skills.setLevels(new HashMap<>());
		if (skills.getExperiences() == null)
			skills.setExperiences(new HashMap<>());
		playerEntity.setSkills(skills);

		PlayerSkillSlayerEntity slayer = Helpers.fallback(playerEntity.getSlayerSkill(), new PlayerSkillSlayerEntity());
		playerEntity.setSlayerSkill(slayer);

		PlayerSkillFarmingEntity farming = Helpers.fallback(playerEntity.getFarmingSkill(),
				new PlayerSkillFarmingEntity());
		playerEntity.setFarmingSkill(farming);

		PlayerDeadmanStateEntity deadman = Helpers.fallback(playerEntity.getDeadmanState(),
				new PlayerDeadmanStateEntity());
		if (deadman.getProtectedCombatSkills() == null)
			deadman.setProtectedCombatSkills(new HashSet<>());
		if (deadman.getProtectedOtherSkills() == null)
			deadman.setProtectedOtherSkills(new HashSet<>());
		playerEntity.setDeadmanState(deadman);

		PlayerZulrahStateEntity zulrah = Helpers.fallback(playerEntity.getZulrahState(), new PlayerZulrahStateEntity());
		if (zulrah.getItemsLostZulrah() == null)
			zulrah.setItemsLostZulrah(new ArrayList<>());
		playerEntity.setZulrahState(zulrah);

		PlayerSettingsEntity settings = Helpers.fallback(playerEntity.getPlayerSettings(), new PlayerSettingsEntity());
		if (settings.getPlayerScreenBrightness() == 0)
			settings.setPlayerScreenBrightness(2);
		if (settings.getPlayerVariables() == null)
			settings.setPlayerVariables(new HashMap<>());
		if (settings.getLockedSkills() == null)
			settings.setLockedSkills(new ArrayList<>());
		playerEntity.setPlayerSettings(settings);

		PlayerBankEntity bank = Helpers.fallback(playerEntity.getBank(), new PlayerBankEntity());
		playerEntity.setBank(bank);

		PlayerEquipmentEntity equipment = Helpers.fallback(playerEntity.getEquipment(), new PlayerEquipmentEntity());
		if (equipment.getItemCharges() == null)
			equipment.setItemCharges(new HashMap<>());
		if (equipment.getItemChargedWith() == null)
			equipment.setItemChargedWith(new HashMap<>());
		playerEntity.setEquipment(equipment);

		PlayerBountyHunterEntity bountyHunter = Helpers.fallback(playerEntity.getBountyHunter(),
				new PlayerBountyHunterEntity());
		playerEntity.setBountyHunter(bountyHunter);

		// Workaround for a horrible bug
		boolean originallyActive = player.isActive();
		if (originallyActive)
			player.setActive(false);
		if (playerEntity.getLocationX() != 0 && playerEntity.getLocationY() != 0) {
			player.setLocation(Location.create(playerEntity.getLocationX(), playerEntity.getLocationY(),
					playerEntity.getLocationZ()));
		} else {
			player.setLocation(Entity.DEFAULT_LOCATION);
			player.sendMessage("You have been moved home.");
		}
		player.setActive(originallyActive);
		// -----------Backwards compatibility

		savePlayer(player);
	}
}