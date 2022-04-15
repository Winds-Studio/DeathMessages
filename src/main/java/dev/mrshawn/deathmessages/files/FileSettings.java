package dev.mrshawn.deathmessages.files;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class FileSettings {

	private final JavaPlugin plugin;
	private final File file;
	private final Map<Enum<?>, Object> values = new HashMap<>();

	public FileSettings(JavaPlugin plugin, File file, boolean isResource) {
		this.plugin = plugin;
		this.file = file;
		if (isResource) {
			plugin.saveResource(this.file.getPath(), false);
		}
	}

	public FileSettings(JavaPlugin plugin, String filePath, boolean isResource) {
		this.plugin = plugin;
		this.file = new File(filePath + ".yml");
		if (isResource) {
			plugin.saveResource(this.file.getPath(), false);
		}
	}

	public <E extends Enum<E>> FileSettings loadSettings(Class<E> enumClass) {
		try {
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

			EnumSet<E> eSet = EnumSet.allOf(enumClass);

			Method getPath = enumClass.getMethod("getPath");
			Method getDefault = null;

			boolean hasDefaults = true;

			try {
				getDefault = enumClass.getMethod("getDefault");
			} catch (NoSuchMethodException | SecurityException e) {
				hasDefaults = false;
			}

			for (E value : eSet) {

				String configPath = (String) getPath.invoke(value);

				if (!config.contains(configPath)) {
					if (hasDefaults) {
						config.set(configPath, getDefault.invoke(value));
					} else {
						continue;
					}
				}

				values.put(value, config.get((String) getPath.invoke(value)));
			}
			return this;
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			Bukkit.getLogger().severe("Error when loading settings for: " + file);
			e.printStackTrace();
			return null;
		}
	}

	public boolean getBoolean(Enum<?> value) {
		return get(value, Boolean.class);
	}

	public boolean getBoolean(Enum<?> value, boolean defaultValue) {
		return get(value, Boolean.class, defaultValue);
	}

	public String getString(Enum<?> value) {
		return get(value, String.class);
	}

	public String getString(Enum<?> value, String defaultValue) {
		return get(value, String.class, defaultValue);
	}

	public int getInt(Enum<?> value) {
		return get(value, Integer.class);
	}

	public int getInt(Enum<?> value, int defaultValue) {
		return get(value, Integer.class, defaultValue);
	}

	public long getLong(Enum<?> value) {
		return get(value, Long.class);
	}

	public long getLong(Enum<?> value, long defaultValue) {
		return get(value, Long.class, defaultValue);
	}

	public List<String> getStringList(Enum<?> value) {
		List<String> tempList = new ArrayList<>();
		for (Object val : get(value, ArrayList.class)) {
			tempList.add((String) val);
		}
		return tempList;
	}

	public List<String> getStringList(Enum<?> value, List<String> defaultValue) {
		return values.containsKey(value) ? getStringList(value) : defaultValue;
	}

	public <T> T get(Enum<?> value, Class<T> clazz) {
		return clazz.cast(values.get(value));
	}

	public <T> T get(Enum<?> value, Class<T> clazz, T defaultValue) {
		return values.containsKey(value) ? get(value, clazz) : clazz.cast(defaultValue);
	}

}
