package dev.mrshawn.deathmessages;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.command.deathmessages.CommandManager;
import dev.mrshawn.deathmessages.command.deathmessages.TabCompleter;
import dev.mrshawn.deathmessages.command.deathmessages.alias.CommandDeathMessagesToggle;
import dev.mrshawn.deathmessages.config.ConfigManager;
import dev.mrshawn.deathmessages.config.Settings;
import dev.mrshawn.deathmessages.hooks.DiscordBotAPIExtension;
import dev.mrshawn.deathmessages.hooks.DiscordSRVExtension;
import dev.mrshawn.deathmessages.hooks.Metrics;
import dev.mrshawn.deathmessages.hooks.PlaceholderAPIExtension;
import dev.mrshawn.deathmessages.listeners.*;
import dev.mrshawn.deathmessages.listeners.customlisteners.BlockExplosion;
import dev.mrshawn.deathmessages.listeners.customlisteners.BroadcastEntityDeathListener;
import dev.mrshawn.deathmessages.listeners.customlisteners.BroadcastPlayerDeathListener;
import dev.mrshawn.deathmessages.listeners.mythicmobs.MobDeath;
import dev.mrshawn.deathmessages.utils.EventUtils;
import dev.mrshawn.deathmessages.worldguard.WorldGuard7Extension;
import dev.mrshawn.deathmessages.worldguard.WorldGuardExtension;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;

public class DeathMessages extends JavaPlugin {

    private static DeathMessages instance;

    public boolean placeholderAPIEnabled = false;
    public boolean combatLogXAPIEnabled = false;

    public MythicBukkit mythicMobs = null;
    public boolean mythicmobsEnabled = false;


    public static String bungeeServerName;
    public static boolean bungeeServerNameRequest = true;
    public static boolean bungeeInit = false;


    public static WorldGuardExtension worldGuardExtension;
    public static boolean worldGuardEnabled;

    public static DiscordBotAPIExtension discordBotAPIExtension;
    public static DiscordSRVExtension discordSRVExtension;

    private static EventPriority eventPriority = EventPriority.HIGH;


    public void onEnable() {

        //Logger log = (Logger) LogManager.getRootLogger();
        //log.addAppender(new SupportLogger());
        initializeListeners();
        initializeCommands();
        initializeHooks();
        initializeOnlinePlayers();
        checkGameRules();
        new Metrics(this, 12365);
        getLogger().log(Level.INFO, "bStats Hook Enabled!");
    }

    public void onLoad() {
        instance = this;
        initializeConfigs();
        initializeHooksOnLoad();
    }

    public void onDisable() {
        instance = null;
    }

    public static String serverVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",")
                .split(",")[3];
    }

    public static int majorVersion() {
        return Integer.parseInt(serverVersion().replace("1_", "")
                .replaceAll("_R\\d", "").replaceAll("v", ""));
    }

    private void initializeConfigs() {
        ConfigManager.getInstance().initialize();

        String eventPriority = Settings.getInstance().getConfig().getString("Death-Listener-Priority");
        DeathMessages.eventPriority = EventPriority.valueOf(eventPriority.toUpperCase());
    }

    private void initializeListeners() {
        EventUtils.registerEvents(
                // Self
                new BroadcastPlayerDeathListener(),
                new BroadcastEntityDeathListener(),
                // Bukkit
                new BlockExplosion(),
                new EntityDamage(),
                new EntityDamageByBlock(),
                new EntityDamageByEntity(),
                new EntityDeath(),
                new InteractEvent(),
                new OnChat(),
                new OnJoin(),
                new OnMove(),
                new OnQuit(),
                new PlayerDeath()
        );
    }

    private void initializeCommands() {
        CommandManager cm = new CommandManager();
        cm.initializeSubCommands();
        getCommand("deathmessages").setExecutor(cm);
        getCommand("deathmessages").setTabCompleter(new TabCompleter());
        getCommand("deathmessagestoggle").setExecutor(new CommandDeathMessagesToggle());
    }

    private void initializeHooks() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPIExtension(this).register();
            placeholderAPIEnabled = true;
            getLogger().log(Level.INFO, "PlaceholderAPI Hook Enabled!");
        }

        if (worldGuardEnabled) {
            getLogger().log(Level.INFO, "WorldGuard Hook Enabled!");
        }

        if (Bukkit.getPluginManager().getPlugin("DiscordBotAPI") != null
                && Settings.getInstance().getConfig().getBoolean("Hooks.Discord.Enabled")) {
            discordBotAPIExtension = new DiscordBotAPIExtension();
            getLogger().log(Level.INFO, "DiscordBotAPI Hook Enabled!");
        }

        if (Bukkit.getPluginManager().getPlugin("DiscordSRV") != null
                && Settings.getInstance().getConfig().getBoolean("Hooks.Discord.Enabled")) {
            discordSRVExtension = new DiscordSRVExtension();
            getLogger().log(Level.INFO, "DiscordSRV Hook Enabled!");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlugMan") && worldGuardExtension != null) {
            Plugin plugMan = Bukkit.getPluginManager().getPlugin("PlugMan");
            getLogger().log(Level.INFO, "PlugMan found. Adding this plugin to its ignored plugins list due to WorldGuard hook being enabled!");
            try {
                List<String> ignoredPlugins = (List<String>) plugMan.getClass().getMethod("getIgnoredPlugins")
                        .invoke(plugMan);
                if (!ignoredPlugins.contains("DeathMessages")) {
                    ignoredPlugins.add("DeathMessages");
                }
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException exception) {
                getLogger().log(Level.SEVERE, "Error adding plugin to ignored plugins list: " +
                        exception.getMessage());
            }
        }

