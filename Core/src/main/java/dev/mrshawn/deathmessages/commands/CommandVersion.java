package dev.mrshawn.deathmessages.commands;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.enums.Permission;
import dev.mrshawn.deathmessages.utils.Assets;
import dev.mrshawn.deathmessages.utils.Updater;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

public class CommandVersion extends DeathMessagesCommand {

	@Override
	public String command() {
		return "version";
	}

	@Override
	public void onCommand(CommandSender sender, String[] args) {
		if (!sender.hasPermission(Permission.DEATHMESSAGES_COMMAND_VERSION.getValue())) {
			DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.formatMessage("Commands.DeathMessages.No-Permission"));
			return;
		}

		Component message = Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Version")
				.replaceText(TextReplacementConfig.builder()
						.match("%version%")
						.replacement(DeathMessages.getInstance().getDescription().getVersion())
						.build())
				.replaceText(TextReplacementConfig.builder()
						.match("%authors%")
						.replacement(String.join(", ", DeathMessages.getInstance().getDescription().getAuthors()))
						.build());

		DeathMessages.getInstance().adventure().sender(sender).sendMessage(message);

		DeathMessages.getInstance().adventure().sender(sender).sendMessage(Component.text("Checking update..."));
		Updater.checkUpdate();
		switch (Updater.shouldUpdate) {
			case 0:
				DeathMessages.getInstance().adventure().sender(sender).sendMessage(Component.text("Great! You are using the latest version.", NamedTextColor.GREEN));
				break;
			case 1:
				DeathMessages.getInstance().adventure().sender(sender).sendMessage(Component.text().append(Component.text("Find a new version! Click to download: https://github.com/Winds-Studio/DeathMessages/releases", NamedTextColor.YELLOW))
						.appendNewline()
						.append(Component.text()
								.append(Component.text("Current Version: ", NamedTextColor.YELLOW))
								.append(Component.text(Updater.nowVersion))
								.append(Component.text(" | Latest Version: ", NamedTextColor.YELLOW))
								.append(Component.text(Updater.latestVersion))
								.build()));
				break;
			case -1:
				DeathMessages.getInstance().adventure().sender(sender).sendMessage(Component.text("Failed to check update!", NamedTextColor.RED));
				break;
		}
	}
}
