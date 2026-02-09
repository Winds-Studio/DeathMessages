package dev.mrshawn.deathmessages.commands;

import dev.mrshawn.deathmessages.api.PlayerCtx;
import dev.mrshawn.deathmessages.enums.Permission;
import dev.mrshawn.deathmessages.utils.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandToggle extends DeathMessagesCommand {

    @Override
    public String command() {
        return "toggle";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Util.formatMessage("Commands.DeathMessages.Player-Only-Command"));
            return;
        }
        if (!player.hasPermission(Permission.DEATHMESSAGES_COMMAND_TOGGLE.getValue())) {
            player.sendMessage(Util.formatMessage("Commands.DeathMessages.No-Permission"));
            return;
        }

        PlayerCtx playerCtx = PlayerCtx.of(player.getUniqueId());
        if (playerCtx != null) {
            if (playerCtx.isMessageEnabled()) {
                playerCtx.setMessageEnabled(false);
                player.sendMessage(Util.formatMessage("Commands.DeathMessages.Sub-Commands.Toggle.Toggle-Off"));
            } else {
                playerCtx.setMessageEnabled(true);
                player.sendMessage(Util.formatMessage("Commands.DeathMessages.Sub-Commands.Toggle.Toggle-On"));
            }
        }
    }
}
