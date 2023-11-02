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
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

public class BroadcastEntityDeathListener implements Listener {

	private static final FileSettings<Config> config = FileStore.INSTANCE.getCONFIG();

	@EventHandler
	public void broadcastListener(BroadcastEntityDeathMessageEvent e) {
		Optional<PlayerManager> pm = Optional.ofNullable(e.getPlayer());

		if (pm.isEmpty()) return;

		boolean hasOwner = e.getEntity() instanceof Tameable;
        if (e.isCancelled()) return;
		if (Messages.getInstance().getConfig().getBoolean("Console.Enabled")) {
			String message = Assets.entityDeathPlaceholders(Messages.getInstance().getConfig().getString("Console.Message"), pm.get().getPlayer(), e.getEntity(), hasOwner);
			message = message
					.replaceAll("%message%", Matcher.quoteReplacement(LegacyComponentSerializer.legacyAmpersand().serialize(e.getTextComponent())));
			DeathMessages.getInstance().adventure().sender(Bukkit.getConsoleSender()).sendMessage(Assets.convertFromLegacy(message));
		}
		if (pm.get().isInCooldown()) {
			return;
		} else {
			pm.get().setCooldown();
		}

		boolean discordSent = false;

		boolean privateTameable = config.getBoolean(Config.PRIVATE_MESSAGES_MOBS);

		for (World w : e.getBroadcastedWorlds()) {
			for (Player player : w.getPlayers()) {
				if (config.getStringList(Config.DISABLED_WORLDS).contains(w.getName())) {
					continue;
				}
				if (privateTameable) {
					Optional<PlayerManager> getPlayer = PlayerManager.getPlayer(player);
					getPlayer.ifPresent(pms -> {
						if (pms.getUUID().equals(pm.get().getPlayer().getUniqueId())) {
							if (pms.getMessagesEnabled()) {
								DeathMessages.getInstance().adventure().player(player).sendMessage(e.getTextComponent());
							}
						}
					});
				} else {
					Optional<PlayerManager> getPlayer = PlayerManager.getPlayer(player);
					getPlayer.ifPresent(pms -> {
						if (pms.getMessagesEnabled()) {
							if (DeathMessages.worldGuardExtension != null) {
								if (DeathMessages.worldGuardExtension.getRegionState(player, e.getMessageType().getValue()).equals(StateFlag.State.DENY)) {
									return;
								}
							}
							DeathMessages.getInstance().adventure().player(player).sendMessage(e.getTextComponent());
							PluginMessaging.sendPluginMSG(pms.getPlayer(), e.getTextComponent().toString());
						}
					});
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
						DeathMessages.discordBotAPIExtension.sendEntityDiscordMessage(PlainTextComponentSerializer.plainText().serialize(e.getTextComponent()), pm.get(), e.getEntity(), hasOwner, e.getMessageType());
						discordSent = true;
					}
					if (DeathMessages.discordSRVExtension != null && !discordSent) {
						DeathMessages.discordSRVExtension.sendEntityDiscordMessage(PlainTextComponentSerializer.plainText().serialize(e.getTextComponent()), pm.get(), e.getEntity(), hasOwner, e.getMessageType());
						discordSent = true;
					}
				}
			}
		}
		PluginMessaging.sendPluginMSG(e.getPlayer().getPlayer(), LegacyComponentSerializer.legacyAmpersand().serialize(e.getTextComponent()));
		Optional<EntityManager> getEntity = EntityManager.getEntity(e.getEntity().getUniqueId());
		getEntity.ifPresent(EntityManager::destroy);
	}
}