//
//        if (Bukkit.getPluginManager().getPlugin("CombatLogX") != null) {
//            combatLogXAPIEnabled = true;
//            Bukkit.getPluginManager().registerEvents(new PlayerUntag(), this);
//            getLogger().log(Level.INFO, "CombatLogX Hook Enabled!");
//        }

        if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null
                && Settings.getInstance().getConfig().getBoolean("Hooks.MythicMobs.Enabled")) {
            mythicMobs = MythicBukkit.inst();
            mythicmobsEnabled = true;
            getLogger().log(Level.INFO, "MythicMobs Hook Enabled!");
            Bukkit.getPluginManager().registerEvents(new MobDeath(), this);
        }

        if (Settings.getInstance().getConfig().getBoolean("Hooks.Bungee.Enabled")) {
            Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            Bukkit.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new PluginMessaging());
            getLogger().log(Level.INFO, "Bungee Hook enabled!");
            if (Settings.getInstance().getConfig().getBoolean("Hooks.Bungee.Server-Name.Get-From-Bungee")) {
                bungeeInit = true;
            } else {
                bungeeInit = false;
                bungeeServerName = Settings.getInstance().getConfig().getString("Hooks.Bungee.Server-Name.Display-Name");
            }
        }
    }

    private void initializeHooksOnLoad() {
        if (Settings.getInstance().getConfig().getBoolean("Hooks.WorldGuard.Enabled")) {
            try {
                final WorldGuardPlugin worldGuardPlugin = WorldGuardPlugin.inst();
                if (worldGuardPlugin == null) throw new Exception();
                final String version = worldGuardPlugin.getDescription().getVersion();
                if (version.startsWith("7")) {
                    worldGuardExtension = new WorldGuard7Extension();
                    worldGuardExtension.registerFlags();
                } else if (version.startsWith("6")) {
                    //worldGuardExtension = new WorldGuard6Extension();
                    worldGuardExtension.registerFlags();
                } else throw new Exception();
                worldGuardEnabled = true;
            } catch (final Throwable e) {
                getLogger().log(Level.SEVERE, "Error loading WorldGuardHook. Error: " + e.getMessage());
                worldGuardEnabled = false;
            }
        }
    }

    private void initializeOnlinePlayers() {
        Bukkit.getOnlinePlayers().forEach(PlayerManager::new);
    }

    private void checkGameRules() {
        if (Settings.getInstance().getConfig().getBoolean("Disable-Default-Messages") && majorVersion() >= 13) {
            for (World world : Bukkit.getWorlds()) {
                if (world.getGameRuleValue(GameRule.SHOW_DEATH_MESSAGES).equals(true)) {
                    world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
                }
            }
        }
    }

    public static EventPriority getEventPriority() {
        return eventPriority;
    }

    public static DeathMessages getInstance() {
        return instance;
    }

}
