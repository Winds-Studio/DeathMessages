package dev.mrshawn.deathmessages.commands;

import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.config.Settings;
import dev.mrshawn.deathmessages.config.UserData;
import dev.mrshawn.deathmessages.enums.Permission;
import dev.mrshawn.deathmessages.files.Config;
import dev.mrshawn.deathmessages.utils.ComponentUtil;
import dev.mrshawn.deathmessages.utils.Util;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class CommandBlacklist extends DeathMessagesCommand {

    @Override
    public String command() {
        return "blacklist";
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
        String targetName = (target != null) ? target.getDisplayName() : args[0];

        TextReplacementConfig player = TextReplacementConfig.builder()
                .matchLiteral("%player%")
                .replacement(targetName)
                .build();

        // Saved-User-Data disabled
        // Only can be used on online players
        if (!Settings.getInstance().getConfig().getBoolean(Config.SAVED_USER_DATA.getPath())) {
            if (target != null && target.isOnline()) {
                Optional<PlayerManager> getPlayer = PlayerManager.getPlayer(target);
                getPlayer.ifPresent(pm -> {
                    if (pm.isBlacklisted()) {
                        pm.setBlacklisted(false);
                        ComponentUtil.sendMessage(sender, Util.formatMessage("Commands.DeathMessages.Sub-Commands.Blacklist.Blacklist-Remove")
                                .replaceText(player));
                    } else {
                        pm.setBlacklisted(true);
                        ComponentUtil.sendMessage(sender, Util.formatMessage("Commands.DeathMessages.Sub-Commands.Blacklist.Blacklist-Add")
                                .replaceText(player));
                    }
                });
            } else {
                ComponentUtil.sendMessage(sender, Util.formatMessage("Commands.DeathMessages.Sub-Commands.Blacklist.Username-None-Existent")
                        .replaceText(player));
            }
            return;
        }

        // Saved-User-Data enabled
        // Can be used on all players stored in userData
        for (Map.Entry<String, Object> entry : UserData.getInstance().getConfig().getValues(false).entrySet()) {
            String username = UserData.getInstance().getConfig().getString(entry.getKey() + ".username");

            if (username.equalsIgnoreCase(args[0])) {
                Optional<PlayerManager> getPlayer = PlayerManager.getPlayer(UUID.fromString(entry.getKey()));
                boolean blacklisted = UserData.getInstance().getConfig().getBoolean(entry.getKey() + ".is-blacklisted");

                if (blacklisted) {
                    getPlayer.ifPresent(pm -> pm.setBlacklisted(false));
                    if (!getPlayer.isPresent()) {
                        UserData.getInstance().getConfig().set(entry.getKey() + ".is-blacklisted", false);
                        UserData.getInstance().save();
                    }
                    ComponentUtil.sendMessage(sender, Util.formatMessage("Commands.DeathMessages.Sub-Commands.Blacklist.Blacklist-Remove")
                            .replaceText(player));
                } else {
                    getPlayer.ifPresent(pm -> pm.setBlacklisted(true));
                    if (!getPlayer.isPresent()) {
                        UserData.getInstance().getConfig().set(entry.getKey() + ".is-blacklisted", true);
                        UserData.getInstance().save();
                    }
                    ComponentUtil.sendMessage(sender, Util.formatMessage("Commands.DeathMessages.Sub-Commands.Blacklist.Blacklist-Add")
                            .replaceText(player));
                }

                return;
            }
        }
        // Can't find args[0] in userData, then does not exist
        ComponentUtil.sendMessage(sender, Util.formatMessage("Commands.DeathMessages.Sub-Commands.Blacklist.Username-None-Existent")
                .replaceText(TextReplacementConfig.builder().match("%player%").replacement(args[0]).build()));
    }
}
