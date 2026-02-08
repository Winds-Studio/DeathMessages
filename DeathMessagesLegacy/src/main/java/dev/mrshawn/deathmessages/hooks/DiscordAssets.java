package dev.mrshawn.deathmessages.hooks;

import dev.mrshawn.deathmessages.enums.MessageType;
import dev.mrshawn.deathmessages.config.files.Config;
import dev.mrshawn.deathmessages.config.files.FileStore;

import java.util.List;

public class DiscordAssets {

    public DiscordAssets() {
    }

    private static final DiscordAssets instance = new DiscordAssets();

    public static DiscordAssets getInstance() {
        return instance;
    }

    public List<String> getIDs(MessageType messageType) {
        switch (messageType) {
            case PLAYER:
                return FileStore.CONFIG.getStringList(Config.HOOKS_DISCORD_CHANNELS_PLAYER_CHANNELS);
            case MOB:
                return FileStore.CONFIG.getStringList(Config.HOOKS_DISCORD_CHANNELS_MOB_CHANNELS);
            case NATURAL:
                return FileStore.CONFIG.getStringList(Config.HOOKS_DISCORD_CHANNELS_NATURAL_CHANNELS);
            case ENTITY:
                return FileStore.CONFIG.getStringList(Config.HOOKS_DISCORD_CHANNELS_ENTITY_CHANNELS);
            default:
                return null;
        }
    }
}
