package dev.mrshawn.deathmessages.commands;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.enums.Permission;
import dev.mrshawn.deathmessages.utils.Assets;
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
		String message = Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Version");
		message = message
				.replaceAll("%version%", DeathMessages.getInstance().getDescription().getVersion())
				.replaceAll("%authors%", DeathMessages.getInstance().getDescription().getAuthors().toString());
		DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(message));
	}
}
