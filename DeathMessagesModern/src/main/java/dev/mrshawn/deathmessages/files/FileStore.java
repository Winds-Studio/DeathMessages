package dev.mrshawn.deathmessages.files;

public class FileStore {

    public static final FileSettings<Config> CONFIG = new FileSettings<Config>("Settings.yml").loadSettings(Config.class);
}
