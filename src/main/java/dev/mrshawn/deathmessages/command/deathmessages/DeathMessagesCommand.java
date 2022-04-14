package dev.mrshawn.deathmessages.command.deathmessages;

import org.bukkit.command.CommandSender;

public abstract class DeathMessagesCommand {

    public abstract String command();

    public abstract void onCommand(CommandSender sender, String[] args);

}
