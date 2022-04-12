package net.joshb.deathmessages.listener.customlisteners;

import com.sk89q.worldguard.protection.flags.StateFlag;
import net.joshb.deathmessages.DeathMessages;
import net.joshb.deathmessages.api.PlayerManager;
import net.joshb.deathmessages.api.events.BroadcastDeathMessageEvent;
import net.joshb.deathmessages.assets.Assets;
import net.joshb.deathmessages.config.Messages;
import net.joshb.deathmessages.config.Settings;
import net.joshb.deathmessages.enums.MessageType;
import net.joshb.deathmessages.listener.PluginMessaging;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.regex.Matcher;

public class BroadcastPlayerDeathListener implements Listener {

    private boolean discordSent = false;

    @EventHandler
    public void broadcastListener(BroadcastDeathMessageEvent e) {

        if (!e.isCancelled()) {
            if (Messages.getInstance().getConfig().getBoolean("Console.Enabled")) {
                String message = Assets.playerDeathPlaceholders(Messages.getInstance().getConfig().getString("Console.Message"), PlayerManager.getPlayer(e.getPlayer()), e.getLivingEntity());
                message = message.replaceAll("%message%", Matcher.quoteReplacement(e.getTextComponent().toLegacyText()));
                Bukkit.getConsoleSender().sendMessage(message);
            }

            PlayerManager pm = PlayerManager.getPlayer(e.getPlayer());
            if(pm.isInCooldown()){
                return;
            } else {
                pm.setCooldown();
            }

            boolean privatePlayer = Settings.getInstance().getConfig().getBoolean("Private-Messages.Player");
            boolean privateMobs = Settings.getInstance().getConfig().getBoolean("Private-Messages.Mobs");
            boolean privateNatural = Settings.getInstance().getConfig().getBoolean("Private-Messages.Natural");

            //To reset for each death message
            discordSent = false;

            for (World w : e.getBroadcastedWorlds()) {
                if(Settings.getInstance().getConfig().getStringList("Disabled-Worlds").contains(w.getName())){
                    continue;
                }
                for (Player pls : w.getPlayers()) {
                    PlayerManager pms = PlayerManager.getPlayer(pls);
                    if(pms == null){
                        pms = new PlayerManager(pls);
                    }
                    if(e.getMessageType().equals(MessageType.PLAYER)){
                        if (privatePlayer && (e.getPlayer().getUniqueId().equals(pms.getUUID())
                                || e.getLivingEntity().getUniqueId().equals(pms.getUUID()))) {
                            normal(e, pms, pls, e.getBroadcastedWorlds());
                        } else if(!privatePlayer){
                            normal(e, pms, pls, e.getBroadcastedWorlds());
                        }
                    } else if (e.getMessageType().equals(MessageType.MOB)){
                        if (privateMobs && e.getPlayer().getUniqueId().equals(pms.getUUID())) {
                            normal(e, pms, pls, e.getBroadcastedWorlds());
                        } else if(!privateMobs){
                            normal(e, pms, pls, e.getBroadcastedWorlds());
                        }
                    } else if (e.getMessageType().equals(MessageType.NATURAL)){
                        if (privateNatural && e.getPlayer().getUniqueId().equals(pms.getUUID())) {
                            normal(e, pms, pls, e.getBroadcastedWorlds());
                        } else if(!privateNatural){
                            normal(e, pms, pls, e.getBroadcastedWorlds());
                        }
                    }
                }
            }
            PluginMessaging.sendPluginMSG(e.getPlayer(), ComponentSerializer.toString(e.getTextComponent()));
        }
    }

    private void normal(BroadcastDeathMessageEvent e, PlayerManager pms, Player pls, List<World> worlds){
        if (DeathMessages.worldGuardExtension != null) {
            if (DeathMessages.worldGuardExtension.getRegionState(pls, e.getMessageType().getValue()).equals(StateFlag.State.DENY)
                || DeathMessages.worldGuardExtension.getRegionState(e.getPlayer(), e.getMessageType().getValue()).equals(StateFlag.State.DENY)) {
                return;
            }
        }
        try {
            if (pms.getMessagesEnabled()) {
                pls.spigot().sendMessage(e.getTextComponent());
            }
            if(Settings.getInstance().getConfig().getBoolean("Hooks.Discord.World-Whitelist.Enabled")) {
                List<String> discordWorldWhitelist = Settings.getInstance().getConfig().getStringList("Hooks.Discord.World-Whitelist.Worlds");
                boolean broadcastToDiscord = false;
                for(World world : worlds){
                    if(discordWorldWhitelist.contains(world.getName())){
                        broadcastToDiscord = true;
                    }
                }
                if(!broadcastToDiscord){
                    //Wont reach the discord broadcast
                    return;
                }
                //Will reach the discord broadcast
            }
            if (DeathMessages.discordBotAPIExtension != null && !discordSent) {
                DeathMessages.discordBotAPIExtension.sendDiscordMessage(PlayerManager.getPlayer(e.getPlayer()), e.getMessageType(), ChatColor.stripColor(e.getTextComponent().toLegacyText()));
                discordSent = true;
            }
            if (DeathMessages.discordSRVExtension != null && !discordSent) {
                DeathMessages.discordSRVExtension.sendDiscordMessage(PlayerManager.getPlayer(e.getPlayer()), e.getMessageType(), ChatColor.stripColor(e.getTextComponent().toLegacyText()));
                discordSent = true;
            }
        } catch (NullPointerException e1){
            e1.printStackTrace();
        }
    }
}
