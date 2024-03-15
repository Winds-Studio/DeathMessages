package dev.mrshawn.deathmessages.config;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.config.modules.EntityDeathMessages;
import dev.mrshawn.deathmessages.config.modules.Gangs;
import dev.mrshawn.deathmessages.config.modules.Messages;
import dev.mrshawn.deathmessages.config.modules.PlayerDeathMessages;
import dev.mrshawn.deathmessages.config.modules.Settings;

public class Config {

    private static ConfigManager<?> entityDeathMessagesManager, gangsManager, messagesManager, playerDeathMessagesManager, settingsManager;

    public static EntityDeathMessages entityDeathMessages;
    public static Gangs gangs;
    public static Messages messages;
    public static PlayerDeathMessages playerDeathMessages;
    public static Settings settings;

    public static void init() {
        entityDeathMessagesManager = ConfigManager.create(DeathMessages.getInstance().getDataFolder().toPath(), "EntityDeathMessages.yml", EntityDeathMessages.class);
        gangsManager = ConfigManager.create(DeathMessages.getInstance().getDataFolder().toPath(), "Gangs.yml", Gangs.class);
        messagesManager = ConfigManager.create(DeathMessages.getInstance().getDataFolder().toPath(), "Messages.yml", Messages.class);
        playerDeathMessagesManager = ConfigManager.create(DeathMessages.getInstance().getDataFolder().toPath(), "PlayerDeathMessages.yml", PlayerDeathMessages.class);
        settingsManager = ConfigManager.create(DeathMessages.getInstance().getDataFolder().toPath(), "Settings.yml", Settings.class);

        reload();

        entityDeathMessages = (EntityDeathMessages) entityDeathMessagesManager.getConfigData();
        gangs = (Gangs) gangsManager.getConfigData();
        messages = (Messages) messagesManager.getConfigData();
        playerDeathMessages = (PlayerDeathMessages) playerDeathMessagesManager.getConfigData();
        settings = (Settings) settingsManager.getConfigData();
    }

    public static void reload() {
        entityDeathMessagesManager.reloadConfig();
        gangsManager.reloadConfig();
        messagesManager.reloadConfig();
        playerDeathMessagesManager.reloadConfig();
        settingsManager.reloadConfig();
    }
}
