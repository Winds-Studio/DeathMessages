package dev.mrshawn.deathmessages.hooks;

import dev.mrshawn.deathmessages.config.Config;
import dev.mrshawn.deathmessages.enums.MessageType;

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
				return Config.settings.HOOKS_DISCORD_CHANNELS_PLAYER_CHANNELS();
			case MOB:
				return Config.settings.HOOKS_DISCORD_CHANNELS_MOB_CHANNELS();
			case NATURAL:
				return Config.settings.HOOKS_DISCORD_CHANNELS_NATURAL_CHANNELS();
			case ENTITY:
				return Config.settings.HOOKS_DISCORD_CHANNELS_ENTITY_CHANNELS();
			default:
				return null;
		}
	}
}
