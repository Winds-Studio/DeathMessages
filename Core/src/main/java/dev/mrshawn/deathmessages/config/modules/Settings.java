package dev.mrshawn.deathmessages.config.modules;

import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfHeader;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ConfHeader({
        "DeathMessages by Dreeam__ (Original by MrShawn/Joshb_/CosmoConsole[DeathMessagesPrime Author])",
        ""
})
public interface Settings {

    @ConfComments({
            "Check plugin version update"
    })
    @ConfKey("Check-Update.Enabled")
    @ConfDefault.DefaultBoolean(true)
    @AnnotationBasedSorter.Order(1)
    boolean CHECK_UPDATE();

    @ConfKey("Check-Update.DEV-Version")
    @ConfDefault.DefaultBoolean(false)
    @AnnotationBasedSorter.Order(10)
    boolean CHECK_UPDATE_DEV();

    @ConfComments({
            "Disable the default messages from minecraft"
    })
    @ConfKey("Disable-Default-Messages")
    @ConfDefault.DefaultBoolean(true)
    @AnnotationBasedSorter.Order(20)
    boolean DISABLE_DEFAULT_MESSAGES();

    @ConfComments({
            "Add a prefix to all death messages? (Prefix from Messages.yml)"
    })
    @ConfKey("Add-Prefix-To-All-Messages")
    @ConfDefault.DefaultBoolean(true)
    @AnnotationBasedSorter.Order(30)
    boolean ADD_PREFIX_TO_ALL_MESSAGES();

    // Settings for specific hooks the plugins interact with

    @ConfComments({
            "If MythicMobs is found, enable the hook",
            "Enable/Disable MythicMobs hook"
    })
    @ConfKey("Hooks.MythicMobs.Enabled")
    @ConfDefault.DefaultBoolean(true)
    @AnnotationBasedSorter.Order(40)
    boolean HOOKS_MYTHICMOBS_ENABLED();

    @ConfComments({
            "If WorldGuard is found, enable the hook",
            "This creates custom flags for regions. These flags are set to ALLOW by default",
            "Flags:",
            "  broadcast-deathmessage-player",
            "  broadcast-deathmessage-mobs",
            "  broadcast-deathmessage-natural",
            "  broadcast-deathmessage-tameable",
            "Use: /rg flag <regionName> <customFlag> allow/deny",
            "The above flags determine death message from being broadcasted if the pvp/pve is taking place in the region.",
    })
    @ConfKey("Hooks.WorldGuard.Enabled")
    @ConfDefault.DefaultBoolean(true)
    @AnnotationBasedSorter.Order(50)
    boolean HOOKS_WORLDGUARD_ENABLED();

    @ConfComments({
            "If CombatLogX is found, enable the hook",
            "Enable/Disable CombatLogX hook"
    })
    @ConfKey("Hooks.CombatLogX.Enabled")
    @ConfDefault.DefaultBoolean(true)
    @AnnotationBasedSorter.Order(60)
    boolean HOOKS_COMBATLOGX_ENABLED();

    @ConfComments({
            "If DiscordSRV (https://www.spigotmc.org/resources/discordsrv.18494/) is installed, and enabled here.",
            "To get the Guild ID and Channel-IDs, you must have developer mode enabled in discord. Then, you will right-click the",
            "guild and click on \"Copy ID\". Then, you will right-click the channel you want to send the messages to and again, click",
            "\"Copy ID\". The format for the list of Channels is: \"guild_ID:channel_ID\"",
            "Example: \"218258614192450048:827286062147837621\"",
            "THE ABOVE EXAMPLE SHOULD NOT BE USED IN LIVE APPLICATIONS! DO NOT ATTEMPT TO USE IT."
    })
    @ConfKey("Hooks.Discord.Enabled")
    @ConfDefault.DefaultBoolean(true)
    @AnnotationBasedSorter.Order(70)
    boolean HOOKS_DISCORD_ENABLED();

    @ConfComments({
            "Specify which channel certain death messages get sent."
    })
    @ConfKey("Hooks.Discord.Channels.Player.Enabled")
    @ConfDefault.DefaultBoolean(true)
    @AnnotationBasedSorter.Order(80)
    boolean HOOKS_DISCORD_CHANNELS_PLAYER_ENABLED();

    @ConfKey("Hooks.Discord.Channels.Player.Channels")
    @ConfDefault.DefaultStrings({
            "218258614192450048:827286062147837621"
    })
    @AnnotationBasedSorter.Order(90)
    List<String> HOOKS_DISCORD_CHANNELS_PLAYER_CHANNELS();

    @ConfKey("Hooks.Discord.Channels.Mob.Enabled")
    @ConfDefault.DefaultBoolean(true)
    @AnnotationBasedSorter.Order(100)
    boolean HOOKS_DISCORD_CHANNELS_MOB_ENABLED();

    @ConfKey("Hooks.Discord.Channels.Mob.Channels")
    @ConfDefault.DefaultStrings({})
    @AnnotationBasedSorter.Order(110)
    List<String> HOOKS_DISCORD_CHANNELS_MOB_CHANNELS();

