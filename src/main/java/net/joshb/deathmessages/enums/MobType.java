package net.joshb.deathmessages.enums;

public enum MobType {

    VANILLA("Entities"),
    MYTHIC_MOB("Mythic-Mobs-Entities");


    private final String value;

    MobType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}