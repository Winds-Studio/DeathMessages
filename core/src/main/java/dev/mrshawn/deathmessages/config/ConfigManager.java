package dev.mrshawn.deathmessages.config;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.utils.Util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ConfigManager {

    public ConfigManager() {
    }

    private static final ConfigManager instance = new ConfigManager();

    public static ConfigManager getInstance() {
        return instance;
    }

    public final File backupDirectory = new File(DeathMessages.getInstance().getDataFolder(), "Backups");

    public void initialize() {
        if (!DeathMessages.getInstance().getDataFolder().exists()) {
            DeathMessages.getInstance().getDataFolder().mkdir();
        }
        EntityDeathMessages.getInstance().initialize();
        Gangs.getInstance().initialize();
        Messages.getInstance().initialize();
        PlayerDeathMessages.getInstance().initialize();
        Settings.getInstance().initialize();
        UserData.getInstance().initialize();
    }

    public void reload() {
        EntityDeathMessages.getInstance().reload();
        Gangs.getInstance().reload();
        Messages.getInstance().reload();
        PlayerDeathMessages.getInstance().reload();
        Settings.getInstance().reload();
    }

    public String backup(boolean excludeUserData) {
        if (!backupDirectory.exists()) {
            backupDirectory.mkdir();
        }
        String randomCode = Util.randomNumeric(4);
        File backupDir = new File(backupDirectory, randomCode);
        backupDir.mkdir();

        backupFile(EntityDeathMessages.getInstance().file, backupDir);
        backupFile(Gangs.getInstance().file, backupDir);
        backupFile(Messages.getInstance().file, backupDir);
        backupFile(PlayerDeathMessages.getInstance().file, backupDir);
        backupFile(Settings.getInstance().getFile(), backupDir);

        if (!excludeUserData) {
            backupFile(UserData.getInstance().file, backupDir);
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

        restoreFile(
                new File(backupDir, EntityDeathMessages.getInstance().fileName + ".yml"),
                EntityDeathMessages.getInstance().file,
                EntityDeathMessages.getInstance().fileName
        );

        restoreFile(
                new File(backupDir, Gangs.getInstance().fileName + ".yml"),
                Gangs.getInstance().file,
                Gangs.getInstance().fileName
        );

        restoreFile(
                new File(backupDir, Messages.getInstance().fileName + ".yml"),
                Messages.getInstance().file,
                Messages.getInstance().fileName
        );

        restoreFile(
                new File(backupDir, PlayerDeathMessages.getInstance().fileName + ".yml"),
                PlayerDeathMessages.getInstance().file,
                PlayerDeathMessages.getInstance().fileName
        );

        restoreFile(
                new File(backupDir, Settings.getInstance().fileName + ".yml"),
                Settings.getInstance().getFile(),
                Settings.getInstance().fileName
        );

        if (!excludeUserData) {
            restoreFile(
                    new File(backupDir, UserData.getInstance().fileName + ".yml"),
                    UserData.getInstance().file,
                    UserData.getInstance().fileName
            );
        }

        ConfigManager.getInstance().reload();
        return true;
    }

    public void copy(InputStream in, File file) {
        try {
            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            DeathMessages.LOGGER.error(e);
        }
    }

    private void backupFile(File file, File backupDir) {
        try {
            Files.copy(
                    file.toPath(),
                    backupDir.toPath().resolve(file.getName()),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            DeathMessages.LOGGER.error(e);
        }
    }

    private void restoreFile(File source, File target, String fileName) {
        try {
            if (target.delete()) {
                Files.copy(
                        source.toPath(),
                        target.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                );
            } else {
                DeathMessages.LOGGER.error("COULD NOT RESTORE {}.", fileName);
            }
        } catch (IOException e) {
            DeathMessages.LOGGER.error(e);
        }
    }
}
