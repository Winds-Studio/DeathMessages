package dev.mrshawn.deathmessages.utils;

import dev.mrshawn.deathmessages.api.EntityManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wolf;

import java.util.Arrays;
import java.util.List;

public class EntityUtil {

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

    public static List<String> getEntities() {
        return list;
    }

    public static String getConfigNodeByEntity(Entity e) {
        return e.getType().getEntityClass().getSimpleName().toLowerCase();
    }

}
