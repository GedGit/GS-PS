package org.rs2server.rs2.domain.service;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import org.rs2server.rs2.domain.dao.MongoService;
import org.rs2server.rs2.domain.dao.MongoServiceImpl;
import org.rs2server.rs2.domain.service.api.*;
import org.rs2server.rs2.domain.service.api.clojure.ClojureService;
import org.rs2server.rs2.domain.service.api.content.*;
import org.rs2server.rs2.domain.service.api.content.abyss.AbyssService;
import org.rs2server.rs2.domain.service.api.content.bounty.BountyHunterService;
import org.rs2server.rs2.domain.service.api.content.cerberus.CerberusService;
import org.rs2server.rs2.domain.service.api.content.clanwars.ClanWarsService;
import org.rs2server.rs2.domain.service.api.content.duel.DuelArenaService;
import org.rs2server.rs2.domain.service.api.content.gamble.DiceGameService;
import org.rs2server.rs2.domain.service.api.content.logging.ServerLoggingService;
import org.rs2server.rs2.domain.service.api.content.magic.OrbChargingService;
import org.rs2server.rs2.domain.service.api.content.mining.GemMiningService;
import org.rs2server.rs2.domain.service.api.content.trade.TradeService;
import org.rs2server.rs2.domain.service.api.loot.LootGenerationService;
import org.rs2server.rs2.domain.service.api.skill.MiningService;
import org.rs2server.rs2.domain.service.api.skill.RunecraftingService;
import org.rs2server.rs2.domain.service.api.skill.experience.ExperienceDropService;
import org.rs2server.rs2.domain.service.api.skill.farming.FarmingService;
import org.rs2server.rs2.domain.service.impl.*;
import org.rs2server.rs2.domain.service.impl.clojure.ClojureServiceImpl;
import org.rs2server.rs2.domain.service.impl.content.*;
import org.rs2server.rs2.domain.service.impl.content.abyss.AbyssServiceImpl;
import org.rs2server.rs2.domain.service.impl.content.bounty.BountyHunterServiceImpl;
import org.rs2server.rs2.domain.service.impl.content.cerberus.CerberusServiceImpl;
import org.rs2server.rs2.domain.service.impl.content.clanwars.ClanWarsServiceImpl;
import org.rs2server.rs2.domain.service.impl.content.duel.DuelArenaServiceImpl;
import org.rs2server.rs2.domain.service.impl.content.gamble.DiceGameServiceImpl;
import org.rs2server.rs2.domain.service.impl.content.logging.ServerLoggingServiceImpl;
import org.rs2server.rs2.domain.service.impl.content.magic.OrbChargingServiceImpl;
import org.rs2server.rs2.domain.service.impl.content.mining.GemMiningServiceImpl;
import org.rs2server.rs2.domain.service.impl.content.trade.TradeServiceImpl;
import org.rs2server.rs2.domain.service.impl.skill.FarmingServiceImpl;
import org.rs2server.rs2.domain.service.impl.skill.MiningServiceImpl;
import org.rs2server.rs2.domain.service.impl.skill.RunecraftingServiceImpl;
import org.rs2server.rs2.domain.service.impl.skill.experience.ExperienceDropServiceImpl;

/**
 * Module for binding services.
 *
 * @author tommo
 */
