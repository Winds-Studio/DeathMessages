package dev.mrshawn.deathmessages.nms.spigot.v1_16_5;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.inventory.ItemStack;

import java.util.regex.Pattern;

public class NMSAdaptorImpl extends dev.mrshawn.deathmessages.nms.spigot.v1_13.NMSAdaptorImpl {

    // Match format like: §x§7§A§0§0§F§F
    private static final Pattern BUNGEE_HEX_PATTERN = Pattern.compile("§x(§[0-9A-Fa-f]){6}");

    @Override
    public Component itemDisplayName(ItemStack i) {
        // For hex color added since 1.16.5
        final String bungeeDisplayName = i.getItemMeta().getDisplayName();
        final String adventureDisplayName = bungeeHexToAdventure(bungeeDisplayName);

        return LegacyComponentSerializer.legacyAmpersand().deserialize(adventureDisplayName)
                .decoration(TextDecoration.ITALIC, true);
    }

    // TODO: Should we? Not sure whether entity's custom name can have hex color or not
//    @Override
//    public Component entityCustomName(Entity entity) {
//        return entity.customName();
//    }

    private static String bungeeHexToAdventure(String input) {
        final String adventureHexPrefix = "&#";
        return BUNGEE_HEX_PATTERN
                .matcher(input)
                .replaceAll(match -> {
                    String s = match.group();

                    StringBuilder hex = new StringBuilder(adventureHexPrefix);
                    for (int i = 3; i < s.length(); i += 2) {
                        hex.append(s.charAt(i));
                    }

                    return hex.toString();
                });
    }
}
