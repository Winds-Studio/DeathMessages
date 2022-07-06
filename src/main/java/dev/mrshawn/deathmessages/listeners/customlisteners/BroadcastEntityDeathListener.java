package dev.mrshawn.deathmessages.listeners.customlisteners;

import com.sk89q.worldguard.protection.flags.StateFlag;
import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.EntityManager;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.api.events.BroadcastEntityDeathMessageEvent;
import dev.mrshawn.deathmessages.config.Messages;
import dev.mrshawn.deathmessages.files.Config;
import dev.mrshawn.deathmessages.files.FileSettings;
import dev.mrshawn.deathmessages.kotlin.files.FileStore;
import dev.mrshawn.deathmessages.listeners.PluginMessaging;
import dev.mrshawn.deathmessages.utils.Assets;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.regex.Matcher;

public class BroadcastEntityDeathListener implements Listener {

	private static final FileSettings<Config> config = FileStore.INSTANCE.getCONFIG();

	@EventHandler
	public void broadcastListener(BroadcastEntityDeathMessageEvent e) {
		PlayerManager pm = e.getPlayer();
		boolean hasOwner = false;
		if (e.getEntity() instanceof Tameable tameable) {
			if (tameable.getOwner() != null) hasOwner = true;
		}
		if (!e.isCancelled()) {
			if (Messages.getInstance().getConfig().getBoolean("Console.Enabled")) {
				String message = Assets.entityDeathPlaceholders(Messages.getInstance().getConfig().getString("Console.Message"), pm.getPlayer(), e.getEntity(), hasOwner);
				message = message.replaceAll("%message%", Matcher.quoteReplacement(e.getTextComponent().toLegacyText()));
				Bukkit.getConsoleSender().sendMessage(message);
			}
			if (pm.isInCooldown()) {
				return;
			} else {
				pm.setCooldown();
			}

			boolean discordSent = false;

			boolean privateTameable = config.getBoolean(Config.PRIVATE_MESSAGES_MOBS);

			for (World w : e.getBroadcastedWorlds()) {
				for (Player pls : w.getPlayers()) {
					if (config.getStringList(Config.DISABLED_WORLDS).contains(w.getName())) {
						continue;
					}
					PlayerManager pms = PlayerManager.getPlayer(pls);
					if (privateTameable && pms.getUUID().equals(pm.getPlayer().getUniqueId())) {
						if (pms.getMessagesEnabled()) {
							pls.spigot().sendMessage(e.getTextComponent());
						}
					} else {
						if (pms.getMessagesEnabled()) {
							if (DeathMessages.worldGuardExtension != null) {
								if (DeathMessages.worldGuardExtension.getRegionState(pls, e.getMessageType().getValue()).equals(StateFlag.State.DENY)) {
									return;
								}
							}
							pls.spigot().sendMessage(e.getTextComponent());
							PluginMessaging.sendPluginMSG(pms.getPlayer(), e.getTextComponent().toString());
						}
						if (config.getBoolean(Config.HOOKS_DISCORD_WORLD_WHITELIST_ENABLED)) {
							List<String> discordWorldWhitelist = config.getStringList(Config.HOOKS_DISCORD_WORLD_WHITELIST_WORLDS);
							boolean broadcastToDiscord = false;
							for (World world : e.getBroadcastedWorlds()) {
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
							DeathMessages.discordBotAPIExtension.sendEntityDiscordMessage(ChatColor.stripColor(e.getTextComponent().toLegacyText()), pm, e.getEntity(), hasOwner, e.getMessageType());
							discordSent = true;
						}
						if (DeathMessages.discordSRVExtension != null && !discordSent) {
							DeathMessages.discordSRVExtension.sendEntityDiscordMessage(ChatColor.stripColor(e.getTextComponent().toLegacyText()), pm, e.getEntity(), hasOwner, e.getMessageType());
							discordSent = true;
						}
					}
				}
			}
			PluginMessaging.sendPluginMSG(e.getPlayer().getPlayer(), ComponentSerializer.toString(e.getTextComponent()));
		}
		EntityManager.getEntity(e.getEntity().getUniqueId()).destroy();
	}
}
