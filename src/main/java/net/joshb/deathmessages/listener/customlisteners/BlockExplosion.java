package net.joshb.deathmessages.listener.customlisteners;

import net.joshb.deathmessages.api.ExplosionManager;
import net.joshb.deathmessages.api.events.DMBlockExplodeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BlockExplosion implements Listener {

    @EventHandler
    public void onExplode(DMBlockExplodeEvent e){
        ExplosionManager explosionManager = ExplosionManager.getExplosion(e.getBlock().getLocation());
        if(explosionManager != null && explosionManager.getLocation() == null){
           explosionManager.setLocation(e.getBlock().getLocation());
        }
    }
}
