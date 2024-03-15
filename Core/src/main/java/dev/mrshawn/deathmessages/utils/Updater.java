package dev.mrshawn.deathmessages.utils;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.config.Config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Updater {

    public static int shouldUpdate = -1;
    public static String latestVersion;
    public static String nowVersion = DeathMessages.getInstance().getDescription().getVersion();
    private static String updateURL = "https://raw.githubusercontent.com/Winds-Studio/DeathMessages/master/VERSION";

    public static void checkUpdate() {
        DeathMessages.getInstance().foliaLib.getImpl().runAsync(task -> {
            try {
                if (Config.settings.CHECK_UPDATE_DEV()) {
                    // Dreeam TODO - check DEV version
                    latestVersion = null;
                } else {
                    URL url = new URL(updateURL);
                    InputStream inputStream = url.openStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    latestVersion = bufferedReader.readLine();
                }
            } catch (Exception e) {
                updateURL = "https://gitee.com/dreeam/DeathMessages/raw/master/VERSION";
                checkUpdate();
            }

            if (latestVersion != null) {
                shouldUpdate = nowVersion.equals(latestVersion) ? 0 : 1;
            }
        });
    }
}
