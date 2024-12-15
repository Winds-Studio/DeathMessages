package dev.mrshawn.deathmessages.commands;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.config.ConfigManager;
import dev.mrshawn.deathmessages.enums.Permission;
import dev.mrshawn.deathmessages.utils.ComponentUtil;
import dev.mrshawn.deathmessages.utils.Util;
import org.bukkit.command.CommandSender;

public class CommandReload extends DeathMessagesCommand {

    @Override
    public String command() {
        return "reload";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission(Permission.DEATHMESSAGES_COMMAND_RELOAD.getValue())) {
            ComponentUtil.sendMessage(sender, Util.formatMessage("Commands.DeathMessages.No-Permission"));
            return;
        }
        ConfigManager.getInstance().reload();
        ComponentUtil.sendMessage(sender, Util.formatMessage("Commands.DeathMessages.Sub-Commands.Reload.Reloaded"));
    }
}
