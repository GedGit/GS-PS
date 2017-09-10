package org.rs2server.rs2.plugin;

import java.util.ArrayList;
import java.util.List;
import org.reflections.Reflections;

/*import org.wildscape.game.content.activity.ActivityManager;
import org.wildscape.game.content.activity.ActivityPlugin;
import org.wildscape.game.content.dialogue.DialoguePlugin;
import org.wildscape.game.node.entity.player.info.login.LoginConfiguration;
import org.wildscape.game.node.entity.player.link.quest.Quest;
import org.wildscape.game.node.entity.player.link.quest.QuestRepository;
*/
/**
 * Represents a class used to handle the loading of all plugins.
 * @author Emperor
 */
/**
 * Represents a class used to handle the loading of all plugins.
 * @author Emperor
 */
public final class PluginManager {

	/**
	 * The amount of plugins loaded.
	 */
	private static int pluginCount;

	/**
	 * The currently loaded plugin names.
	 */
	private static List<String> loadedPlugins = new ArrayList<>();
	
	/**
	 * The last loaded plugin.
	 */
	private static String lastLoaded;

	/**
	 * Initializes the plugin manager.
	 */
	public static void init() {
		try {
			//loadLocal(new File("plugin/"));
			load("plugin");
			loadedPlugins.clear();
			loadedPlugins = null;
			System.err.println("Initialized " + pluginCount + " plugins...");
		} catch (Throwable t) {
			System.err.println("Error initializing Plugins -> " + t.getLocalizedMessage() + " for file -> " + lastLoaded);
			t.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	public static void load(String root) throws Throwable {
		if (root == null || root.isEmpty()) {
			root = "plugin";
		}
		Reflections reflections = new Reflections(root);
		for (Class c : reflections.getTypesAnnotatedWith(InitializablePlugin.class)) {
			try {
				if (!c.isMemberClass() && !c.isAnonymousClass()) {
					final Plugin plugin = (Plugin) c.newInstance();
					definePlugin(plugin);
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}
	
	/**
	 * Defines a list of plugins.
	 * @param plugins the plugins.
	 */
	public static void definePlugin(Plugin<?>... plugins) {
		for (Plugin<?> p : plugins) {
			definePlugin(p);
		}
	}

	/**
	 * Defines the plugin.
	 * @param plugin The plugin.
	 */
	public static void definePlugin(Plugin<?> plugin) {
		try {
			PluginManifest manifest = plugin.getClass().getAnnotation(PluginManifest.class);
			if (manifest == null) {
				manifest = plugin.getClass().getSuperclass().getAnnotation(PluginManifest.class);
			}
			if (manifest == null || manifest.type() == PluginType.ACTION) {
				plugin.newInstance(null);
				System.out.println("Manifest: " + manifest.type());
			}
			pluginCount++;
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the amount of plugins currently loaded.
	 * @return The plugin count.
	 */
	public static int getAmountLoaded() {
		return pluginCount;
	}

	/**
	 * Checks if the plugin shouldn't be repacked.
	 * @param name The plugin name.
	 * @return {@code True} if so.
	 */
	public static boolean isAutoPacked(String name) {
		return name.equals("VicTheTraderPlugin.jar");
	}

	/**
	 * Gets the pluginCount.
	 * @return the pluginCount.
	 */
	public static int getPluginCount() {
		return pluginCount;
	}

	/**
	 * Sets the pluginCount.
	 * @param pluginCount the pluginCount to set
	 */
	public static void setPluginCount(int pluginCount) {
		PluginManager.pluginCount = pluginCount;
	}

	/**
	 * Gets the loadedPlugins.
	 * @return the loadedPlugins.
	 */
	public static List<String> getLoadedPlugins() {
		return loadedPlugins;
	}

	/**
	 * Sets the loadedPlugins.
	 * @param loadedPlugins the loadedPlugins to set
	 */
	public static void setLoadedPlugins(List<String> loadedPlugins) {
		PluginManager.loadedPlugins = loadedPlugins;
	}
}