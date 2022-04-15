package dev.mrshawn.deathmessages.listeners;

import dev.mrshawn.deathmessages.files.Config;
import dev.mrshawn.deathmessages.files.FileSettings;
import dev.mrshawn.deathmessages.kotlin.files.FileStore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeath implements Listener {

    private static final FileSettings config = FileStore.INSTANCE.getCONFIG();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent e){
        if (config.getBoolean(Config.DISABLE_DEFAULT_MESSAGES)) {
            e.setDeathMessage(null);
        }
    }
}
