package dev.mrshawn.deathmessages.commands.alias;

import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.enums.Permission;
import dev.mrshawn.deathmessages.utils.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandDeathMessagesToggle implements CommandExecutor {

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String cmdLabel, final String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Util.formatMessage("Commands.DeathMessages.Player-Only-Command"));
            return false;
        }
        if (!player.hasPermission(Permission.DEATHMESSAGES_COMMAND_TOGGLE.getValue())) {
            player.sendMessage(Util.formatMessage("Commands.DeathMessages.No-Permission"));
            return false;
        }
        PlayerManager getPlayer = PlayerManager.getPlayer(player);
        if (getPlayer != null) {
            boolean msg = getPlayer.getMessagesEnabled();
            if(msg) {
                getPlayer.setMessagesEnabled(false);
                player.sendMessage(Util.formatMessage("Commands.DeathMessages.Sub-Commands.Toggle.Toggle-Off"));
            } else {
                getPlayer.setMessagesEnabled(true);
                player.sendMessage(Util.formatMessage("Commands.DeathMessages.Sub-Commands.Toggle.Toggle-On"));
            }
        }
        return false;
    }
}
