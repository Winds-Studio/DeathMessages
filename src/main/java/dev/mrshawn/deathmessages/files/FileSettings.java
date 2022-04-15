package dev.mrshawn.deathmessages.files;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class FileSettings {

	private final JavaPlugin plugin;
	private final File file;
	private YamlConfiguration yamlConfig;
	private final boolean isResource;
	private final Map<Enum<?>, Object> values = new HashMap<>();

	public FileSettings(JavaPlugin plugin, File file, boolean isResource) {
		this.plugin = plugin;
		this.file = file;
		this.isResource = isResource;
//		if (isResource) {
//			plugin.saveResource(this.file.getPath(), false);
//		}
		loadFile();
	}

	public FileSettings(JavaPlugin plugin, String filePath, boolean isResource) {
		this.plugin = plugin;
		this.file = new File(filePath.replace(".yml", "") + ".yml");
		this.isResource = isResource;
//		if (isResource) {
//			plugin.saveResource(this.file.getPath(), false);
//		}
		loadFile();
	}

	private void loadFile() {
		if (!file.exists()) {
			if (isResource) {
				plugin.saveResource(file.getPath(), false);
			} else {
				file.getParentFile().mkdirs();
				try {
					file.createNewFile();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	public void save() {
		try {
			yamlConfig.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public <E extends Enum<E>> FileSettings loadSettings(Class<E> enumClass) {
		try {
			yamlConfig = YamlConfiguration.loadConfiguration(file);

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

				if (!yamlConfig.contains(configPath)) {
					if (hasDefaults) {
						yamlConfig.set(configPath, getDefault.invoke(value));
					} else {
						continue;
					}
				}

				values.put(value, yamlConfig.get((String) getPath.invoke(value)));
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
		for (Object val : get(value, List.class)) {
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

	public <T, E extends Enum<E>> void set(Class<E> enumClass, Enum<?> value, T setValue) {
		values.put(value, setValue);
		yamlConfig.set(getPath(enumClass, value), setValue);
	}

	private <E extends Enum<E>> String getPath(Class<E> enumClass, Enum<?> value) {
		try {
			Method getPath = enumClass.getMethod("getPath");
			return (String) getPath.invoke(value);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			return "";
		}
	}

}
