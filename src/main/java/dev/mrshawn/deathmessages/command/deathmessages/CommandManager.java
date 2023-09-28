package dev.mrshawn.deathmessages.command.deathmessages;

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
			sender.sendMessage(Assets.convertLegacy(Assets.formatMessage("Commands.DeathMessages.No-Permission")));
			return false;
		}
		if (args.length == 0) {
			for (String s : Assets.formatMessage(
					Messages.getInstance().getConfig().getStringList("Commands.DeathMessages.Help"))) {
				sender.sendMessage(Assets.convertLegacy(s));
			}
		} else {
			DeathMessagesCommand cmd = get(args[0]);
			if (!(cmd == null)) {
				ArrayList<String> a = new ArrayList<>(Arrays.asList(args));
				a.remove(0);
				args = a.toArray(new String[0]);
				cmd.onCommand(sender, args);
				return false;
			}
			for (String s : Assets.formatMessage(
					Messages.getInstance().getConfig().getStringList("Commands.DeathMessages.Help"))) {
				sender.sendMessage(Assets.convertLegacy(s));
			}
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
