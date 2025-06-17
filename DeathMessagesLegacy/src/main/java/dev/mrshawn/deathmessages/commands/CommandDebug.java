package dev.mrshawn.deathmessages.commands;

import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.enums.Permission;
import dev.mrshawn.deathmessages.utils.ComponentUtil;
import dev.mrshawn.deathmessages.utils.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandDebug extends DeathMessagesCommand {

    @Override
    public String command() {
        return "debug";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission(Permission.DEATHMESSAGES_COMMAND_BLACKLIST.getValue())) {
            ComponentUtil.sendMessage(sender, Util.formatMessage("Commands.DeathMessages.No-Permission"));
            return;
        }
        if (args.length == 0) {
            ComponentUtil.sendMessage(sender, Util.formatMessage("Commands.DeathMessages.Sub-Commands.Blacklist.Help"));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        String targetName = (target != null) ? target.getName() : args[0];

        if (target == null || !PlayerManager.getPlayer(target).isPresent()) {
            sender.sendMessage("player not found");
            return;
        }

        PlayerManager.getPlayer(target).ifPresent(player -> {
            int cooldown = player.getCooldown();

            TextComponent.Builder component = Component.text();

            component.append(Component.text("player: " + targetName + " is in cooldown: " + cooldown + "s"));

            ComponentUtil.sendMessage(sender, component.build());
        });
    }
}
