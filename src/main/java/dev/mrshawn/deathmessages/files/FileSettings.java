package dev.mrshawn.deathmessages.files;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FileSettings {
    private final JavaPlugin plugin;
    private final String fileName;
    private final File file;
    private YamlConfiguration yamlConfig;
    private final Map<Enum<?>, Object> values = new HashMap<>();

    public FileSettings(JavaPlugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.file = new File(plugin.getDataFolder(), fileName);
        loadFile();
    }

    private void loadFile() {
        if (!file.exists()) {
            plugin.saveResource(fileName, false);
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
        yamlConfig = YamlConfiguration.loadConfiguration(file);

        EnumSet<E> enumSet = EnumSet.allOf(enumClass);
        for (E value : enumSet) {
            if (!(value instanceof ConfigEnum configEnum)) {
                throw new IllegalArgumentException("Enum " + enumClass.getName() + " must implement ConfigEnum");
            }

            String configPath = configEnum.getPath();
            if (yamlConfig.contains(configPath)) {
                values.put(value, yamlConfig.get(configPath));
            } else {
                Object defaultValue = configEnum.getDefault();
                if (defaultValue != null) {
                    yamlConfig.set(configPath, defaultValue);
                    values.put(value, defaultValue);
                }
            }
        }

        return this;
    }

    public boolean getBoolean(Enum<?> value) {
        return get(value, Boolean.class);
    }

    public String getString(Enum<?> value) {
        return get(value, String.class);
    }

    public int getInt(Enum<?> value) {
        return get(value, Integer.class);
    }

    public long getLong(Enum<?> value) {
        return get(value, Long.class);
    }

    public List<String> getStringList(Enum<?> value) {
        List<String> tempList = new ArrayList<>();
        for (Object val : get(value, List.class)) {
            tempList.add((String) val);
        }
        return tempList;
    }

    public <T> T get(Enum<?> value, Class<T> clazz) {
        return clazz.cast(values.get(value));
    }

    public void set(Enum<?> enumValue, ConfigEnum configEnum, Object setValue) {
        values.put(enumValue, setValue);
        yamlConfig.set(configEnum.getPath(), setValue);
    }
}
