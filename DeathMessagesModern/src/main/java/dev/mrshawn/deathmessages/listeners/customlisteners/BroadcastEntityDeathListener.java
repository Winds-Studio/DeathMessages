package dev.mrshawn.deathmessages.listeners.customlisteners;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.EntityManager;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.api.events.BroadcastEntityDeathMessageEvent;
import dev.mrshawn.deathmessages.config.Messages;
import dev.mrshawn.deathmessages.files.Config;
import dev.mrshawn.deathmessages.files.FileStore;
import dev.mrshawn.deathmessages.listeners.PluginMessaging;
import dev.mrshawn.deathmessages.utils.Assets;
import dev.mrshawn.deathmessages.utils.ComponentUtil;
import dev.mrshawn.deathmessages.utils.EntityUtil;
import dev.mrshawn.deathmessages.utils.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Optional;

public class BroadcastEntityDeathListener implements Listener {

    @EventHandler
    public void broadcastListener(BroadcastEntityDeathMessageEvent e) {
        if (e.getTextComponent().equals(Component.empty()))
            return; // Dreeam - in Assets: return null -> renturn Component.empty()

        Optional<PlayerManager> pm = Optional.of(e.getPlayer());
        Entity entity = e.getEntity();
        boolean hasOwner = EntityUtil.hasOwner(entity);

        if (Messages.getInstance().getConfig().getBoolean("Console.Enabled")) {
            Component message = Assets.entityDeathPlaceholders(Util.convertFromLegacy(Messages.getInstance().getConfig().getString("Console.Message")), pm.get().getPlayer(), entity, hasOwner);
            ComponentUtil.sendConsoleMessage(message.replaceText(TextReplacementConfig.builder()
                    .matchLiteral("%message%")
                    .replacement(e.getTextComponent())
                    .build()));
        }

        if (pm.get().isInCooldown()) {
            return;
        } else {
            pm.get().setCooldown();
        }

        boolean privateTameable = FileStore.CONFIG.getBoolean(Config.PRIVATE_MESSAGES_MOBS);
        boolean discordSent = false;

        for (World w : e.getBroadcastedWorlds()) {
            if (FileStore.CONFIG.getStringList(Config.DISABLED_WORLDS).contains(w.getName())) {
                continue;
            }

            for (Player player : w.getPlayers()) {
                Optional<PlayerManager> getPlayer = PlayerManager.getPlayer(player);
                if (privateTameable) {
                    getPlayer.ifPresent(pms -> {
                        if (pms.getUUID().equals(pm.get().getPlayer().getUniqueId())) {
                            if (pms.getMessagesEnabled()) {
                                player.sendMessage(e.getTextComponent());
                            }
                        }
                    });
                } else {
                    getPlayer.ifPresent(pms -> {
                        if (pms.getMessagesEnabled()) {
                            if (DeathMessages.getHooks().worldGuardExtension != null) {
                                if (DeathMessages.getHooks().worldGuardExtension.denyFromRegion(player, e.getMessageType().getValue())) {
                                    return;
                                }
                            }
                            player.sendMessage(e.getTextComponent());
                            PluginMessaging.sendPluginMSG(pms.getPlayer(), Util.convertToLegacy(e.getTextComponent()));
                        }
                    });
                    if (FileStore.CONFIG.getBoolean(Config.HOOKS_DISCORD_WORLD_WHITELIST_ENABLED)) {
                        List<String> discordWorldWhitelist = FileStore.CONFIG.getStringList(Config.HOOKS_DISCORD_WORLD_WHITELIST_WORLDS);
                        boolean broadcastToDiscord = false;
                        for (World world : e.getBroadcastedWorlds()) {
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
                    if (DeathMessages.getHooks().discordSRVExtension != null && !discordSent) {
                        DeathMessages.getHooks().discordSRVExtension.sendEntityDiscordMessage(PlainTextComponentSerializer.plainText().serialize(e.getTextComponent()), pm.get(), entity, hasOwner, e.getMessageType());
                        discordSent = true;
                    }
                }
            }
        }

        PluginMessaging.sendPluginMSG(e.getPlayer().getPlayer(), Util.convertToLegacy(e.getTextComponent()));
        Optional<EntityManager> getEntity = EntityManager.getEntity(entity.getUniqueId());
        getEntity.ifPresent(EntityManager::destroy);
    }
}