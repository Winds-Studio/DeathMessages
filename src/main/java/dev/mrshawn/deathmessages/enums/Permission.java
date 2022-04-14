package dev.mrshawn.deathmessages.enums;

public enum Permission {

    DEATHMESSAGES_COMMAND("deathmessages.command.deathmessages"),
    DEATHMESSAGES_COMMAND_BACKUP("deathmessages.command.deathmessages.backup"),
    DEATHMESSAGES_COMMAND_RESTORE("deathmessages.command.deathmessages.restore"),
    DEATHMESSAGES_COMMAND_DISCORDLOG("deathmessages.command.deathmessages.discordlog"),
    DEATHMESSAGES_COMMAND_EDIT("deathmessages.command.deathmessages.edit"),
    DEATHMESSAGES_COMMAND_BLACKLIST("deathmessages.command.deathmessages.blacklist"),
    DEATHMESSAGES_COMMAND_TOGGLE("deathmessages.command.deathmessages.toggle"),
    DEATHMESSAGES_COMMAND_RELOAD("deathmessages.command.deathmessages.reload"),
    DEATHMESSAGES_COMMAND_VERSION("deathmessages.command.deathmessages.version");

    private final String value;

    Permission(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
