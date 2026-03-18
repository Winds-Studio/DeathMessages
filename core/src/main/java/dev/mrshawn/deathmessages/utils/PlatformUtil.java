package dev.mrshawn.deathmessages.utils;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.nms.NMSProvider;

public class PlatformUtil {

    public static final boolean IS_PAPER = Util.doesClassExists("io.papermc.paper.configuration.GlobalConfiguration");
    public static final boolean IS_FOLIA = DeathMessages.getInstance().foliaLib.isFolia();

    public static void init() {
        NMSProvider.initNMS();
    }
}
