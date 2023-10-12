package dev.mrshawn.deathmessages.listeners.customlisteners;

import com.sk89q.worldguard.protection.flags.StateFlag;
import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.api.events.BroadcastDeathMessageEvent;
import dev.mrshawn.deathmessages.config.Messages;
import dev.mrshawn.deathmessages.enums.MessageType;
import dev.mrshawn.deathmessages.files.Config;
import dev.mrshawn.deathmessages.files.FileSettings;
import dev.mrshawn.deathmessages.kotlin.files.FileStore;
import dev.mrshawn.deathmessages.listeners.PluginMessaging;
import dev.mrshawn.deathmessages.utils.Assets;
import java.util.List;
import java.util.regex.Matcher;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BroadcastPlayerDeathListener implements Listener {

	private static final FileSettings<Config> config = FileStore.INSTANCE.getCONFIG();

	private boolean discordSent = false;

	@EventHandler
	public void broadcastListener(BroadcastDeathMessageEvent e) {

		if (!e.isCancelled()) {
			if (Messages.getInstance().getConfig().getBoolean("Console.Enabled")) {
				String message = Assets.playerDeathPlaceholders(Messages.getInstance().getConfig().getString("Console.Message"), PlayerManager.getPlayer(e.getPlayer()), e.getLivingEntity());
				message = message.replaceAll("%message%", Matcher.quoteReplacement(LegacyComponentSerializer.legacyAmpersand().serialize(e.getTextComponent())));
				Bukkit.getConsoleSender().sendMessage(Assets.convertLegacy(message));
			}

			PlayerManager pm = PlayerManager.getPlayer(e.getPlayer());
			if (pm.isInCooldown()) {
				return;
			} else {
				pm.setCooldown();
			}

			boolean privatePlayer = config.getBoolean(Config.PRIVATE_MESSAGES_PLAYER);
			boolean privateMobs = config.getBoolean(Config.PRIVATE_MESSAGES_MOBS);
			boolean privateNatural = config.getBoolean(Config.PRIVATE_MESSAGES_NATURAL);

			//To reset for each death message
			discordSent = false;

			for (World w : e.getBroadcastedWorlds()) {
				if (config.getStringList(Config.DISABLED_WORLDS).contains(w.getName())) {
					continue;
				}
				for (Player pls : w.getPlayers()) {
					PlayerManager pms = PlayerManager.getPlayer(pls);
					if (pms == null) {
						pms = new PlayerManager(pls);
					}
					if (e.getMessageType().equals(MessageType.PLAYER)) {
						if (privatePlayer && (e.getPlayer().getUniqueId().equals(pms.getUUID())
								|| e.getLivingEntity().getUniqueId().equals(pms.getUUID()))) {
							normal(e, pms, pls, e.getBroadcastedWorlds());
						} else if (!privatePlayer) {
							normal(e, pms, pls, e.getBroadcastedWorlds());
						}
					} else if (e.getMessageType().equals(MessageType.MOB)) {
						if (privateMobs && e.getPlayer().getUniqueId().equals(pms.getUUID())) {
							normal(e, pms, pls, e.getBroadcastedWorlds());
						} else if (!privateMobs) {
							normal(e, pms, pls, e.getBroadcastedWorlds());
						}
					} else if (e.getMessageType().equals(MessageType.NATURAL)) {
						if (privateNatural && e.getPlayer().getUniqueId().equals(pms.getUUID())) {
							normal(e, pms, pls, e.getBroadcastedWorlds());
						} else if (!privateNatural) {
							normal(e, pms, pls, e.getBroadcastedWorlds());
						}
					}
				}
			}
			PluginMessaging.sendPluginMSG(e.getPlayer(), LegacyComponentSerializer.legacyAmpersand().serialize(e.getTextComponent()));
		}
	}

	private void normal(BroadcastDeathMessageEvent e, PlayerManager pm, Player player, List<World> worlds) {
		if (DeathMessages.worldGuardExtension != null) {
			if (DeathMessages.worldGuardExtension.getRegionState(player, e.getMessageType().getValue()).equals(StateFlag.State.DENY)
					|| DeathMessages.worldGuardExtension.getRegionState(e.getPlayer(), e.getMessageType().getValue()).equals(StateFlag.State.DENY)) {
				return;
			}
		}
		try {
			if (pm.getMessagesEnabled()) {
				player.sendMessage(e.getTextComponent());
			}
			if (config.getBoolean(Config.HOOKS_DISCORD_WORLD_WHITELIST_ENABLED)) {
				List<String> discordWorldWhitelist = config.getStringList(Config.HOOKS_DISCORD_WORLD_WHITELIST_WORLDS);
				boolean broadcastToDiscord = false;
				for (World world : worlds) {
					if (discordWorldWhitelist.contains(world.getName())) {
						broadcastToDiscord = true;
					}
				}
				if (!broadcastToDiscord) {
					//Wont reach the discord broadcast
					return;
				}
				//Will reach the discord broadcast
			}
			if (DeathMessages.discordBotAPIExtension != null && !discordSent) {
				DeathMessages.discordBotAPIExtension.sendDiscordMessage(PlayerManager.getPlayer(e.getPlayer()), e.getMessageType(), PlainTextComponentSerializer.plainText().serialize(e.getTextComponent()));
				discordSent = true;
			}
			if (DeathMessages.discordSRVExtension != null && !discordSent) {
				DeathMessages.discordSRVExtension.sendDiscordMessage(PlayerManager.getPlayer(e.getPlayer()), e.getMessageType(), PlainTextComponentSerializer.plainText().serialize(e.getTextComponent()));
				discordSent = true;
			}
		} catch (NullPointerException e1) {
			e1.printStackTrace();
		}
	}
}
