package dev.mrshawn.deathmessages.utils;

import dev.mrshawn.deathmessages.config.Settings;
import dev.mrshawn.deathmessages.config.files.Config;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
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
        if (PlatformUtil.isOlderAndEqual(13, 0)) {
            // From 1.14 org.bukkit.Material.isAir()
            return switch (i.getType()) {
                case AIR, CAVE_AIR, VOID_AIR, LEGACY_AIR -> true;
                default -> false;
            };
        }

        return i.getType().isAir();
    }

    public static boolean isAir(Material material) {
        if (PlatformUtil.isOlderAndEqual(13, 2)) {
            // From 1.14 org.bukkit.Material.isAir()
            return switch (material) {
                case AIR, CAVE_AIR, VOID_AIR, LEGACY_AIR -> true;
                default -> false;
            };
        }

        return material.isAir();
    }

    private static boolean itemNameIsWeapon(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasDisplayName()) return false;

        String displayName = itemStack.getItemMeta().getDisplayName();
        Pattern[] customWeaponNames = Util.customWeaponNamePatterns;

        if (customWeaponNames == null) {
            List<String> customWeaponNameList = Settings.getInstance().getConfig().getStringList(Config.CUSTOM_ITEM_DISPLAY_NAMES_IS_WEAPON.getPath());
            customWeaponNames = new Pattern[customWeaponNameList.size()];

            for (int i = 0; i < customWeaponNameList.size(); i++) {
                String customWeaponName = customWeaponNameList.get(i);
                customWeaponNames[i] = Pattern.compile(customWeaponName);
            }

            Util.customWeaponNamePatterns = customWeaponNames; // Update cache
        }

        for (Pattern pattern : customWeaponNames) {
            Matcher matcher = pattern.matcher(displayName);
            if (matcher.find()) {
                return true;
            }
        }

        return false;
    }

    private static boolean itemMaterialIsWeapon(ItemStack itemStack) {
        for (String s : Settings.getInstance().getConfig().getStringList(Config.CUSTOM_ITEM_MATERIAL_IS_WEAPON.getPath())) {
            Material material = Material.getMaterial(s);
            if (itemStack.getType().equals(material)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isWeapon(ItemStack itemStack) {
        return isWeapon(itemStack.getType())
                || itemNameIsWeapon(itemStack)
                || itemMaterialIsWeapon(itemStack);
    }

    // Refer https://minecraft.wiki/w/Weapon
    // Or https://zh.minecraft.wiki/w/%E6%AD%A6%E5%99%A8
    private static boolean isWeapon(Material material) {
        String materialName = material.toString();
        return materialName.contains("SHOVEL")
                || materialName.contains("PICKAXE")
                || materialName.contains("AXE")
                || materialName.contains("HOE")
                || materialName.contains("SWORD")
                || materialName.contains("BOW")
                || materialName.contains("CROSSBOW")
                || materialName.contains("ARROW")
                // Dreeam note: these three weapons below all have their own unique kill messages in vanilla
                || materialName.contains("TRIDENT")
                || materialName.contains("MACE")
                || materialName.contains("SPEAR");
    }

    public static boolean hasWeapon(LivingEntity mob, EntityDamageEvent.DamageCause damageCause) {
        if (mob.getEquipment() == null || damageCause.equals(EntityDamageEvent.DamageCause.THORNS)) return false;

        return isWeapon(mob.getEquipment().getItemInMainHand());
    }
}
