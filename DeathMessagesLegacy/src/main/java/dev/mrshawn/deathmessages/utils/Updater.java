package dev.mrshawn.deathmessages.utils;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.config.Settings;
import dev.mrshawn.deathmessages.config.files.Config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Updater {

    public static int shouldUpdate = -1;
    public static String latest;
    public static final String nowVer = DeathMessages.getInstance().getDescription().getVersion();
    private static String updateURL = "https://raw.githubusercontent.com/Winds-Studio/DeathMessages/master/VERSION";

    public static void checkUpdate() {
        DeathMessages.getInstance().foliaLib.getScheduler().runAsync(task -> {
            try {
                if (Settings.getInstance().getConfig().getBoolean(Config.CHECK_DEV_VERSION.getPath())) {
                    // Dreeam TODO - check DEV version
                    // Settings.getInstance().getConfig().getBoolean(Config.CHECK_DEV_VERSION.getPath() || isDev(nowVerStripped))
                    latest = null;
                } else {
                    URL url = new URL(updateURL);
                    InputStream inputStream = url.openStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    latest = bufferedReader.readLine();
                }
            } catch (Exception e) {
                updateURL = "https://gitee.com/dreeam/DeathMessages/raw/master/VERSION";
                checkUpdate();
            }

            if (latest != null) {
                String nowVerStripped = nowVer;
                if (isDev(nowVerStripped)) {
                    nowVerStripped = nowVerStripped.replace("-SNAPSHOT", "");
                }

                String[] latestNum = latest.split("\\.");
                String[] nowVerNum = nowVerStripped.split("\\.");
                for (int i = 0; i < latestNum.length; i++) {
                    if (Integer.parseInt(latestNum[i]) > Integer.parseInt(nowVerNum[i])) {
                        shouldUpdate = 1;
                        break;
                    } else {
                        shouldUpdate = 0;
                    }
                }
            }
        });
    }

    private static boolean isDev(String ver) {
        return ver.endsWith("-SNAPSHOT");
    }
}
