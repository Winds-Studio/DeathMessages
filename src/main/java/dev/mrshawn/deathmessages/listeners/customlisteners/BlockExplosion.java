package dev.mrshawn.deathmessages.listeners.customlisteners;

import dev.mrshawn.deathmessages.api.ExplosionManager;
import dev.mrshawn.deathmessages.api.events.DMBlockExplodeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BlockExplosion implements Listener {

	@EventHandler
	public void onExplode(DMBlockExplodeEvent e) {
		ExplosionManager explosionManager = ExplosionManager.getExplosion(e.getBlock().getLocation());
		if (explosionManager != null && explosionManager.getLocation() == null) {
			explosionManager.setLocation(e.getBlock().getLocation());
		}
	}
}
