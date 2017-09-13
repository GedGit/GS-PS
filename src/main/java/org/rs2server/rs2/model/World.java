package org.rs2server.rs2.model;

import com.diffplug.common.base.Errors;
import com.google.inject.Inject;

import org.apache.mina.core.buffer.IoBuffer;
import org.rs2server.Server;
import org.rs2server.cache.*;
import org.rs2server.cache.format.*;
import org.rs2server.http.WorldList;
import org.rs2server.rs2.*;
import org.rs2server.rs2.WorldLoader;
import org.rs2server.rs2.WorldLoader.LoginResult;
import org.rs2server.rs2.content.StarterMap;
import org.rs2server.rs2.content.api.*;
import org.rs2server.rs2.content.areas.CoordinateEvent;
import org.rs2server.rs2.domain.model.player.PlayerEntity;
import org.rs2server.rs2.domain.service.api.*;
import org.rs2server.rs2.domain.service.impl.content.ResourceArenaServiceImpl;
import org.rs2server.rs2.event.*;
import org.rs2server.rs2.model.CombatNPCDefinition.Skill;
import org.rs2server.rs2.model.boundary.BoundaryManager;
import org.rs2server.rs2.model.combat.impl.MagicCombatAction;
import org.rs2server.rs2.model.container.Inventory;
import org.rs2server.rs2.model.container.PriceChecker;
import org.rs2server.rs2.model.equipment.EquipmentDefinition;
import org.rs2server.rs2.model.event.ClickEventManager;
import org.rs2server.rs2.model.map.*;
import org.rs2server.rs2.model.map.path.PathFinder;
import org.rs2server.rs2.model.map.path.TilePath;
import org.rs2server.rs2.model.minigame.impl.FightPits;
import org.rs2server.rs2.model.npc.*;
import org.rs2server.rs2.model.player.*;
import org.rs2server.rs2.model.player.PrivateChat.*;
import org.rs2server.rs2.model.region.*;
import org.rs2server.rs2.mysql.impl.*;
import org.rs2server.rs2.net.*;
import org.rs2server.rs2.packet.PacketHandler;
import org.rs2server.rs2.task.impl.SessionLoginTask;
import org.rs2server.rs2.tickable.*;
import org.rs2server.rs2.tickable.impl.DoubleEXPTick;
import org.rs2server.rs2.tickable.impl.LoyaltyPointTick;
import org.rs2server.rs2.util.*;
import org.rs2server.rs2.varp.PlayerVariables;
import org.rs2server.util.*;
import org.slf4j.*;

import java.io.*;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.*;

/**
 * Holds data global to the game world.
 *
 * @author Graham Edgecombe
 */
public class World {

	/**
	 * Logging class.
	 */
	private static final Logger logger = LoggerFactory.getLogger(World.class);

	/**
	 * A boolean representing if the world is ready to be logged into.
	 */
	public boolean IS_READY = false;

	/**
	 * A boolean representing whether there's an ongoing system update.
	 */
	public static boolean SYSTEM_UPDATE;
	
	/**
	 * An int representing the time until world shutdown.
	 */
	public static int UPDATE_TIMER;

	/**
	 * Gets the world instance.
	 *
	 * @return The world instance.
	 */
	public static World getWorld() {
		return Server.getInjector().getInstance(World.class);
	}

	/**
	 * An executor service which handles background loading tasks.
	 */
	private BlockingExecutorService backgroundLoader = new BlockingExecutorService(Executors.newSingleThreadExecutor());

	/**
	 * The game engine.
	 */
	private EngineService engineService;

	/**
	 * The event manager.
	 */
	private EventManager eventManager;

	/**
	 * The tickable manager.
	 */
	private TickableManager tickManager;

	/**
	 * The current loader implementation.
	 */
	private WorldLoader loader;

	/**
	 * A list of connected players.
	 */
	private EntityList<Player> players = new EntityList<>(Constants.MAX_PLAYERS);

	/**
	 * A map of connected player names
	 */
	private Map<Long, Player> playerNames = new HashMap<>();

	/**
	 * A list of active NPCs.
	 */
	private EntityList<NPC> npcs = new EntityList<>(Constants.MAX_NPCS);

	/**
	 * The region manager.
	 */
	private RegionManager regionManager = new RegionManager();

	/**
	 * A map of all private chats.
	 */
	private Map<String, PrivateChat> privateChat = new HashMap<String, PrivateChat>();

	/**
	 * The global fight pits instance.
	 */
	private FightPits fightPits;

	/**
	 * The initial events to be submitted for execution.
	 */
	private final Set<Event> events;

	/**
	 * The initial tickables to be submitted for execution.
	 */
	private final Set<Tickable> tickables;

	private final PersistenceService persistenceService;
	private final HookService hookService;

	/**
	 * The world type.
	 */
	private WorldType type = WorldType.STANDARD;// STANDARD DEADMAN_MODE

