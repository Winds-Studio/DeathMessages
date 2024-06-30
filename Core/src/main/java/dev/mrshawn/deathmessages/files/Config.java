package dev.mrshawn.deathmessages.files;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public enum Config {

	CHECK_UPDATE("Check-Update.Enabled", true),
	CHECK_DEV_VERSION("Check-Update.DEV-Version", false),

	DISABLE_DEFAULT_MESSAGES("Disable-Default-Messages", true),
	ADD_PREFIX_TO_ALL_MESSAGES("Add-Prefix-To-All-Messages", true),

	HOOKS_MYTHICMOBS_ENABLED("Hooks.MythicMobs.Enabled", true),
	HOOKS_WORLDGUARD_ENABLED("Hooks.WorldGuard.Enabled", true),
	HOOKS_COMBATLOGX_ENABLED("Hooks.CombatLogX.Enabled", true),

	HOOKS_DISCORD_ENABLED("Hooks.Discord.Enabled", true),
	HOOKS_DISCORD_CHANNELS_PLAYER_ENABLED("Hooks.Discord.Channels.Player.Enabled", true),
	HOOKS_DISCORD_CHANNELS_PLAYER_CHANNELS("Hooks.Discord.Channels.Player.Channels", Arrays.asList("218258614192450048:827286062147837621")),
	HOOKS_DISCORD_CHANNELS_MOB_ENABLED("Hooks.Discord.Channels.Mob.Enabled", true),
	HOOKS_DISCORD_CHANNELS_MOB_CHANNELS("Hooks.Discord.Channels.Mob.Channels", new ArrayList<String>()),
	HOOKS_DISCORD_CHANNELS_NATURAL_ENABLED("Hooks.Discord.Channels.Natural.Enabled", true),
	HOOKS_DISCORD_CHANNELS_NATURAL_CHANNELS("Hooks.Discord.Channels.Natural.Channels", new ArrayList<String>()),
	HOOKS_DISCORD_CHANNELS_ENTITY_ENABLED("Hooks.Discord.Channels.Entity.Enabled", true),
	HOOKS_DISCORD_CHANNELS_ENTITY_CHANNELS("Hooks.Discord.Channels.Entity.Channels", new ArrayList<String>()),
	HOOKS_DISCORD_WORLD_WHITELIST_ENABLED("Hooks.Discord.World-Whitelist.Enabled", false),
	HOOKS_DISCORD_WORLD_WHITELIST_WORLDS("Hooks.Discord.World-Whitelist.Worlds", Arrays.asList("test1", "test2")),

	HOOKS_BUNGEE_ENABLED("Hooks.Bungee.Enabled", false),
	HOOKS_BUNGEE_SERVER_NAME_GET_FROM_BUNGEE("Hooks.Bungee.Server-Name.Get-From-Bungee", false),
	HOOKS_BUNGEE_SERVER_NAME_DISPLAY_NAME("Hooks.Bungee.Server-Name.Display-Name", "lobby"),
	HOOKS_BUNGEE_SERVER_GROUPS_ENABLED("Hooks.Bungee.Server-Groups.Enabled", false),
	HOOKS_BUNGEE_SERVER_GROUPS_SERVERS("Hooks.Bungee.Server-Groups.Servers", Arrays.asList("lobby", "survival")),

	SAVED_USER_DATA("Saved-User-Data", true),

	DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_ENABLED("Disable-Weapon-Kill-With-No-Custom-Name.Enabled", false),
	DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_IGNORE_ENCHANTMENTS
			("Disable-Weapon-Kill-With-No-Custom-Name.Ignore-Enchantments", true),
	DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_SOURCE_PROJECTILE_DEFAULT_TO
			("Disable-Weapon-Kill-With-No-Custom-Name.Source.Projectile.Default-To", "Projectile-Unknown"),
	DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_SOURCE_WEAPON_DEFAULT_TO
			("Disable-Weapon-Kill-With-No-Custom-Name.Source.Weapon.Default-To", "Melee"),

	DEFAULT_MELEE_LAST_DAMAGE_NOT_DEFINED("Default-Melee-Last-Damage-Not-Defined", true),

	DEFAULT_NATURAL_DEATH_NOT_DEFINED("Default-Natural-Death-Not-Defined", true),

	DEATH_LISTENER_PRIORITY("Death-Listener-Priority", "HIGH"),

	RENAME_MOBS_ENABLED("Rename-Mobs.Enabled", true),
	RENAME_MOBS_IF_CONTAINS("Rename-Mobs.If-Contains", "♡♥❤■"),

	DISABLE_NAMED_MOBS("Disable-Named-Mobs", false),

	FIX_MC_84595("Fix-MC-84595", false),

	DISPLAY_I18N_ITEM_NAME("I18N-Display.Item-Name", true),
	DISPLAY_I18N_MOB_NAME("I18N-Display.Mob-Name", true),

	EXPIRE_LAST_DAMAGE_EXPIRE_PLAYER("Expire-Last-Damage.Expire-Player", 7),
	EXPIRE_LAST_DAMAGE_EXPIRE_ENTITY("Expire-Last-Damage.Expire-Entity", 7),

	PER_WORLD_MESSAGES("Per-World-Messages", false),

	WORLD_GROUPS
			("World-Groups", new ConcurrentHashMap<Object, Object>() {{
				put("1", Arrays.asList("world", "world_nether", "world_the_end"));
			}}),

	DISABLED_WORLDS("Disabled-Worlds", Arrays.asList("someDisabledWorld", "someOtherDisabledWorld")),

	PRIVATE_MESSAGES_PLAYER("Private-Messages.Player", false),
	PRIVATE_MESSAGES_MOBS("Private-Messages.Mobs", false),
	PRIVATE_MESSAGES_NATURAL("Private-Messages.Natural", false),
	PRIVATE_MESSAGES_TAMEABLE("Private-Messages.Tameable", false),

	COOLDOWN("Cooldown", 0),

	CUSTOM_ITEM_DISPLAY_NAMES_IS_WEAPON
			("Custom-Item-Display-Names-Is-Weapon", Arrays.asList("&6SUPER COOL GOLDEN APPLE", "SICKNAME")),

	CUSTOM_ITEM_MATERIAL_IS_WEAPON("Custom-Item-Material-Is-Weapon", Arrays.asList("ACACIA_FENCE")),
	CUSTOM_SUICIDE_COMMANDS("Custom-Suicide-Commands", Arrays.asList("/kill", "/suicide")),

	DEBUG("Debug", false);

	private final String path;
	private final Object defaultValue;

	Config(String path, Object defaultValue) {
		this.path = path;
		this.defaultValue = defaultValue;
	}

	public String getPath() {
		return this.path;
	}

	public Object getDefault() {
		return this.defaultValue;
	}

}
