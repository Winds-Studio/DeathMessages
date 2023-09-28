package dev.mrshawn.deathmessages.command.deathmessages;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.config.Messages;
import dev.mrshawn.deathmessages.enums.Permission;
import dev.mrshawn.deathmessages.files.Config;
import dev.mrshawn.deathmessages.files.FileSettings;
import dev.mrshawn.deathmessages.kotlin.files.FileStore;
import dev.mrshawn.deathmessages.utils.Assets;
import github.scarsz.discordsrv.DiscordSRV;
import me.joshb.discordbotapi.server.DiscordBotAPI;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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
			sender.sendMessage(Assets.convertLegacy(Assets.formatMessage("Commands.DeathMessages.No-Permission")));
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
				sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("  &aEnabled: &c" + config.getBoolean(Config.HOOKS_DISCORD_ENABLED)));
				sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("  &aChannels:"));
				// Player
				sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("    &aPlayer-Enabled: &c" + config.getBoolean(Config.HOOKS_DISCORD_CHANNELS_PLAYER_ENABLED)));
				sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("    &aPlayer-Channels:"));
				for (String channels : config.getStringList(Config.HOOKS_DISCORD_CHANNELS_PLAYER_CHANNELS)) {
					sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("      - " + channels));
				}
				// Mob
				sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("    &aMob-Enabled: &c" + config.getBoolean(Config.HOOKS_DISCORD_CHANNELS_MOB_ENABLED)));
				sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("    &aMob-Channels:"));
				for (String channels : config.getStringList(Config.HOOKS_DISCORD_CHANNELS_MOB_CHANNELS)) {
					sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("      - " + channels));
				}
				// Player
				sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("    &aNatural-Enabled: &c" + config.getBoolean(Config.HOOKS_DISCORD_CHANNELS_NATURAL_ENABLED)));
				sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("    &aNatural-Channels:"));
				for (String channels : config.getStringList(Config.HOOKS_DISCORD_CHANNELS_NATURAL_CHANNELS)) {
					sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("      - " + channels));
				}
				// Player
				sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("    &aEntity-Enabled: &c" + config.getBoolean(Config.HOOKS_DISCORD_CHANNELS_ENTITY_ENABLED)));
				sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("    &aEntity-Channels:"));
				for (String channels : config.getStringList(Config.HOOKS_DISCORD_CHANNELS_ENTITY_CHANNELS)) {
					sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("      - " + channels));
				}
				continue;
			}
			sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(log
					.replaceAll("%discordJar%", discordJar)
					.replaceAll("%discordToken%", discordToken)
					.replace("%prefix%", Messages.getInstance().getConfig().getString("Prefix"))));
		}
	}
}
