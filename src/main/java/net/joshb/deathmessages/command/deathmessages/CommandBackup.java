package net.joshb.deathmessages.command.deathmessages;

import net.joshb.deathmessages.assets.Assets;
import net.joshb.deathmessages.config.ConfigManager;
import net.joshb.deathmessages.enums.Permission;
import org.bukkit.command.CommandSender;

public class CommandBackup extends DeathMessagesCommand {


    @Override
    public String command() {
        return "backup";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(!sender.hasPermission(Permission.DEATHMESSAGES_COMMAND_BACKUP.getValue())){
            sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.No-Permission"));
            return;
        }
        if(args.length == 0){
            sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Backup.Usage"));
        } else {
            boolean b = Boolean.parseBoolean(args[0]);
            String code = ConfigManager.getInstance().backup(b);
            sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Backup.Backed-Up")
                    .replaceAll("%backup-code%", code));
        }
    }
}