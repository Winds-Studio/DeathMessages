package net.joshb.deathmessages.enums;

public enum MessageType {

    PLAYER("player"),
    MOB("mob"),
    NATURAL("natural"),
    ENTITY("entity");

    private final String value;

    MessageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
