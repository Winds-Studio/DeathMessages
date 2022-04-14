package dev.mrshawn.deathmessages.hook;

import dev.mrshawn.deathmessages.enums.MessageType;
import dev.mrshawn.deathmessages.config.Settings;
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
