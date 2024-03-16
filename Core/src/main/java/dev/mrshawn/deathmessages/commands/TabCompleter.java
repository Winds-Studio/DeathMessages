package dev.mrshawn.deathmessages.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TabCompleter implements org.bukkit.command.TabCompleter {

	List<String> arguments = new ArrayList<>();

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 1) {
			// Dreeam - refer to https://github.com/mrgeneralq/sleep-most/blob/5f2f7772c9715cf57530e2af3573652d17cd7420/src/main/java/me/mrgeneralq/sleepmost/commands/SleepmostCommand.java#L135
			return Stream.of(
							"backup",
							"blacklist",
							"discordlog",
							"reload",
							"restore",
							"toggle",
							"version"
					).filter(arg -> sender.hasPermission("deathmessages.command." + arg))
					.collect(Collectors.toList());
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("backup")) {
				return Arrays.asList(
						"true",
						"false"
				);
			} else if (args[0].equalsIgnoreCase("blacklist")) {
				arguments.clear();
				for (Player player : Bukkit.getServer().getOnlinePlayers()) {
					arguments.add(player.getName());
				}
				return arguments;
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
