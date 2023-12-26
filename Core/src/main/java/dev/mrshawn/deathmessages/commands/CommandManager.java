package dev.mrshawn.deathmessages.commands;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.config.Messages;
import dev.mrshawn.deathmessages.enums.Permission;
import dev.mrshawn.deathmessages.utils.Assets;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager implements CommandExecutor {

	private final List<DeathMessagesCommand> commands = new ArrayList<>();

	public void initializeSubCommands() {
		commands.add(new CommandBackup());
		commands.add(new CommandBlacklist());
		commands.add(new CommandDiscordLog());
		commands.add(new CommandEdit());
		commands.add(new CommandReload());
		commands.add(new CommandRestore());
		commands.add(new CommandToggle());
		commands.add(new CommandVersion());
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String cmdLabel, String[] args) {
		if (sender instanceof Player && !sender.hasPermission(Permission.DEATHMESSAGES_COMMAND.getValue())) {
			DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.No-Permission")));
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
