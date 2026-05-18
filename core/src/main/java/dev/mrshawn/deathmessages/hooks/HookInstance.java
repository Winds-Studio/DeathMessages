package dev.mrshawn.deathmessages.hooks;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.config.Settings;
import dev.mrshawn.deathmessages.config.files.Config;
import dev.mrshawn.deathmessages.config.files.FileStore;
import dev.mrshawn.deathmessages.listeners.PluginMessaging;
import dev.mrshawn.deathmessages.listeners.combatlogx.PlayerUntag;
import dev.mrshawn.deathmessages.listeners.mythicmobs.MobDeath;
import dev.mrshawn.deathmessages.utils.PlatformUtil;
import dev.mrshawn.deathmessages.utils.Util;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;

public class HookInstance {

    private final DeathMessages instance;

    public boolean placeholderAPIEnabled = false;
    public boolean combatLogXAPIEnabled = false;
    public boolean langUtilsEnabled = false;
    public boolean commonVanishPluginsEnabled = false; // TODO: really needs this (to keep consistency with other enabled)? Or just use config boolean?
    public boolean disableI18nDisplay = false;

    public FastStatsHook fastStats;

    public WorldGuardHook worldGuard;
    public boolean worldGuardEnabled;

    public String bungeeServerName; // Own identification name in Bungee
    public String bungeeServerDisplayName;
    public boolean bungeeServerNameRequest = true;
    public boolean bungeeInit = false;

    public DiscordSRVHook discordSRV;
    public boolean discordSRVEnabled = false;

    public MythicMobsHook mythicMobs;
    public boolean mythicmobsEnabled = false;

    public EcoHook eco;
    public boolean ecoEnchantsEnabled = false;

    public HookInstance(DeathMessages pluginInstance) {
        instance = pluginInstance;

        registerHooksOnLoad();
    }

    private void registerHooksOnLoad() {
        final PluginManager pluginManager = Bukkit.getPluginManager();

        if (pluginManager.getPlugin("WorldGuard") != null && FileStore.CONFIG.getBoolean(Config.HOOKS_WORLDGUARD_ENABLED)) {
            try {
                final String version = pluginManager.getPlugin("WorldGuard").getDescription().getVersion();
                final String hookClass;
                if (version.startsWith("7")) {
                    hookClass = "WorldGuard7Hook";
                } else if (version.startsWith("6")) {
                    hookClass = "WorldGuard6Hook";
                } else throw new UnsupportedOperationException();

                worldGuard = (WorldGuardHook) Class.forName(Util.HOOKS_PACKAGE_PREFIX_NAME + hookClass).getConstructor().newInstance();
                worldGuard.registerFlags();
                worldGuardEnabled = true;
            } catch (Throwable t) {
                worldGuardEnabled = false;
                DeathMessages.LOGGER.warn("Failed to hook WorldGuard.", t);
            }
        }
    }

