package dev.mrshawn.deathmessages.utils;

import dev.mrshawn.deathmessages.config.Settings;
import dev.mrshawn.deathmessages.files.Config;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MaterialUtil {

    public static boolean isClimbable(Material material) {
        final String name = material.name();
        return name.contains("LADDER")
                || name.contains("VINE")
                || name.contains("SCAFFOLDING")
                || name.contains("TRAPDOOR");
    }

    public static boolean isAir(ItemStack i) {
        if (Util.isOlderAndEqual(13, 0)) {
            // From 1.14 org.bukkit.Material.isAir()
            switch (i.getType()) {
                //<editor-fold defaultstate="collapsed" desc="isAir">
                case AIR:
                case CAVE_AIR:
                case VOID_AIR:
                    // ----- Legacy Separator -----
                case LEGACY_AIR:
                    //</editor-fold>
                    return true;
                default:
                    return false;
            }
        }

        return i.getType().isAir();
    }

    public static boolean isAir(Material material) {
        if (Util.isOlderAndEqual(13, 2)) {
            // From 1.14 org.bukkit.Material.isAir()
            switch (material) {
                //<editor-fold defaultstate="collapsed" desc="isAir">
                case AIR:
                case CAVE_AIR:
                case VOID_AIR:
                    // ----- Legacy Separator -----
                case LEGACY_AIR:
                    //</editor-fold>
                    return true;
                default:
                    return false;
            }
        }
        return material.isAir();
    }

    public static boolean itemNameIsWeapon(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasDisplayName()) return false;

        String displayName = itemStack.getItemMeta().getDisplayName();

        for (String s : Settings.getInstance().getConfig().getStringList(Config.CUSTOM_ITEM_DISPLAY_NAMES_IS_WEAPON.getPath())) {
            Pattern pattern = Pattern.compile(s);
            Matcher matcher = pattern.matcher(displayName);
            if (matcher.find()) {
                return true;
            }
        }
        return false;
    }

    public static boolean itemMaterialIsWeapon(ItemStack itemStack) {
        for (String s : Settings.getInstance().getConfig().getStringList(Config.CUSTOM_ITEM_MATERIAL_IS_WEAPON.getPath())) {
            Material material = Material.getMaterial(s);
            if (itemStack.getType().equals(material)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWeapon(ItemStack itemStack) {
        return isWeapon(itemStack.getType())
                || itemNameIsWeapon(itemStack)
                || itemMaterialIsWeapon(itemStack);
    }

    // Refer https://minecraft.wiki/w/Weapon
    // Or https://zh.minecraft.wiki/w/%E6%AD%A6%E5%99%A8
    public static boolean isWeapon(Material material) {
        String materialName = material.toString();
        return materialName.contains("SHOVEL")
                || materialName.contains("PICKAXE")
                || materialName.contains("AXE")
                || materialName.contains("HOE")
                || materialName.contains("SWORD")
                || materialName.contains("BOW")
                || materialName.contains("CROSSBOW")
                || materialName.contains("ARROW")
                || materialName.contains("TRIDENT")
                || materialName.contains("MACE");
    }

    public static boolean hasWeapon(LivingEntity mob, EntityDamageEvent.DamageCause damageCause) {
        if (mob.getEquipment() == null || damageCause.equals(EntityDamageEvent.DamageCause.THORNS)) return false;

        return isWeapon(mob.getEquipment().getItemInMainHand());
    }
}
