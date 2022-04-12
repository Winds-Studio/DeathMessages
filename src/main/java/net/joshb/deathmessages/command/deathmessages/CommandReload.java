package net.joshb.deathmessages.command.deathmessages;

import net.joshb.deathmessages.assets.Assets;
import net.joshb.deathmessages.config.*;
import net.joshb.deathmessages.enums.Permission;
import org.bukkit.command.CommandSender;

public class CommandReload extends DeathMessagesCommand {


    @Override
    public String command() {
        return "reload";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(!sender.hasPermission(Permission.DEATHMESSAGES_COMMAND_RELOAD.getValue())){
            sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.No-Permission"));
            return;
        }
        ConfigManager.getInstance().reload();
        sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Reload.Reloaded"));
    }
}
