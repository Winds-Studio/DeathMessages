package dev.mrshawn.deathmessages.hooks;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.display.Display;
import com.willfp.eco.core.fast.FastItemStack;
import com.willfp.eco.core.fast.FastItemStackExtensions;
import com.willfp.ecoenchants.display.DisplayableEnchant;
import com.willfp.ecoenchants.display.EnchantmentFormattingKt;
import com.willfp.ecoenchants.enchant.EcoEnchant;
import com.willfp.ecoenchants.enchant.EcoEnchantLevel;
import com.willfp.ecoenchants.enchant.EcoEnchantLike;
import com.willfp.ecoenchants.target.EnchantmentTargets;
import com.willfp.libreforge.ItemProvidedHolder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EcoExtension {

    public EcoExtension() {
    }

    public List<String> getEcoEnchantsItem(ItemStack itemStack, Player player) {
        if (!EnchantmentTargets.INSTANCE.isEnchantable(itemStack) && Eco.get().getPluginByName("EcoEnchants").getConfigYml().getBool("display.require-enchantable")) {
            return null;
        }

        FastItemStack fast = FastItemStackExtensions.fast(itemStack);
        /*
        PersistentDataContainer pdc = fast.getPersistentDataContainer();

        NamespacedKey hideStateKey =
                EcoEnchantsPlugin.getPlugin("EcoEnchants").getNamespacedKeyFactory().create("ecoenchantlore-skip"); // Same for backwards compatibility
        // Args represent hide enchants
        if (args[0] == true) {
            fast.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            if (itemStack.getType() == Material.ENCHANTED_BOOK) {
                fast.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            }
            pdc.set(hideStateKey, PersistentDataType.INTEGER, 1);
            return null;
        } else {
            pdc.set(hideStateKey, PersistentDataType.INTEGER, 0);
        }
         */

        List<String> lore = fast.getLore();
        List<String> enchantLore = new ArrayList<>();

        // Get enchants mapped to EcoEnchantLike
        Map<Enchantment, Integer> enchants = fast.getEnchants(true);

        boolean shouldCollapse = Eco.get().getPluginByName("EcoEnchants").getConfigYml().getBool("display.collapse.enabled") &&
                enchants.size() > Eco.get().getPluginByName("EcoEnchants").getConfigYml().getInt("display.collapse.threshold");

        boolean shouldDescribe = Eco.get().getPluginByName("EcoEnchants").getConfigYml().getBool("display.descriptions.enabled") &&
                enchants.size() <= Eco.get().getPluginByName("EcoEnchants").getConfigYml().getInt("display.descriptions.threshold");
                //&& ((player != null) ? seesEnchantmentDescriptions : true);

        ConcurrentHashMap<DisplayableEnchant, String> formattedNames = new ConcurrentHashMap<>();

        List<String> notMetLines = new ArrayList<>();

        for (Map.Entry<Enchantment, Integer> ench : enchants.entrySet()) {
            Enchantment enchant = ench.getKey();
            Integer level = ench.getValue();
            boolean showNotMet = false;

            if (player != null && enchant instanceof EcoEnchant) {
                EcoEnchantLevel enchantLevel = ((EcoEnchant) enchant).getLevel(level);
                ItemProvidedHolder holder = new ItemProvidedHolder(enchantLevel, itemStack);

                List<String> enchantNotMetLines = holder.getNotMetLines(player).stream()
                        .map(line -> Display.PREFIX + line)
                        .collect(Collectors.toList());
                notMetLines.addAll(enchantNotMetLines);

                if (!enchantNotMetLines.isEmpty() || holder.isShowingAnyNotMet(player)) {
                    showNotMet = true;
                }
            }

            formattedNames.put(new DisplayableEnchant((EcoEnchantLike) enchant, level, showNotMet),
                    EnchantmentFormattingKt.getFormattedName((EcoEnchantLike) enchant, level));
        }

        if (shouldCollapse) {
            int perLine = Eco.get().getPluginByName("EcoEnchants").getConfigYml().getInt("display.collapse.per-line");
            for (List<String> names : formattedNames.values().stream().collect(Collectors.partitioningBy(lines -> lines.length() > perLine)).values()) {
                enchantLore.add(
                        Display.PREFIX + names.stream().collect(Collectors.joining(
                                Eco.get().getPluginByName("EcoEnchants").getConfigYml().getFormattedString("display.collapse.delimiter")
                        ))
                );
            }
        } else {
            for (Map.Entry<DisplayableEnchant, String> entry : formattedNames.entrySet()) {
                DisplayableEnchant displayable = entry.getKey();
                String formattedName = entry.getValue();

                EcoEnchant enchant = (EcoEnchant) displayable.getEnchant();
                int level = displayable.getLevel();

                enchantLore.add(Display.PREFIX + formattedName);

                if (shouldDescribe) {
                    enchantLore.addAll(EnchantmentFormattingKt.getFormattedDescription(enchant, level)
                            .stream()
                            .filter(line -> !line.isEmpty())
                            .map(line -> Display.PREFIX + line)
                            .collect(Collectors.toList()));
                }
            }
        }

        /*fast.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        if (itemStack.getType() == Material.ENCHANTED_BOOK) {
            fast.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        }*/

        enchantLore.addAll(lore);
        enchantLore.addAll(notMetLines);

        return enchantLore;
    }
}
