package dev.mrshawn.deathmessages.commands;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.config.Messages;
import dev.mrshawn.deathmessages.enums.Permission;
import dev.mrshawn.deathmessages.utils.Assets;
import org.jetbrains.annotations.NotNull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CommandManager implements CommandExecutor {

	private List<DeathMessagesCommand> commands;

	public void initSubCommands() {
		commands = Arrays.asList(
				new CommandBackup(),
				new CommandBlacklist(),
				new CommandDiscordLog(),
				new CommandReload(),
				new CommandRestore(),
				new CommandToggle(),
				new CommandVersion()
		);
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String cmdLabel, String[] args) {
		if (sender instanceof Player && !sender.hasPermission(Permission.DEATHMESSAGES_COMMAND.getValue())) {
			DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.formatMessage("Commands.DeathMessages.No-Permission"));
			return false;
		}
		if (args.length == 0) {
			Messages.getInstance().getConfig().getStringList("Commands.DeathMessages.Help")
					.stream()
					.map(Assets::convertFromLegacy)
					.forEach(msg -> DeathMessages.getInstance().adventure().sender(sender).sendMessage(msg
							.replaceText(Assets.prefix)));
		} else {
			DeathMessagesCommand cmd = get(args[0]);
			if (cmd != null) {
				String[] trimmedArgs = Arrays.copyOfRange(args, 1, args.length);
				cmd.onCommand(sender, trimmedArgs);
				return false;
			}
			Messages.getInstance().getConfig().getStringList("Commands.DeathMessages.Help")
					.stream()
					.map(Assets::convertFromLegacy)
					.forEach(msg -> DeathMessages.getInstance().adventure().sender(sender).sendMessage(msg
							.replaceText(Assets.prefix)));
		}
		return false;
	}

	private DeathMessagesCommand get(String name) {
		for (DeathMessagesCommand cmd : commands) {
			if (cmd.command().equalsIgnoreCase(name))
				return cmd;
		}
		return null;
	}
}
