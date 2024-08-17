package dev.mrshawn.deathmessages.utils;

import dev.mrshawn.deathmessages.api.EntityManager;
import dev.mrshawn.deathmessages.api.ExplosionManager;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.api.events.DMBlockExplodeEvent;
import dev.mrshawn.deathmessages.config.Messages;
import dev.mrshawn.deathmessages.enums.MobType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    public static TextReplacementConfig prefix = TextReplacementConfig.builder()
            .matchLiteral("%prefix%")
            .replacement(convertFromLegacy(Messages.getInstance().getConfig().getString("Prefix")))
            .build();

    public static TextReplacementConfig replace(String matchLiteral, String replace) {
        return TextReplacementConfig.builder()
                .matchLiteral(matchLiteral)
                .replacement((replace != null) ? replace : matchLiteral) // Prevent null replacement
                .build();
    }

    public static Component formatMessage(String path) {
        return convertFromLegacy(Messages.getInstance().getConfig().getString(path)).replaceText(prefix);
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
        if (isNewerAndEqual(16, 0)) {
            // Match bungee RGB color code only, use Negative Lookbehind to avoid matching code begin with &
            Pattern pattern = Pattern.compile("(?<!&)(#[0-9a-fA-F]{6})");
            Matcher matcher = pattern.matcher(s);
            StringBuffer result = new StringBuffer();

            while (matcher.find()) {
                String colorCode = matcher.group(1);

                String replacement = "&" + colorCode;
                matcher.appendReplacement(result, replacement);
            }

            // Append rest of string
            matcher.appendTail(result);

            s = result.toString();
        }

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
        List<Entity> getNearby = new ArrayList<>(isNewerAndEqual(13, 0)
                ? b.getWorld().getNearbyEntities(BoundingBox.of(b).expand(24)) // TODO: make it configurable
                : b.getWorld().getNearbyEntities(b.getLocation(), 24, 24, 24));

        getNearby.forEach(ent -> {
                    if (ent instanceof Player) {
                        Optional<PlayerManager> getPlayer = PlayerManager.getPlayer((Player) ent);
                        getPlayer.ifPresent(effect -> {
                            effected.add(ent.getUniqueId());
                            effect.setLastEntityDamager(p);
                        });
                    } else {
                        Optional<EntityManager> getEntity = EntityManager.getEntity(ent.getUniqueId());
                        Optional<PlayerManager> getPlayer = PlayerManager.getPlayer(p);
                        getEntity.ifPresent(em -> {
                            effected.add(ent.getUniqueId());
                            getPlayer.ifPresent(em::setLastPlayerDamager);
                        });
                        if (!getEntity.isPresent()) {
                            new EntityManager(ent, ent.getUniqueId(), MobType.VANILLA);
                        }
                    }
                }
        );

        new ExplosionManager(p.getUniqueId(), b.getType(), b.getLocation(), effected);
        DMBlockExplodeEvent explodeEvent = new DMBlockExplodeEvent(p, b);
        Bukkit.getPluginManager().callEvent(explodeEvent);
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

    /*
        Sakamoto Util
     */

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
