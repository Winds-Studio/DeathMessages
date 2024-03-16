package dev.mrshawn.deathmessages.config;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.config.modules.UserData;
import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Sound;
import org.slf4j.event.Level;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ConfigManager {

    public final File backupDirectory = new File(DeathMessages.getInstance().getDataFolder(), "Backups");

    public static void saveConfig(ConfigFile config) {
        try {
            config.save();
        } catch (Exception e) {
            DeathMessages.LOGGER.error("Failed to save config file!", e);
        }
    }

    private void structureConfig() {
        config.addDefault("config-version", 1.00);
        createTitledSection("Language", "language");
        createTitledSection("General", "general");
        createTitledSection("Patches", "patches");
        createTitledSection("Preventions", "preventions");
        createTitledSection("Lag Preventions", "lag-preventions");
        createTitledSection("Dupe Preventions", "dupe-preventions");
        createTitledSection("Combat", "combat");
        createTitledSection("Illegals", "illegals");
        createTitledSection("Chunk Limits", "chunk-limits");
        createTitledSection("Bedrock", "bedrock");
        createTitledSection("Elytra", "elytra");
        createTitledSection("Chat", "chat");
        createTitledSection("Miscellaneous", "misc");
    }

    public void createTitledSection(ConfigFile config, String title, String path) {
        config.addSection(title);
        config.addDefault(path, null);
    }

    public boolean getBoolean(ConfigFile config, String path, boolean def, String comment) {
        config.addDefault(path, def, comment);
        return config.getBoolean(path, def);
    }

    public boolean getBoolean(ConfigFile config, String path, boolean def) {
        config.addDefault(path, def);
        return config.getBoolean(path, def);
    }

    public String getString(ConfigFile config, String path, String def, String comment) {
        config.addDefault(path, def, comment);
        return config.getString(path, def);
    }

    public String getString(ConfigFile config, String path, String def) {
        config.addDefault(path, def);
        return config.getString(path, def);
    }

    public double getDouble(ConfigFile config, String path, double def, String comment) {
        config.addDefault(path, def, comment);
        return config.getDouble(path, def);
    }

    public double getDouble(ConfigFile config, String path, double def) {
        config.addDefault(path, def);
        return config.getDouble(path, def);
    }

    public int getInt(ConfigFile config, String path, int def, String comment) {
        config.addDefault(path, def, comment);
        return config.getInteger(path, def);
    }

    public int getInt(ConfigFile config, String path, int def) {
        config.addDefault(path, def);
        return config.getInteger(path, def);
    }

    public List<String> getList(ConfigFile config, String path, List<String> def, String comment) {
        config.addDefault(path, def, comment);
        return config.getStringList(path);
    }

    public List<String> getList(ConfigFile config, String path, List<String> def) {
        config.addDefault(path, def);
        return config.getStringList(path);
    }

    public ConfigSection getConfigSection(ConfigFile config, String path, Map<String, Object> defaultKeyValue) {
        config.addDefault(path, null);
        config.makeSectionLenient(path);
        defaultKeyValue.forEach((string, object) -> config.addExample(path + "." + string, object));
        return config.getConfigSection(path);
    }

    public ConfigSection getConfigSection(ConfigFile config, String path, Map<String, Object> defaultKeyValue, String comment) {
        config.addDefault(path, null, comment);
        config.makeSectionLenient(path);
        defaultKeyValue.forEach((string, object) -> config.addExample(path + "." + string, object));
        return config.getConfigSection(path);
    }

    public void addComment(ConfigFile config, String path, String comment) {
        config.addComment(path, comment);
    }

    public String backup(boolean excludeUserData) {
        if (!backupDirectory.exists()) {
            backupDirectory.mkdir();
        }
        String randomCode = RandomStringUtils.randomNumeric(4);
        File backupDir = new File(backupDirectory, randomCode);
        backupDir.mkdir();
        try {
            FileUtils.copyFileToDirectory(EntityDeathMessages.getInstance().file, backupDir);
        } catch (IOException e) {
            LogManager.getLogger().error(e);
        }
        try {
            FileUtils.copyFileToDirectory(Gangs.getInstance().file, backupDir);
        } catch (IOException e) {
            LogManager.getLogger().error(e);
        }
        try {
            FileUtils.copyFileToDirectory(Messages.getInstance().file, backupDir);
        } catch (IOException e) {
            LogManager.getLogger().error(e);
        }
        try {
            FileUtils.copyFileToDirectory(PlayerDeathMessages.getInstance().file, backupDir);
        } catch (IOException e) {
            LogManager.getLogger().error(e);
        }
        try {
            FileUtils.copyFileToDirectory(Settings.getInstance().getFile(), backupDir);
        } catch (IOException e) {
            LogManager.getLogger().error(e);
        }
        if (!excludeUserData) {
            try {
                FileUtils.copyFileToDirectory(UserData.getInstance().file, backupDir);
            } catch (IOException e) {
                LogManager.getLogger().error(e);
            }
        }
        return randomCode;
    }

    /*
        Returns true if the operation was successful.
        Returns false if the operation was not successful.
     */
    public boolean restore(String code, boolean excludeUserData) {
        File backupDir = new File(backupDirectory, code);
        if (!backupDir.exists()) {
            return false;
        }
        try {
            String fileName = EntityDeathMessages.getInstance().fileName;
            File f = new File(backupDir, fileName + ".yml");
            if (EntityDeathMessages.getInstance().file.delete()) {
                FileUtils.copyFileToDirectory(f, DeathMessages.getInstance().getDataFolder());
            } else {
                LogManager.getLogger().error("COULD NOT RESTORE " + fileName + ".");
            }
        } catch (IOException e) {
            LogManager.getLogger().error(e);
        }
        try {
            String fileName = Gangs.getInstance().fileName;
            File f = new File(backupDir, fileName + ".yml");
            if (Gangs.getInstance().file.delete()) {
                FileUtils.copyFileToDirectory(f, DeathMessages.getInstance().getDataFolder());
            } else {
                LogManager.getLogger().error("COULD NOT RESTORE " + fileName + ".");
            }
        } catch (IOException e) {
            LogManager.getLogger().error(e);
        }
        try {
            String fileName = Messages.getInstance().fileName;
            File f = new File(backupDir, fileName + ".yml");
            if (Messages.getInstance().file.delete()) {
                FileUtils.copyFileToDirectory(f, DeathMessages.getInstance().getDataFolder());
            } else {
                LogManager.getLogger().error("COULD NOT RESTORE " + fileName + ".");
            }
        } catch (IOException e) {
            LogManager.getLogger().error(e);
        }
        try {
            String fileName = PlayerDeathMessages.getInstance().fileName;
            File f = new File(backupDir, fileName + ".yml");
            if (PlayerDeathMessages.getInstance().file.delete()) {
                FileUtils.copyFileToDirectory(f, DeathMessages.getInstance().getDataFolder());
            } else {
                LogManager.getLogger().error("COULD NOT RESTORE " + fileName + ".");
            }
        } catch (IOException e) {
            LogManager.getLogger().error(e);
        }
        try {
            String fileName = Settings.getInstance().fileName;
            File f = new File(backupDir, fileName + ".yml");
            if (Settings.getInstance().getFile().delete()) {
                FileUtils.copyFileToDirectory(f, DeathMessages.getInstance().getDataFolder());
            } else {
                LogManager.getLogger().error("COULD NOT RESTORE " + fileName + ".");
            }
        } catch (IOException e) {
            LogManager.getLogger().error(e);
        }
        if (!excludeUserData) {
            try {
                String fileName = UserData.getInstance().fileName;
                File f = new File(backupDir, fileName + ".yml");
                if (UserData.getInstance().file.delete()) {
                    FileUtils.copyFileToDirectory(f, DeathMessages.getInstance().getDataFolder());
                } else {
                    LogManager.getLogger().error("COULD NOT RESTORE " + fileName + ".");
                }
            } catch (IOException e) {
                LogManager.getLogger().error(e);
            }
        }
        dev.mrshawn.deathmessages.config.legacy.ConfigManager.getInstance().reload();
        return true;
    }

    public void copy(InputStream in, File file) {
        try {
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            LogManager.getLogger().error(e);
        }
    }
}