	@Inject
	public World(final Set<Event> events, final Set<Tickable> tickables, final PersistenceService persistenceService,
			final HookService hookService) {
		this.events = events;
		this.tickables = tickables;
		this.persistenceService = persistenceService;
		this.hookService = hookService;

		backgroundLoader.submit(() -> Errors.log().run(() -> {
			logger.info("Loading cache...");
			org.rs2server.cache.Cache.init();
			logger.info("Loaded cache.");
		}));

		backgroundLoader.submit(new Callable<Object>() {
			@Override
			public Object call() throws Exception {

				int size = Cache.getAmountOfObjects();
				logger.info("Loading object definitions...");
				for (int i = 0; i < size; i++) {
					CacheObjectDefinition.forID(i);
				}
				logger.info("Loaded " + size + " object definitions!");
				size = Cache.getAmountOfNpcs();
				logger.info("Loading npc definitions...");
				for (int i = 0; i < size; i++) {
					org.rs2server.cache.format.CacheNPCDefinition.get(i);
				}
				logger.info("Loaded " + size + " npc definitions!");
				NPCLootTable.load();
				NPCLootTable.loadExamines();
				logger.info("Loading item definitions...");
				size = Cache.getAmountOfItems();
				for (int i = 0; i < size; i++) {
					org.rs2server.cache.format.CacheItemDefinition.get(i);
				}
				logger.info("Loaded " + size + " item definitions!");
				// new ItemDefinitionGenerator(13377, 13381);

				Map<Integer, CacheObjectDefinition> objectVarps = new HashMap<>();
				Map<Integer, CacheNPCDefinition> npcVarps = new HashMap<>();
				for (int i = 0; i < CacheManager.getRealContainerChildCount(2, 6); i++) {
					CacheObjectDefinition def = CacheObjectDefinition.forID(i);

					if (def.playerVariable != -1) {
						objectVarps.put(def.playerVariable, def);
					}
				}

				for (int i = 0; i < CacheManager.getRealContainerChildCount(2, 14); i++)
					PlayerVariables.compositeOf(i);

				for (int i = 0; i < CacheManager.getRealContainerChildCount(2, 9); i++) {
					CacheNPCDefinition def = CacheNPCDefinition.get(i);
					if (def != null && def.playerVariable != -1) {
						npcVarps.put(def.playerVariable, def);
					}
				}
				StringBuilder sb = new StringBuilder();

				PlayerVariableComposite.CONFIG_VARIABLE_MAP.keySet().forEach(k -> {
					sb.append("config ").append(k).append("  ->\n");
					PlayerVariableComposite.CONFIG_VARIABLE_MAP.get(k).forEach(v -> {
						sb.append("\t(variable ").append(v.getId());
						if (objectVarps.containsKey(v.getId())) {
							CacheObjectDefinition def = objectVarps.get(v.getId());
							sb.append(" \n\t\t(object ").append(def.getId()).append(" ")
									.append(Arrays.toString(def.anIntArray3746));
							for (int x : def.anIntArray3746) {
								sb.append("\n\t\t\t\t\t");
								if (x == -1) {
									sb.append("null");
								} else {
									if (x >= CacheManager.getRealContainerChildCount(2, 6)) {
										sb.append("" + x);
									} else {
										CacheObjectDefinition child = CacheObjectDefinition.forID(x);
										if (child.getName() == null) {
											sb.append("null");
										} else {
											sb.append(child.getName());
										}
									}
								}
							}
							sb.append(")\n ");
						}

						if (npcVarps.containsKey(v.getId())) {
							CacheNPCDefinition def = npcVarps.get(v.getId());

							if (!objectVarps.containsKey(v.getId())) {
								sb.append("\n\t");
							} else {
								sb.append("\t\t");
							}
							sb.append(" \t(npc ").append(def.getId()).append(" ")
									.append(Arrays.toString(def.anIntArray2185)).append(")");
							for (int x : def.anIntArray2185) {
								sb.append("\n\t\t\t\t\t");
								if (x == -1) {
									sb.append("null");
								} else {
									CacheNPCDefinition child = CacheNPCDefinition.get(x);
									if (child != null) {
										if (child.getName() == null) {
											sb.append("null");
										} else {
											sb.append(child.getName());
										}
									}
								}
							}
						}
						sb.append("\t -> [").append(v.getLeastSignificant()).append(" ... ")
								.append(v.getMostSignificant()).append("])\n");
					});
					sb.append("end").append("\n");
				});

				/*
				 * for (int i = 0; i < CacheManager.getFileStore(12).length(); i++) {
				 * System.out.println("script " + i + " -> "); try { ExpressionStack stack =
				 * ExpressionStack.of(new ClientScriptComposite(i).getInstructions()).print();
				 * // System.exit(0); } catch (Exception e) { e.printStackTrace(); //
				 * System.exit(0); continue; } System.out.println("end"); }
				 */
				// System.out.println(sb.toString());
				return null;
			}
		});

		backgroundLoader.submit(() -> {
			ClickEventManager.getEventManager().load();
			return null;
		});

		backgroundLoader.submit(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				logger.info("Loading XTEA...");
				MapXTEA.init();
				loadExtraFlags();
				return null;
			}
		});
		backgroundLoader.submit(() -> {// dont enable that xd
			ItemDefinition.init();
			ItemSpawn.init();
			return null;
		});

		backgroundLoader.submit(() -> {
			EquipmentDefinition.init();
			return null;
		});

		backgroundLoader.submit(() -> {
			NPCDefinition.init();
			CombatNPCDefinition.init();
			NPCSpawnLoader.init();
			return null;
		});

		backgroundLoader.submit(() -> {
			Shop.init();
			return null;
		});

		backgroundLoader.submit(() -> {
			BoundaryManager.init();
			ObjectManager mngr = new ObjectManager();
			mngr.load();
			// RegionMusicServiceImpl.load();
			// vote.start();
			return null;
		});
		StarterMap.getSingleton().init();
	}

	/**
	 * Gets the background loader.
	 *
	 * @return The background loader.
	 */
	public BlockingExecutorService getBackgroundLoader() {
		return backgroundLoader;
	}

	/**
	 * Gets the region manager.
	 *
	 * @return The region manager.
	 */
	public RegionManager getRegionManager() {
		return regionManager;
	}

	/**
	 * @return the privateChat
	 */
	public Map<String, PrivateChat> getPrivateChat() {
		return privateChat;
	}

	public FightPits getFightPits() {
		return fightPits;
	}

	/**
	 * Gets the tickable manager.
	 *
	 * @return The tickable manager.
	 */
	public TickableManager getTickableManager() {
		return tickManager;
	}

	/**
	 * Initialises the world: loading configuration and registering global events.
	 *
	 * @throws IOException
	 *             if an I/O error occurs loading configuration.
	 * @throws ClassNotFoundException
	 *             if a class loaded through reflection was not found.
	 * @throws IllegalAccessException
	 *             if a class could not be accessed.
	 * @throws InstantiationException
	 *             if a class could not be created.
	 * @throws IllegalStateException
	 *             if the world is already initialised.
	 */
	public void init() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (this.engineService != null)
			throw new IllegalStateException("The world has already been initialised.");
		else {
			this.engineService = Server.getInjector().getInstance(EngineService.class);
			this.eventManager = new EventManager();
			this.tickManager = new TickableManager();

			// Submit events and tickables to their respective managers
			events.forEach(this::submit);
			tickables.forEach(this::submit);

			this.loadConfiguration();
		}
	}

	/**
	 * Loads server configuration.
	 *
	 * @throws IOException
	 *             if an I/O error occurs.
	 * @throws ClassNotFoundException
	 *             if a class loaded through reflection was not found.
	 * @throws IllegalAccessException
	 *             if a class could not be accessed.
	 * @throws InstantiationException
	 *             if a class could not be created.
	 */
	private void loadConfiguration()
			throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		FileInputStream fis = new FileInputStream("data/configuration.cfg");
		try {
			ConfigurationParser p = new ConfigurationParser(fis);
			this.loader = new GenericWorldLoader();
			Map<String, Map<String, String>> complexMappings = p.getComplexMappings();
			if (complexMappings.containsKey("packetHandlers")) {
				Map<Class<?>, Object> loadedHandlers = new HashMap<Class<?>, Object>();
				for (Map.Entry<String, String> handler : complexMappings.get("packetHandlers").entrySet()) {
					int id = Integer.parseInt(handler.getKey());
					Class<?> handlerClass = Class.forName(handler.getValue());
					Object handlerInstance;
					if (loadedHandlers.containsKey(handlerClass)) {
						handlerInstance = loadedHandlers.get(loadedHandlers.get(handlerClass));
					} else {
						handlerInstance = handlerClass.newInstance();
					}
					PacketManager.getPacketManager().bind(id, (PacketHandler) handlerInstance);
					logger.debug("Bound " + handler.getValue() + " to opcode : " + id);
				}
			}
		} finally {
			fis.close();
		}
	}

	/**
	 * Submits a new event.
	 *
	 * @param event
	 *            The event to submit.
	 */
	public void submit(Event event) {
		eventManager.submit(event);
	}

	/**
	 * Submits a new tickable.
	 *
	 * @param tickable
	 *            The tickable to submit.
	 */
	public void submit(final Tickable tickable) {
		engineService.offerTask(() -> tickManager.submit(tickable));
	}

	/**
	 * Gets the world loader.
	 *
	 * @return The world loader.
	 */
	public WorldLoader getWorldLoader() {
		return loader;
	}

	/**
	 * Loads a player's game in the work service.
	 *
	 * @param details
	 *            The player's details. return codes; 4 banned, 6 update, 5 account
	 *            logged in, 3 = invalid user/pass, 7 world full, 8 loginserver off
	 *            9 too many conns from ip,
	 */
	public void load(final PlayerDetails details) {
		engineService.offerToSingle(() -> {
			LoginResult lr = loader.checkLogin(details);
			int code = lr.getReturnCode();
			if (code != 2) {
				PacketBuilder bldr = new PacketBuilder();
				bldr.put((byte) code);
				details.getSession().write(bldr.toPacket()).addListener(future -> future.getSession().close(false));
			} else {
				lr.getPlayer().getSession().setAttribute("player", lr.getPlayer());
				lr.getPlayer().setAttribute("starter", !loader.loadPlayer(lr.getPlayer()));
				engineService.offerTask(new SessionLoginTask(lr.getPlayer()));
			}
		});
	}

	// TODO DAFUQ IS THIS????? Cerberus unwalkables .-.
	private void loadExtraFlags() {
		RegionClipping.addClipping(1239, 1245, 0, 0x200000);
		RegionClipping.addClipping(1239, 1244, 0, 0x200000);
		RegionClipping.addClipping(1239, 1243, 0, 0x200000);
		RegionClipping.addClipping(1239, 1242, 0, 0x200000);
		RegionClipping.addClipping(1239, 1241, 0, 0x200000);
		RegionClipping.addClipping(1239, 1240, 0, 0x200000);
		RegionClipping.addClipping(1239, 1239, 0, 0x200000);
		RegionClipping.addClipping(1241, 1239, 0, 0x200000);
		RegionClipping.addClipping(1241, 1240, 0, 0x200000);
		RegionClipping.addClipping(1241, 1241, 0, 0x200000);
		RegionClipping.addClipping(1241, 1242, 0, 0x200000);
		RegionClipping.addClipping(1241, 1243, 0, 0x200000);
		RegionClipping.addClipping(1241, 1244, 0, 0x200000);
		RegionClipping.addClipping(1241, 1245, 0, 0x200000);
		RegionClipping.addClipping(1242, 1245, 0, 0x200000);
		RegionClipping.addClipping(1243, 1246, 0, 0x200000);
		RegionClipping.addClipping(1244, 1246, 0, 0x200000);
		RegionClipping.addClipping(1245, 1246, 0, 0x200000);
		RegionClipping.addClipping(1246, 1247, 0, 0x200000);
		RegionClipping.addClipping(1247, 1248, 0, 0x200000);
		RegionClipping.addClipping(1246, 1249, 0, 0x200000);
		RegionClipping.addClipping(1246, 1250, 0, 0x200000);
		RegionClipping.addClipping(1246, 1251, 0, 0x200000);
		RegionClipping.addClipping(1246, 1252, 0, 0x200000);
		RegionClipping.addClipping(1247, 1252, 0, 0x200000);
		RegionClipping.addClipping(1248, 1252, 0, 0x200000);
		RegionClipping.addClipping(1249, 1252, 0, 0x200000);
		RegionClipping.addClipping(1238, 1245, 0, 0x200000);
		RegionClipping.addClipping(1237, 1245, 0, 0x200000);
		RegionClipping.addClipping(1236, 1246, 0, 0x200000);
		RegionClipping.addClipping(1235, 1246, 0, 0x200000);
		RegionClipping.addClipping(1234, 1247, 0, 0x200000);
		RegionClipping.addClipping(1233, 1248, 0, 0x200000);
		RegionClipping.addClipping(1233, 1249, 0, 0x200000);
		RegionClipping.addClipping(1234, 1250, 0, 0x200000);
		RegionClipping.addClipping(1234, 1251, 0, 0x200000);
		RegionClipping.addClipping(1234, 1252, 0, 0x200000);
		RegionClipping.addClipping(1233, 1252, 0, 0x200000);
		RegionClipping.addClipping(1232, 1252, 0, 0x200000);
		RegionClipping.addClipping(1231, 1252, 0, 0x200000);
		RegionClipping.addClipping(1239, 1236, 0, 0x200000);
		RegionClipping.addClipping(1239, 1235, 0, 0x200000);
		RegionClipping.addClipping(1239, 1234, 0, 0x200000);
		RegionClipping.addClipping(1239, 1233, 0, 0x200000);
		RegionClipping.addClipping(1239, 1232, 0, 0x200000);
		RegionClipping.addClipping(1239, 1231, 0, 0x200000);
		RegionClipping.addClipping(1239, 1230, 0, 0x200000);
		RegionClipping.addClipping(1239, 1229, 0, 0x200000);
		RegionClipping.addClipping(1239, 1228, 0, 0x200000);
		RegionClipping.addClipping(1239, 1227, 0, 0x200000);
		RegionClipping.addClipping(1239, 1226, 0, 0x200000);
		RegionClipping.addClipping(1241, 1226, 0, 0x200000);
		RegionClipping.addClipping(1241, 1227, 0, 0x200000);
		RegionClipping.addClipping(1241, 1228, 0, 0x200000);
		RegionClipping.addClipping(1241, 1229, 0, 0x200000);
		RegionClipping.addClipping(1241, 1230, 0, 0x200000);
		RegionClipping.addClipping(1241, 1231, 0, 0x200000);
		RegionClipping.addClipping(1241, 1232, 0, 0x200000);
		RegionClipping.addClipping(1241, 1233, 0, 0x200000);
		RegionClipping.addClipping(1241, 1234, 0, 0x200000);
		RegionClipping.addClipping(1241, 1235, 0, 0x200000);
		RegionClipping.addClipping(1241, 1236, 0, 0x200000);
		RegionClipping.addClipping(1231, 1256, 0, 0x200000);
		RegionClipping.addClipping(1232, 1256, 0, 0x200000);
		RegionClipping.addClipping(1233, 1256, 0, 0x200000);
		RegionClipping.addClipping(1234, 1256, 0, 0x200000);
		RegionClipping.addClipping(1235, 1256, 0, 0x200000);
		RegionClipping.addClipping(1245, 1256, 0, 0x200000);
		RegionClipping.addClipping(1246, 1256, 0, 0x200000);
		RegionClipping.addClipping(1247, 1256, 0, 0x200000);
		RegionClipping.addClipping(1248, 1256, 0, 0x200000);
		RegionClipping.addClipping(1249, 1256, 0, 0x200000);
		RegionClipping.addClipping(1810, 3771, 0, 0x200000);
		RegionClipping.addClipping(1809, 3771, 0, 0x200000);
		RegionClipping.addClipping(1808, 3771, 0, 0x200000);
	}

	/**
	 * Registers a new npc.
	 *
	 * @param npc
	 *            The npc to register.
	 */
	public void register(NPC npc) {
		npcs.add(npc);
		npc.setLocation(npc.getSpawnLocation());
		CombatNPCDefinition combatDefinition = CombatNPCDefinition.of(npc.getDefinition().getId());
		if (combatDefinition != null) {
			npc.setCombatDefinition(combatDefinition);
			npc.setCombatCooldownDelay(combatDefinition.getCombatCooldownDelay());
			for (Skill skill : combatDefinition.getSkills().keySet()) {
				npc.getSkills().setSkill(skill.getId(), combatDefinition.getSkills().get(skill),
						npc.getSkills().getExperienceForLevel(combatDefinition.getSkills().get(skill)));
			}
			npc.getCombatState().setCombatStyle(combatDefinition.getCombatStyle());
			npc.getCombatState().setAttackType(combatDefinition.getAttackType());
			npc.getCombatState().setBonuses(combatDefinition.getBonuses());
		}
	}

	public NPC createNPC(NPC npc) {
		npcs.add(npc);
		npc.setLocation(npc.getSpawnLocation());
		CombatNPCDefinition combatDefinition = CombatNPCDefinition.of(npc.getDefinition().getId());
		if (combatDefinition != null) {
			npc.setCombatDefinition(combatDefinition);
			npc.setCombatCooldownDelay(combatDefinition.getCombatCooldownDelay());
			for (Skill skill : combatDefinition.getSkills().keySet()) {
				npc.getSkills().setSkill(skill.getId(), combatDefinition.getSkills().get(skill),
						npc.getSkills().getExperienceForLevel(combatDefinition.getSkills().get(skill)));
			}
			npc.getCombatState().setCombatStyle(combatDefinition.getCombatStyle());
			npc.getCombatState().setAttackType(combatDefinition.getAttackType());
			npc.getCombatState().setBonuses(combatDefinition.getBonuses());
		} else {
			if (Constants.DEBUG)
				System.out.println(npc.getId() + " is missing combat definitions; plzz add :L");
		}
		System.out.println("spawning npc at "+npc.getSpawnLocation());
		return npc;
	}
	
	/**
	 * Unregisters an old npc.
	 *
	 * @param npc
	 *            The npc to unregister.
	 */
	public void unregister(NPC npc) {
		npcs.remove(npc);
		npc.destroy();
	}

	/**
	 * Registers a new player.
	 *
	 * @param player
	 *            The player to register.
	 */
	public void register(final Player player) {
		int returnCode = 2;

		final PlayerEntity entity = persistenceService.getOrCreatePlayer(player);

		if (isPlayerOnline(player.getName())) {
			if (Constants.DEBUG)
				logger.warn("Could not log in {}. Player is already online.", entity.getAccountName());
			returnCode = 5;
		} else {
			if (!players.add(player)) {
				returnCode = 7;
				if (Constants.DEBUG)
					logger.info("Could not register player : " + player + " [world full]");
			} else {
				playerNames.put(NameUtils.nameToLong(player.getName()), player);

				new Thread(new PlayersOnlineManager(player, false)).start();

				World.getWorld().submit(new LoyaltyPointTick(player));
				World.getWorld().submit(new DoubleEXPTick(player));

				if (player.getIndex() < 1) {
					logger.error("Player {} has an index of {}. This should not happen!!!!!!", player.getName(),
							player.getIndex());

				}
			}
		}
		final int fReturnCode = returnCode;

		final PacketBuilder bldr = new PacketBuilder();
		bldr.put((byte) returnCode);// rip
		bldr.put((byte) 0);
		bldr.putInt(0);
		bldr.put((byte) 2);
		bldr.put((byte) 0);
		bldr.putShort(player.getIndex());
		bldr.put((byte) 0);
		player.getSession().write(bldr.toPacket()).addListener(future -> {
			if (fReturnCode != 2)
				player.getSession().close(false);
			else {
				persistenceService.initialisePlayer(player);
				// Server.getLoginServer().register(player);
				player.getActionSender().sendLogin();
				hookService.post(new GamePlayerLoginEvent(player));
			}
		});
		if (WorldList.list.get(Constants.WORLD_ID) != null)
			WorldList.list.get(Constants.WORLD_ID).setPlayercount(World.getWorld().getPlayers().size());
	}

	public void createGroundItem(GroundItem item, Mob mob) {
		createGroundItem(new GroundItemDefinition(mob.getUndefinedName(), item.getLocation(), item.getItem().getId(),
				item.getItem().getCount()), mob);
	}

	public void register(GroundItem item, Mob mob) {
		register(new GroundItemDefinition(mob.getUndefinedName(), item.getLocation(), item.getItem().getId(),
				item.getItem().getCount()), mob);
	}

	public void unregister(GroundItem item) {
		unregister(new GroundItemDefinition(item.getControllerName(), item.getLocation(), item.getItem().getId(),
				item.getItem().getCount()));
	}

	/**
	 * Creates a ground item.
	 *
	 * @param item
	 *            The ground item.
	 * @param player
	 *            The controller.
	 */
	public void createGroundItem(final GroundItemDefinition item, final Mob mob) {
		if (mob.isPlayer()) {
			Player player = (Player) mob;
			GroundItemService groundItemService = Server.getInjector().getInstance(GroundItemService.class);
			groundItemService.createGroundItem(player, new GroundItemService.GroundItem(
					new Item(item.getId(), item.getCount()), item.getLocation(), player, false));
		}
	}

	/**
	 * Registers a new ground item.
	 *
	 * @param item
	 *            The item to register.
	 * @param player
	 *            The controller.
	 */
	public void register(final GroundItemDefinition item, final Mob mob) {
		if (mob.isPlayer()) {
			Player player = (Player) mob;
			GroundItemService groundItemService = Server.getInjector().getInstance(GroundItemService.class);
			groundItemService.createGroundItem(player, new GroundItemService.GroundItem(
					new Item(item.getId(), item.getCount()), item.getLocation(), player, false));
		}
	}

	/**
	 * Unregisters a new ground item.
	 *
	 * @param fletchItem
	 *            The item to unregister.
	 */
	public void unregister(GroundItemDefinition ground) {
		GroundItemService groundItemService = Server.getInjector().getInstance(GroundItemService.class);
		Optional<GroundItemService.GroundItem> groundItem = groundItemService.getGroundItem(ground.getId(),
				ground.getLocation());
		if (groundItem.isPresent()) {
			groundItemService.removeGroundItem(groundItem.get());
		}
	}

	public void removeObjectKeepClipping(final GameObject obj, int cycles) {
		for (Region r : regionManager.getSurroundingRegions(obj.getLocation())) {
			for (Player p : r.getPlayers()) {
				p.getActionSender().removeObject(obj);
			}
		}
		submit(new Tickable(cycles) {
			@Override
			public void execute() {
				for (Region r : regionManager.getSurroundingRegions(obj.getLocation())) {
					for (Player p : r.getPlayers()) {
						p.getActionSender().sendObject(obj);
					}
				}
				stop();
			}
		});
	}

	/**
	 * Registers a game object.
	 *
	 * @param obj
	 *            The game object to register.
	 */
	public void register(GameObject obj) {
		obj.setLocation(obj.getSpawnLocation() != null ? obj.getSpawnLocation() : obj.getLocation());
		if (!obj.isLoadedInLandscape()) {
			Region[] regions = regionManager.getSurroundingRegions(obj.getLocation());
			for (Region r : regions) {
				for (Player p : r.getPlayers())
					p.getActionSender().sendObject(obj);
			}
		}
		if (getRemovedObjects().contains(obj)) {
			getRemovedObjects().remove(obj);
		}
		obj.addToRegion(obj.getRegion());
		RegionClipping.addClipping(obj);
	}

	public void registerObjectForPlayer(Player player, GameObject obj) {
		obj.setLocation(obj.getSpawnLocation() != null ? obj.getSpawnLocation() : obj.getLocation());
		if (!obj.isLoadedInLandscape()) {
			player.getActionSender().sendObject(obj);
		}
	}

	public void replaceObject(final GameObject original, final GameObject replacement, int cycles) {
		unregister(original, true);
		RegionClipping.removeClipping(original);
		if (replacement != null) {
			register(replacement);
			RegionClipping.addClipping(replacement);
		}
		if (cycles < 0)
			return;
		submit(new Tickable(cycles) {
			@Override
			public void execute() {
				if (replacement != null) {
					unregister(replacement, true);
					RegionClipping.removeClipping(replacement);
				}
				GameObject addOrig = new GameObject(original.getLocation(), original.getId(), original.getType(),
						original.getDirection(), false);
				register(addOrig);
				RegionClipping.addClipping(addOrig);
				stop();
			}
		});
	}

	public void replaceObjectKeepClipping(final GameObject original, final GameObject replacement, int cycles) {
		unregister(original, true);
		if (replacement != null) {
			register(replacement);
		}
		if (cycles < 0)
			return;
		submit(new Tickable(cycles) {
			@Override
			public void execute() {
				if (replacement != null) {
					unregister(replacement, true);
				}
				GameObject addOrig = new GameObject(original.getLocation(), original.getId(), original.getType(),
						original.getDirection(), false);
				register(addOrig);
				stop();
			}
		});
	}

	public List<GameObject> removedObjects = new ArrayList<>();

	public List<GameObject> getRemovedObjects() {
		return removedObjects;
	}

	/**
	 * Unregisters a game object.
	 *
	 * @param obj
	 *            The game object to unregister.
	 * @param remove
	 *            The flag to remove it on players screens.
	 */
	public void unregister(GameObject obj, boolean remove) {
		Region[] regions = null;
		if (remove) {
			regions = regionManager.getSurroundingRegions(obj.getLocation());
			for (Region r : regions) {
				for (Player p : r.getPlayers()) {
					p.getActionSender().removeObject(obj);
				}
			}
			removedObjects.add(obj);
		}
		if (regions == null)
			regions = regionManager.getSurroundingRegions(obj.getLocation());
		for (Region r : regions) {
			r.removeObject(obj);
		}
		if (obj.getRegion() != null) {
			obj.removeFromRegion(obj.getRegion());
		}
	}

	/**
	 * Gets the player list.
	 *
	 * @return The player list.
	 */
	public EntityList<Player> getPlayers() {
		return players;
	}

	/**
	 * @return the playerNames
	 */
	public Map<Long, Player> getPlayerNames() {
		return playerNames;
	}

	/**
	 * Gets the npc list.
	 *
	 * @return The npc list.
	 */
	public EntityList<NPC> getNPCs() {
		return npcs;
	}

	/**
	 * Checks if a player is online.
	 *
	 * @param name
	 *            The player's name.
	 * @return <code>true</code> if they are online, <code>false</code> if not.
	 */
	public boolean isPlayerOnline(String name) {
		final long longName = NameUtils.nameToLong(name);
		return playerNames.containsKey(longName) && playerNames.get(longName) != null;
	}

	/**
	 * Unregisters a player, and saves their game.
	 *
	 * @param player
	 *            The player to unregister.
	 */
	public void unregister(final Player player) {
		try {
			if (player.isActive()) {
				if (player.getBountyHunter() != null) {
					player.getBountyHunter().getCrater().getPlayers().stream()
							.filter(p -> p.getBountyHunter() != null && p.getBountyHunter().getTarget() == player)
							.forEach(p -> {
								p.getBountyHunter().setTarget(null);
								p.getActionSender()
										.sendMessage("Your target has logged out. You shall be found a new target.");
							});
					player.getBountyHunter().getCrater().remove(player);
				}
				if (player.getAttribute("cannon") != null) {
					Cannon cannon = (Cannon) player.getAttribute("cannon");
					cannon.destroy();
					player.removeAttribute("cannon");
				}
				if (player.getAttribute("cutScene") != null) {
					DialogueManager.openDialogue(player, 123); // XXX no idea what this was for lol
				}
				if (player.getVenomDrainTick() != null) {
					player.getVenomDrainTick().stop();
				}
				if (player.getInterfaceState().getClan().length() > 0) {
					World.getWorld().getPrivateChat().get(player.getInterfaceState().getClan())
							.removeClanMember(player);
				}
				if (player.getCombatState().spellbookSwap()) {
					player.getCombatState().setSpellBook(MagicCombatAction.SpellBook.LUNAR_MAGICS.getSpellBookId());
					player.getCombatState().setSpellbookSwap(false);
				}
				if (player.getInterfaceState().isInterfaceOpen(PriceChecker.PRICE_INVENTORY_INTERFACE))
					PriceChecker.returnItems(player);
				hookService.post(new GamePlayerLogoutEvent(player));
				player.getContentManager().stopAll();
				player.resetInteractingEntity();
				player.getActionQueue().clearAllActions();
				player.getActionManager().stopAction();
				player.getActionSender().removeAllInterfaces();
				if (player.getPrivateChat() != null)
					player.getPrivateChat().updateFriendList(false);
				if (player.getMinigame() != null)
					player.getMinigame().quit(player);
				if (player.getInterfaceAttribute("fightPitOrbs") != null)
					World.getWorld().getFightPits().quit(player);
				if (ResourceArenaServiceImpl.IN_ARENA.contains(player)
						&& BoundaryManager.isWithinBoundaryNoZ(player.getLocation(), "ResourceArena"))
					player.setLocation(Location.create(3184, 3945, 0));

				// Server.getLoginServer().unregister(player);
				player.write(new PacketBuilder(168).toPacket());
			}
			new Thread(new HiscoresManager(player)).start();
			new Thread(new PlayersOnlineManager(player, true)).start();
			new Thread(new ForumSynchronisation(player)).start();

			player.destroy();

			if (Constants.DEBUG)
				logger.info("Unregistering player : " + player + " [online=" + players.size() + "]");

		} catch (Exception e) {
			logger.error("Exception in World#unregister(Player)", e);
		}

		try {
			playerNames.remove(NameUtils.nameToLong(player.getName()));
			players.remove(player);
		} catch (ConcurrentModificationException e) {
			// Ignore
		}

		loader.savePlayer(player);
		// engineService.offerToSingle(() -> loader.savePlayer(player));

		if (!player.getSession().isClosing()) {
			player.getSession().close(false);
		}
	}

	/**
	 * Handles an exception in any of the pools.
	 *
	 * @param t
	 *            The exception.
	 */
	public void handleError(Throwable t) {
		logger.error("An error occurred in an executor service! The server will be halted immediately.");
		t.printStackTrace();
		// System.exit(1);
	}

	@SuppressWarnings("unchecked")
	public boolean deserializePrivate(String owner) {
		owner = NameUtils.formatName(owner);
		File f = new File("data/savedGames/privateChats/" + owner + ".dat.gz");
		if (f.exists()) {
			try {
				InputStream is = new GZIPInputStream(new FileInputStream(f));
				IoBuffer buf = IoBuffer.allocate(1024);
				buf.setAutoExpand(true);
				while (true) {
					byte[] temp = new byte[1024];
					int read = is.read(temp, 0, temp.length);
					if (read == -1) {
						break;
					} else {
						buf.put(temp, 0, read);
					}
				}
				buf.flip();

				PrivateChat privateChat = new PrivateChat(owner, IoBufferUtils.getRS2String(buf));
				privateChat.setEntryRank(EntryRank.forId(buf.get()));
				privateChat.setTalkRank(TalkRank.forId(buf.get()));
				privateChat.setKickRank(KickRank.forId(buf.get()));
				World.getWorld().getPrivateChat().put(owner, privateChat);

				if (buf.hasRemaining()) {
					try {
						HashMap<Long, ClanRank> friends = (HashMap<Long, ClanRank>) buf.getObject();
						for (long l : friends.keySet()) {
							World.getWorld().getPrivateChat().get(owner).addFriend(l, friends.get(l));
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
				if (buf.hasRemaining()) {
					try {
						List<Long> ignores = (List<Long>) buf.getObject();
						for (long l : ignores) {
							World.getWorld().getPrivateChat().get(owner).addIgnore(l);
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}

				if (buf.hasRemaining()) {
					World.getWorld().getPrivateChat().get(owner).setLootRank(LootRank.forId(buf.get()));
					World.getWorld().getPrivateChat().get(owner).setCoinShare(buf.get() == 1);
				}

				is.close();
				return true;
			} catch (IOException ex) {
				return false;
			}
		}
		return false;
	}

	public boolean serializePrivate(String owner) {
		try {
			OutputStream os = new GZIPOutputStream(
					new FileOutputStream("data/savedGames/privateChats/" + owner + ".dat.gz"));
			IoBuffer buf = IoBuffer.allocate(1024);
			buf.setAutoExpand(true);

			PrivateChat privateChat = World.getWorld().getPrivateChat().get(owner);
			if (privateChat != null) {
				IoBufferUtils.putRS2String(buf, privateChat.getChannelName());
				buf.put((byte) privateChat.getEntryRank().getId());
				buf.put((byte) privateChat.getTalkRank().getId());
				buf.put((byte) privateChat.getKickRank().getId());
				buf.putObject(privateChat.getFriends());
				buf.putObject(privateChat.getIgnores());

				buf.put((byte) privateChat.getLootRank().getId());
				buf.put((byte) (privateChat.isCoinSharing() ? 1 : 0));

				buf.flip();
				byte[] data = new byte[buf.limit()];
				buf.get(data);
				os.write(data);
				os.flush();
				os.close();
			}
			return true;
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public boolean privateExists(String owner) {
		// owner = NameUtils.formatName(owner);
		File f = new File("data/savedGames/privateChats/" + owner + ".dat.gz");
		return f.exists();
	}

	public boolean privateIsRegistered(String owner) {
		return World.getWorld().getPrivateChat().containsKey(owner);
	}

	public boolean clanIsRegistered(String owner) {
		if (privateExists(owner) && !privateIsRegistered(owner)) {
			if (!deserializePrivate(owner))
				return false;
		}
		return World.getWorld().getPrivateChat().get(owner).getChannelName().length() > 0;
	}

	public TilePath doPath(PathFinder pathFinder, Mob mob, int x, int y) {
		return doPath(pathFinder, mob, x, y, false, true);
	}

	public TilePath doPath(final PathFinder pathFinder, final Mob mob, final int x, final int y,
			final boolean ignoreLastStep, boolean addToWalking) {
		if (!mob.getCombatState().canMove()) {
			TilePath state = new TilePath();
			state.routeFailed();
			return state;
		}
		Location destination = Location.create(x, y, mob.getLocation().getPlane());
		Location base = mob.getLocation();
		int srcX = base.getLocalX();
		int srcY = base.getLocalY();
		int destX = destination.getLocalX(base);
		int destY = destination.getLocalY(base);
		TilePath state = pathFinder.findPath(mob, mob.getLocation(), srcX, srcY, destX, destY,
				mob.getLocation().getPlane(), mob.getWidth(), mob.getWalkingQueue().isRunning(), ignoreLastStep, true);
		if (state != null && addToWalking) {
			mob.getWalkingQueue().reset();
			for (BasicPoint step : state.getPoints())
				mob.getWalkingQueue().addStep(step.getX(), step.getY());
			mob.getWalkingQueue().finish();
		}
		return state;
	}

	/**
	 * Creates the world and begins background loading tasks.
	 */
	private Map<Integer, int[]> mapData;

	/**
	 * Gets mapdata for a region.
	 *
	 * @param region
	 *            The region.
	 * @return The map data.
	 */
	public int[] getMapData(int region) {
		return mapData.get(region);
	}

	public void doPath(Mob mob, TilePath state) {
		if (state != null) {
			// TODO: Check if this works as planned.
			if (!mob.getCombatState().canMove()) {
				return;
			}
			mob.getWalkingQueue().reset();
			for (BasicPoint step : state.getPoints()) {
				mob.getWalkingQueue().addStep(step.getX(), step.getY());
			}
		}
	}

	public void submitAreaEvent(final Mob mob, final CoordinateEvent coordinateEvent) {
		mob.submitTick("area_event", new Tickable(1) {

			private int attempts;

			@Override
			public void execute() {
				if (++attempts >= 20) {
					stop();
					return;
				}
				if ((coordinateEvent.inArea()) && mob.getCombatState().canMove()) {
					stop();
					mob.getWalkingQueue().reset();
					coordinateEvent.execute();
				}
			}
		});
	}

	public void sendWorldMessage(String text) {
		for (Player player : World.getWorld().getPlayers()) {
			if (player == null || player.getActionSender() == null)
				continue;
			player.getActionSender().sendMessage(text);
		}
	}

	public WorldType getType() {
		return type;
	}

	public void setType(WorldType type) {
		this.type = type;
	}

	private boolean worldUpdateInProgress;

	public boolean isWorldUpdateInProgress() {
		return worldUpdateInProgress;
	}

	/**
	 * World progress is being updated
	 * 
	 * @param worldUpdateInProgress
	 */
	public void setWorldUpdateInProgress(boolean worldUpdateInProgress) {
		this.worldUpdateInProgress = worldUpdateInProgress;
	}

	/**
	 * Map holding all worlds ordered by their Id
	 */
	private final Map<Integer, ReferencedWorld> worldDock = new HashMap<Integer, ReferencedWorld>();

	public final Map<Integer, ReferencedWorld> getWorldDock() {
		return worldDock;
	}

	/**
	 * Checks for the day of the week.
	 * 
	 * @return dayOfWeek the weekday to return.
	 */
	private static int dayOfWeek() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * Checks if it's the weekend.
	 * 
	 * @return theDayOfWeek.
	 */
	public static boolean isWeekend() {
		return dayOfWeek() == 1 ? true : dayOfWeek() == 6 ? true : dayOfWeek() == 7 ? true : false;
	}

	/**
	 * These will be used for vote parties and whatnot
	 */
	private int[] voteBazinga = new int[4];
	
	public int getTotalVotes() {
		return this.voteBazinga[2];
	}

	/**
	 * Increase total world votes and do stuff when X amounts reached
	 * 
	 * @param votes
	 *            the Integer to increase by
	 */
	public void increaseVotes(int votes) {
		this.voteBazinga[2] += votes; // static total votes used for reference

		if (this.voteBazinga[2] % 50 == 0 && this.voteBazinga[2] > 0 && this.voteBazinga[2] % 300 != 0) {
			this.sendWorldMessage(
					"<img=21><col=EDB20E><shad=000000>Total of [<col=ff0000>"
							+ Misc.formatNumber(this.voteBazinga[2])
							+ "<col=EDB20E>] votes have been claimed; vote to redeem yours!");
		}

		if (this.voteBazinga[2] % 300 == 0 && this.voteBazinga[2] > 0) {

			Item keys = new Item((Misc.random(1) == 0 ? 986 : 988), 3); // crystal key parts

			int peopleOnline = 0;

			for (Player player : this.getPlayers()) {
				if (player == null || player.getAfkTolerance() > 15 || player.isIronMan() || player.isHardcoreIronMan()
						|| player.isUltimateIronMan()) // no afk'ers and no ironman
					continue;
				Inventory.addDroppable(player, keys);
				peopleOnline++;
			}

			this.sendWorldMessage("<img=27><col=EDB20E><shad=000000>You and <col=ff0000>" + peopleOnline
					+ "<col=EDB20E> others received <col=ff0000>3"
					+ "<col=EDB20E> x <col=ff0000>Crystal key parts<col=EDB20E>.");

			Server.sendDiscordMessage("[SERVER] Vote Party: " + getPlayers().size() + " x players rewarded with "
					+ "3 Crystal key parts; total votes: " + Misc.formatNumber(this.voteBazinga[2]) + ".");
		}
	}
}