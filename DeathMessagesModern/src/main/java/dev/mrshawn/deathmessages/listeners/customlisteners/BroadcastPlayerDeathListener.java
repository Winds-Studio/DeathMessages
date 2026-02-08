package dev.mrshawn.deathmessages.listeners.customlisteners;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.api.events.BroadcastDeathMessageEvent;
import dev.mrshawn.deathmessages.config.Messages;
import dev.mrshawn.deathmessages.enums.MessageType;
import dev.mrshawn.deathmessages.config.files.Config;
import dev.mrshawn.deathmessages.config.files.FileStore;
import dev.mrshawn.deathmessages.listeners.PluginMessaging;
import dev.mrshawn.deathmessages.utils.Assets;
import dev.mrshawn.deathmessages.utils.ComponentUtil;
import dev.mrshawn.deathmessages.utils.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class BroadcastPlayerDeathListener implements Listener {

    private boolean discordSent = false;

    @EventHandler
    public void broadcastListener(BroadcastDeathMessageEvent e) {
        PlayerManager getPlayer = PlayerManager.getPlayer(e.getPlayer());

        if (getPlayer == null) return;

        final TextComponent[] components = e.getTextComponents();
        final TextComponent prefix = components[0];
        final TextComponent messageBody = components[1];
        final TextComponent message = prefix.append(messageBody);

        if (Messages.getInstance().getConfig().getBoolean("Console.Enabled")) {
            // Dreeam TODO: maybe just use formatMessage is also ok?
            Component rawMessage = Util.convertFromLegacy(Messages.getInstance().getConfig().getString("Console.Message"));
            Component consoleMessage = Assets.playerDeathPlaceholders(rawMessage, getPlayer, e.getLivingEntity())
                    .replaceText(TextReplacementConfig.builder()
                            .matchLiteral("%message%")
                            .replacement(message)
                            .build());

            ComponentUtil.sendConsoleMessage(consoleMessage);
        }

        if (getPlayer.isInCooldown()) {
            return;
        } else {
            getPlayer.setCooldown();
        }

        final boolean privatePlayer = FileStore.CONFIG.getBoolean(Config.PRIVATE_MESSAGES_PLAYER);
        final boolean privateMobs = FileStore.CONFIG.getBoolean(Config.PRIVATE_MESSAGES_MOBS);
        final boolean privateNatural = FileStore.CONFIG.getBoolean(Config.PRIVATE_MESSAGES_NATURAL);

        // To reset for each death message
        discordSent = false;

        for (World world : e.getBroadcastedWorlds()) {
            if (FileStore.CONFIG.getStringList(Config.DISABLED_WORLDS).contains(world.getName())) {
                continue;
            }

            for (Player player : world.getPlayers()) {
                PlayerManager getPlayer2 = PlayerManager.getPlayer(player);
                if (getPlayer2 != null) {
                    if (e.getMessageType().equals(MessageType.PLAYER)) {
                        if (privatePlayer && (e.getPlayer().getUniqueId().equals(getPlayer2.getUUID())
                                || e.getLivingEntity().getUniqueId().equals(getPlayer2.getUUID()))) {
                            normal(e, components, message, getPlayer2, player, e.getBroadcastedWorlds());
                        } else if (!privatePlayer) {
                            normal(e, components, message, getPlayer2, player, e.getBroadcastedWorlds());
                        }
                    } else if (e.getMessageType().equals(MessageType.MOB)) {
                        if (privateMobs && e.getPlayer().getUniqueId().equals(getPlayer2.getUUID())) {
                            normal(e, components, message, getPlayer2, player, e.getBroadcastedWorlds());
                        } else if (!privateMobs) {
                            normal(e, components, message, getPlayer2, player, e.getBroadcastedWorlds());
                        }
                    } else if (e.getMessageType().equals(MessageType.NATURAL)) {
                        if (privateNatural && e.getPlayer().getUniqueId().equals(getPlayer2.getUUID())) {
                            normal(e, components, message, getPlayer2, player, e.getBroadcastedWorlds());
                        } else if (!privateNatural) {
                            normal(e, components, message, getPlayer2, player, e.getBroadcastedWorlds());
                        }
                    }
                } else {
                    new PlayerManager(player);
                }
            }
        }

        PluginMessaging.sendPluginMSG(e.getPlayer(), Util.convertToLegacy(message));
    }

    private void normal(BroadcastDeathMessageEvent e, TextComponent[] components, TextComponent message, PlayerManager pm, Player player, List<World> worlds) {
        if (DeathMessages.getHooks().worldGuardExtension != null) {
            if (DeathMessages.getHooks().worldGuardExtension.denyFromRegion(player, e.getMessageType().getValue())
                    || DeathMessages.getHooks().worldGuardExtension.denyFromRegion(e.getPlayer(), e.getMessageType().getValue())) {
                return;
            }
        }

        if (pm.getMessagesEnabled()) {
            player.sendMessage(message);
        }

        if (FileStore.CONFIG.getBoolean(Config.HOOKS_DISCORD_WORLD_WHITELIST_ENABLED)) {
            List<String> discordWorldWhitelist = FileStore.CONFIG.getStringList(Config.HOOKS_DISCORD_WORLD_WHITELIST_WORLDS);
            boolean broadcastToDiscord = false;
            for (World world : worlds) {
                if (discordWorldWhitelist.contains(world.getName())) {
                    broadcastToDiscord = true;
                }
            }
            if (!broadcastToDiscord) {
                // Won't reach the discord broadcast
                return;
            }
            // Will reach the discord broadcast
        }

        PlayerManager getPlayer = PlayerManager.getPlayer(e.getPlayer());
        if (getPlayer != null) {
            if (DeathMessages.getHooks().discordSRVExtension != null && !discordSent) {
                DeathMessages.getHooks().discordSRVExtension.sendDiscordMessage(
                        components,
                        e.getMessageType(),
                        getPlayer
                );
                discordSent = true;
            }
        }
    }
}
