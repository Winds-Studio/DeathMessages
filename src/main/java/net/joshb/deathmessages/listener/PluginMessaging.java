package net.joshb.deathmessages.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.joshb.deathmessages.DeathMessages;
import net.joshb.deathmessages.api.PlayerManager;
import net.joshb.deathmessages.assets.Assets;
import net.joshb.deathmessages.config.Messages;
import net.joshb.deathmessages.config.Settings;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.List;
import java.util.logging.Level;

public class PluginMessaging implements PluginMessageListener {

    public void onPluginMessageReceived(String channel, @NotNull Player player, byte[] messageBytes) {
        if (!channel.equals("BungeeCord")) return;

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(messageBytes));
        try {
            String subChannel = in.readUTF();

            if (subChannel.equals("GetServer")) {
                String serverName = in.readUTF();
                DeathMessages.plugin.getLogger().log(Level.INFO, "Server-Name successfully initialized from Bungee! (" + serverName + ")");
                DeathMessages.bungeeServerName = serverName;
                Settings.getInstance().getConfig().set("Hooks.Bungee.Server-Name.Display-Name", serverName);
                Settings.getInstance().save();
                DeathMessages.bungeeServerNameRequest = false;
            } else if (subChannel.equals("DeathMessages")) {
                String[] data = in.readUTF().split("######");
                String serverName = data[0];
                String rawMsg = data[1];
                TextComponent prefix = new TextComponent(Assets.colorize(Messages.getInstance().getConfig().getString("Bungee.Message").replaceAll("%server_name%", serverName)));
                TextComponent message = new TextComponent(ComponentSerializer.parse(rawMsg));
                for (Player pls : Bukkit.getOnlinePlayers()) {
                    PlayerManager pms = PlayerManager.getPlayer(pls);
                    if(pms.getMessagesEnabled()){
                        pls.spigot().sendMessage(prefix, message);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendServerNameRequest(Player p) {
        if (!Settings.getInstance().getConfig().getBoolean("Hooks.Bungee.Enabled")) return;
        DeathMessages.plugin.getLogger().log(Level.INFO, "Attempting to initialize server-name variable from Bungee...");
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServer");
        p.sendPluginMessage(DeathMessages.plugin, "BungeeCord", out.toByteArray());
    }

    public static void sendPluginMSG(Player p, String msg) {
        if (!Settings.getInstance().getConfig().getBoolean("Hooks.Bungee.Enabled")) return;
        if (Settings.getInstance().getConfig().getBoolean("Hooks.Bungee.Server-Groups.Enabled")) {
            List<String> serverList = Settings.getInstance().getConfig().getStringList("Hooks.Bungee.Server-Groups.Servers");
            for (String server : serverList) {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Forward");
                out.writeUTF(server);
                out.writeUTF("DeathMessages");
                out.writeUTF(DeathMessages.bungeeServerName + "######" + msg);
                p.sendPluginMessage(DeathMessages.plugin, "BungeeCord", out.toByteArray());
            }
        } else {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Forward");
            out.writeUTF("ONLINE");
            out.writeUTF("DeathMessages");
            out.writeUTF(DeathMessages.bungeeServerName + "######" + msg);
            p.sendPluginMessage(DeathMessages.plugin, "BungeeCord", out.toByteArray());
        }
    }
}