    public void registerHooks() {
        final PluginManager pluginManager = Bukkit.getPluginManager();
        final List<String> hooksName = new ArrayList<>();

        new Metrics(instance, 24145);
        hooksName.add("bStats");

        fastStats = new FastStatsHook();
        fastStats.get().ready();
        hooksName.add("FastStats");

        if (FileStore.CONFIG.getBoolean(Config.HOOKS_BUNGEE_ENABLED)) {
            Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(instance, "BungeeCord");
            Bukkit.getServer().getMessenger().registerIncomingPluginChannel(instance, "BungeeCord", new PluginMessaging());
            if (FileStore.CONFIG.getBoolean(Config.HOOKS_BUNGEE_SERVER_NAME_GET_FROM_BUNGEE)) {
                bungeeInit = true;
            } else {
                bungeeInit = false;
                bungeeServerDisplayName = FileStore.CONFIG.getString(Config.HOOKS_BUNGEE_SERVER_NAME_DISPLAY_NAME);
            }
            hooksName.add("Bungee");
        }

        if (pluginManager.getPlugin("PlaceholderAPI") != null) {
            try {
                new PlaceholderAPIHook(instance).register();
                placeholderAPIEnabled = true;
                hooksName.add("PlaceholderAPI");
            } catch (Throwable t) {
                DeathMessages.LOGGER.warn("Failed to hook PlaceholderAPI.", t);
            }
        }

        if (pluginManager.getPlugin("NBTAPI") != null) {
            // Dreeam - Remove this useless notice in the future.
            hooksName.add("Item-NBT-API");
        }

        if (worldGuardEnabled) {
            hooksName.add("WorldGuard");
        }

        if (pluginManager.getPlugin("DiscordSRV") != null && FileStore.CONFIG.getBoolean(Config.HOOKS_DISCORD_ENABLED)) {
            discordSRV = new DiscordSRVHook();
            discordSRVEnabled = true;
            hooksName.add("DiscordSRV");

            if (Settings.getInstance().getConfig().getBoolean(Config.DISPLAY_I18N_ITEM_NAME.getPath())
                    || Settings.getInstance().getConfig().getBoolean(Config.DISPLAY_I18N_MOB_NAME.getPath())) {
                disableI18nDisplay = true;
                DeathMessages.LOGGER.warn("I18N Display will be disabled automatically, due to incompatible with platform integration related plugins!");
            }
        }

        // Logic binds to PlugMan's impl instead of PlugMan version or Minecraft versions that PlugMan supports (So report if got errors)
        // Use Reflection here to make things easier, since new PlugMan version doesn't have maven repo
        if (pluginManager.isPluginEnabled("PlugMan") && worldGuard != null) {
            Plugin plugManInstance = pluginManager.getPlugin("PlugMan");
            DeathMessages.LOGGER.info("Detected PlugMan and WorldGuard. DeathMessages can't be unloaded using PlugMan now, due to DeathMessages's WorldGuard onLoad hook being enabled!");
            try {
                // After https://github.com/Test-Account666/PlugManX/commit/8245798c8cc511909789b8be5ac2c425af90d092
                if (Util.doesClassExists("bukkit.com.rylinaux.plugman.PlugManBukkit")) {
                    final Class<?> plugManAPIClass = Class.forName("bukkit.com.rylinaux.plugman.api.PlugManAPI");

                    // PlugManAPI#iDoNotWantToBeUnOrReloaded
                    plugManAPIClass.getMethod("iDoNotWantToBeUnOrReloaded", Plugin.class).invoke(null, DeathMessages.getInstance());
                } else {
                    final List<String> ignoredPlugins = (List<String>) plugManInstance.getClass().getMethod("getIgnoredPlugins").invoke(plugManInstance);
                    if (!ignoredPlugins.contains("DeathMessages")) {
                        ignoredPlugins.add("DeathMessages");
                    }
                }
                hooksName.add("PlugMan");
            } catch (Throwable t) {
                DeathMessages.LOGGER.warn("Failed to hook PlugMan.", t);
            }
        }

        if (pluginManager.getPlugin("CombatLogX") != null && FileStore.CONFIG.getBoolean(Config.HOOKS_COMBATLOGX_ENABLED)) {
            combatLogXAPIEnabled = true;
            pluginManager.registerEvents(new PlayerUntag(), instance);
            hooksName.add("CombatLogX");
        }

        if (pluginManager.getPlugin("MythicMobs") != null && FileStore.CONFIG.getBoolean(Config.HOOKS_MYTHICMOBS_ENABLED)) {
            try {
                mythicMobs = new MythicMobsHook();
                mythicmobsEnabled = true;
                pluginManager.registerEvents(new MobDeath(), instance);
                hooksName.add("MythicMobs");
            } catch (Throwable t) {
                DeathMessages.LOGGER.warn("Failed to hook MythicMobs.", t);
            }
        }

        if (pluginManager.getPlugin("eco") != null && pluginManager.getPlugin("EcoEnchants") != null) {
            eco = new EcoHook();
            ecoEnchantsEnabled = true;
            hooksName.add("EcoEnchants");
        }

        if (FileStore.CONFIG.getBoolean(Config.HOOKS_VANISH_COMMON_PLUGINS_ENABLED)) {
            commonVanishPluginsEnabled = true;
            hooksName.add("Common Vanish Plugins");
        }

        if (!disableI18nDisplay && PlatformUtil.isOlderAndEqual(12, 2)) {
            if (Settings.getInstance().getConfig().getBoolean(Config.DISPLAY_I18N_ITEM_NAME.getPath())
                    || Settings.getInstance().getConfig().getBoolean(Config.DISPLAY_I18N_MOB_NAME.getPath())) {
                if (pluginManager.getPlugin("LangUtils") != null) {
                    langUtilsEnabled = true;
                    hooksName.add("LangUtils");
                } else {
                    langUtilsEnabled = false;
                    DeathMessages.LOGGER.warn("You enable the I18N Display feature, you need LangUtils plugin to make this feature works under <=1.12.2");
                    DeathMessages.LOGGER.warn("Turn off I18N Display feature in config, or install LangUtils: https://github.com/MascusJeoraly/LanguageUtils/releases");
                }
            }
        }

        final StringBuilder sb = new StringBuilder("Enabled hooks for: \n");
        for (int i = 0, size = hooksName.size(); i < size; i++) {
            final String name = hooksName.get(i);

            sb.append("- ").append(name);

            if (i < size - 1) {
                sb.append("\n");
            }
        }
        DeathMessages.LOGGER.info(sb.toString());
    }

    public void shutdownFastStats() {
        if (fastStats != null) {
            fastStats.get().shutdown();
        }
    }

    public HookInstance getInstance() {
        return this;
    }
}