public class ServiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(MongoService.class).to(MongoServiceImpl.class).in(Singleton.class);
		bind(AsyncExecutorService.class).to(AsyncExecutorServiceImpl.class).in(Singleton.class);
		bind(ClaimService.class).to(ClaimServiceImpl.class).in(Singleton.class);
		bind(PlayerService.class).to(PlayerServiceImpl.class).in(Singleton.class);
		bind(WorldService.class).to(WorldServiceImpl.class).in(Singleton.class);
		bind(PersistenceService.class).to(PersistenceServiceImpl.class).in(Singleton.class);
		bind(PlayerStatisticsService.class).to(PlayerStatisticsServiceImpl.class).in(Singleton.class);
		bind(EngineService.class).to(EngineServiceImpl.class).in(Singleton.class);
		bind(DeadmanService.class).to(DeadmanServiceImpl.class).in(Singleton.class);
		bind(HookService.class).to(HookServiceImpl.class).in(Singleton.class);
		bind(PathfindingService.class).to(PathfindingServiceImpl.class).in(Singleton.class);
		bind(RegionService.class).to(RegionServiceImpl.class).in(Singleton.class);
		bind(PermissionService.class).to(PermissionServiceImpl.class).in(Singleton.class);
		bind(PlayerVariableService.class).to(PlayerVariableServiceImpl.class).in(Singleton.class);

		bind(SkillsService.class).to(SkillsServiceImpl.class).in(Singleton.class);
		bind(SlayerService.class).to(SlayerServiceImpl.class).in(Singleton.class);
		bind(MiningService.class).to(MiningServiceImpl.class).in(Singleton.class);

		// Content related services which register game hooks in the constructor MUST be
		// bound eagerly.
		bind(DebugService.class).to(DebugServiceImpl.class).asEagerSingleton();
		bind(MotherlodeMineService.class).to(MotherlodeMineServiceImpl.class).asEagerSingleton();
		bind(PestControlService.class).to(PestControlServiceImpl.class).asEagerSingleton();
		bind(BankPinService.class).to(BankPinServiceImpl.class).asEagerSingleton();
		bind(TreasureTrailService.class).to(TreasureTrailServiceImpl.class).asEagerSingleton();
		bind(BankDepositBoxService.class).to(BankDepositBoxServiceImpl.class).asEagerSingleton();
		// bind(RegionMusicService.class).to(RegionMusicServiceImpl.class).asEagerSingleton();
		bind(ResourceArenaService.class).to(ResourceArenaServiceImpl.class).asEagerSingleton();
		bind(FountainOfHeroesService.class).to(FountainOfHeroesServiceImpl.class).asEagerSingleton();
		bind(LootGenerationService.class).to(LootGenerationServiceImpl.class).asEagerSingleton();
		bind(ExperienceDropService.class).to(ExperienceDropServiceImpl.class).asEagerSingleton();
		bind(GrandExchangeService.class).to(GrandExchangeServiceImpl.class).asEagerSingleton();
		bind(PotionDecanterService.class).to(PotionDecanterServiceImpl.class).asEagerSingleton();
		bind(ItemService.class).to(ItemServiceImpl.class).asEagerSingleton();
		bind(KrakenService.class).to(KrakenServiceImpl.class).asEagerSingleton();
		bind(MusicService.class).to(MusicServiceImpl.class).asEagerSingleton();
		bind(MaxCapeService.class).to(MaxCapeServiceImpl.class).asEagerSingleton();
		bind(BountyHunterService.class).to(BountyHunterServiceImpl.class).asEagerSingleton();
		bind(TournamentSuppliesService.class).to(TournamentSuppliesServiceImpl.class).asEagerSingleton();
		bind(ClanWarsService.class).to(ClanWarsServiceImpl.class).asEagerSingleton();
		bind(GroundItemService.class).to(GroundItemServiceImpl.class).asEagerSingleton();
		bind(RunecraftingService.class).to(RunecraftingServiceImpl.class).asEagerSingleton();
		bind(LoggingService.class).to(LoggingServiceImpl.class).asEagerSingleton();

		bind(FarmingService.class).to(FarmingServiceImpl.class).asEagerSingleton();
		bind(ClojureService.class).to(ClojureServiceImpl.class).asEagerSingleton();

		bind(ObeliskService.class).to(ObeliskServiceImpl.class).asEagerSingleton();
		bind(PrivateChatService.class).to(PrivateChatServiceImpl.class).asEagerSingleton();
		bind(StaffService.class).to(StaffServiceImpl.class).asEagerSingleton();
		bind(OrbChargingService.class).to(OrbChargingServiceImpl.class).asEagerSingleton();
		bind(DuelArenaService.class).to(DuelArenaServiceImpl.class).asEagerSingleton();
		bind(TradeService.class).to(TradeServiceImpl.class).asEagerSingleton();
		bind(LootingBagService.class).to(LootingBagServiceImpl.class).asEagerSingleton();
		bind(ServerLoggingService.class).to(ServerLoggingServiceImpl.class).asEagerSingleton();
		bind(AbyssService.class).to(AbyssServiceImpl.class).asEagerSingleton();
		bind(MonsterExamineService.class).to(MonsterExamineServiceImpl.class).asEagerSingleton();
		bind(CoalBagService.class).to(CoalBagServiceImpl.class).asEagerSingleton();
		bind(CerberusService.class).to(CerberusServiceImpl.class).asEagerSingleton();
		bind(GemMiningService.class).to(GemMiningServiceImpl.class).asEagerSingleton();
		bind(GemBagService.class).to(GemBagServiceImpl.class).asEagerSingleton();
		bind(DiceGameService.class).to(DiceGameServiceImpl.class).asEagerSingleton();
		bind(HerbSackService.class).to(HerbSackServiceImpl.class).asEagerSingleton();
	}
}
