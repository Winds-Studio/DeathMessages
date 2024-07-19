package dev.mrshawn.deathmessages;

import com.tcoded.folialib.FoliaLib;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.commands.CommandManager;
import dev.mrshawn.deathmessages.commands.TabCompleter;
import dev.mrshawn.deathmessages.commands.alias.CommandDeathMessagesToggle;
import dev.mrshawn.deathmessages.config.ConfigManager;
import dev.mrshawn.deathmessages.config.Settings;
import dev.mrshawn.deathmessages.files.Config;
import dev.mrshawn.deathmessages.files.FileSettings;
import dev.mrshawn.deathmessages.hooks.*;
import dev.mrshawn.deathmessages.utils.nms.V1_20_6;
import dev.mrshawn.deathmessages.utils.nms.V1_21;
import dev.mrshawn.deathmessages.utils.nms.Wrapper;
import dev.mrshawn.deathmessages.kotlin.files.FileStore;
import dev.mrshawn.deathmessages.listeners.EntityDamage;
import dev.mrshawn.deathmessages.listeners.EntityDamageByBlock;
import dev.mrshawn.deathmessages.listeners.EntityDamageByEntity;
import dev.mrshawn.deathmessages.listeners.EntityDeath;
import dev.mrshawn.deathmessages.listeners.OnCommand;
import dev.mrshawn.deathmessages.listeners.OnInteract;
import dev.mrshawn.deathmessages.listeners.OnJoin;
import dev.mrshawn.deathmessages.listeners.OnMove;
import dev.mrshawn.deathmessages.listeners.OnQuit;
import dev.mrshawn.deathmessages.listeners.PlayerDeath;
import dev.mrshawn.deathmessages.listeners.PluginMessaging;
import dev.mrshawn.deathmessages.listeners.combatlogx.PlayerUntag;
import dev.mrshawn.deathmessages.listeners.customlisteners.BlockExplosion;
import dev.mrshawn.deathmessages.listeners.customlisteners.BroadcastEntityDeathListener;
import dev.mrshawn.deathmessages.listeners.customlisteners.BroadcastPlayerDeathListener;
import dev.mrshawn.deathmessages.listeners.mythicmobs.MobDeath;
import dev.mrshawn.deathmessages.utils.EventUtil;
import dev.mrshawn.deathmessages.utils.Updater;
import dev.mrshawn.deathmessages.utils.Util;
import io.lumine.mythic.bukkit.MythicBukkit;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bstats.bukkit.Metrics;
import org.jetbrains.annotations.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class DeathMessages extends JavaPlugin {

	public static final Logger LOGGER = LogManager.getLogger(DeathMessages.class.getSimpleName());
	private static DeathMessages instance;
	private BukkitAudiences adventure;
	public final FoliaLib foliaLib = new FoliaLib(this);
	private static Wrapper nmsInstance;

	public boolean placeholderAPIEnabled = false;
	public boolean combatLogXAPIEnabled = false;
	public boolean langUtilsEnabled = false;

	public MythicBukkit mythicMobs = null;
	public boolean mythicmobsEnabled = false;

	public static WorldGuardExtension worldGuardExtension;
	public static boolean worldGuardEnabled;

	public static String bungeeServerName;
	public static boolean bungeeServerNameRequest = true;
	public static boolean bungeeInit = false;

	public static DiscordSRVExtension discordSRVExtension;
	public static boolean discordSRVEnabled = false;

	public EcoExtension ecoExtension;
	public boolean ecoEnchantsEnabled = false;

	public SayanVanishExtension sayanVanishExtension;
	public boolean sayanVanishEnabled = false;

	private static EventPriority eventPriority = EventPriority.HIGH;
	private static FileSettings<Config> config;

	@Override
	public void onEnable() {
		instance.adventure = BukkitAudiences.create(instance);
		instance.adventure.console().sendMessage(loadedLogo);

		initNMS();
		initListeners();
		initCommands();
		initHooks();
		initOnlinePlayers();
		checkGameRules();

		new Metrics(instance, 12365);
		LOGGER.info("bStats Hook Enabled!");
		instance.adventure.console().sendMessage(Component.text("DeathMessages " + instance.getDescription().getVersion() + " successfully loaded!", NamedTextColor.GOLD));
		checkUpdate();
	}

	@Override
	public void onLoad() {
		instance = this;

		initConfig();
		initHooksOnLoad();
	}

	@Override
	public void onDisable() {
		if (this.adventure != null) {
			this.adventure.close();
			this.adventure = null;
		}
		instance = null;
	}

	private void initNMS() {
		try {
			if (Util.isNewerAndEqual(21, 0)) {
				nmsInstance = V1_21.class.newInstance();
			} else if (Util.isNewerAndEqual(20, 5)) {
				nmsInstance = V1_20_6.class.newInstance();
			}
		} catch (InstantiationException | IllegalAccessException e) {
			LOGGER.error(e);
		}
	}

	public void initConfig() {
		ConfigManager.getInstance().initialize();
		config = FileStore.INSTANCE.getCONFIG();

		DeathMessages.eventPriority = EventPriority.valueOf(
				config.getString(Config.DEATH_LISTENER_PRIORITY).toUpperCase()
		);
	}

	private void initListeners() {
		EventUtil.registerEvents(
				// DeathMessages
				new BroadcastPlayerDeathListener(),
				new BroadcastEntityDeathListener(),
				// Bukkit
				new BlockExplosion(),
				new EntityDamage(),
				new EntityDamageByBlock(),
				new EntityDamageByEntity(),
				new EntityDeath(),
				new OnCommand(),
				new OnInteract(),
				new OnJoin(),
				new OnMove(),
				new OnQuit(),
				new PlayerDeath()
		);
	}

	private void initCommands() {
		CommandManager cm = new CommandManager();
		cm.initSubCommands();
		getCommand("deathmessages").setExecutor(cm);
		getCommand("deathmessages").setTabCompleter(new TabCompleter());
		getCommand("deathmessagestoggle").setExecutor(new CommandDeathMessagesToggle());
	}

	private void initHooks() {
		if (config.getBoolean(Config.HOOKS_BUNGEE_ENABLED)) {
			Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(instance, "BungeeCord");
			Bukkit.getServer().getMessenger().registerIncomingPluginChannel(instance, "BungeeCord", new PluginMessaging());
			LOGGER.info("Bungee Hook enabled!");
			if (config.getBoolean(Config.HOOKS_BUNGEE_SERVER_NAME_GET_FROM_BUNGEE)) {
				bungeeInit = true;
			} else {
				bungeeInit = false;
				bungeeServerName = config.getString(Config.HOOKS_BUNGEE_SERVER_NAME_DISPLAY_NAME);
			}
		}

		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			new PlaceholderAPIExtension(instance).register();
			placeholderAPIEnabled = true;
			LOGGER.info("PlaceholderAPI Hook Enabled!");
		}

		if (Bukkit.getPluginManager().getPlugin("NBTAPI") != null) {
			// Dreeam - Remove this useless notice in the future.
			LOGGER.info("Item-NBT-API Hook Enabled!");
		}

		if (worldGuardEnabled) {
			LOGGER.info("WorldGuard Hook Enabled!");
		}

		if (Bukkit.getPluginManager().getPlugin("DiscordSRV") != null && config.getBoolean(Config.HOOKS_DISCORD_ENABLED)) {
			discordSRVExtension = new DiscordSRVExtension();
			discordSRVEnabled = true;
			LOGGER.info("DiscordSRV Hook Enabled!");
		}

		if (Bukkit.getPluginManager().isPluginEnabled("PlugMan") && worldGuardExtension != null) {
			Plugin plugMan = Bukkit.getPluginManager().getPlugin("PlugMan");
			LOGGER.info("PlugMan found. Adding this plugin to its ignored plugins list due to WorldGuard hook being enabled!");
			try {
				List<String> ignoredPlugins = (List<String>) plugMan.getClass().getMethod("getIgnoredPlugins").invoke(plugMan);
				if (!ignoredPlugins.contains("DeathMessages")) {
					ignoredPlugins.add("DeathMessages");
				}
			} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException exception) {
				LOGGER.error("Error adding plugin to ignored plugins list: ", exception);
			}
		}

		if (Bukkit.getPluginManager().getPlugin("CombatLogX") != null && config.getBoolean(Config.HOOKS_COMBATLOGX_ENABLED)) {
			combatLogXAPIEnabled = true;
			Bukkit.getPluginManager().registerEvents(new PlayerUntag(), instance);
			LOGGER.info("CombatLogX Hook Enabled!");
		}

		if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null && config.getBoolean(Config.HOOKS_MYTHICMOBS_ENABLED)) {
			mythicMobs = MythicBukkit.inst();
			mythicmobsEnabled = true;
			Bukkit.getPluginManager().registerEvents(new MobDeath(), instance);
			LOGGER.info("MythicMobs Hook Enabled!");
		}

		if (Bukkit.getPluginManager().getPlugin("eco") != null && Bukkit.getPluginManager().getPlugin("EcoEnchants") != null) {
			ecoExtension = new EcoExtension();
			ecoEnchantsEnabled = true;
			LOGGER.info("EcoEnchants Hook Enabled!");
		}

		if (Bukkit.getPluginManager().getPlugin("SayanVanish") != null && config.getBoolean(Config.HOOKS_SAYANVANISH_ENABLED)) {
			sayanVanishExtension = new SayanVanishExtension();
			sayanVanishEnabled = true;
			LOGGER.info("SayanVanish Hook Enabled!");
		}

		if (Util.isOlderAndEqual(12, 2)) {
			if (Settings.getInstance().getConfig().getBoolean(Config.DISPLAY_I18N_ITEM_NAME.getPath())
					|| Settings.getInstance().getConfig().getBoolean(Config.DISPLAY_I18N_MOB_NAME.getPath())) {
				if (Bukkit.getPluginManager().getPlugin("LangUtils") != null) {
					langUtilsEnabled = true;
					LOGGER.info("LangUtils Hook Enabled!");
				} else {
					langUtilsEnabled = false;
					LOGGER.error("You enable the I18N Display feature, you need LangUtils plugin to make this feature works under <=1.12.2");
					LOGGER.error("Turn off I18N Display feature in config, or install LangUtils: https://github.com/MascusJeoraly/LanguageUtils/releases");
				}
			}
		}
	}

	private void initHooksOnLoad() {
		if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null && config.getBoolean(Config.HOOKS_WORLDGUARD_ENABLED)) {
			try {
				final String version = Bukkit.getPluginManager().getPlugin("WorldGuard").getDescription().getVersion();
				if (version.startsWith("7")) {
					worldGuardExtension = (WorldGuardExtension) Class.forName("dev.mrshawn.deathmessages.hooks.WorldGuard7Extension").getConstructor().newInstance();
					worldGuardExtension.registerFlags();
					worldGuardEnabled = true;
				} else if (version.startsWith("6")) {
					worldGuardExtension = (WorldGuardExtension) Class.forName("dev.mrshawn.deathmessages.hooks.WorldGuard6Extension").getConstructor().newInstance();
					worldGuardExtension.registerFlags();
					worldGuardEnabled = true;
				} else throw new UnsupportedOperationException();
			} catch (ClassNotFoundException | InvocationTargetException | InstantiationException |
					 IllegalAccessException | NoSuchMethodException | UnsupportedOperationException e) {
				LOGGER.error("Error loading WorldGuardHook. Error: ", e);
				worldGuardEnabled = false;
			}
		}
	}

	private void initOnlinePlayers() {
		getServer().getOnlinePlayers().forEach(PlayerManager::new);
	}

	private void checkGameRules() {
		if (config.getBoolean(Config.DISABLE_DEFAULT_MESSAGES)) {
			for (World world : Bukkit.getWorlds()) {
				try {
					if (Boolean.TRUE.equals(world.getGameRuleValue(GameRule.SHOW_DEATH_MESSAGES))) {
						foliaLib.getScheduler().runNextTick(task -> world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false));
					}
				} catch (NoClassDefFoundError e) {
					if (world.getGameRuleValue("showDeathMessages").equals("true")) world.setGameRuleValue("showDeathMessages", "false");
				}
			}
		}
	}

	private final TextComponent loadedLogo = Component.text().appendNewline()
			.append(Component.text("    ____             __  __    __  ___                                    ")).appendNewline()
			.append(Component.text("   / __ \\___  ____ _/ /_/ /_  /  |/  /__  ______________ _____ ____  _____")).appendNewline()
			.append(Component.text("  / / / / _ \\/ __ `/ __/ __ \\/ /|_/ / _ \\/ ___/ ___/ __ `/ __ `/ _ \\/ ___/")).appendNewline()
			.append(Component.text(" / /_/ /  __/ /_/ / /_/ / / / /  / /  __(__  |__  ) /_/ / /_/ /  __(__  ) ")).appendNewline()
			.append(Component.text("/_____/\\___/\\__,_/\\__/_/ /_/_/  /_/\\___/____/____/\\__,_/\\__, /\\___/____/  ")).appendNewline()
			.append(Component.text("                                                       /____/             ")).appendNewline()
			.build();

	private void checkUpdate() {
		if (Settings.getInstance().getConfig().getBoolean(Config.CHECK_UPDATE.getPath())) {
			Updater.checkUpdate();
			foliaLib.getScheduler().runLaterAsync(() -> {
				switch (Updater.shouldUpdate) {
					case 1:
						LOGGER.warn("Find a new version! Click to download: https://github.com/Winds-Studio/DeathMessages/releases");
						LOGGER.warn("Current Version: {} | Latest Version: {}", Updater.nowVer, Updater.latest);
						break;
					case -1:
						LOGGER.warn("Failed to check update!");
						break;
				}
			}, 50);
		}
	}

	public static DeathMessages getInstance() {
		return instance;
	}
	public static Wrapper getNMS() {
		return nmsInstance;
	}
	public static EventPriority getEventPriority() {
		return eventPriority;
	}

	public @NotNull BukkitAudiences adventure() {
		if (this.adventure == null) {
			throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
		}
		return this.adventure;
	}
}
