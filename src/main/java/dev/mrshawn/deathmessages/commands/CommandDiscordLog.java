package dev.mrshawn.deathmessages.commands;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.config.Messages;
import dev.mrshawn.deathmessages.enums.Permission;
import dev.mrshawn.deathmessages.files.Config;
import dev.mrshawn.deathmessages.files.FileSettings;
import dev.mrshawn.deathmessages.kotlin.files.FileStore;
import dev.mrshawn.deathmessages.utils.Assets;
import github.scarsz.discordsrv.DiscordSRV;
import me.joshb.discordbotapi.server.DiscordBotAPI;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CommandDiscordLog extends DeathMessagesCommand {

	private static final FileSettings<Config> config = FileStore.INSTANCE.getCONFIG();

	@Override
	public String command() {
		return "discordlog";
	}

	@Override
	public void onCommand(CommandSender sender, String[] args) {
		if (!sender.hasPermission(Permission.DEATHMESSAGES_COMMAND_DISCORDLOG.getValue())) {
			DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.No-Permission")));
			return;
		}
		List<String> discordLog = Messages.getInstance().getConfig().getStringList("Commands.DeathMessages.Sub-Commands.DiscordLog");
		String discordJar;
		if (DeathMessages.discordBotAPIExtension != null) {
			discordJar = "DiscordBotAPI";
		} else if (DeathMessages.discordSRVExtension != null) {
			discordJar = "DiscordSRV";
		} else {
			discordJar = "Discord Jar Not Installed";
		}
		String discordToken;
		if (discordJar.equals("DiscordBotAPI")) {
			discordToken = DiscordBotAPI.getJDA().getToken().length() > 40 ? DiscordBotAPI.getJDA().getToken().substring(40) : "Token Not Set";
		} else if (DeathMessages.discordSRVExtension != null) {
			discordToken = DiscordSRV.getPlugin().getJda().getToken().length() > 40 ? DiscordSRV.getPlugin().getJda().getToken().substring(40) : "Token Not Set";
		} else {
			discordToken = "Discord Jar Not Installed";
		}
		for (String log : discordLog) {
			if (log.equals("%discordConfig%")) {
				DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy("  &aEnabled: &c" + config.getBoolean(Config.HOOKS_DISCORD_ENABLED)));
				DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy("  &aChannels:"));
				// Player
				DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy("    &aPlayer-Enabled: &c" + config.getBoolean(Config.HOOKS_DISCORD_CHANNELS_PLAYER_ENABLED)));
				DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy("    &aPlayer-Channels:"));
				for (String channels : config.getStringList(Config.HOOKS_DISCORD_CHANNELS_PLAYER_CHANNELS)) {
					DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy("      - " + channels));
				}
				// Mob
				DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy("    &aMob-Enabled: &c" + config.getBoolean(Config.HOOKS_DISCORD_CHANNELS_MOB_ENABLED)));
				DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy("    &aMob-Channels:"));
				for (String channels : config.getStringList(Config.HOOKS_DISCORD_CHANNELS_MOB_CHANNELS)) {
					DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy("      - " + channels));
				}
				// Player
				DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy("    &aNatural-Enabled: &c" + config.getBoolean(Config.HOOKS_DISCORD_CHANNELS_NATURAL_ENABLED)));
				DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy("    &aNatural-Channels:"));
				for (String channels : config.getStringList(Config.HOOKS_DISCORD_CHANNELS_NATURAL_CHANNELS)) {
					DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy("      - " + channels));
				}
				// Player
				DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy("    &aEntity-Enabled: &c" + config.getBoolean(Config.HOOKS_DISCORD_CHANNELS_ENTITY_ENABLED)));
				DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy("    &aEntity-Channels:"));
				for (String channels : config.getStringList(Config.HOOKS_DISCORD_CHANNELS_ENTITY_CHANNELS)) {
					DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy("      - " + channels));
				}
				continue;
			}
			DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(log
					.replaceAll("%discordJar%", discordJar)
					.replaceAll("%discordToken%", discordToken)
					.replace("%prefix%", Messages.getInstance().getConfig().getString("Prefix"))));
		}
	}
}
