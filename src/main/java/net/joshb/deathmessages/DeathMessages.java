package net.joshb.deathmessages;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import io.lumine.mythic.bukkit.MythicBukkit;
import net.joshb.deathmessages.api.PlayerManager;
import net.joshb.deathmessages.command.deathmessages.CommandManager;
import net.joshb.deathmessages.command.deathmessages.TabCompleter;
import net.joshb.deathmessages.command.deathmessages.alias.CommandDeathMessagesToggle;
import net.joshb.deathmessages.config.ConfigManager;
import net.joshb.deathmessages.config.Settings;
import net.joshb.deathmessages.hook.*;
import net.joshb.deathmessages.listener.*;
import net.joshb.deathmessages.listener.customlisteners.BlockExplosion;
import net.joshb.deathmessages.listener.customlisteners.BroadcastEntityDeathListener;
import net.joshb.deathmessages.listener.customlisteners.BroadcastPlayerDeathListener;
import net.joshb.deathmessages.listener.mythicmobs.MobDeath;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;
import net.joshb.deathmessages.worldguard.WorldGuard7Extension;
import net.joshb.deathmessages.worldguard.WorldGuardExtension;

public class DeathMessages extends JavaPlugin {

    public static DeathMessages plugin;

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

    public static EventPriority eventPriority = EventPriority.HIGH;


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
        plugin = this;
        initializeConfigs();
        initializeHooksOnLoad();
    }

    public void onDisable() {

        plugin = null;
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

        String eventPri = Settings.getInstance().getConfig().getString("Death-Listener-Priority");
        if (eventPri.equalsIgnoreCase("LOWEST")) {
            eventPriority = EventPriority.LOWEST;
        } else if (eventPri.equalsIgnoreCase("LOW")) {
            eventPriority = EventPriority.LOW;
        } else if (eventPri.equalsIgnoreCase("NORMAL")) {
            eventPriority = EventPriority.NORMAL;
        } else if (eventPri.equalsIgnoreCase("HIGH")) {
            eventPriority = EventPriority.HIGH;
        } else if (eventPri.equalsIgnoreCase("HIGHEST")) {
            eventPriority = EventPriority.HIGHEST;
        } else if (eventPri.equalsIgnoreCase("MONITOR")) {
            eventPriority = EventPriority.HIGH;
        } else {
            eventPriority = EventPriority.HIGH;
        }
    }

    private void initializeListeners() {
        //Custom Events
        Bukkit.getPluginManager().registerEvents(new BroadcastPlayerDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new BroadcastEntityDeathListener(), this);
        //Bukkits events
        Bukkit.getPluginManager().registerEvents(new BlockExplosion(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamage(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamageByBlock(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamageByEntity(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDeath(), this);
        Bukkit.getPluginManager().registerEvents(new InteractEvent(), this);
        Bukkit.getPluginManager().registerEvents(new OnChat(), this);
        Bukkit.getPluginManager().registerEvents(new OnJoin(), this);
        Bukkit.getPluginManager().registerEvents(new OnMove(), this);
        Bukkit.getPluginManager().registerEvents(new OnQuit(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeath(), this);
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
        for (Player p : Bukkit.getOnlinePlayers()) {
            new PlayerManager(p);
        }
    }

    private void checkGameRules() {
        if (Settings.getInstance().getConfig().getBoolean("Disable-Default-Messages") && majorVersion() >= 13) {
            for (World w : Bukkit.getWorlds()) {
                if (w.getGameRuleValue(GameRule.SHOW_DEATH_MESSAGES).equals(true)) {
                    w.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
                }
            }
        }
    }

    public static EventPriority getEventPriority() {
        return eventPriority;
    }
}
