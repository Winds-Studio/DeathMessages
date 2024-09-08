package dev.mrshawn.deathmessages.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabCompleter implements org.bukkit.command.TabCompleter {

	private final List<String> tabCompletion = new ArrayList<>(Arrays.asList(
			"backup",
			"blacklist",
			"discordlog",
			"reload",
			"restore",
			"toggle",
			"version"
	));

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 1) {
			List<String> completions = new ArrayList<>();

			// Dreeam - refer to https://github.com/mrgeneralq/sleep-most/blob/5f2f7772c9715cf57530e2af3573652d17cd7420/src/main/java/me/mrgeneralq/sleepmost/commands/SleepmostCommand.java#L135
			for (String completion: tabCompletion) {
				final String arg = args[0];
				if (completion.startsWith(arg) && sender.hasPermission("deathmessages.command." + arg)) {
					completions.add(completion);
				}
			}

			return completions;
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("backup")) {
				return Arrays.asList(
						"true",
						"false"
				);
			} else if (args[0].equalsIgnoreCase("blacklist")) {
				final List<String> players = new ArrayList<>();

				for (Player player : Bukkit.getServer().getOnlinePlayers()) {
					if (player.getName().toLowerCase().startsWith(args[1])) {
						players.add(player.getName());
					}
				}

				return players;
			} else if (args[0].equalsIgnoreCase("edit")) {
				return Arrays.asList(
						"player",
						"entity"
				);
			}
		} else if (args.length == 3 && args[0].equalsIgnoreCase("restore")) {
			return Arrays.asList(
					"true",
					"false"
			);
		}

		return null;
	}
}
