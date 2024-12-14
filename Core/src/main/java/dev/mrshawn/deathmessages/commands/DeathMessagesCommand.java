package dev.mrshawn.deathmessages.commands;

import org.bukkit.command.CommandSender;

public abstract class DeathMessagesCommand {

    public abstract String command();

    public abstract void onCommand(CommandSender sender, String[] args);

}
