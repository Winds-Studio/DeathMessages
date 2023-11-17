package dev.mrshawn.deathmessages.commands;

import dev.mrshawn.deathmessages.enums.DamageTypes;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TabCompleter implements org.bukkit.command.TabCompleter {

	List<String> arguments = new ArrayList<>();

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 1) {
			arguments.clear();
			arguments.add("backup");
			arguments.add("blacklist");
			arguments.add("discordlog");
			arguments.add("edit");
			arguments.add("reload");
			arguments.add("restore");
			arguments.add("toggle");
			arguments.add("version");
			return arguments;
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("backup")) {
				arguments.clear();
				arguments.add("true");
				arguments.add("false");
				return arguments;
			} else if (args[0].equalsIgnoreCase("blacklist")) {
				arguments.clear();
				for (Player player : Bukkit.getServer().getOnlinePlayers()) {
					arguments.add(player.getName());
				}
				return arguments;
			} else if (args[0].equalsIgnoreCase("edit")) {
				arguments.clear();
				arguments.add("player");
				arguments.add("entity");
				return arguments;
			}
		} else if (args.length == 3 && args[0].equalsIgnoreCase("restore")) {
			arguments.clear();
			arguments.add("true");
			arguments.add("false");
			return arguments;
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
					arguments.clear();
					arguments.add("solo");
					arguments.add("gang");
					return arguments;
				} else if (args[1].equalsIgnoreCase("entity")) {
					return DamageTypes.getFriendlyNames();
				}
			} else if (args.length == 5) {
				// /dm edit <player> <mobName> <solo, gang> <damage-type>
				if (args[1].equalsIgnoreCase("player")) {
					return DamageTypes.getFriendlyNames();
				} else if (args[1].equalsIgnoreCase("entity")) {
					arguments.clear();
					arguments.add("add");
					arguments.add("remove");
					arguments.add("list");
					return arguments;
				}
			} else if (args.length == 6) {
				// /dm edit <player> <mobName> <solo, gang> <damage-type> <add, remove, list>
				if (args[1].equalsIgnoreCase("player")) {
					arguments.clear();
					arguments.add("add");
					arguments.add("remove");
					arguments.add("list");
					return arguments;
				}
			}
		}
		return null;
	}
}
