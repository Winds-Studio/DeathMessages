package dev.mrshawn.deathmessages.enums;

public enum DeathModes {

	BASIC_MODE("Basic-Mode"),
	MOBS("Mobs");

	private final String value;

	DeathModes(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
