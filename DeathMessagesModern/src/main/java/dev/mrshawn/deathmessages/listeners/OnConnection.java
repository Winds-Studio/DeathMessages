package dev.mrshawn.deathmessages.listeners;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.PlayerCtx;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnConnection implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        PlayerCtx.create(new PlayerCtx(p));

        if (DeathMessages.getHooks().bungeeInit) {
            DeathMessages.getInstance().foliaLib.getScheduler().runLater(() -> {
                if (DeathMessages.getHooks().bungeeServerNameRequest) {
                    PluginMessaging.sendServerNameRequest(p);
                }
            }, 5);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        PlayerCtx.remove(e.getPlayer().getUniqueId());
    }
}
