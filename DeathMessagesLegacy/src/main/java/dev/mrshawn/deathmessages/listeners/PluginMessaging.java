package dev.mrshawn.deathmessages.listeners;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.config.Messages;
import dev.mrshawn.deathmessages.files.Config;
import dev.mrshawn.deathmessages.files.FileStore;
import dev.mrshawn.deathmessages.utils.ComponentUtil;
import dev.mrshawn.deathmessages.utils.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import org.jspecify.annotations.NullMarked;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.List;

@NullMarked
public class PluginMessaging implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] messageBytes) {
        if (!channel.equals("BungeeCord")) return;

        DataInputStream stream = new DataInputStream(new ByteArrayInputStream(messageBytes));

        try {
            String subChannel = stream.readUTF();

            if (subChannel.equals("GetServer")) {
                String serverName = stream.readUTF();
                DeathMessages.LOGGER.info("Server-Name successfully initialized from Bungee! ({})", serverName);
                DeathMessages.getHooks().bungeeServerName = DeathMessages.getHooks().bungeeServerDisplayName = serverName;
                FileStore.CONFIG.set(Config.HOOKS_BUNGEE_SERVER_NAME_DISPLAY_NAME, Config.HOOKS_BUNGEE_SERVER_NAME_DISPLAY_NAME, serverName); // name get from bungee will override displayname, why?
                FileStore.CONFIG.save();
                DeathMessages.getHooks().bungeeServerNameRequest = false;
            } else if (subChannel.equals("DeathMessages")) {
                String[] data = stream.readUTF().split("######");
                String serverName = data[0];
                String rawMsg = data[1];

                if (DeathMessages.getHooks().bungeeServerName != null && serverName.equals(DeathMessages.getHooks().bungeeServerName))
                    return; // Don't send to self

                Component prefix = Util.convertFromLegacy(Messages.getInstance().getConfig().getString("Bungee.Message"))
                        .replaceText(TextReplacementConfig.builder()
                                .matchLiteral("%server_name%")
                                .replacement(serverName)
                                .build());
                TextComponent message = Util.convertFromLegacy(rawMsg);

                for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
                    PlayerManager getPlayer = PlayerManager.getPlayer(onlinePlayer);
                    if (getPlayer != null) {
                        if (getPlayer.getMessagesEnabled()) {
                            ComponentUtil.sendMessage(onlinePlayer, Component.text()
                                    .append(prefix)
                                    .append(message)
                                    .build());
                        }
                    }
                }
            }
        } catch (Exception e) {
            DeathMessages.LOGGER.error(e);
        }
    }

    public static void sendServerNameRequest(Player player) {
        if (!FileStore.CONFIG.getBoolean(Config.HOOKS_BUNGEE_ENABLED)) return;

        DeathMessages.LOGGER.info("Attempting to initialize server-name variable from Bungee...");
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServer");
        player.sendPluginMessage(DeathMessages.getInstance(), "BungeeCord", out.toByteArray());
    }

    public static void sendPluginMSG(Player player, String msg) {
        if (!FileStore.CONFIG.getBoolean(Config.HOOKS_BUNGEE_ENABLED)) return;

        if (FileStore.CONFIG.getBoolean(Config.HOOKS_BUNGEE_SERVER_GROUPS_ENABLED)) {
            List<String> serverList = FileStore.CONFIG.getStringList(Config.HOOKS_BUNGEE_SERVER_GROUPS_SERVERS);
            for (String server : serverList) {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Forward");
                out.writeUTF(server);
                out.writeUTF("DeathMessages");
                out.writeUTF(DeathMessages.getHooks().bungeeServerDisplayName + "######" + msg);
                player.sendPluginMessage(DeathMessages.getInstance(), "BungeeCord", out.toByteArray());
            }
        } else {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Forward");
            out.writeUTF("ONLINE");
            out.writeUTF("DeathMessages");
            out.writeUTF(DeathMessages.getHooks().bungeeServerDisplayName + "######" + msg);
            player.sendPluginMessage(DeathMessages.getInstance(), "BungeeCord", out.toByteArray());
        }
    }
}
