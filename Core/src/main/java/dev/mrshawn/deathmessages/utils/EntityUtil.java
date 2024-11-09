package dev.mrshawn.deathmessages.utils;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Tameable;

import java.util.Arrays;
import java.util.List;

public class EntityUtil {

    // TODO: update to newest version (1.21.1) and add version comment
    // TODO: update to newest version and add version comment
    private static final List<String> list = Arrays.asList(
            "elder_guardian",
            "wither_skeleton",
            "stray",
            "husk",
            "zombie_villager",
            "skeleton_horse",
            "zombie_horse",
            "armor_stand",
            "donkey",
            "mule",
            "evoker",
            "vex",
            "vindicator",
            "illusioner",
            "creeper",
            "skeleton",
            "spider",
            "giant",
            "zombie",
            "slime",
            "ghast",
            "zombified_piglin",
            "enderman",
            "cave_spider",
            "silverfish",
            "blaze",
            "magma_cube",
            "ender_dragon",
            "wither",
            "bat",
            "witch",
            "endermite",
            "guardian",
            "shulker",
            "pig",
            "sheep",
            "cow",
            "chicken",
            "squid",
            "wolf",
            "mooshroom",
            "snow_golem",
            "ocelot",
            "iron_golem",
            "horse",
            "rabbit",
            "polar_bear",
            "llama",
            "parrot",
            "villager",
            "turtle",
            "phantom",
            "cod",
            "salmon",
            "pufferfish",
            "tropical_fish",
            "drowned",
            "dolphin",
            "cat",
            "panda",
            "pillager",
            "ravager",
            "trader_llama",
            "wandering_trader",
            "fox",
            "bee",
            "hoglin",
            "piglin",
            "strider",
            "zoglin",
            "piglin_brute",
            "axolotl",
            "glow_squid",
            "goat",
            "allay",
            "frog",
            "tadpole",
            "warden",
            "camel",
            "sniffer",
            "breeze",
            "player"
    private static final List<String> list = Arrays.asList(
            "elderguardian",
            "witherskeleton",
            "stray",
            "husk",
            "zombievillager",
            "skeletonhorse",
            "zombiehorse",
            "armorstand",
            "donkey",
            "mule",
            "evoker",
            "vex",
            "vindicator",
            "illusioner",
            "creeper",
            "skeleton",
            "spider",
            "giant",
            "zombie",
            "slime",
            "ghast",
            "pigzombie",
            "enderman",
            "cavespider",
            "silverfish",
            "blaze",
            "magmacube",
            "enderdragon",
            "wither",
            "bat",
            "witch",
            "endermite",
            "guardian",
            "shulker",
            "pig",
            "sheep",
            "cow",
            "chicken",
            "squid",
            "wolf",
            "mushroomcow",
            "snowman",
            "ocelot",
            "irongolem",
            "horse",
            "rabbit",
            "polarbear",
            "llama",
            "parrot",
            "villager",
            "turtle",
            "phantom",
            "cod",
            "salmon",
            "pufferfish",
            "tropicalfish",
            "drowned",
            "dolphin",
            "cat",
            "panda",
            "pillager",
            "ravager",
            "traderllama",
            "wanderingtrader",
            "fox",
            "bee",
            "hoglin",
            "piglin",
            "strider",
            "zoglin",
            "piglinbrute",
            "player",
            "goat",
            "warden",
            "bogged",
            "breeze"

    public static List<String> getEntityList() {
        return list;
    }

    public static String getConfigNodeByEntity(Entity e) {
        return e.getType().toString().toLowerCase();
        return e.getType().getEntityClass().getSimpleName().toLowerCase();
    }

    // Reduce directly detect hasOwner as few as possible just makes it looks better
    public static boolean hasOwner(Entity e) {
        if (e instanceof Tameable) {
            final Tameable tameable = (Tameable) e;
            return tameable.getOwner() != null && tameable.getOwner().getName() != null;
        }

        return false;
    }

    // TODO - get mob variant name (not sure is needed)
    /*
    public static String getName(Entity e) {
        if (Util.isNewerAndEqual(20, 5) && e.getType() == EntityType.WOLF) {
            String key = e.getType().getKey().getKey();
            //Wolf.Variant.ASHEN
            System.out.println(key);
        }
        return null;
    }
     */
}
