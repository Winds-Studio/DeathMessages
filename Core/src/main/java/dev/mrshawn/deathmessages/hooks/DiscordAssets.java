package dev.mrshawn.deathmessages.hooks;

import dev.mrshawn.deathmessages.enums.MessageType;
import dev.mrshawn.deathmessages.files.Config;
import dev.mrshawn.deathmessages.files.FileSettings;
import dev.mrshawn.deathmessages.kotlin.files.FileStore;

import java.util.List;

public class DiscordAssets {

	public DiscordAssets() {
	}

	private static final DiscordAssets instance = new DiscordAssets();
	private static final FileSettings<Config> config = FileStore.INSTANCE.getCONFIG();

	public static DiscordAssets getInstance() {
		return instance;
	}

	public List<String> getIDs(MessageType messageType) {
		switch (messageType) {
			case PLAYER:
				return config.getStringList(Config.HOOKS_DISCORD_CHANNELS_PLAYER_CHANNELS);
			case MOB:
				return config.getStringList(Config.HOOKS_DISCORD_CHANNELS_MOB_CHANNELS);
			case NATURAL:
				return config.getStringList(Config.HOOKS_DISCORD_CHANNELS_NATURAL_CHANNELS);
			case ENTITY:
				return config.getStringList(Config.HOOKS_DISCORD_CHANNELS_ENTITY_CHANNELS);
			default:
				return null;
		}
	}
}
