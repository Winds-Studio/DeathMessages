package net.joshb.deathmessages.hook;

import net.joshb.deathmessages.config.Settings;
import net.joshb.deathmessages.enums.MessageType;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class DiscordAssets {

    public DiscordAssets() {
    }

    private static DiscordAssets instance = new DiscordAssets();

    public static DiscordAssets getInstance() {
        return instance;
    }

    public List<String> getIDs(MessageType messageType) {
        switch (messageType) {
            case PLAYER:
                return getSettings().getStringList("Hooks.Discord.Channels.Player.Channels");
            case MOB:
                return getSettings().getStringList("Hooks.Discord.Channels.Mob.Channels");
            case NATURAL:
                return getSettings().getStringList("Hooks.Discord.Channels.Natural.Channels");
            case ENTITY:
                return getSettings().getStringList("Hooks.Discord.Channels.Entity.Channels");
            default:
                return null;
        }
    }

    private FileConfiguration getSettings() {
        return Settings.getInstance().getConfig();
    }
}
