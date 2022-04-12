package net.joshb.deathmessages.command.deathmessages;

import net.joshb.deathmessages.assets.Assets;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TabCompleter implements org.bukkit.command.TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if(!args[0].equalsIgnoreCase("edit")){
            return null;
        }
        // /dm edit <player> <mobName> <solo, gang> <damage-type> <add, remove, list> (add=message, remove=placeholder, list=noArgs)
        if(args.length == 2){
            // /dm edit <args>
            List<String> arguments = new ArrayList<>();
            arguments.add("player");
            arguments.add("entity");
            return arguments;
        } else if(args.length == 3){
            // /dm edit <player> <mobName>
            if(args[1].equalsIgnoreCase("player") ||  args[1].equalsIgnoreCase("entity")){
                // Not checking config cause we can add sections if we want
                //List<String> mobNames = new ArrayList<>(PlayerDeathMessages.getInstance().getConfig()
                  //      .getConfigurationSection("Mobs").getKeys(false));
                List<String> mobNames = new ArrayList<>();
                for(EntityType entityType : EntityType.values()){
                    if(entityType.isAlive()){
                        mobNames.add(entityType.getEntityClass().getSimpleName().toLowerCase());
                    }
                }
                return mobNames;
            }
        } else if(args.length == 4){
            // /dm edit <player> <mobName> <solo, gang>
            if(args[1].equalsIgnoreCase("player")){
                List<String> arguments = new ArrayList<>();
                arguments.add("solo");
                arguments.add("gang");
                return arguments;
            } else if(args[1].equalsIgnoreCase("entity")){
                return Assets.damageTypes;
            }
        } else if(args.length == 5){
            // /dm edit <player> <mobName> <solo, gang> <damage-type>
            if(args[1].equalsIgnoreCase("player")){
                return Assets.damageTypes;
            } else if(args[1].equalsIgnoreCase("entity")){
                List<String> arguments = new ArrayList<>();
                arguments.add("add");
                arguments.add("remove");
                arguments.add("list");
                return arguments;
            }
        } else if(args.length == 6){
            // /dm edit <player> <mobName> <solo, gang> <damage-type> <add, remove, list>
            if(args[1].equalsIgnoreCase("player")){
                List<String> arguments = new ArrayList<>();
                arguments.add("add");
                arguments.add("remove");
                arguments.add("list");
                return arguments;
            }
        }
        return null;
    }
}
