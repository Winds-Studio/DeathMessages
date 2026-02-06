package dev.mrshawn.deathmessages.utils;

import dev.mrshawn.deathmessages.DeathMessages;
import org.bukkit.Bukkit;

public class PlatformUtil {

    //public static final boolean IS_FOLIA = DeathMessages.getInstance().foliaLib.isFolia();

    public static void init() {
        if (Util.isOlderAndEqual(20, 4)) {
            DeathMessages.LOGGER.error("You should use DeathMessagesLegacy for version <= 1.20.4");
            Bukkit.getPluginManager().disablePlugin(DeathMessages.getInstance());
        }
    }
}
