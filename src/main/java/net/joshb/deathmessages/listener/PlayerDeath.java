package net.joshb.deathmessages.listener;

import net.joshb.deathmessages.config.Settings;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeath implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent e){
        if(Settings.getInstance().getConfig().getBoolean("Disable-Default-Messages")){
            e.setDeathMessage(null);
        }
    }
}
