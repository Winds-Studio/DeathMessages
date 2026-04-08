package dev.mrshawn.deathmessages.nms;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.utils.PlatformUtil;
import dev.mrshawn.deathmessages.utils.Util;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;

public class NMSProvider {

    private static NMSAdaptor instance;

    public static void initNMS() {
        final String instClassPath;
        if (PlatformUtil.IS_PAPER) {
            if (PlatformUtil.isNewerAndEqual(21, 4)) {
                instClassPath = "paper.v1_21_4";
            } else if (PlatformUtil.isNewerAndEqual(21, 3)) {
                instClassPath = "paper.v1_21_3";
            } else if (PlatformUtil.isNewerAndEqual(16, 5)) {
                instClassPath = "paper.v1_16_5";
            } else if (PlatformUtil.isNewerAndEqual(13, 0)) { // Just use Spigot impl for <= Paper 1.16.5
                instClassPath = "spigot.v1_13";
            } else if (PlatformUtil.isNewerAndEqual(12, 2)) {
                instClassPath = "spigot.v1_12_2";
            } else {
                // TODO - Whether need to support lower?
                throw new UnsupportedOperationException();
            }
        } else {
            if (PlatformUtil.isNewerAndEqual(21, 3)) {
                instClassPath = "spigot.v1_21_3";
            } else if (PlatformUtil.isNewerAndEqual(16, 5)) {
                instClassPath = "spigot.v1_16_5";
            } else if (PlatformUtil.isNewerAndEqual(13, 0)) {
                instClassPath = "spigot.v1_13";
            } else if (PlatformUtil.isNewerAndEqual(12, 2)) {
                instClassPath = "spigot.v1_12_2";
            } else {
                // TODO - Whether need to support lower?
                throw new UnsupportedOperationException();
            }
        }

        try {
            instance = (NMSAdaptor) Class.forName(Util.NMS_PACKAGE_PREFIX_NAME + instClassPath + ".NMSAdaptorImpl").getConstructor().newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            DeathMessages.LOGGER.error("Could not find NMS implementation for {}", instClassPath, e);
            Bukkit.getPluginManager().disablePlugin(DeathMessages.getInstance());
        }
    }

    public static NMSAdaptor get() {
        return instance;
    }
}
