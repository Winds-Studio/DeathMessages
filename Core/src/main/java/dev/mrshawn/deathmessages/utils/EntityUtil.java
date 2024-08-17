package dev.mrshawn.deathmessages.utils;

import dev.mrshawn.deathmessages.api.EntityManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;

import java.util.Arrays;
import java.util.List;

public class EntityUtil {

    // TODO: update to newest version and add version comment
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
    );

    public static List<String> getEntityList() {
        return list;
    }

    public static String getConfigNodeByEntity(Entity e) {
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
