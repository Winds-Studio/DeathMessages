package dev.mrshawn.deathmessages.listeners;

import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.utils.MaterialUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class OnMove implements Listener {

    private boolean falling;
    private Material lastBlock;
    private boolean message;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        PlayerManager getPlayer = PlayerManager.getPlayer(p);
        if (getPlayer != null) {
            lastBlock = e.getTo().getBlock().getType();
            if (MaterialUtil.isClimbable(lastBlock)) {
                getPlayer.setLastClimbing(lastBlock);
            } else {
                if (p.getFallDistance() > 0) {
                    message = true;
                    if (!falling) {
                        falling = true;
                        message = false;
                    }
                } else {
                    if (message) {
                        getPlayer.setLastClimbing(null);
                        falling = false;
                        message = false;
                    }
                }
            }
        }
    }
}
