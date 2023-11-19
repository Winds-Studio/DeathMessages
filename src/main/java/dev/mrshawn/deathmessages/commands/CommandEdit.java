package dev.mrshawn.deathmessages.commands;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.config.EntityDeathMessages;
import dev.mrshawn.deathmessages.config.PlayerDeathMessages;
import dev.mrshawn.deathmessages.enums.DamageTypes;
import dev.mrshawn.deathmessages.enums.Permission;
import dev.mrshawn.deathmessages.utils.Assets;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;

import java.util.List;

// Dreeam - Really useless
@Deprecated
public class CommandEdit extends DeathMessagesCommand {

	@Override
	public String command() {
		return "edit";
	}

	@Override
	public void onCommand(CommandSender sender, String[] args) {
		// /dm edit <player> <mobName> <solo, gang> <damage type> <add, remove, list> (remove=placeholder)
		if (!sender.hasPermission(Permission.DEATHMESSAGES_COMMAND_EDIT.getValue())) {
			DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.No-Permission")));
			return;
		}

		DeathMessages.getInstance().adventure().sender(sender).sendMessage(Component.text("You are trying to use a deprecated command. Command edit will be removed since DeathMessages 1.4.19", NamedTextColor.RED));

		if (args.length <= 3) {
			DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Usage")));
		} else {
			if (args[0].equalsIgnoreCase("player")) {
				String mobName = args[1];
				String damageType = args[3];
				boolean exists = false;
				for (EntityType entityType : EntityType.values()) {
					// Check isAlive as getSimpleName could be null if the entity is not living
                    if (entityType.isAlive() && entityType.getEntityClass().getSimpleName().equalsIgnoreCase(mobName)) {
                        exists = true;
                        break;
                    }
				}
				if (!exists) {
					DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Mob-Type")));
					return;
				}
				if (!DamageTypes.getFriendlyNames().contains(damageType)) {
					DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Damage-Type")));
					return;
				}
				if (args[4].equalsIgnoreCase("add")) {
					if (args[2].equalsIgnoreCase("solo")) {
						DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Adding-Start")));
						Assets.addingMessage.put(sender.getName(), "Solo:" + mobName + ":" + damageType);
					} else if (args[2].equalsIgnoreCase("gang")) {
						DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Adding-Start")));
						Assets.addingMessage.put(sender.getName(), "Gang:" + mobName + ":" + damageType);
					} else {
						DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Arguments")));
					}
				} else if (args[4].equalsIgnoreCase("remove")) {
					if (args[5] == null) {
						DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Arguments")));
						return;
					}
					if (!Assets.isNumeric(args[5])) {
						DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Placeholder")));
						return;
					}
					int placeholder = Integer.parseInt(args[5]) - 1;
					if (args[2].equalsIgnoreCase("solo")) {
						List<String> list = PlayerDeathMessages.getInstance().getConfig().getStringList("Mobs." + mobName + ".Solo." + damageType);
						if (list.get(placeholder) == null) {
							DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Selection")));
							return;
						}
						DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Removed-Message").replaceAll("%message%", list.get(placeholder))));
						list.remove(placeholder);
						PlayerDeathMessages.getInstance().getConfig().set("Mobs." + mobName + ".Solo." + damageType, list);
						PlayerDeathMessages.getInstance().save();
						PlayerDeathMessages.getInstance().reload();
					} else if (args[2].equalsIgnoreCase("gang")) {
						List<String> list = PlayerDeathMessages.getInstance().getConfig().getStringList("Mobs." + mobName + ".Gang." + damageType);
						if (list.get(placeholder) == null) {
							DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Selection")));
							return;
						}
						DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Removed-Message").replaceAll("%message%", list.get(placeholder))));
						list.remove(placeholder);
						PlayerDeathMessages.getInstance().getConfig().set("Mobs." + mobName + ".Gang." + damageType, list);
						PlayerDeathMessages.getInstance().save();
						PlayerDeathMessages.getInstance().reload();
					}


				} else if (args[4].equalsIgnoreCase("list")) {
					int placeholder = 1;
					if (args[2].equalsIgnoreCase("solo")) {
						List<String> list = PlayerDeathMessages.getInstance().getConfig().getStringList("Mobs." + mobName + ".Solo." + damageType);
						for (String messages : list) {
							DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy("[" + placeholder + "] " + messages).replaceText(Assets.prefix));
							placeholder++;
						}
					} else if (args[2].equalsIgnoreCase("gang")) {
						List<String> list = PlayerDeathMessages.getInstance().getConfig().getStringList("Mobs." + mobName + ".Gang." + damageType);
						for (String messages : list) {
							DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy("[" + placeholder + "] " + messages).replaceText(Assets.prefix));
							placeholder++;
						}
					} else {
						DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Arguments")));
					}
				} else {
					DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Arguments")));
				}
			} else if (args[0].equalsIgnoreCase("entity")) {
				String mobName = args[1];
				String damageType = args[2];
				boolean exists = false;
				for (EntityType entityType : EntityType.values()) {
					// Check isAlive as getSimpleName could be null if the entity is not living
                    if (entityType.isAlive() && entityType.getEntityClass().getSimpleName().equalsIgnoreCase(mobName)) {
                        exists = true;
                        break;
                    }
				}
				if (!exists) {
					DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Mob-Type")));
					return;
				}
				if (!DamageTypes.getFriendlyNames().contains(damageType)) {
					DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Damage-Type")));
					return;
				}
				if (args[3].equalsIgnoreCase("add")) {
					DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Adding-Start")));
					Assets.addingMessage.put(sender.getName(), mobName + ":" + damageType);
				} else if (args[3].equalsIgnoreCase("remove")) {
					if (args[4] == null) {
						DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Arguments")));
						return;
					}
					if (!Assets.isNumeric(args[4])) {
						DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Placeholder")));
						return;
					}
					int placeholder = Integer.parseInt(args[4]) - 1;
					List<String> list = EntityDeathMessages.getInstance().getConfig().getStringList("Entities." + mobName + "." + damageType);
					if (list.get(placeholder) == null) {
						DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Selection")));
						return;
					}
					DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Removed-Message").replaceAll("%message%", list.get(placeholder))));
					list.remove(placeholder);
					EntityDeathMessages.getInstance().getConfig().set("Entities." + mobName + "." + damageType, list);
					EntityDeathMessages.getInstance().save();
					EntityDeathMessages.getInstance().reload();


				} else if (args[3].equalsIgnoreCase("list")) {
					int placeholder = 1;
					List<String> list = EntityDeathMessages.getInstance().getConfig().getStringList("Entities." + mobName + "." + damageType);
					for (String messages : list) {
						DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy("[" + placeholder + "] " + messages).replaceText(Assets.prefix));
						placeholder++;
					}
				} else {
					DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Arguments")));
				}
			}
		}
	}
}
