package dev.mrshawn.deathmessages.hooks;

import dev.mrshawn.deathmessages.config.files.Config;
import dev.mrshawn.deathmessages.config.files.FileStore;
import dev.mrshawn.deathmessages.enums.MessageType;

import java.util.List;

public class DiscordAssets {

    private static final DiscordAssets instance = new DiscordAssets();

    public static DiscordAssets getInstance() {
        return instance;
    }

    public List<String> getIDs(MessageType messageType) {
        return switch (messageType) {
            case PLAYER -> FileStore.CONFIG.getStringList(Config.HOOKS_DISCORD_CHANNELS_PLAYER_CHANNELS);
            case MOB -> FileStore.CONFIG.getStringList(Config.HOOKS_DISCORD_CHANNELS_MOB_CHANNELS);
            case NATURAL -> FileStore.CONFIG.getStringList(Config.HOOKS_DISCORD_CHANNELS_NATURAL_CHANNELS);
            case ENTITY -> FileStore.CONFIG.getStringList(Config.HOOKS_DISCORD_CHANNELS_ENTITY_CHANNELS);
        };
    }
}
