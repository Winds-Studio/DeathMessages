package dev.mrshawn.deathmessages.listeners;

import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.utils.Assets;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class OnMove implements Listener {

	boolean falling;
	Material lastBlock;
	boolean message;

	@EventHandler(priority = EventPriority.MONITOR)
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		PlayerManager pm = PlayerManager.getPlayer(p);
		if (pm == null) return; // Dreeam - No NPE
		if (Assets.isClimbable(e.getTo().getBlock())) {
			pm.setLastClimbing(e.getTo().getBlock().getType());
			lastBlock = e.getTo().getBlock().getType();
		} else {
			if (p.getFallDistance() > 0) {
				message = true;
				if (!falling) {
					falling = true;
					message = false;
				}
			} else {
				if (message) {
					pm.setLastClimbing(null);
					falling = false;
					message = false;
				}
			}
		}
	}
}
