package dev.mrshawn.deathmessages.hooks;

import dev.mrshawn.deathmessages.DeathMessages;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

public class CommonVanishPluginHook {

    public static boolean isPluginVanished(Player p) {
        if (DeathMessages.getHooks().commonVanishPluginsEnabled) {
            for (MetadataValue meta : p.getMetadata("vanished")) {
                if (meta.asBoolean()) return true;
            }
        }
        return false;
    }
}
