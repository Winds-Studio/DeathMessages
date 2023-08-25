package dev.mrshawn.deathmessages.listeners;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class OnJoin implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		new BukkitRunnable() {
			@Override
			public void run() {
				if (PlayerManager.getPlayer(p) == null) new PlayerManager(p);
			}
		}.runTaskAsynchronously(DeathMessages.getInstance());

		if (!DeathMessages.bungeeInit) return;
		new BukkitRunnable() {
			@Override
			public void run() {
				if (DeathMessages.bungeeServerNameRequest) {
					PluginMessaging.sendServerNameRequest(p);
				}
			}
		}.runTaskLater(DeathMessages.getInstance(), 5);
	}
}