package dev.mrshawn.deathmessages.listeners;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnJoin implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		DeathMessages.getInstance().foliaLib.getScheduler().runAsync(task -> {
			if (PlayerManager.isEmpty(p)) new PlayerManager(p);
		});

		if (!DeathMessages.bungeeInit) return;

		DeathMessages.getInstance().foliaLib.getScheduler().runLater(() -> {
			if (DeathMessages.bungeeServerNameRequest) {
				PluginMessaging.sendServerNameRequest(p);
			}
		}, 5);
	}
}