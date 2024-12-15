package dev.mrshawn.deathmessages.commands;

import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.enums.Permission;
import dev.mrshawn.deathmessages.utils.ComponentUtil;
import dev.mrshawn.deathmessages.utils.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class CommandToggle extends DeathMessagesCommand {

    @Override
    public String command() {
        return "toggle";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            ComponentUtil.sendMessage(sender, Util.formatMessage("Commands.DeathMessages.Player-Only-Command"));
            return;
        }
        Player player = (Player) sender;
        if (!player.hasPermission(Permission.DEATHMESSAGES_COMMAND_TOGGLE.getValue())) {
            ComponentUtil.sendMessage(player, Util.formatMessage("Commands.DeathMessages.No-Permission"));
            return;
        }
        Optional<PlayerManager> getPlayer = PlayerManager.getPlayer(player);
        getPlayer.ifPresent(pm -> {
            boolean msg = pm.getMessagesEnabled();
            if (msg) {
                pm.setMessagesEnabled(false);
                ComponentUtil.sendMessage(player, Util.formatMessage("Commands.DeathMessages.Sub-Commands.Toggle.Toggle-Off"));
            } else {
                pm.setMessagesEnabled(true);
                ComponentUtil.sendMessage(player, Util.formatMessage("Commands.DeathMessages.Sub-Commands.Toggle.Toggle-On"));
            }
        });
    }
}
