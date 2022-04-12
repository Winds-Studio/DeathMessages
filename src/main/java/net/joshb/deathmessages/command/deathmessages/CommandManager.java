package net.joshb.deathmessages.command.deathmessages;

import net.joshb.deathmessages.assets.Assets;
import net.joshb.deathmessages.config.Messages;
import net.joshb.deathmessages.enums.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager implements CommandExecutor {

    private final List<DeathMessagesCommand> commands = new ArrayList<>();

    public void initializeSubCommands(){
        commands.add(new CommandBackup());
        commands.add(new CommandBlacklist());
        commands.add(new CommandDiscordLog());
        commands.add(new CommandEdit());
        commands.add(new CommandReload());
        commands.add(new CommandRestore());
        commands.add(new CommandToggle());
        commands.add(new CommandVersion());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String cmdLabel, String[] args){
        if(sender instanceof Player && !sender.hasPermission(Permission.DEATHMESSAGES_COMMAND.getValue())){
            sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.No-Permission"));
            return false;
        }
        if(args.length == 0){
            for(String s : Assets.formatMessage(
                    Messages.getInstance().getConfig().getStringList("Commands.DeathMessages.Help"))){
                sender.sendMessage(s);
            }
        } else {
            DeathMessagesCommand cmd = get(args[0]);
            if (!(cmd == null)) {
                ArrayList<String> a = new ArrayList<>(Arrays.asList(args));
                a.remove(0);
                args = a.toArray(new String[a.size()]);
                cmd.onCommand(sender, args);
                return false;
            }
            for(String s : Assets.formatMessage(
                    Messages.getInstance().getConfig().getStringList("Commands.DeathMessages.Help"))){
                sender.sendMessage(s);
            }
        }
        return false;
    }

    private DeathMessagesCommand get(String name) {
        for (DeathMessagesCommand cmd : commands) {
            if (cmd.command().equalsIgnoreCase(name))
                return cmd;
        }
        return null;
    }
}
