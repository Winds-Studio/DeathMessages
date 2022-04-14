package dev.mrshawn.deathmessages.command.deathmessages.alias;

import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.utils.Assets;
import dev.mrshawn.deathmessages.config.UserData;
import dev.mrshawn.deathmessages.enums.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandDeathMessagesToggle implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String cmdLabel, String[] args) {
        if (sender instanceof Player && !sender.hasPermission(Permission.DEATHMESSAGES_COMMAND.getValue())) {
            sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.No-Permission"));
            return false;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.Player-Only-Command"));
            return false;
        }
        Player p = (Player) sender;
        if (!p.hasPermission(Permission.DEATHMESSAGES_COMMAND_TOGGLE.getValue())) {
            p.sendMessage(Assets.formatMessage("Commands.DeathMessages.No-Permission"));
            return false;
        }
        PlayerManager pm = PlayerManager.getPlayer(p);
        boolean b = UserData.getInstance().getConfig().getBoolean(p.getUniqueId().toString() + ".messages-enabled");
        if (b) {
            pm.setMessagesEnabled(false);
            p.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Toggle.Toggle-Off"));
        } else {
            pm.setMessagesEnabled(true);
            p.sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Toggle.Toggle-On"));
        }
        return false;
    }
}
