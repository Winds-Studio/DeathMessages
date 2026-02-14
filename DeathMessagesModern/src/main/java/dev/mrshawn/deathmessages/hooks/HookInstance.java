package dev.mrshawn.deathmessages.hooks;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.config.Settings;
import dev.mrshawn.deathmessages.config.files.Config;
import dev.mrshawn.deathmessages.config.files.FileStore;
import dev.mrshawn.deathmessages.listeners.PluginMessaging;
import dev.mrshawn.deathmessages.listeners.combatlogx.PlayerUntag;
import dev.mrshawn.deathmessages.listeners.mythicmobs.MobDeath;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class HookInstance {

    private final DeathMessages instance;

    public boolean placeholderAPIEnabled = false;
    public boolean combatLogXAPIEnabled = false;

    public MythicBukkit mythicMobs = null;
    public boolean mythicmobsEnabled = false;
    public boolean useMythicMobsDeathMessages = false;

    public WorldGuardExtension worldGuardExtension;
    public boolean worldGuardEnabled;

    public String bungeeServerName; // Own identification name in Bungee
    public String bungeeServerDisplayName;
    public boolean bungeeServerNameRequest = true;
    public boolean bungeeInit = false;

    public DiscordSRVExtension discordSRVExtension;
    public boolean discordSRVEnabled = false;

    public EcoExtension ecoExtension;
    public boolean ecoEnchantsEnabled = false;

    // TODO: really needs this (to keep consistency with other enabled)? Or just use config boolean?
    public boolean commonVanishPluginsEnabled = false;

    public boolean disableI18nDisplay = false;

    public HookInstance(DeathMessages pluginInstance) {
        instance = pluginInstance;

        registerHooksOnLoad();
    }

    private void registerHooksOnLoad() {
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null && FileStore.CONFIG.getBoolean(Config.HOOKS_WORLDGUARD_ENABLED)) {
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
                worldGuardEnabled = false;
                DeathMessages.LOGGER.error("Error loading WorldGuardHook. Error: ", e);
            }
        }
    }

    public void registerHooks() {
        if (FileStore.CONFIG.getBoolean(Config.HOOKS_BUNGEE_ENABLED)) {
            Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(instance, "BungeeCord");
            Bukkit.getServer().getMessenger().registerIncomingPluginChannel(instance, "BungeeCord", new PluginMessaging());
            if (FileStore.CONFIG.getBoolean(Config.HOOKS_BUNGEE_SERVER_NAME_GET_FROM_BUNGEE)) {
                bungeeInit = true;
            } else {
                bungeeInit = false;
                bungeeServerDisplayName = FileStore.CONFIG.getString(Config.HOOKS_BUNGEE_SERVER_NAME_DISPLAY_NAME);
            }
            DeathMessages.LOGGER.info("Bungee Hook enabled!");
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPIExtension(instance).register();
            placeholderAPIEnabled = true;
            DeathMessages.LOGGER.info("PlaceholderAPI Hook Enabled!");
        }

        if (Bukkit.getPluginManager().getPlugin("NBTAPI") != null) {
            // Dreeam - Remove this useless notice in the future.
            DeathMessages.LOGGER.info("Item-NBT-API Hook Enabled!");
        }

        if (worldGuardEnabled) {
            DeathMessages.LOGGER.info("WorldGuard Hook Enabled!");
        }

        if (Bukkit.getPluginManager().getPlugin("DiscordSRV") != null && FileStore.CONFIG.getBoolean(Config.HOOKS_DISCORD_ENABLED)) {
            discordSRVExtension = new DiscordSRVExtension();
            discordSRVEnabled = true;
            DeathMessages.LOGGER.info("DiscordSRV Hook Enabled!");

            if (Settings.getInstance().getConfig().getBoolean(Config.DISPLAY_I18N_ITEM_NAME.getPath())
                    || Settings.getInstance().getConfig().getBoolean(Config.DISPLAY_I18N_MOB_NAME.getPath())) {
                disableI18nDisplay = true;
                DeathMessages.LOGGER.warn("I18N Display will be disabled automatically, due to incompatible with platform integration related plugins!");
            }
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlugMan") && worldGuardExtension != null) {
            Plugin plugMan = Bukkit.getPluginManager().getPlugin("PlugMan");
            DeathMessages.LOGGER.info("PlugMan found. Adding this plugin to its ignored plugins list due to WorldGuard hook being enabled!");
            try {
                List<String> ignoredPlugins = (List<String>) plugMan.getClass().getMethod("getIgnoredPlugins").invoke(plugMan);
                if (!ignoredPlugins.contains("DeathMessages")) {
                    ignoredPlugins.add("DeathMessages");
                }
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException exception) {
                DeathMessages.LOGGER.error("Error adding plugin to ignored plugins list: ", exception);
            }
        }

        if (Bukkit.getPluginManager().getPlugin("CombatLogX") != null && FileStore.CONFIG.getBoolean(Config.HOOKS_COMBATLOGX_ENABLED)) {
            combatLogXAPIEnabled = true;
            Bukkit.getPluginManager().registerEvents(new PlayerUntag(), instance);
            DeathMessages.LOGGER.info("CombatLogX Hook Enabled!");
        }

        if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null && FileStore.CONFIG.getBoolean(Config.HOOKS_MYTHICMOBS_ENABLED)) {
            mythicMobs = MythicBukkit.inst();
            mythicmobsEnabled = true;
            useMythicMobsDeathMessages = FileStore.CONFIG.getBoolean(Config.HOOKS_MYTHICMOBS_USE_MYTHICMOBS_DEATH_MESSAGE);
            Bukkit.getPluginManager().registerEvents(new MobDeath(), instance);
            DeathMessages.LOGGER.info("MythicMobs Hook Enabled!");
        }

        if (Bukkit.getPluginManager().getPlugin("eco") != null && Bukkit.getPluginManager().getPlugin("EcoEnchants") != null) {
            ecoExtension = new EcoExtension();
            ecoEnchantsEnabled = true;
            DeathMessages.LOGGER.info("EcoEnchants Hook Enabled!");
        }

        if (FileStore.CONFIG.getBoolean(Config.HOOKS_VANISH_COMMON_PLUGINS_ENABLED)) {
            commonVanishPluginsEnabled = true;
            DeathMessages.LOGGER.info("Common Vanish Plugins Hook Enabled!");
        }
    }

    public HookInstance getInstance() {
        return this;
    }
}
