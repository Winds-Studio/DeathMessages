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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

public class CommandDiscordLog extends DeathMessagesCommand {

	private static final FileSettings<Config> config = FileStore.INSTANCE.getCONFIG();

	@Override
	public String command() {
		return "discordlog";
	}

	@Override
	public void onCommand(CommandSender sender, String[] args) {
		if (!sender.hasPermission(Permission.DEATHMESSAGES_COMMAND_DISCORDLOG.getValue())) {
			DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.formatMessage("Commands.DeathMessages.No-Permission"));
			return;
		}
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

		TextComponent discordConfig = Component.text()
				.append(Component.newline())
				.append(Component.text("  Enabled: ", NamedTextColor.GREEN))
				.append(Component.text(config.getBoolean(Config.HOOKS_DISCORD_ENABLED), NamedTextColor.RED)).append(Component.newline())
				.append(Component.text("  Channels:", NamedTextColor.GREEN)).append(Component.newline())
				// Player
				.append(Component.text("    Player-Enabled: ", NamedTextColor.GREEN))
				.append(Component.text(config.getBoolean(Config.HOOKS_DISCORD_CHANNELS_PLAYER_ENABLED), NamedTextColor.RED)).append(Component.newline())
				.append(Component.text("    Player-Channels:", NamedTextColor.GREEN)).append(Component.newline())
				.append(Component.text("      - " + String.join("\n      - ", config.getStringList(Config.HOOKS_DISCORD_CHANNELS_PLAYER_CHANNELS)))).append(Component.newline())
				// Mob
				.append(Component.text("    Mob-Enabled: ", NamedTextColor.GREEN))
				.append(Component.text(config.getBoolean(Config.HOOKS_DISCORD_CHANNELS_MOB_ENABLED), NamedTextColor.RED)).append(Component.newline())
				.append(Component.text("    Mob-Channels:", NamedTextColor.GREEN)).append(Component.newline())
				.append(Component.text("      - " + String.join("\n      - ", config.getStringList(Config.HOOKS_DISCORD_CHANNELS_MOB_CHANNELS)))).append(Component.newline())
				// Player
				.append(Component.text("    Natural-Enabled: ", NamedTextColor.GREEN))
				.append(Component.text(config.getBoolean(Config.HOOKS_DISCORD_CHANNELS_NATURAL_ENABLED), NamedTextColor.RED)).append(Component.newline())
				.append(Component.text("    Natural-Channels:", NamedTextColor.GREEN)).append(Component.newline())
				.append(Component.text("      - " + String.join("\n      - ", config.getStringList(Config.HOOKS_DISCORD_CHANNELS_NATURAL_CHANNELS)))).append(Component.newline())
				// Player
				.append(Component.text("    Entity-Enabled: ", NamedTextColor.GREEN))
				.append(Component.text(config.getBoolean(Config.HOOKS_DISCORD_CHANNELS_ENTITY_ENABLED), NamedTextColor.RED)).append(Component.newline())
				.append(Component.text("    Entity-Channels:", NamedTextColor.GREEN)).append(Component.newline())
				.append(Component.text("      - " + String.join("\n      - ", config.getStringList(Config.HOOKS_DISCORD_CHANNELS_ENTITY_CHANNELS))))
				.build();

		Messages.getInstance().getConfig().getStringList("Commands.DeathMessages.Sub-Commands.DiscordLog")
				.stream()
				.map(Assets::convertFromLegacy)
				.forEach(msg -> DeathMessages.getInstance().adventure().sender(sender).sendMessage(msg
						.replaceText(Assets.prefix)
						.replaceText(TextReplacementConfig.builder()
								.match("%discordJar%")
								.replacement(discordJar)
								.build())
						.replaceText(TextReplacementConfig.builder()
								.match("%discordToken%")
								.replacement(discordToken)
								.build())
						.replaceText(TextReplacementConfig.builder()
								.match("%discordConfig%")
								.replacement(discordConfig)
								.build())
				));
	}
}
