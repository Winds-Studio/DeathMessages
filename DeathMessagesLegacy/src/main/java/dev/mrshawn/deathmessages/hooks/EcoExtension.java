package dev.mrshawn.deathmessages.hooks;

import com.willfp.eco.core.display.Display;
import com.willfp.ecoenchants.target.EnchantmentTargets;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EcoExtension {

    public ItemStack getEcoEnchantsItem(ItemStack i, Player player) {
        return Display.displayAndFinalize(i, player);
    }

    // EcoEnchants item has no specific pdc data
    // And it converts Vanilla enchants lore to its own lore description
    // So just using isEnchantable to detect whether using EcoEnchants way to display dore.
    public boolean isEcoEnchantsItem(ItemStack i) {
        return EnchantmentTargets.INSTANCE.isEnchantable(i);
    }
}
