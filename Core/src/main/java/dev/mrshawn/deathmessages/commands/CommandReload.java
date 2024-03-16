package dev.mrshawn.deathmessages.commands;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.enums.Permission;
import dev.mrshawn.deathmessages.utils.Assets;
import org.bukkit.command.CommandSender;

public class CommandReload extends DeathMessagesCommand {

	@Override
	public String command() {
		return "reload";
	}

	@Override
	public void onCommand(CommandSender sender, String[] args) {
		if (!sender.hasPermission(Permission.DEATHMESSAGES_COMMAND_RELOAD.getValue())) {
			DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.formatMessage("Commands.DeathMessages.No-Permission"));
			return;
		}
		DeathMessages.getInstance().initConfig();
		DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Reload.Reloaded"));
	}
}
