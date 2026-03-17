package dev.mrshawn.deathmessages.listeners;

import dev.mrshawn.deathmessages.config.files.Config;
import dev.mrshawn.deathmessages.config.files.FileStore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeath implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (FileStore.CONFIG.getBoolean(Config.DISABLE_DEFAULT_MESSAGES)) {
            event.setDeathMessage(null);
        }
    }
}
