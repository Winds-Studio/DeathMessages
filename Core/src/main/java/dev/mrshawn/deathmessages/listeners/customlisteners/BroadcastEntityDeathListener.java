package dev.mrshawn.deathmessages.listeners.customlisteners;

import com.sk89q.worldguard.protection.flags.StateFlag;
import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.EntityManager;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.api.events.BroadcastEntityDeathMessageEvent;
import dev.mrshawn.deathmessages.config.Config;
import dev.mrshawn.deathmessages.config.legacy.Messages;
import dev.mrshawn.deathmessages.listeners.PluginMessaging;
import dev.mrshawn.deathmessages.utils.Assets;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Optional;

public class BroadcastEntityDeathListener implements Listener {

	@EventHandler
	public void broadcastListener(BroadcastEntityDeathMessageEvent e) {
		if (e.getTextComponent().equals(Component.empty())) return; // Dreeam - in Assets: return null -> renturn Component.empty()

		Optional<PlayerManager> pm = Optional.of(e.getPlayer());
		boolean hasOwner = e.getEntity() instanceof Tameable;

		if (Messages.getInstance().getConfig().getBoolean("Console.Enabled")) {
			Component message = Assets.entityDeathPlaceholders(Assets.convertFromLegacy(Messages.getInstance().getConfig().getString("Console.Message")), pm.get().getPlayer(), e.getEntity(), hasOwner);
			DeathMessages.getInstance().adventure().console().sendMessage(message.replaceText(TextReplacementConfig.builder()
					.matchLiteral("%message%")
					.replacement(e.getTextComponent())
					.build()));
		}

		if (pm.get().isInCooldown()) {
			return;
		} else {
			pm.get().setCooldown();
		}

		boolean privateTameable = Config.settings.PRIVATE_MESSAGES_MOBS();
		boolean discordSent = false;

		for (World w : e.getBroadcastedWorlds()) {
			for (Player player : w.getPlayers()) {
				if (Config.settings.DISABLED_WORLDS().contains(w.getName())) {
					continue;
				}
                Optional<PlayerManager> getPlayer = PlayerManager.getPlayer(player);
                if (privateTameable) {
                    getPlayer.ifPresent(pms -> {
						if (pms.getUUID().equals(pm.get().getPlayer().getUniqueId())) {
							if (pms.getMessagesEnabled()) {
								DeathMessages.getInstance().adventure().player(player).sendMessage(e.getTextComponent());
							}
						}
					});
				} else {
                    getPlayer.ifPresent(pms -> {
						if (pms.getMessagesEnabled()) {
							if (DeathMessages.worldGuardExtension != null) {
								if (DeathMessages.worldGuardExtension.getRegionState(player, e.getMessageType().getValue()).equals(StateFlag.State.DENY)) {
									return;
								}
							}
							DeathMessages.getInstance().adventure().player(player).sendMessage(e.getTextComponent());
							PluginMessaging.sendPluginMSG(pms.getPlayer(), Assets.convertToLegacy(e.getTextComponent()));
						}
					});
					if (Config.settings.HOOKS_DISCORD_WORLD_WHITELIST_ENABLED()) {
						List<String> discordWorldWhitelist = Config.settings.HOOKS_DISCORD_WORLD_WHITELIST_WORLDS();
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
					if (DeathMessages.discordSRVExtension != null && !discordSent) {
						DeathMessages.discordSRVExtension.sendEntityDiscordMessage(PlainTextComponentSerializer.plainText().serialize(e.getTextComponent()), pm.get(), e.getEntity(), hasOwner, e.getMessageType());
						discordSent = true;
					}
				}
			}
		}
		PluginMessaging.sendPluginMSG(e.getPlayer().getPlayer(), Assets.convertToLegacy(e.getTextComponent()));
		Optional<EntityManager> getEntity = EntityManager.getEntity(e.getEntity().getUniqueId());
		getEntity.ifPresent(EntityManager::destroy);
	}
}
