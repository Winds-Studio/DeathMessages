package dev.mrshawn.deathmessages.utils;

import dev.mrshawn.deathmessages.DeathMessages;
import org.bukkit.Bukkit;

public class PlatformUtil {

    //public static final boolean IS_FOLIA = DeathMessages.getInstance().foliaLib.isFolia();

    public static void init() {
        if (Util.isNewerAndEqual(20, 5)) {
            DeathMessages.LOGGER.error("You should use DeathMessagesModern for version >= 1.20.5");
            Bukkit.getPluginManager().disablePlugin(DeathMessages.getInstance());
        }
    }
}
