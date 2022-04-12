package net.joshb.deathmessages.enums;

public enum DeathAffiliation {

    SOLO("Solo"),
    GANG("Gang");


    private final String value;

    DeathAffiliation(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}