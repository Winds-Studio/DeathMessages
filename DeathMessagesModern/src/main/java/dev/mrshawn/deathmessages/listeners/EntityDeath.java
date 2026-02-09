package dev.mrshawn.deathmessages.listeners;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.EntityCtx;
import dev.mrshawn.deathmessages.api.PlayerCtx;
import dev.mrshawn.deathmessages.api.events.BroadcastDeathMessageEvent;
import dev.mrshawn.deathmessages.api.events.BroadcastEntityDeathMessageEvent;
import dev.mrshawn.deathmessages.config.Gangs;
import dev.mrshawn.deathmessages.enums.MessageType;
import dev.mrshawn.deathmessages.enums.MobType;
import dev.mrshawn.deathmessages.utils.Assets;
import dev.mrshawn.deathmessages.utils.ComponentUtil;
import dev.mrshawn.deathmessages.utils.EntityUtil;
import dev.mrshawn.deathmessages.utils.Util;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;

public class EntityDeath implements Listener {

    void onEntityDeath(EntityDeathEvent e) {
        // Player death
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            PlayerCtx playerCtx = PlayerCtx.of(player.getUniqueId());

            if (playerCtx == null) return;

            if (playerCtx.isCommandDeath()) { // If died by using suicide like command
                // set to null since it is command death
                playerCtx.setLastEntityDamager(null);
                playerCtx.setLastDamageCause(EntityDamageEvent.DamageCause.SUICIDE);
                playerCtx.setCommandDeath(false);
            } else if (e.getEntity().getLastDamageCause() == null) {
                playerCtx.setLastDamageCause(EntityDamageEvent.DamageCause.CUSTOM);
            } else { // Reset lastDamageCause
                playerCtx.setLastDamageCause(e.getEntity().getLastDamageCause().getCause());
            }

            if (playerCtx.isBlacklisted()) return;

            if (!(playerCtx.getLastEntityDamager() instanceof LivingEntity) || playerCtx.getLastEntityDamager() == e.getEntity()) {
                TextComponent[] naturalDeath = Assets.playerNatureDeathMessage(playerCtx, player);
                TextComponent oldNaturalDeath = naturalDeath[0].append(naturalDeath[1]); // Dreeam TODO: Remove in 1.4.21

                if (!ComponentUtil.isMessageEmpty(naturalDeath)) {
                    BroadcastDeathMessageEvent event = new BroadcastDeathMessageEvent(
                            player,
                            null,
                            MessageType.NATURAL,
                            oldNaturalDeath,
                            naturalDeath,
                            Util.getBroadcastWorlds(player),
                            false
                    );
                    Bukkit.getPluginManager().callEvent(event);
                }
            } else {
                // Killed by mob
                Entity ent = playerCtx.getLastEntityDamager();
                boolean gangKill = false;

                if (Gangs.getInstance().getConfig().getBoolean("Gang.Enabled")) {
                    String mobName = EntityUtil.getConfigNodeByEntity(ent);
                    int radius = Gangs.getInstance().getConfig().getInt("Gang.Mobs." + mobName + ".Radius");
                    int amount = Gangs.getInstance().getConfig().getInt("Gang.Mobs." + mobName + ".Amount");

                    int totalMobEntities = 0;
                    List<Entity> nearbyEntities = player.getNearbyEntities(radius, radius, radius);

                    for (Entity entity : nearbyEntities) {
                        if (entity.toString().contains("EnderDragonPart")) { // Exclude EnderDragonPart
                            continue;
                        }

                        if (entity.getType().equals(ent.getType())) {
                            if (++totalMobEntities >= amount) {
                                gangKill = true;
                                break;
                            }
                        }
                    }
                }

                TextComponent[] playerDeath = Assets.playerDeathMessage(playerCtx, gangKill);
                TextComponent oldPlayerDeath = playerDeath[0].append(playerDeath[1]); // Dreeam TODO: Remove in 1.4.21

                if (!ComponentUtil.isMessageEmpty(playerDeath)) {
                    MessageType messageType = ent instanceof Player ? MessageType.PLAYER : MessageType.MOB;
                    BroadcastDeathMessageEvent event = new BroadcastDeathMessageEvent(
                            player,
                            (LivingEntity) playerCtx.getLastEntityDamager(),
                            messageType,
                            oldPlayerDeath,
                            playerDeath,
                            Util.getBroadcastWorlds(player),
                            gangKill
                    );
                    Bukkit.getPluginManager().callEvent(event);
                }
            }
        } else {
            // Entity killed by Player
            EntityCtx entityCtx = EntityCtx.of(e.getEntity().getUniqueId());
            if (entityCtx != null) {
                MobType mobType = MobType.VANILLA;
                if (DeathMessages.getHooks().mythicmobsEnabled) {
                    if (DeathMessages.getHooks().mythicMobs.getAPIHelper().isMythicMob(e.getEntity().getUniqueId())) {
                        mobType = MobType.MYTHIC_MOB;
                    }
                }

                PlayerCtx damagerCtx = entityCtx.getLastPlayerDamager();
                if (damagerCtx == null) return; // Entity killed by Entity should not include in DM

                TextComponent[] entityDeath = Assets.entityDeathMessage(entityCtx, mobType);
                TextComponent oldEntityDeath = entityDeath[0].append(entityDeath[1]); // Dreeam TODO: Remove in 1.4.21

                if (!ComponentUtil.isMessageEmpty(entityDeath)) {
                    BroadcastEntityDeathMessageEvent event = new BroadcastEntityDeathMessageEvent(
                            damagerCtx,
                            e.getEntity(),
                            MessageType.ENTITY,
                            oldEntityDeath,
                            entityDeath,
                            Util.getBroadcastWorlds(e.getEntity())
                    );
                    Bukkit.getPluginManager().callEvent(event);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDeath_LOWEST(EntityDeathEvent e) {
        if (DeathMessages.getEventPriority().equals(EventPriority.LOWEST)) {
            onEntityDeath(e);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDeath_LOW(EntityDeathEvent e) {
        if (DeathMessages.getEventPriority().equals(EventPriority.LOW)) {
            onEntityDeath(e);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDeath_NORMAL(EntityDeathEvent e) {
        if (DeathMessages.getEventPriority().equals(EventPriority.NORMAL)) {
            onEntityDeath(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath_HIGH(EntityDeathEvent e) {
        if (DeathMessages.getEventPriority().equals(EventPriority.HIGH)) {
            onEntityDeath(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath_HIGHEST(EntityDeathEvent e) {
        if (DeathMessages.getEventPriority().equals(EventPriority.HIGHEST)) {
            onEntityDeath(e);
        }
    }
}
