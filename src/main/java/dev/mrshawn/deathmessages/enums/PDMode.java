package dev.mrshawn.deathmessages.enums;

public enum PDMode {
    BASIC_MODE("Basic-Mode"),
    MOBS("Mobs");

    private final String value;

    PDMode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
