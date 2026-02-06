package dev.mrshawn.deathmessages.listeners.customlisteners;

import dev.mrshawn.deathmessages.api.ExplosionManager;
import dev.mrshawn.deathmessages.api.events.DMBlockExplodeEvent;
import org.jetbrains.annotations.NotNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BlockExplosion implements Listener {

    @EventHandler
    public void onExplode(@NotNull DMBlockExplodeEvent e) {
        ExplosionManager explosions = ExplosionManager.getExplosion(e.getBlock().getLocation());
        if  (explosions != null) {
            if (explosions.getLocation() == null) {
                explosions.setLocation(e.getBlock().getLocation());
            }
        }
    }
}
