package net.joshb.deathmessages.command.deathmessages;

import github.scarsz.discordsrv.DiscordSRV;
import me.joshb.discordbotapi.server.DiscordBotAPI;
import net.joshb.deathmessages.DeathMessages;
import net.joshb.deathmessages.assets.Assets;
import net.joshb.deathmessages.assets.CommentedConfiguration;
import net.joshb.deathmessages.config.Messages;
import net.joshb.deathmessages.config.Settings;
import net.joshb.deathmessages.enums.Permission;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CommandDiscordLog extends DeathMessagesCommand {


    @Override
    public String command() {
        return "discordlog";
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(!sender.hasPermission(Permission.DEATHMESSAGES_COMMAND_DISCORDLOG.getValue())){
            sender.sendMessage(Assets.formatMessage("Commands.DeathMessages.No-Permission"));
            return;
        }
        List<String> discordLog = Messages.getInstance().getConfig().getStringList("Commands.DeathMessages.Sub-Commands.DiscordLog");
        CommentedConfiguration settings = Settings.getInstance().getConfig();
        String discordJar;
        if(DeathMessages.discordBotAPIExtension != null){
            discordJar = "DiscordBotAPI";
        } else if(DeathMessages.discordSRVExtension != null){
            discordJar = "DiscordSRV";
        } else {
            discordJar = "Discord Jar Not Installed";
        }
        String discordToken;
        if(discordJar.equals("DiscordBotAPI")){
            discordToken = DiscordBotAPI.getJDA().getToken().length() > 40 ? DiscordBotAPI.getJDA().getToken().substring(40) : "Token Not Set";
        } else if(DeathMessages.discordSRVExtension != null){
            discordToken = DiscordSRV.getPlugin().getJda().getToken().length() > 40 ? DiscordSRV.getPlugin().getJda().getToken().substring(40) : "Token Not Set";
        } else {
            discordToken = "Discord Jar Not Installed";
        }
        for(String log : discordLog){
            if(log.equals("%discordConfig%")){
                sender.sendMessage(Assets.colorize("  &aEnabled: &c" + settings.getBoolean("Hooks.Discord.Enabled")));
                sender.sendMessage(Assets.colorize("  &aChannels:"));
                //Player
                sender.sendMessage(Assets.colorize("    &aPlayer-Enabled: &c" + settings.getBoolean("Hooks.Discord.Player.Enabled")));
                sender.sendMessage(Assets.colorize("    &aPlayer-Channels:"));
                for(String channels : settings.getStringList("Hooks.Discord.Channels.Player.Channels")){
                    sender.sendMessage("      - " + channels);
                }
                //Mob
                sender.sendMessage(Assets.colorize("    &aMob-Enabled: &c" + settings.getBoolean("Hooks.Discord.Mob.Enabled")));
                sender.sendMessage(Assets.colorize("    &aMob-Channels:"));
                for(String channels : settings.getStringList("Hooks.Discord.Channels.Mob.Channels")){
                    sender.sendMessage("      - " + channels);
                }
                //Player
                sender.sendMessage(Assets.colorize("    &aNatural-Enabled: &c" + settings.getBoolean("Hooks.Discord.Natural.Enabled")));
                sender.sendMessage(Assets.colorize("    &aNatural-Channels:"));
                for(String channels : settings.getStringList("Hooks.Discord.Channels.Natural.Channels")){
                    sender.sendMessage("      - " + channels);
                }
                //Player
                sender.sendMessage(Assets.colorize("    &aEntity-Enabled: &c" + settings.getBoolean("Hooks.Discord.Entity.Enabled")));
                sender.sendMessage(Assets.colorize("    &aEntity-Channels:"));
                for(String channels : settings.getStringList("Hooks.Discord.Channels.Entity.Channels")){
                    sender.sendMessage("      - " + channels);
                }
                continue;
            }
            sender.sendMessage(Assets.colorize(log
                    .replaceAll("%discordJar%", discordJar)
                    .replaceAll("%discordToken%", discordToken)
                    .replace("%prefix%", Messages.getInstance().getConfig().getString("Prefix"))));
        }
    }
}
