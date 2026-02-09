package dev.mrshawn.deathmessages.listeners.customlisteners;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.EntityCtx;
import dev.mrshawn.deathmessages.api.PlayerCtx;
import dev.mrshawn.deathmessages.api.events.BroadcastEntityDeathMessageEvent;
import dev.mrshawn.deathmessages.config.Messages;
import dev.mrshawn.deathmessages.config.files.Config;
import dev.mrshawn.deathmessages.config.files.FileStore;
import dev.mrshawn.deathmessages.listeners.PluginMessaging;
import dev.mrshawn.deathmessages.utils.Assets;
import dev.mrshawn.deathmessages.utils.ComponentUtil;
import dev.mrshawn.deathmessages.utils.EntityUtil;
import dev.mrshawn.deathmessages.utils.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class BroadcastEntityDeathListener implements Listener {

    @EventHandler
    public void broadcastListener(BroadcastEntityDeathMessageEvent e) {
        final PlayerCtx playerCtx = e.getPlayerContext();
        final Entity entity = e.getEntity();
        final boolean hasOwner = EntityUtil.hasOwner(entity);
        final TextComponent[] components = e.getTextComponents();
        final TextComponent prefix = components[0];
        final TextComponent messageBody = components[1];
        final TextComponent message = prefix.append(messageBody);

        if (Messages.getInstance().getConfig().getBoolean("Console.Enabled")) {
            Component rawMessage = Util.convertFromLegacy(Messages.getInstance().getConfig().getString("Console.Message"));
            Component consoleMessage = Assets.entityDeathPlaceholders(rawMessage, playerCtx.getPlayer(), entity, hasOwner)
                    .replaceText(TextReplacementConfig.builder()
                            .matchLiteral("%message%")
                            .replacement(message)
                            .build());

            ComponentUtil.sendConsoleMessage(consoleMessage);
        }

        if (playerCtx.isInCooldown()) {
            return;
        } else {
            playerCtx.setCooldown();
        }

        final boolean privateTameable = FileStore.CONFIG.getBoolean(Config.PRIVATE_MESSAGES_MOBS);
        boolean discordSent = false;

        for (World world : e.getBroadcastedWorlds()) {
            if (FileStore.CONFIG.getStringList(Config.DISABLED_WORLDS).contains(world.getName())) {
                continue;
            }

            for (Player otherPlayer : world.getPlayers()) {
                PlayerCtx otherPlayerCtx = PlayerCtx.of(otherPlayer.getUniqueId());
                if (privateTameable) {
                    if (otherPlayerCtx != null) {
                        if (otherPlayerCtx.getUUID().equals(playerCtx.getUUID())) {
                            if (otherPlayerCtx.isMessageEnabled()) {
                                otherPlayer.sendMessage(message);
                            }
                        }
                    }
                } else {
                    if (otherPlayerCtx != null) {
                        if (otherPlayerCtx.isMessageEnabled()) {
                            if (DeathMessages.getHooks().worldGuardExtension != null) {
                                if (DeathMessages.getHooks().worldGuardExtension.denyFromRegion(otherPlayer, e.getMessageType().getValue())) {
                                    return;
                                }
                            }

                            otherPlayer.sendMessage(message);
                            PluginMessaging.sendPluginMSG(otherPlayerCtx.getPlayer(), Util.convertToLegacy(message));
                        }
                    }

                    if (FileStore.CONFIG.getBoolean(Config.HOOKS_DISCORD_WORLD_WHITELIST_ENABLED)) {
                        List<String> discordWorldWhitelist = FileStore.CONFIG.getStringList(Config.HOOKS_DISCORD_WORLD_WHITELIST_WORLDS);
                        boolean broadcastToDiscord = false;
                        for (World broadcastWorld : e.getBroadcastedWorlds()) {
                            if (discordWorldWhitelist.contains(broadcastWorld.getName())) {
                                broadcastToDiscord = true;
                            }
                        }
                        if (!broadcastToDiscord) {
                            // Won't reach the discord broadcast
                            return;
                        }
                        // Will reach the discord broadcast
                    }

                    if (DeathMessages.getHooks().discordSRVExtension != null && !discordSent) {
                        DeathMessages.getHooks().discordSRVExtension.sendEntityDiscordMessage(
                                components,
                                e.getMessageType(),
                                playerCtx,
                                entity,
                                hasOwner
                        );
                        discordSent = true;
                    }
                }
            }
        }

        PluginMessaging.sendPluginMSG(e.getPlayerContext().getPlayer(), Util.convertToLegacy(message));
        EntityCtx.remove(entity.getUniqueId());
    }
}
