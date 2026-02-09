package dev.mrshawn.deathmessages;

import com.tcoded.folialib.FoliaLib;
import dev.mrshawn.deathmessages.api.PlayerCtx;
import dev.mrshawn.deathmessages.commands.CommandManager;
import dev.mrshawn.deathmessages.commands.TabCompleter;
import dev.mrshawn.deathmessages.commands.alias.CommandDeathMessagesToggle;
import dev.mrshawn.deathmessages.config.ConfigManager;
import dev.mrshawn.deathmessages.config.Settings;
import dev.mrshawn.deathmessages.config.files.Config;
import dev.mrshawn.deathmessages.config.files.FileStore;
import dev.mrshawn.deathmessages.hooks.HookInstance;
import dev.mrshawn.deathmessages.listeners.EntityDamage;
import dev.mrshawn.deathmessages.listeners.EntityDamageByBlock;
import dev.mrshawn.deathmessages.listeners.EntityDamageByEntity;
import dev.mrshawn.deathmessages.listeners.EntityDeath;
import dev.mrshawn.deathmessages.listeners.OnCommand;
import dev.mrshawn.deathmessages.listeners.OnConnection;
import dev.mrshawn.deathmessages.listeners.OnInteract;
import dev.mrshawn.deathmessages.listeners.OnMove;
import dev.mrshawn.deathmessages.listeners.OnQuit;
import dev.mrshawn.deathmessages.listeners.PlayerDeath;
import dev.mrshawn.deathmessages.listeners.customlisteners.BlockExplosion;
import dev.mrshawn.deathmessages.listeners.customlisteners.BroadcastEntityDeathListener;
import dev.mrshawn.deathmessages.listeners.customlisteners.BroadcastPlayerDeathListener;
import dev.mrshawn.deathmessages.utils.ComponentUtil;
import dev.mrshawn.deathmessages.utils.EventUtil;
import dev.mrshawn.deathmessages.utils.PlatformUtil;
import dev.mrshawn.deathmessages.utils.Updater;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

public class DeathMessages extends JavaPlugin {

    public static final Logger LOGGER = LogManager.getLogger(DeathMessages.class.getSimpleName());
    private static DeathMessages instance;
    private static HookInstance hookInstance;
    public final FoliaLib foliaLib = new FoliaLib(this);

    private static EventPriority eventPriority = EventPriority.HIGH;

    @Override
    public void onEnable() {
        PlatformUtil.init();
        initListeners();
        initCommands();
        getHooks().registerHooks();
        initOnlinePlayers();
        checkGameRules();

        new Metrics(instance, 24145); // Move to single class for more Metrics function
        LOGGER.info("bStats Hook Enabled!");
        ComponentUtil.sendConsoleMessage(Component.text("DeathMessages " + instance.getDescription().getVersion() + " successfully loaded!", NamedTextColor.GOLD));
        checkUpdate();
    }

    @Override
    public void onLoad() {
        instance = this;

        initConfig();
        initHooks();
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public void initConfig() {
        ConfigManager.getInstance().initialize();

        DeathMessages.eventPriority = EventPriority.valueOf(
                FileStore.CONFIG.getString(Config.DEATH_LISTENER_PRIORITY).toUpperCase()
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
                new OnConnection(),
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
        hookInstance = new HookInstance(this);
    }

    private void initOnlinePlayers() {
        for (Player player : getServer().getOnlinePlayers()) {
            PlayerCtx.create(new PlayerCtx(player));
        }
    }

    private void checkGameRules() {
        if (FileStore.CONFIG.getBoolean(Config.DISABLE_DEFAULT_MESSAGES)) {
            foliaLib.getScheduler().runNextTick(task -> {
                for (World world : Bukkit.getWorlds()) {
                    world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
                }
            });
        }
    }

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

    public static HookInstance getHooks() {
        return hookInstance.getInstance();
    }

    public static EventPriority getEventPriority() {
        return eventPriority;
    }
}