    @ConfKey("Hooks.Discord.Channels.Natural.Enabled")
    @ConfDefault.DefaultBoolean(true)
    @AnnotationBasedSorter.Order(120)
    boolean HOOKS_DISCORD_CHANNELS_NATURAL_ENABLED();

    @ConfKey("Hooks.Discord.Channels.Natural.Channels")
    @ConfDefault.DefaultStrings({})
    @AnnotationBasedSorter.Order(130)
    List<String> HOOKS_DISCORD_CHANNELS_NATURAL_CHANNELS();

    @ConfKey("Hooks.Discord.Channels.Entity.Enabled")
    @ConfDefault.DefaultBoolean(true)
    @AnnotationBasedSorter.Order(140)
    boolean HOOKS_DISCORD_CHANNELS_ENTITY_ENABLED();

    @ConfKey("Hooks.Discord.Channels.Entity.Channels")
    @ConfDefault.DefaultStrings({})
    @AnnotationBasedSorter.Order(150)
    List<String> HOOKS_DISCORD_CHANNELS_ENTITY_CHANNELS();

    @ConfComments({
            "If you wish to only have certain worlds with death message broadcast to discord, define them here"
    })
    @ConfKey("Hooks.Discord.World-Whitelist.Enabled")
    @ConfDefault.DefaultBoolean(false)
    @AnnotationBasedSorter.Order(160)
    boolean HOOKS_DISCORD_WORLD_WHITELIST_ENABLED();

    @ConfKey("Hooks.Discord.World-Whitelist.Worlds")
    @ConfDefault.DefaultStrings({
            "test1",
            "test2"
    })
    @AnnotationBasedSorter.Order(170)
    List<String> HOOKS_DISCORD_WORLD_WHITELIST_WORLDS();

    @ConfComments({
            "If you run a bungee server that you would like to send death messages to other servers, enable this"
    })
    @ConfKey("Hooks.Bungee.Enabled")
    @ConfDefault.DefaultBoolean(false)
    @AnnotationBasedSorter.Order(180)
    boolean HOOKS_BUNGEE_ENABLED();

    @ConfKey("Hooks.Bungee.Server-Name.Get-From-Bungee")
    @ConfDefault.DefaultBoolean(false)
    @AnnotationBasedSorter.Order(190)
    boolean HOOKS_BUNGEE_SERVER_NAME_GET_FROM_BUNGEE();

    @ConfKey("Hooks.Bungee.Server-Name.Display-Name")
    @ConfDefault.DefaultString("lobby")
    @AnnotationBasedSorter.Order(200)
    String HOOKS_BUNGEE_SERVER_NAME_DISPLAY_NAME();

    @ConfKey("Hooks.Bungee.Server-Groups.Enabled")
    @ConfDefault.DefaultBoolean(false)
    @AnnotationBasedSorter.Order(210)
    boolean HOOKS_BUNGEE_SERVER_GROUPS_ENABLED();

    @ConfKey("Hooks.Bungee.Server-Groups.Servers")
    @ConfDefault.DefaultStrings({
            "lobby",
            "survival"
    })
    @AnnotationBasedSorter.Order(220)
    List<String> HOOKS_BUNGEE_SERVER_GROUPS_SERVERS();

    @ConfKey("Saved-User-Data")
    @ConfDefault.DefaultBoolean(true)
    @AnnotationBasedSorter.Order(230)
    boolean SAVED_USER_DATA();

    @ConfKey("Disable-Weapon-Kill-With-No-Custom-Name.Enabled")
    @ConfDefault.DefaultBoolean(false)
    @AnnotationBasedSorter.Order(240)
    boolean DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_ENABLED();

    @ConfKey("Disable-Weapon-Kill-With-No-Custom-Name.Allow-Message-Color-Override")
    @ConfDefault.DefaultBoolean(true)
    @AnnotationBasedSorter.Order(250)
    boolean DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_ALLOW_MESSAGE_COLOR_OVERRIDE();

    @ConfKey("Disable-Weapon-Kill-With-No-Custom-Name.Ignore-Enchantments")
    @ConfDefault.DefaultBoolean(true)
    @AnnotationBasedSorter.Order(260)
    boolean DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_IGNORE_ENCHANTMENTS();

    @ConfKey("Disable-Weapon-Kill-With-No-Custom-Name.Source.Projectile.Default-To")
    @ConfDefault.DefaultString("Projectile-Unknown")
    @AnnotationBasedSorter.Order(270)
    String DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_SOURCE_PROJECTILE_DEFAULT_TO();

    @ConfKey("Disable-Weapon-Kill-With-No-Custom-Name.Source.Weapon.Default-To")
    @ConfDefault.DefaultString("Melee")
    @AnnotationBasedSorter.Order(280)
    String DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_SOURCE_WEAPON_DEFAULT_TO();

    @ConfKey("Default-Melee-Last-Damage-Not-Defined")
    @ConfDefault.DefaultBoolean(true)
    @AnnotationBasedSorter.Order(290)
    boolean DEFAULT_MELEE_LAST_DAMAGE_NOT_DEFINED();

