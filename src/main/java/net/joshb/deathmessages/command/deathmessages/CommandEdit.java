package net.joshb.deathmessages.command.deathmessages;

import net.joshb.deathmessages.assets.Assets;
import net.joshb.deathmessages.config.EntityDeathMessages;
import net.joshb.deathmessages.config.PlayerDeathMessages;
import net.joshb.deathmessages.enums.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;

import java.util.List;

public class CommandEdit extends DeathMessagesCommand {

    @Override
    public String command() {
        return "edit";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        // /dm edit <player> <mobName> <solo, gang> <damage type> <add, remove, list> (remove=placeholder)
        if (!sender.hasPermission(Permission.DEATHMESSAGES_COMMAND_EDIT.getValue())) {
            sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.No-Permission"));
            return;
        }
        if (args.length <= 3) {
            sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Usage"));
        } else {
            if (args[0].equalsIgnoreCase("player")) {
                String mobName = args[1];
                String damageType = args[3];
                boolean exists = false;
                for (EntityType entityType : EntityType.values()) {
                    //Check isAlive as getSimpleName could be null if the entity is not living
                    if (entityType.isAlive() && entityType.getEntityClass().getSimpleName().toLowerCase().equalsIgnoreCase(mobName.toLowerCase())) {
                        exists = true;
                    }
                }
                if (!exists) {
                    sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Mob-Type"));
                    return;
                }
                if (!Assets.damageTypes.contains(damageType)) {
                    sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Damage-Type"));
                    return;
                }
                if (args[4].equalsIgnoreCase("add")) {
                    if (args[2].equalsIgnoreCase("solo")) {
                        sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Adding-Start"));
                        Assets.addingMessage.put(sender.getName(), "Solo:" + mobName + ":" + damageType);
                    } else if (args[2].equalsIgnoreCase("gang")) {
                        sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Adding-Start"));
                        Assets.addingMessage.put(sender.getName(), "Gang:" + mobName + ":" + damageType);
                    } else {
                        sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Arguments"));
                    }
                } else if (args[4].equalsIgnoreCase("remove")) {
                    if (args[5] == null) {
                        sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Arguments"));
                        return;
                    }
                    if (!Assets.isNumeric(args[5])) {
                        sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Placeholder"));
                        return;
                    }
                    int placeholder = Integer.parseInt(args[5]) - 1;
                    if (args[2].equalsIgnoreCase("solo")) {
                        List<String> list = PlayerDeathMessages.getInstance().getConfig().getStringList("Mobs." + mobName + ".Solo." + damageType);
                        if(list.get(placeholder) == null){
                            sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Selection"));
                            return;
                        }
                        sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Removed-Message").replaceAll("%message%", list.get(placeholder)));
                        list.remove(placeholder);
                        PlayerDeathMessages.getInstance().getConfig().set("Mobs." + mobName + ".Solo." + damageType, list);
                        PlayerDeathMessages.getInstance().save();
                        PlayerDeathMessages.getInstance().reload();
                    } else if (args[2].equalsIgnoreCase("gang")) {
                        List<String> list = PlayerDeathMessages.getInstance().getConfig().getStringList("Mobs." + mobName + ".Gang." + damageType);
                        if(list.get(placeholder) == null){
                            sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Selection"));
                            return;
                        }
                        sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Removed-Message").replaceAll("%message%", list.get(placeholder)));
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
                            sender.sendMessage("[" + placeholder + "] " + Assets.formatString(messages));
                            placeholder++;
                        }
                    } else if (args[2].equalsIgnoreCase("gang")) {
                        List<String> list = PlayerDeathMessages.getInstance().getConfig().getStringList("Mobs." + mobName + ".Gang." + damageType);
                        for (String messages : list) {
                            sender.sendMessage("[" + placeholder + "] " + Assets.formatString(messages));
                            placeholder++;
                        }
                    } else {
                        sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Arguments"));
                    }
                } else {
                    sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Arguments"));
                }
            } else if (args[0].equalsIgnoreCase("entity")) {
                String mobName = args[1];
                String damageType = args[2];
                boolean exists = false;
                for (EntityType entityType : EntityType.values()) {
                    //Check isAlive as getSimpleName could be null if the entity is not living
                    if (entityType.isAlive() && entityType.getEntityClass().getSimpleName().toLowerCase().equalsIgnoreCase(mobName.toLowerCase())) {
                        exists = true;
                    }
                }
                if (!exists) {
                    sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Mob-Type"));
                    return;
                }
                if (!Assets.damageTypes.contains(damageType)) {
                    sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Damage-Type"));
                    return;
                }
                if (args[3].equalsIgnoreCase("add")) {
                    sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Adding-Start"));
                    Assets.addingMessage.put(sender.getName(), mobName + ":" + damageType);
                } else if (args[3].equalsIgnoreCase("remove")) {
                    if (args[4] == null) {
                        sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Arguments"));
                        return;
                    }
                    if (!Assets.isNumeric(args[4])) {
                        sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Placeholder"));
                        return;
                    }
                    int placeholder = Integer.parseInt(args[4]) - 1;
                    List<String> list = EntityDeathMessages.getInstance().getConfig().getStringList("Entities." + mobName + "." + damageType);
                    if(list.get(placeholder) == null){
                        sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Selection"));
                        return;
                    }
                    sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Removed-Message").replaceAll("%message%", list.get(placeholder)));
                    list.remove(placeholder);
                    EntityDeathMessages.getInstance().getConfig().set("Entities." + mobName + "." + damageType, list);
                    EntityDeathMessages.getInstance().save();
                    EntityDeathMessages.getInstance().reload();


                } else if (args[3].equalsIgnoreCase("list")) {
                    int placeholder = 1;
                    List<String> list = EntityDeathMessages.getInstance().getConfig().getStringList("Entities." + mobName + "." + damageType);
                    for (String messages : list) {
                        sender.sendMessage("[" + placeholder + "] " + Assets.formatString(messages));
                        placeholder++;
                    }
                } else {
                    sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Invalid-Arguments"));
                }
            }
        }
    }
}
