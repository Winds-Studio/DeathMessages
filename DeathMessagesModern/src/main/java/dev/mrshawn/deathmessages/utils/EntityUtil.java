package dev.mrshawn.deathmessages.utils;

import dev.mrshawn.deathmessages.api.EntityCtx;
import dev.mrshawn.deathmessages.api.ExplosionManager;
import dev.mrshawn.deathmessages.api.PlayerCtx;
import dev.mrshawn.deathmessages.api.events.DMBlockExplodeEvent;
import dev.mrshawn.deathmessages.config.Messages;
import dev.mrshawn.deathmessages.enums.MobType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    // K: EndCrystal UUID
    // V: Causing Entity instance
    public static final Map<UUID, LivingEntity> crystalDeathContext = new HashMap<>();

    public static List<String> getEntityList() {
        return list;
    }

    public static String getConfigNodeByEntity(Entity e) {
        return e.getType().getEntityClass().getSimpleName().toLowerCase();
    }

    public static Component getEntityCustomNameComponent(Entity e) {
        final String rawName = Messages.getInstance().getConfig().getString("Mobs." + EntityUtil.getConfigNodeByEntity(e));

        return Util.convertFromLegacy(rawName != null ? rawName : "");
    }

    public static String getEntityCustomName(Entity e) {
        final String rawName = Messages.getInstance().getConfig().getString("Mobs." + EntityUtil.getConfigNodeByEntity(e));

        return PlainTextComponentSerializer.plainText().serialize(Util.convertFromLegacy(rawName != null ? rawName : ""));
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

    public static void getExplosionNearbyEffected(Player p, Block b) {
        List<UUID> effected = new ArrayList<>();
        Collection<Entity> getNearby = b.getWorld().getNearbyEntities(BoundingBox.of(b).expand(24)); // TODO: make it configurable

        getNearby.forEach(ent -> {
                    if (ent instanceof Player) {
                        PlayerCtx playerCtx = PlayerCtx.of(ent.getUniqueId());
                        if (playerCtx != null) {
                            effected.add(ent.getUniqueId());
                            playerCtx.setLastEntityDamager(p);
                        }
                    } else {
                        EntityCtx entityCtx = EntityCtx.of(ent.getUniqueId());
                        if (entityCtx != null) {
                            effected.add(ent.getUniqueId());

                            PlayerCtx playerCtx = PlayerCtx.of(p.getUniqueId());
                            if (playerCtx != null) {
                                entityCtx.setLastPlayerDamager(playerCtx);
                            }
                        } else {
                            EntityCtx.create(new EntityCtx(ent, MobType.VANILLA));
                        }
                    }
                }
        );

        new ExplosionManager(p.getUniqueId(), b.getType(), b.getLocation(), effected);
        DMBlockExplodeEvent explodeEvent = new DMBlockExplodeEvent(p, b);
        Bukkit.getPluginManager().callEvent(explodeEvent);
    }

    public static void loadCrystalDamager(Entity entity, Entity damager) {
        // Scenario 1
        // Player clicked (damaged) crystal
        // I didn't consider the scenario about entity clicked crystal, idk if it's needed?
        if (entity instanceof EnderCrystal && damager instanceof Player) {
            crystalDeathContext.put(entity.getUniqueId(), (Player) damager);
        }
        // Scenario 2
        // The crystal is triggered by a projectile when pass through (player A / LivingEntity -> projectile -> crystal -> Player A/B / Entity)
        else if (entity instanceof EnderCrystal && damager instanceof Projectile) {
            ProjectileSource shooter = ((Projectile) damager).getShooter();
            if (shooter instanceof LivingEntity) {
                crystalDeathContext.put(entity.getUniqueId(), (LivingEntity) shooter);
            }
        }
    }
}
