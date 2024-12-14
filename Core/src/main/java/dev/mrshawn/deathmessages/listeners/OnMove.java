package dev.mrshawn.deathmessages.listeners;

import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.utils.Assets;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Optional;

public class OnMove implements Listener {

    private boolean falling;
    private Material lastBlock;
    private boolean message;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Optional<PlayerManager> getPlayer = PlayerManager.getPlayer(p);
        getPlayer.ifPresent(pm -> {
            lastBlock = e.getTo().getBlock().getType();
            if (Assets.isClimbable(lastBlock)) {
                pm.setLastClimbing(lastBlock);
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
        });
    }
}
