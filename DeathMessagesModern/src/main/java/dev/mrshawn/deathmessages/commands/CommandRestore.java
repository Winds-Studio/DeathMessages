package dev.mrshawn.deathmessages.commands;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.config.ConfigManager;
import dev.mrshawn.deathmessages.enums.Permission;
import dev.mrshawn.deathmessages.utils.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.command.CommandSender;

public class CommandRestore extends DeathMessagesCommand {

    @Override
    public String command() {
        return "restore";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission(Permission.DEATHMESSAGES_COMMAND_RESTORE.getValue())) {
            sender.sendMessage(Util.formatMessage("Commands.DeathMessages.No-Permission"));
            return;
        }
        if (args.length <= 1) {
            sender.sendMessage(Util.formatMessage("Commands.DeathMessages.Sub-Commands.Restore.Usage"));
        } else {
            String code = args[0];
            boolean excludeUserData = Boolean.parseBoolean(args[1]);
            if (ConfigManager.getInstance().restore(code, excludeUserData)) {

                Component message = Util.formatMessage("Commands.DeathMessages.Sub-Commands.Restore.Restored")
                        .replaceText(TextReplacementConfig.builder()
                                .matchLiteral("%backup-code%")
                                .replacement(code)
                                .build());

                sender.sendMessage(message);
            } else {
                sender.sendMessage(Util.formatMessage("Commands.DeathMessages.Sub-Commands.Restore.Backup-Not-Found"));
            }
        }
    }
}
