package dev.mrshawn.deathmessages.config.files;

import dev.mrshawn.deathmessages.DeathMessages;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileSettings<C extends Enum<C>> {

    private final String fileName;
    private final File file;
    private YamlConfiguration yamlConfig;
    private final Map<Enum<C>, Object> values = new HashMap<>();

    public FileSettings(String fileName) {
        this.fileName = fileName;
        this.file = new File(DeathMessages.getInstance().getDataFolder(), fileName);
        loadFile();
    }

    private void loadFile() {
        if (!file.exists()) {
            DeathMessages.getInstance().saveResource(fileName, false);
        }
    }

    public void save() {
        try {
            yamlConfig.save(file);
        } catch (IOException e) {
            DeathMessages.LOGGER.error(e);
        }
    }

    public FileSettings<C> loadSettings(Class<C> enumClass) {
        yamlConfig = YamlConfiguration.loadConfiguration(file);

        EnumSet<C> enumSet = EnumSet.allOf(enumClass);
        for (C value : enumSet) {
            if (!(value instanceof Config)) {
                throw new IllegalArgumentException("Enum " + enumClass.getName() + " must implement ConfigEnum");
            }
            Config config = (Config) value;

            String configPath = config.getPath();
            if (yamlConfig.contains(configPath)) {
                // Dreeam TODO - will not reach here when reload configs
                values.put(value, yamlConfig.get(configPath));
            } else {
                Object defaultValue = config.getDefault();
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

    public void set(Enum<C> enumValue, Config config, Object setValue) {
        values.put(enumValue, setValue);
        yamlConfig.set(config.getPath(), setValue);
    }
}
