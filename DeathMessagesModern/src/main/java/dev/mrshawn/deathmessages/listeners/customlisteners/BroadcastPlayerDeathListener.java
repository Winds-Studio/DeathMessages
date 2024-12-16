package dev.mrshawn.deathmessages.listeners.customlisteners;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.api.events.BroadcastDeathMessageEvent;
import dev.mrshawn.deathmessages.config.Messages;
import dev.mrshawn.deathmessages.enums.MessageType;
import dev.mrshawn.deathmessages.files.Config;
import dev.mrshawn.deathmessages.files.FileStore;
import dev.mrshawn.deathmessages.listeners.PluginMessaging;
import dev.mrshawn.deathmessages.utils.Assets;
import dev.mrshawn.deathmessages.utils.ComponentUtil;
import dev.mrshawn.deathmessages.utils.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Optional;

public class BroadcastPlayerDeathListener implements Listener {

    private boolean discordSent = false;

    @EventHandler
    public void broadcastListener(BroadcastDeathMessageEvent e) {
        if (e.getTextComponents() == ComponentUtil.EMPTY)
            return; // Dreeam - in Assets: return null -> return ComponentUtil.EMPTY

        Optional<PlayerManager> getPlayer = PlayerManager.getPlayer(e.getPlayer());

        if (!getPlayer.isPresent()) return;

        final TextComponent[] components = e.getTextComponents();
        final TextComponent prefix = components[0];
        final TextComponent messageBody = components[1];
        final TextComponent message = prefix != null ? prefix.append(messageBody) : messageBody;

        if (Messages.getInstance().getConfig().getBoolean("Console.Enabled")) {
            // Dreeam TODO: maybe just use formatMessage is also ok?
            Component rawMessage = Util.convertFromLegacy(Messages.getInstance().getConfig().getString("Console.Message"));
            Component consoleMessage = Assets.playerDeathPlaceholders(rawMessage, getPlayer.get(), e.getLivingEntity())
                    .replaceText(TextReplacementConfig.builder()
                            .matchLiteral("%message%")
                            .replacement(message)
                            .build());

            ComponentUtil.sendConsoleMessage(consoleMessage);
        }

        if (getPlayer.get().isInCooldown()) {
            return;
        } else {
            getPlayer.get().setCooldown();
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
                Optional<PlayerManager> getPlayer2 = PlayerManager.getPlayer(player);
                getPlayer2.ifPresent(pms -> {
                    if (e.getMessageType().equals(MessageType.PLAYER)) {
                        if (privatePlayer && (e.getPlayer().getUniqueId().equals(pms.getUUID())
                                || e.getLivingEntity().getUniqueId().equals(pms.getUUID()))) {
                            normal(e, components, message, pms, player, e.getBroadcastedWorlds());
                        } else if (!privatePlayer) {
                            normal(e, components, message, pms, player, e.getBroadcastedWorlds());
                        }
                    } else if (e.getMessageType().equals(MessageType.MOB)) {
                        if (privateMobs && e.getPlayer().getUniqueId().equals(pms.getUUID())) {
                            normal(e, components, message, pms, player, e.getBroadcastedWorlds());
                        } else if (!privateMobs) {
                            normal(e, components, message, pms, player, e.getBroadcastedWorlds());
                        }
                    } else if (e.getMessageType().equals(MessageType.NATURAL)) {
                        if (privateNatural && e.getPlayer().getUniqueId().equals(pms.getUUID())) {
                            normal(e, components, message, pms, player, e.getBroadcastedWorlds());
                        } else if (!privateNatural) {
                            normal(e, components, message, pms, player, e.getBroadcastedWorlds());
                        }
                    }
                });

                if (!getPlayer2.isPresent()) {
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

        Optional<PlayerManager> getPlayer = PlayerManager.getPlayer(e.getPlayer());
        if (getPlayer.isPresent()) {
            if (DeathMessages.getHooks().discordSRVExtension != null && !discordSent) {
                DeathMessages.getHooks().discordSRVExtension.sendDiscordMessage(
                        components,
                        e.getMessageType(),
                        getPlayer.get()
                );
                discordSent = true;
            }
        }
    }
}
