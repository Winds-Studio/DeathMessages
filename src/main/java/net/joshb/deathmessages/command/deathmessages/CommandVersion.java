package net.joshb.deathmessages.command.deathmessages;

import net.joshb.deathmessages.DeathMessages;
import net.joshb.deathmessages.assets.Assets;
import net.joshb.deathmessages.enums.Permission;
import org.bukkit.command.CommandSender;

public class CommandVersion extends DeathMessagesCommand {


    @Override
    public String command() {
        return "version";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(!sender.hasPermission(Permission.DEATHMESSAGES_COMMAND_VERSION.getValue())){
            sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.No-Permission"));
            return;
        }
        String message = Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Version");
        message = message.replaceAll("%version%", DeathMessages.plugin.getDescription().getVersion());
        sender.sendMessage(message);
    }
}
