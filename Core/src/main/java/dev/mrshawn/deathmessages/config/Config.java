package dev.mrshawn.deathmessages.config;

import dev.mrshawn.deathmessages.DeathMessages;
import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import java.io.File;

public class Config {

    private static ConfigFile entityDeathMessages, gangs, messages, playerDeathMessages, settings;

    public static void load() {
        File configFolder = DeathMessages.getInstance().getDataFolder();

        if (!configFolder.exists() && !configFolder.mkdir()) {
            DeathMessages.LOGGER.error("Failed to create plugin folder.");
        }

        try {
            entityDeathMessages = ConfigFile.loadConfig(new File(configFolder, "EntityDeathMessages.yml"));
            gangs = ConfigFile.loadConfig(new File(configFolder, "Gangs.yml"));
            messages = ConfigFile.loadConfig(new File(configFolder, "Messages.yml"));
            playerDeathMessages = ConfigFile.loadConfig(new File(configFolder, "PlayerDeathMessages.yml"));
            settings = ConfigFile.loadConfig(new File(configFolder, "Settings.yml"));
        } catch (Exception e) {
            DeathMessages.LOGGER.error("Config load failed.", e);
        }

        Config.save();
    }

    public static void save() {
        ConfigManager.saveConfig(entityDeathMessages);
        ConfigManager.saveConfig(gangs);
        ConfigManager.saveConfig(messages);
        ConfigManager.saveConfig(playerDeathMessages);
        ConfigManager.saveConfig(settings);
    }
}
