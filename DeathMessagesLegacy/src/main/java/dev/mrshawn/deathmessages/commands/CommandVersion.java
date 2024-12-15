package dev.mrshawn.deathmessages.commands;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.enums.Permission;
import dev.mrshawn.deathmessages.utils.ComponentUtil;
import dev.mrshawn.deathmessages.utils.Updater;
import dev.mrshawn.deathmessages.utils.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

public class CommandVersion extends DeathMessagesCommand {

    @Override
    public String command() {
        return "version";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission(Permission.DEATHMESSAGES_COMMAND_VERSION.getValue())) {
            ComponentUtil.sendMessage(sender, Util.formatMessage("Commands.DeathMessages.No-Permission"));
            return;
        }

        Component message = Util.formatMessage("Commands.DeathMessages.Sub-Commands.Version")
                .replaceText(TextReplacementConfig.builder()
                        .matchLiteral("%version%")
                        .replacement(DeathMessages.getInstance().getDescription().getVersion())
                        .build())
                .replaceText(TextReplacementConfig.builder()
                        .matchLiteral("%authors%")
                        .replacement(String.join(", ", DeathMessages.getInstance().getDescription().getAuthors()))
                        .build());

        ComponentUtil.sendMessage(sender, message);

        ComponentUtil.sendMessage(sender, Component.text("Checking update..."));
        Updater.checkUpdate();
        switch (Updater.shouldUpdate) {
            case 0:
                ComponentUtil.sendMessage(sender, Component.text("Great! You are using the latest version.", NamedTextColor.GREEN));
                break;
            case 1:
                ComponentUtil.sendMessage(sender, Component.text().append(Component.text("Find a new version! Click to download: https://github.com/Winds-Studio/DeathMessages/releases", NamedTextColor.YELLOW))
                        .appendNewline()
                        .append(Component.text()
                                .append(Component.text("Current Version: ", NamedTextColor.YELLOW))
                                .append(Component.text(Updater.nowVer))
                                .append(Component.text(" | Latest Version: ", NamedTextColor.YELLOW))
                                .append(Component.text(Updater.latest))
                                .build()).build());
                break;
            case -1:
                ComponentUtil.sendMessage(sender, Component.text("Failed to check update!", NamedTextColor.RED));
                break;
        }
    }
}
