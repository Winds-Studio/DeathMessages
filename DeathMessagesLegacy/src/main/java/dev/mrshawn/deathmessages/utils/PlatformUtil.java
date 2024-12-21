package dev.mrshawn.deathmessages.utils;

public class PlatformUtil {

    //public static final boolean IS_FOLIA = DeathMessages.getInstance().foliaLib.isFolia();

    public static void init() {
        if (Util.isNewerAndEqual(20, 5)) {
            throw new UnsupportedOperationException("You should use DeathMessagesModern for version >= 1.20.5");
        }
    }
}
