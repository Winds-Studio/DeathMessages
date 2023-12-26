package dev.mrshawn.deathmessages.listeners.customlisteners;

import dev.mrshawn.deathmessages.api.ExplosionManager;
import dev.mrshawn.deathmessages.api.events.DMBlockExplodeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BlockExplosion implements Listener {

	@EventHandler
	public void onExplode(@NotNull DMBlockExplodeEvent e) {
		Optional<ExplosionManager> explosions = ExplosionManager.getExplosion(e.getBlock().getLocation());
		explosions.ifPresent(explosionManager -> {
			if (explosionManager.getLocation() == null) {
				explosionManager.setLocation(e.getBlock().getLocation());
			}
		});
	}
}
