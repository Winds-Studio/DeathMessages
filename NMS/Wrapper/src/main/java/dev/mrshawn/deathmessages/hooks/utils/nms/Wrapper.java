package dev.mrshawn.deathmessages.hooks.utils.nms;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.event.DataComponentValue;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public interface Wrapper {

    Map<Key, DataComponentValue> getItemStackComponentsMap(ItemStack i);
}
