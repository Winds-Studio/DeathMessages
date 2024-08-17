package dev.mrshawn.deathmessages.config;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.utils.Util;
import org.apache.commons.io.FileUtils;

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
		try {
			FileUtils.copyFileToDirectory(EntityDeathMessages.getInstance().file, backupDir);
		} catch (IOException e) {
			DeathMessages.LOGGER.error(e);
		}
		try {
			FileUtils.copyFileToDirectory(Gangs.getInstance().file, backupDir);
		} catch (IOException e) {
			DeathMessages.LOGGER.error(e);
		}
		try {
			FileUtils.copyFileToDirectory(Messages.getInstance().file, backupDir);
		} catch (IOException e) {
			DeathMessages.LOGGER.error(e);
		}
		try {
			FileUtils.copyFileToDirectory(PlayerDeathMessages.getInstance().file, backupDir);
		} catch (IOException e) {
			DeathMessages.LOGGER.error(e);
		}
		try {
			FileUtils.copyFileToDirectory(Settings.getInstance().getFile(), backupDir);
		} catch (IOException e) {
			DeathMessages.LOGGER.error(e);
		}
		if (!excludeUserData) {
			try {
				FileUtils.copyFileToDirectory(UserData.getInstance().file, backupDir);
			} catch (IOException e) {
				DeathMessages.LOGGER.error(e);
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
				DeathMessages.LOGGER.error("COULD NOT RESTORE {}.", fileName);
			}
		} catch (IOException e) {
			DeathMessages.LOGGER.error(e);
		}
		try {
			String fileName = Gangs.getInstance().fileName;
			File f = new File(backupDir, fileName + ".yml");
			if (Gangs.getInstance().file.delete()) {
				FileUtils.copyFileToDirectory(f, DeathMessages.getInstance().getDataFolder());
			} else {
				DeathMessages.LOGGER.error("COULD NOT RESTORE {}.", fileName);
			}
		} catch (IOException e) {
			DeathMessages.LOGGER.error(e);
		}
		try {
			String fileName = Messages.getInstance().fileName;
			File f = new File(backupDir, fileName + ".yml");
			if (Messages.getInstance().file.delete()) {
				FileUtils.copyFileToDirectory(f, DeathMessages.getInstance().getDataFolder());
			} else {
				DeathMessages.LOGGER.error("COULD NOT RESTORE {}.", fileName);
			}
		} catch (IOException e) {
			DeathMessages.LOGGER.error(e);
		}
		try {
			String fileName = PlayerDeathMessages.getInstance().fileName;
			File f = new File(backupDir, fileName + ".yml");
			if (PlayerDeathMessages.getInstance().file.delete()) {
				FileUtils.copyFileToDirectory(f, DeathMessages.getInstance().getDataFolder());
			} else {
				DeathMessages.LOGGER.error("COULD NOT RESTORE {}.", fileName);
			}
		} catch (IOException e) {
			DeathMessages.LOGGER.error(e);
		}
		try {
			String fileName = Settings.getInstance().fileName;
			File f = new File(backupDir, fileName + ".yml");
			if (Settings.getInstance().getFile().delete()) {
				FileUtils.copyFileToDirectory(f, DeathMessages.getInstance().getDataFolder());
			} else {
				DeathMessages.LOGGER.error("COULD NOT RESTORE {}.", fileName);
			}
		} catch (IOException e) {
			DeathMessages.LOGGER.error(e);
		}
		if (!excludeUserData) {
			try {
				String fileName = UserData.getInstance().fileName;
				File f = new File(backupDir, fileName + ".yml");
				if (UserData.getInstance().file.delete()) {
					FileUtils.copyFileToDirectory(f, DeathMessages.getInstance().getDataFolder());
				} else {
					DeathMessages.LOGGER.error("COULD NOT RESTORE {}.", fileName);
				}
			} catch (IOException e) {
				DeathMessages.LOGGER.error(e);
			}
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
}
