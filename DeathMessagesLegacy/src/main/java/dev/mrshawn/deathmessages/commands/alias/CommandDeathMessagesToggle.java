package dev.mrshawn.deathmessages.commands.alias;

import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.enums.Permission;
import dev.mrshawn.deathmessages.utils.ComponentUtil;
import dev.mrshawn.deathmessages.utils.Util;
import org.jetbrains.annotations.NotNull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandDeathMessagesToggle implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String cmdLabel, String[] args) {
        if (!(sender instanceof Player)) {
            ComponentUtil.sendMessage(sender, Util.formatMessage("Commands.DeathMessages.Player-Only-Command"));
            return false;
        }
        Player player = (Player) sender;
        if (!player.hasPermission(Permission.DEATHMESSAGES_COMMAND_TOGGLE.getValue())) {
            ComponentUtil.sendMessage(player, Util.formatMessage("Commands.DeathMessages.No-Permission"));
            return false;
        }
        PlayerManager getPlayer = PlayerManager.getPlayer(player);
        if (getPlayer != null) {
            boolean msg = getPlayer.getMessagesEnabled();
            if (msg) {
                getPlayer.setMessagesEnabled(false);
                ComponentUtil.sendMessage(player, Util.formatMessage("Commands.DeathMessages.Sub-Commands.Toggle.Toggle-Off"));
            } else {
                getPlayer.setMessagesEnabled(true);
                ComponentUtil.sendMessage(player, Util.formatMessage("Commands.DeathMessages.Sub-Commands.Toggle.Toggle-On"));
            }
        }
        return false;
    }
}
