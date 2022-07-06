package dev.mrshawn.deathmessages.listeners;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.config.Messages;
import dev.mrshawn.deathmessages.files.Config;
import dev.mrshawn.deathmessages.files.FileSettings;
import dev.mrshawn.deathmessages.kotlin.files.FileStore;
import dev.mrshawn.deathmessages.utils.Assets;
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

	private static final FileSettings<Config> config = FileStore.INSTANCE.getCONFIG();

	public void onPluginMessageReceived(String channel, @NotNull Player player, byte[] messageBytes) {
		if (!channel.equals("BungeeCord")) return;

		DataInputStream in = new DataInputStream(new ByteArrayInputStream(messageBytes));
		try {
			String subChannel = in.readUTF();

			if (subChannel.equals("GetServer")) {
				String serverName = in.readUTF();
				DeathMessages.getInstance().getLogger().info("Server-Name successfully initialized from Bungee! (" + serverName + ")");
				DeathMessages.bungeeServerName = serverName;
				config.set(Config.HOOKS_BUNGEE_SERVER_NAME_DISPLAY_NAME, Config.HOOKS_BUNGEE_SERVER_NAME_DISPLAY_NAME, serverName);
				config.save();
				DeathMessages.bungeeServerNameRequest = false;
			} else if (subChannel.equals("DeathMessages")) {
				String[] data = in.readUTF().split("######");
				String serverName = data[0];
				String rawMsg = data[1];
				TextComponent prefix = new TextComponent(Assets.colorize(Messages.getInstance().getConfig().getString("Bungee.Message").replaceAll("%server_name%", serverName)));
				TextComponent message = new TextComponent(ComponentSerializer.parse(rawMsg));
				for (Player pls : Bukkit.getOnlinePlayers()) {
					PlayerManager pms = PlayerManager.getPlayer(pls);
					if (pms.getMessagesEnabled()) {
						pls.spigot().sendMessage(prefix, message);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void sendServerNameRequest(Player p) {
		if (!config.getBoolean(Config.HOOKS_BUNGEE_ENABLED)) return;
		DeathMessages.getInstance().getLogger().info("Attempting to initialize server-name variable from Bungee...");
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("GetServer");
		p.sendPluginMessage(DeathMessages.getInstance(), "BungeeCord", out.toByteArray());
	}

	public static void sendPluginMSG(Player p, String msg) {
		if (!config.getBoolean(Config.HOOKS_BUNGEE_ENABLED)) return;
		if (config.getBoolean(Config.HOOKS_BUNGEE_SERVER_GROUPS_ENABLED)) {
			List<String> serverList = config.getStringList(Config.HOOKS_BUNGEE_SERVER_GROUPS_SERVERS);
			for (String server : serverList) {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("Forward");
				out.writeUTF(server);
				out.writeUTF("DeathMessages");
				out.writeUTF(DeathMessages.bungeeServerName + "######" + msg);
				p.sendPluginMessage(DeathMessages.getInstance(), "BungeeCord", out.toByteArray());
			}
		} else {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("Forward");
			out.writeUTF("ONLINE");
			out.writeUTF("DeathMessages");
			out.writeUTF(DeathMessages.bungeeServerName + "######" + msg);
			p.sendPluginMessage(DeathMessages.getInstance(), "BungeeCord", out.toByteArray());
		}
	}
}
