package dev.mrshawn.deathmessages.enums;

public enum Permission {

	DEATHMESSAGES_COMMAND_BACKUP("deathmessages.command.backup"),
	DEATHMESSAGES_COMMAND_BLACKLIST("deathmessages.command.blacklist"),
	DEATHMESSAGES_COMMAND_DISCORDLOG("deathmessages.command.discordlog"),
	DEATHMESSAGES_COMMAND_EDIT("deathmessages.command.edit"),
	DEATHMESSAGES_COMMAND_RELOAD("deathmessages.command.reload"),
	DEATHMESSAGES_COMMAND_RESTORE("deathmessages.command.restore"),
	DEATHMESSAGES_COMMAND_TOGGLE("deathmessages.command.toggle"),
	DEATHMESSAGES_COMMAND_VERSION("deathmessages.command.version");

	private final String value;

	Permission(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
