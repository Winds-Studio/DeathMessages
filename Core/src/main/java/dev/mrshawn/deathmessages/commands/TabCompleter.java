package dev.mrshawn.deathmessages.commands;

import dev.mrshawn.deathmessages.enums.DamageTypes;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
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
							"edit",
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

		// Dreeam TODO - Command edit Will be removed since DeathMessages 1.4.19
		if (args[0].equalsIgnoreCase("edit")) {
			if (args.length == 3) {
				// /dm edit <player> <mobName>
				if (args[1].equalsIgnoreCase("player") || args[1].equalsIgnoreCase("entity")) {
					arguments.clear();
					// Not checking config cause we can add sections if we want
					// List<String> mobNames = new ArrayList<>(PlayerDeathMessages.getInstance().getConfig()
					//      .getConfigurationSection("Mobs").getKeys(false));
					for (EntityType entityType : EntityType.values()) {
						if (entityType.isAlive()) {
							arguments.add(entityType.getEntityClass().getSimpleName().toLowerCase());
						}
					}
					return arguments;
				}
			} else if (args.length == 4) {
				// /dm edit <player> <mobName> <solo, gang>
				if (args[1].equalsIgnoreCase("player")) {
					return Arrays.asList(
							"solo",
							"gang"
					);
				} else if (args[1].equalsIgnoreCase("entity")) {
					return DamageTypes.getFriendlyNames();
				}
			} else if (args.length == 5) {
				// /dm edit <player> <mobName> <solo, gang> <damage-type>
				if (args[1].equalsIgnoreCase("player")) {
					return DamageTypes.getFriendlyNames();
				} else if (args[1].equalsIgnoreCase("entity")) {
					return Arrays.asList(
							"add",
							"remove",
							"list"
					);
				}
			} else if (args.length == 6) {
				// /dm edit <player> <mobName> <solo, gang> <damage-type> <add, remove, list>
				if (args[1].equalsIgnoreCase("player")) {
					return Arrays.asList(
							"add",
							"remove",
							"list"
					);
				}
			}
		}
		return null;
	}
}
