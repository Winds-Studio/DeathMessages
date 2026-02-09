package dev.mrshawn.deathmessages.utils;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.EntityCtx;
import dev.mrshawn.deathmessages.api.ExplosionManager;
import dev.mrshawn.deathmessages.api.PlayerCtx;
import dev.mrshawn.deathmessages.api.events.DMBlockExplodeEvent;
import dev.mrshawn.deathmessages.config.Messages;
import dev.mrshawn.deathmessages.config.Settings;
import dev.mrshawn.deathmessages.enums.MobType;
import dev.mrshawn.deathmessages.config.files.Config;
import dev.mrshawn.deathmessages.config.files.FileStore;
import dev.mrshawn.deathmessages.hooks.CommonVanishPluginExtension;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    private static final Pattern BUNGEE_RGB_PATTERN = Pattern.compile("(?<!&)(#[0-9a-fA-F]{6})"); // Match bungee RGB color code only, use Negative Lookbehind to avoid matching code begin with &
    public static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + "§" + "[0-9A-FK-ORX]");
    public static final Pattern DM_PERM_PATTERN = Pattern.compile("PERMISSION\\[(.*?)]");
    public static final Pattern DM_KILLER_PERM_PATTERN = Pattern.compile("PERMISSION_KILLER\\[(.*?)]");
    public static final Pattern DM_REGION_PATTERN = Pattern.compile("REGION\\[(.*?)]");
    public static final Pattern DM_HOVER_EVENT_PATTERN = Pattern.compile("\\[(.*?)]"); // Match all string between [ and ], e.g. aaa[123]bbb -> [123]
    public static final Pattern PAPI_PLACEHOLDER_PATTERN = Pattern.compile("%([^%]+)%");

    public static final ConsoleCommandSender CONSOLE = Bukkit.getServer().getConsoleSender();
    public static final TextReplacementConfig PREFIX = TextReplacementConfig.builder()
            .matchLiteral("%prefix%")
            .replacement(convertFromLegacy(Messages.getInstance().getConfig().getString("Prefix")))
            .build();

    public static Pattern[] customWeaponNamePatterns;

    public static final Map<UUID, LivingEntity> crystalDeathData = new HashMap<>(); // <EndCrystal UUID, Causing Entity instance>

    public static TextReplacementConfig replace(String matchLiteral, String replace) {
        return TextReplacementConfig.builder()
                .matchLiteral(matchLiteral)
                .replacement((replace != null) ? replace : matchLiteral) // Prevent null replacement
                .build();
    }

    public static TextReplacementConfig replace(String matchLiteral, Component replace) {
        return TextReplacementConfig.builder()
                .matchLiteral(matchLiteral)
                .replacement((replace != null) ? replace : Component.text(matchLiteral)) // Prevent null replacement
                .build();
    }

    public static Component formatMessage(String path) {
        return convertFromLegacy(Messages.getInstance().getConfig().getString(path)).replaceText(PREFIX);
    }

    public static TextComponent convertFromLegacy(String s) {
        s = colorizeBungeeRGB(s);

        return LegacyComponentSerializer.legacyAmpersand().deserialize(s);
    }

    public static String convertToLegacy(Component component) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(component);
    }

    /*
        Match and add & in front of each bungee RGB code for adventure to serialize, for backward compatibility
        In adventure, RGB color code format is like &#a25981
        In bungee api, RGB color code format is like #a25981
        To support both bungee and adventure RGB color code in legacy
    */
    private static String colorizeBungeeRGB(String s) {
        Matcher matcher = BUNGEE_RGB_PATTERN.matcher(s);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String colorCode = matcher.group(1);

            String replacement = "&" + colorCode;
            matcher.appendReplacement(result, replacement);
        }

        // Append rest of string
        matcher.appendTail(result);

        s = result.toString();

        return s;
    }

    public static boolean isNumeric(String s) {
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }

        return false;
    }

    public static void getExplosionNearbyEffected(Player p, Block b) {
        List<UUID> effected = new ArrayList<>();
        Collection<Entity> getNearby = b.getWorld().getNearbyEntities(BoundingBox.of(b).expand(24)); // TODO: make it configurable

        getNearby.forEach(ent -> {
                    if (ent instanceof Player) {
                        PlayerCtx playerCtx = PlayerCtx.of(ent.getUniqueId());
                        if (playerCtx != null) {
                            effected.add(ent.getUniqueId());
                            playerCtx.setLastEntityDamager(p);
                        }
                    } else {
                        EntityCtx entityCtx = EntityCtx.of(ent.getUniqueId());
                        if (entityCtx != null) {
                            effected.add(ent.getUniqueId());

                            PlayerCtx playerCtx = PlayerCtx.of(p.getUniqueId());
                            if (playerCtx != null) {
                                entityCtx.setLastPlayerDamager(playerCtx);
                            }
                        } else {
                            EntityCtx.create(new EntityCtx(ent, MobType.VANILLA));
                        }
                    }
                }
        );

        new ExplosionManager(p.getUniqueId(), b.getType(), b.getLocation(), effected);
        DMBlockExplodeEvent explodeEvent = new DMBlockExplodeEvent(p, b);
        Bukkit.getPluginManager().callEvent(explodeEvent);
    }

    public static void loadCrystalDamager(Entity entity, Entity damager) {
        // Scenario 1
        // Player clicked (damaged) crystal
        // I didn't consider the scenario about entity clicked crystal, idk if it's needed?
        if (entity instanceof EnderCrystal && damager instanceof Player) {
            crystalDeathData.put(entity.getUniqueId(), (Player) damager);
        }
        // Scenario 2
        // The crystal is triggered by a projectile when pass through (player A / LivingEntity -> projectile -> crystal -> Player A/B / Entity)
        else if (entity instanceof EnderCrystal && damager instanceof Projectile) {
            ProjectileSource shooter = ((Projectile) damager).getShooter();
            if (shooter instanceof LivingEntity) {
                crystalDeathData.put(entity.getUniqueId(), (LivingEntity) shooter);
            }
        }
    }

    public static List<World> getBroadcastWorlds(Entity e) {
        List<World> broadcastWorlds = new ArrayList<>();

        if (FileStore.CONFIG.getStringList(Config.DISABLED_WORLDS).contains(e.getWorld().getName())) {
            return broadcastWorlds;
        }

        if (FileStore.CONFIG.getBoolean(Config.PER_WORLD_MESSAGES)) {
            // TODO: Add support for Map in FileSettings
            for (String groups : Settings.getInstance().getConfig().getConfigurationSection("World-Groups").getKeys(false)) {
                List<String> worlds = Settings.getInstance().getConfig().getStringList("World-Groups." + groups);
                if (worlds.contains(e.getWorld().getName())) {
                    for (String single : worlds) {
                        World world = Bukkit.getWorld(single);
                        if (world != null) {
                            broadcastWorlds.add(world);
                        } else {
                            DeathMessages.LOGGER.warn("Can't find world with name: {}, in World-Groups", single);
                        }
                    }
                }
            }
            if (broadcastWorlds.isEmpty()) {
                broadcastWorlds.add(e.getWorld());
            }
        } else {
            return Bukkit.getWorlds();
        }

        return broadcastWorlds;
    }

    // The simpler and better version of common-lang's RandomStringUtils#randomNumeric
    public static String randomNumeric(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be greater than zero.");
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            // Bound range 0~9
            int digit = ThreadLocalRandom.current().nextInt(10);
            sb.append(digit);
        }

        return sb.toString();
    }

    public static String getPlayerName(PlayerCtx playerCtx) {
        if (isPlayerVanished(playerCtx.getPlayer())) {
            return PlainTextComponentSerializer.plainText().serialize(getVanishedName());
        }

        return playerCtx.getName();
    }

    public static String getPlayerName(Player player) {
        if (isPlayerVanished(player)) {
            return PlainTextComponentSerializer.plainText().serialize(getVanishedName());
        }

        return player.getName();
    }

    public static String getPlayerDisplayName(PlayerCtx playerCtx) {
        final Player player = playerCtx.getPlayer();

        if (isPlayerVanished(player)) {
            return PlainTextComponentSerializer.plainText().serialize(getVanishedName());
        }

        return player.getDisplayName();
    }

    public static String getPlayerDisplayName(Player player) {
        if (isPlayerVanished(player)) {
            return PlainTextComponentSerializer.plainText().serialize(getVanishedName());
        }

        return player.getDisplayName();
    }

    public static Component getPlayerDisplayNameComponent(PlayerCtx playerCtx) {
        final Player player = playerCtx.getPlayer();

        if (isPlayerVanished(player)) {
            return getVanishedName();
        }

        return player.displayName();
    }

    public static Component getPlayerDisplayNameComponent(Player player) {
        if (isPlayerVanished(player)) {
            return getVanishedName();
        }

        return player.displayName();
    }

    public static boolean isPlayerVanished(Player player) {
        if (FileStore.CONFIG.getBoolean(Config.HOOKS_VANISH_VANILLA_ENABLED) && player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            return true;
        }

        return CommonVanishPluginExtension.isPluginVanished(player);
    }

    public static Component getVanishedName() {
        final String name = Settings.getInstance().getConfig().getString(Config.HOOKS_VANISH_VANISHED_NAME.getPath());

        return Util.convertFromLegacy(name != null ? name : "");
    }

    /*
        Sakamoto Util
     */
    // Note:
    // In Modern module, any condition relates to <= 1.20.4 are removed
    // In legacy module, any conditions relates to > 1.20.4 are removed
    // Version support range can be found in README.md

    // Server version, e.g. 1.20.2-R0.1-SNAPSHOT -> {"1","20","2"}
    private final static String[] serverVersion = Bukkit.getServer().getBukkitVersion()
            .substring(0, Bukkit.getServer().getBukkitVersion().indexOf("-"))
            .split("\\.");

    private final static int mcFirstVersion = Integer.parseInt(serverVersion[0]);
    private final static int majorVersion = Integer.parseInt(serverVersion[1]);
    private final static int minorVersion = serverVersion.length == 3 ? Integer.parseInt(serverVersion[2]) : 0;

    // > (major, minor)
    public static boolean isNewerThan(int major, int minor) {
        if (majorVersion > major) {
            return true;
        }

        return majorVersion == major && minorVersion > minor;
    }

    // == (major, minor)
    public static boolean isEqualTo(int major, int minor) {
        return majorVersion == major && minorVersion == minor;
    }

    // < (major, minor)
    public static boolean isOlderThan(int major, int minor) {
        if (majorVersion < major) {
            return true;
        }

        return majorVersion == major && minorVersion < minor;
    }

    // >= (major, minor)
    public static boolean isNewerAndEqual(int major, int minor) {
        if (majorVersion > major) {
            return true;
        }

        return majorVersion == major && minorVersion >= minor;
    }

    // <= (major, minor)
    public static boolean isOlderAndEqual(int major, int minor) {
        if (majorVersion < major) {
            return true;
        }

        return majorVersion == major && minorVersion <= minor;
    }
}
