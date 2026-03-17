package dev.mrshawn.deathmessages.listeners;

import dev.mrshawn.deathmessages.api.PlayerCtx;
import dev.mrshawn.deathmessages.config.Settings;
import dev.mrshawn.deathmessages.config.files.Config;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class OnCommand implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        final PlayerCtx playerCtx = PlayerCtx.of(e.getPlayer().getUniqueId());

        if (playerCtx != null) {
            final List<String> commands = Settings.getInstance().getConfig().getStringList(Config.CUSTOM_SUICIDE_COMMANDS.getPath());
            if (commands.contains(e.getMessage())) {
                playerCtx.setCommandDeath(true);
            }
        }
    }
}
