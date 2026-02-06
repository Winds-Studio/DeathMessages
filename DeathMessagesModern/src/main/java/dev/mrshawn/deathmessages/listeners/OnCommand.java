package dev.mrshawn.deathmessages.listeners;

import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.config.Settings;
import dev.mrshawn.deathmessages.files.Config;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class OnCommand implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        PlayerManager getPlayer = PlayerManager.getPlayer(e.getPlayer());
        List<String> commands = Settings.getInstance().getConfig().getStringList(Config.CUSTOM_SUICIDE_COMMANDS.getPath());

        if (getPlayer != null) {
            for (String command : commands) {
                if (e.getMessage().equals(command)) {
                    getPlayer.setCommandDeath(true);
                    break;
                }
            }
        }
    }
}
