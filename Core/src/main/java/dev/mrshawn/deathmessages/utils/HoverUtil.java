package dev.mrshawn.deathmessages.utils;

import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.changeme.nbtapi.NBT;
import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.PlayerManager;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HoverUtil {

    /*
        Process hover event string in message
        If found, add string to rawEvents list, then replace them to placeholder like %example% in msg
        If there are multiple event string in message, the message should be like `%hover_event_0%, ..., %hover_event_N%` after the replacing
     */
    public static String sortHoverEvents(String msg, List<String> rawEvents) {
        // If contains event string, process, otherwise return original msg directly
        if (msg.contains("[")) {
            int index = 0;
            // Match all string between [ and ], e.g. abc[123]edf -> [123]
            Pattern pattern = Pattern.compile("\\[(.*?)]");
            Matcher matcher = pattern.matcher(msg);

            while (matcher.find()) {
                String replacement = "%hover_event_" + index + "%";

                // Added in raw Events list
                rawEvents.add(matcher.group(1));
                // Replace original message
                msg = msg.replace("[" + matcher.group(1) + "]", replacement);
                // Update index
                index++;
            }
        }

        return msg;
    }

    public static Component buildHover(Player player, ItemStack i, Component displayName) {

        HoverEvent<HoverEvent.ShowItem> showItem;
        String iNamespace = XMaterial.matchXMaterial(i.getType().name()).get().name().toLowerCase();

        // EcoEnchants items
        if (DeathMessages.getInstance().ecoEnchantsEnabled && DeathMessages.getInstance().ecoExtension.isEcoEnchantsItem(i)) {
            List<String> ecoEItemLore = DeathMessages.getInstance().ecoExtension.getEcoEnchantsItemLore(i, player);

            ItemStack tempItem = i.clone();
            ItemMeta meta = tempItem.getItemMeta();
            meta.setLore(ecoEItemLore);
            tempItem.setItemMeta(meta);

            showItem = HoverEvent.showItem(Key.key(iNamespace), i.getAmount(), BinaryTagHolder.binaryTagHolder(NBT.itemStackToNBT(tempItem).getCompound("tag").toString()));
        } else {
            try {
                // Item with NBT
                showItem = HoverEvent.showItem(Key.key(iNamespace), i.getAmount(), BinaryTagHolder.binaryTagHolder(NBT.itemStackToNBT(i).getCompound("tag").toString()));
            } catch (NullPointerException e) {
                // Item has no `tag` compound in nbt
                showItem = HoverEvent.showItem(Key.key(iNamespace), i.getAmount());
            }
        }

        return Component.text()
                .append(displayName)
                .build()
                .hoverEvent(showItem);
    }

    /*
        Process and build hover events from raw events list
        Only for playerDeath: pm, e, Only for EntityDeath: p, e, owner
     */
    public static Component buildHoverEvents(
            String rawEvent,
            PlayerManager pm,
            Player p,
            Entity e,
            boolean owner,
            boolean isPlayerDeath
    ) {
        rawEvent = rawEvent.replace("[", "").replace("]", "");
        String[] rawHover = rawEvent.split("::");
        TextComponent.Builder event = Component.text();

        // Append base message which has the hover text and events
        event.append(Util.convertFromLegacy(rawHover[0]));

        // Append hover text if exists
        if (!rawHover[1].isEmpty()) {
            HoverEvent<Component> showText = HoverEvent.showText(Util.convertFromLegacy(rawHover[1]));
            event.hoverEvent(showText);
        }

        // Append hover click events if exists
        if (rawHover.length == 4) {
            ClickEvent click = null;
            final String content = isPlayerDeath ? Assets.playerDeathPlaceholders(rawHover[3], pm, (LivingEntity) e) : Assets.entityDeathPlaceholders(rawHover[3], p, e, owner);

            switch (rawHover[2]) {
                case "COPY_TO_CLIPBOARD":
                    click = ClickEvent.copyToClipboard(content);
                    break;
                case "OPEN_URL":
                    click = ClickEvent.openUrl(content);
                    break;
                case "RUN_COMMAND":
                    click = ClickEvent.runCommand("/" + content);
                    break;
                case "SUGGEST_COMMAND":
                    click = ClickEvent.suggestCommand("/" + content);
                    break;
                default:
                    DeathMessages.LOGGER.error("Unknown hover event action: {}", rawHover[2]);
                    break;
            }

            event.clickEvent(click);
        }

        return event.build();
    }
}
