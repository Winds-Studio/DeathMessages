package dev.mrshawn.deathmessages.listeners;

import dev.mrshawn.deathmessages.config.Config;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeath implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (Config.settings.DISABLE_DEFAULT_MESSAGES()) {
			event.setDeathMessage(null);
		}
	}
}
