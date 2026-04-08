package dev.mrshawn.deathmessages.utils;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.PlayerCtx;
import dev.mrshawn.deathmessages.config.Messages;
import dev.mrshawn.deathmessages.config.Settings;
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
import org.bukkit.block.Biome;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

public class Util {

    private static final Pattern BUNGEE_RGB_PATTERN = Pattern.compile("(?<!&)(#[0-9a-fA-F]{6})"); // Match bungee RGB color code only, use Negative Lookbehind to avoid matching code begin with &
    public static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + "§" + "[0-9A-FK-ORX]");
    public static final Pattern DM_PERM_PATTERN = Pattern.compile("PERMISSION\\[(.*?)]");
    public static final Pattern DM_KILLER_PERM_PATTERN = Pattern.compile("PERMISSION_KILLER\\[(.*?)]");
    public static final Pattern DM_REGION_PATTERN = Pattern.compile("REGION\\[(.*?)]");
    public static final Pattern DM_HOVER_EVENT_PATTERN = Pattern.compile("\\[(.*?)]"); // Match all string between [ and ], e.g. aaa[123]bbb -> [123]
    public static final Pattern PAPI_PLACEHOLDER_PATTERN = Pattern.compile("%([^%]+)%");

    public static final String HOOKS_PACKAGE_PREFIX_NAME = "dev.mrshawn.deathmessages.hooks.";
    public static final String NMS_PACKAGE_PREFIX_NAME = "dev.mrshawn.deathmessages.nms.";

    public static final TextReplacementConfig PREFIX = TextReplacementConfig.builder()
            .matchLiteral("%prefix%")
            .replacement(convertFromLegacy(Messages.getInstance().getConfig().getString("Prefix")))
            .build();

    public static Pattern[] customWeaponNamePatterns;

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
        if (PlatformUtil.isNewerAndEqual(16, 0)) {
            s = bungeeHexToAdventureInConfig(s);
        }

        return LegacyComponentSerializer.legacyAmpersand().deserialize(s);
    }

    public static String convertToLegacy(Component component) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(component);
    }

    /*
        Match and append & in front of each bungee hex color code for adventure serializer, for backward compatibility
        In adventure, RGB color code format is like &#a25981
        In bungee api, RGB color code format is like #a25981
        To support both bungee and adventure RGB color code in legacy
    */
    private static String bungeeHexToAdventureInConfig(String s) {
        return BUNGEE_RGB_PATTERN
                .matcher(s)
                .replaceAll(match -> "&" + match.group(1));
    }

    public static boolean isNumeric(String s) {
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }

        return false;
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

    // e.g. FLOWER_FOREST -> Flower Forest
    public static String capitalize(String name) {
        // Split with "_"
        String[] list = name.split("_");
        StringBuilder sb = new StringBuilder();
        int i = 0;

        // To make the first letter of each word capitalized, then append the rest of the string in each word together
        for (String s : list) {
            String fst = s.substring(0, 1).toUpperCase();
            String snd = s.substring(1).toLowerCase();

            sb.append(fst).append(snd);

            // Add space between split words
            if (i < list.length - 1) {
                sb.append(" ");
            }

            i++;
        }

        return sb.toString();
    }

    public static void registerEvents(Listener... listeners) {
        final PluginManager manager = Bukkit.getPluginManager();
        final DeathMessages instance = DeathMessages.getInstance();

        for (Listener listener : listeners) {
            manager.registerEvents(listener, instance);
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

    @Deprecated
    public static String getPlayerName(PlayerCtx playerCtx) {
        if (isPlayerVanished(playerCtx.getPlayer())) {
            return PlainTextComponentSerializer.plainText().serialize(getVanishedName());
        }

        return playerCtx.getName();
    }

    @Deprecated
    public static String getPlayerName(Player player) {
        if (isPlayerVanished(player)) {
            return PlainTextComponentSerializer.plainText().serialize(getVanishedName());
        }

        return player.getName();
    }

    @Deprecated
    public static String getPlayerDisplayName(PlayerCtx playerCtx) {
        final Player player = playerCtx.getPlayer();

        if (isPlayerVanished(player)) {
            return PlainTextComponentSerializer.plainText().serialize(getVanishedName());
        }

        return player.getDisplayName();
    }

    @Deprecated
    public static String getPlayerDisplayName(Player player) {
        if (isPlayerVanished(player)) {
            return PlainTextComponentSerializer.plainText().serialize(getVanishedName());
        }

        return player.getDisplayName();
    }

    public static Component getPlayerNameComponent(PlayerCtx playerCtx) {
        if (isPlayerVanished(playerCtx.getPlayer())) {
            return getVanishedName();
        }

        return Component.text(playerCtx.getName());
    }

    public static Component getPlayerNameComponent(Player player) {
        if (isPlayerVanished(player)) {
            return getVanishedName();
        }

        return Component.text(player.getName());
    }

    public static Component getPlayerDisplayNameComponent(PlayerCtx playerCtx) {
        final Player player = playerCtx.getPlayer();

        if (isPlayerVanished(player)) {
            return getVanishedName();
        }

        // TODO
        //return player.displayName();
        return null;
    }

    public static Component getPlayerDisplayNameComponent(Player player) {
        if (isPlayerVanished(player)) {
            return getVanishedName();
        }

        // TODO
        //return player.displayName();
        return null;
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

    public static String getBiomeName(Biome biome) {
        return capitalize(DeathMessages.getNMS().biomeKeyName(biome));
    }

    public static boolean doesClassExists(String clazz) {
        try {
            Class.forName(clazz);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
