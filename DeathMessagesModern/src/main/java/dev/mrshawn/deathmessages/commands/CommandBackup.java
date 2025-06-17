package dev.mrshawn.deathmessages.commands;

import dev.mrshawn.deathmessages.config.ConfigManager;
import dev.mrshawn.deathmessages.enums.Permission;
import dev.mrshawn.deathmessages.utils.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.command.CommandSender;

public class CommandBackup extends DeathMessagesCommand {

    @Override
    public String command() {
        return "backup";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission(Permission.DEATHMESSAGES_COMMAND_BACKUP.getValue())) {
            sender.sendMessage(Util.formatMessage("Commands.DeathMessages.No-Permission"));
            return;
        }
        if (args.length == 0) {
            sender.sendMessage(Util.formatMessage("Commands.DeathMessages.Sub-Commands.Backup.Usage"));
            return;
        }

        boolean excludeUserData = Boolean.parseBoolean(args[0]);
        String code = ConfigManager.getInstance().backup(excludeUserData);

        Component message = Util.formatMessage("Commands.DeathMessages.Sub-Commands.Backup.Backed-Up")
                .replaceText(TextReplacementConfig.builder()
                        .matchLiteral("%backup-code%")
                        .replacement(code)
                        .build());

        sender.sendMessage(message);
    }
}