    @ConfKey("Default-Natural-Death-Not-Defined")
    @ConfDefault.DefaultBoolean(true)
    @AnnotationBasedSorter.Order(300)
    boolean DEFAULT_NATURAL_DEATH_NOT_DEFINED();

    @ConfKey("Death-Listener-Priority")
    @ConfDefault.DefaultString("HIGH")
    @AnnotationBasedSorter.Order(310)
    String DEATH_LISTENER_PRIORITY();

    @ConfKey("Rename-Mobs.Enabled")
    @ConfDefault.DefaultBoolean(true)
    @AnnotationBasedSorter.Order(320)
    boolean RENAME_MOBS_ENABLED();

    @ConfKey("Rename-Mobs.If-Contains")
    @ConfDefault.DefaultString("♡♥❤■")
    @AnnotationBasedSorter.Order(330)
    String RENAME_MOBS_IF_CONTAINS();

    @ConfKey("Disable-Named-Mobs")
    @ConfDefault.DefaultBoolean(false)
    @AnnotationBasedSorter.Order(340)
    boolean DISABLE_NAMED_MOBS();

    @ConfKey("I18N-Display.Item-Name")
    @ConfDefault.DefaultBoolean(true)
    @AnnotationBasedSorter.Order(350)
    boolean DISPLAY_I18N_ITEM_NAME();

    @ConfKey("I18N-Display.Mob-Name")
    @ConfDefault.DefaultBoolean(true)
    @AnnotationBasedSorter.Order(360)
    boolean DISPLAY_I18N_MOB_NAME();

    @ConfKey("Expire-Last-Damage.Expire-Player")
    @ConfDefault.DefaultInteger(7)
    @AnnotationBasedSorter.Order(370)
    int EXPIRE_LAST_DAMAGE_EXPIRE_PLAYER();

    @ConfKey("Expire-Last-Damage.Expire-Entity")
    @ConfDefault.DefaultInteger(7)
    @AnnotationBasedSorter.Order(380)
    int EXPIRE_LAST_DAMAGE_EXPIRE_ENTITY();

    @ConfKey("Per-World-Messages")
    @ConfDefault.DefaultBoolean(false)
    @AnnotationBasedSorter.Order(390)
    boolean PER_WORLD_MESSAGES();

    static Map<String, Level> defaultWorldGroups() {
        Map<String, Level> groups = new LinkedHashMap<>();

        groups.put("1", Level.of(Arrays.asList(
                "world",
                "world_nether",
                "world_the_end"
        )));

        return groups;
    }

    @ConfKey("World-Groups")
    @ConfDefault.DefaultObject("defaultWorldGroups")
    @AnnotationBasedSorter.Order(400)
    Map<String, @SubSection Level> WORLD_GROUPS();

    interface Level {

        List<String> worlds();

        static Level of(List<String> worlds) {
            return () -> worlds;
        }
    }

    @ConfKey("Disabled-Worlds")
    @ConfDefault.DefaultStrings({
            "someDisabledWorld",
            "someOtherDisabledWorld"
    })
    @AnnotationBasedSorter.Order(410)
    List<String> DISABLED_WORLDS();

    @ConfKey("Private-Messages.Player")
    @ConfDefault.DefaultBoolean(false)
    @AnnotationBasedSorter.Order(420)
    boolean PRIVATE_MESSAGES_PLAYER();

    @ConfKey("Private-Messages.Mobs")
    @ConfDefault.DefaultBoolean(false)
    @AnnotationBasedSorter.Order(430)
    boolean PRIVATE_MESSAGES_MOBS();

    @ConfKey("Private-Messages.Natural")
    @ConfDefault.DefaultBoolean(false)
    @AnnotationBasedSorter.Order(440)
    boolean PRIVATE_MESSAGES_NATURAL();

    @ConfKey("Private-Messages.Tameable")
    @ConfDefault.DefaultBoolean(false)
    @AnnotationBasedSorter.Order(450)
    boolean PRIVATE_MESSAGES_TAMEABLE();

    @ConfKey("Cooldown")
    @ConfDefault.DefaultInteger(0)
    @AnnotationBasedSorter.Order(460)
    int COOLDOWN();

    @ConfKey("Custom-Item-Display-Names-Is-Weapon")
    @ConfDefault.DefaultStrings({
            "&6SUPER COOL GOLDEN APPLE",
            "SICKNAME"
    })
    @AnnotationBasedSorter.Order(470)
    List<String> CUSTOM_ITEM_DISPLAY_NAMES_IS_WEAPON();

    @ConfKey("Custom-Item-Material-Is-Weapon")
    @ConfDefault.DefaultStrings({
            "ACACIA_FENCE"
    })
    @AnnotationBasedSorter.Order(480)
    List<String> CUSTOM_ITEM_MATERIAL_IS_WEAPON();

    @ConfKey("Debug")
    @ConfDefault.DefaultBoolean(false)
    @AnnotationBasedSorter.Order(490)
    boolean DEBUG();
}
