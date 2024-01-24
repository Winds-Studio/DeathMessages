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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Optional;

public class BroadcastPlayerDeathListener implements Listener {

	private static final FileSettings<Config> config = FileStore.INSTANCE.getCONFIG();
	private boolean discordSent = false;

	@EventHandler
	public void broadcastListener(BroadcastDeathMessageEvent e) {
		if (e.getTextComponent().equals(Component.empty())) return; // Dreeam - in Assets: return null -> renturn Component.empty()

		Optional<PlayerManager> getPlayer = PlayerManager.getPlayer(e.getPlayer());

		if (!getPlayer.isPresent()) return;

		if (Messages.getInstance().getConfig().getBoolean("Console.Enabled")) {
			// Dreeam TODO: maybe just use formatMessage is also ok?
			Component message = Assets.playerDeathPlaceholders(Assets.convertFromLegacy(Messages.getInstance().getConfig().getString("Console.Message")), getPlayer.get(), e.getLivingEntity());
			DeathMessages.getInstance().adventure().console().sendMessage(
					message.replaceText(TextReplacementConfig.builder()
							.matchLiteral("%message%")
							.replacement(e.getTextComponent())
							.build())
			);
		}

		if (getPlayer.get().isInCooldown()) {
			return;
		} else {
			getPlayer.get().setCooldown();
		}

		boolean privatePlayer = config.getBoolean(Config.PRIVATE_MESSAGES_PLAYER);
		boolean privateMobs = config.getBoolean(Config.PRIVATE_MESSAGES_MOBS);
		boolean privateNatural = config.getBoolean(Config.PRIVATE_MESSAGES_NATURAL);

		// To reset for each death message
		discordSent = false;

		for (World w : e.getBroadcastedWorlds()) {
			if (config.getStringList(Config.DISABLED_WORLDS).contains(w.getName())) {
				continue;
			}
			for (Player pls : w.getPlayers()) {
				Optional<PlayerManager> getPlayer2 = PlayerManager.getPlayer(pls);
				getPlayer2.ifPresent(pms -> {
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
				});
				if (!getPlayer2.isPresent()) {
					new PlayerManager(pls);
				}
			}
		}
		PluginMessaging.sendPluginMSG(e.getPlayer(), Assets.convertToLegacy(e.getTextComponent()));
	}

	private void normal(BroadcastDeathMessageEvent e, PlayerManager pm, Player player, List<World> worlds) {
		if (DeathMessages.worldGuardExtension != null) {
			if (DeathMessages.worldGuardExtension.getRegionState(player, e.getMessageType().getValue()).equals(StateFlag.State.DENY)
					|| DeathMessages.worldGuardExtension.getRegionState(e.getPlayer(), e.getMessageType().getValue()).equals(StateFlag.State.DENY)) {
				return;
			}
		}
		if (pm.getMessagesEnabled()) {
			DeathMessages.getInstance().adventure().player(player).sendMessage(e.getTextComponent());
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
				// Won't reach the discord broadcast
				return;
			}
			// Will reach the discord broadcast
		}
		Optional<PlayerManager> getPlayer = PlayerManager.getPlayer(e.getPlayer());
		if (getPlayer.isPresent()) {
			if (DeathMessages.discordBotAPIExtension != null && !discordSent) {
				DeathMessages.discordBotAPIExtension.sendDiscordMessage(getPlayer.get(), e.getMessageType(), PlainTextComponentSerializer.plainText().serialize(e.getTextComponent()));
				discordSent = true;
			}
			if (DeathMessages.discordSRVExtension != null && !discordSent) {
				DeathMessages.discordSRVExtension.sendDiscordMessage(getPlayer.get(), e.getMessageType(), PlainTextComponentSerializer.plainText().serialize(e.getTextComponent()));
				discordSent = true;
			}
		}
	}
}
