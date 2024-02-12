package dev.mrshawn.deathmessages.config;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.utils.CommentedConfiguration;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.util.Date;

public class Gangs {

	public final String fileName = "Gangs";

	CommentedConfiguration config;

	File file;

	public Gangs() {
	}

	private static final Gangs instance = new Gangs();

	public static Gangs getInstance() {
		return instance;
	}

	public CommentedConfiguration getConfig() {
		return config;
	}

	public void save() {
		try {
			config.save(file);
		} catch (Exception e) {
			File f = new File(DeathMessages.getInstance().getDataFolder(), fileName + ".broken." + new Date().getTime());
			LogManager.getLogger().error("Could not save: " + fileName + ".yml");
			LogManager.getLogger().error("Regenerating file and renaming the current file to: " + f.getName());
			LogManager.getLogger().error("You can try fixing the file with a yaml parser online!");
			file.renameTo(f);
			initialize();
		}
	}

	public void reload() {
		try {
			config = CommentedConfiguration.loadConfiguration(file);
		} catch (Exception e) {
			File f = new File(DeathMessages.getInstance().getDataFolder(), fileName + ".broken." + new Date().getTime());
			LogManager.getLogger().error("Could not reload: " + fileName + ".yml");
			LogManager.getLogger().error("Regenerating file and renaming the current file to: " + f.getName());
			LogManager.getLogger().error("You can try fixing the file with a yaml parser online!");
			file.renameTo(f);
			initialize();
		}
	}

	public void initialize() {
		file = new File(DeathMessages.getInstance().getDataFolder(), fileName + ".yml");

		if (!file.exists()) {
			file.getParentFile().mkdirs();
			ConfigManager.getInstance().copy(DeathMessages.getInstance().getResource(fileName + ".yml"), file);
		}
		config = CommentedConfiguration.loadConfiguration(file);
		try {
			config.syncWithConfig(file, DeathMessages.getInstance().getResource(fileName + ".yml"), "none");
		} catch (Exception e) {
			LogManager.getLogger().error(e);
		}
	}
}
