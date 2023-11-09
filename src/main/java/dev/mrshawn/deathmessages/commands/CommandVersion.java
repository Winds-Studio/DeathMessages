package dev.mrshawn.deathmessages.commands;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.enums.Permission;
import dev.mrshawn.deathmessages.utils.Assets;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.command.CommandSender;

public class CommandVersion extends DeathMessagesCommand {

	@Override
	public String command() {
		return "version";
	}

	@Override
	public void onCommand(CommandSender sender, String[] args) {
		if (!sender.hasPermission(Permission.DEATHMESSAGES_COMMAND_VERSION.getValue())) {
			DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.No-Permission")));
			return;
		}

		Component message = Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Version"))
				.replaceText(TextReplacementConfig.builder()
						.match("%version%")
						.replacement(DeathMessages.getInstance().getDescription().getVersion())
						.build())
				.replaceText(TextReplacementConfig.builder()
						.match("%authors%")
						.replacement(DeathMessages.getInstance().getDescription().getAuthors().toString()) // Dreeam TODO - remove []
						.build());

		DeathMessages.getInstance().adventure().sender(sender).sendMessage(message);
	}
}
